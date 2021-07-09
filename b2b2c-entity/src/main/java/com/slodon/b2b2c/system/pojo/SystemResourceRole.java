package com.slodon.b2b2c.system.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 角色资源对应表
 */
@Data
public class SystemResourceRole implements Serializable {
    private static final long serialVersionUID = -1191047031395404723L;
    @ApiModelProperty("资源角色id")
    private Integer resourceRoleId;

    @ApiModelProperty("资源id")
    private Integer resourceId;

    @ApiModelProperty("角色id")
    private Integer roleId;

    @ApiModelProperty("创建时间")
    private Date createTime;
}