package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 拼团活动绑定商品表
 */
@Data
public class SpellGoods implements Serializable {
    private static final long serialVersionUID = -5906942410402573784L;
    @ApiModelProperty("拼团活动商品id")
    private Integer spellGoodsId;

    @ApiModelProperty("拼团活动id编号")
    private Integer spellId;

    @ApiModelProperty("活动商品id（spu）")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品广告语")
    private String goodsBrief;

    @ApiModelProperty("商品图片")
    private String goodsImage;

    @ApiModelProperty("规格值，用逗号分隔")
    private String specValues;

    @ApiModelProperty("活动商品id（sku）")
    private Long productId;

    @ApiModelProperty("商品原价")
    private BigDecimal productPrice;

    @ApiModelProperty("拼团价")
    private BigDecimal spellPrice;

    @ApiModelProperty("团长优惠价")
    private BigDecimal leaderPrice;

    @ApiModelProperty("销量")
    private Integer salesVolume;

    @ApiModelProperty("活动库存")
    private Integer spellStock;
}