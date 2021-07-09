package com.slodon.b2b2c.promotion.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SpellGoodsExample implements Serializable {
    private static final long serialVersionUID = -4129954968224189658L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer spellGoodsIdNotEquals;

    /**
     * 用于批量操作
     */
    private String spellGoodsIdIn;

    /**
     * 拼团活动商品id
     */
    private Integer spellGoodsId;

    /**
     * 拼团活动id编号
     */
    private Integer spellId;

    /**
     * 用于批量操作
     */
    private String spellIdIn;

    /**
     * 活动商品id（spu）
     */
    private Long goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品名称,用于模糊查询
     */
    private String goodsNameLike;

    /**
     * 商品广告语
     */
    private String goodsBrief;

    /**
     * 商品图片
     */
    private String goodsImage;

    /**
     * 规格值，用逗号分隔
     */
    private String specValues;

    /**
     * 活动商品id（sku）
     */
    private Long productId;

    /**
     * 商品原价
     */
    private BigDecimal productPrice;

    /**
     * 拼团价
     */
    private BigDecimal spellPrice;

    /**
     * 团长优惠价
     */
    private BigDecimal leaderPrice;

    /**
     * 销量
     */
    private Integer salesVolume;

    /**
     * 活动库存
     */
    private Integer spellStock;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照spellGoodsId倒序排列
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