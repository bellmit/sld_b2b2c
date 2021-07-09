package com.slodon.b2b2c.member.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MemberGradeExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer gradeIdNotEquals;

    /**
     * 用于批量操作
     */
    private String gradeIdIn;

    /**
     * 等级ID
     */
    private Integer gradeId;

    /**
     * 等级名称
     */
    private String gradeName;

    /**
     * 等级名称,用于模糊查询
     */
    private String gradeNameLike;

    /**
     * 等级图标
     */
    private String gradeImg;

    /**
     * 经验值
     */
    private Integer experienceValue;

    /**
     * 是否内置数据：0-否；1-是，内置数据不可修改、删除
     */
    private Integer isInner;

    /**
     * 创建人id
     */
    private Integer createAdminId;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 修改人id
     */
    private Integer updateAdminId;

    /**
     * 大于等于开始时间
     */
    private Date updateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date updateTimeBefore;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照gradeId倒序排列
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