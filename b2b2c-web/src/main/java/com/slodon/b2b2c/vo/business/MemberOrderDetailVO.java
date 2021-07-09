package com.slodon.b2b2c.vo.business;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.slodon.b2b2c.business.pojo.Order;
import com.slodon.b2b2c.business.pojo.OrderExtend;
import com.slodon.b2b2c.business.pojo.OrderLog;
import com.slodon.b2b2c.business.pojo.OrderPay;
import com.slodon.b2b2c.core.constant.LadderGroupConst;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.constant.PayConst;
import com.slodon.b2b2c.core.constant.PromotionConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.CommonUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.member.pojo.MemberInvoice;
import com.slodon.b2b2c.promotion.pojo.LadderGroupOrderExtend;
import com.slodon.b2b2c.promotion.pojo.PresellOrderExtend;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.slodon.b2b2c.vo.business.MemberOrderListVO.getRealOrderStateValue;

/**
 * @author lxk
 */
@Data
public class MemberOrderDetailVO {

    @ApiModelProperty("订单id")
    private Integer orderId;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("支付单号")
    private String paySn;

    @ApiModelProperty("父订单号，无需拆单时，父订单号=订单号")
    private String parentSn;

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

    @ApiModelProperty("订单状态：0-已取消；10-未付款订单；20-已付款；30-已发货；40-已完成;50-已关闭")
    private Integer orderState;

    @ApiModelProperty("订单状态值：0-已取消；10-未付款订单；20-已付款；30-已发货；40-已完成;50-已关闭")
    private String orderStateValue;

    @ApiModelProperty("订单子状态：101-待付定金；102-待付尾款；103-已完成付款")
    private Integer orderSubState = 0;

    @ApiModelProperty("锁定状态:0是正常,大于0是锁定,默认是0")
    private Integer lockState;

    @ApiModelProperty("商品总金额")
    private BigDecimal totalMoney;

    @ApiModelProperty("实付款")
    private BigDecimal actualPayment;

    @ApiModelProperty("总运费")
    private BigDecimal totalExpress;

    //    @ApiModelProperty("三方支付金额")
//    private BigDecimal payAmount;
//
//    @ApiModelProperty("余额账户支付总金额")
//    private BigDecimal balanceAmount;
//
//    @ApiModelProperty("积分抵扣金额")
//    private BigDecimal integralCashAmount;
//
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

    @ApiModelProperty("发货类型：0-物流发货，1-无需物流")
    private Integer deliverType;

    @ApiModelProperty("发货人")
    private String deliverName;

    @ApiModelProperty("发货人电话")
    private String deliverMobile;

    @ApiModelProperty("自动收货时间，=商家发货时间+后台设置的自动收货时间")
    private Date autoReceiveTime;

    @ApiModelProperty("支付状态:：0默认未支付,1已支付")
    private String payState;

    @ApiModelProperty("支付状态值:：0默认未支付,1已支付")
    private String payStateValue;

    @ApiModelProperty("是否评价:1.未评价,2.部分评价,3.全部评价")
    private Integer evaluateState;

    @ApiModelProperty("发票信息")
    private MemberInvoice invoice;

    @ApiModelProperty("订单类型：1-普通订单")
    private Integer orderType;

    @ApiModelProperty("订单类型：1-普通订单；其他直接存活动类型（具体类型查看ActivityConst）")
    private String orderTypeValue;

    @ApiModelProperty("商家优惠券优惠金额")
    private BigDecimal storeVoucherAmount;

    @ApiModelProperty("平台优惠券优惠金额")
    private BigDecimal platformVoucherAmount;

    @ApiModelProperty("满优惠金额")
    private BigDecimal fullDiscountAmount;

    @ApiModelProperty("是否多店铺")
    private Boolean isManyStore = false;

    @ApiModelProperty("剩余时间（秒）")
    private long remainTime;

    @ApiModelProperty("订单日志列表")
    private List<OrderLog> orderLogs;

    @ApiModelProperty("子订单列表")
    private List<ChildOrdersVO> childOrdersVOS;

    @ApiModelProperty("预售信息")
    private PresellDetailInfo presellInfo;

    @ApiModelProperty("阶梯团信息")
    private LadderGroupDetailInfo ladderGroupDetailInfo;

