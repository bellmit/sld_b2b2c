package com.slodon.b2b2c.system.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 插件配置信息表
 */
@Data
public class PluginConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增主键")
    private Integer pId;

    @ApiModelProperty("插件ID")
    private String pluginId;

    @ApiModelProperty("插件相关配置")
    private String pluginConfig;

    @ApiModelProperty("插件版本")
    private String version;

    @ApiModelProperty("是否启用（0-关闭，1-启用）")
    private Integer isEnable;

    @ApiModelProperty("插件安装日期")
    private Date installDate;

    @ApiModelProperty("最后更新配置时间")
    private Date updateDate;

    @ApiModelProperty("插件显示排序")
    private Integer sort;
}