package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.promotion.pojo.SeckillStageProduct;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: slodon
 * @Description 封装秒杀货品VO对象
 */
@Data
public class SellerSeckillProductVO implements Serializable {

    private static final long serialVersionUID = 7950608732104770396L;
    @ApiModelProperty("货品id")
    private Long productId;

    @ApiModelProperty("货品原价格")
    private BigDecimal productPrice;

    @ApiModelProperty("秒杀价格")
    private BigDecimal seckillPrice;

    @ApiModelProperty("货品秒杀库存")
    private Integer seckillStock;

    @ApiModelProperty("限购数量")
    private Integer upperLimit;

    @ApiModelProperty("原库存")
    private Integer productStock;

    @ApiModelProperty("规格值，用逗号分隔")
    private String specValues;

    public SellerSeckillProductVO(SeckillStageProduct seckillStageProduct) {
        this.productId = seckillStageProduct.getProductId();
        this.productPrice = seckillStageProduct.getProductPrice();
        this.seckillPrice = seckillStageProduct.getSeckillPrice();
        this.seckillStock = seckillStageProduct.getSeckillStock();
        this.upperLimit = seckillStageProduct.getUpperLimit();
    }
}
