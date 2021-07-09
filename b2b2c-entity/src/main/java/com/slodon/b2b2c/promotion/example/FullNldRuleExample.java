package com.slodon.b2b2c.promotion.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class FullNldRuleExample implements Serializable {
    private static final long serialVersionUID = 7073433371091116279L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer ruleIdNotEquals;

    /**
     * 用于批量操作
     */
    private String ruleIdIn;

    /**
     * 规则id
     */
    private Integer ruleId;

    /**
     * 阶梯满件折扣活动id
     */
    private Integer fullId;

    /**
     * 满指定件
     */
    private Integer fullValue;

    /**
     * 打指定折扣
     */
    private BigDecimal minusValue;

    /**
     * 赠送积分
     */
    private Integer sendIntegral;

    /**
     * 优惠券id集合
     */
    private String sendCouponIds;

    /**
     * 赠送商品id集合
     */
    private String sendGoodsIds;

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