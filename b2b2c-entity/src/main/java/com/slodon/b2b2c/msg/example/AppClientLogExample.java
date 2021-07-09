package com.slodon.b2b2c.msg.example;

import java.io.Serializable;
import java.util.Date;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

/**
 * app客户端记录表，每个客户端有一个唯一的记录，用于app消息推送example
 */
@Data
public class AppClientLogExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer logIdNotEquals;

    /**
     * 用于批量操作
     */
    private String logIdIn;

    /**
     * 绑定id
     */
    private Integer logId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户类型，1-会员，2-供应商管理员
     */
    private Integer userType;

    /**
     * 客户端身份ID
     */
    private String clientId;

    /**
     * 客户端身份别名
     */
    private String alias;

    /**
     * 大于等于开始时间
     */
    private Date updateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date updateTimeBefore;

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