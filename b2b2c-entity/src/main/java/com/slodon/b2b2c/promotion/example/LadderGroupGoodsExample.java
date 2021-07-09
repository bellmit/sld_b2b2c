package com.slodon.b2b2c.promotion.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 阶梯团商品表example
 */
@Data
public class LadderGroupGoodsExample implements Serializable {
    private static final long serialVersionUID = -5130740265359568426L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer groupGoodsIdNotEquals;

    /**
     * 用于批量操作
     */
    private String groupGoodsIdIn;

    /**
     * 阶梯团商品id
     */
    private Integer groupGoodsId;

    /**
     * 阶梯团活动id
     */
    private Integer groupId;

    /**
     * 用于批量操作
     */
    private String groupIdIn;

    /**
     * 商品id（spu）
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
     * 货品id（sku）
     */
    private Long productId;

    /**
     * 商品原价
     */
    private BigDecimal productPrice;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 销量
     */
    private Integer salesVolume;

    /**
     * 预付定金
     */
    private BigDecimal advanceDeposit;

    /**
     * 第一阶梯价格或折扣
     */
    private BigDecimal ladderPrice1;

    /**
     * 第二阶梯价格或折扣
     */
    private BigDecimal ladderPrice2;

    /**
     * 第三阶梯价格或折扣
     */
    private BigDecimal ladderPrice3;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照groupGoodsId倒序排列
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