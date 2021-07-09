package com.slodon.b2b2c.promotion.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SpellLabelExample implements Serializable {
    private static final long serialVersionUID = -4710284840734334825L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer spellLabelIdNotEquals;

    /**
     * 用于批量操作
     */
    private String spellLabelIdIn;

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
     * 是否展示：0为不展示，1为展示，默认为1
     */
    private Integer isShow;

    /**
     * 排序
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
     * 排序条件，条件之间用逗号隔开，如果不传则按照spellLabelId倒序排列
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
     * 正在进行中的标识
     */
    private String onGoing;
}