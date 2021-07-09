package com.slodon.b2b2c.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class MemberBalanceRechargeAddDTO implements Serializable {
    private static final long serialVersionUID = 1213344953337063243L;


    @ApiModelProperty(value = "支付方式code：PCUNIONPAY；H5UNIONPAY；PCALIPAY；H5ALIPAY；PCWXPAY；H5WXPAY",required = true)
    private String paymentCode;

    @ApiModelProperty(value = "支付金额",required = true)
    private BigDecimal payAmount;

    @ApiModelProperty(value = "支付方式名称：PC银联；H5银联；PC支付宝；H5支付宝；PC微信；H5微信",required = true)
    private String paymentName;

    @ApiModelProperty(value = "第三方支付接口交易号,第三方返回",required = true)
    private String tradeSn;


}
