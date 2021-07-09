package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 阶梯满折扣活动规则
 */
@Data
public class FullAldRule implements Serializable {
    private static final long serialVersionUID = -4324877556335071714L;
    @ApiModelProperty("规则id")
    private Integer ruleId;

    @ApiModelProperty("阶梯满折扣活动id")
    private Integer fullId;

    @ApiModelProperty("满指定金额")
    private BigDecimal fullValue;

    @ApiModelProperty("打指定折扣")
    private BigDecimal minusValue;

    @ApiModelProperty("赠送积分")
    private Integer sendIntegral;

    @ApiModelProperty("优惠券id集合")
    private String sendCouponIds;

    @ApiModelProperty("赠送商品id集合")
    private String sendGoodsIds;
}