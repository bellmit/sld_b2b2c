package com.slodon.b2b2c.msg.pojo;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;

/**
 * 商家权限组拥有的消息模板表
 */
@Data
public class StoreTplRoleBind implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("绑定id")
    private Integer bindId;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("商户id")
    private Long vendorId;

    @ApiModelProperty("店铺权限组id")
    private Integer roleId;

    @ApiModelProperty("店铺权限组消息模板code集合")
    private String tplCodes;
}