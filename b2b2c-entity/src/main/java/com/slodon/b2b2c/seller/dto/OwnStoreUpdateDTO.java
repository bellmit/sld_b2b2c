package com.slodon.b2b2c.seller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lxk
 */
@Data
public class OwnStoreUpdateDTO implements Serializable {

    private static final long serialVersionUID = 4858417544737750983L;

    @ApiModelProperty(value = "店铺id",required = true)
    private Long storeId;

    @ApiModelProperty(value = "店铺联系人")
    private String contactName;

    @ApiModelProperty(value = "联系人手机号")
    private String contactPhone;

    @ApiModelProperty(value = "邮政编码")
    private String postCode;

    @ApiModelProperty(value = "省份编码")
    private String provinceCode;

    @ApiModelProperty(value = "城市编码")
    private String cityCode;

    @ApiModelProperty(value = "地区编码")
    private String areaCode;

    @ApiModelProperty(value = "省市区名称组合")
    private String areaInfo;

    @ApiModelProperty("店铺详细地址")
    private String address;

    @ApiModelProperty(value = "结算方式：1-按月结算，2-按周结算")
    private Integer billType;

    @ApiModelProperty(value = "结算日期字符串，以逗号隔开")
    private String billDays;
}
