package com.slodon.b2b2c.goods.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品标签
 */
@Data
public class GoodsLabelAddDTO implements Serializable {

    private static final long serialVersionUID = 8684723449208103379L;
    @ApiModelProperty(value = "标签名称",required = true)
    private String labelName;

    @ApiModelProperty(value = "标签描述",required = true)
    private String description;

    @ApiModelProperty(value = "排序",required = true)
    private Integer sort;

}