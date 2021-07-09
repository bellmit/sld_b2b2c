package com.slodon.b2b2c.controller.promotion.seller;

import cn.hutool.core.collection.CollectionUtil;
import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.constant.PromotionConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.model.goods.GoodsModel;
import com.slodon.b2b2c.model.promotion.CouponModel;
import com.slodon.b2b2c.model.promotion.FullAmountCycleMinusModel;
import com.slodon.b2b2c.promotion.dto.FullAcmAddDTO;
import com.slodon.b2b2c.promotion.dto.FullAcmUpdateDTO;
import com.slodon.b2b2c.promotion.example.FullAmountCycleMinusExample;
import com.slodon.b2b2c.promotion.pojo.Coupon;
import com.slodon.b2b2c.promotion.pojo.FullAmountCycleMinus;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.promotion.FullAcmDetailVO;
import com.slodon.b2b2c.vo.promotion.FullCouponVO;
import com.slodon.b2b2c.vo.promotion.FullDiscountVO;
import com.slodon.b2b2c.vo.promotion.FullGiftVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "seller-循环满减")
@RestController
@Slf4j
@RequestMapping("v3/promotion/seller/fullAcm")
public class SellerFullAcmController extends BaseController {

    @Resource
    private FullAmountCycleMinusModel fullAmountCycleMinusModel;
    @Resource
    private CouponModel couponModel;
    @Resource
    private GoodsModel goodsModel;

    @ApiOperation("循环满减列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullName", value = "活动名称", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "活动开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "活动开始时间", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "活动状态( 1-待发布，2-未开始，3-进行中，4-已结束，5-已失效)", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<FullDiscountVO>> list(HttpServletRequest request, String fullName,
                                                   Date startTime, Date endTime, Integer state) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        FullAmountCycleMinusExample example = new FullAmountCycleMinusExample();
        example.setStoreId(vendor.getStoreId());
        example.setFullNameLike(fullName);
        example.setStartTimeBefore(endTime);
        example.setEndTimeAfter(startTime);
        if (!StringUtil.isNullOrZero(state)) {
            if (state == PromotionConst.STATE_RELEASE_2) {
                example.setState(PromotionConst.STATE_RELEASE_2);
                example.setStartTimeAfter(new Date());
            } else if (state == PromotionConst.STATE_ON_GOING_3) {
                example.setState(PromotionConst.STATE_RELEASE_2);
                example.setStartTimeBefore(new Date());
                example.setEndTimeAfter(new Date());
            } else if (state == PromotionConst.STATE_COMPLETE_4) {
                example.setState(PromotionConst.STATE_RELEASE_2);
                example.setEndTimeBefore(new Date());
            } else {
                example.setState(state);
            }
        }
        example.setStateNotEquals(PromotionConst.STATE_DELETED_6);
        List<FullAmountCycleMinus> list = fullAmountCycleMinusModel.getFullAmountCycleMinusList(example, pager);
        List<FullDiscountVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(discount -> {
                vos.add(new FullDiscountVO(discount));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("循环满减详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullId", value = "循环满减id", required = true)
    })
    @GetMapping("detail")
    public JsonResult<FullAcmDetailVO> detail(HttpServletRequest request, Integer fullId) {
        AssertUtil.notNullOrZero(fullId, "循环满减活动id不能为空");
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        FullAmountCycleMinus fullAmountStepMinus = fullAmountCycleMinusModel.getFullAmountCycleMinusByFullId(fullId);
        AssertUtil.notNull(fullAmountStepMinus, "循环满减id:"+fullId+"详情为空");
        AssertUtil.isTrue(!fullAmountStepMinus.getStoreId().equals(vendor.getStoreId()), "无权限");

        FullAcmDetailVO detailVO = new FullAcmDetailVO(fullAmountStepMinus);
        if (!StringUtil.isEmpty(fullAmountStepMinus.getSendCouponIds())) {
            String sendCouponIds = fullAmountStepMinus.getSendCouponIds();
            String[] couponIds = sendCouponIds.split(",");
            List<FullCouponVO> couponVOS = new ArrayList<>();
            for (String str : couponIds) {
                if (StringUtil.isEmpty(str)) continue;
                Coupon coupon = couponModel.getCouponByCouponId(Integer.parseInt(str));
                if (coupon == null) {
                    continue;
                }
                couponVOS.add(new FullCouponVO(coupon));
            }
            detailVO.setCouponList(couponVOS);
        }
        if (!StringUtil.isEmpty(fullAmountStepMinus.getSendGoodsIds())) {
            String sendGoodsIds = fullAmountStepMinus.getSendGoodsIds();
            String[] goodsIds = sendGoodsIds.split(",");
            List<FullGiftVO> giftVOS = new ArrayList<>();
            for (String str : goodsIds) {
                if (StringUtil.isEmpty(str)) continue;
                Goods goods = goodsModel.getGoodsByGoodsId(Long.parseLong(str));
                AssertUtil.notNull(goods, "获取商品信息为空");

                giftVOS.add(new FullGiftVO(goods));
            }
            detailVO.setGiftList(giftVOS);
        }
        return SldResponse.success(detailVO);
    }

