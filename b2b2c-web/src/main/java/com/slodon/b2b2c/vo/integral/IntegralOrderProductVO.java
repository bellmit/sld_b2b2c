package com.slodon.b2b2c.vo.integral;

import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.integral.pojo.IntegralOrderProduct;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 封装积分订单货品Vo对象
 */
@Data
public class IntegralOrderProductVO implements Serializable {

    private static final long serialVersionUID = 4617205085023393378L;
    @ApiModelProperty("订单货品id")
    private Long orderProductId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品数量")
    private Integer productNum;

    @ApiModelProperty("商品id")
    private Long goodsId;

    @ApiModelProperty("货品id")
    private Long productId;

    @ApiModelProperty("规格详情")
    private String specValues;

    @ApiModelProperty("商品主图")
    private String productImage;

    @ApiModelProperty("积分")
    private Integer integralPrice;

    @ApiModelProperty("现金")
    private BigDecimal cashPrice;

    @ApiModelProperty("支付使用积分数量")
    private Integer integral;

    @ApiModelProperty("现金支付金额")
    private BigDecimal cashAmount;

    public IntegralOrderProductVO(IntegralOrderProduct integralOrderProduct) {
        orderProductId = integralOrderProduct.getOrderProductId();
        goodsName = integralOrderProduct.getGoodsName();
        productNum = integralOrderProduct.getProductNum();
        goodsId = integralOrderProduct.getGoodsId();
        productId = integralOrderProduct.getProductId();
        specValues = integralOrderProduct.getSpecValues();
        productImage = FileUrlUtil.getFileUrl(integralOrderProduct.getProductImage(), null);
        integralPrice = integralOrderProduct.getIntegralPrice();
        cashPrice = integralOrderProduct.getCashPrice();
        integral = integralOrderProduct.getIntegral();
        cashAmount = integralOrderProduct.getCashAmount();
    }
}
