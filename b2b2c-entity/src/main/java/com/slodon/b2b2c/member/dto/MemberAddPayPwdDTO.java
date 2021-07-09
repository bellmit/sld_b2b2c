package com.slodon.b2b2c.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MemberAddPayPwdDTO {

    @ApiModelProperty(value = "密码", required = true)
    private String payPwd;

    @ApiModelProperty(value = "手机号", required = true)
    private String memberMobile;

    @ApiModelProperty(value = "验证码", required = true)
    private String verifyCode;
}
