package com.slodon.b2b2c.vo.business;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: slodon
 * @Description 封装销售额VO对象
 * @Author wuxy
 */
@Data
public class SalesVolumeVO implements Serializable {

    private static final long serialVersionUID = 848921514509451787L;
    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("销售额")
    private BigDecimal moneyAmount;

    @ApiModelProperty("百分比")
    private String per;
}
