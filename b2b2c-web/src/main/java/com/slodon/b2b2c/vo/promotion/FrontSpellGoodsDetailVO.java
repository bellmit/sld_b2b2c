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

/**
 * @program: slodon
 * @Description 封装拼团商品详情VO对象
 */
@Data
public class FrontSpellGoodsDetailVO implements Serializable {

    private static final long serialVersionUID = 6388928214290974481L;
    /**
     * 拼团活动绑定商品表
     */
    @ApiModelProperty("拼团活动商品id")
    private Integer spellGoodsId;

    @ApiModelProperty("拼团活动id编号")
    private Integer spellId;

    @ApiModelProperty("活动商品id（spu）")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品图片")
    private String goodsImage;

    @ApiModelProperty("规格值，用逗号分隔")
    private String specValues;

    @ApiModelProperty("活动商品id（sku）")
    private Long productId;

    @ApiModelProperty("商品原价")
    private BigDecimal productPrice;

    @ApiModelProperty("拼团价")
    private BigDecimal spellPrice;

    @ApiModelProperty("团长优惠价")
    private BigDecimal leaderPrice;

    @ApiModelProperty("库存")
    private Integer stock;

    @ApiModelProperty("活动库存")
    private Integer spellStock;

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

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("活动状态(0-未开始;1-进行中)")
    private Integer state;

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

    @ApiModelProperty("活动团队人数")
    private Integer spellTeamNum;

    @ApiModelProperty("剩余时间")
    private Long distanceEndTime;

    @ApiModelProperty("销量")
    private Integer saleNum;

    @ApiModelProperty("已购买数量")
    private Integer purchasedNum = 0;

    @ApiModelProperty("是否可以购买:true-可以,false-不可以")
    private Boolean isCanBuy = true;

    @ApiModelProperty("团长优惠")
    private BigDecimal leaderDiscount;

    public FrontSpellGoodsDetailVO(SpellGoods spellGoods, Spell spell) {
        spellGoodsId = spellGoods.getSpellGoodsId();
        spellId = spellGoods.getSpellId();
        goodsId = spellGoods.getGoodsId();
        goodsName = spellGoods.getGoodsName();
        goodsImage = FileUrlUtil.getFileUrl(spellGoods.getGoodsImage(), null);
        specValues = spellGoods.getSpecValues();
        productId = spellGoods.getProductId();
        productPrice = spellGoods.getProductPrice();
        spellPrice = spellGoods.getSpellPrice();
        leaderPrice = spellGoods.getLeaderPrice();
        spellStock = spellGoods.getSpellStock();
        spellName = spell.getSpellName();
        spellLabelId = spell.getSpellLabelId();
        spellLabelName = spell.getSpellLabelName();
        startTime = spell.getStartTime();
        endTime = spell.getEndTime();
        storeId = spell.getStoreId();
        storeName = spell.getStoreName();
        state = dealStateValue(spell.getStartTime());
        requiredNum = spell.getRequiredNum();
        cycle = spell.getCycle();
        buyLimit = spell.getBuyLimit();
        isSimulateGroup = spell.getIsSimulateGroup();
        isSimulateGroupValue = dealIsSimulateGroupValue(spell.getIsSimulateGroup());
        leaderIsPromotion = spell.getLeaderIsPromotion();
        leaderIsPromotionValue = dealLeaderIsPromotionValue(spell.getLeaderIsPromotion());
        saleNum = spellGoods.getSalesVolume();
        leaderDiscount = spellGoods.getLeaderPrice().compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : spellGoods.getProductPrice().subtract(spellGoods.getLeaderPrice());
    }

    public static Integer dealStateValue(Date startTime) {
        Integer value = 0;
        if (StringUtils.isEmpty(startTime)) return null;
        Date now = new Date();
        if (startTime.before(now)) {
            value = SpellConst.SPELL_GROUP_STATE_1;
        }
        return value;
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
}
