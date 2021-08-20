package com.slodon.b2b2c.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MemberAddressAddDTO implements Serializable {


    private static final long serialVersionUID = -1351192600433316905L;
    @ApiModelProperty(value = "收货人",required = true)
    private String memberName;

    @ApiModelProperty(value = "省份编码",required = true)
    private String provinceCode;

    @ApiModelProperty(value = "城市编码",required = true)
    private String cityCode;

    @ApiModelProperty(value = "区县编码",required = true)
    private String districtCode;

    @ApiModelProperty(value = "邮政编码",required = true)
    private String postCode;

    @ApiModelProperty(value = "省市区组合")
    private String addressAll;

    @ApiModelProperty(value = "详细地址",required = true)
    private String detailAddress;

    @ApiModelProperty(value = "手机",required = true)
    private String telMobile;


    @ApiModelProperty(value = "是否默认地址：1-默认地址，0-非默认地址",required = true)
    private Integer isDefault;

}
