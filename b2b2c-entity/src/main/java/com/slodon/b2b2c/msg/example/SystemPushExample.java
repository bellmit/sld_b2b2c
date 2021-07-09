package com.slodon.b2b2c.msg.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SystemPushExample implements Serializable {
    private static final long serialVersionUID = -2446749739949989245L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer pushIdNotEquals;

    /**
     * 用于批量操作
     */
    private String pushIdIn;

    /**
     * 推送id
     */
    private Integer pushId;

    /**
     * 系统推送内容
     */
    private String content;

    /**
     * 系统推送内容,用于模糊查询
     */
    private String contentLike;

    /**
     * 大于等于开始时间
     */
    private Date sendTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date sendTimeBefore;

    /**
     * 发送方式：1--站内信，2--短信, 3--邮件
     */
    private Integer sendWay;

    /**
     * 是否记录消息查看状态：0--否，1--是
     */
    private Integer isCheck;

    /**
     * 接收人类型：1--会员，2--商家
     */
    private Integer receiveType;

    /**
     * 推送内容描述
     */
    private String description;

    /**
     * 发送消息的管理员id
     */
    private Integer createId;

    /**
     * 接收人id串，如: ,1,2,3,5,
     */
    private String receiveIds;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照pushId倒序排列
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