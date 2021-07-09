package com.slodon.b2b2c.member.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MemberFeedbackTypeExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer typeIdNotEquals;

    /**
     * 用于批量操作
     */
    private String typeIdIn;

    /**
     * 类型id
     */
    private Integer typeId;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 类型名称,用于模糊查询
     */
    private String typeNameLike;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否启用，0：不启用（默认）；1：启用
     */
    private Integer isUse;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 创建人姓名
     */
    private String creator;

    /**
     * 大于等于开始时间
     */
    private Date updateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date updateTimeBefore;

    /**
     * 修改人姓名
     */
    private String updater;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照typeId倒序排列
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