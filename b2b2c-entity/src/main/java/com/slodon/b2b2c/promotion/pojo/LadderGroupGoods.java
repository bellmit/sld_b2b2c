package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 阶梯团商品表
 */
@Data
public class LadderGroupGoods implements Serializable {
    private static final long serialVersionUID = -3930690813687009654L;

    @ApiModelProperty("阶梯团商品id")
    private Integer groupGoodsId;

    @ApiModelProperty("阶梯团活动id")
    private Integer groupId;

    @ApiModelProperty("商品id（spu）")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品广告语")
    private String goodsBrief;

    @ApiModelProperty("商品图片")
    private String goodsImage;

    @ApiModelProperty("规格值，用逗号分隔")
    private String specValues;

    @ApiModelProperty("货品id（sku）")
    private Long productId;

    @ApiModelProperty("商品原价")
    private BigDecimal productPrice;

    @ApiModelProperty("库存")
    private Integer stock;

    @ApiModelProperty("销量")
    private Integer salesVolume;

    @ApiModelProperty("预付定金")
    private BigDecimal advanceDeposit;

    @ApiModelProperty("第一阶梯价格或折扣")
    private BigDecimal ladderPrice1;

    @ApiModelProperty("第二阶梯价格或折扣")
    private BigDecimal ladderPrice2;

    @ApiModelProperty("第三阶梯价格或折扣")
    private BigDecimal ladderPrice3;
}