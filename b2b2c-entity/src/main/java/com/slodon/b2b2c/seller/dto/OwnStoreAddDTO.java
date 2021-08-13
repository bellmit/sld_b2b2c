package com.slodon.b2b2c.seller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lxk
 */
@Data
public class OwnStoreAddDTO implements Serializable {

    private static final long serialVersionUID = 1123867034236321014L;

    @ApiModelProperty(value = "店铺名称",required = true)
    private String storeName;

    @ApiModelProperty(value = "店铺联系人",required = true)
    private String contactName;

    @ApiModelProperty(value = "联系人手机号",required = true)
    private String contactPhone;

    @ApiModelProperty(value = "商户账号",required = true)
    private String vendorName;

    @ApiModelProperty(value = "登陆密码",required = true)
    private String vendorPassword;

    @ApiModelProperty(value = "邮政编码",required = true)
    private String postCode;

    @ApiModelProperty(value = "省份编码",required = true)
    private String provinceCode;

    @ApiModelProperty(value = "城市编码",required = true)
    private String cityCode;

    @ApiModelProperty(value = "地区编码",required = true)
    private String areaCode;

    @ApiModelProperty(value = "省市区名称组合",required = true)
    private String areaInfo;

    @ApiModelProperty("店铺详细地址")
    private String address;

    @ApiModelProperty(value = "结算方式：1-按月结算，2-按周结算")
    private Integer billType;

    @ApiModelProperty(value = "结算日期字符串，以逗号隔开")
    private String billDays;

}
