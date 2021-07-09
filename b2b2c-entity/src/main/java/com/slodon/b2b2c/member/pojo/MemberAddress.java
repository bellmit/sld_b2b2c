package com.slodon.b2b2c.member.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 收货地址
 */
@Data
public class MemberAddress implements Serializable {

    private static final long serialVersionUID = 7751142571524952914L;
    @ApiModelProperty("地址id")
    private Integer addressId;

    @ApiModelProperty("会员id")
    private Integer memberId;

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

    @ApiModelProperty("电话")
    private String telPhone;

    @ApiModelProperty("是否默认地址：1-默认地址，0-非默认地址")
    private Integer isDefault;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改时间")
    private Date updateTime;
}