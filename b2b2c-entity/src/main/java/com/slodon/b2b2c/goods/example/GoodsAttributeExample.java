package com.slodon.b2b2c.goods.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class GoodsAttributeExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer attributeIdNotEquals;

    /**
     * 用于批量操作
     */
    private String attributeIdIn;

    /**
     * 属性id
     */
    private Integer attributeId;

    /**
     * 属性名称
     */
    private String attributeName;

    /**
     * 属性名称,用于模糊查询
     */
    private String attributeNameLike;

    /**
     * 创建人ID
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
     * 排序0到255，越小越靠前展示
     */
    private Integer sort;

    /**
     * 是否展示：0-不展示，1-展示
     */
    private Integer isShow;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照attributeId倒序排列
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