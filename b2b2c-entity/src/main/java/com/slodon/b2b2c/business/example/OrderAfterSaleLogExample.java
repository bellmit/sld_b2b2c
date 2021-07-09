package com.slodon.b2b2c.business.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OrderAfterSaleLogExample implements Serializable {
    private static final long serialVersionUID = 8399399271160352868L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer logIdNotEquals;

    /**
     * 用于批量操作
     */
    private String logIdIn;

    /**
     * 日志id
     */
    private Integer logId;

    /**
     * 操作人角色(1-系统管理员，2-商户，3-会员）
     */
    private Integer logRole;

    /**
     * 操作人id
     */
    private Long logUserId;

    /**
     * 操作人名称
     */
    private String logUserName;

    /**
     * 操作人名称,用于模糊查询
     */
    private String logUserNameLike;

    /**
     * 售后单号
     */
    private String afsSn;

    /**
     * 售后单号,用于模糊查询
     */
    private String afsSnLike;

    /**
     * 售后服务端类型：1-退货退款单，2-换货单，3-仅退款单
     */
    private Integer afsType;

    /**
     * 状态，与退换货表状态相同
     */
    private String state;

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
     * 排序条件，条件之间用逗号隔开，如果不传则按照logId倒序排列
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