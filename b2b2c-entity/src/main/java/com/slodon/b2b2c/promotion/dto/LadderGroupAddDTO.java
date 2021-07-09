package com.slodon.b2b2c.promotion.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @program: slodon
 * @Description 阶梯团新增DTO
 * @Author wuxy
 */
@Data
public class LadderGroupAddDTO implements Serializable {

    private static final long serialVersionUID = 781892062155101372L;
    @ApiModelProperty(value = "阶梯团活动名称", required = true)
    private String groupName;

    @ApiModelProperty(value = "活动开始时间", required = true)
    private Date startTime;

    @ApiModelProperty(value = "活动结束时间", required = true)
    private Date endTime;

    @ApiModelProperty(value = "活动标签id", required = true)
    private Integer labelId;

    @ApiModelProperty(value = "尾款时间(活动结束多少小时内需要支付尾款)", required = true)
    private Integer balanceTime;

    @ApiModelProperty(value = "限购件数，0为不限购", required = true)
    private Integer buyLimitNum;

    @ApiModelProperty(value = "是否退还定金：1-是；0-否", required = true)
    private Integer isRefundDeposit;

    @ApiModelProperty(value = "阶梯优惠方式：1-阶梯价格；2-阶梯折扣", required = true)
    private Integer discountType;

    @ApiModelProperty(value = "货品列表信息", required = true)
    private List<LadderGroupProduct> productList;

    @ApiModelProperty(value = "阶梯团规则列表", required = true)
    private List<LadderGroupRuleInfo> ruleList;

    @Data
    public static class LadderGroupProduct implements Serializable {

        private static final long serialVersionUID = 6590674428990504315L;
        @ApiModelProperty(value = "货品id（sku）", required = true)
        private Long productId;

        @ApiModelProperty(value = "预付定金", required = true)
        private BigDecimal advanceDeposit;

        @ApiModelProperty(value = "第一阶梯价格或折扣", required = true)
        private BigDecimal ladderPrice1;

        @ApiModelProperty("第二阶梯价格或折扣")
        private BigDecimal ladderPrice2;

        @ApiModelProperty("第三阶梯价格或折扣")
        private BigDecimal ladderPrice3;
    }

    @Data
    public static class LadderGroupRuleInfo implements Serializable {

        private static final long serialVersionUID = -4118419189365143845L;
        @ApiModelProperty(value = "阶梯等级", required = true)
        private Integer ladderLevel;

        @ApiModelProperty(value = "阶梯团参团人数", required = true)
        private Integer joinGroupNum;
    }
}
