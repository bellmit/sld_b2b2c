package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.promotion.pojo.PresellGoods;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: slodon
 * @description: 封装预售商品下的货品信息VO对象
 * @author: wuxy
 * @create: 2020.11.04 10:00
 **/
@Data
public class PreSellProductVO implements Serializable {

    private static final long serialVersionUID = 1613264183633625113L;
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

    @ApiModelProperty("销量:实际销量+虚拟销量")
    private Integer sale;

    @ApiModelProperty("库存")
    private Integer stock;

    @ApiModelProperty("活动库存")
    private Integer presellStock;

    public PreSellProductVO(Product product) {
        productId = product.getProductId();
        specValues = product.getSpecValues();
        productPrice = product.getProductPrice();
        stock = product.getProductStock();
        presellPrice = product.getProductPrice();
        firstMoney = BigDecimal.ZERO;
        firstExpand = BigDecimal.ZERO;
        presellStock = 0;
    }

    public PreSellProductVO(PresellGoods presellGoods) {
        presellGoodsId=presellGoods.getPresellGoodsId();
        presellId=presellGoods.getPresellId();
        productId = presellGoods.getProductId();
        goodsId = presellGoods.getGoodsId();
        goodsName = presellGoods.getGoodsName();
        goodsImage = FileUrlUtil.getFileUrl(presellGoods.getGoodsImage(), null);
        specValues = presellGoods.getSpecValues();
        productPrice = presellGoods.getProductPrice();
        presellPrice = presellGoods.getPresellPrice();
        firstMoney = presellGoods.getFirstMoney();
        secondMoney = presellGoods.getSecondMoney();
        firstExpand = presellGoods.getFirstExpand();
        presellDescription = presellGoods.getPresellDescription();
        depositCompensate = presellGoods.getDepositCompensate();
        sale = presellGoods.getActualSale()+presellGoods.getVirtualSale();
        stock = presellGoods.getStock();
        presellStock = presellGoods.getPresellStock();
    }
}
