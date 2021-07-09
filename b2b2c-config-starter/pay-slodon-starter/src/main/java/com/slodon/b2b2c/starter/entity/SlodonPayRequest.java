package com.slodon.b2b2c.starter.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 统一支付封装参数
 *
 * @author lxk
 */
@Data
public class SlodonPayRequest implements Serializable {

    private static final long serialVersionUID = -7394130189197173961L;

    /**
     * {@link com.slodon.b2b2c.core.constant.PayConst#METHOD_WX}
     */
    @ApiModelProperty(value = "支付方式，wx-微信支付，alipay-支付宝支付", required = true)
    private String payMethod;

    /**
     * {@link com.slodon.b2b2c.core.constant.PayConst#WX_TYPE_NATIVE}
     */
    @ApiModelProperty(value = "支付类型", required = true)
    private String payType;

    @ApiModelProperty(value = "支付宝付款基础参数，支付宝付款时必传")
    private AliPayProperties alipayProperties;

    @ApiModelProperty(value = "微信付款基础参数，微信支付时必传")
    private WxPayProperties wxPayProperties;

    @ApiModelProperty(value = "支付单号", required = true)
    private String paySn;

    @ApiModelProperty(value = "支付金额,单位元", required = true)
    private BigDecimal payAmount;

    @ApiModelProperty(value = "回调地址", required = true)
    private String notifyUrl;

    @ApiModelProperty(value = "返回地址")
    private String returnUrl;

    @ApiModelProperty(value = "支付标题", required = true)
    private String subject = "sld";

    @ApiModelProperty(value = "商品描述，如：腾讯充值中心-QQ会员充值", required = true)
    private String body = "sld";

    @ApiModelProperty(value = "终端IP,微信支付必传")
    private String ip;

    @ApiModelProperty(value = "openId,微信小程序，微信内部浏览器时必传")
    private String openId;

}
