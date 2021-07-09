package com.slodon.b2b2c.helpdesk.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class HelpdeskContactExample implements Serializable {
    private static final long serialVersionUID = 7686775783085322671L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer contactIdNotEquals;

    /**
     * 用于批量操作
     */
    private String contactIdIn;

    /**
     * 关系ID
     */
    private Integer contactId;

    /**
     * 会员ID
     */
    private Integer memberId;

    /**
     * 会话客服ID
     */
    private Long vendorId;

    /**
     * 店铺ID
     */
    private Long storeId;

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
     * 聊天关系状态：1-活跃，0-不活跃（已被转接）
     */
    private Integer state;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照contactId倒序排列
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