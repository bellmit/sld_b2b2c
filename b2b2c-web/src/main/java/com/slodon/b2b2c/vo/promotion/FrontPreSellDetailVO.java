package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.promotion.pojo.Presell;
import com.slodon.b2b2c.promotion.pojo.PresellGoods;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: slodon
 * @Description 封装预售商品详情
 * @Author wuxy
 */
@Data
public class FrontPreSellDetailVO implements Serializable {

    private static final long serialVersionUID = 2491152691653722713L;
    @ApiModelProperty("预售活动id")
    private Integer presellId;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("预售类型（1-定金预售，2-全款预售）")
    private Integer type;

    @ApiModelProperty("最大限购数量；0为不限购")
    private Integer buyLimit;

    @ApiModelProperty("发货时间类型（1-固定日期，2-固定天数）")
    private Integer deliverTimeType;

    @ApiModelProperty("发货时间")
    private Date deliverTime;

    @ApiModelProperty("支付尾款的开始时间")
    private Date remainStartTime;

    @ApiModelProperty("支付尾款的结束时间")
    private Date remainEndTime;

    @ApiModelProperty("发货开始时间(以天数为单位)")
    private Integer deliverStartTime;

    @ApiModelProperty("活动商品id(sku)")
    private Long productId;

    @ApiModelProperty("活动商品id(spu)")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品广告语")
    private String goodsBrief;

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

    @ApiModelProperty("订金可以抵现的金额（全款预售不需要此项，定金预售需要）")
    private BigDecimal firstExpand;

    @ApiModelProperty("销量")
    private Integer saleNum;

    @ApiModelProperty("库存")
    private Integer productStock;

    @ApiModelProperty("活动库存")
    private Integer presellStock;

    @ApiModelProperty("距离结束时间")
    private Long distanceEndTime;

    @ApiModelProperty("已购买数量")
    private Integer purchasedNum = 0;

    @ApiModelProperty("是否可以购买:true-可以,false-不可以")
    private Boolean isCanBuy = true;

    public FrontPreSellDetailVO(Presell presell, PresellGoods presellGoods) {
        this.presellId = presell.getPresellId();
        this.startTime = presell.getStartTime();
        this.endTime = presell.getEndTime();
        this.storeId = presell.getStoreId();
        this.storeName = presell.getStoreName();
        this.type = presell.getType();
        this.buyLimit = presell.getBuyLimit();
        deliverTimeType = presell.getDeliverTimeType();
        this.deliverTime = presell.getDeliverTime();
        remainStartTime = presell.getRemainStartTime();
        remainEndTime = presell.getRemainEndTime();
        deliverStartTime = presell.getDeliverStartTime();
        this.productId = presellGoods.getProductId();
        this.goodsId = presellGoods.getGoodsId();
        this.goodsName = presellGoods.getGoodsName();
        this.goodsBrief = presellGoods.getPresellDescription();
        this.goodsImage = FileUrlUtil.getFileUrl(presellGoods.getGoodsImage(), null);
        this.specValues = presellGoods.getSpecValues();
        this.productPrice = presellGoods.getProductPrice();
        this.presellPrice = presellGoods.getPresellPrice();
        this.firstMoney = presellGoods.getFirstMoney();
        this.firstExpand = presellGoods.getFirstExpand();
        this.saleNum = presellGoods.getActualSale() + presellGoods.getVirtualSale();
        this.productStock = presellGoods.getStock();
        this.presellStock = presellGoods.getPresellStock();
    }
}
