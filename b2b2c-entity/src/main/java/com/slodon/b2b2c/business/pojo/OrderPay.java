package com.slodon.b2b2c.business.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单支付表
 */
@Data
public class OrderPay implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("支付id")
    private Integer payId;

    @ApiModelProperty("支付单号，全局唯一")
    private String paySn;

    @ApiModelProperty("支付单号关联的父订单编号")
    private String orderSn;

    @ApiModelProperty("现金支付金额")
    private BigDecimal payAmount;

    @ApiModelProperty("买家ID")
    private Integer memberId;

    @ApiModelProperty("支付状态：0默认未支付1已支付(只有第三方支付接口通知到时才会更改此状态)")
    private String apiPayState;

    @ApiModelProperty("回调成功时间")
    private Date callbackTime;

    @ApiModelProperty("第三方支付交易流水号")
    private String tradeSn;

    @ApiModelProperty("支付方式名称，参考OrderPaymentConst类")
    private String paymentName;

    @ApiModelProperty("支付方式code, 参考OrderPaymentConst类")
    private String paymentCode;
}