package com.slodon.b2b2c.seller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lxk
 */
@Data
public class StoreAddressUpdateDTO implements Serializable {

    @ApiModelProperty(value = "地址id", required = true)
    private Integer addressId;

    @ApiModelProperty(value = "类型：1-发货地址；2-收货地址", required = true)
    private Integer type;

    @ApiModelProperty(value = "联系人")
    private String contactName;

    @ApiModelProperty(value = "手机号")
    private String telphone;

    @ApiModelProperty(value = "省编码")
    private String provinceCode;

    @ApiModelProperty(value = "市编码")
    private String cityCode;

    @ApiModelProperty(value = "区编码")
    private String areaCode;

    @ApiModelProperty(value = "省市区组合")
    private String areaInfo;

    @ApiModelProperty(value = "详细地址")
    private String address;

    @ApiModelProperty("是否设为默认地址:1-是;0-否")
    private Integer isDefault;
}