    @ApiOperation("新建循环满减")
    @VendorLogger(option = "新建循环满减")
    @PostMapping("add")
    public JsonResult addFullAcm(HttpServletRequest request, FullAcmAddDTO fullAcmAddDTO) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        FullAmountCycleMinusExample example = new FullAmountCycleMinusExample();
        example.setStoreId(vendor.getStoreId());
        example.setStartTime(fullAcmAddDTO.getStartTime());
        example.setEndTime(fullAcmAddDTO.getEndTime());
        example.setQueryTime("notNull");
        example.setStateNotIn(PromotionConst.STATE_EXPIRED_5 + "," + PromotionConst.STATE_DELETED_6);
        List<FullAmountCycleMinus> list = fullAmountCycleMinusModel.getFullAmountCycleMinusList(example, null);
        AssertUtil.isTrue(!CollectionUtil.isEmpty(list), "该时段已存在其他活动，请重设活动时间");

        FullAmountCycleMinus fullAmountCycleMinus = new FullAmountCycleMinus();
        BeanUtils.copyProperties(fullAcmAddDTO, fullAmountCycleMinus);
        fullAmountCycleMinus.setStoreId(vendor.getStoreId());
        fullAmountCycleMinus.setStoreName(vendor.getStore().getStoreName());
        fullAmountCycleMinus.setCreateVendorId(vendor.getVendorId());
        fullAmountCycleMinusModel.saveFullAmountCycleMinus(fullAmountCycleMinus);
        return SldResponse.success("添加成功");
    }

    @ApiOperation("编辑循环满减")
    @VendorLogger(option = "编辑循环满减")
    @PostMapping("update")
    public JsonResult updateFullAcm(HttpServletRequest request, FullAcmUpdateDTO fullAcmUpdateDTO) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        FullAmountCycleMinusExample example = new FullAmountCycleMinusExample();
        example.setStoreId(vendor.getStoreId());
        example.setStartTimeAfter(fullAcmUpdateDTO.getStartTime());
        example.setStartTime(fullAcmUpdateDTO.getStartTime());
        example.setEndTime(fullAcmUpdateDTO.getEndTime());
        example.setQueryTime("notNull");
        example.setFullIdNotEquals(fullAcmUpdateDTO.getFullId());
        example.setStateNotIn(PromotionConst.STATE_EXPIRED_5 + "," + PromotionConst.STATE_DELETED_6);
        List<FullAmountCycleMinus> list = fullAmountCycleMinusModel.getFullAmountCycleMinusList(example, null);
        AssertUtil.isTrue(!CollectionUtil.isEmpty(list), "该时段已存在其他活动，请重设活动时间");

        FullAmountCycleMinus fullAmountStepMinus = new FullAmountCycleMinus();
        BeanUtils.copyProperties(fullAcmUpdateDTO, fullAmountStepMinus);
        fullAmountStepMinus.setUpdateVendorId(vendor.getVendorId());
        fullAmountCycleMinusModel.updateFullAmountCycleMinus(fullAmountStepMinus);
        return SldResponse.success("编辑成功");
    }

    @ApiOperation("删除循环满减")
    @VendorLogger(option = "编辑循环满减")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullId", value = "循环满减id", required = true)
    })
    @PostMapping("del")
    public JsonResult delFullAcm(HttpServletRequest request, Integer fullId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(fullId, "循环满减id不能为空");
        FullAmountCycleMinus fullAmountCycleMinusByFullId = fullAmountCycleMinusModel.getFullAmountCycleMinusByFullId(fullId);
        AssertUtil.notNull(fullAmountCycleMinusByFullId, "循环满减id:"+fullId+"详情为空");
        AssertUtil.isTrue(!fullAmountCycleMinusByFullId.getStoreId().equals(vendor.getStoreId()), "无权限");

        fullAmountCycleMinusModel.deleteFullAmountCycleMinus(fullId);
        return SldResponse.success("删除成功");
    }

    @ApiOperation("发布循环满减")
    @VendorLogger(option = "发布循环满减")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullId", value = "循环满减id", required = true)
    })
    @PostMapping("publish")
    public JsonResult publishFullAcm(HttpServletRequest request, Integer fullId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(fullId, "循环满减id不能为空");
        FullAmountCycleMinus fullAmountCycleMinusByFullId = fullAmountCycleMinusModel.getFullAmountCycleMinusByFullId(fullId);
        AssertUtil.notNull(fullAmountCycleMinusByFullId, "循环满减id:"+fullId+"详情为空");
        AssertUtil.isTrue(!fullAmountCycleMinusByFullId.getStoreId().equals(vendor.getStoreId()), "无权限");

        FullAmountCycleMinus fullAmountCycleMinus = new FullAmountCycleMinus();
        fullAmountCycleMinus.setFullId(fullId);
        fullAmountCycleMinus.setState(PromotionConst.STATE_RELEASE_2);
        fullAmountCycleMinusModel.updateFullAmountCycleMinus(fullAmountCycleMinus);
        return SldResponse.success("发布成功");
    }

    @ApiOperation("失效循环满减")
    @VendorLogger(option = "失效循环满减")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullId", value = "循环满减id", required = true)
    })
    @PostMapping("invalid")
    public JsonResult invalidFullAcm(HttpServletRequest request, Integer fullId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(fullId, "循环满减id不能为空");
        FullAmountCycleMinus fullAmountCycleMinusByFullId = fullAmountCycleMinusModel.getFullAmountCycleMinusByFullId(fullId);
        AssertUtil.notNull(fullAmountCycleMinusByFullId, "循环满减id:"+fullId+"详情为空");
        AssertUtil.isTrue(!fullAmountCycleMinusByFullId.getStoreId().equals(vendor.getStoreId()), "无权限");

        FullAmountCycleMinus fullAmountCycleMinus = new FullAmountCycleMinus();
        fullAmountCycleMinus.setFullId(fullId);
        fullAmountCycleMinus.setState(PromotionConst.STATE_EXPIRED_5);
        fullAmountCycleMinusModel.updateFullAmountCycleMinus(fullAmountCycleMinus);
        return SldResponse.success("失效成功");
    }

    @ApiOperation("复制循环满减")
    @VendorLogger(option = "复制循环满减")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullId", value = "循环满减id", required = true)
    })
    @PostMapping("copy")
    public JsonResult copyFullAcm(HttpServletRequest request, Integer fullId) {
        AssertUtil.notNullOrZero(fullId, "请选择要复制的数据!");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        FullAmountCycleMinus fullAmountCycleMinus = new FullAmountCycleMinus();
        fullAmountCycleMinus.setFullId(fullId);
        fullAmountCycleMinus.setStoreId(vendor.getStoreId());
        fullAmountCycleMinus.setStoreName(vendor.getStore().getStoreName());
        fullAmountCycleMinus.setCreateVendorId(vendor.getVendorId());
        fullAmountCycleMinusModel.copyFullAcm(fullAmountCycleMinus);
        return SldResponse.success("复制成功");
    }
}
