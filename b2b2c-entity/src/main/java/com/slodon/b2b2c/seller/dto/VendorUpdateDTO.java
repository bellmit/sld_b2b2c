package com.slodon.b2b2c.seller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lxk
 */
@Data
public class VendorUpdateDTO implements Serializable {

    private static final long serialVersionUID = 4397896137631711616L;
    @ApiModelProperty(value = "商户id", required = true)
    private Long vendorId;

    @ApiModelProperty(value = "商户账号", required = true)
    private String vendorName;

    @ApiModelProperty(value = "商户邮箱", required = true)
    private String vendorEmail;

    @ApiModelProperty(value = "手机号", required = true)
    private String vendorMobile;

    @ApiModelProperty(value = "权限组id")
    private Integer rolesId;

}
