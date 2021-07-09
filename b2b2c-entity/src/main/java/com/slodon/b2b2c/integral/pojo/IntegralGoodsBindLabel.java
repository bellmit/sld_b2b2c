package com.slodon.b2b2c.integral.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 积分商品绑定标签表
 */
@Data
public class IntegralGoodsBindLabel implements Serializable {
    private static final long serialVersionUID = -4559010492451220538L;
    @ApiModelProperty("绑定id")
    private Integer bindId;

    @ApiModelProperty("商品id")
    private Long goodsId;

    @ApiModelProperty("一级标签id")
    private Integer labelId1;

    @ApiModelProperty("二级标签id")
    private Integer labelId2;

    @ApiModelProperty("标签等级")
    private Integer grade;

    @ApiModelProperty("标签路径")
    private String labelPath;
}