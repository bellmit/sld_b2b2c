package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 阶梯团规则表
 */
@Data
public class LadderGroupRule implements Serializable {
    private static final long serialVersionUID = -1892749955789890822L;

    @ApiModelProperty("阶梯团规则id")
    private Integer ruleId;

    @ApiModelProperty("阶梯团活动id")
    private Integer groupId;

    @ApiModelProperty("阶梯团参团人数")
    private Integer joinGroupNum;

    @ApiModelProperty("阶梯等级")
    private Integer ladderLevel;
}