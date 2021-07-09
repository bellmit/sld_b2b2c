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

@Api(tags = "seller-满N元折扣")
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

    @ApiOperation("阶梯满折扣列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullName", value = "活动名称", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "活动开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "活动开始时间", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "活动状态( 1-已创建，2-已发布，3-进行中，4-已结束，5-已失效)", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
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

    @ApiOperation("阶梯满折扣详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullId", value = "阶梯满折扣id", required = true)
    })
    @GetMapping("detail")
    public JsonResult<FullAldDetailVO> detail(HttpServletRequest request, Integer fullId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(fullId, "阶梯满折扣活动id不能为空");

        FullAmountLadderDiscount fullAmountLadderDiscount = fullAmountLadderDiscountModel.getFullAmountLadderDiscountByFullId(fullId);
        AssertUtil.notNull(fullAmountLadderDiscount, "阶梯满折扣id:"+fullId+"详情为空");
        AssertUtil.isTrue(!fullAmountLadderDiscount.getStoreId().equals(vendor.getStoreId()), "无权限");

        FullAldDetailVO detailVO = new FullAldDetailVO(fullAmountLadderDiscount);
        //查询阶梯满折扣规则
        FullAldRuleExample example = new FullAldRuleExample();
        example.setFullId(fullId);
        List<FullAldRule> list = fullAldRuleModel.getFullAldRuleList(example, null);
        AssertUtil.notEmpty(list, "获取阶梯满折扣规则信息为空，请重试");

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
                    AssertUtil.notNull(goods, "获取商品信息为空");

                    giftVOS.add(new FullGiftVO(goods));
                }
                ruleVO.setGiftList(giftVOS);
            }
            ruleList.add(ruleVO);
        }
        detailVO.setRuleList(ruleList);
        return SldResponse.success(detailVO);
    }

    @ApiOperation("新建阶梯满折扣")
    @VendorLogger(option = "新建阶梯满折扣")
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
        AssertUtil.isTrue(!CollectionUtil.isEmpty(list), "该时段已存在其他活动，请重设活动时间");

        FullAmountLadderDiscount fullAmountLadderDiscount = new FullAmountLadderDiscount();
        BeanUtils.copyProperties(fullAldAddDTO, fullAmountLadderDiscount);
        fullAmountLadderDiscount.setStoreId(vendor.getStoreId());
        fullAmountLadderDiscount.setStoreName(vendor.getStore().getStoreName());
        fullAmountLadderDiscount.setCreateVendorId(vendor.getVendorId());
        fullAmountLadderDiscountModel.saveFullAmountLadderDiscount(fullAmountLadderDiscount, fullAldAddDTO.getRuleJson());
        return SldResponse.success("添加成功");
    }

    @ApiOperation("编辑阶梯满折扣")
    @VendorLogger(option = "编辑阶梯满折扣")
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
        AssertUtil.isTrue(!CollectionUtil.isEmpty(list), "该时段已存在其他活动，请重设活动时间");

        FullAmountLadderDiscount fullAmountLadderDiscount = new FullAmountLadderDiscount();
        BeanUtils.copyProperties(fullAldUpdateDTO, fullAmountLadderDiscount);
        fullAmountLadderDiscount.setUpdateVendorId(vendor.getVendorId());
        fullAmountLadderDiscountModel.updateFullAmountLadderDiscount(fullAmountLadderDiscount, fullAldUpdateDTO.getRuleJson());
        return SldResponse.success("编辑成功");
    }

    @ApiOperation("删除阶梯满折扣")
    @VendorLogger(option = "删除阶梯满折扣")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullId", value = "阶梯满折扣id", required = true)
    })
    @PostMapping("del")
    public JsonResult delFullAld(HttpServletRequest request, Integer fullId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(fullId, "阶梯满折扣id不能为空");
        FullAmountLadderDiscount fullAmountLadderDiscountByFullId = fullAmountLadderDiscountModel.getFullAmountLadderDiscountByFullId(fullId);
        AssertUtil.notNull(fullAmountLadderDiscountByFullId, "阶梯满折扣id:"+fullId+"详情为空");
        AssertUtil.isTrue(!fullAmountLadderDiscountByFullId.getStoreId().equals(vendor.getStoreId()), "无权限");

        fullAmountLadderDiscountModel.deleteFullAmountLadderDiscount(fullId);
        return SldResponse.success("删除成功");
    }

    @ApiOperation("发布阶梯满折扣")
    @VendorLogger(option = "发布阶梯满折扣")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullId", value = "阶梯满折扣id", required = true)
    })
    @PostMapping("publish")
    public JsonResult publishFullAld(HttpServletRequest request, Integer fullId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(fullId, "阶梯满折扣id不能为空");
        FullAmountLadderDiscount fullAmountLadderDiscountByFullId = fullAmountLadderDiscountModel.getFullAmountLadderDiscountByFullId(fullId);
        AssertUtil.notNull(fullAmountLadderDiscountByFullId, "阶梯满折扣id:"+fullId+"详情为空");
        AssertUtil.isTrue(!fullAmountLadderDiscountByFullId.getStoreId().equals(vendor.getStoreId()), "无权限");

        FullAmountLadderDiscount fullAmountLadderDiscount = new FullAmountLadderDiscount();
        fullAmountLadderDiscount.setFullId(fullId);
        fullAmountLadderDiscount.setState(PromotionConst.STATE_RELEASE_2);
        fullAmountLadderDiscountModel.updateFullAmountLadderDiscount(fullAmountLadderDiscount);
        return SldResponse.success("发布成功");
    }

    @ApiOperation("失效阶梯满折扣")
    @VendorLogger(option = "失效阶梯满折扣")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullId", value = "阶梯满折扣id", required = true)
    })
    @PostMapping("invalid")
    public JsonResult invalidFullAld(HttpServletRequest request, Integer fullId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(fullId, "阶梯满折扣id不能为空");
        FullAmountLadderDiscount fullAmountLadderDiscountByFullId = fullAmountLadderDiscountModel.getFullAmountLadderDiscountByFullId(fullId);
        AssertUtil.notNull(fullAmountLadderDiscountByFullId, "阶梯满折扣id:"+fullId+"详情为空");
        AssertUtil.isTrue(!fullAmountLadderDiscountByFullId.getStoreId().equals(vendor.getStoreId()), "无权限");

        FullAmountLadderDiscount fullAmountLadderDiscount = new FullAmountLadderDiscount();
        fullAmountLadderDiscount.setFullId(fullId);
        fullAmountLadderDiscount.setState(PromotionConst.STATE_EXPIRED_5);
        fullAmountLadderDiscountModel.updateFullAmountLadderDiscount(fullAmountLadderDiscount);
        return SldResponse.success("失效成功");
    }

    @ApiOperation("复制阶梯满折扣")
    @VendorLogger(option = "复制阶梯满折扣")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fullId", value = "阶梯满折扣id", required = true)
    })
    @PostMapping("copy")
    public JsonResult copyFullAld(HttpServletRequest request, Integer fullId) {
        AssertUtil.notNullOrZero(fullId, "请选择要复制的数据!");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        FullAmountLadderDiscount fullAmountLadderDiscount = new FullAmountLadderDiscount();
        fullAmountLadderDiscount.setFullId(fullId);
        fullAmountLadderDiscount.setStoreId(vendor.getStoreId());
        fullAmountLadderDiscount.setStoreName(vendor.getStore().getStoreName());
        fullAmountLadderDiscount.setCreateVendorId(vendor.getVendorId());
        fullAmountLadderDiscountModel.copyFullAld(fullAmountLadderDiscount);
        return SldResponse.success("复制成功");
    }
}
