package com.slodon.b2b2c.member.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 微信联合登录
 */
@Data
public class MemberWxsign implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("登录id")
    private Integer signId;

    @ApiModelProperty("微信用户标识")
    private String openid;

    @ApiModelProperty("用户id")
    private Integer memberId;

    @ApiModelProperty("用户来源：1==pc应用，2==app应用，3==公众号，4==小程序")
    private Integer resource;
}