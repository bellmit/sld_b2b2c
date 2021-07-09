package com.slodon.b2b2c.seller.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商家角色表
 */
@Data
public class VendorRoles implements Serializable {
    private static final long serialVersionUID = -8284673599055441606L;

    @ApiModelProperty("角色id")
    private Integer rolesId;

    @ApiModelProperty("角色名称")
    private String rolesName;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("角色描述")
    private String content;

    @ApiModelProperty("创建人id")
    private Long createVendorId;

    @ApiModelProperty("创建人名称")
    private String createVendorName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人id")
    private Long updateVendorId;

    @ApiModelProperty("更新人名称")
    private String updateVendorName;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("状态：1、启用，0、未启用")
    private Integer state;

    @ApiModelProperty("是否系统内置：1-内置（不可删除、不可修改），0-非内置（可删除、修改）")
    private Integer isInner;
}