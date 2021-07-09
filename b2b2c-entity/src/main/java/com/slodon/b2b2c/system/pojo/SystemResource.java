package com.slodon.b2b2c.system.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 平台资源表
 */
@Data
public class SystemResource implements Serializable {
    private static final long serialVersionUID = 2509030718156457401L;
    @ApiModelProperty("资源id")
    private Integer resourceId;

    @ApiModelProperty("父级id")
    private Integer pid;

    @ApiModelProperty("资源名称")
    private String content;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("资源状态：0-未启用，1-启用")
    private Integer state;

    @ApiModelProperty("资源等级：1-一级菜单，2-二级菜单，3-三级菜单，4-按钮")
    private Integer grade;

    @ApiModelProperty("对应路由")
    private String url;

    @ApiModelProperty("前端对应路由")
    private String frontPath;

    @ApiModelProperty("子资源")
    private List<SystemResource> children;
}