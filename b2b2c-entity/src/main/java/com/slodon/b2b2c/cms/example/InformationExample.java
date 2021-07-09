package com.slodon.b2b2c.cms.example;


import java.io.Serializable;
import java.util.Date;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

@Data
public class InformationExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer informationIdNotEquals;

    /**
     * 用于批量操作
     */
    private String informationIdIn;

    /**
     * 资讯id
     */
    private Integer informationId;

    /**
     * 资讯分类id
     */
    private Integer cateId;

    /**
     * 分类名称
     */
    private String cateName;

    /**
     * 分类名称,用于模糊查询
     */
    private String cateNameLike;

    /**
     * 资讯标题
     */
    private String title;

    /**
     * 资讯标题,用于模糊查询
     */
    private String titleLike;

    /**
     * 资讯封面
     */
    private String coverImage;

    /**
     * 浏览量
     */
    private Integer pageView;

    /**
     * 是否推荐，0-否，1-是，默认0
     */
    private Integer isRecommend;

    /**
     * 是否显示，0-否，1-是，默认1
     */
    private Integer isShow;

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
     * 创建人名称
     */
    private String createAdminName;

    /**
     * 创建人名称,用于模糊查询
     */
    private String createAdminNameLike;

    /**
     * 大于等于开始时间
     */
    private Date updateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date updateTimeBefore;

    /**
     * 修改人id
     */
    private Integer updateAdminId;

    /**
     * 修改人名称
     */
    private String updateAdminName;

    /**
     * 修改人名称,用于模糊查询
     */
    private String updateAdminNameLike;

    /**
     * 资讯内容
     */
    private String content;

    /**
     * 资讯内容,用于模糊查询
     */
    private String contentLike;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照informationId倒序排列
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