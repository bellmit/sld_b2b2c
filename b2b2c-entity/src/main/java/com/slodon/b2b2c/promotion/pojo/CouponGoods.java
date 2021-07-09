package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 优惠券和产品的绑定表
 */
@Data
public class CouponGoods implements Serializable {
    private static final long serialVersionUID = -3234041836941452127L;
    @ApiModelProperty("优惠活动商品ID")
    private Integer couponGoodsId;

    @ApiModelProperty("绑定的优惠券ID")
    private Integer couponId;

    @ApiModelProperty("商品SPU ID")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品所属的一级分类；用于快速查询对应类别的优惠券")
    private Integer goodsCategoryId;
}