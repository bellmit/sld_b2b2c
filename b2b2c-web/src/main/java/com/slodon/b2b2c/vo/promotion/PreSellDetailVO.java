package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.promotion.pojo.Presell;
import com.slodon.b2b2c.promotion.pojo.PresellGoods;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装预售详情VO对象
 * @Author wuxy
 * @date 2020.11.04 09:41
 */
@Data
public class PreSellDetailVO implements Serializable {

    private static final long serialVersionUID = 912344821455610856L;
    @ApiModelProperty("预售活动id")
    private Integer presellId;

    @ApiModelProperty("预售活动名称")
    private String presellName;

    @ApiModelProperty("预售活动标签id")
    private Integer presellLabelId;

    @ApiModelProperty("预售活动标签名称")
    private String presellLabelName;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @ApiModelProperty("预售类型（1-定金预售，2-全款预售）")
    private Integer type;

    @ApiModelProperty("最大限购数量；0为不限购")
    private Integer buyLimit;

    @ApiModelProperty("最大限购数量；0为不限购")
    private String buyLimitValue;

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

    @ApiModelProperty("预售商品列表")
    private List<PresellGoodsInfo> goodsList;

    public PreSellDetailVO(Presell presell) {
        presellId = presell.getPresellId();
        presellName = presell.getPresellName();
        presellLabelId = presell.getPresellLabelId();
        presellLabelName = presell.getPresellLabelName();
        startTime = presell.getStartTime();
        endTime = presell.getEndTime();
        type = presell.getType();
        buyLimit = presell.getBuyLimit();
        buyLimitValue = presell.getBuyLimit() == 0 ? "不限购" : presell.getBuyLimit() + "件";
        deliverTimeType = presell.getDeliverTimeType();
        deliverTime = presell.getDeliverTime();
        remainStartTime = presell.getRemainStartTime();
        remainEndTime = presell.getRemainEndTime();
        deliverStartTime = presell.getDeliverStartTime();
    }

    @Data
    public static class PresellGoodsInfo implements Serializable {

        private static final long serialVersionUID = 3969166479653447556L;
        @ApiModelProperty("商品ID")
        private Long goodsId;

        @ApiModelProperty("商品名称")
        private String goodsName;

        @ApiModelProperty("商品名称")
        private String goodsImage;

        @ApiModelProperty("预售商品规格列表")
        private List<PreSellProductVO> productList;

        public PresellGoodsInfo(PresellGoods presellGoods) {
            goodsId = presellGoods.getGoodsId();
            goodsName = presellGoods.getGoodsName();
            goodsImage = FileUrlUtil.getFileUrl(presellGoods.getGoodsImage(), null);
        }

        @Data
        public static class PreSellProductVO implements Serializable {

            private static final long serialVersionUID = 1167512397774852296L;
            @ApiModelProperty("货品id")
            private long productId;

            @ApiModelProperty("SKU规格")
            private String specValues;

            @ApiModelProperty("商品原价")
            private BigDecimal productPrice;

            @ApiModelProperty("库存")
            private Integer stock;

            @ApiModelProperty("预售价格")
            private BigDecimal presellPrice;

            @ApiModelProperty("预售定金")
            private BigDecimal firstMoney;

            @ApiModelProperty("定金膨胀（订金可以抵现的金额）")
            private BigDecimal firstExpand;

            @ApiModelProperty("预售库存")
            private Integer presellStock;

            public PreSellProductVO(PresellGoods presellGoods) {
                productId = presellGoods.getProductId();
                specValues = presellGoods.getSpecValues();
                productPrice = presellGoods.getProductPrice();
                presellPrice = presellGoods.getPresellPrice();
                firstMoney = presellGoods.getFirstMoney();
                firstExpand = presellGoods.getFirstExpand();
                presellStock = presellGoods.getPresellStock();
            }
        }
    }

}
