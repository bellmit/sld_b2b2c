package com.slodon.b2b2c.business.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OrderLogExample implements Serializable {
    private static final long serialVersionUID = 2825866903871199791L;

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
     * 操作人ID，结合log_role使用
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
     * 订单号
     */
    private String orderSn;

    /**
     * 订单号,用于模糊查询
     */
    private String orderSnLike;

    /**
     * 订单状态：0-已取消；10-未付款订单；20-已付款；30-已发货；40-已完成;50-已关闭
     */
    private Integer orderStateLog;

    /**
     * 大于等于开始时间
     */
    private Date logTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date logTimeBefore;

    /**
     * 文字描述
     */
    private String logContent;

    /**
     * 文字描述,用于模糊查询
     */
    private String logContentLike;

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