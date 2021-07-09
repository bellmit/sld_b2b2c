package com.slodon.b2b2c.seller.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商家表
 */
@Data
public class Vendor implements Serializable {
    private static final long serialVersionUID = -1958885603406177282L;
    @ApiModelProperty("商户id")
    private Long vendorId;

    @ApiModelProperty("商户账号")
    private String vendorName;

    @ApiModelProperty("商户密码")
    private String vendorPassword;

    @ApiModelProperty("商户手机")
    private String vendorMobile;

    @ApiModelProperty("商户邮件")
    private String vendorEmail;

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("是否店铺管理员：0-否，1-是，每个店铺只有一个账号为1")
    private String isStoreAdmin;

    @ApiModelProperty("注册时间")
    private Date registerTime;

    @ApiModelProperty("最后登陆时间")
    private Date latestLoginTime;

    @ApiModelProperty("最后登录ip")
    private String latestLoginIp;

    @ApiModelProperty("商家头像")
    private String avatarUrl;

    @ApiModelProperty("0-禁止，1-允许")
    private Integer isAllowLogin;

    @ApiModelProperty("角色id")
    private Integer rolesId;

    @ApiModelProperty("店铺信息")
    private Store store;
}