package com.slodon.b2b2c.promotion.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SpellExample implements Serializable {
    private static final long serialVersionUID = -963351099226686942L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer spellIdNotEquals;

    /**
     * 用于批量操作
     */
    private String spellIdIn;

    /**
     * 拼团活动id
     */
    private Integer spellId;

    /**
     * 拼团活动名称
     */
    private String spellName;

    /**
     * 拼团活动名称,用于模糊查询
     */
    private String spellNameLike;

    /**
     * 拼团活动标签id
     */
    private Integer spellLabelId;

    /**
     * 拼团活动标签名称
     */
    private String spellLabelName;

    /**
     * 拼团活动标签名称,用于模糊查询
     */
    private String spellLabelNameLike;

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
     * 活动状态(1-创建；2-发布；3-失效；4-删除)
     */
    private Integer state;

    /**
     * 要求成团人数
     */
    private Integer requiredNum;

    /**
     * 成团周期（开团-截团时长），单位：小时
     */
    private Integer cycle;

    /**
     * 活动最大限购数量；0为不限购
     */
    private Integer buyLimit;

    /**
     * 是否模拟成团(0-关闭/1-开启）
     */
    private Integer isSimulateGroup;

    /**
     * 团长是否有优惠(0-没有/1-有）
     */
    private Integer leaderIsPromotion;

    /**
     * 拼团规则说明
     */
    private String spellDesc;

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
     * 排序条件，条件之间用逗号隔开，如果不传则按照spellId倒序排列
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
     * 活动状态(1-创建；2-发布；3-失效；4-删除)
     */
    private Integer stateNotEquals;
}