package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.promotion.pojo.SpellGoods;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: slodon
 * @Description 封装拼团商品VO对象
 */
@Data
public class FrontSpellGoodsVO implements Serializable {

    private static final long serialVersionUID = -4468637118691133037L;

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

    @ApiModelProperty("活动库存")
    private Integer spellStock;

    @ApiModelProperty("要求成团人数")
    private Integer requiredNum;

    @ApiModelProperty("拼团购买立省")
    private BigDecimal spellDiscount;

    @ApiModelProperty("团长优惠")
    private BigDecimal leaderDiscount;

    public FrontSpellGoodsVO(SpellGoods spellGoods) {
        spellGoodsId = spellGoods.getSpellGoodsId();
        spellId = spellGoods.getSpellId();
        goodsId = spellGoods.getGoodsId();
        goodsName = spellGoods.getGoodsName();
        goodsBrief = spellGoods.getGoodsBrief();
        goodsImage = FileUrlUtil.getFileUrl(spellGoods.getGoodsImage(), null);
        specValues = spellGoods.getSpecValues();
        productId = spellGoods.getProductId();
        productPrice = spellGoods.getProductPrice();
        spellPrice = spellGoods.getSpellPrice();
        leaderPrice = spellGoods.getLeaderPrice();
        spellStock = spellGoods.getSpellStock();
        spellDiscount = spellGoods.getProductPrice().subtract(spellGoods.getSpellPrice());
        leaderDiscount = spellGoods.getLeaderPrice().compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : spellGoods.getProductPrice().subtract(spellGoods.getLeaderPrice());
    }
}
