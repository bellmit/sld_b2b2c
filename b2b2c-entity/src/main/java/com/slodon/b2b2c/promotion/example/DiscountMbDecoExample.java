package com.slodon.b2b2c.promotion.example;

import java.io.Serializable;
import java.util.Date;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

@Data
public class DiscountMbDecoExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer idNotEquals;

    /**
     * 用于批量操作
     */
    private String idIn;

    /**
     * 装修页id
     */
    private Integer id;

    /**
     * 装修页名称
     */
    private String name;

    /**
     * 装修页名称,用于模糊查询
     */
    private String nameLike;

    /**
     * 装修页类型(首页home/专题topic/店铺seller/活动activity)
     */
    private String type;

    /**
     * 店铺id,0==平台
     */
    private Long storeId;

    /**
     * 是否启用
     */
    private Integer android;

    /**
     * 是否启用
     */
    private Integer ios;

    /**
     * 是否启用
     */
    private Integer h5;

    /**
     * 是否启用
     */
    private Integer weixinXcx;

    /**
     * 是否启用
     */
    private Integer alipayXcx;

    /**
     * 创建人
     */
    private Integer createUserId;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 更新人
     */
    private Integer updateUserId;

    /**
     * 大于等于开始时间
     */
    private Date updateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date updateTimeBefore;

    /**
     * 装修页数据
     */
    private String data;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照id倒序排列
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