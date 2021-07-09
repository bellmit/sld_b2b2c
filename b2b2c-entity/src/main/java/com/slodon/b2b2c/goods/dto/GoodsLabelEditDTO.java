package com.slodon.b2b2c.goods.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品标签
 */
@Data
public class GoodsLabelEditDTO implements Serializable {

    private static final long serialVersionUID = 6253444536854098612L;

    @ApiModelProperty("标签id")
    private Integer labelId;

    @ApiModelProperty("标签名称")
    private String labelName;

    @ApiModelProperty("标签描述")
    private String description;

    @ApiModelProperty("排序")
    private Integer sort;

}