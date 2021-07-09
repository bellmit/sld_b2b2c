package com.slodon.b2b2c.goods.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class GoodsFailedReasonExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer reasonIdNotEquals;

    /**
     * 用于批量操作
     */
    private String reasonIdIn;

    /**
     * 原因id
     */
    private Integer reasonId;

    /**
     * 原因id,用于模糊查询
     */
    private String reasonIdLike;

    /**
     * 类型：1-审核驳回原因，2-违规下架原因
     */
    private Integer type;

    /**
     * 内容
     */
    private String content;

    /**
     * 内容,用于模糊查询
     */
    private String contentLike;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 管理员ID
     */
    private Integer createAdminId;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否显示，1-展示，0-不显示
     */
    private Integer isVisible;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照reasonId倒序排列
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