package com.slodon.b2b2c.business.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 活动和订单商品的扩展关系表
 */
@Data
public class OrderProductExtend implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("扩展id")
    private Integer extendId;

    @ApiModelProperty("订单货品id")
    private Long orderProductId;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("活动等级 1-商品活动；2-店铺活动；3-平台活动;4-积分、优惠券")
    private Integer promotionGrade;

    @ApiModelProperty("活动类型  ")
    private Integer promotionType;

    @ApiModelProperty("活动id")
    private String promotionId;

    @ApiModelProperty("优惠金额")
    private BigDecimal promotionAmount;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("是否店铺活动，1-是，0-否")
    private Integer isStore;
}