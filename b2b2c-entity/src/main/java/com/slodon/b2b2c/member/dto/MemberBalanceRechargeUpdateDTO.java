package com.slodon.b2b2c.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员充值明细表
 */
@Data
public class MemberBalanceRechargeUpdateDTO implements Serializable {

    private static final long serialVersionUID = -498722695311305297L;
    @ApiModelProperty(value = "充值id",required = true)
    private Integer rechargeId;

    @ApiModelProperty(value = "支付方式code：PCUNIONPAY；H5UNIONPAY；PCALIPAY；H5ALIPAY；PCWXPAY；H5WXPAY")
    private String paymentCode;

//    @ApiModelProperty(value = "支付方式名称：PC银联；H5银联；PC支付宝；H5支付宝；PC微信；H5微信")
//    private String paymentName;

    @ApiModelProperty(value = "第三方支付接口交易号,第三方返回")
    private String tradeSn;

    @ApiModelProperty(value = "支付完成时间")
    private Date payTime;

}