package com.slodon.b2b2c.vo.business;

import com.slodon.b2b2c.business.pojo.Order;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.constant.PayConst;
import com.slodon.b2b2c.core.constant.PromotionConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.CommonUtil;
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
public class MemberOrderListVO {

    @ApiModelProperty("订单id")
    private Integer orderId;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("支付单号")
    private String paySn;

    @ApiModelProperty("父订单号，无需拆单时，父订单号=订单号")
    private String parentSn;

    @ApiModelProperty("商家id")
    private Long storeId;

    @ApiModelProperty("商家名称")
    private String storeName;

    @ApiModelProperty("店铺logo")
    private String storeLogo;

    @ApiModelProperty("订单创建时间")
    private Date createTime;

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

    @ApiModelProperty("订单子状态：101-待付定金；102-待付尾款；103-已完成付款")
    private Integer orderSubState;

    @ApiModelProperty("是否评价:1.未评价,2.部分评价,3.全部评价")
    private Integer evaluateState;

    @ApiModelProperty("是否评价:1.未评价,2.部分评价,3.全部评价")
    private String evaluateStateValue;

    @ApiModelProperty("收货人")
    private String receiverName;

    @ApiModelProperty("收货人详细地址")
    private String receiverAddress;

    @ApiModelProperty("收货人手机号")
    private String receiverMobile;

    @ApiModelProperty("订单货品列表")
    private List<OrderProductListVO> orderProductListVOList;

    @ApiModelProperty("合计金额")
    private BigDecimal totalMoney;

    @ApiModelProperty("合计商品数")
    private Integer goodsNum;

    @ApiModelProperty("订单类型：1-普通订单；其他直接存活动类型（具体类型查看ActivityConst）")
    private Integer orderType;

    @ApiModelProperty("订单类型：1-普通订单；其他直接存活动类型（具体类型查看ActivityConst）")
    private String orderTypeValue;

    @ApiModelProperty("尾款支付的开始时间")
    private Date remainStartTime;

    @ApiModelProperty("支付定金剩余时间（秒）")
    private long depositRemainTime;

    @ApiModelProperty("尾款支付剩余时间（秒）")
    private long remainEndTime;

    @ApiModelProperty("预售发货时间")
    private String deliverTime;

    @ApiModelProperty("是否退还定金")
    private Boolean isRefundDeposit;

    public MemberOrderListVO(Order order, Integer orderSubState, Date remainTime, boolean isHasDeposit) {
        orderId = order.getOrderId();
        orderSn = order.getOrderSn();
        paySn = order.getPaySn();
        parentSn = order.getParentSn();
        createTime = order.getCreateTime();
        orderAmount = order.getOrderAmount();
        isFreeShipping = order.getExpressFee().compareTo(BigDecimal.ZERO) <= 0 ? 1 : 0;
        expressFee = order.getExpressFee();
        paymentName = order.getPaymentCode().toUpperCase().contains(PayConst.METHOD_ALIPAY.toUpperCase()) ? "支付宝支付"
                : order.getPaymentCode().toUpperCase().contains(PayConst.METHOD_WX.toUpperCase()) ? "微信支付" : "在线支付";
        paymentCode = order.getPaymentCode();
        orderState = order.getOrderState();
        evaluateState = order.getEvaluateState();
        evaluateStateValue = getRealEvaluateStateValue(evaluateState);
        receiverName = order.getReceiverName();
        receiverAddress = order.getReceiverAddress();
        receiverMobile = CommonUtil.dealMobile(order.getReceiverMobile());
        orderType = order.getOrderType();
        orderTypeValue = dealOrderTypeValue(order.getOrderType(), isHasDeposit);
        if (order.getOrderType() == PromotionConst.PROMOTION_TYPE_103
                || order.getOrderType() == PromotionConst.PROMOTION_TYPE_105) {
            if (isHasDeposit) {
                remainStartTime = remainTime;

                long time1 = remainTime.getTime();
                long time2 = new Date().getTime();
                long remainTimeValue = (time1 - time2) / 1000;
                depositRemainTime = remainTimeValue < 0 ? 0 : remainTimeValue;
                orderStateValue = getRealOrderStateValue(order.getOrderState(), orderSubState, depositRemainTime, order.getOrderType());
            } else {
                orderStateValue = getRealOrderStateValue(order.getOrderState(), orderSubState, 0L, order.getOrderType());
            }
        } else {
            orderStateValue = getRealOrderStateValue(order.getOrderState(), orderSubState, 0L, order.getOrderType());
        }
    }

    public static String getRealOrderStateValue(Integer orderState, Integer orderSubState, long depositRemainTime, Integer orderType) {
        String value = null;
        if (StringUtils.isEmpty(orderState)) return Language.translate("未知");
        switch (orderState) {
            case OrderConst.ORDER_STATE_0:
                value = "交易关闭";
                break;
            case OrderConst.ORDER_STATE_10:
                if (orderSubState == OrderConst.ORDER_SUB_STATE_101) {
                    value = "定金等待买家付款";
                } else if (orderSubState == OrderConst.ORDER_SUB_STATE_102) {
                    if (orderType == PromotionConst.PROMOTION_TYPE_103) {
                        value = "尾款等待买家付款";
                    } else {
                        if (depositRemainTime > 0) {
                            value = "尾款生成中";
                        } else {
                            value = "尾款等待买家付款";
                        }
                    }
                } else {
                    value = "待付款";
                }
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

    public static String getRealEvaluateStateValue(Integer evaluateState) {
        String value = null;
        if (StringUtils.isEmpty(evaluateState)) return Language.translate("未知");
        switch (evaluateState) {
            case OrderConst.EVALUATE_STATE_1:
                value = "待评价";
                break;
            case OrderConst.EVALUATE_STATE_2:
                value = "部分评价";
                break;
            case OrderConst.EVALUATE_STATE_3:
                value = "全部评价";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }

    public static String dealOrderTypeValue(Integer orderType, boolean isHasDeposit) {
        String value = null;
        if (StringUtils.isEmpty(orderType)) return Language.translate("未知");
        switch (orderType) {
            case PromotionConst.PROMOTION_TYPE_102:
                value = "拼团";
                break;
            case PromotionConst.PROMOTION_TYPE_103:
                if (isHasDeposit) {
                    value = "定金预售";
                } else {
                    value = "全款预售";
                }
                break;
            case PromotionConst.PROMOTION_TYPE_105:
                value = "阶梯团";
                break;
            default:
                value = "";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }

}
