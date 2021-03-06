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
import com.slodon.b2b2c.model.promotion.FullNldRuleModel;
import com.slodon.b2b2c.model.promotion.FullNumLadderDiscountModel;
import com.slodon.b2b2c.promotion.dto.FullNldAddDTO;
import com.slodon.b2b2c.promotion.dto.FullNldUpdateDTO;
import com.slodon.b2b2c.promotion.example.FullNldRuleExample;
import com.slodon.b2b2c.promotion.example.FullNumLadderDiscountExample;
import com.slodon.b2b2c.promotion.pojo.Coupon;
import com.slodon.b2b2c.promotion.pojo.FullNldRule;
import com.slodon.b2b2c.promotion.pojo.FullNumLadderDiscount;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.promotion.FullCouponVO;
import com.slodon.b2b2c.vo.promotion.FullDiscountVO;
import com.slodon.b2b2c.vo.promotion.FullGiftVO;
import com.slodon.b2b2c.vo.promotion.FullNldDetailVO;
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
@RequestMapping("v3/promotion/seller/fullNld")
public class SellerFullNldController extends BaseController {

    @Resource
    private FullNumLadderDiscountModel fullNumLadderDiscountModel;
    @Resource
    private FullNldRuleModel fullNldRuleModel;
    @Resource
    private CouponModel couponModel;
    @Resource
    private GoodsModel goodsModel;

