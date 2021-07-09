package com.slodon.b2b2c.system.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 角色表
 */
@Data
public class SystemRole implements Serializable {
    private static final long serialVersionUID = -430743772995216960L;
    @ApiModelProperty("角色id")
    private Integer roleId;

    @ApiModelProperty("角色名称")
    private String roleName;

    @ApiModelProperty("角色描述")
    private String description;

    @ApiModelProperty("创建人id")
    private Integer createAdminId;

    @ApiModelProperty("创建人名称")
    private String createAdminName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人id")
    private Integer updateAdminId;

    @ApiModelProperty("更新人名称")
    private String updateAdminName;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("状态：0-未启用，1-启用")
    private Integer state;

    @ApiModelProperty("是否系统内置 1-内置（不可删除、不可修改），0-非内置（可删除、修改）")
    private Integer isInner;
}