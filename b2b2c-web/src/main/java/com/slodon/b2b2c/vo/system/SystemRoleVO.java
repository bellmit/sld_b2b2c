package com.slodon.b2b2c.vo.system;

import com.slodon.b2b2c.system.pojo.SystemRole;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装权限组VO对象
 * @Author wuxy
 */
@Data
public class SystemRoleVO implements Serializable {

    private static final long serialVersionUID = 7705755932609635849L;
    @ApiModelProperty("角色id")
    private Integer roleId;

    @ApiModelProperty("角色名称")
    private String roleName;

    @ApiModelProperty("角色描述")
    private String description;

    @ApiModelProperty("创建人")
    private String createAdminName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("是否系统内置 1-内置（不可删除、不可修改），0-非内置（可删除、修改）")
    private Integer isInner;

    @ApiModelProperty("角色拥有资源id")
    private List<Integer> resourcesList;

    public SystemRoleVO(SystemRole systemRole) {
        roleId = systemRole.getRoleId();
        roleName = systemRole.getRoleName();
        description = systemRole.getDescription();
        createAdminName = systemRole.getCreateAdminName();
        createTime = systemRole.getCreateTime();
        updateTime = systemRole.getUpdateTime();
        isInner = systemRole.getIsInner();
    }

}
