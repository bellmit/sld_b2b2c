package com.slodon.b2b2c.business.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单退货表
 */
@Data
public class OrderReturn implements Serializable {
    private static final long serialVersionUID = -9095259077494686915L;
    @ApiModelProperty("退货id")
    private Integer returnId;

    @ApiModelProperty("售后服务单号")
    private String afsSn;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("用户ID")
    private Integer memberId;

    @ApiModelProperty("用户名称")
    private String memberName;

    @ApiModelProperty("退款方式：0-原路退回，1-退账户余额")
    private Integer returnMoneyType;

    @ApiModelProperty("退款类型：1==仅退款 2=退货退款")
    private Integer returnType;

    @ApiModelProperty("退货数量")
    private Integer returnNum;

    @ApiModelProperty("退款金额")
    private BigDecimal returnMoneyAmount;

    @ApiModelProperty("退还积分")
    private Integer returnIntegralAmount;

    @ApiModelProperty("扣除积分数量（购物赠送的积分）")
    private Integer deductIntegralAmount;

    @ApiModelProperty("退还运费的金额（用于待发货订单仅退款时处理）")
    private BigDecimal returnExpressAmount;

    @ApiModelProperty("退还优惠券编码（最后一单退还优惠券）")
    private String returnVoucherCode;

    @ApiModelProperty("平台对应类别的佣金比例，0-1数字，")
    private BigDecimal commissionRate;

    @ApiModelProperty("佣金金额")
    private BigDecimal commissionAmount;

    @ApiModelProperty("退货退款状态：100-买家申请仅退款；101-买家申请退货退款；102-买家退货给商家；200-商家同意退款申请；201-商家同意退货退款申请；202-商家拒绝退款申请(退款关闭/拒收关闭)；203-商家确认收货；300-平台确认退款(已完成)")
    private Integer state;

    @ApiModelProperty("申请时间")
    private Date applyTime;

    @ApiModelProperty("退货完成时间")
    private Date completeTime;

    @ApiModelProperty("拒绝原因")
    private String refuseReason;
}