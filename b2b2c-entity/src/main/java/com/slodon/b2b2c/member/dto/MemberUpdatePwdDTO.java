package com.slodon.b2b2c.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MemberUpdatePwdDTO implements Serializable {


    private static final long serialVersionUID = 1663604051513236411L;
    @ApiModelProperty(value = "会员id",required = true)
    private Integer memberId;

    @ApiModelProperty( value = "密码",required = true)
    private String loginPwd;

    @ApiModelProperty(value = "确认密码",required = true)
    private String confirmPwd;

}
