package com.slodon.b2b2c.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MemberUpdatePayPwdDTO {
    @ApiModelProperty( value = "旧支付密码",required = true)
    private String oldPayPwd;

    @ApiModelProperty( value = "新支付密码",required = true)
    private String payPwd;

}
