package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.business.dto.OrderConfirmDTO;
import com.slodon.b2b2c.business.dto.OrderSubmitDTO;
import com.slodon.b2b2c.business.dto.PreOrderDTO;
import com.slodon.b2b2c.business.pojo.Cart;
import com.slodon.b2b2c.core.constant.CouponConst;
import com.slodon.b2b2c.core.constant.PromotionConst;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.model.promotion.base.PromotionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Component
@Slf4j
public class PromotionCommonModel {

    /**
     * 订单确认（购物车详情页）计算活动优惠FullAmountCycleMinusModel
     *
     * @param dto
     */
    public OrderConfirmDTO orderConfirmCalculationDiscount(OrderConfirmDTO dto) {
        dto.getStoreCartGroupList().forEach(storeCartGroup -> {
            CouponModel couponModel = (CouponModel) PromotionType.getPromotionBaseModel(PromotionConst.PROMOTION_TYPE_402)
                    .setMark(CouponConst.STORE_COUPON);
            //todo 查询店铺是否有可领取的优惠券
            Boolean hasAbleCoupon = couponModel.isHasAbleCoupon(storeCartGroup.getStoreId());
            storeCartGroup.setHasCoupon(hasAbleCoupon);

            storeCartGroup.getPromotionCartGroupList().forEach(promotionCartGroup -> {
                //每种活动分组单独计算活动优惠
                if (!StringUtil.isNullOrZero(promotionCartGroup.getPromotionType())) {
                    PromotionType.getPromotionBaseModel(promotionCartGroup.getPromotionType()).orderConfirmCalculationDiscount(promotionCartGroup);
                }
            });
        });
        return dto;
    }

    /**
     * 提交订单时计算活动优惠
     * 计算顺序：（计算之前先判断选中的活动是否过期，过期则提示修改购物车）
     * 1.计算单条购物车选中的活动优惠，如满减等
     * 2.根据上一步算出的金额，查询可用的店铺优惠券列表，并默认选中最优惠的优惠券（如果已有选中的优惠券则不执行默认操作）
     * 3.计算店铺优惠券
     * 4.根据上一步算出的金额，查询可用的平台优惠券列表，并默认选中最优惠的优惠券（如果已有选中的优惠券则不执行默认操作）
     * 5.计算平台优惠券
     * 6.计算积分
     *
     * @param dto
     * @param source 1==去结算时计算优惠，计算上述所有步骤;2==提交订单页计算优惠，步骤2、4不默认优惠券;3==后台提交订单结算优惠，不执行2、4步骤
     */
    public OrderSubmitDTO orderSubmitCalculationDiscount(OrderSubmitDTO dto, Integer source) {
        //1.计算单条购物车选中的活动优惠，如满减等
        dto.getPromotionType().forEach(type -> {
            PromotionType.getPromotionBaseModel(type).orderSubmitCalculationDiscount(dto);
        });

        //2.根据上一步算出的金额，查询可用的店铺优惠券列表，并默认选中最优惠的优惠券（如果已有选中的优惠券则不执行默认操作）
        if (source == 1 || source == 2) {
            CouponModel couponModel = (CouponModel) PromotionType.getPromotionBaseModel(PromotionConst.PROMOTION_TYPE_402)
                    .setMark(CouponConst.STORE_COUPON);
            couponModel.getStoreCouponList(dto, source);
        }

        //3.计算店铺优惠券
        PromotionType.getPromotionBaseModel(PromotionConst.PROMOTION_TYPE_402)
                .setMark(CouponConst.STORE_COUPON)
                .orderSubmitCalculationDiscount(dto);

        //4.根据上一步算出的金额，查询可用的平台优惠券列表，并默认选中最优惠的优惠券（如果已有选中的优惠券则不执行默认操作）
        if (source == 1 || source == 2) {
            CouponModel couponModel = (CouponModel) PromotionType.getPromotionBaseModel(PromotionConst.PROMOTION_TYPE_402)
                    .setMark(CouponConst.PLATFORM_COUPON);
            couponModel.getPlatformCouponList(dto, source);
        }

        //5.计算平台优惠券
        PromotionType.getPromotionBaseModel(PromotionConst.PROMOTION_TYPE_402)
                .setMark(CouponConst.PLATFORM_COUPON)
                .orderSubmitCalculationDiscount(dto);

        //4.计算积分
        PromotionType.getPromotionBaseModel(PromotionConst.PROMOTION_TYPE_401).orderSubmitCalculationDiscount(dto);
        return dto;
    }

