package com.slodon.b2b2c.goods.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 规格值表（系统维护）
 */
@Data
public class GoodsSpecValue implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("规格值id")
    private Integer specValueId;

    @ApiModelProperty("规格值")
    private String specValue;

    @ApiModelProperty("规格ID")
    private Integer specId;

    @ApiModelProperty("店铺id，0为系统创建")
    private Long storeId;

    @ApiModelProperty("创建人")
    private Integer createId;

    @ApiModelProperty("创建时间")
    private Date createTime;
}