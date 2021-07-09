package com.slodon.b2b2c.msg.example;

import java.io.Serializable;
import java.util.Date;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

@Data
public class NoticeCheckStatusExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer checkStatusIdNotEquals;

    /**
     * 用于批量操作
     */
    private String checkStatusIdIn;

    /**
     * 主键id
     */
    private Integer checkStatusId;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 公告id
     */
    private Integer noticeId;

    /**
     * 大于等于开始时间
     */
    private Date viewTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date viewTimeBefore;

    /**
     * 状态 0-未读 1-已读（可标记为未读，暂时不用）
     */
    private Integer state;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照checkStatusId倒序排列
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