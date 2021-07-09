package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.promotion.pojo.FullAldRule;
import com.slodon.b2b2c.promotion.pojo.FullAmountLadderDiscount;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装阶梯满折扣详情VO对象
 * @Author wuxy
 * @date 2020.11.05 14:56
 */
@Data
public class FullAldDetailVO implements Serializable {

    private static final long serialVersionUID = 3686120019434178442L;
    @ApiModelProperty("活动id")
    private Integer fullId;

    @ApiModelProperty("活动名称")
    private String fullName;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @ApiModelProperty("规则列表")
    private List<FullAldRuleVO> ruleList;

    public FullAldDetailVO(FullAmountLadderDiscount fullAmountLadderDiscount) {
        fullId = fullAmountLadderDiscount.getFullId();
        fullName = fullAmountLadderDiscount.getFullName();
        startTime = fullAmountLadderDiscount.getStartTime();
        endTime = fullAmountLadderDiscount.getEndTime();
    }

    @Data
    public static class FullAldRuleVO implements Serializable {

        private static final long serialVersionUID = -5953649306657577612L;
        @ApiModelProperty("规则id")
        private Integer ruleId;

        @ApiModelProperty("满指定金额")
        private BigDecimal fullValue;

        @ApiModelProperty("打指定折扣")
        private BigDecimal minusValue;

        @ApiModelProperty("赠送积分")
        private Integer sendIntegral;

        @ApiModelProperty("优惠券列表")
        private List<FullCouponVO> couponList;

        @ApiModelProperty("赠品列表")
        private List<FullGiftVO> giftList;

        public FullAldRuleVO(FullAldRule fullAldRule) {
            ruleId = fullAldRule.getRuleId();
            fullValue = fullAldRule.getFullValue();
            minusValue = fullAldRule.getMinusValue();
            sendIntegral = fullAldRule.getSendIntegral();
        }
    }

}
