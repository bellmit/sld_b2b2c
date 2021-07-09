package com.slodon.b2b2c.goods.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品分类与品牌定关系表
 */
@Data
public class GoodsCategoryBindBrand implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("绑定id")
    private Integer bindId;

    @ApiModelProperty("类型id")
    private Integer categoryId;

    @ApiModelProperty("品牌id")
    private Integer brandId;
}