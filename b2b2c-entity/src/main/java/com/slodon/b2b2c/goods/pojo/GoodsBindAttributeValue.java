package com.slodon.b2b2c.goods.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品对应属性表(保存商品时插入)
 */
@Data
public class GoodsBindAttributeValue implements Serializable {
    private static final long serialVersionUID = -1520722859754244929L;
    @ApiModelProperty("绑定id")
    private Integer bindId;

    @ApiModelProperty("商品ID")
    private Long goodsId;

    @ApiModelProperty("检索属性ID")
    private Integer attributeId;

    @ApiModelProperty("属性名称")
    private String attributeName;

    @ApiModelProperty("属性值ID")
    private Integer attributeValueId;

    @ApiModelProperty("属性值")
    private String attributeValue;
}