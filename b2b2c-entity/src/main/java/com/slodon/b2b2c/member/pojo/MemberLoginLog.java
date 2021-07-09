package com.slodon.b2b2c.member.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户会员登录日志表
 */
@Data
public class MemberLoginLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("日志id")
    private Integer logId;

    @ApiModelProperty("会员id")
    private Integer memberId;

    @ApiModelProperty("会员名称")
    private String memberName;

    @ApiModelProperty("登录IP")
    private String loginIp;

    @ApiModelProperty("登录时间")
    private Date createTime;

    @ApiModelProperty("会员来源：1、pc；2、H5；3、Android；4、IOS ;5、微信商城")
    private Integer source;
}