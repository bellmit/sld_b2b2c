package com.slodon.b2b2c.vo.business;

import com.slodon.b2b2c.business.pojo.Order;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.constant.PayConst;
import com.slodon.b2b2c.core.i18n.Language;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author lxk
 */
@Data
public class OrderListVO {

    @ApiModelProperty("订单id")
    private Integer orderId;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("商家名称")
    private String storeName;

    @ApiModelProperty("订单创建时间")
    private Date createTime;

    @ApiModelProperty("买家name")
    private String memberName;

    @ApiModelProperty("订单总金额(用户需要支付的金额)，等于商品总金额＋运费-活动优惠金额总额activity_discount_amount")
    private BigDecimal orderAmount;

    @ApiModelProperty("是否免运费，1-免运费，0-有运费")
    private Integer isFreeShipping;

    @ApiModelProperty("物流费用")
    private BigDecimal expressFee;

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

    @ApiModelProperty("收货人详细地址")
    private String receiverAddress;

    @ApiModelProperty("收货人手机号")
    private String receiverMobile;

    @ApiModelProperty("锁定状态：0-是正常, 大于0是锁定状态，用户申请退款或退货时锁定状态加1，处理完毕减1。锁定后不能操作订单")
    private Integer lockState;

    @ApiModelProperty("订单类型：1-普通订单；其他直接存活动类型（具体类型查看ActivityConst）")
    private Integer orderType;

    @ApiModelProperty("订单类型：1-普通订单；其他直接存活动类型（具体类型查看ActivityConst）")
    private String orderTypeValue;

    @ApiModelProperty("订单子状态：101-待付定金；102-待付尾款；103-已付全款")
    private Integer orderSubState;

    @ApiModelProperty("是否显示发货按钮")
    private Boolean isShowDeliverButton;

    @ApiModelProperty("订单货品列表")
    private List<OrderProductListVO> orderProductListVOList;

    public OrderListVO(Order order) {
        orderId = order.getOrderId();
        orderSn = order.getOrderSn();
        storeName = order.getStoreName();
        createTime = order.getCreateTime();
        memberName = order.getMemberName();
        orderAmount = order.getOrderAmount();
        isFreeShipping = order.getExpressFee().compareTo(BigDecimal.ZERO) <= 0 ? 1 : 0;
        expressFee = order.getExpressFee();
        paymentName = order.getPaymentCode().toUpperCase().contains(PayConst.METHOD_ALIPAY.toUpperCase()) ? "支付宝支付"
                : order.getPaymentCode().toUpperCase().contains(PayConst.METHOD_WX.toUpperCase()) ? "微信支付" : "在线支付";
        paymentCode = order.getPaymentCode();
        orderState = order.getOrderState();
        orderStateValue = getRealOrderStateValue(orderState);
        receiverName = order.getReceiverName();
        receiverAddress = order.getReceiverAreaInfo() + order.getReceiverAddress();
        receiverMobile = order.getReceiverMobile();
        lockState = order.getLockState();
        orderType = order.getOrderType();
        isShowDeliverButton = order.getOrderState() == OrderConst.ORDER_STATE_20;
    }

    public static String getRealOrderStateValue(Integer orderState) {
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
                value = "已完成";
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
