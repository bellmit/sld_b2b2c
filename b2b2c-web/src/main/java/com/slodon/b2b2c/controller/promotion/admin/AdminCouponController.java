package com.slodon.b2b2c.controller.promotion.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.CouponConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.*;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.GoodsCategory;
import com.slodon.b2b2c.model.goods.GoodsCategoryModel;
import com.slodon.b2b2c.model.goods.GoodsModel;
import com.slodon.b2b2c.model.promotion.CouponGoodsCategoryModel;
import com.slodon.b2b2c.model.promotion.CouponGoodsModel;
import com.slodon.b2b2c.model.promotion.CouponMemberModel;
import com.slodon.b2b2c.model.promotion.CouponModel;
import com.slodon.b2b2c.promotion.dto.CouponAddDTO;
import com.slodon.b2b2c.promotion.dto.CouponUpdateDTO;
import com.slodon.b2b2c.promotion.example.CouponExample;
import com.slodon.b2b2c.promotion.example.CouponGoodsCategoryExample;
import com.slodon.b2b2c.promotion.example.CouponGoodsExample;
import com.slodon.b2b2c.promotion.example.CouponMemberExample;
import com.slodon.b2b2c.promotion.pojo.Coupon;
import com.slodon.b2b2c.promotion.pojo.CouponGoods;
import com.slodon.b2b2c.promotion.pojo.CouponGoodsCategory;
import com.slodon.b2b2c.promotion.pojo.CouponMember;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.vo.promotion.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "admin-优惠券")
@RestController
@Slf4j
@RequestMapping("v3/promotion/admin/coupon")
public class AdminCouponController extends BaseController {

