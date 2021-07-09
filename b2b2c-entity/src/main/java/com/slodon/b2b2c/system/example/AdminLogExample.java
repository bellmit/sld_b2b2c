package com.slodon.b2b2c.system.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AdminLogExample implements Serializable {
    private static final long serialVersionUID = -9043080454493741286L;
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
     * 操作管理员id
     */
    private Integer adminId;

    /**
     * 操作管理员名称
     */
    private String adminName;

    /**
     * 操作管理员名称,用于模糊查询
     */
    private String adminNameLike;

    /**
     * 操作URL
     */
    private String logUrl;

    /**
     * 操作行为
     */
    private String logContent;

    /**
     * 操作行为,用于模糊查询
     */
    private String logContentLike;

    /**
     * 大于等于开始时间
     */
    private Date logTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date logTimeBefore;

    /**
     * ip
     */
    private String logIp;

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

    /**
     * 按照时间删除
     */
    private Date deleteTime;
}