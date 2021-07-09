package com.slodon.b2b2c.vo.promotion;


import com.slodon.b2b2c.promotion.pojo.SeckillStageProduct;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: slodon
 * @Description 封装秒杀场次查看货品VO对象
 */
@Data
public class SeckillStageProductVO implements Serializable {

    private static final long serialVersionUID = 8938385932747849039L;
    @ApiModelProperty("货品id")
    private Long productId;

    @ApiModelProperty("货品价格")
    private BigDecimal productPrice;

    @ApiModelProperty("秒杀价格")
    private BigDecimal seckillPrice;

    @ApiModelProperty("货品秒杀当前库存")
    private Integer seckillStock;

    @ApiModelProperty("限购数量")
    private Integer upperLimit;

    @ApiModelProperty("规格值，用逗号分隔")
    private String specValues;

    @ApiModelProperty("货品库存")
    private Integer productStock;

    public SeckillStageProductVO(SeckillStageProduct seckillStageProduct) {
        this.productId = seckillStageProduct.getProductId();
        this.productPrice = seckillStageProduct.getProductPrice();
        this.seckillPrice = seckillStageProduct.getSeckillPrice();
        this.seckillStock = seckillStageProduct.getSeckillStock();
        this.upperLimit = seckillStageProduct.getUpperLimit();
    }

}
