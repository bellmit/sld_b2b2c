package com.slodon.b2b2c.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MemberAddressUpdateDTO implements Serializable {


    private static final long serialVersionUID = 7595952850396884195L;

    @ApiModelProperty(value = "地址id",required = true)
    private Integer addressId;

    @ApiModelProperty("收货人")
    private String memberName;

    @ApiModelProperty("省份编码")
    private String provinceCode;

    @ApiModelProperty("城市编码")
    private String cityCode;

    @ApiModelProperty("区县编码")
    private String districtCode;

    @ApiModelProperty("省市区组合")
    private String addressAll;

    @ApiModelProperty("详细地址")
    private String detailAddress;

    @ApiModelProperty("手机")
    private String telMobile;

    @ApiModelProperty(value = "是否默认地址：1-默认地址，0-非默认地址",required = true)
    private Integer isDefault;

}