    public MemberOrderDetailVO(Order order, OrderExtend orderExtend, OrderPay orderPay) {
        orderId = order.getOrderId();
        orderSn = order.getOrderSn();
        paySn = order.getPaySn();
        parentSn = order.getParentSn();
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
        orderState = order.getOrderState();
        orderStateValue = getRealOrderStateValue(orderState, 0, 0L, order.getOrderType());
        lockState = order.getLockState();
//        payAmount = order.getPayAmount();
//        balanceAmount = order.getBalanceAmount();
//        integralCashAmount = order.getIntegralCashAmount();
        refuseReason = order.getRefuseReason();
        refuseRemark = order.getRefuseRemark();
        orderRemark = orderExtend.getOrderRemark();
        voucherPrice = orderExtend.getVoucherPrice();
        deliverType = orderExtend.getDeliverType();
        deliverName = orderExtend.getDeliverName();
        deliverMobile = orderExtend.getDeliverMobile();
        payState = orderPay.getApiPayState();
        payStateValue = getRealPayStateValue(payState);
        evaluateState = order.getEvaluateState();
        invoice = StringUtils.isEmpty(orderExtend.getInvoiceInfo()) ? null : JSONObject.parseObject(orderExtend.getInvoiceInfo(), MemberInvoice.class);
        orderType = order.getOrderType();
        storeVoucherAmount = orderExtend.getStoreVoucherAmount();
        platformVoucherAmount = orderExtend.getPlatformVoucherAmount();
        fullDiscountAmount = dealFullDiscountAmount(order.getActivityDiscountDetail());
    }

    public static String getRealPayStateValue(String payState) {
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

    public BigDecimal dealFullDiscountAmount(String activityDiscountDetail) {
        BigDecimal fullDiscountAmount = new BigDecimal("0.00");
        if (StringUtil.isEmpty(activityDiscountDetail)) {
            return fullDiscountAmount;
        }
        //只解析属于满优惠的信息（满优惠类型：201-204）
        JSONArray jsonArray = JSONObject.parseArray(activityDiscountDetail);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            int promotionType = Integer.parseInt(jsonObject.getString("promotionType"));
            if (promotionType >= PromotionConst.PROMOTION_TYPE_201 && promotionType <= PromotionConst.PROMOTION_TYPE_204) {
                BigDecimal discount = new BigDecimal(jsonObject.getString("discount"));
                fullDiscountAmount = fullDiscountAmount.add(discount);
            }
        }
        return fullDiscountAmount;
    }

    public BigDecimal getActivityDiscountAmount() {
        BigDecimal discount = new BigDecimal("0.00");
        if (CollectionUtils.isEmpty(childOrdersVOS)) {
            return discount;
        }
        for (ChildOrdersVO childOrdersVO : childOrdersVOS) {
            discount = discount.add(childOrdersVO.getActivityDiscountAmount());
        }
        return discount;
    }

    @Data
    public static class PresellDetailInfo {
        @ApiModelProperty("订单状态：101-待付定金；102-待付尾款；103-已付全款")
        private Integer orderSubState;

        @ApiModelProperty("预售价格")
        private BigDecimal presellPrice;

        @ApiModelProperty("定金可以抵现的金额（全款预售不需要此项，定金预售需要）")
        private BigDecimal firstExpand;

        @ApiModelProperty("商品定金")
        private BigDecimal depositAmount;

        @ApiModelProperty("定金需付款")
        private BigDecimal needDepositAmount;

        @ApiModelProperty("商品尾款")
        private BigDecimal remainAmount;

        @ApiModelProperty("尾款需付款")
        private BigDecimal needRemainAmount;

        @ApiModelProperty("是否全款订单：1-全款订单，0-定金预售订单")
        private Integer isAllPay;

        @ApiModelProperty("尾款支付的开始时间")
        private Date remainStartTime;

        @ApiModelProperty("尾款优惠")
        private BigDecimal finalDiscount;

        @ApiModelProperty("支付定金剩余时间（秒）")
        private long depositRemainTime;

        @ApiModelProperty("是否开始支付尾款")
        private Boolean isStartRemainPay = false;

        @ApiModelProperty("尾款支付剩余时间（秒）")
        private long remainEndTime;

        @ApiModelProperty("发货时间")
        private Date deliverTime;

