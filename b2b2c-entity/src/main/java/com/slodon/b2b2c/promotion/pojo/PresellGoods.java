package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 预售活动商品表
 */
@Data
public class PresellGoods implements Serializable {
    private static final long serialVersionUID = 8652554644421861246L;
    @ApiModelProperty("预售活动商品id")
    private Integer presellGoodsId;

    @ApiModelProperty("预售活动id编号")
    private Integer presellId;

    @ApiModelProperty("活动商品id(sku)")
    private Long productId;

    @ApiModelProperty("活动商品id(spu)")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品图片")
    private String goodsImage;

    @ApiModelProperty("规格值，用逗号分隔")
    private String specValues;

    @ApiModelProperty("商品原价")
    private BigDecimal productPrice;

    @ApiModelProperty("预售价格")
    private BigDecimal presellPrice;

    @ApiModelProperty("预售定金（全款预售不需要此项，定金预售需要）")
    private BigDecimal firstMoney;

    @ApiModelProperty("预售尾款（全款预售不需要此项，定金预售需要）")
    private BigDecimal secondMoney;

    @ApiModelProperty("订金可以抵现的金额（全款预售不需要此项，定金预售需要）")
    private BigDecimal firstExpand;

    @ApiModelProperty("预售广告语")
    private String presellDescription;

    @ApiModelProperty("赔偿金额（一般为定金指定倍数，倍数由平台设置）")
    private Integer depositCompensate;

    @ApiModelProperty("实际销量")
    private Integer actualSale;

    @ApiModelProperty("虚拟销量")
    private Integer virtualSale;

    @ApiModelProperty("库存")
    private Integer stock;

    @ApiModelProperty("活动库存")
    private Integer presellStock;
}