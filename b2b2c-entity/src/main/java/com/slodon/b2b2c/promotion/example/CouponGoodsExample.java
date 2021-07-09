package com.slodon.b2b2c.promotion.example;


import java.io.Serializable;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

@Data
public class CouponGoodsExample implements Serializable {
    private static final long serialVersionUID = 6380777038847566004L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer couponGoodsIdNotEquals;

    /**
     * 用于批量操作
     */
    private String couponGoodsIdIn;

    /**
     * 优惠活动商品ID
     */
    private Integer couponGoodsId;

    /**
     * 绑定的优惠券ID
     */
    private Integer couponId;

    /**
     * 商品SPU ID
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
     * 商品所属的一级分类；用于快速查询对应类别的优惠券
     */
    private Integer goodsCategoryId;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照couponGoodsId倒序排列
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

    /**
     * 用于批量操作
     */
    private String goodsIdIn;
}