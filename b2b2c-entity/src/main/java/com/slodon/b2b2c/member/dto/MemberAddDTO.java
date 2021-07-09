package com.slodon.b2b2c.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MemberAddDTO implements Serializable {


    private static final long serialVersionUID = 3737927129030215710L;

    @ApiModelProperty(value = "用户名（登录名称）", required = true)
    private String memberName;

    @ApiModelProperty(value = "密码", required = true)
    private String loginPwd;

    @ApiModelProperty(value = "确认密码", required = true)
    private String confirmPwd;

    @ApiModelProperty(value = "mobile", required = true)
    private String memberMobile;

    @ApiModelProperty("邮箱")
    private String memberEmail;

    @ApiModelProperty("真实姓名")
    private String memberTrueName;

    @ApiModelProperty(value = "会员来源：1、pc；2、H5；3、Android；4、IOS ;5 商城管理平台 ; 6 微信商城",required = true)
    private Integer registerChannel;

}
