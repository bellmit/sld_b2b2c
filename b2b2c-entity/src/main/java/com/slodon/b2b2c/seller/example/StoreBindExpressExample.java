package com.slodon.b2b2c.seller.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 店铺的选择使用的快递公司example
 */
@Data
public class StoreBindExpressExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer bindIdNotEquals;

    /**
     * 用于批量操作
     */
    private String bindIdIn;

    /**
     * ID
     */
    private Integer bindId;

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 快递公司ID
     */
    private Integer expressId;

    /**
     * 公司名称
     */
    private String expressName;

    /**
     * 公司名称,用于模糊查询
     */
    private String expressNameLike;

    /**
     * 快递公司状态，平台是否启用：1-启用，0-不启用
     */
    private String expressState;

    /**
     * expressStateIn，用于批量操作
     */
    private String expressStateIn;

    /**
     * expressStateNotIn，用于批量操作
     */
    private String expressStateNotIn;

    /**
     * expressStateNotEquals，用于批量操作
     */
    private String expressStateNotEquals;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 编号
     */
    private String expressCode;

    /**
     * 公司网址
     */
    private String expressWebsite;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照bindId倒序排列
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