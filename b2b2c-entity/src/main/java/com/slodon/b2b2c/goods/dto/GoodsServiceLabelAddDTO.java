package com.slodon.b2b2c.goods.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品标签
 */
@Data
public class GoodsServiceLabelAddDTO implements Serializable {

    private static final long serialVersionUID = 2684323534645645745L;
    @ApiModelProperty(value = "标签名称",required = true)
    private String labelName;

    @ApiModelProperty(value = "标签描述",required = true)
    private String description;

}