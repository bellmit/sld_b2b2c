package com.slodon.b2b2c.system.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: slodon
 * @Description 操作员新增DTO
 * @Author wuxy
 */
@Data
public class AdminAddDTO implements Serializable {

    private static final long serialVersionUID = 6964209170565074477L;
    @ApiModelProperty(value = "账号", required = true)
    private String adminName;

    @ApiModelProperty(value = "密码", required = true)
    private String password;

    @ApiModelProperty(value = "确认密码", required = true)
    private String confirmPwd;

    @ApiModelProperty(value = "手机号", required = true)
    private String phone;

    @ApiModelProperty(value = "邮箱", required = true)
    private String email;

    @ApiModelProperty("权限组id")
    private Integer roleId;
}
