package com.slodon.b2b2c.integral.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单货品明细表
 */
@Data
public class IntegralOrderProduct implements Serializable {
    private static final long serialVersionUID = -1620200701077215293L;
    @ApiModelProperty("订单货品id")
    private Long orderProductId;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("商家ID")
    private Long storeId;

    @ApiModelProperty("商家名称")
    private String storeName;

    @ApiModelProperty("会员ID")
    private Integer memberId;

    @ApiModelProperty("商品id")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("货品图片")
    private String productImage;

    @ApiModelProperty("规格详情")
    private String specValues;

    @ApiModelProperty("货品ID")
    private Long productId;

    @ApiModelProperty("价格组合的最多使用积分(基础积分，可以减少积分增加现金），不能为零")
    private Integer integralPrice;

    @ApiModelProperty("价格组合最少使用现金(基础现金，可以增加现金，减少积分）为零则只显示积分")
    private BigDecimal cashPrice;

    @ApiModelProperty("商品数量")
    private Integer productNum;

    @ApiModelProperty("支付使用积分数量")
    private Integer integral;

    @ApiModelProperty("积分抵扣金额")
    private BigDecimal integralCashAmount;

    @ApiModelProperty("现金支付金额")
    private BigDecimal cashAmount;
}