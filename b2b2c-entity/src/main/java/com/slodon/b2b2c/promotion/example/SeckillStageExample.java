package com.slodon.b2b2c.promotion.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 秒杀场次example
 */
@Data
public class SeckillStageExample implements Serializable {

    private static final long serialVersionUID = -1299578743590424816L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer stageIdNotEquals;

    /**
     * 用于批量操作
     */
    private String stageIdIn;

    /**
     * 场次id
     */
    private Integer stageId;

    /**
     * 场次名称
     */
    private String stageName;

    /**
     * 场次名称,用于模糊查询
     */
    private String stageNameLike;

    /**
     * 秒杀活动id
     */
    private Integer seckillId;

    /**
     * 秒杀活动名称
     */
    private String seckillName;

    /**
     * 秒杀活动名称,用于模糊查询
     */
    private String seckillNameLike;

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
     * 排序条件，条件之间用逗号隔开，如果不传则按照stageId倒序排列
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