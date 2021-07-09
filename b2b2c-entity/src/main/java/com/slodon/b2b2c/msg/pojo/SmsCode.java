package com.slodon.b2b2c.msg.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 手机短信验证码记录
 */
@Data
public class SmsCode implements Serializable {
    private static final long serialVersionUID = -4492581498711125556L;
    @ApiModelProperty("主键id")
    private Integer codeId;

    @ApiModelProperty("会员id,注册为0")
    private Integer memberId;

    @ApiModelProperty("会员名称")
    private String memberName;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("手机验证码")
    private String verifyCode;

    @ApiModelProperty("短信类型:1.注册,2.登录,3.找回密码,4.其它")
    private Integer smsType;

    @ApiModelProperty("请求ip")
    private String requestIp;

    @ApiModelProperty("创建时间")
    private Date createTime;
}