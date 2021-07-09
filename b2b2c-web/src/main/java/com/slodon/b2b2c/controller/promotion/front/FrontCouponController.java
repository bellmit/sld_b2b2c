package com.slodon.b2b2c.controller.promotion.front;

import com.slodon.b2b2c.core.constant.CouponConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.model.goods.ProductModel;
import com.slodon.b2b2c.model.promotion.CouponGoodsCategoryModel;
import com.slodon.b2b2c.model.promotion.CouponGoodsModel;
import com.slodon.b2b2c.model.promotion.CouponMemberModel;
import com.slodon.b2b2c.model.promotion.CouponModel;
import com.slodon.b2b2c.promotion.example.CouponExample;
import com.slodon.b2b2c.promotion.example.CouponGoodsCategoryExample;
import com.slodon.b2b2c.promotion.example.CouponGoodsExample;
import com.slodon.b2b2c.promotion.example.CouponMemberExample;
import com.slodon.b2b2c.promotion.pojo.Coupon;
import com.slodon.b2b2c.promotion.pojo.CouponGoods;
import com.slodon.b2b2c.promotion.pojo.CouponGoodsCategory;
import com.slodon.b2b2c.promotion.pojo.CouponMember;
import com.slodon.b2b2c.vo.promotion.CouponListVO;
import com.slodon.b2b2c.vo.promotion.CouponReceiveVO;
import com.slodon.b2b2c.vo.promotion.FrontCouponVO;
import com.slodon.b2b2c.vo.promotion.StoreCouponVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.slodon.b2b2c.core.constant.CouponConst.*;

@Api(tags = "front-我的优惠券")
@RestController
@RequestMapping("v3/promotion/front/coupon")
public class FrontCouponController extends BaseController {

    @Resource
    private CouponModel couponModel;
    @Resource
    private CouponGoodsCategoryModel couponGoodsCategoryModel;
    @Resource
    private CouponMemberModel couponMemberModel;
    @Resource
    private CouponGoodsModel couponGoodsModel;
    @Resource
    private ProductModel productModel;

