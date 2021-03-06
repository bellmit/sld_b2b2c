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

@Api(tags = "front-???????????????")
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

    @ApiOperation("??????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isSelected", value = "???????????????true-??????false-???", defaultValue = "true", paramType = "query"),
            @ApiImplicitParam(name = "categoryId", value = "??????id", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "????????????", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "??????????????????", defaultValue = "1", paramType = "query")
    })
    @GetMapping("couponCenter")
    public JsonResult<FrontCouponVO> couponCenter(HttpServletRequest request, Integer categoryId,
                                                  @RequestParam(value = "isSelected", defaultValue = "true") boolean isSelected) {
        Member member = UserUtil.getUser(request, Member.class);

        //??????????????????
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
        //?????????????????????
        CouponExample example = new CouponExample();
        //??????????????????????????????????????????
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
                //???????????????????????????????????????????????????????????????????????????
                if (member.getMemberId() == null) {
                    if (coupon.getReceivedNum().equals(coupon.getPublishNum())) {
                        //?????????
                        couponVO.setReceivedState(CouponConst.COUPON_FINISHED);
                    } else {
                        //?????????????????????
                        couponVO.setReceivedState(CouponConst.COUPON_NOT_RECEIVE);
                    }
                } else {
                    CouponMemberExample couponMemberExample = new CouponMemberExample();
                    couponMemberExample.setMemberId(member.getMemberId());
                    couponMemberExample.setCouponId(coupon.getCouponId());
                    List<CouponMember> couponMemberList = couponMemberModel.getCouponMemberList(couponMemberExample, null);
                    if (CollectionUtils.isEmpty(couponMemberList)) {
                        //??????????????????????????????????????????
                        if (coupon.getReceivedNum().equals(coupon.getPublishNum())) {
                            //?????????
                            couponVO.setReceivedState(CouponConst.COUPON_FINISHED);
                        } else {
                            //?????????????????????
                            couponVO.setReceivedState(CouponConst.COUPON_NOT_RECEIVE);
                        }
                    } else {
                        //?????????
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

    @ApiOperation("???????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "couponId", value = "?????????id", required = true, paramType = "query")
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

    @ApiOperation("?????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "useState", value = "????????????(1-????????????2-????????????3-?????????(?????????)???", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "????????????", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "??????????????????", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<CouponListVO>> list(HttpServletRequest request, Integer useState) {
        Member member = UserUtil.getUser(request, Member.class);

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        CouponMemberExample example = new CouponMemberExample();
        example.setMemberId(member.getMemberId());
        if (useState == CouponConst.USE_STATE_3) {
            //??????????????????????????????????????????????????????
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
                AssertUtil.notNull(coupon, "???????????????????????????");
                CouponListVO vo = new CouponListVO(coupon, couponMember);
                if (useState == CouponConst.USE_STATE_3) {
                    vo.setUseState(CouponConst.USE_STATE_3);
                }
                StringBuilder builder = new StringBuilder();
                //?????????????????????????????????????????????id???
                //??????????????????
                if (coupon.getUseType() == CouponConst.USE_TYPE_1) {
                    vo.setGoodsIds(null);
                    vo.setCateIds(null);
                } else if (coupon.getUseType() == CouponConst.USE_TYPE_2) {
                    //??????????????????
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
                    //??????????????????
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

    @ApiOperation("????????????????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeId", value = "??????id", required = true),
            @ApiImplicitParam(name = "pageSize", value = "????????????", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "??????????????????", defaultValue = "1", paramType = "query")
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
                //????????????????????????????????????
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String receiveStartTime = format.format(coupon.getPublishStartTime());
                String receiveEndTime = format.format(coupon.getPublishEndTime());
                vo.setPublishStartTime(receiveStartTime);
                vo.setPublishEndTime(receiveEndTime);
                //??????????????????????????????????????????
                if (!StringUtils.isEmpty(coupon.getEffectiveStart()) && !StringUtils.isEmpty(coupon.getEffectiveEnd())) {
                    String useStartTime = format.format(coupon.getEffectiveStart());
                    String userEndTime = format.format(coupon.getEffectiveEnd());
                    vo.setEffectiveStart(useStartTime);
                    vo.setEffectiveEnd(userEndTime);
                }
                //????????????????????????????????????????????????
                //1.???????????????
                if (coupon.getCouponType().equals(COUPON_TYPE_1)) {
                    vo.setPublishValue(coupon.getPublishValue().toString());
                }
                //2.???????????????
                if (coupon.getCouponType().equals(COUPON_TYPE_2)) {
                    vo.setPublishValue(coupon.getPublishValue().divide(new BigDecimal(10)).toString());
                }
                //????????????????????????????????????????????????id
                StringBuilder builder = new StringBuilder();
                if (coupon.getUseType().equals(USE_TYPE_2)) {
                    //???????????????id??????????????????????????????
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
                    //????????????????????????????????????
                    CouponMemberExample couponMemberExample = new CouponMemberExample();
                    couponMemberExample.setMemberId(member.getMemberId());
                    couponMemberExample.setCouponId(coupon.getCouponId());
                    couponMemberExample.setStoreId(storeId);
                    List<CouponMember> couponMemberList = couponMemberModel.getCouponMemberList(couponMemberExample, null);
                    if (CollectionUtils.isEmpty(couponMemberList)) {
                        //??????????????????????????????????????????
                        if (coupon.getReceivedNum().equals(coupon.getPublishNum())) {
                            //?????????
                            vo.setIsReceive(COUPON_FINISHED);
                        } else {
                            //????????????????????????
                            vo.setIsReceive(COUPON_NOT_RECEIVE);
                        }
                    } else {
                        //?????????
                        if (vo.getReceivePercent() != 100) {
                            if (coupon.getLimitReceive().equals(couponMemberList.size())) {
                                vo.setIsReceive(COUPON_IS_RECEIVE);
                            } else {
                                //???????????????????????????
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

    @ApiOperation("????????????id????????????????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "??????id", required = true),
            @ApiImplicitParam(name = "pageSize", value = "????????????", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "??????????????????", defaultValue = "1", paramType = "query")
    })
    @GetMapping("storeCouponListByProductId")
    public JsonResult<PageVO<StoreCouponVO>> getStoreCouponListByProductId(HttpServletRequest request, @RequestParam("productId") Long productId) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        Member member = UserUtil.getUser(request, Member.class);

        Product product = productModel.getProductByProductId(productId);
        AssertUtil.notNull(product, "???????????????");
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
                //????????????????????????????????????
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String receiveStartTime = format.format(coupon.getPublishStartTime());
                String receiveEndTime = format.format(coupon.getPublishEndTime());
                vo.setPublishStartTime(receiveStartTime);
                vo.setPublishEndTime(receiveEndTime);
                //??????????????????????????????????????????
                if (!StringUtils.isEmpty(coupon.getEffectiveStart()) && !StringUtils.isEmpty(coupon.getEffectiveEnd())) {
                    String useStartTime = format.format(coupon.getEffectiveStart());
                    String userEndTime = format.format(coupon.getEffectiveEnd());
                    vo.setEffectiveStart(useStartTime);
                    vo.setEffectiveEnd(userEndTime);
                }
                //????????????????????????????????????????????????
                //1.???????????????
                if (coupon.getCouponType().equals(COUPON_TYPE_1)) {
                    vo.setPublishValue(coupon.getPublishValue().toString());
                }
                //2.???????????????
                if (coupon.getCouponType().equals(COUPON_TYPE_2)) {
                    vo.setPublishValue(coupon.getPublishValue().divide(new BigDecimal(10)).toString());
                }
                //????????????????????????????????????????????????id
                StringBuilder builder = new StringBuilder();
                if (coupon.getUseType().equals(USE_TYPE_2)) {
                    //???????????????id??????????????????????????????
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
                    //????????????????????????????????????
                    CouponMemberExample couponMemberExample = new CouponMemberExample();
                    couponMemberExample.setMemberId(member.getMemberId());
                    couponMemberExample.setCouponId(coupon.getCouponId());
                    couponMemberExample.setStoreId(product.getStoreId());
                    List<CouponMember> couponMemberList = couponMemberModel.getCouponMemberList(couponMemberExample, null);
                    if (CollectionUtils.isEmpty(couponMemberList)) {
                        //??????????????????????????????????????????
                        if (coupon.getReceivedNum().equals(coupon.getPublishNum())) {
                            //?????????
                            vo.setIsReceive(COUPON_FINISHED);
                        } else {
                            //????????????????????????
                            vo.setIsReceive(COUPON_NOT_RECEIVE);
                        }
                    } else {
                        //?????????
                        if (vo.getReceivePercent() != 100) {
                            if (coupon.getLimitReceive().equals(couponMemberList.size())) {
                                vo.setIsReceive(COUPON_IS_RECEIVE);
                            } else {
                                //???????????????????????????
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
