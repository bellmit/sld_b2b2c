package com.slodon.b2b2c.vo.business;

import com.slodon.b2b2c.business.pojo.OrderProduct;
import com.slodon.b2b2c.core.constant.ImageSizeEnum;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: slodon
 * @Description 订单货品VO对象
 * @Author wuxy
 */
@Data
public class ChatOrderProductVO implements Serializable {

    private static final long serialVersionUID = -6883153747131368921L;
    @ApiModelProperty("订单货品id")
    private Long orderProductId;

    @ApiModelProperty("商品id")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("规格详情")
    private String specValues;

    @ApiModelProperty("货品ID")
    private Long productId;

    @ApiModelProperty("货品单价，与订单表中goods_amount对应")
    private BigDecimal productShowPrice;

    @ApiModelProperty("商品数量")
    private Integer productNum;

    @ApiModelProperty("商品图片")
    private String productImage;

    @ApiModelProperty("订单货品明细金额（用户支付金额，发生退款时最高可退金额）（=货品单价*数量-活动优惠总额）")
    private BigDecimal moneyAmount;

    public ChatOrderProductVO(OrderProduct orderProduct) {
        orderProductId = orderProduct.getOrderProductId();
        goodsId = orderProduct.getGoodsId();
        goodsName = orderProduct.getGoodsName();
        specValues = orderProduct.getSpecValues();
        productId = orderProduct.getProductId();
        productShowPrice = orderProduct.getProductShowPrice();
        productNum = orderProduct.getProductNum();
        moneyAmount = orderProduct.getMoneyAmount();
        productImage = FileUrlUtil.getFileUrl(orderProduct.getProductImage(), ImageSizeEnum.SMALL);
    }

}
