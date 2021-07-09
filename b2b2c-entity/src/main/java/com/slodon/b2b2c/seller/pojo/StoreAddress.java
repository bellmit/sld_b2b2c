package com.slodon.b2b2c.seller.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 店铺地址表
 */
@Data
public class StoreAddress implements Serializable {
    private static final long serialVersionUID = -7225830441510829340L;

    @ApiModelProperty("地址ID")
    private Integer addressId;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("收件人姓名")
    private String contactName;

    @ApiModelProperty("联系电话")
    private String telphone;

    @ApiModelProperty("备用联系电话")
    private String telphone2;

    @ApiModelProperty("省份编码")
    private String provinceCode;

    @ApiModelProperty("城市编码")
    private String cityCode;

    @ApiModelProperty("地区编码")
    private String areaCode;

    @ApiModelProperty("街道编码")
    private String streetCode;

    @ApiModelProperty("省市区组合")
    private String areaInfo;

    @ApiModelProperty("详细地址")
    private String address;

    @ApiModelProperty("创建时间")
    private Date createdTime;

    @ApiModelProperty("修改时间")
    private Date updateTime;

    @ApiModelProperty("是否默认：1-是;0-否")
    private Integer isDefault;

    @ApiModelProperty("类型：1-发货地址；2-收货地址")
    private Integer type;
}