    /**
     * 订单提交预处理
     *
     * @param promotionType 活动类型
     * @param promotionId   活动id
     * @param productId     货品id
     * @param buyNum        购买数量
     * @return
     */
    public PreOrderDTO preOrderSubmit(@RequestParam("promotionType") Integer promotionType,
                                      @RequestParam("promotionId") Integer promotionId,
                                      @RequestParam("productId") Long productId,
                                      @RequestParam("buyNum") Integer buyNum,
                                      @RequestParam("memberId") Integer memberId) {
        return PromotionType.getPromotionBaseModel(promotionType).preOrderSubmit(promotionId, productId, buyNum, memberId);
    }

    /**
     * 失效活动
     *
     * @param promotionType 活动类型
     * @return
     */
    public Integer invalidPromotion(@RequestParam("promotionType") Integer promotionType) {
        return PromotionType.getPromotionBaseModel(promotionType).invalidPromotion();
    }

    /**
     * 查看某类活动是否有扩展方法
     *
     * @param promotionType 活动类型{@link PromotionConst}
     * @return
     */
    public Boolean specialOrder(Integer promotionType) {
        return PromotionType.getPromotionBaseModel(promotionType).specialOrder();
    }

    /**
     * 活动提交订单
     *
     * @param orderSn 订单号
     */
    public Integer submitPromotionOrder(Integer promotionType, String orderSn) {
        return PromotionType.getPromotionBaseModel(promotionType).submitOrderExtend(orderSn);
    }

    /**
     * 活动订单支付成功特殊处理
     *
     * @param orderSn       订单号
     * @param promotionType 活动类型
     */
    public void orderPaySuccess(@RequestParam("orderSn") String orderSn, @RequestParam("promotionType") Integer promotionType,
                                @RequestParam("paySn") String paySn, @RequestParam(value = "tradeSn", required = false) String tradeSn,
                                @RequestParam("paymentName") String paymentName, @RequestParam("paymentCode") String paymentCode) {
        PromotionType.getPromotionBaseModel(promotionType).orderPaySuccess(orderSn, tradeSn, paySn, paymentName, paymentCode);
    }

    /**
     * 计算单条购物车优惠价格
     * 用于添加购物车默认最优活动
     *
     * @param cart
     * @return
     */
    public BigDecimal calculationCartDiscount(Cart cart) {
        if (StringUtil.isNullOrZero(cart.getPromotionType())) {
            return new BigDecimal("0.00");
        }
        return PromotionType.getPromotionBaseModel(cart.getPromotionType()).calculationCartDiscount(cart);
    }

    /**
     * 校验活动是否可用
     *
     * @param promotionType 活动类型
     * @param promotionId   活动id
     * @return
     */
    public Boolean isPromotionAvailable(Integer promotionType, String promotionId) {
        return PromotionType.getPromotionBaseModel(promotionType).isPromotionAvailable(promotionId);
    }

    /**
     * 根据活动类型获取活动名称
     *
     * @param promotionType 活动类型
     * @return
     */
    public String getPromotionName(Integer promotionType) {
        return PromotionType.getPromotionBaseModel(promotionType).promotionName();
    }

    /**
     * 根据活动类型获取活动描述
     *
     * @param promotionType 活动类型
     * @param promotionId   活动id
     * @return
     */
    public List<String> getPromotionDescription(Integer promotionType, Integer promotionId) {
        return PromotionType.getPromotionBaseModel(promotionType).promotionDescription(promotionId);
    }

    /**
     * 根据活动类型获取活动价格
     *
     * @param promotionType 活动类型
     * @param promotionId   活动id
     * @param productId     货品id
     * @return
     */
    public BigDecimal getPromotionPrice(@RequestParam("promotionType") Integer promotionType, @RequestParam("promotionId") Integer promotionId,
                                        @RequestParam("productId") Long productId) {
        return PromotionType.getPromotionBaseModel(promotionType).promotionPrice(promotionId, productId);
    }
}
