package com.slodon.b2b2c.promotion.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 秒杀标签example
 */
@Data
public class SeckillLabelExample implements Serializable {

    private static final long serialVersionUID = 6051204672767886036L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer labelIdNotEquals;

    /**
     * 用于批量操作
     */
    private String labelIdIn;

    /**
     * 主键id自增长
     */
    private Integer labelId;

    /**
     * 标签名称
     */
    private String labelName;

    /**
     * 标签名称,用于模糊查询
     */
    private String labelNameLike;

    /**
     * 是否显示 0-不显示 1-显示
     */
    private Integer isShow;

    /**
     * 标签排序
     */
    private Integer sort;

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
     * 创建人ID（系统管理员）
     */
    private Integer createAdminId;

    /**
     * 更新人ID（系统管理员）
     */
    private Integer updateAdminId;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照labelId倒序排列
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
     * 秒杀活动id
     */
    private Integer seckillId;

    /**
     * 小于等于结束时间
     */
    private Date endTimeBefore;

    /**
     * 用于处理时间不为空标识
     */
    private String queryTime;
}