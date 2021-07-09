package com.slodon.b2b2c.system.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SearchFilterExample implements Serializable {
    private static final long serialVersionUID = -3710917883384672984L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer filterIdNotEquals;

    /**
     * 用于批量操作
     */
    private String filterIdIn;

    /**
     * 过滤id
     */
    private Integer filterId;

    /**
     * 关键字
     */
    private String keyword;

    /**
     * 关键字,用于模糊查询
     */
    private String keywordLike;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 创建人id
     */
    private Integer createId;

    /**
     * 创建时间
     */
    private String createName;

    /**
     * 创建时间,用于模糊查询
     */
    private String createNameLike;

    /**
     * 大于等于开始时间
     */
    private Date updateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date updateTimeBefore;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照filterId倒序排列
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