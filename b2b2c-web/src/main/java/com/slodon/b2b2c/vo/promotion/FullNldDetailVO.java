package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.promotion.pojo.FullNldRule;
import com.slodon.b2b2c.promotion.pojo.FullNumLadderDiscount;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装阶梯满件折扣详情VO对象
 * @Author wuxy
 * @date 2020.11.05 15:56
 */
@Data
public class FullNldDetailVO implements Serializable {

    private static final long serialVersionUID = -3319744025019297225L;
    @ApiModelProperty("活动id")
    private Integer fullId;

    @ApiModelProperty("活动名称")
    private String fullName;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @ApiModelProperty("规则列表")
    private List<FullNldRuleVO> ruleList;

    public FullNldDetailVO(FullNumLadderDiscount fullNumLadderDiscount) {
        fullId = fullNumLadderDiscount.getFullId();
        fullName = fullNumLadderDiscount.getFullName();
        startTime = fullNumLadderDiscount.getStartTime();
        endTime = fullNumLadderDiscount.getEndTime();
    }

    @Data
    public static class FullNldRuleVO implements Serializable {

        @ApiModelProperty("规则id")
        private Integer ruleId;

        @ApiModelProperty("满指定件")
        private Integer fullValue;

        @ApiModelProperty("打指定折扣")
        private BigDecimal minusValue;

        @ApiModelProperty("赠送积分")
        private Integer sendIntegral;

        @ApiModelProperty("优惠券列表")
        private List<FullCouponVO> couponList;

        @ApiModelProperty("赠品列表")
        private List<FullGiftVO> giftList;

        public FullNldRuleVO(FullNldRule fullNldRule) {
            ruleId = fullNldRule.getRuleId();
            fullValue = fullNldRule.getFullValue();
            minusValue = fullNldRule.getMinusValue();
            sendIntegral = fullNldRule.getSendIntegral();
        }
    }

}
