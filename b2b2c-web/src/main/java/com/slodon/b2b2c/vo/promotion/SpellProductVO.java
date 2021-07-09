package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.promotion.pojo.SpellGoods;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: slodon
 * @description: 封装拼团商品下的货品信息VO对象
 * @author: wuxy
 * @create: 2020.11.04 19:40
 **/
@Data
public class SpellProductVO implements Serializable {

    private static final long serialVersionUID = -7814338869004022127L;
    @ApiModelProperty("活动商品id（sku）")
    private long productId;

    @ApiModelProperty("SKU规格")
    private String specValues;

    @ApiModelProperty("商品原价")
    private BigDecimal productPrice;

    @ApiModelProperty("库存")
    private Integer stock;

    @ApiModelProperty("拼团价")
    private BigDecimal spellPrice;

    @ApiModelProperty("团长优惠价")
    private BigDecimal leaderPrice;

    @ApiModelProperty("拼团库存")
    private Integer spellStock;

    public SpellProductVO(Product product) {
        productId = product.getProductId();
        specValues = product.getSpecValues();
        productPrice = product.getProductPrice();
        stock = product.getProductStock();
        spellPrice = product.getProductPrice();
        leaderPrice = product.getProductPrice();
        spellStock = 0;
    }

    public SpellProductVO(SpellGoods spellGoods) {
        productId = spellGoods.getProductId();
        specValues = spellGoods.getSpecValues();
        productPrice = spellGoods.getProductPrice();
        spellPrice = spellGoods.getSpellPrice();
        leaderPrice = spellGoods.getLeaderPrice();
        spellStock = spellGoods.getSpellStock();
    }
}
