package com.slodon.b2b2c.system.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 平台管理员表
 */
@Data
public class Admin implements Serializable {
    private static final long serialVersionUID = -2256596429651417474L;
    @ApiModelProperty("管理员id")
    private Integer adminId;

    @ApiModelProperty("登录名")
    private String adminName;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("角色id")
    private Integer roleId;

    @ApiModelProperty("角色名称")
    private String roleName;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("状态：1-正常；2-冻结；3-删除（伪删除）")
    private Integer state;

    @ApiModelProperty("是否超级管理员：1-是；0-否")
    private Integer isSuper;

    @ApiModelProperty("最后登录时间")
    private Date loginTime;

    @ApiModelProperty("创建管理员ID")
    private Integer createAdminId;

    @ApiModelProperty("创建时间")
    private Date createTime;
}