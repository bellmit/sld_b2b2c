package com.slodon.b2b2c.system.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public class PcFirstAdvExample implements Serializable {
    private static final long serialVersionUID = -7217774145595818115L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer advIdNotEquals;

    /**
     * 用于批量操作
     */
    private String advIdIn;

    /**
     * 广告id
     */
    private Integer advId;

    /**
     * 弹层广告数据
     */
    private String data;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照advId倒序排列
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