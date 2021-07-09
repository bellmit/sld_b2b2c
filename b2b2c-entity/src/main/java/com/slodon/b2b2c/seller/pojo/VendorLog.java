package com.slodon.b2b2c.seller.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商家用户操作日志表
 */
@Data
public class VendorLog implements Serializable {
    private static final long serialVersionUID = -2085223937203355056L;

    @ApiModelProperty("日志id")
    private Integer logId;

    @ApiModelProperty("商家用户id")
    private Long vendorId;

    @ApiModelProperty("商家用户名称")
    private String vendorName;

    @ApiModelProperty("操作URL")
    private String operationUrl;

    @ApiModelProperty("操作行为")
    private String operationContent;

    @ApiModelProperty("操作时间")
    private Date optTime;

    @ApiModelProperty("ip")
    private String ip;
}