package com.slodon.b2b2c.model.promotion.base;


import com.slodon.b2b2c.business.dto.OrderConfirmDTO;
import com.slodon.b2b2c.business.dto.OrderSubmitDTO;
import com.slodon.b2b2c.business.dto.PreOrderDTO;
import com.slodon.b2b2c.business.pojo.Cart;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.model.goods.GoodsModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 活动model基类
 * 用于计算优惠金额、提交订单等
 *
 * @see PromotionType#getPromotionBaseModel(Integer)
 * 获取一个 PromotionBaseModel
 * 调用 orderSubmitCalculationDiscount() 方法
 */
@Component
@Slf4j
public abstract class PromotionBaseModel implements ApplicationContextAware {

    protected static ApplicationContext applicationContext;

    /**
     * 定义一个标识，供各个活动内部判断使用
     * 各个活动可自行定义
     * 比如优惠券区分店铺优惠券与平台优惠券
     */
    protected Integer mark = 0;

    /**
     * 活动名称
     *
     * @return
     */
    public abstract String promotionName();

    /**
     * 活动描述
     *
     * @return
     */
    public List<String> promotionDescription(Integer promotionId) {
        return new ArrayList<>();
    }

    /**
     * 活动价格
     *
     * @param promotionId
     * @param productId
     * @return
     */
    public BigDecimal promotionPrice(Integer promotionId, Long productId) {
        return BigDecimal.ZERO;
    }

    /**
     * 是否有扩展方法
     *
     * @return
     */
    public abstract Boolean specialOrder();

    /**
     * 提交订单扩展方法
     *
     * @return paySn
     */
    public Integer submitOrderExtend(String orderSn) {
        if (specialOrder()) {
            log.error(promotionName() + "提交订单扩展方法未编写");
        }
        return 0;
    }

    /**
     * 活动订单支付成功特殊处理
     *
     * @param orderSn 订单号
     */
    public void orderPaySuccess(String orderSn, String tradeSn, String paySn, String paymentName, String paymentCode) {

    }

    /**
     * 确认订单计算活动优惠
     *
     * @param promotionCartGroup 按照活动类型-活动id分组的购物车列表
     */
    public abstract void orderConfirmCalculationDiscount(OrderConfirmDTO.StoreCartGroup.PromotionCartGroup promotionCartGroup);

    /**
     * 订单提交预处理
     *
     * @param promotionId 活动id
     * @param productId   货品id
     * @param buyNum      购买数量
     * @return
     */
    public PreOrderDTO preOrderSubmit(Integer promotionId, Long productId, Integer buyNum, Integer memberId) {
        return null;
    }

    /**
     * 失效活动
     *
     * @return
     */
    public Integer invalidPromotion() {
        return 0;
    }

    /**
     * 提交订单计算活动优惠
     *
     * @param dto 拆单后的订单提交信息
     */
    public abstract void orderSubmitCalculationDiscount(OrderSubmitDTO dto);

    /**
     * 校验活动是否可用
     *
     * @param promotionId 活动id
     * @return
     */
    public abstract Boolean isPromotionAvailable(String promotionId);

    /**
     * 计算单条购物车优惠价格
     * 平台满减和店铺满减类活动需要重写此方法，用于添加购物车默认最优活动
     *
     * @return
     */
    public BigDecimal calculationCartDiscount(Cart cart) {
        return BigDecimal.ZERO;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        PromotionBaseModel.applicationContext = applicationContext;
    }

    public Integer getMark() {
        return mark;
    }

    public PromotionBaseModel setMark(Integer mark) {
        this.mark = mark;
        return this;
    }

//region 分摊金额公共方法

