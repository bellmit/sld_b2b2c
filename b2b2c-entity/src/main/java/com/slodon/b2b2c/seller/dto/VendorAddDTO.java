package com.slodon.b2b2c.seller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lxk
 */
@Data
public class VendorAddDTO implements Serializable {

    private static final long serialVersionUID = 8834903826243136572L;
    @ApiModelProperty(value = "商户账号", required = true)
    private String vendorName;

    @ApiModelProperty(value = "商户密码", required = true)
    private String vendorPassword;

    @ApiModelProperty(value = "商户邮箱", required = true)
    private String vendorEmail;

    @ApiModelProperty(value = "确认密码", required = true)
    private String confirmPassword;

    @ApiModelProperty(value = "手机号", required = true)
    private String vendorMobile;

    @ApiModelProperty(value = "权限组id")
    private Integer rolesId;

}
