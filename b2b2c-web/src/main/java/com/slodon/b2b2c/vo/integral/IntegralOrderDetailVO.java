package com.slodon.b2b2c.vo.integral;

import com.alibaba.fastjson.JSONObject;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.constant.PayConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.CommonUtil;
import com.slodon.b2b2c.integral.pojo.IntegralOrder;
import com.slodon.b2b2c.integral.pojo.IntegralOrderLog;
import com.slodon.b2b2c.integral.pojo.IntegralOrderPay;
import com.slodon.b2b2c.member.pojo.MemberInvoice;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 封装积分订单详情Vo对象
 */
@Data
public class IntegralOrderDetailVO implements Serializable {

    private static final long serialVersionUID = -7998089837301320745L;
    @ApiModelProperty("订单id")
    private Integer orderId;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("支付单号")
    private String paySn;

    @ApiModelProperty("支付方式名称，参考OrderPaymentConst类")
    private String paymentName;

    @ApiModelProperty("支付方式code, 参考OrderPaymentConst类")
    private String paymentCode;

    @ApiModelProperty("会员名称")
    private String memberName;

    @ApiModelProperty("收货人")
    private String receiverName;

    @ApiModelProperty("收货地址")
    private String receiverAddress;

    @ApiModelProperty("收货人手机号")
    private String receiverMobile;

    @ApiModelProperty("订单创建时间")
    private Date createTime;

    @ApiModelProperty("订单完成时间")
    private Date finishTime;

    @ApiModelProperty("订单状态：0-已取消；10-未付款订单；20-已付款；30-已发货；40-已完成;50-已关闭")
    private Integer orderState;

    @ApiModelProperty("订单状态值：0-已取消；10-未付款订单；20-已付款；30-已发货；40-已完成;50-已关闭")
    private String orderStateValue;

    @ApiModelProperty("支付使用积分数量")
    private Integer integral;

    @ApiModelProperty("现金支付金额")
    private BigDecimal cashAmount;

    @ApiModelProperty("取消原因")
    private String refuseReason;

    @ApiModelProperty("取消备注")
    private String refuseRemark;

    @ApiModelProperty("订单备注")
    private String orderRemark;

    @ApiModelProperty("发货类型：0-物流发货，1-无需物流")
    private Integer deliverType;

    @ApiModelProperty("发货人")
    private String deliverName;

    @ApiModelProperty("发货人电话")
    private String deliverMobile;

    @ApiModelProperty("物流公司ID")
    private Integer expressId;

    @ApiModelProperty("物流公司")
    private String expressName;

    @ApiModelProperty("快递单号")
    private String expressNumber;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("自动收货时间，=商家发货时间+后台设置的自动收货时间")
    private Date autoReceiveTime;

    @ApiModelProperty("支付状态:：0默认未支付,1已支付")
    private String payState;

    @ApiModelProperty("支付状态值:：0默认未支付,1已支付")
    private String payStateValue;

    @ApiModelProperty("发票信息")
    private MemberInvoice invoiceInfo;

    @ApiModelProperty("订单货品列表")
    private List<IntegralOrderProductVO> orderProductList;

    @ApiModelProperty("日志列表")
    private List<IntegralOrderLog> orderLogList;

    @ApiModelProperty("店铺客服电话")
    private String servicePhone;

    public IntegralOrderDetailVO(IntegralOrder integralOrder, IntegralOrderPay integralOrderPay) {
        orderId = integralOrder.getOrderId();
        orderSn = integralOrder.getOrderSn();
        paySn = integralOrder.getPaySn();
        paymentName = integralOrder.getIntegral() == 0 && integralOrder.getPaymentCode().contains("INTEGRAL") ?
                (paymentName = integralOrder.getPaymentCode().toUpperCase().contains(PayConst.METHOD_ALIPAY.toUpperCase()) ? "支付宝支付" : integralOrder.getPaymentCode().toUpperCase().contains(PayConst.METHOD_WX.toUpperCase()) ? "微信支付" : "在线支付") :
                (paymentName = integralOrder.getPaymentCode().toUpperCase().contains(PayConst.METHOD_ALIPAY.toUpperCase()) ? "积分+支付宝支付" : integralOrder.getPaymentCode().toUpperCase().contains(PayConst.METHOD_WX.toUpperCase()) ? "积分+微信支付" : "积分+在线支付");
        paymentCode = integralOrder.getPaymentCode();
        memberName = integralOrder.getMemberName();
        receiverName = integralOrder.getReceiverName();
        receiverAddress = integralOrder.getReceiverAreaInfo() + integralOrder.getReceiverAddress();
        receiverMobile = CommonUtil.dealMobile(integralOrder.getReceiverMobile());
        createTime = integralOrder.getCreateTime();
        finishTime = integralOrder.getFinishTime();
        orderState = integralOrder.getOrderState();
        orderStateValue = IntegralOrderVO.dealOrderStateValue(integralOrder.getOrderState());
        integral = integralOrder.getIntegral();
        cashAmount = integralOrder.getOrderAmount();
        refuseReason = integralOrder.getRefuseReason();
        refuseRemark = integralOrder.getRefuseRemark();
        orderRemark = integralOrder.getOrderRemark();
        deliverType = integralOrder.getDeliverType();
        deliverName = integralOrder.getDeliverName();
        deliverMobile = CommonUtil.dealMobile(integralOrder.getDeliverMobile());
        expressId = integralOrder.getExpressId();
        expressName = integralOrder.getExpressName();
        expressNumber = integralOrder.getExpressNumber();
        storeId = integralOrder.getStoreId();
        storeName = integralOrder.getStoreName();
        payState = integralOrderPay.getApiPayState();
        payStateValue = dealPayStateValue(integralOrderPay.getApiPayState());
        invoiceInfo = StringUtils.isEmpty(integralOrder.getInvoiceInfo()) ? null : JSONObject.parseObject(integralOrder.getInvoiceInfo(), MemberInvoice.class);
    }

    public static String dealPayStateValue(String payState) {
        String value = null;
        if (StringUtils.isEmpty(payState)) return Language.translate("未知");
        switch (payState) {
            case OrderConst.API_PAY_STATE_0:
                value = "未支付";
                break;
            case OrderConst.API_PAY_STATE_1:
                value = "已支付";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }

}
