package com.slodon.b2b2c.system.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: slodon
 * @Description 操作员编辑DTO
 * @Author wuxy
 */
@Data
public class AdminUpdateDTO implements Serializable {

    private static final long serialVersionUID = -6134206986103877349L;
    @ApiModelProperty(value = "管理员id", required = true)
    private Integer adminId;

    @ApiModelProperty(value = "账号", required = true)
    private String adminName;

    @ApiModelProperty(value = "手机号", required = true)
    private String phone;

    @ApiModelProperty(value = "邮箱", required = true)
    private String email;

    @ApiModelProperty("权限组id")
    private Integer roleId;
}
