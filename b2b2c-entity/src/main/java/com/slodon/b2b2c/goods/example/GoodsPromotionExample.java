package com.slodon.b2b2c.goods.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class GoodsPromotionExample implements Serializable {
    private static final long serialVersionUID = -2330222354612214989L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer goodsPromotionIdNotEquals;

    /**
     * 用于批量操作
     */
    private String goodsPromotionIdIn;

    /**
     * 商品活动ID
     */
    private Integer goodsPromotionId;

    /**
     * 活动ID
     */
    private Integer promotionId;

    /**
     * 店铺id
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
     * 创建活动的店铺管理员ID
     */
    private Integer createVendorId;

    /**
     * 大于等于开始时间
     */
    private Date bindTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date bindTimeBefore;

    /**
     * 商品id（spu）
     */
    private Long goodsId;

    /**
     * 货品id（sku）
     */
    private Long productId;

    /**
     * 商品三级分类id
     */
    private Integer goodsCategoryId3;

    /**
     * 活动等级 1-商品活动；2-店铺活动；3-平台活动
     */
    private Integer promotionGrade;

    /**
     * 系统内置，每创建一个活动都有一个活动类型
     */
    private Integer promotionType;

    /**
     * 大于等于开始时间
     */
    private Date startTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date startTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date endTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date endTimeBefore;

    /**
     * 描述信息
     */
    private String description;

    /**
     * 活动是否有效,0-无效，1-有效，置为无效状态后，将此活动信息存入slodon_goods_activity_history
     */
    private Integer isEffective;

    /**
     * 绑定类型：1-商品绑定；2-店铺绑定；3-三级分类绑定
     */
    private Integer bindType;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照goodsPromotionId倒序排列
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