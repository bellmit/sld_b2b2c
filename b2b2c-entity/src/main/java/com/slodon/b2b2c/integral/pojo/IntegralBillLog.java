package com.slodon.b2b2c.integral.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 积分商城结算日志表
 */
@Data
public class IntegralBillLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("日志id")
    private Integer logId;

    @ApiModelProperty("结算单号")
    private String billSn;

    @ApiModelProperty("操作人id")
    private Long operatorId;

    @ApiModelProperty("操作人名称")
    private String operatorName;

    @ApiModelProperty("操作人角色，1-平台，2-商户")
    private Integer operatorRole;

    @ApiModelProperty("操作状态")
    private Integer state;

    @ApiModelProperty("操作行为")
    private String content;

    @ApiModelProperty("操作时间")
    private Date createTime;
}