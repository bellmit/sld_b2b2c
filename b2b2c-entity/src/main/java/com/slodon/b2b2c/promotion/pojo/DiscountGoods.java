package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;

/**
 * 折扣活动商品关联表
 */
@Data
public class DiscountGoods implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("折扣活动商品ID")
    private Integer discountGoodsId;

    @ApiModelProperty("商品ID")
    private Long goodsId;

    @ApiModelProperty("活动ID")
    private Integer discountId;

    @ApiModelProperty("商户ID")
    private Long storeId;
}