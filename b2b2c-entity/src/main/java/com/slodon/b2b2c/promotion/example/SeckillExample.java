package com.slodon.b2b2c.promotion.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 秒杀活动表example
 */
@Data
public class SeckillExample implements Serializable {

    private static final long serialVersionUID = 5387228955346780082L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer seckillIdNotEquals;

    /**
     * 用于批量操作
     */
    private String seckillIdIn;

    /**
     * 主键id
     */
    private Integer seckillId;

    /**
     * 活动名称
     */
    private String seckillName;

    /**
     * 活动名称,用于模糊查询
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
     * 轮播图
     */
    private String banner;

    /**
     * 创建人ID（系统管理员）
     */
    private Integer createAdminId;

    /**
     * 更新人ID（系统管理员）
     */
    private Integer updateAdminId;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照seckillId倒序排列
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
     * 店铺id,用于连表查询
     */
    private Long storeId;
}