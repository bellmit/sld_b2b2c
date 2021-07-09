package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 秒杀商品表
 */
@Data
public class SeckillStageProduct implements Serializable {

    private static final long serialVersionUID = 971043366737660867L;
    @ApiModelProperty("id")
    private Integer stageProductId;

    @ApiModelProperty("开始时间")
    private Date startTime;

    @ApiModelProperty("结束时间")
    private Date endTime;

    @ApiModelProperty("货品ID")
    private Long productId;

    @ApiModelProperty("商品ID")
    private Long goodsId;

    @ApiModelProperty("商品主图路径，每个SPU一张主图")
    private String mainImage;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商家ID")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("货品价格")
    private BigDecimal productPrice;

    @ApiModelProperty("秒杀价格")
    private BigDecimal seckillPrice;

    @ApiModelProperty("货品秒杀当前库存")
    private Integer seckillStock;

    @ApiModelProperty("限购数量")
    private Integer upperLimit;

    @ApiModelProperty("已购买人数")
    private Integer buyerCount;

    @ApiModelProperty("购买数量")
    private Integer buyQuantity;

    @ApiModelProperty("状态 1待审核 2审核通过，3拒绝")
    private Integer verifyState;

    @ApiModelProperty("审核拒绝理由")
    private String remark;

    @ApiModelProperty("活动id")
    private Integer seckillId;

    @ApiModelProperty("活动名称")
    private String seckillName;

    @ApiModelProperty("场次id")
    private Integer stageId;

    @ApiModelProperty("场次名称")
    private String stageName;

    @ApiModelProperty("标签id")
    private Integer labelId;

    @ApiModelProperty("标签名称")
    private String labelName;

    @ApiModelProperty("货品库存")
    private Integer productStock;

    @ApiModelProperty("秒杀活动状态(1-未开始 2-进行中 3-已结束)")
    private Integer state;
}