        public PresellDetailInfo(PresellOrderExtend presellOrderExtend) {
            this.orderSubState = presellOrderExtend.getOrderSubState();
            this.presellPrice = presellOrderExtend.getPresellPrice();
            this.firstExpand = StringUtil.isNullOrZero(presellOrderExtend.getFirstExpand()) ? BigDecimal.ZERO : presellOrderExtend.getFirstExpand();
            this.depositAmount = presellOrderExtend.getDepositAmount();
            this.needDepositAmount = presellOrderExtend.getDepositAmount().multiply(new BigDecimal(presellOrderExtend.getProductNum()));
            this.remainAmount = presellOrderExtend.getRemainAmount();
            this.needRemainAmount = presellOrderExtend.getRemainAmount().multiply(new BigDecimal(presellOrderExtend.getProductNum()));
            this.isAllPay = presellOrderExtend.getIsAllPay();
            this.finalDiscount = presellOrderExtend.getIsAllPay() == OrderConst.IS_ALL_PAY_1 ? BigDecimal.ZERO
                    : StringUtil.isNullOrZero(presellOrderExtend.getFirstExpand()) ? BigDecimal.ZERO
                    : (presellOrderExtend.getFirstExpand().subtract(presellOrderExtend.getDepositAmount())).multiply(new BigDecimal(presellOrderExtend.getProductNum()));
            if (presellOrderExtend.getIsAllPay() == OrderConst.IS_ALL_PAY_0) {
                this.remainStartTime = presellOrderExtend.getRemainStartTime();
                long time1 = presellOrderExtend.getDepositEndTime().getTime();
                long time4 = presellOrderExtend.getRemainStartTime().getTime();

                long time2 = new Date().getTime();
                long startRemainPay = (time4 - time2) / 1000;
                if (startRemainPay < 0) {
                    isStartRemainPay = true;
                }
                long depositRemainTime = (time1 - time2) / 1000;
                this.depositRemainTime = depositRemainTime < 0 ? 0 : depositRemainTime;
                long time3 = presellOrderExtend.getRemainEndTime().getTime();
                long remainEndTime = (time3 - time2) / 1000;
                this.remainEndTime = remainEndTime < 0 ? 0 : remainEndTime;
            }
            this.deliverTime = presellOrderExtend.getDeliverTime();
        }
    }

    @Data
    public static class LadderGroupDetailInfo {
        @ApiModelProperty("订单状态：101-待付定金；102-待付尾款；103-已付全款")
        private Integer orderSubState;

        @ApiModelProperty("商品定金")
        private BigDecimal advanceDeposit;

        @ApiModelProperty("定金需付款")
        private BigDecimal needAdvanceDeposit;

        @ApiModelProperty("商品尾款")
        private BigDecimal remainAmount;

        @ApiModelProperty("尾款需付款")
        private BigDecimal needRemainAmount;

        @ApiModelProperty("实付尾款金额")
        private BigDecimal realRemainAmount;

        @ApiModelProperty("尾款支付的开始时间")
        private Date remainStartTime;

        @ApiModelProperty("支付定金剩余时间（秒）")
        private long depositRemainTime;

        @ApiModelProperty("尾款支付剩余时间（秒）")
        private long remainEndTime;

        @ApiModelProperty("是否退还定金")
        private Boolean isRefundDeposit = false;

        public LadderGroupDetailInfo(LadderGroupOrderExtend ladderGroupOrderExtend, BigDecimal activityDiscountAmount) {
            this.orderSubState = ladderGroupOrderExtend.getOrderSubState();
            this.advanceDeposit = ladderGroupOrderExtend.getAdvanceDeposit();
            this.needAdvanceDeposit = ladderGroupOrderExtend.getAdvanceDeposit().multiply(new BigDecimal(ladderGroupOrderExtend.getProductNum()));
            this.remainAmount = ladderGroupOrderExtend.getRemainAmount();
            if (!StringUtil.isNullOrZero(ladderGroupOrderExtend.getRemainAmount())) {
                this.needRemainAmount = ladderGroupOrderExtend.getRemainAmount().multiply(new BigDecimal(ladderGroupOrderExtend.getProductNum()));
            }
            if (ladderGroupOrderExtend.getOrderSubState() == LadderGroupConst.ORDER_SUB_STATE_3) {
                this.realRemainAmount = ladderGroupOrderExtend.getRemainAmount().multiply(new BigDecimal(ladderGroupOrderExtend.getProductNum())).subtract(activityDiscountAmount);
            } else {
                if (!StringUtil.isNullOrZero(ladderGroupOrderExtend.getRemainAmount())) {
                    this.realRemainAmount = ladderGroupOrderExtend.getRemainAmount().multiply(new BigDecimal(ladderGroupOrderExtend.getProductNum()));
                }
            }
            this.remainStartTime = ladderGroupOrderExtend.getRemainStartTime();
            long time1 = ladderGroupOrderExtend.getRemainStartTime().getTime();
            long time2 = new Date().getTime();
            long depositRemainTime = (time1 - time2) / 1000;
            this.depositRemainTime = depositRemainTime < 0 ? 0 : depositRemainTime;
            long time3 = ladderGroupOrderExtend.getRemainEndTime().getTime();
            long remainEndTime = (time3 - time2) / 1000;
            this.remainEndTime = remainEndTime < 0 ? 0 : remainEndTime;
        }
    }
}
