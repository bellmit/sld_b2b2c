package com.slodon.b2b2c.promotion.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class PresellGoodsExample implements Serializable {
    private static final long serialVersionUID = 4291498841121052026L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer presellGoodsIdNotEquals;

    /**
     * 用于批量操作
     */
    private String presellGoodsIdIn;

    /**
     * 预售活动商品id
     */
    private Integer presellGoodsId;

    /**
     * 预售活动id编号
     */
    private Integer presellId;

    /**
     * 用于批量操作
     */
    private String presellIdIn;

    /**
     * 活动商品id(sku)
     */
    private Long productId;

    /**
     * 活动商品id(spu)
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
     * 商品图片
     */
    private String goodsImage;

    /**
     * 规格值，用逗号分隔
     */
    private String specValues;

    /**
     * 商品原价
     */
    private BigDecimal productPrice;

    /**
     * 预售价格
     */
    private BigDecimal presellPrice;

    /**
     * 预售定金（全款预售不需要此项，定金预售需要）
     */
    private BigDecimal firstMoney;

    /**
     * 预售尾款（全款预售不需要此项，定金预售需要）
     */
    private BigDecimal secondMoney;

    /**
     * 订金可以抵现的金额（全款预售不需要此项，定金预售需要）
     */
    private BigDecimal firstExpand;

    /**
     * 预售广告语
     */
    private String presellDescription;

    /**
     * 赔偿金额（一般为定金指定倍数，倍数由平台设置）
     */
    private Integer depositCompensate;

    /**
     * 实际销量
     */
    private Integer actualSale;

    /**
     * 虚拟销量
     */
    private Integer virtualSale;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 活动库存
     */
    private Integer presellStock;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照presellGoodsId倒序排列
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