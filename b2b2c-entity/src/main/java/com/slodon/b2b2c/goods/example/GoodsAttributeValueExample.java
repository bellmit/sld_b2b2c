package com.slodon.b2b2c.goods.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class GoodsAttributeValueExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer valueIdNotEquals;

    /**
     * 用于批量操作
     */
    private String valueIdIn;

    /**
     * 主键id
     */
    private Integer valueId;

    /**
     * 属性ID
     */
    private Integer attributeId;

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
     * 属性值
     */
    private String attributeValue;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照valueId倒序排列
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