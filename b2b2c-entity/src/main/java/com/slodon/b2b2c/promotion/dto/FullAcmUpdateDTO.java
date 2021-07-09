package com.slodon.b2b2c.promotion.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: slodon
 * @Description 循环满减新增DTO
 * @Author wuxy
 * @date 2020.11.05 10:40
 */
@Data
public class FullAcmUpdateDTO implements Serializable {

    private static final long serialVersionUID = 2305889507086501230L;
    @ApiModelProperty(value = "循环满减活动id", required = true)
    private Integer fullId;

    @ApiModelProperty(value = "循环满减活动名称", required = true)
    private String fullName;

    @ApiModelProperty(value = "循环满减活动开始时间", required = true)
    private Date startTime;

    @ApiModelProperty(value = "循环满减活动结束时间", required = true)
    private Date endTime;

    @ApiModelProperty(value = "满指定金额", required = true)
    private BigDecimal fullValue;

    @ApiModelProperty(value = "减指定金额", required = true)
    private BigDecimal minusValue;

    @ApiModelProperty("赠送积分")
    private Integer sendIntegral;

    @ApiModelProperty("优惠券id集合")
    private String sendCouponIds;

    @ApiModelProperty("赠送商品id集合")
    private String sendGoodsIds;
}
