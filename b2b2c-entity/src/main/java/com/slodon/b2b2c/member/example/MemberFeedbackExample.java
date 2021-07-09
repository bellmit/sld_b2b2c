package com.slodon.b2b2c.member.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MemberFeedbackExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer feedbackIdNotEquals;

    /**
     * 用于批量操作
     */
    private String feedbackIdIn;

    /**
     * 反馈id
     */
    private Integer feedbackId;

    /**
     * 反馈类型id
     */
    private Integer typeId;

    /**
     * 反馈内容
     */
    private String content;

    /**
     * 反馈内容,用于模糊查询
     */
    private String contentLike;

    /**
     * 图片
     */
    private String image;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户id
     */
    private Integer memberId;

    /**
     * 状态：0为未处理、1为已处理
     */
    private Integer state;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 处理人
     */
    private String handler;

    /**
     * 大于等于开始时间
     */
    private Date handleTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date handleTimeBefore;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照feedbackId倒序排列
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