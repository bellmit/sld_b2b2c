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
import com.slodon.b2b2c.model.promotion.FullAldRuleModel;
import com.slodon.b2b2c.model.promotion.FullAmountLadderDiscountModel;
import com.slodon.b2b2c.promotion.dto.FullAldAddDTO;
import com.slodon.b2b2c.promotion.dto.FullAldUpdateDTO;
import com.slodon.b2b2c.promotion.example.FullAldRuleExample;
import com.slodon.b2b2c.promotion.example.FullAmountLadderDiscountExample;
import com.slodon.b2b2c.promotion.pojo.Coupon;
import com.slodon.b2b2c.promotion.pojo.FullAldRule;
import com.slodon.b2b2c.promotion.pojo.FullAmountLadderDiscount;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.promotion.FullAldDetailVO;
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

@Api(tags = "seller-???N?????????")
@RestController
@Slf4j
@RequestMapping("v3/promotion/seller/fullAld")
public class SellerFullAldController extends BaseController {

    @Resource
    private FullAmountLadderDiscountModel fullAmountLadderDiscountModel;
    @Resource
    private FullAldRuleModel fullAldRuleModel;
    @Resource
    private CouponModel couponModel;
    @Resource
    private GoodsModel goodsModel;

    @ApiOperation("?????????????????????")
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
        FullAmountLadderDiscountExample example = new FullAmountLadderDiscountExample();
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
        List<FullAmountLadderDiscount> list = fullAmountLadderDiscountModel.getFullAmountLadderDiscountList(example, pager);
        List<FullDiscountVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(discount -> {
                vos.add(new FullDiscountVO(discount));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("?????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullId", value = "???????????????id", required = true)
    })
    @GetMapping("detail")
    public JsonResult<FullAldDetailVO> detail(HttpServletRequest request, Integer fullId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(fullId, "?????????????????????id????????????");

        FullAmountLadderDiscount fullAmountLadderDiscount = fullAmountLadderDiscountModel.getFullAmountLadderDiscountByFullId(fullId);
        AssertUtil.notNull(fullAmountLadderDiscount, "???????????????id:"+fullId+"????????????");
        AssertUtil.isTrue(!fullAmountLadderDiscount.getStoreId().equals(vendor.getStoreId()), "?????????");

        FullAldDetailVO detailVO = new FullAldDetailVO(fullAmountLadderDiscount);
        //???????????????????????????
        FullAldRuleExample example = new FullAldRuleExample();
        example.setFullId(fullId);
        List<FullAldRule> list = fullAldRuleModel.getFullAldRuleList(example, null);
        AssertUtil.notEmpty(list, "???????????????????????????????????????????????????");

        List<FullAldDetailVO.FullAldRuleVO> ruleList = new ArrayList<>();
        for (FullAldRule rule : list) {
            FullAldDetailVO.FullAldRuleVO ruleVO = new FullAldDetailVO.FullAldRuleVO(rule);
            if (!StringUtil.isEmpty(rule.getSendCouponIds())) {
                String sendCouponIds = rule.getSendCouponIds();
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
                ruleVO.setCouponList(couponVOS);
            }
            if (!StringUtil.isEmpty(rule.getSendGoodsIds())) {
                String sendGoodsIds = rule.getSendGoodsIds();
                String[] goodsIds = sendGoodsIds.split(",");
                List<FullGiftVO> giftVOS = new ArrayList<>();
                for (String str : goodsIds) {
                    if (StringUtil.isEmpty(str)) continue;
                    Goods goods = goodsModel.getGoodsByGoodsId(Long.parseLong(str));
                    AssertUtil.notNull(goods, "????????????????????????");

                    giftVOS.add(new FullGiftVO(goods));
                }
                ruleVO.setGiftList(giftVOS);
            }
            ruleList.add(ruleVO);
        }
        detailVO.setRuleList(ruleList);
        return SldResponse.success(detailVO);
    }

    @ApiOperation("?????????????????????")
    @VendorLogger(option = "?????????????????????")
    @PostMapping("add")
    public JsonResult addFullAld(HttpServletRequest request, FullAldAddDTO fullAldAddDTO) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        FullAmountLadderDiscountExample example = new FullAmountLadderDiscountExample();
        example.setStoreId(vendor.getStoreId());
        example.setStartTime(fullAldAddDTO.getStartTime());
        example.setEndTime(fullAldAddDTO.getEndTime());
        example.setQueryTime("notNull");
        example.setStateNotIn(PromotionConst.STATE_EXPIRED_5 + "," + PromotionConst.STATE_DELETED_6);
        List<FullAmountLadderDiscount> list = fullAmountLadderDiscountModel.getFullAmountLadderDiscountList(example, null);
        AssertUtil.isTrue(!CollectionUtil.isEmpty(list), "??????????????????????????????????????????????????????");

