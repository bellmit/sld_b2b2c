package com.slodon.b2b2c.promotion.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 优惠券表example
 */
@Data
public class CouponExample implements Serializable {
    private static final long serialVersionUID = -2200440509446414309L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer couponIdNotEquals;

    /**
     * 用于批量操作
     */
    private String couponIdIn;

    /**
     * 优惠券id
     */
    private Integer couponId;

    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 优惠券名称,用于模糊查询
     */
    private String couponNameLike;

    /**
     * 优惠内容
     */
    private String couponContent;

    /**
     * 优惠内容,用于模糊查询
     */
    private String couponContentLike;

    /**
     * 优惠券使用说明
     */
    private String description;

    /**
     * 优惠券获取类型(1-免费领取；2-积分兑换；3-活动赠送；4-礼包券）
     */
    private Integer publishType;

    /**
     * 优惠券类型(1-固定金额券；2-折扣券（折扣比例）；3-随机金额券）
     */
    private Integer couponType;

    /**
     * 消耗积分（积分兑换时用）
     */
    private Integer consumeIntegral;

    /**
     * 统计信息-已经领取数量
     */
    private Integer receivedNum;

    /**
     * 统计信息-已使用数量
     */
    private Integer usedNum;

    /**
     * 发行限制-优惠券类型对应的具体值(满减金额:例如20元，折扣：例如90为90%）
     */
    private BigDecimal publishValue;

    /**
     * 发行限制-随机金额的最大值
     */
    private BigDecimal randomMax;

    /**
     * 发行限制-随机金额最小值
     */
    private BigDecimal randomMin;

    /**
     * 发行限制-发行总金额=发行数量*面额；随机金额券为所有券的和；折扣券不需要此项
     */
    private BigDecimal publishAmount;

    /**
     * 发行限制-发行数量
     */
    private Integer publishNum;

    /**
     * 大于等于开始时间
     */
    private Date publishStartTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date publishStartTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date publishEndTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date publishEndTimeBefore;

    /**
     * 发行限制-限制领取次数，0-为不限制，1-限制领1次，默认为1
     */
    private Integer limitReceive;

    /**
     * 使用限制-（1-固定起止时间，2-固定有效期）
     */
    private Integer effectiveTimeType;

    /**
     * 大于等于使用开始时间
     */
    private Date effectiveStartAfter;

    /**
     * 小于等于使用开始时间
     */
    private Date effectiveStartBefore;

    /**
     * 大于等于使用结束时间
     */
    private Date effectiveEndAfter;

    /**
     * 小于等于使用结束时间
     */
    private Date effectiveEndBefore;

    /**
     * 使用限制-固定有效期时长，单位天
     */
    private Integer cycle;

    /**
     * 优惠券状态(1-正常；2-失效；3-删除）
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
     * 使用限制-适用商品类型(1-全部商品；2-指定商品；3-指定分类）
     */
    private Integer useType;

    /**
     * 订单金额使用限制，超过quota后可用
     */
    private BigDecimal limitQuota;

    /**
     * 折扣最多优惠金额
     */
    private BigDecimal discountLimitAmount;

    /**
     * 不同优惠券类型是否允许叠加使用(0-不允许；1-允许）（店铺和平台的券可以叠加使用）
     */
    private Integer plusQualification;

    /**
     * 是否累加满减，每满一个quota减对应金额（1-是，0-否）
     */
    private Integer isPerQuota;

    /**
     * 是否推荐 1、推荐；0、不推荐
     */
    private Integer isRecommend;

    /**
     * 活动应用渠道1-通用；2-PC；3-微信，4-app
     */
    private Integer channel;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 如果store_id=0即为平台管理员，否则为vendor用户创建
     */
    private Long createUserId;

    /**
     * 创建用户名
     */
    private String createUserName;

    /**
     * 创建用户名,用于模糊查询
     */
    private String createUserNameLike;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照couponId倒序排列
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
     * 分类id
     */
    private Integer categoryId;

    /**
     * 店铺id
     */
    private Long storeIdNotEquals;
}