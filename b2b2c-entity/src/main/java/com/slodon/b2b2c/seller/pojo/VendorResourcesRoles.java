package com.slodon.b2b2c.seller.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商家角色资源对应表
 */
@Data
public class VendorResourcesRoles implements Serializable {
    private static final long serialVersionUID = -6112951965356926995L;

    @ApiModelProperty("资源角色id")
    private Integer resourcesRolesId;

    @ApiModelProperty("资源id")
    private Integer resourcesId;

    @ApiModelProperty("角色id")
    private Integer rolesId;

    @ApiModelProperty("创建时间")
    private Date createTime;
}