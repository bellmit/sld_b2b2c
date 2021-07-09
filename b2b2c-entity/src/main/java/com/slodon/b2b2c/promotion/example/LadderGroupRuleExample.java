package com.slodon.b2b2c.promotion.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * 阶梯团规则表example
 */
@Data
public class LadderGroupRuleExample implements Serializable {
    private static final long serialVersionUID = -2158121832601482065L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer ruleIdNotEquals;

    /**
     * 用于批量操作
     */
    private String ruleIdIn;

    /**
     * 阶梯团规则id
     */
    private Integer ruleId;

    /**
     * 阶梯团活动id
     */
    private Integer groupId;

    /**
     * 阶梯团参团人数
     */
    private Integer joinGroupNum;

    /**
     * 阶梯等级
     */
    private Integer ladderLevel;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照ruleId倒序排列
     */
    private String orderBy;

    /**
     * 分组条件
     */
    private String groupBy;

    /**
     * 分页信息
     */
    private PagerInfo pager;
}