    @Resource
    private CouponModel couponModel;
    @Resource
    private CouponGoodsModel couponGoodsModel;
    @Resource
    private CouponGoodsCategoryModel couponGoodsCategoryModel;
    @Resource
    private CouponMemberModel couponMemberModel;
    @Resource
    private GoodsModel goodsModel;
    @Resource
    private GoodsCategoryModel goodsCategoryModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("优惠券列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "systemType", value = "系统类型（admin:平台优惠券，seller:店铺优惠券）", required = true, paramType = "query"),
            @ApiImplicitParam(name = "storeName", value = "店铺名称", paramType = "query"),
            @ApiImplicitParam(name = "couponName", value = "优惠券名称", paramType = "query"),
            @ApiImplicitParam(name = "couponType", value = "优惠券类型(1-固定金额券；2-折扣券（折扣比例）；3-随机金额券）", paramType = "query"),
            @ApiImplicitParam(name = "publishType", value = "活动类型(1-免费领取；3-活动赠送）", paramType = "query"),
            @ApiImplicitParam(name = "publishStartTime", value = "活动开始时间", paramType = "query"),
            @ApiImplicitParam(name = "publishEndTime", value = "活动结束时间", paramType = "query"),
            @ApiImplicitParam(name = "effectiveStart", value = "使用开始时间", paramType = "query"),
            @ApiImplicitParam(name = "effectiveEnd", value = "使用结束时间", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "活动状态(1-未开始;2-进行中;3-已失效;4-已结束)", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<CouponVO>> list(HttpServletRequest request, String systemType, String storeName, String couponName,
                                             Integer couponType, Integer state, Integer publishType, Date publishStartTime,
                                             Date publishEndTime, Date effectiveStart, Date effectiveEnd) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        CouponExample example = new CouponExample();
        if (systemType.equals("admin")) {
            example.setStoreId(0L);
        } else {
            example.setStoreIdNotEquals(0L);
        }
        example.setStoreNameLike(storeName);
        example.setCouponNameLike(couponName);
        example.setCouponType(couponType);
        example.setPublishType(publishType);
        example.setPublishStartTimeBefore(publishEndTime);
        example.setPublishEndTimeAfter(publishStartTime);
        example.setEffectiveStartBefore(effectiveEnd);
        example.setEffectiveEndAfter(effectiveStart);
        if (!StringUtil.isNullOrZero(state)) {
            if (state == CouponConst.STATE_1) {
                example.setState(CouponConst.STATE_1);
                example.setPublishStartTimeAfter(new Date());
            } else if (state == CouponConst.STATE_2) {
                example.setState(CouponConst.STATE_1);
                example.setPublishStartTimeBefore(new Date());
                example.setPublishEndTimeAfter(new Date());
            } else if (state == CouponConst.STATE_3) {
                example.setState(CouponConst.STATE_2);
            } else if (state == CouponConst.STATE_4) {
                example.setState(CouponConst.STATE_1);
                example.setPublishEndTimeBefore(new Date());
            }
        }
        example.setStateNotEquals(CouponConst.ACTIVITY_STATE_3);
        List<Coupon> list = couponModel.getCouponList(example, pager);
        List<CouponVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(coupon -> {
                vos.add(new CouponVO(coupon));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("正常优惠券列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "couponName", value = "优惠券名称", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "活动状态(1-未开始;2-进行中;)", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("normalList")
    public JsonResult<PageVO<CouponVO>> normalList(HttpServletRequest request, String couponName, Integer state) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        CouponExample example = new CouponExample();
        example.setCouponNameLike(couponName);
        //例:签到赠送优惠券只有平台优惠券
        example.setStoreId(0L);
        if (!StringUtil.isNullOrZero(state)) {
            if (state == CouponConst.STATE_1) {
                example.setState(CouponConst.STATE_1);
                example.setPublishStartTimeAfter(new Date());
            } else if (state == CouponConst.STATE_2) {
                example.setState(CouponConst.STATE_1);
                example.setPublishStartTimeBefore(new Date());
                example.setPublishEndTimeAfter(new Date());
            }
        } else {
            example.setState(CouponConst.STATE_1);
            example.setPublishEndTimeAfter(new Date());
        }
        example.setStateNotEquals(CouponConst.ACTIVITY_STATE_3);
        List<Coupon> list = couponModel.getCouponList(example, pager);
        List<CouponVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(coupon -> {
                vos.add(new CouponVO(coupon));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("优惠券详情")
    @GetMapping("detail")
    public JsonResult<CouponDetailVO> detail(HttpServletRequest request, Integer couponId) {
        AssertUtil.notNullOrZero(couponId, "优惠券id不能为空");

        Coupon coupon = couponModel.getCouponByCouponId(couponId);
        AssertUtil.notNull(coupon, "获取优惠券信息为空，请重试！");

        CouponDetailVO detailVO = new CouponDetailVO(coupon);
        if (coupon.getUseType() == CouponConst.USE_TYPE_3) {
            //1、取第一级分类
            CouponGoodsCategoryExample example = new CouponGoodsCategoryExample();
            example.setCouponId(coupon.getCouponId());
            List<CouponGoodsCategory> list = couponGoodsCategoryModel.getCouponGoodsCategoryList(example, null);

            List<CouponGoodsCategoryVO> vos = new ArrayList<>();
            if (!CollectionUtils.isEmpty(list)) {
                for (CouponGoodsCategory category : list) {
                    CouponGoodsCategoryVO vo = new CouponGoodsCategoryVO();
                    if (category.getCategoryGrade() == CouponConst.CATEGORY_GRADE_1) {
                        vo.setCategoryId1(category.getCategoryId());
                        vo.setCategoryName1(category.getCategoryName());
                    } else if (category.getCategoryGrade() == CouponConst.CATEGORY_GRADE_2) {
                        vo.setCategoryId2(category.getCategoryId());
                        vo.setCategoryName2(category.getCategoryName());
                        //查询二级分类
                        GoodsCategory goodsCategory2 = goodsCategoryModel.getGoodsCategoryByCategoryId(category.getCategoryId());
                        AssertUtil.notNull(goodsCategory2, "获取二级分类信息为空，请重试");
                        //查询一级分类
                        GoodsCategory goodsCategory1 = goodsCategoryModel.getGoodsCategoryByCategoryId(goodsCategory2.getPid());
                        AssertUtil.notNull(goodsCategory1, "获取一级分类信息为空，请重试");

                        vo.setCategoryId1(goodsCategory1.getCategoryId());
                        vo.setCategoryName1(goodsCategory1.getCategoryName());
                    } else if (category.getCategoryGrade() == CouponConst.CATEGORY_GRADE_3) {
                        vo.setCategoryId3(category.getCategoryId());
                        vo.setCategoryName3(category.getCategoryName());
                        //查询二级分类
                        GoodsCategory goodsCategory = goodsCategoryModel.getGoodsCategoryByCategoryId(category.getCategoryId());
                        AssertUtil.notNull(goodsCategory, "获取三级分类信息为空，请重试");
                        GoodsCategory goodsCategory2 = goodsCategoryModel.getGoodsCategoryByCategoryId(goodsCategory.getPid());
                        AssertUtil.notNull(goodsCategory2, "获取二级分类信息为空，请重试");

                        vo.setCategoryId2(goodsCategory2.getCategoryId());
                        vo.setCategoryName2(goodsCategory2.getCategoryName());
                        //查询一级分类
                        GoodsCategory goodsCategory1 = goodsCategoryModel.getGoodsCategoryByCategoryId(goodsCategory2.getPid());
                        AssertUtil.notNull(goodsCategory1, "获取一级分类信息为空，请重试");

                        vo.setCategoryId1(goodsCategory1.getCategoryId());
                        vo.setCategoryName1(goodsCategory1.getCategoryName());
                    }
                    vos.add(vo);
                }
            }
            detailVO.setGoodsCategoryList(vos);
        }
        return SldResponse.success(detailVO);
    }

    @ApiOperation("优惠券指定商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "couponId", value = "优惠券活动id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("goodsList")
    public JsonResult<PageVO<CouponGoodsVO>> goodsList(HttpServletRequest request, Integer couponId) {
        AssertUtil.notNullOrZero(couponId, "优惠券id不能为空");
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        CouponGoodsExample example = new CouponGoodsExample();
        example.setCouponId(couponId);
        List<CouponGoods> list = couponGoodsModel.getCouponGoodsList(example, pager);
        List<CouponGoodsVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            for (CouponGoods couponGoods : list) {
                Goods goods = goodsModel.getGoodsByGoodsId(couponGoods.getGoodsId());
                AssertUtil.notNull(goods, "获取商品信息为空，请重试！");

                vos.add(new CouponGoodsVO(goods));
            }
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("新建优惠券")
    @OperationLogger(option = "新建优惠券")
    @PostMapping("add")
    public JsonResult addCoupon(HttpServletRequest request, CouponAddDTO couponAddDTO) {
        AssertUtil.isTrue(!"1".equals(stringRedisTemplate.opsForValue().get("coupon_is_enable")), "优惠券活动未开启");
        AssertUtil.isTrue(couponAddDTO.getPublishStartTime().before(TimeUtil.getDayStartFormatYMDHMS(new Date())), "领取开始时间应在当前日期之后，请重新输入");
        Admin admin = UserUtil.getUser(request, Admin.class);

        Coupon coupon = new Coupon();
        BeanUtils.copyProperties(couponAddDTO, coupon);
        if (couponAddDTO.getLimitQuota().equals(BigDecimal.ZERO)) {
            coupon.setCouponContent("无门槛");
        } else {
            //拼接优惠内容
            if (couponAddDTO.getCouponType() == CouponConst.COUPON_TYPE_1) {
                coupon.setCouponContent("满" + couponAddDTO.getLimitQuota() + "减" + couponAddDTO.getPublishValue());
            } else if (couponAddDTO.getCouponType() == CouponConst.COUPON_TYPE_2) {
                coupon.setCouponContent("满" + couponAddDTO.getLimitQuota() + "打" + couponAddDTO.getPublishValue().divide(new BigDecimal(10)) + "折");
            } else if (couponAddDTO.getCouponType() == CouponConst.COUPON_TYPE_3) {
                AssertUtil.isTrue(couponAddDTO.getRandomMin().compareTo(couponAddDTO.getRandomMax()) > 0, "随机金额券最小值不大于最大值，请重新输入");
                AssertUtil.isTrue(couponAddDTO.getRandomMin().multiply(new BigDecimal(couponAddDTO.getPublishNum())).compareTo(couponAddDTO.getPublishAmount()) > 0 || couponAddDTO.getPublishAmount().compareTo(couponAddDTO.getRandomMax().multiply(new BigDecimal(couponAddDTO.getPublishNum()))) > 0, "发行总金额必须在" + couponAddDTO.getRandomMin().multiply(new BigDecimal(couponAddDTO.getPublishNum())) + "~" + couponAddDTO.getRandomMax().multiply(new BigDecimal(couponAddDTO.getPublishNum())) + "元之间，请重新输入");
                coupon.setCouponContent(couponAddDTO.getRandomMin() + "-" + couponAddDTO.getRandomMax() + "随机");
            }
        }
        //如果cycle为空，说明是固定起始时间；否则就是领券当日起cycle天内可用
        if (StringUtil.isNullOrZero(couponAddDTO.getCycle())) {
            coupon.setEffectiveTimeType(CouponConst.EFFECTIVE_TIME_TYPE_1);
            AssertUtil.isTrue(couponAddDTO.getEffectiveStart().before(couponAddDTO.getPublishStartTime()), "使用开始时间应在领取开始时间之后，请重新输入");
            AssertUtil.isTrue(couponAddDTO.getEffectiveEnd().before(couponAddDTO.getPublishEndTime()), "使用结束时间应在领取结束时间之后，请重新输入");
        } else {
            coupon.setEffectiveTimeType(CouponConst.EFFECTIVE_TIME_TYPE_2);
        }
        coupon.setStoreId(0L);
        coupon.setStoreName("");
        coupon.setCreateUserId(admin.getAdminId().longValue());
        coupon.setCreateUserName(admin.getAdminName());
        couponModel.saveCoupon(coupon, couponAddDTO.getGoodsIds(), couponAddDTO.getCategoryIds());
        return SldResponse.success("添加成功");
    }

    @ApiOperation("编辑优惠券")
    @OperationLogger(option = "编辑优惠券")
    @PostMapping("update")
    public JsonResult updateCoupon(HttpServletRequest request, CouponUpdateDTO couponUpdateDTO) {
        Admin admin = UserUtil.getUser(request, Admin.class);

        AssertUtil.isTrue(couponUpdateDTO.getLimitReceive() > couponUpdateDTO.getPublishNum(), "领取数量不可大于发行数量");
        AssertUtil.isTrue(couponUpdateDTO.getPublishStartTime().before(TimeUtil.getDayStartFormatYMDHMS(new Date())), "领取开始时间应在当前日期之后，请重新输入");
        Coupon coupon = new Coupon();
        BeanUtils.copyProperties(couponUpdateDTO, coupon);
        if (couponUpdateDTO.getLimitQuota().equals(BigDecimal.ZERO)) {
            coupon.setCouponContent("无门槛");
        } else {
            //拼接优惠内容
            if (couponUpdateDTO.getCouponType() == CouponConst.COUPON_TYPE_1) {
                coupon.setCouponContent("满" + couponUpdateDTO.getLimitQuota() + "减" + couponUpdateDTO.getPublishValue());
            } else if (couponUpdateDTO.getCouponType() == CouponConst.COUPON_TYPE_2) {
                coupon.setCouponContent("满" + couponUpdateDTO.getLimitQuota() + "打" + couponUpdateDTO.getPublishValue().divide(new BigDecimal(10)) + "折");
            } else if (couponUpdateDTO.getCouponType() == CouponConst.COUPON_TYPE_3) {
                AssertUtil.isTrue(couponUpdateDTO.getRandomMin().compareTo(couponUpdateDTO.getRandomMax()) > 0, "随机金额券最小值不大于最大值，请重新输入");
                AssertUtil.isTrue(couponUpdateDTO.getRandomMin().multiply(new BigDecimal(couponUpdateDTO.getPublishNum())).compareTo(couponUpdateDTO.getPublishAmount()) > 0 || couponUpdateDTO.getPublishAmount().compareTo(couponUpdateDTO.getRandomMax().multiply(new BigDecimal(couponUpdateDTO.getPublishNum()))) > 0, "发行总金额必须在" + couponUpdateDTO.getRandomMin().multiply(new BigDecimal(couponUpdateDTO.getPublishNum())) + "~" + couponUpdateDTO.getRandomMax().multiply(new BigDecimal(couponUpdateDTO.getPublishNum())) + "元之间，请重新输入");
                coupon.setCouponContent(couponUpdateDTO.getRandomMin() + "-" + couponUpdateDTO.getRandomMax() + "随机");
            }
        }
        //如果cycle为空，说明是固定起始时间；否则就是领券当日起cycle天内可用
        if (StringUtil.isNullOrZero(couponUpdateDTO.getCycle())) {
            coupon.setEffectiveTimeType(CouponConst.EFFECTIVE_TIME_TYPE_1);
            AssertUtil.isTrue(couponUpdateDTO.getEffectiveStart().before(couponUpdateDTO.getPublishStartTime()), "使用开始时间应在领取开始时间之后，请重新输入");
            AssertUtil.isTrue(couponUpdateDTO.getEffectiveEnd().before(couponUpdateDTO.getPublishEndTime()), "使用结束时间应在领取结束时间之后，请重新输入");
        } else {
            coupon.setEffectiveTimeType(CouponConst.EFFECTIVE_TIME_TYPE_2);
        }
        coupon.setStoreId(0L);
        couponModel.updateCoupon(coupon, couponUpdateDTO.getGoodsIds(), couponUpdateDTO.getCategoryIds());
        return SldResponse.success("编辑成功");
    }

    @ApiOperation("删除优惠券")
    @OperationLogger(option = "删除优惠券")
    @PostMapping("del")
    public JsonResult delCoupon(HttpServletRequest request, Integer couponId) {
        //查询是否有用户领取未使用的优惠券，有则不能删除
        CouponMemberExample example = new CouponMemberExample();
        example.setCouponId(couponId);
        example.setEffectiveStartBefore(new Date());
        example.setEffectiveEndAfter(new Date());
        example.setUseState(CouponConst.USE_STATE_1);
        List<CouponMember> list = couponMemberModel.getCouponMemberList(example, null);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(list), "该优惠券已有用户领取未使用不能删除");

        couponModel.deleteCoupon(couponId);
        return SldResponse.success("删除成功");
    }

    @ApiOperation("失效优惠券")
    @OperationLogger(option = "失效优惠券")
    @PostMapping("invalid")
    public JsonResult invalid(HttpServletRequest request, Integer couponId) {
        Coupon coupon = new Coupon();
        coupon.setCouponId(couponId);
        coupon.setState(CouponConst.ACTIVITY_STATE_2);
        couponModel.updateCoupon(coupon);
        return SldResponse.success("失效成功");
    }

    @ApiOperation("是否推荐优惠券")
    @OperationLogger(option = "是否推荐优惠券")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "couponId", value = "优惠券id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isRecommend", value = "是否推荐 1、推荐；0、不推荐", required = true, paramType = "query")
    })
    @PostMapping("isRecommend")
    public JsonResult isRecommend(HttpServletRequest request, Integer couponId, Integer isRecommend) {
        Coupon coupon = new Coupon();
        coupon.setCouponId(couponId);
        coupon.setIsRecommend(isRecommend);
        couponModel.updateCoupon(coupon);
        return SldResponse.success("推荐成功");
    }

    @ApiOperation("复制优惠券")
    @OperationLogger(option = "复制优惠券")
    @PostMapping("copy")
    public JsonResult copyCoupon(HttpServletRequest request, Integer couponId) {
        AssertUtil.notNullOrZero(couponId, "请选择要复制的数据!");

        Admin admin = UserUtil.getUser(request, Admin.class);

        Coupon coupon = new Coupon();
        coupon.setCouponId(couponId);
        coupon.setStoreId(0L);
        coupon.setCreateUserId(admin.getAdminId().longValue());
        coupon.setCreateUserName(admin.getAdminName());
        couponModel.copyCoupon(coupon);
        return SldResponse.success("复制成功");
    }

    @ApiOperation("优惠券领取详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "couponId", value = "优惠券id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "memberName", value = "会员名称", paramType = "query"),
            @ApiImplicitParam(name = "useState", value = "使用状态(1-未使用；2-使用；3-过期无效）", paramType = "query"),
            @ApiImplicitParam(name = "receiveStartTime", value = "领取开始时间", paramType = "query"),
            @ApiImplicitParam(name = "receiveEndTime", value = "领取结束时间", paramType = "query"),
            @ApiImplicitParam(name = "useStartTime", value = "使用开始时间", paramType = "query"),
            @ApiImplicitParam(name = "useEndTime", value = "使用结束时间", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("receiveDetails")
    public JsonResult<PageVO<CouponMemberVO>> receiveDetails(HttpServletRequest request, Integer couponId, String memberName,
                                                             Integer useState, Date receiveStartTime, Date receiveEndTime,
                                                             Date useStartTime, Date useEndTime) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        CouponMemberExample example = new CouponMemberExample();
        example.setCouponId(couponId);
        example.setMemberNameLike(memberName);
        example.setUseState(useState);
        example.setReceiveTimeAfter(receiveStartTime);
        example.setReceiveTimeBefore(receiveEndTime);
        example.setUseTimeAfter(useStartTime);
        example.setUseTimeBefore(useEndTime);
        List<CouponMember> list = couponMemberModel.getCouponMemberList(example, pager);
        List<CouponMemberVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(member -> {
                vos.add(new CouponMemberVO(member));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }
}
