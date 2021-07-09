package com.slodon.b2b2c.business.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ComplainTalkExample implements Serializable {
    private static final long serialVersionUID = -1590432382185797927L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer complainTalkIdNotEquals;

    /**
     * 用于批量操作
     */
    private String complainTalkIdIn;

    /**
     * 投诉对话id
     */
    private Integer complainTalkId;

    /**
     * 投诉id编号
     */
    private Integer complainId;

    /**
     * 投诉对话用户id
     */
    private Long talkUserId;

    /**
     * 投诉对话用户名称
     */
    private String talkUserName;

    /**
     * 投诉对话用户名称,用于模糊查询
     */
    private String talkUserNameLike;

    /**
     * 投诉对话用户类型：1-会员，2-商户，3-平台
     */
    private Integer talkUserType;

    /**
     * 投诉对话内容
     */
    private String talkContent;

    /**
     * 投诉对话内容,用于模糊查询
     */
    private String talkContentLike;

    /**
     * 大于等于开始时间
     */
    private Date talkTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date talkTimeBefore;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照complainTalkId倒序排列
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