    /**
     * 确认订单分摊优惠金额
     *
     * @param totalDiscount 总优惠金额
     * @param totalAmount 用于拆分计算的分母（优惠后金额）
     * @param cartList 要分配的货品列表
     */
    protected void orderConfirmAssignDiscount(BigDecimal totalDiscount, BigDecimal totalAmount,List<Cart> cartList){
        BigDecimal totalAssign = new BigDecimal("0.00");//已分配金额
        for (int i = 0; i < cartList.size(); i++) {
            Cart cart = cartList.get(i);
            BigDecimal moneyAmount = cart.getProductPrice().multiply(BigDecimal.valueOf(cart.getBuyNum()));//当前货品剩余需支付金额（此次最多可优惠金额）

            BigDecimal currentAssign;//当前货品此次分摊的优惠
            if (i == cartList.size() - 1){
                //最后一个，分摊剩余优惠
                currentAssign = totalDiscount.subtract(totalAssign).min(moneyAmount);
            }else {
                //不是最后一个，按比例分配
                currentAssign = moneyAmount.multiply(totalDiscount).divide(totalAmount,2, RoundingMode.HALF_UP).min(moneyAmount);
                //已分配金额累加
                totalAssign = totalAssign.add(currentAssign);
            }
            cart.setOffPrice(currentAssign);
        }
    }

    /**
     * 提交订单分摊优惠金额
     *
     * @param totalDiscount 总优惠金额
     * @param totalAmount 用于拆分计算的分母（优惠后金额）
     * @param cartList 要分配的货品列表
     * @param promotionType 活动类型
     * @param promotionId 活动id
     * @param promotionName 活动名称
     * @param isStore 是否店铺
     * @param sendIntegral 赠送积分数
     * @param sendCouponIds 赠送优惠券ids
     * @param sendGoodsIds 赠送商品ids
     * @param goodsModel 商品model
     */
    protected void orderSubmitAssignDiscount(BigDecimal totalDiscount, BigDecimal totalAmount, List<OrderSubmitDTO.OrderInfo.OrderProductInfo> cartList,
                                             Integer promotionType, String promotionId, String promotionName,Boolean isStore, Integer sendIntegral,
                                             String sendCouponIds, String sendGoodsIds, GoodsModel goodsModel){
        BigDecimal totalAssign = new BigDecimal("0.00");//已分配金额
        for (int i = 0; i < cartList.size(); i++) {
            OrderSubmitDTO.OrderInfo.OrderProductInfo orderProductInfo = cartList.get(i);
            BigDecimal moneyAmount = orderProductInfo.getMoneyAmount();//当前货品剩余需支付金额（此次最多可优惠金额）

            BigDecimal currentAssign;//当前货品此次分摊的优惠
            if (i == cartList.size() - 1){
                //最后一个，分摊剩余优惠
                currentAssign = totalDiscount.subtract(totalAssign).min(moneyAmount);
            }else {
                //不是最后一个，按比例分配
                currentAssign = moneyAmount.multiply(totalDiscount).divide(totalAmount,2, RoundingMode.HALF_UP).min(moneyAmount);
                //已分配金额累加
                totalAssign = totalAssign.add(currentAssign);
            }

            //构造活动信息
            OrderSubmitDTO.PromotionInfo promotionInfo = new OrderSubmitDTO.PromotionInfo();
            promotionInfo.setPromotionType(promotionType);
            promotionInfo.setPromotionId(promotionId);
            promotionInfo.setPromotionName(promotionName);
            promotionInfo.setDiscount(currentAssign);
            promotionInfo.setIsStore(isStore);
            //赠送积分
            promotionInfo.setSendIntegral(sendIntegral);
            if (!StringUtils.isEmpty(sendCouponIds)) {
                //赠送优惠券
                for (String couponId : sendCouponIds.split(",")) {
                    OrderSubmitDTO.PromotionInfo.SendCoupon sendCoupon = new OrderSubmitDTO.PromotionInfo.SendCoupon();
                    sendCoupon.setCouponId(Integer.valueOf(couponId));
                    sendCoupon.setNum(1);
                    promotionInfo.getSendCouponList().add(sendCoupon);
                }
            }
            if (!StringUtils.isEmpty(sendGoodsIds)){
                //赠品
                for (String goodsId : sendGoodsIds.split(",")) {
                    OrderSubmitDTO.PromotionInfo.SendProduct sendProduct = new OrderSubmitDTO.PromotionInfo.SendProduct();
                    Goods goods = goodsModel.getGoodsByGoodsId(Long.valueOf(goodsId));
                    sendProduct.setProductId(goods.getDefaultProductId());
                    sendProduct.setGoodsName(goods.getGoodsName());
                    sendProduct.setNum(1);
                    promotionInfo.getSendProductList().add(sendProduct);
                }
            }
            orderProductInfo.getPromotionInfoList().add(promotionInfo);
        }
    }

    //endregion
}
