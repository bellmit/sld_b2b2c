package com.slodon.b2b2c.goods.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品活动绑定历史信息
 */
@Data
public class GoodsPromotionHistory implements Serializable {
    private static final long serialVersionUID = -1729527230714321889L;
    @ApiModelProperty("活动历史id")
    private Integer historyId;

    @ApiModelProperty("商品活动id")
    private Integer goodsPromotionId;

    @ApiModelProperty("绑定时间")
    private Date bindTime;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("商品id（spu）")
    private Long goodsId;

    @ApiModelProperty("货品id（sku）")
    private Long productId;

    @ApiModelProperty("活动id")
    private Integer promotionId;

    @ApiModelProperty("活动类型")
    private Integer promotionType;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @ApiModelProperty("描述信息")
    private String description;
}