    @ApiOperation("????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullName", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "??????????????????", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "??????????????????", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "????????????( 1-????????????2-????????????3-????????????4-?????????  5-?????????)", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "????????????", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "??????????????????", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<FullDiscountVO>> list(HttpServletRequest request, String fullName,
                                                   Date startTime, Date endTime, Integer state) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        FullNumLadderDiscountExample example = new FullNumLadderDiscountExample();
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
        List<FullNumLadderDiscount> list = fullNumLadderDiscountModel.getFullNumLadderDiscountList(example, pager);
        List<FullDiscountVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(discount -> {
                vos.add(new FullDiscountVO(discount));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullId", value = "??????????????????id", required = true)
    })
    @GetMapping("detail")
    public JsonResult<FullNldDetailVO> detail(HttpServletRequest request, Integer fullId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(fullId, "??????????????????id????????????");

        FullNumLadderDiscount fullNumLadderDiscount = fullNumLadderDiscountModel.getFullNumLadderDiscountByFullId(fullId);
        AssertUtil.notNull(fullNumLadderDiscount, "??????????????????id:"+fullId+"????????????");
        AssertUtil.isTrue(!fullNumLadderDiscount.getStoreId().equals(vendor.getStoreId()), "?????????");

        FullNldDetailVO detailVO = new FullNldDetailVO(fullNumLadderDiscount);
        //??????????????????????????????
        FullNldRuleExample example = new FullNldRuleExample();
        example.setFullId(fullId);
        List<FullNldRule> list = fullNldRuleModel.getFullNldRuleList(example, null);
        AssertUtil.notEmpty(list, "??????????????????????????????????????????????????????");

        List<FullNldDetailVO.FullNldRuleVO> ruleList = new ArrayList<>();
        for (FullNldRule rule : list) {
            FullNldDetailVO.FullNldRuleVO ruleVO = new FullNldDetailVO.FullNldRuleVO(rule);
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

    @ApiOperation("????????????????????????")
    @VendorLogger(option = "????????????????????????")
    @PostMapping("add")
    public JsonResult addFullNld(HttpServletRequest request, FullNldAddDTO fullNldAddDTO) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        FullNumLadderDiscountExample example = new FullNumLadderDiscountExample();
        example.setStoreId(vendor.getStoreId());
        example.setStartTime(fullNldAddDTO.getStartTime());
        example.setEndTime(fullNldAddDTO.getEndTime());
        example.setQueryTime("notNull");
        example.setStateNotIn(PromotionConst.STATE_EXPIRED_5 + "," + PromotionConst.STATE_DELETED_6);
        List<FullNumLadderDiscount> list = fullNumLadderDiscountModel.getFullNumLadderDiscountList(example, null);
        AssertUtil.isTrue(!CollectionUtil.isEmpty(list), "??????????????????????????????????????????????????????");

        FullNumLadderDiscount fullNumLadderDiscount = new FullNumLadderDiscount();
        BeanUtils.copyProperties(fullNldAddDTO, fullNumLadderDiscount);
        fullNumLadderDiscount.setStoreId(vendor.getStoreId());
        fullNumLadderDiscount.setStoreName(vendor.getStore().getStoreName());
        fullNumLadderDiscount.setCreateVendorId(vendor.getVendorId());
        fullNumLadderDiscountModel.saveFullNumLadderDiscount(fullNumLadderDiscount, fullNldAddDTO.getRuleJson());
        return SldResponse.success("????????????");
    }

    @ApiOperation("????????????????????????")
    @VendorLogger(option = "????????????????????????")
    @PostMapping("update")
    public JsonResult updateFullNld(HttpServletRequest request, FullNldUpdateDTO fullNldUpdateDTO) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        FullNumLadderDiscountExample example = new FullNumLadderDiscountExample();
        example.setStoreId(vendor.getStoreId());
        example.setStartTimeAfter(fullNldUpdateDTO.getStartTime());
        example.setStartTime(fullNldUpdateDTO.getStartTime());
        example.setEndTime(fullNldUpdateDTO.getEndTime());
        example.setQueryTime("notNull");
        example.setFullIdNotEquals(fullNldUpdateDTO.getFullId());
        example.setStateNotIn(PromotionConst.STATE_EXPIRED_5 + "," + PromotionConst.STATE_DELETED_6);
        List<FullNumLadderDiscount> list = fullNumLadderDiscountModel.getFullNumLadderDiscountList(example, null);
        AssertUtil.isTrue(!CollectionUtil.isEmpty(list), "??????????????????????????????????????????????????????");

        FullNumLadderDiscount fullNumLadderDiscount = new FullNumLadderDiscount();
        BeanUtils.copyProperties(fullNldUpdateDTO, fullNumLadderDiscount);
        fullNumLadderDiscount.setUpdateVendorId(vendor.getVendorId());
        fullNumLadderDiscountModel.updateFullNumLadderDiscount(fullNumLadderDiscount, fullNldUpdateDTO.getRuleJson());
        return SldResponse.success("????????????");
    }

    @ApiOperation("????????????????????????")
    @VendorLogger(option = "????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullId", value = "??????????????????id", required = true)
    })
    @PostMapping("del")
    public JsonResult delFullNld(HttpServletRequest request, Integer fullId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(fullId, "??????????????????id????????????");
        FullNumLadderDiscount fullNumLadderDiscountByFullId = fullNumLadderDiscountModel.getFullNumLadderDiscountByFullId(fullId);
        AssertUtil.notNull(fullNumLadderDiscountByFullId, "???????????????????????????????????????,?????????");
        AssertUtil.isTrue(!fullNumLadderDiscountByFullId.getStoreId().equals(vendor.getStoreId()), "?????????");

        fullNumLadderDiscountModel.deleteFullNumLadderDiscount(fullId);
        return SldResponse.success("????????????");
    }

    @ApiOperation("????????????????????????")
    @VendorLogger(option = "????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullId", value = "??????????????????id", required = true)
    })
    @PostMapping("publish")
    public JsonResult publishFullNld(HttpServletRequest request, Integer fullId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(fullId, "??????????????????id????????????");
        FullNumLadderDiscount fullNumLadderDiscountByFullId = fullNumLadderDiscountModel.getFullNumLadderDiscountByFullId(fullId);
        AssertUtil.notNull(fullNumLadderDiscountByFullId, "??????????????????id:"+fullId+"????????????");
        AssertUtil.isTrue(!fullNumLadderDiscountByFullId.getStoreId().equals(vendor.getStoreId()), "?????????");

        FullNumLadderDiscount fullNumLadderDiscount = new FullNumLadderDiscount();
        fullNumLadderDiscount.setFullId(fullId);
        fullNumLadderDiscount.setState(PromotionConst.STATE_RELEASE_2);
        fullNumLadderDiscountModel.updateFullNumLadderDiscount(fullNumLadderDiscount);
        return SldResponse.success("????????????");
    }

    @ApiOperation("????????????????????????")
    @VendorLogger(option = "????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullId", value = "??????????????????id", required = true)
    })
    @PostMapping("invalid")
    public JsonResult invalidFullNld(HttpServletRequest request, Integer fullId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(fullId, "??????????????????id????????????");
        FullNumLadderDiscount fullNumLadderDiscountByFullId = fullNumLadderDiscountModel.getFullNumLadderDiscountByFullId(fullId);
        AssertUtil.notNull(fullNumLadderDiscountByFullId, "??????????????????id:"+fullId+"????????????");
        AssertUtil.isTrue(!fullNumLadderDiscountByFullId.getStoreId().equals(vendor.getStoreId()), "?????????");

        FullNumLadderDiscount fullNumLadderDiscount = new FullNumLadderDiscount();
        fullNumLadderDiscount.setFullId(fullId);
        fullNumLadderDiscount.setState(PromotionConst.STATE_EXPIRED_5);
        fullNumLadderDiscountModel.updateFullNumLadderDiscount(fullNumLadderDiscount);
        return SldResponse.success("????????????");
    }

    @ApiOperation("????????????????????????")
    @VendorLogger(option = "????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullId", value = "??????????????????id", required = true)
    })
    @PostMapping("copy")
    public JsonResult copyFullNld(HttpServletRequest request, Integer fullId) {
        AssertUtil.notNullOrZero(fullId, "???????????????????????????!");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        FullNumLadderDiscount fullNumLadderDiscount = new FullNumLadderDiscount();
        fullNumLadderDiscount.setFullId(fullId);
        fullNumLadderDiscount.setStoreId(vendor.getStoreId());
        fullNumLadderDiscount.setStoreName(vendor.getStore().getStoreName());
        fullNumLadderDiscount.setCreateVendorId(vendor.getVendorId());
        fullNumLadderDiscountModel.copyFullNld(fullNumLadderDiscount);
        return SldResponse.success("????????????");
    }
}
