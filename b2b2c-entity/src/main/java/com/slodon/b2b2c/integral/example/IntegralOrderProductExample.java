package com.slodon.b2b2c.integral.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单货品明细表example
 */
@Data
public class IntegralOrderProductExample implements Serializable {
    private static final long serialVersionUID = -4244026440092694485L;
    /**
     * 用于编辑时的重复判断
     */
    private Long orderProductIdNotEquals;

    /**
     * 用于批量操作
     */
    private String orderProductIdIn;

    /**
     * 订单货品id
     */
    private Long orderProductId;

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 订单号,用于模糊查询
     */
    private String orderSnLike;

    /**
     * 商家ID
     */
    private Long storeId;

    /**
     * 商家名称
     */
    private String storeName;

    /**
     * 商家名称,用于模糊查询
     */
    private String storeNameLike;

    /**
     * 会员ID
     */
    private Integer memberId;

    /**
     * 商品id
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
     * 货品图片
     */
    private String productImage;

    /**
     * 规格详情
     */
    private String specValues;

    /**
     * 货品ID
     */
    private Long productId;

    /**
     * 价格组合的最多使用积分(基础积分，可以减少积分增加现金），不能为零
     */
    private Integer integralPrice;

    /**
     * 价格组合最少使用现金(基础现金，可以增加现金，减少积分）为零则只显示积分
     */
    private BigDecimal cashPrice;

    /**
     * 商品数量
     */
    private Integer productNum;

    /**
     * 支付使用积分数量
     */
    private Integer integral;

    /**
     * 积分抵扣金额
     */
    private BigDecimal integralCashAmount;

    /**
     * 现金支付金额
     */
    private BigDecimal cashAmount;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照orderProductId倒序排列
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