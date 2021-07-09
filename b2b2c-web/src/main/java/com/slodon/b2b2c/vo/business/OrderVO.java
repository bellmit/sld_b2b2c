package com.slodon.b2b2c.vo.business;

import com.alibaba.fastjson.JSONObject;
import com.slodon.b2b2c.business.dto.OrderSubmitDTO;
import com.slodon.b2b2c.business.pojo.Order;
import com.slodon.b2b2c.business.pojo.OrderExtend;
import com.slodon.b2b2c.business.pojo.OrderLog;
import com.slodon.b2b2c.business.pojo.OrderProduct;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.constant.PayConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.CommonUtil;
import com.slodon.b2b2c.member.pojo.MemberInvoice;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.slodon.b2b2c.vo.business.OrderListVO.getRealOrderStateValue;

/**
 * @author lxk
 */
@Data
public class OrderVO {

    @ApiModelProperty("订单id")
    private Integer orderId;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("商家ID")
    private Long storeId;

    @ApiModelProperty("商家名称")
    private String storeName;

    @ApiModelProperty("订单状态：0-已取消；10-未付款订单；20-已付款；30-已发货；40-已完成;50-已关闭")
    private Integer orderState;

    @ApiModelProperty("订单状态值：0-已取消；10-未付款订单；20-已付款；30-已发货；40-已完成;50-已关闭")
    private String orderStateValue;

    @ApiModelProperty("支付方式名称，参考OrderPaymentConst类")
    private String paymentName;

    @ApiModelProperty("支付方式code, 参考OrderPaymentConst类")
    private String paymentCode;

    @ApiModelProperty("会员名称")
    private String memberName;

    @ApiModelProperty("收货人")
    private String receiverName;

    @ApiModelProperty("省市区组合")
    private String receiverAreaInfo;

    @ApiModelProperty("收货地址")
    private String receiverAddress;

    @ApiModelProperty("收货人手机号")
    private String receiverMobile;

    @ApiModelProperty("订单创建时间")
    private Date createTime;

    @ApiModelProperty("订单完成时间")
    private Date finishTime;

    @ApiModelProperty("锁定状态:0是正常,大于0是锁定,默认是0")
    private Integer lockState;

    @ApiModelProperty("商品总金额")
    private BigDecimal goodsAmount;

    @ApiModelProperty("订单总金额")
    private BigDecimal orderAmount;

    @ApiModelProperty("物流费用")
    private BigDecimal expressFee;

    @ApiModelProperty("三方支付金额")
    private BigDecimal payAmount;

    @ApiModelProperty("余额账户支付总金额")
    private BigDecimal balanceAmount;

    @ApiModelProperty("积分抵扣金额")
    private BigDecimal integralCashAmount;

    @ApiModelProperty("活动优惠总金额 （= 店铺优惠券 + 平台优惠券 + 活动优惠【店铺活动 + 平台活动】 + 积分抵扣金额）")
    private BigDecimal activityDiscountAmount;

    @ApiModelProperty("取消原因")
    private String refuseReason;

    @ApiModelProperty("取消备注")
    private String refuseRemark;

    @ApiModelProperty("会员邮箱")
    private String memberEmail;

    @ApiModelProperty("订单备注")
    private String orderRemark;

    @ApiModelProperty("优惠券面额")
    private BigDecimal voucherPrice;

    @ApiModelProperty("发票信息")
    private MemberInvoice invoiceInfo;

    @ApiModelProperty("发票状态0-未开、1-已开")
    private Integer invoiceStatus;

    @ApiModelProperty("发票状态值0-未开、1-已开")
    private String invoiceStatusValue;

    @ApiModelProperty("发货类型：0-物流发货，1-无需物流")
    private Integer deliverType;

    @ApiModelProperty("发货人")
    private String deliverName;

    @ApiModelProperty("发货人电话")
    private String deliverMobile;

    @ApiModelProperty("订单商品信息")
    private List<OrderProduct> orderProductList;

    @ApiModelProperty("订单日志信息")
    private List<OrderLog> orderLogs;

    @ApiModelProperty("促销信息")
    private List<OrderSubmitDTO.PromotionInfo> promotionInfo;

    public OrderVO(Order order, OrderExtend orderExtend) {
        orderId = order.getOrderId();
        orderSn = order.getOrderSn();
        storeId = order.getStoreId();
        storeName = order.getStoreName();
        orderState = order.getOrderState();
        orderStateValue = getRealOrderStateValue(orderState);
        paymentName = order.getPaymentCode().toUpperCase().contains(PayConst.METHOD_ALIPAY.toUpperCase()) ? "支付宝支付"
                : order.getPaymentCode().toUpperCase().contains(PayConst.METHOD_WX.toUpperCase()) ? "微信支付" : "在线支付";
        paymentCode = order.getPaymentCode();
        memberName = order.getMemberName();
        receiverName = order.getReceiverName();
        receiverAreaInfo = order.getReceiverAreaInfo();
        receiverAddress = order.getReceiverAddress();
        receiverMobile = CommonUtil.dealMobile(order.getReceiverMobile());
        createTime = order.getCreateTime();
        finishTime = order.getFinishTime();
        lockState = order.getLockState();
        goodsAmount = order.getGoodsAmount();
        orderAmount = order.getOrderAmount();
        payAmount = order.getPayAmount();
        expressFee = order.getExpressFee();
        balanceAmount = order.getBalanceAmount();
        integralCashAmount = order.getIntegralCashAmount();
        activityDiscountAmount = order.getActivityDiscountAmount();
        refuseReason = order.getRefuseReason();
        refuseRemark = order.getRefuseRemark();
        orderRemark = orderExtend.getOrderRemark();
        voucherPrice = orderExtend.getVoucherPrice();
        invoiceInfo = StringUtils.isEmpty(orderExtend.getInvoiceInfo()) ? null : JSONObject.parseObject(orderExtend.getInvoiceInfo(), MemberInvoice.class);
        invoiceStatus = orderExtend.getInvoiceStatus();
        invoiceStatusValue = dealRealInvoiceStatusValue(invoiceStatus);
        deliverType = orderExtend.getDeliverType();
        deliverName = orderExtend.getDeliverName();
        deliverMobile = CommonUtil.dealMobile(orderExtend.getDeliverMobile());
        if (StringUtils.isEmpty(orderExtend.getPromotionInfo())) {
            promotionInfo = null;
        } else {
            promotionInfo = JSONObject.parseArray(orderExtend.getPromotionInfo(), OrderSubmitDTO.PromotionInfo.class);
        }
    }

    public static String dealRealInvoiceStatusValue(Integer invoiceStatus) {
        String value = null;
        if (StringUtils.isEmpty(invoiceStatus)) return Language.translate("未知");
        switch (invoiceStatus) {
            case OrderConst.INVOICE_STATE_0:
                value = "未开发票";
                break;
            case OrderConst.INVOICE_STATE_1:
                value = "已开发票";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }

}
