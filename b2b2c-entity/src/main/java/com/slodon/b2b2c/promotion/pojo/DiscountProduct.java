package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 折扣活动货品关联表
 */
@Data
public class DiscountProduct implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("折扣活动货品ID")
    private Integer discountProductId;

    @ApiModelProperty("商品ID")
    private Long goodsId;

    @ApiModelProperty("货品ID")
    private Long productId;

    @ApiModelProperty("商户ID")
    private Long storeId;

    @ApiModelProperty("是")
    private Integer isCustomPrice;

    @ApiModelProperty("如果为按比例折扣，此价格为空，折扣活动价格")
    private BigDecimal discountPrice;

    @ApiModelProperty("规格属性值ID，用逗号分隔")
    private String specAttrId;

    @ApiModelProperty("规格值，用逗号分隔")
    private String specName;

    @ApiModelProperty("库存")
    private Integer stock;

    @ApiModelProperty("库存预警值")
    private Integer stockWarning;

    @ApiModelProperty("是否启用 1-启用 0-不启用")
    private Integer state;

    @ApiModelProperty("重量kg")
    private BigDecimal weight;

    @ApiModelProperty("体积立方厘米;长(CM)×宽(CM)×高(CM)")
    private Long volume;
}