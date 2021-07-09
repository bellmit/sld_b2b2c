package com.slodon.b2b2c.promotion.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PresellExample implements Serializable {
    private static final long serialVersionUID = -9110608218225437451L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer presellIdNotEquals;

    /**
     * 用于批量操作
     */
    private String presellIdIn;

    /**
     * 预售活动id
     */
    private Integer presellId;

    /**
     * 预售活动名称
     */
    private String presellName;

    /**
     * 预售活动名称,用于模糊查询
     */
    private String presellNameLike;

    /**
     * 预售活动标签id
     */
    private Integer presellLabelId;

    /**
     * 预售活动标签名称
     */
    private String presellLabelName;

    /**
     * 预售活动标签名称,用于模糊查询
     */
    private String presellLabelNameLike;

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
     * 预售类型（1-定金预售，2-全款预售）
     */
    private Integer type;

    /**
     * 最大限购数量；0为不限购
     */
    private Integer buyLimit;

    /**
     * 发货时间类型（1-固定日期，2-固定天数）
     */
    private Integer deliverTimeType;

    /**
     * 大于等于开始时间
     */
    private Date deliverTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date deliverTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date remainStartTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date remainStartTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date remainEndTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date remainEndTimeBefore;

    /**
     * 发货开始时间(以天数为单位)
     */
    private Integer deliverStartTime;

    /**
     * 赔偿定金的倍数
     */
    private Integer depositCompensate;

    /**
     * 创建商户id
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
     * 排序条件，条件之间用逗号隔开，如果不传则按照presellId倒序排列
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