package com.slodon.b2b2c.goods.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品图片参数
 */
@Data
public class GoodsPictureAddDTO implements Serializable {
    private static final long serialVersionUID = 7494690922834738530L;

    @ApiModelProperty(value = "商品主spec_value_id，对于没有启用主规格的此值为0",required = true)
    private Integer specValueId;

    @ApiModelProperty(value ="图片路径,第一张为主图",required = true)
    private String[] imagePath;
}