        FullAmountLadderDiscount fullAmountLadderDiscount = new FullAmountLadderDiscount();
        BeanUtils.copyProperties(fullAldAddDTO, fullAmountLadderDiscount);
        fullAmountLadderDiscount.setStoreId(vendor.getStoreId());
        fullAmountLadderDiscount.setStoreName(vendor.getStore().getStoreName());
        fullAmountLadderDiscount.setCreateVendorId(vendor.getVendorId());
        fullAmountLadderDiscountModel.saveFullAmountLadderDiscount(fullAmountLadderDiscount, fullAldAddDTO.getRuleJson());
        return SldResponse.success("????????????");
    }

    @ApiOperation("?????????????????????")
    @VendorLogger(option = "?????????????????????")
    @PostMapping("update")
    public JsonResult updateFullAld(HttpServletRequest request, FullAldUpdateDTO fullAldUpdateDTO) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        FullAmountLadderDiscountExample example = new FullAmountLadderDiscountExample();
        example.setStoreId(vendor.getStoreId());
        example.setStartTime(fullAldUpdateDTO.getStartTime());
        example.setEndTime(fullAldUpdateDTO.getEndTime());
        example.setQueryTime("notNull");
        example.setFullIdNotEquals(fullAldUpdateDTO.getFullId());
        example.setStateNotIn(PromotionConst.STATE_EXPIRED_5 + "," + PromotionConst.STATE_DELETED_6);
        List<FullAmountLadderDiscount> list = fullAmountLadderDiscountModel.getFullAmountLadderDiscountList(example, null);
        AssertUtil.isTrue(!CollectionUtil.isEmpty(list), "??????????????????????????????????????????????????????");

        FullAmountLadderDiscount fullAmountLadderDiscount = new FullAmountLadderDiscount();
        BeanUtils.copyProperties(fullAldUpdateDTO, fullAmountLadderDiscount);
        fullAmountLadderDiscount.setUpdateVendorId(vendor.getVendorId());
        fullAmountLadderDiscountModel.updateFullAmountLadderDiscount(fullAmountLadderDiscount, fullAldUpdateDTO.getRuleJson());
        return SldResponse.success("????????????");
    }

    @ApiOperation("?????????????????????")
    @VendorLogger(option = "?????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullId", value = "???????????????id", required = true)
    })
    @PostMapping("del")
    public JsonResult delFullAld(HttpServletRequest request, Integer fullId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(fullId, "???????????????id????????????");
        FullAmountLadderDiscount fullAmountLadderDiscountByFullId = fullAmountLadderDiscountModel.getFullAmountLadderDiscountByFullId(fullId);
        AssertUtil.notNull(fullAmountLadderDiscountByFullId, "???????????????id:"+fullId+"????????????");
        AssertUtil.isTrue(!fullAmountLadderDiscountByFullId.getStoreId().equals(vendor.getStoreId()), "?????????");

        fullAmountLadderDiscountModel.deleteFullAmountLadderDiscount(fullId);
        return SldResponse.success("????????????");
    }

    @ApiOperation("?????????????????????")
    @VendorLogger(option = "?????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullId", value = "???????????????id", required = true)
    })
    @PostMapping("publish")
    public JsonResult publishFullAld(HttpServletRequest request, Integer fullId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(fullId, "???????????????id????????????");
        FullAmountLadderDiscount fullAmountLadderDiscountByFullId = fullAmountLadderDiscountModel.getFullAmountLadderDiscountByFullId(fullId);
        AssertUtil.notNull(fullAmountLadderDiscountByFullId, "???????????????id:"+fullId+"????????????");
        AssertUtil.isTrue(!fullAmountLadderDiscountByFullId.getStoreId().equals(vendor.getStoreId()), "?????????");

        FullAmountLadderDiscount fullAmountLadderDiscount = new FullAmountLadderDiscount();
        fullAmountLadderDiscount.setFullId(fullId);
        fullAmountLadderDiscount.setState(PromotionConst.STATE_RELEASE_2);
        fullAmountLadderDiscountModel.updateFullAmountLadderDiscount(fullAmountLadderDiscount);
        return SldResponse.success("????????????");
    }

    @ApiOperation("?????????????????????")
    @VendorLogger(option = "?????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullId", value = "???????????????id", required = true)
    })
    @PostMapping("invalid")
    public JsonResult invalidFullAld(HttpServletRequest request, Integer fullId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(fullId, "???????????????id????????????");
        FullAmountLadderDiscount fullAmountLadderDiscountByFullId = fullAmountLadderDiscountModel.getFullAmountLadderDiscountByFullId(fullId);
        AssertUtil.notNull(fullAmountLadderDiscountByFullId, "???????????????id:"+fullId+"????????????");
        AssertUtil.isTrue(!fullAmountLadderDiscountByFullId.getStoreId().equals(vendor.getStoreId()), "?????????");

        FullAmountLadderDiscount fullAmountLadderDiscount = new FullAmountLadderDiscount();
        fullAmountLadderDiscount.setFullId(fullId);
        fullAmountLadderDiscount.setState(PromotionConst.STATE_EXPIRED_5);
        fullAmountLadderDiscountModel.updateFullAmountLadderDiscount(fullAmountLadderDiscount);
        return SldResponse.success("????????????");
    }

    @ApiOperation("?????????????????????")
    @VendorLogger(option = "?????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullId", value = "???????????????id", required = true)
    })
    @PostMapping("copy")
    public JsonResult copyFullAld(HttpServletRequest request, Integer fullId) {
        AssertUtil.notNullOrZero(fullId, "???????????????????????????!");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        FullAmountLadderDiscount fullAmountLadderDiscount = new FullAmountLadderDiscount();
        fullAmountLadderDiscount.setFullId(fullId);
        fullAmountLadderDiscount.setStoreId(vendor.getStoreId());
        fullAmountLadderDiscount.setStoreName(vendor.getStore().getStoreName());
        fullAmountLadderDiscount.setCreateVendorId(vendor.getVendorId());
        fullAmountLadderDiscountModel.copyFullAld(fullAmountLadderDiscount);
        return SldResponse.success("????????????");
    }
}
