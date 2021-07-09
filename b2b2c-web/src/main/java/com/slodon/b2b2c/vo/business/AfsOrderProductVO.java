package com.slodon.b2b2c.vo.business;

import com.slodon.b2b2c.business.pojo.OrderProduct;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 可申请售后的订单货品vo
 */
@Data
public class AfsOrderProductVO implements Serializable {

    private static final long serialVersionUID = 1012252472251127312L;
    @ApiModelProperty("订单货品id")
    private Long orderProductId;

    @ApiModelProperty("订单号")
    private String orderSn;

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

    @ApiModelProperty("商品数量")
    private Integer productNum;

    @ApiModelProperty("使用积分数量")
    private Integer integral;

    @ApiModelProperty("订单货品明细金额（用户支付金额，发生退款时最高可退金额）（=货品单价*数量-活动优惠总额）")
    private BigDecimal moneyAmount;

    @ApiModelProperty("是否是赠品，0、不是；1、是；")
    private Integer isGift;

    public AfsOrderProductVO(OrderProduct orderProduct) {
        orderProductId = orderProduct.getOrderProductId();
        orderSn = orderProduct.getOrderSn();
        goodsId = orderProduct.getGoodsId();
        goodsName = orderProduct.getGoodsName();
        productImage = FileUrlUtil.getFileUrl(orderProduct.getProductImage(), null);
        specValues = orderProduct.getSpecValues();
        productId = orderProduct.getProductId();
        productNum = orderProduct.getProductNum();
        integral = orderProduct.getIntegral();
        moneyAmount = orderProduct.getMoneyAmount();
        isGift = orderProduct.getIsGift();
    }
}
