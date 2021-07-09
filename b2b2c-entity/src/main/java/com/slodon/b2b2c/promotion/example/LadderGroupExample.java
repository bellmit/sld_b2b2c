package com.slodon.b2b2c.promotion.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 阶梯团活动表example
 */
@Data
public class LadderGroupExample implements Serializable {
    private static final long serialVersionUID = -7985166952301471795L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer groupIdNotEquals;

    /**
     * 用于批量操作
     */
    private String groupIdIn;

    /**
     * 阶梯团活动id
     */
    private Integer groupId;

    /**
     * 阶梯团活动名称
     */
    private String groupName;

    /**
     * 阶梯团活动名称,用于模糊查询
     */
    private String groupNameLike;

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
     * 活动标签id
     */
    private Integer labelId;

    /**
     * 活动标签名称
     */
    private String labelName;

    /**
     * 活动标签名称,用于模糊查询
     */
    private String labelNameLike;

    /**
     * 尾款时间(活动结束多少小时内需要支付尾款)
     */
    private Integer balanceTime;

    /**
     * 限购件数，0为不限购
     */
    private Integer buyLimitNum;

    /**
     * 是否退还定金：1-是；0-否
     */
    private Integer isRefundDeposit;

    /**
     * 阶梯优惠方式：1-阶梯价格；2-阶梯折扣
     */
    private Integer discountType;

    /**
     * 商品id(spu)
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
     * 状态：1-创建；2-发布；3-失效；4-删除
     */
    private Integer state;

    /**
     * stateIn，用于批量操作
     */
    private String stateIn;

    /**
     * stateNotIn，用于批量操作
     */
    private String stateNotIn;

    /**
     * stateNotEquals，用于批量操作
     */
    private Integer stateNotEquals;

    /**
     * 是否生成尾款信息：0、未生成；1、已生成
     */
    private Integer executeState;

    /**
     * executeStateIn，用于批量操作
     */
    private String executeStateIn;

    /**
     * executeStateNotIn，用于批量操作
     */
    private String executeStateNotIn;

    /**
     * executeStateNotEquals，用于批量操作
     */
    private Integer executeStateNotEquals;

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
     * 创建商户ID
     */
    private Long createVendorId;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date updateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date updateTimeBefore;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照groupId倒序排列
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