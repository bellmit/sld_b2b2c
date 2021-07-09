package com.slodon.b2b2c.helpdesk.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 客服常见问题表example
 */
@Data
public class HelpdeskCommonProblemMsgExample implements Serializable {
    private static final long serialVersionUID = 6727718429857261092L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer problemMsgIdNotEquals;

    /**
     * 用于批量操作
     */
    private String problemMsgIdIn;

    /**
     * 常见问题消息ID
     */
    private Integer problemMsgId;

    /**
     * 商户ID
     */
    private Long storeId;

    /**
     * 消息内容
     */
    private String msgContent;

    /**
     * 消息内容,用于模糊查询
     */
    private String msgContentLike;

    /**
     * 回答内容
     */
    private String msgReply;

    /**
     * 是否显示 0-不显示 1-显示
     */
    private Integer isShow;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建人id
     */
    private Long createVendorId;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照problemMsgId倒序排列
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