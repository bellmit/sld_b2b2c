package com.slodon.b2b2c.promotion.example;

import java.io.Serializable;
import java.util.Date;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

@Data
public class DiscountPcDecoExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer integralDecoIdNotEquals;

    /**
     * 用于批量操作
     */
    private String integralDecoIdIn;

    /**
     * 装修模板数据id
     */
    private Integer integralDecoId;

    /**
     * 装修模板id
     */
    private Integer tplId;

    /**
     * 模板风格
     */
    private String tplName;

    /**
     * 模板风格,用于模糊查询
     */
    private String tplNameLike;

    /**
     * 装修模板名称
     */
    private String tplTypeName;

    /**
     * 装修模板名称,用于模糊查询
     */
    private String tplTypeNameLike;

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
    private Integer createAdminId;

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
    private Integer updateAdminId;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否启用；0==不启用，1==启用
     */
    private Integer isEnable;

    /**
     * 实例化装修模板(html片段)
     */
    private String html;

    /**
     * 装修模板数据(json)
     */
    private String json;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照integralDecoId倒序排列
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