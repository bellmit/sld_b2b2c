package com.slodon.b2b2c.promotion.example;

import java.io.Serializable;
import java.util.Date;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

@Data
public class DiscountExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer discountIdNotEquals;

    /**
     * 用于批量操作
     */
    private String discountIdIn;

    /**
     * 折扣活动ID
     */
    private Integer discountId;

    /**
     * 活动名称
     */
    private String discountName;

    /**
     * 活动名称,用于模糊查询
     */
    private String discountNameLike;

    /**
     * 大于等于开始时间
     */
    private Date startTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date startTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date endTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date endTimeBefore;

    /**
     * 限购数量，0-不限制，其他数值为具体限购数量
     */
    private Integer buyLimit;

    /**
     * 是否指定价格1-指定，0-不指定；不指定价格需填折扣比例
     */
    private Integer isCustomPrice;

    /**
     * 折扣比例
     */
    private Integer discountRate;

    /**
     * 商户ID
     */
    private Long storeId;

    /**
     * 状态：1-可用，0-不可用
     */
    private Integer state;

    /**
     * 使用json存储规则，对应的业务类进行解析处理
     */
    private String rule;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 创建商家ID，vendor表
     */
    private Long vendorId;

    /**
     * 创建人名称
     */
    private String vendorName;

    /**
     * 创建人名称,用于模糊查询
     */
    private String vendorNameLike;

    /**
     * 大于等于开始时间
     */
    private Date updateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date updateTimeBefore;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照discountId倒序排列
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