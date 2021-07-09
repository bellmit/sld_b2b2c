package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 秒杀订单表
 */
@Data
public class SeckillOrderExtend implements Serializable {

    private static final long serialVersionUID = 6275623290788921231L;
    @ApiModelProperty("主键索引")
    private Integer externId;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("秒杀商品记录id")
    private Integer seckillId;

    @ApiModelProperty("秒杀价")
    private BigDecimal seckillPrice;

    @ApiModelProperty("场次")
    private Integer stageId;

    @ApiModelProperty(" 场次货品绑定id")
    private Integer stageProductstageProductId;
}