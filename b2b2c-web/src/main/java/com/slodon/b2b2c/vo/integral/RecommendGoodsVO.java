package com.slodon.b2b2c.vo.integral;

import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.integral.pojo.IntegralGoods;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 封装推荐商品VO对象
 */
@Data
public class RecommendGoodsVO implements Serializable {

    private static final long serialVersionUID = -4867146007536378282L;
    @ApiModelProperty("商品id")
    private Long integralGoodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品副标题")
    private String goodsBrief;

    @ApiModelProperty("商品图片")
    private String goodsImage;

    @ApiModelProperty("市场价")
    private BigDecimal marketPrice;

    @ApiModelProperty("积分")
    private Integer integralPrice;

    @ApiModelProperty("现金")
    private BigDecimal cashPrice;

    @ApiModelProperty("默认货品id")
    private Long defaultProductId;

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    public RecommendGoodsVO(IntegralGoods integralGoods) {
        this.integralGoodsId = integralGoods.getIntegralGoodsId();
        this.goodsName = integralGoods.getGoodsName();
        this.goodsBrief = integralGoods.getGoodsBrief();
        this.goodsImage = FileUrlUtil.getFileUrl(integralGoods.getMainImage(), null);
        this.marketPrice = integralGoods.getMarketPrice();
        this.integralPrice = integralGoods.getIntegralPrice();
        this.cashPrice = integralGoods.getCashPrice();
        this.defaultProductId = integralGoods.getDefaultProductId();
        this.storeId = integralGoods.getStoreId();
        this.storeName = integralGoods.getStoreName();
    }
}
