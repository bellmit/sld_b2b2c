package com.slodon.b2b2c.vo.integral;

import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.constant.PayConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.CommonUtil;
import com.slodon.b2b2c.integral.pojo.IntegralOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 封装积分订单Vo对象
 */
@Data
public class IntegralOrderVO implements Serializable {

    private static final long serialVersionUID = 1654738110859157058L;
    @ApiModelProperty("订单id")
    private Integer orderId;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("支付单号")
    private String paySn;

    @ApiModelProperty("商家id")
    private Long storeId;

    @ApiModelProperty("商家名称")
    private String storeName;

    @ApiModelProperty("买家ID")
    private Integer memberId;

    @ApiModelProperty("买家name")
    private String memberName;

    @ApiModelProperty("订单创建时间")
    private Date createTime;

    @ApiModelProperty("支付使用积分数量")
    private Integer integral;

    @ApiModelProperty("现金支付金额")
    private BigDecimal cashAmount;

    @ApiModelProperty("支付方式名称，参考OrderPaymentConst类")
    private String paymentName;

    @ApiModelProperty("支付方式code, 参考OrderPaymentConst类")
    private String paymentCode;

    @ApiModelProperty("订单状态：0-已取消；10-未付款订单；20-已付款；30-已发货；40-已完成;50-已关闭")
    private Integer orderState;

    @ApiModelProperty("订单状态值：0-已取消；10-未付款订单；20-已付款；30-已发货；40-已完成;50-已关闭")
    private String orderStateValue;

    @ApiModelProperty("收货人")
    private String receiverName;

    @ApiModelProperty("收货人地址")
    private String receiverAddress;

    @ApiModelProperty("收货人手机号")
    private String receiverMobile;

    @ApiModelProperty("订单货品列表")
    private List<IntegralOrderProductVO> orderProductList;

    public IntegralOrderVO(IntegralOrder integralOrder) {
        orderId = integralOrder.getOrderId();
        orderSn = integralOrder.getOrderSn();
        paySn = integralOrder.getPaySn();
        storeId = integralOrder.getStoreId();
        storeName = integralOrder.getStoreName();
        memberId = integralOrder.getMemberId();
        memberName = integralOrder.getMemberName();
        createTime = integralOrder.getCreateTime();
        integral = integralOrder.getIntegral();
        cashAmount = integralOrder.getOrderAmount();
        paymentName = integralOrder.getIntegral() == 0
                ? (integralOrder.getPaymentCode().toUpperCase().contains(PayConst.METHOD_ALIPAY.toUpperCase())
                ? "支付宝支付" : integralOrder.getPaymentCode().toUpperCase().contains(PayConst.METHOD_WX.toUpperCase()) ? "微信支付" : "在线支付")
                : (integralOrder.getPaymentCode().toUpperCase().contains(PayConst.METHOD_ALIPAY.toUpperCase())
                ? "积分+支付宝支付" : integralOrder.getPaymentCode().toUpperCase().contains(PayConst.METHOD_WX.toUpperCase())
                ? "积分+微信支付" : integralOrder.getOrderAmount().compareTo(BigDecimal.ZERO) > 0 ? "积分+在线支付" : "积分支付");
        paymentCode = integralOrder.getPaymentCode();
        orderState = integralOrder.getOrderState();
        orderStateValue = dealOrderStateValue(orderState);
        receiverName = integralOrder.getReceiverName();
        receiverAddress = integralOrder.getReceiverAreaInfo() + integralOrder.getReceiverAddress();
        receiverMobile = CommonUtil.dealMobile(integralOrder.getReceiverMobile());
    }

    public static String dealOrderStateValue(Integer orderState) {
        String value = null;
        if (StringUtils.isEmpty(orderState)) return Language.translate("未知");
        switch (orderState) {
            case OrderConst.ORDER_STATE_0:
                value = "已取消";
                break;
            case OrderConst.ORDER_STATE_10:
                value = "待付款";
                break;
            case OrderConst.ORDER_STATE_20:
                value = "待发货";
                break;
            case OrderConst.ORDER_STATE_30:
                value = "待收货";
                break;
            case OrderConst.ORDER_STATE_40:
                value = "交易完成";
                break;
            case OrderConst.ORDER_STATE_50:
                value = "已关闭";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }

}
