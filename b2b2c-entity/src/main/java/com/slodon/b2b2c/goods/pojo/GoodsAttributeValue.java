package com.slodon.b2b2c.goods.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 检索属性表，系统创建，和类型绑定
 */
@Data
public class GoodsAttributeValue implements Serializable {
    private static final long serialVersionUID = -8461178506792874057L;
    @ApiModelProperty("主键id")
    private Integer valueId;

    @ApiModelProperty("属性ID")
    private Integer attributeId;

    @ApiModelProperty("创建人ID")
    private Integer createAdminId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("属性值")
    private String attributeValue;
}