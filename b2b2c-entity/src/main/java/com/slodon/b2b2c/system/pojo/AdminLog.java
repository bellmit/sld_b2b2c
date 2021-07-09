package com.slodon.b2b2c.system.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 管理员操作日志表
 */
@Data
public class AdminLog implements Serializable {
    private static final long serialVersionUID = -2391992279611754461L;
    @ApiModelProperty("日志id")
    private Integer logId;

    @ApiModelProperty("操作管理员id")
    private Integer adminId;

    @ApiModelProperty("操作管理员名称")
    private String adminName;

    @ApiModelProperty("操作URL")
    private String logUrl;

    @ApiModelProperty("操作行为")
    private String logContent;

    @ApiModelProperty("操作时间")
    private Date logTime;

    @ApiModelProperty("ip")
    private String logIp;
}