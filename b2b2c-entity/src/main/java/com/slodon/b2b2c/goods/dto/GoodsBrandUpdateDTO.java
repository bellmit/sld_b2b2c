package com.slodon.b2b2c.goods.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author cwl
 */
@Data
public class GoodsBrandUpdateDTO implements Serializable {

    private static final long serialVersionUID = 4858417634557750983L;

    @ApiModelProperty(value ="品牌id",required = true)
    private Integer brandId;

    @ApiModelProperty(value ="品牌名称",required = true)
    private String brandName;

    @ApiModelProperty(value ="品牌描述（一段文字）")
    private String brandDesc;

    @ApiModelProperty(value ="品牌图片",required = true)
    private String image;
}
