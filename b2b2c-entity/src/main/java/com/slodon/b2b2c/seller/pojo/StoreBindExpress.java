package com.slodon.b2b2c.seller.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 店铺的选择使用的快递公司
 */
@Data
public class StoreBindExpress implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ID")
    private Integer bindId;

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("快递公司ID")
    private Integer expressId;

    @ApiModelProperty("公司名称")
    private String expressName;

    @ApiModelProperty("快递公司状态，平台是否启用：1-启用，0-不启用")
    private String expressState;

    @ApiModelProperty("添加时间")
    private Date createTime;

    @ApiModelProperty("编号")
    private String expressCode;

    @ApiModelProperty("公司网址")
    private String expressWebsite;
}