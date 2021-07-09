package com.slodon.b2b2c.member.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员提现银行账号
 */
@Data
public class MemberBankAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("账号id")
    private Integer accountId;

    @ApiModelProperty("会员id")
    private Integer memberId;

    @ApiModelProperty("会员名称")
    private String memberName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("账户code：UNIONPAY；ALIPAY,WXPAY")
    private String accountCode;

    @ApiModelProperty("账户名称")
    private String accountName;

    @ApiModelProperty("账户号码（银行账号，支付宝/微信账号）")
    private String accountNumber;

    @ApiModelProperty("银行名称（如为第三方支付直接填微信、支付宝）")
    private String bankName;
}