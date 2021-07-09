package com.slodon.b2b2c.system.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * PC商城装修模板实例化数据表example
 */
@Data
public class TplPcMallDataExample implements Serializable {
    private static final long serialVersionUID = 8851720395626732318L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer dataIdNotEquals;

    /**
     * 用于批量操作
     */
    private String dataIdIn;

    /**
     * 装修模板数据id
     */
    private Integer dataId;

    /**
     * 装修模板id
     */
    private Integer tplPcId;

    /**
     * 模板风格
     */
    private String tplPcName;

    /**
     * 模板风格,用于模糊查询
     */
    private String tplPcNameLike;

    /**
     * 模板类型
     */
    private String tplPcType;

    /**
     * 装修模板名称
     */
    private String tplPcTypeName;

    /**
     * 装修模板名称,用于模糊查询
     */
    private String tplPcTypeNameLike;

    /**
     * 装修模板数据名称(用于管理端展示)
     */
    private String name;

    /**
     * 装修模板数据名称(用于管理端展示),用于模糊查询
     */
    private String nameLike;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 创建人id
     */
    private Long createUserId;

    /**
     * 大于等于开始时间
     */
    private Date updateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date updateTimeBefore;

    /**
     * 更新人id
     */
    private Long updateUserId;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否启用；0==不启用，1==启用
     */
    private Integer isEnable;

    /**
     * 店铺id(0为平台)
     */
    private Long storeId;

    /**
     * 实例化装修模板(html片段)
     */
    private String html;

    /**
     * 装修模板数据(json)
     */
    private String json;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照dataId倒序排列
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