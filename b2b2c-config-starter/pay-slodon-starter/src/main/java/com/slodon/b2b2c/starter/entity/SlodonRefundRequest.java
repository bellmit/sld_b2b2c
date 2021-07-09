package com.slodon.b2b2c.starter.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 统一退款封装参数
 *
 */
@Data
public class SlodonRefundRequest implements Serializable {

    private static final long serialVersionUID = -740731351931418638L;

    @ApiModelProperty(value = "支付宝付款基础参数，支付宝付款时必传")
    private AliPayProperties alipayProperties;

    @ApiModelProperty(value = "微信付款基础参数，微信支付时必传")
    private WxPayProperties wxPayProperties;

    @ApiModelProperty(value = "支付单号", required = true)
    private String paySn;

    @ApiModelProperty(value = "退款单号", required = true)
    private String refundSn;

    @ApiModelProperty(value = "退款金额", required = true)
    private BigDecimal refundAmount;

    @ApiModelProperty(value = "支付总金额，微信退款必传")
    private BigDecimal totalAmount;
}
