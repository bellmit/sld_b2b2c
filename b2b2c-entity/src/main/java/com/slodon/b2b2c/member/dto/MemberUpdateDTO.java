package com.slodon.b2b2c.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MemberUpdateDTO implements Serializable {

    private static final long serialVersionUID = -692980638470217072L;

    @ApiModelProperty(value = "会员id",required = true)
    private Integer memberId;

    @ApiModelProperty(value = "用户名（登录名称）,不可编辑,查重使用",required = true)
    private String memberName;

    @ApiModelProperty("mobile")
    private String memberMobile;

    @ApiModelProperty("邮箱")
    private String memberEmail;

    @ApiModelProperty("真实姓名")
    private String memberTrueName;

}
