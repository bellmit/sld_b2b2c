package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.core.constant.SpellConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.promotion.pojo.Spell;
import com.slodon.b2b2c.promotion.pojo.SpellGoods;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装拼团详情VO对象
 * @Author wuxy
 * @date 2020.11.04 17:54
 */
@Data
public class SpellDetailVO implements Serializable {

    private static final long serialVersionUID = 4378374563971513321L;
    @ApiModelProperty("拼团活动id")
    private Integer spellId;

    @ApiModelProperty("拼团活动名称")
    private String spellName;

    @ApiModelProperty("拼团活动标签id")
    private Integer spellLabelId;

    @ApiModelProperty("拼团活动标签名称")
    private String spellLabelName;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @ApiModelProperty("要求成团人数")
    private Integer requiredNum;

    @ApiModelProperty("成团周期（开团-截团时长），单位：小时")
    private Integer cycle;

    @ApiModelProperty("活动最大限购数量；0为不限购")
    private Integer buyLimit;

    @ApiModelProperty("是否模拟成团(0-关闭/1-开启）")
    private Integer isSimulateGroup;

    @ApiModelProperty("是否模拟成团(0-关闭/1-开启）")
    private String isSimulateGroupValue;

    @ApiModelProperty("团长是否有优惠(0-没有/1-有）")
    private Integer leaderIsPromotion;

    @ApiModelProperty("团长是否有优惠(0-没有/1-有）")
    private String leaderIsPromotionValue;

    @ApiModelProperty("拼团商品列表")
    private List<GoodsVO> goodsList;

    public SpellDetailVO(Spell spell) {
        spellId = spell.getSpellId();
        spellName = spell.getSpellName();
        spellLabelId = spell.getSpellLabelId();
        spellLabelName = spell.getSpellLabelName();
        startTime = spell.getStartTime();
        endTime = spell.getEndTime();
        requiredNum = spell.getRequiredNum();
        cycle = spell.getCycle();
        buyLimit = spell.getBuyLimit();
        isSimulateGroup = spell.getIsSimulateGroup();
        isSimulateGroupValue = dealIsSimulateGroupValue(spell.getIsSimulateGroup());
        leaderIsPromotion = spell.getLeaderIsPromotion();
        leaderIsPromotionValue = dealLeaderIsPromotionValue(spell.getLeaderIsPromotion());
    }

    public static String dealIsSimulateGroupValue(Integer isSimulateGroup) {
        String value = null;
        if (StringUtils.isEmpty(isSimulateGroup)) return null;
        switch (isSimulateGroup) {
            case SpellConst.IS_SIMULATE_GROUP_0:
                value = "不开启";
                break;
            case SpellConst.IS_SIMULATE_GROUP_1:
                value = "开启";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }

    public static String dealLeaderIsPromotionValue(Integer leaderIsPromotion) {
        String value = null;
        if (StringUtils.isEmpty(leaderIsPromotion)) return null;
        switch (leaderIsPromotion) {
            case SpellConst.LEADER_IS_PROMOTION_0:
                value = "不开启";
                break;
            case SpellConst.LEADER_IS_PROMOTION_1:
                value = "开启";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }

    @Data
    public static class GoodsVO implements Serializable {

        private static final long serialVersionUID = 3969166479653447556L;
        @ApiModelProperty("商品ID")
        private Long goodsId;

        @ApiModelProperty("商品名称")
        private String goodsName;

        @ApiModelProperty("商品图片")
        private String goodsImage;

        @ApiModelProperty("拼团商品规格列表")
        private List<SpellProductVO> productList;

        public GoodsVO(SpellGoods spellGoods) {
            goodsId = spellGoods.getGoodsId();
            goodsName = spellGoods.getGoodsName();
            goodsImage = FileUrlUtil.getFileUrl(spellGoods.getGoodsImage(), null);
        }

        @Data
        public static class SpellProductVO implements Serializable {

            private static final long serialVersionUID = 1167512397774852296L;
            @ApiModelProperty("货品id")
            private long productId;

            @ApiModelProperty("SKU规格")
            private String specValues;

            @ApiModelProperty("商品原价")
            private BigDecimal productPrice;

            @ApiModelProperty("库存")
            private Integer stock;

            @ApiModelProperty("拼团价格")
            private BigDecimal spellPrice;

            @ApiModelProperty("团长优惠价")
            private BigDecimal leaderPrice;

            @ApiModelProperty("拼团库存")
            private Integer spellStock;

            public SpellProductVO(SpellGoods spellGoods) {
                productId = spellGoods.getProductId();
                specValues = spellGoods.getSpecValues();
                productPrice = spellGoods.getProductPrice();
                spellPrice = spellGoods.getSpellPrice();
                leaderPrice = spellGoods.getLeaderPrice();
                spellStock = spellGoods.getSpellStock();
            }
        }
    }
}
