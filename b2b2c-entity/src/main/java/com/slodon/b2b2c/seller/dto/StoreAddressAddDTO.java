package com.slodon.b2b2c.seller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lxk
 */
@Data
public class StoreAddressAddDTO implements Serializable {


    private static final long serialVersionUID = 808302827177066710L;
    @ApiModelProperty(value = "类型：1-发货地址；2-收货地址",required = true)
    private Integer type;

    @ApiModelProperty(value = "联系人",required = true)
    private String contactName;

    @ApiModelProperty(value = "手机号",required = true)
    private String telphone;

    @ApiModelProperty(value = "省编码",required = true)
    private String provinceCode;

    @ApiModelProperty(value = "市编码",required = true)
    private String cityCode;

    @ApiModelProperty(value = "区编码",required = true)
    private String areaCode;

    @ApiModelProperty(value = "省市区组合",required = true)
    private String areaInfo;

    @ApiModelProperty(value = "详细地址",required = true)
    private String address;

    @ApiModelProperty("是否设为默认地址:1-是;0-否")
    private Integer isDefault;
}
