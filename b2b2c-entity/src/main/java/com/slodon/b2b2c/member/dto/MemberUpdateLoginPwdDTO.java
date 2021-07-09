package com.slodon.b2b2c.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MemberUpdateLoginPwdDTO {
    @ApiModelProperty( value = "旧密码",required = true)
    private String oldLoginPwd;

    @ApiModelProperty( value = "新密码",required = true)
    private String loginPwd;

}
