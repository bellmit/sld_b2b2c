package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 阶梯满金额减活动规则
 */
@Data
public class FullAsmRule implements Serializable {
    private static final long serialVersionUID = 1407023869086379548L;
    @ApiModelProperty("规则id")
    private Integer ruleId;

    @ApiModelProperty("阶梯满减活动id")
    private Integer fullId;

    @ApiModelProperty("满指定金额")
    private BigDecimal fullValue;

    @ApiModelProperty("减指定金额")
    private BigDecimal minusValue;

    @ApiModelProperty("赠送积分")
    private Integer sendIntegral;

    @ApiModelProperty("优惠券id集合")
    private String sendCouponIds;

    @ApiModelProperty("赠送商品id集合")
    private String sendGoodsIds;
}