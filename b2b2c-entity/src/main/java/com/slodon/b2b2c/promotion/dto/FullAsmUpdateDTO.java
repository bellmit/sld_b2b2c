package com.slodon.b2b2c.promotion.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: slodon
 * @Description 阶梯满减编辑DTO
 * @Author wuxy
 * @date 2020.11.05 14:00
 */
@Data
public class FullAsmUpdateDTO implements Serializable {

    private static final long serialVersionUID = 6064542910756421791L;
    @ApiModelProperty(value = "阶梯满减活动id", required = true)
    private Integer fullId;

    @ApiModelProperty(value = "阶梯满减活动名称", required = true)
    private String fullName;

    @ApiModelProperty(value = "活动开始时间", required = true)
    private Date startTime;

    @ApiModelProperty(value = "活动结束时间", required = true)
    private Date endTime;

    @ApiModelProperty(value = "阶梯满减规则，json字符串([{\"fullValue\":100,\"minusValue\":50,\"sendIntegral\":20,\"sendCouponIds\":\"1,2,3\",\"sendGoodsIds\":\"11,12,13\"}])", required = true)
    private String ruleJson;
}
