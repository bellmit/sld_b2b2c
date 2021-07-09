package com.slodon.b2b2c.seller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lxk
 */
@Data
public class VendorRegisterDTO implements Serializable {

    private static final long serialVersionUID = 668168765261609069L;

    @ApiModelProperty(value = "商户账号", required = true)
    private String vendorName;

    @ApiModelProperty(value = "商户密码", required = true)
    private String vendorPassword;

    @ApiModelProperty(value = "商户手机号", required = true)
    private String vendorMobile;

    @ApiModelProperty(value = "短信验证码",required = true)
    private String smsCode;

    @ApiModelProperty(value = "商户邮箱")
    private String vendorEmail;

    @ApiModelProperty(value = "确认密码", required = true)
    private String confirmPassword;

    @ApiModelProperty(value = "图形验证码", required = true)
    private String verifyCode;

    @ApiModelProperty(value = "图形验证码key", required = true)
    private String verifyKey;

    @ApiModelProperty(value = "登陆ip")
    private String ip;

}
