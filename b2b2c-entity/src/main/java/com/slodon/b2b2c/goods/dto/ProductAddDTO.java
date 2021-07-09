package com.slodon.b2b2c.goods.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品表（SKU），指定特定规格
 */
@Data
public class ProductAddDTO implements Serializable {
    private static final long serialVersionUID = 454850847372893214L;

    @ApiModelProperty("规格值，用逗号分隔")
    private String specValues;

    @ApiModelProperty("规格值的ID，用逗号分隔")
    private String specValueIds;

    @ApiModelProperty("货品货号：即SKU ID（店铺内唯一）")
    private String productCode;

    @ApiModelProperty(value="货品价格")
    private BigDecimal productPrice;

    @ApiModelProperty("市场价")
    private BigDecimal marketPrice;

    @ApiModelProperty("商品条形码（标准的商品条形码）")
    private String barCode;

    @ApiModelProperty(value="库存")
    private Integer productStock;

    @ApiModelProperty(value="库存预警值")
    private Integer productStockWarning;

    @ApiModelProperty(value="重量kg")
    private BigDecimal weight;

    @ApiModelProperty(value="长度cm")
    private BigDecimal length;

    @ApiModelProperty(value="宽度cm")
    private BigDecimal width;

    @ApiModelProperty(value="高度cm")
    private BigDecimal height;


    @ApiModelProperty(value="是否默认展示或加车货品：0-否；1-是")
    private Integer isDefault;

}