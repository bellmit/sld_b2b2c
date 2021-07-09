package com.slodon.b2b2c.vo.seller;

import com.slodon.b2b2c.seller.pojo.VendorRoles;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lxk
 * @program: slodon
 * @Description 封装权限组VO对象
 */
@Data
public class VendorRolesVO {

    @ApiModelProperty("角色id")
    private Integer rolesId;

    @ApiModelProperty("角色名称")
    private String rolesName;

    @ApiModelProperty("角色描述")
    private String description;

    @ApiModelProperty("创建人名称")
    private String createVendorName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改人名称")
    private String updateVendorName;

    @ApiModelProperty("修改时间")
    private Date updateTime;

    @ApiModelProperty("状态：1、启用，0、未启用")
    private Integer state;

    @ApiModelProperty("是否系统内置：1-内置（不可删除、不可修改），0-非内置（可删除、修改）")
    private Integer isInner;

    @ApiModelProperty("角色拥有资源id")
    private List<Integer> resourcesList;

    @ApiModelProperty("消息接收设置编码")
    private List<String> msgList = new ArrayList<>();

    public VendorRolesVO(VendorRoles vendorRoles) {
        rolesId = vendorRoles.getRolesId();
        rolesName = vendorRoles.getRolesName();
        description = vendorRoles.getContent();
        createVendorName = vendorRoles.getCreateVendorName();
        createTime = vendorRoles.getCreateTime();
        updateVendorName = vendorRoles.getUpdateVendorName();
        updateTime = vendorRoles.getUpdateTime();
        state = vendorRoles.getState();
        isInner = vendorRoles.getIsInner();
    }
}
