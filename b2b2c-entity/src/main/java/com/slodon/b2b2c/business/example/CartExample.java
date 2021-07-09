package com.slodon.b2b2c.business.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class CartExample implements Serializable {
    private static final long serialVersionUID = -8464011969031943427L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer cartIdNotEquals;

    /**
     * 用于批量操作
     */
    private String cartIdIn;

    /**
     * 购物车ID
     */
    private Integer cartId;

    /**
     * 会员ID
     */
    private Integer memberId;

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 店铺名称,用于模糊查询
     */
    private String storeNameLike;

    /**
     * 商品ID(SPU ID)
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
     * 货品ID(SKU ID)
     */
    private Long productId;

    /**
     * 数量
     */
    private Integer buyNum;

    /**
     * 货品价格
     */
    private BigDecimal productPrice;

    /**
     * 商品图片
     */
    private String productImage;

    /**
     * 规格值ID，多个规格用逗号分隔
     */
    private String specValueIds;

    /**
     * 规格值集合
     */
    private String specValues;

    /**
     * 是否选中状态：0-未选中、1-选中
     */
    private Integer isChecked;

    /**
     * 大于等于开始时间
     */
    private Date updateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date updateTimeBefore;

    /**
     * 促销活动id
     */
    private Integer promotionId;

    /**
     * 促销活动类型
     */
    private Integer promotionType;

    /**
     * 促销活动描述
     */
    private String promotionDescription;

    /**
     * 活动优惠减少的价格
     */
    private BigDecimal offPrice;

    /**
     * 1-正常状态，2-商品失效（已删除），3-商品无货
     */
    private Integer productState;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照cartId倒序排列
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