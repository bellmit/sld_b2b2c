package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.business.pojo.Order;
import com.slodon.b2b2c.business.pojo.OrderExtend;
import com.slodon.b2b2c.business.pojo.OrderProduct;
import com.slodon.b2b2c.core.constant.PayConst;
import com.slodon.b2b2c.core.util.CommonUtil;
import com.slodon.b2b2c.promotion.pojo.SpellGoods;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: slodon
 * @Description 封装拼团活动订单详情VO对象
 * @Author wuxy
 * @date 2020.11.06 11:27
 */
@Data
public class SpellOrderDetailVO implements Serializable {

    private static final long serialVersionUID = 9051822889748160626L;
    @ApiModelProperty("订单id")
    private Integer orderId;

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("会员名称")
    private String memberName;

    @ApiModelProperty("收货人姓名")
    private String receiverName;

    @ApiModelProperty("收货人手机号")
    private String receiverMobile;

    @ApiModelProperty("收货地址")
    private String receiverAddress;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("订单状态：0-已取消；10-未付款订单；20-已付款；30-已发货；40-已完成;50-已关闭")
    private Integer orderState;

    @ApiModelProperty("订单状态值：0-已取消；10-未付款订单；20-已付款；30-已发货；40-已完成;50-已关闭")
    private String orderStateValue;

    @ApiModelProperty("下单时间")
    private Date createTime;

    @ApiModelProperty("支付方式(编码)")
    private String paymentCode;

    @ApiModelProperty("支付方式(名称)")
    private String paymentName;

    @ApiModelProperty("订单备注")
    private String remark;

    @ApiModelProperty("发货时间")
    private Date deliverTime;

    @ApiModelProperty("物流公司名称")
    private String expressName;

    @ApiModelProperty("物流单号")
    private String expressNumber;

    @ApiModelProperty("收货时间")
    private Date finishTime;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品规格")
    private String specValues;

    @ApiModelProperty("商品原价")
    private BigDecimal productPrice;

    @ApiModelProperty("拼团价")
    private BigDecimal spellPrice;

    @ApiModelProperty("订单运费")
    private BigDecimal expressFee;

    @ApiModelProperty("订单金额")
    private BigDecimal orderAmount;

    @ApiModelProperty("余额支付")
    private BigDecimal balanceAmount;

    @ApiModelProperty("在线支付")
    private BigDecimal payAmount;

    public SpellOrderDetailVO(Order order, OrderProduct orderProduct, OrderExtend orderExtend, SpellGoods spellGoods) {
        this.orderId = order.getOrderId();
        this.memberName = order.getMemberName();
        this.receiverName = order.getReceiverName();
        this.receiverMobile = CommonUtil.dealMobile(order.getReceiverMobile());
        this.receiverAddress = order.getReceiverAreaInfo() + order.getReceiverAddress();
        this.orderSn = order.getOrderSn();
        this.orderState = order.getOrderState();
        this.orderStateValue = SpellMemberOrderVO.dealOrderStateValue(order.getOrderState());
        this.createTime = order.getCreateTime();
        this.paymentCode = order.getPaymentCode();
        this.paymentName = order.getPaymentCode().toUpperCase().contains(PayConst.METHOD_ALIPAY.toUpperCase()) ? "支付宝支付"
                : order.getPaymentCode().toUpperCase().contains(PayConst.METHOD_WX.toUpperCase()) ? "微信支付" : "在线支付";
        this.remark = orderExtend.getOrderRemark();
        this.deliverTime = orderExtend.getDeliverTime();
        this.expressName = order.getExpressName();
        this.expressNumber = order.getExpressNumber();
        this.finishTime = order.getFinishTime();
        this.goodsName = orderProduct.getGoodsName();
        this.specValues = orderProduct.getSpecValues();
        this.productPrice = spellGoods.getProductPrice();
        this.spellPrice = spellGoods.getSpellPrice();
        this.expressFee = order.getExpressFee();
        this.orderAmount = order.getOrderAmount();
        this.balanceAmount = order.getBalanceAmount();
        this.payAmount = order.getPayAmount();
    }
}