    @ApiOperation("领券中心列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isSelected", value = "是否精选：true-是，false-否", defaultValue = "true", paramType = "query"),
            @ApiImplicitParam(name = "categoryId", value = "分类id", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("couponCenter")
    public JsonResult<FrontCouponVO> couponCenter(HttpServletRequest request, Integer categoryId,
                                                  @RequestParam(value = "isSelected", defaultValue = "true") boolean isSelected) {
        Member member = UserUtil.getUser(request, Member.class);

        //查询分类列表
        CouponGoodsCategoryExample categoryExample = new CouponGoodsCategoryExample();
        categoryExample.setCategoryGrade(CouponConst.CATEGORY_GRADE_1);
        categoryExample.setGroupBy("category_id, category_name");
        categoryExample.setOrderBy("category_id desc");
        String fields = "category_id, category_name";
        List<CouponGoodsCategory> categoryList = couponGoodsCategoryModel.getCouponGoodsCategoryListByField(fields, categoryExample);
        List<FrontCouponVO.CategoryVO> categoryVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(categoryList)) {
            categoryList.forEach(category -> {
                categoryVOS.add(new FrontCouponVO.CategoryVO(category.getCategoryId(), category.getCategoryName()));
            });
        }
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        //查询优惠券列表
        CouponExample example = new CouponExample();
        //如果是精选，查平台推荐优惠券
        if (isSelected) {
            example.setIsRecommend(CouponConst.IS_RECOMMEND_1);
        } else {
            if (!StringUtil.isNullOrZero(categoryId)) {
                example.setCategoryId(categoryId);
            }
        }
        example.setPublishType(CouponConst.PUBLISH_TYPE_1);
        example.setState(CouponConst.ACTIVITY_STATE_1);
        example.setPublishStartTimeBefore(new Date());
        example.setPublishEndTimeAfter(new Date());
        List<Coupon> list = couponModel.getCouponList(example, pager);
        List<FrontCouponVO.CouponVO> couponVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(coupon -> {
                FrontCouponVO.CouponVO couponVO = new FrontCouponVO.CouponVO(coupon);
                //如果未登陆，已抢完的显示已抢完，未抢完的都可以领取
                if (member.getMemberId() == null) {
                    if (coupon.getReceivedNum().equals(coupon.getPublishNum())) {
                        //已抢完
                        couponVO.setReceivedState(CouponConst.COUPON_FINISHED);
                    } else {
                        //未抢完，还能领
                        couponVO.setReceivedState(CouponConst.COUPON_NOT_RECEIVE);
                    }
                } else {
                    CouponMemberExample couponMemberExample = new CouponMemberExample();
                    couponMemberExample.setMemberId(member.getMemberId());
                    couponMemberExample.setCouponId(coupon.getCouponId());
                    List<CouponMember> couponMemberList = couponMemberModel.getCouponMemberList(couponMemberExample, null);
                    if (CollectionUtils.isEmpty(couponMemberList)) {
                        //未领取的优惠券判断是否还能领
                        if (coupon.getReceivedNum().equals(coupon.getPublishNum())) {
                            //已抢完
                            couponVO.setReceivedState(CouponConst.COUPON_FINISHED);
                        } else {
                            //未抢完，还能领
                            couponVO.setReceivedState(CouponConst.COUPON_NOT_RECEIVE);
                        }
                    } else {
                        //已领取
                        if (couponVO.getRobbedRate() != 100) {
                            if (coupon.getLimitReceive().equals(couponMemberList.size())) {
                                couponVO.setReceivedState(CouponConst.COUPON_IS_RECEIVE);
                            } else {
                                couponVO.setReceivedState(CouponConst.COUPON_NOT_RECEIVE);
                            }
                        } else {
                            couponVO.setReceivedState(CouponConst.COUPON_FINISHED);
                        }
                    }
                }
                couponVOS.add(couponVO);
            });
        }
        return SldResponse.success(new FrontCouponVO(categoryVOS, couponVOS, pager));
    }

    @ApiOperation("领取优惠券")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "couponId", value = "优惠券id", required = true, paramType = "query")
    })
    @GetMapping("receiveCoupon")
    public JsonResult<CouponReceiveVO> receiveCoupon(HttpServletRequest request, Integer couponId) {
        Member member = UserUtil.getUser(request, Member.class);

        CouponMember couponMember = new CouponMember();
        couponMember.setCouponId(couponId);
        couponMember.setMemberId(member.getMemberId());
        couponMember.setMemberName(member.getMemberName());
        CouponReceiveVO vo = couponMemberModel.saveCouponMember(couponMember);
        return SldResponse.success(vo);
    }

    @ApiOperation("我的优惠券列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "useState", value = "使用状态(1-未使用；2-已使用；3-已过期(已失效)）", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<CouponListVO>> list(HttpServletRequest request, Integer useState) {
        Member member = UserUtil.getUser(request, Member.class);

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        CouponMemberExample example = new CouponMemberExample();
        example.setMemberId(member.getMemberId());
        if (useState == CouponConst.USE_STATE_3) {
            //查询已过期或者后台执行失效操作的数据
            example.setEffectiveEndBefore(new Date());
        } else if (useState == CouponConst.USE_STATE_1) {
            example.setUseState(useState);
            example.setEffectiveEndAfter(new Date());
        } else {
            example.setUseState(CouponConst.USE_STATE_2);
        }
        List<CouponMember> list = couponMemberModel.getCouponMemberList(example, pager);

        List<CouponListVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(couponMember -> {
                Coupon coupon = couponModel.getCouponByCouponId(couponMember.getCouponId());
                AssertUtil.notNull(coupon, "获取优惠券信息为空");
                CouponListVO vo = new CouponListVO(coupon, couponMember);
                if (useState == CouponConst.USE_STATE_3) {
                    vo.setUseState(CouponConst.USE_STATE_3);
                }
                StringBuilder builder = new StringBuilder();
                //根据优惠券适用商品类型获取相应id串
                //适用全部商品
                if (coupon.getUseType() == CouponConst.USE_TYPE_1) {
                    vo.setGoodsIds(null);
                    vo.setCateIds(null);
                } else if (coupon.getUseType() == CouponConst.USE_TYPE_2) {
                    //适用指定商品
                    CouponGoodsExample couponGoodsExample = new CouponGoodsExample();
                    couponGoodsExample.setCouponId(coupon.getCouponId());
                    List<CouponGoods> couponGoodsList = couponGoodsModel.getCouponGoodsList(couponGoodsExample, null);
                    if (!CollectionUtils.isEmpty(couponGoodsList)) {
                        for (CouponGoods couponGoods : couponGoodsList) {
                            builder.append(couponGoods.getGoodsId());
                            builder.append(",");
                        }
                    }
                    vo.setGoodsIds(builder.substring(0, builder.length() - 1));
                    vo.setCateIds(null);
                } else if (coupon.getUseType() == CouponConst.USE_TYPE_3) {
                    //适用指定分类
                    CouponGoodsCategoryExample couponGoodsCategoryExample = new CouponGoodsCategoryExample();
                    couponGoodsCategoryExample.setCouponId(coupon.getCouponId());
                    List<CouponGoodsCategory> couponGoodsCategoryList = couponGoodsCategoryModel.getCouponGoodsCategoryList(couponGoodsCategoryExample, null);
                    if (!CollectionUtils.isEmpty(couponGoodsCategoryList)) {
                        for (CouponGoodsCategory couponGoodsCategory : couponGoodsCategoryList) {
                            builder.append(couponGoodsCategory.getCategoryId());
                            builder.append(",");
                        }
                    }
                    vo.setCateIds(builder.substring(0, builder.length() - 1));
                    vo.setGoodsIds(null);
                }
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("获取店铺发放的优惠券列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeId", value = "店铺id", required = true),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("storeCouponList")
    public JsonResult<PageVO<StoreCouponVO>> getStoreCouponList(HttpServletRequest request, @RequestParam("storeId") Long storeId) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        Member member = UserUtil.getUser(request, Member.class);

        CouponExample couponExample = new CouponExample();
        couponExample.setStoreId(storeId);
        couponExample.setState(CouponConst.ACTIVITY_STATE_1);
        couponExample.setPublishType(PUBLISH_TYPE_1);
        couponExample.setPublishStartTimeBefore(new Date());
        couponExample.setPublishEndTimeAfter(new Date());
        List<Coupon> couponList = couponModel.getCouponList(couponExample, pager);
        List<StoreCouponVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(couponList)) {
            for (Coupon coupon : couponList) {
                StoreCouponVO vo = new StoreCouponVO(coupon);
                //优惠券领取开始和结束时间
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String receiveStartTime = format.format(coupon.getPublishStartTime());
                String receiveEndTime = format.format(coupon.getPublishEndTime());
                vo.setPublishStartTime(receiveStartTime);
                vo.setPublishEndTime(receiveEndTime);
                //优惠券使用开始时间和结束时间
                if (!StringUtils.isEmpty(coupon.getEffectiveStart()) && !StringUtils.isEmpty(coupon.getEffectiveEnd())) {
                    String useStartTime = format.format(coupon.getEffectiveStart());
                    String userEndTime = format.format(coupon.getEffectiveEnd());
                    vo.setEffectiveStart(useStartTime);
                    vo.setEffectiveEnd(userEndTime);
                }
                //根据优惠券类型获取优惠金额或折扣
                //1.固定优惠券
                if (coupon.getCouponType().equals(COUPON_TYPE_1)) {
                    vo.setPublishValue(coupon.getPublishValue().toString());
                }
                //2.折扣优惠券
                if (coupon.getCouponType().equals(COUPON_TYPE_2)) {
                    vo.setPublishValue(coupon.getPublishValue().divide(new BigDecimal(10)).toString());
                }
                //获取使用商品类型为指定商品的商品id
                StringBuilder builder = new StringBuilder();
                if (coupon.getUseType().equals(USE_TYPE_2)) {
                    //根据优惠券id查询优惠券绑定的商品
                    CouponGoodsExample couponGoodsExample = new CouponGoodsExample();
                    couponGoodsExample.setCouponId(coupon.getCouponId());
                    List<CouponGoods> couponGoodsList = couponGoodsModel.getCouponGoodsList(couponGoodsExample, null);
                    if (!CollectionUtils.isEmpty(couponGoodsList)) {
                        for (CouponGoods couponGoods : couponGoodsList) {
                            builder.append(couponGoods.getGoodsId());
                            builder.append(",");
                        }
                    }
                    vo.setGoodsIds(builder.substring(0, builder.length() - 1));
                }
                if (StringUtil.isNullOrZero(member.getMemberId())) {
                    vo.setIsReceive(COUPON_NOT_RECEIVE);
                } else {
                    //判断会员是否领取该优惠券
                    CouponMemberExample couponMemberExample = new CouponMemberExample();
                    couponMemberExample.setMemberId(member.getMemberId());
                    couponMemberExample.setCouponId(coupon.getCouponId());
                    couponMemberExample.setStoreId(storeId);
                    List<CouponMember> couponMemberList = couponMemberModel.getCouponMemberList(couponMemberExample, null);
                    if (CollectionUtils.isEmpty(couponMemberList)) {
                        //未领取的优惠券判断是否还能领
                        if (coupon.getReceivedNum().equals(coupon.getPublishNum())) {
                            //已抢完
                            vo.setIsReceive(COUPON_FINISHED);
                        } else {
                            //未领取，还能领取
                            vo.setIsReceive(COUPON_NOT_RECEIVE);
                        }
                    } else {
                        //已领取
                        if (vo.getReceivePercent() != 100) {
                            if (coupon.getLimitReceive().equals(couponMemberList.size())) {
                                vo.setIsReceive(COUPON_IS_RECEIVE);
                            } else {
                                //已领取，还能继续领
                                vo.setIsReceive(COUPON_NOT_RECEIVE);
                            }
                        } else {
                            vo.setIsReceive(COUPON_FINISHED);
                        }
                    }
                }
                vos.add(vo);
            }
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("根据货品id获取店铺发放的优惠券列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "货品id", required = true),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("storeCouponListByProductId")
    public JsonResult<PageVO<StoreCouponVO>> getStoreCouponListByProductId(HttpServletRequest request, @RequestParam("productId") Long productId) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        Member member = UserUtil.getUser(request, Member.class);

        Product product = productModel.getProductByProductId(productId);
        AssertUtil.notNull(product, "货品不存在");
        CouponExample couponExample = new CouponExample();
        couponExample.setStoreId(product.getStoreId());
        couponExample.setState(CouponConst.ACTIVITY_STATE_1);
        couponExample.setPublishType(PUBLISH_TYPE_1);
        couponExample.setPublishStartTimeBefore(new Date());
        couponExample.setPublishEndTimeAfter(new Date());
        couponExample.setPager(pager);
        List<Coupon> couponList = couponModel.getCouponList(couponExample, pager);
        List<StoreCouponVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(couponList)) {
            for (Coupon coupon : couponList) {
                StoreCouponVO vo = new StoreCouponVO(coupon);
                //优惠券领取开始和结束时间
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String receiveStartTime = format.format(coupon.getPublishStartTime());
                String receiveEndTime = format.format(coupon.getPublishEndTime());
                vo.setPublishStartTime(receiveStartTime);
                vo.setPublishEndTime(receiveEndTime);
                //优惠券使用开始时间和结束时间
                if (!StringUtils.isEmpty(coupon.getEffectiveStart()) && !StringUtils.isEmpty(coupon.getEffectiveEnd())) {
                    String useStartTime = format.format(coupon.getEffectiveStart());
                    String userEndTime = format.format(coupon.getEffectiveEnd());
                    vo.setEffectiveStart(useStartTime);
                    vo.setEffectiveEnd(userEndTime);
                }
                //根据优惠券类型获取优惠金额或折扣
                //1.固定优惠券
                if (coupon.getCouponType().equals(COUPON_TYPE_1)) {
                    vo.setPublishValue(coupon.getPublishValue().toString());
                }
                //2.折扣优惠券
                if (coupon.getCouponType().equals(COUPON_TYPE_2)) {
                    vo.setPublishValue(coupon.getPublishValue().divide(new BigDecimal(10)).toString());
                }
                //获取使用商品类型为指定商品的商品id
                StringBuilder builder = new StringBuilder();
                if (coupon.getUseType().equals(USE_TYPE_2)) {
                    //根据优惠券id查询优惠券绑定的商品
                    CouponGoodsExample couponGoodsExample = new CouponGoodsExample();
                    couponGoodsExample.setCouponId(coupon.getCouponId());
                    List<CouponGoods> couponGoodsList = couponGoodsModel.getCouponGoodsList(couponGoodsExample, null);
                    if (!CollectionUtils.isEmpty(couponGoodsList)) {
                        for (CouponGoods couponGoods : couponGoodsList) {
                            builder.append(couponGoods.getGoodsId());
                            builder.append(",");
                        }
                    }
                    if (!builder.toString().contains(product.getGoodsId() + "")) {
                        pager.setRowsCount(pager.getRowsCount() - 1);
                        continue;
                    }
                    vo.setGoodsIds(builder.substring(0, builder.length() - 1));
                }
                if (StringUtil.isNullOrZero(member.getMemberId())) {
                    vo.setIsReceive(COUPON_NOT_RECEIVE);
                } else {
                    //判断会员是否领取该优惠券
                    CouponMemberExample couponMemberExample = new CouponMemberExample();
                    couponMemberExample.setMemberId(member.getMemberId());
                    couponMemberExample.setCouponId(coupon.getCouponId());
                    couponMemberExample.setStoreId(product.getStoreId());
                    List<CouponMember> couponMemberList = couponMemberModel.getCouponMemberList(couponMemberExample, null);
                    if (CollectionUtils.isEmpty(couponMemberList)) {
                        //未领取的优惠券判断是否还能领
                        if (coupon.getReceivedNum().equals(coupon.getPublishNum())) {
                            //已抢完
                            vo.setIsReceive(COUPON_FINISHED);
                        } else {
                            //未领取，还能领取
                            vo.setIsReceive(COUPON_NOT_RECEIVE);
                        }
                    } else {
                        //已领取
                        if (vo.getReceivePercent() != 100) {
                            if (coupon.getLimitReceive().equals(couponMemberList.size())) {
                                vo.setIsReceive(COUPON_IS_RECEIVE);
                            } else {
                                //已领取，还能继续领
                                vo.setIsReceive(COUPON_NOT_RECEIVE);
                            }
                        } else {
                            vo.setIsReceive(COUPON_FINISHED);
                        }
                    }
                }
                vos.add(vo);
            }
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }
}
