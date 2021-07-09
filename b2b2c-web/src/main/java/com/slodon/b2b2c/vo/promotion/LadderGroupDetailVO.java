package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.core.constant.LadderGroupConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.promotion.pojo.LadderGroup;
import com.slodon.b2b2c.promotion.pojo.LadderGroupGoods;
import com.slodon.b2b2c.promotion.pojo.LadderGroupRule;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装阶梯团详情VO对象
 * @Author wuxy
 */
@Data
public class LadderGroupDetailVO implements Serializable {

    private static final long serialVersionUID = 6112264945865566737L;
    @ApiModelProperty("阶梯团活动id")
    private Integer groupId;

    @ApiModelProperty("阶梯团活动名称")
    private String groupName;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @ApiModelProperty("活动标签id")
    private Integer labelId;

    @ApiModelProperty("活动标签名称")
    private String labelName;

    @ApiModelProperty("尾款时间(活动结束多少小时内需要支付尾款)")
    private Integer balanceTime;

    @ApiModelProperty("限购件数，0为不限购")
    private Integer buyLimitNum;

    @ApiModelProperty("限购件数，0为不限购")
    private String buyLimitNumValue;

    @ApiModelProperty("是否退还定金：1-是；0-否")
    private Integer isRefundDeposit;

    @ApiModelProperty("是否退还定金：1-是；0-否")
    private String isRefundDepositValue;

    @ApiModelProperty("阶梯优惠方式：1-阶梯价格；2-阶梯折扣")
    private Integer discountType;

    @ApiModelProperty("阶梯优惠方式：1-阶梯价格；2-阶梯折扣")
    private String discountTypeValue;

    @ApiModelProperty("阶梯团商品信息")
    private LadderGroupGoodsInfo goodsInfo;

    @ApiModelProperty("阶梯团规则列表")
    private List<LadderGroupRule> ruleList;

    public LadderGroupDetailVO(LadderGroup ladderGroup) {
        this.groupId = ladderGroup.getGroupId();
        this.groupName = ladderGroup.getGroupName();
        this.startTime = ladderGroup.getStartTime();
        this.endTime = ladderGroup.getEndTime();
        this.labelId = ladderGroup.getLabelId();
        this.labelName = ladderGroup.getLabelName();
        this.balanceTime = ladderGroup.getBalanceTime();
        this.buyLimitNum = ladderGroup.getBuyLimitNum();
        this.buyLimitNumValue = ladderGroup.getBuyLimitNum() == 0 ? "不限购" : ladderGroup.getBuyLimitNum() + "件";
        this.isRefundDeposit = ladderGroup.getIsRefundDeposit();
        this.isRefundDepositValue = ladderGroup.getIsRefundDeposit() == LadderGroupConst.IS_REFUND_DEPOSIT_1 ? "是" : "否";
        this.discountType = ladderGroup.getDiscountType();
        this.discountTypeValue = dealDiscountTypeValue(ladderGroup.getDiscountType());
    }

    @Data
    public static class LadderGroupGoodsInfo implements Serializable {

        private static final long serialVersionUID = 6971116493969603496L;
        @ApiModelProperty("商品id（spu）")
        private Long goodsId;

        @ApiModelProperty("商品名称")
        private String goodsName;

        @ApiModelProperty("商品广告语")
        private String goodsBrief;

        @ApiModelProperty("商品图片")
        private String goodsImage;

        @ApiModelProperty("预付定金")
        private BigDecimal advanceDeposit;

        @ApiModelProperty("货品列表")
        private List<LadderGroupProductVO> productList;

        public LadderGroupGoodsInfo(LadderGroupGoods ladderGroupGoods) {
            this.goodsId = ladderGroupGoods.getGoodsId();
            this.goodsName = ladderGroupGoods.getGoodsName();
            this.goodsBrief = ladderGroupGoods.getGoodsBrief();
            this.goodsImage = FileUrlUtil.getFileUrl(ladderGroupGoods.getGoodsImage(), null);
            this.advanceDeposit = ladderGroupGoods.getAdvanceDeposit();
        }
    }

    public static String dealDiscountTypeValue(Integer discountType) {
        String value = null;
        if (StringUtils.isEmpty(discountType)) return null;
        switch (discountType) {
            case LadderGroupConst.DISCOUNT_TYPE_1:
                value = "阶梯价格";
                break;
            case LadderGroupConst.DISCOUNT_TYPE_2:
                value = "阶梯折扣";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }
}
