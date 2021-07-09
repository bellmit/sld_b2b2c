package com.slodon.b2b2c.helpdesk.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 客服转接表
 */
@Data
public class HelpdeskTransfer implements Serializable {
    private static final long serialVersionUID = 3763959451393439967L;
    @ApiModelProperty("转接ID")
    private Integer transferId;

    @ApiModelProperty("会员ID")
    private Integer memberId;

    @ApiModelProperty("源vendor_id")
    private Long srcVendorId;

    @ApiModelProperty("转接后vendor_id")
    private Long dstVendorId;

    @ApiModelProperty("转接时间")
    private Date switchTime;

    @ApiModelProperty("店铺ID")
    private Long storeId;
}