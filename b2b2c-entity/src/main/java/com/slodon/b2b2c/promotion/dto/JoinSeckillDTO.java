package com.slodon.b2b2c.promotion.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @program: slodon
 * @Description 秒杀新增DTO
 */
@Data
public class JoinSeckillDTO implements Serializable {

    private static final long serialVersionUID = 1930217316760461933L;
    @ApiModelProperty(value = "秒杀活动id", required = true)
    private Integer seckillId;

    @ApiModelProperty(value = "秒杀场次id", required = true)
    private Integer stageId;

    @ApiModelProperty(value = "活动标签id", required = true)
    private Integer labelId;

    @ApiModelProperty(value = "商品秒杀结束时间")
    private Date endTime;

    @ApiModelProperty(value = "选择商品列表", required = true)
    private List<GoodsInfo> goodsInfoList;

    /**
     * 商品内部类
     */
    @Data
    public static class GoodsInfo implements Serializable {

        private static final long serialVersionUID = 3415892624848283587L;
        @ApiModelProperty(value = "商品id")
        private Long goodsId;
        @ApiModelProperty(value = "货品列表")
        private List<ProductInfo> productInfoList;
    }

    /**
     * 货品内部类
     */
    @Data
    public static class ProductInfo implements Serializable {

        private static final long serialVersionUID = 4572539577799047896L;
        @ApiModelProperty(value = "货品id")
        private Long ProductId;
        @ApiModelProperty(value = "秒杀价格")
        private BigDecimal seckillPrice;
        @ApiModelProperty(value = "秒杀库存")
        private Integer seckillStock;
        @ApiModelProperty(value = "限购数量")
        private Integer upperLimit;
    }
}