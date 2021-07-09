package com.slodon.b2b2c.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MemberAddEmailDTO {
    @ApiModelProperty("会员名")
    private String memberName;

    @ApiModelProperty("验证码")
    private String verifyCode;

    @ApiModelProperty("是否继续绑定:1 取消,2 继续绑定")
    private Integer isContinuebind;

    @ApiModelProperty("邮箱")
    private String memberEmail;
//
//    @ApiModelProperty("类型:1 邮箱, 2 手机")
//    private Integer type;
}
