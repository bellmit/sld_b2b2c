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

@Api(tags = "seller-????????????")
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

    @ApiOperation("??????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullName", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "??????????????????", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "??????????????????", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "????????????( 1-????????????2-????????????3-????????????4-????????????5-?????????)", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "????????????", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "??????????????????", defaultValue = "1", paramType = "query")
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

    @ApiOperation("??????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullId", value = "????????????id", required = true)
    })
    @GetMapping("detail")
    public JsonResult<FullAcmDetailVO> detail(HttpServletRequest request, Integer fullId) {
        AssertUtil.notNullOrZero(fullId, "??????????????????id????????????");
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        FullAmountCycleMinus fullAmountStepMinus = fullAmountCycleMinusModel.getFullAmountCycleMinusByFullId(fullId);
        AssertUtil.notNull(fullAmountStepMinus, "????????????id:"+fullId+"????????????");
        AssertUtil.isTrue(!fullAmountStepMinus.getStoreId().equals(vendor.getStoreId()), "?????????");

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
                AssertUtil.notNull(goods, "????????????????????????");

                giftVOS.add(new FullGiftVO(goods));
            }
            detailVO.setGiftList(giftVOS);
        }
        return SldResponse.success(detailVO);
    }

    @ApiOperation("??????????????????")
    @VendorLogger(option = "??????????????????")
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
        AssertUtil.isTrue(!CollectionUtil.isEmpty(list), "??????????????????????????????????????????????????????");

        FullAmountCycleMinus fullAmountCycleMinus = new FullAmountCycleMinus();
        BeanUtils.copyProperties(fullAcmAddDTO, fullAmountCycleMinus);
        fullAmountCycleMinus.setStoreId(vendor.getStoreId());
        fullAmountCycleMinus.setStoreName(vendor.getStore().getStoreName());
        fullAmountCycleMinus.setCreateVendorId(vendor.getVendorId());
        fullAmountCycleMinusModel.saveFullAmountCycleMinus(fullAmountCycleMinus);
        return SldResponse.success("????????????");
    }

    @ApiOperation("??????????????????")
    @VendorLogger(option = "??????????????????")
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
        AssertUtil.isTrue(!CollectionUtil.isEmpty(list), "??????????????????????????????????????????????????????");

        FullAmountCycleMinus fullAmountStepMinus = new FullAmountCycleMinus();
        BeanUtils.copyProperties(fullAcmUpdateDTO, fullAmountStepMinus);
        fullAmountStepMinus.setUpdateVendorId(vendor.getVendorId());
        fullAmountCycleMinusModel.updateFullAmountCycleMinus(fullAmountStepMinus);
        return SldResponse.success("????????????");
    }

    @ApiOperation("??????????????????")
    @VendorLogger(option = "??????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullId", value = "????????????id", required = true)
    })
    @PostMapping("del")
    public JsonResult delFullAcm(HttpServletRequest request, Integer fullId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(fullId, "????????????id????????????");
        FullAmountCycleMinus fullAmountCycleMinusByFullId = fullAmountCycleMinusModel.getFullAmountCycleMinusByFullId(fullId);
        AssertUtil.notNull(fullAmountCycleMinusByFullId, "????????????id:"+fullId+"????????????");
        AssertUtil.isTrue(!fullAmountCycleMinusByFullId.getStoreId().equals(vendor.getStoreId()), "?????????");

        fullAmountCycleMinusModel.deleteFullAmountCycleMinus(fullId);
        return SldResponse.success("????????????");
    }

    @ApiOperation("??????????????????")
    @VendorLogger(option = "??????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullId", value = "????????????id", required = true)
    })
    @PostMapping("publish")
    public JsonResult publishFullAcm(HttpServletRequest request, Integer fullId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(fullId, "????????????id????????????");
        FullAmountCycleMinus fullAmountCycleMinusByFullId = fullAmountCycleMinusModel.getFullAmountCycleMinusByFullId(fullId);
        AssertUtil.notNull(fullAmountCycleMinusByFullId, "????????????id:"+fullId+"????????????");
        AssertUtil.isTrue(!fullAmountCycleMinusByFullId.getStoreId().equals(vendor.getStoreId()), "?????????");

        FullAmountCycleMinus fullAmountCycleMinus = new FullAmountCycleMinus();
        fullAmountCycleMinus.setFullId(fullId);
        fullAmountCycleMinus.setState(PromotionConst.STATE_RELEASE_2);
        fullAmountCycleMinusModel.updateFullAmountCycleMinus(fullAmountCycleMinus);
        return SldResponse.success("????????????");
    }

    @ApiOperation("??????????????????")
    @VendorLogger(option = "??????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullId", value = "????????????id", required = true)
    })
    @PostMapping("invalid")
    public JsonResult invalidFullAcm(HttpServletRequest request, Integer fullId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(fullId, "????????????id????????????");
        FullAmountCycleMinus fullAmountCycleMinusByFullId = fullAmountCycleMinusModel.getFullAmountCycleMinusByFullId(fullId);
        AssertUtil.notNull(fullAmountCycleMinusByFullId, "????????????id:"+fullId+"????????????");
        AssertUtil.isTrue(!fullAmountCycleMinusByFullId.getStoreId().equals(vendor.getStoreId()), "?????????");

        FullAmountCycleMinus fullAmountCycleMinus = new FullAmountCycleMinus();
        fullAmountCycleMinus.setFullId(fullId);
        fullAmountCycleMinus.setState(PromotionConst.STATE_EXPIRED_5);
        fullAmountCycleMinusModel.updateFullAmountCycleMinus(fullAmountCycleMinus);
        return SldResponse.success("????????????");
    }

    @ApiOperation("??????????????????")
    @VendorLogger(option = "??????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullId", value = "????????????id", required = true)
    })
    @PostMapping("copy")
    public JsonResult copyFullAcm(HttpServletRequest request, Integer fullId) {
        AssertUtil.notNullOrZero(fullId, "???????????????????????????!");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        FullAmountCycleMinus fullAmountCycleMinus = new FullAmountCycleMinus();
        fullAmountCycleMinus.setFullId(fullId);
        fullAmountCycleMinus.setStoreId(vendor.getStoreId());
        fullAmountCycleMinus.setStoreName(vendor.getStore().getStoreName());
        fullAmountCycleMinus.setCreateVendorId(vendor.getVendorId());
        fullAmountCycleMinusModel.copyFullAcm(fullAmountCycleMinus);
        return SldResponse.success("????????????");
    }
}
