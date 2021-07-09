package com.slodon.b2b2c.business.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ComplainSubjectExample implements Serializable {
    private static final long serialVersionUID = 5867985286465365740L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer complainSubjectIdNotEquals;

    /**
     * 用于批量操作
     */
    private String complainSubjectIdIn;

    /**
     * 投诉主题id
     */
    private Integer complainSubjectId;

    /**
     * 投诉主题名称
     */
    private String complainSubjectName;

    /**
     * 投诉主题名称,用于模糊查询
     */
    private String complainSubjectNameLike;

    /**
     * 投诉主题描述
     */
    private String complainSubjectDesc;

    /**
     * 是否展示：0-不展示，1-展示，默认为1
     */
    private Integer isShow;

    /**
     * 排序(0-255),越小排序靠前
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
     * 创建管理员id
     */
    private Integer createAdminId;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照complainSubjectId倒序排列
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