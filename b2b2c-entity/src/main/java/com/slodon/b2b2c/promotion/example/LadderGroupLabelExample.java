package com.slodon.b2b2c.promotion.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 阶梯团标签表example
 */
@Data
public class LadderGroupLabelExample implements Serializable {
    private static final long serialVersionUID = 4598882959755162018L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer labelIdNotEquals;

    /**
     * 用于批量操作
     */
    private String labelIdIn;

    /**
     * 标签id
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
     * 排序
     */
    private Integer sort;

    /**
     * 是否显示：0、不显示；1、显示
     */
    private Integer isShow;

    /**
     * 创建管理员ID
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
     * 正在进行中的标识
     */
    private String onGoing;
}