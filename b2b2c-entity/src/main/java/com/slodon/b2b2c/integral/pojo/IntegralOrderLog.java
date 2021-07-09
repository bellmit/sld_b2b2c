package com.slodon.b2b2c.integral.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单操作日志表
 */
@Data
public class IntegralOrderLog implements Serializable {
    private static final long serialVersionUID = 466472303424034263L;
    @ApiModelProperty("日志id")
    private Integer logId;

    @ApiModelProperty("操作人角色(1-系统管理员，2-商户，3-会员）")
    private Integer logRole;

    @ApiModelProperty("操作人ID，结合log_role使用")
    private Long logUserId;

    @ApiModelProperty("操作人名称")
    private String logUserName;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("订单状态：0-已取消；10-未付款订单；20-已付款；30-已发货；40-已完成;50-已关闭")
    private Integer orderStateLog;

    @ApiModelProperty("处理时间")
    private Date logTime;

    @ApiModelProperty("文字描述")
    private String logContent;
}