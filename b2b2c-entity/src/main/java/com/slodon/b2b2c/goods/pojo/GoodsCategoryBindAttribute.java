package com.slodon.b2b2c.goods.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 类型与检索属性绑定关系表
 */
@Data
public class GoodsCategoryBindAttribute implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("绑定id")
    private Integer bindId;

    @ApiModelProperty("分类id")
    private Integer categoryId;

    @ApiModelProperty("属性id")
    private Integer attributeId;
}