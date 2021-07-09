package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.promotion.pojo.LadderGroup;
import com.slodon.b2b2c.promotion.pojo.LadderGroupGoods;
import com.slodon.b2b2c.promotion.pojo.LadderGroupRule;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装阶梯团商品详情
 * @Author wuxy
 */
@Data
public class FrontLadderGroupDetailVO implements Serializable {

    private static final long serialVersionUID = -1745290648861246834L;
    @ApiModelProperty("阶梯团活动id")
    private Integer groupId;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @ApiModelProperty("尾款时间(活动结束多少小时内需要支付尾款)")
    private Integer balanceTime;

    @ApiModelProperty("限购件数，0为不限购")
    private Integer buyLimitNum;

    @ApiModelProperty("是否退还定金：1-是；0-否")
    private Integer isRefundDeposit;

    @ApiModelProperty("阶梯优惠方式：1-阶梯价格；2-阶梯折扣")
    private Integer discountType;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("活动商品id(sku)")
    private Long productId;

    @ApiModelProperty("活动商品id(spu)")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品广告语")
    private String goodsBrief;

    @ApiModelProperty("商品图片")
    private String goodsImage;

    @ApiModelProperty("规格值，用逗号分隔")
    private String specValues;

    @ApiModelProperty("商品原价")
    private BigDecimal productPrice;

    @ApiModelProperty("预付定金")
    private BigDecimal advanceDeposit;

    @ApiModelProperty("当前价")
    private BigDecimal currentPrice;

    @ApiModelProperty("已参团人数")
    private Integer joinedNum;

    @ApiModelProperty("当前阶梯等级")
    private Integer currentLadderLevel = 0;

    @ApiModelProperty("已购买数量")
    private Integer purchasedNum = 0;

    @ApiModelProperty("是否可以购买:true-可以,false-不可以")
    private Boolean isCanBuy = true;

    @ApiModelProperty("距离结束时间")
    private Long distanceEndTime;

    @ApiModelProperty("阶梯团规则列表")
    private List<LadderGroupRuleVO> ruleList;

    public FrontLadderGroupDetailVO(LadderGroup ladderGroup, LadderGroupGoods ladderGroupGoods) {
        this.groupId = ladderGroup.getGroupId();
        this.startTime = ladderGroup.getStartTime();
        this.endTime = ladderGroup.getEndTime();
        this.balanceTime = ladderGroup.getBalanceTime();
        this.buyLimitNum = ladderGroup.getBuyLimitNum();
        this.isRefundDeposit = ladderGroup.getIsRefundDeposit();
        this.discountType = ladderGroup.getDiscountType();
        this.storeId = ladderGroup.getStoreId();
        this.storeName = ladderGroup.getStoreName();
        this.productId = ladderGroupGoods.getProductId();
        this.goodsId = ladderGroupGoods.getGoodsId();
        this.goodsName = ladderGroupGoods.getGoodsName();
        this.goodsBrief = ladderGroupGoods.getGoodsBrief();
        this.goodsImage = FileUrlUtil.getFileUrl(ladderGroupGoods.getGoodsImage(), null);
        this.specValues = ladderGroupGoods.getSpecValues();
        this.productPrice = ladderGroupGoods.getProductPrice();
        this.advanceDeposit = ladderGroupGoods.getAdvanceDeposit();
        this.currentPrice = ladderGroupGoods.getProductPrice();
    }

    @Data
    public static class LadderGroupRuleVO implements Serializable {

        private static final long serialVersionUID = 7177283769246508133L;
        @ApiModelProperty("阶梯团规则id")
        private Integer ruleId;

        @ApiModelProperty("阶梯团参团人数")
        private Integer joinGroupNum;

        @ApiModelProperty("阶梯等级")
        private Integer ladderLevel;

        @ApiModelProperty("阶梯价格或折扣")
        private BigDecimal ladderPrice;

        public LadderGroupRuleVO(LadderGroupRule ladderGroupRule, BigDecimal ladderPrice) {
            this.ruleId = ladderGroupRule.getRuleId();
            this.joinGroupNum = ladderGroupRule.getJoinGroupNum();
            this.ladderLevel = ladderGroupRule.getLadderLevel();
            this.ladderPrice = ladderPrice;
        }
    }
}
