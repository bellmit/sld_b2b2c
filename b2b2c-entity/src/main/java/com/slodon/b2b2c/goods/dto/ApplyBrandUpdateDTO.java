package com.slodon.b2b2c.goods.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 商户编辑品牌接口参数
 * @author cwl
 */
@Data
public class ApplyBrandUpdateDTO implements Serializable {

    private static final long serialVersionUID = 2434587435487572184L;

    @ApiModelProperty(value ="品牌id",required = true)
    private Integer brandId;

    @ApiModelProperty(value ="品牌名称",required = true)
    private String brandName;

    @ApiModelProperty(value ="品牌描述（一段文字）")
    private String brandDesc;

    @ApiModelProperty(value ="品牌图片",required = true)
    private String image;

    @ApiModelProperty(value ="三级分类id",required = true)
    private Integer categoryId;

}
