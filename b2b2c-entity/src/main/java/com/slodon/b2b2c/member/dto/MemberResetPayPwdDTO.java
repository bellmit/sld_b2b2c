package com.slodon.b2b2c.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MemberResetPayPwdDTO {
    @ApiModelProperty( "会员id")
    private Integer memberId;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("验证码")
    private String verifyCode;

    @ApiModelProperty( "密码")
    private String newPayPwd;

    @ApiModelProperty("确认密码")
    private String confirmPwd;
}
