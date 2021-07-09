package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.promotion.pojo.FullAmountStepMinus;
import com.slodon.b2b2c.promotion.pojo.FullAsmRule;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装阶梯满减详情VO对象
 * @Author wuxy
 * @date 2020.11.05 12:06
 */
@Data
public class FullAsmDetailVO implements Serializable {

    private static final long serialVersionUID = -3384315523184594105L;
    @ApiModelProperty("活动id")
    private Integer fullId;

    @ApiModelProperty("活动名称")
    private String fullName;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @ApiModelProperty("规则列表")
    private List<FullAsmRuleVO> ruleList;

    public FullAsmDetailVO(FullAmountStepMinus fullAmountStepMinus) {
        fullId = fullAmountStepMinus.getFullId();
        fullName = fullAmountStepMinus.getFullName();
        startTime = fullAmountStepMinus.getStartTime();
        endTime = fullAmountStepMinus.getEndTime();
    }

    @Data
    public static class FullAsmRuleVO implements Serializable {

        private static final long serialVersionUID = -5953649306657577612L;
        @ApiModelProperty("规则id")
        private Integer ruleId;

        @ApiModelProperty("满指定金额")
        private BigDecimal fullValue;

        @ApiModelProperty("减指定金额")
        private BigDecimal minusValue;

        @ApiModelProperty("赠送积分")
        private Integer sendIntegral;

        @ApiModelProperty("优惠券列表")
        private List<FullCouponVO> couponList;

        @ApiModelProperty("赠品列表")
        private List<FullGiftVO> giftList;

        public FullAsmRuleVO(FullAsmRule fullAsmRule) {
            ruleId = fullAsmRule.getRuleId();
            fullValue = fullAsmRule.getFullValue();
            minusValue = fullAsmRule.getMinusValue();
            sendIntegral = fullAsmRule.getSendIntegral();
        }
    }

}
