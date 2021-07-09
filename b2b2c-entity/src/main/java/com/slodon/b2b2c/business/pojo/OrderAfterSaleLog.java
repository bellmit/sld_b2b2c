package com.slodon.b2b2c.business.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 售后服务操作日志表
 */
@Data
public class OrderAfterSaleLog implements Serializable {
    private static final long serialVersionUID = 7770524984078252518L;
    @ApiModelProperty("日志id")
    private Integer logId;

    @ApiModelProperty("操作人角色(1-系统管理员，2-商户，3-会员）")
    private Integer logRole;

    @ApiModelProperty("操作人id")
    private Long logUserId;

    @ApiModelProperty("操作人名称")
    private String logUserName;

    @ApiModelProperty("售后单号")
    private String afsSn;

    @ApiModelProperty("售后服务端类型：1-退货退款单，2-换货单，3-仅退款单")
    private Integer afsType;

    @ApiModelProperty("状态，与退换货表状态相同")
    private String state;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("操作时间")
    private Date createTime;
}