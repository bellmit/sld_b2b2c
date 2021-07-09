package com.slodon.b2b2c.goods.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品服务标签（比如：7天无理由退货、急速发货）example
 */
@Data
public class GoodsServiceLabelExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer labelIdNotEquals;

    /**
     * 用于批量操作
     */
    private String labelIdIn;

    /**
     * 标签id
     */
    private Integer labelId;

    /**
     * 标签名称
     */
    private String labelName;

    /**
     * 标签名称,用于模糊查询
     */
    private String labelNameLike;

    /**
     * 标签描述
     */
    private String description;

    /**
     * 创建人ID（系统管理员）
     */
    private Integer createAdminId;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date updateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date updateTimeBefore;

    /**
     * 更新人
     */
    private String updater;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照labelId倒序排列
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