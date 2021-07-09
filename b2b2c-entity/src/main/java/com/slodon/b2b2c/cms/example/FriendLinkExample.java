package com.slodon.b2b2c.cms.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class FriendLinkExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer linkIdNotEquals;

    /**
     * 用于批量操作
     */
    private String linkIdIn;

    /**
     * 链接id
     */
    private Integer linkId;

    /**
     * 链接名称
     */
    private String linkName;

    /**
     * 链接名称,用于模糊查询
     */
    private String linkNameLike;

    /**
     * 链接图片
     */
    private String linkImage;

    /**
     * 展示方式：1、文字；2、图片
     */
    private Integer showType;

    /**
     * 链接url
     */
    private String linkUrl;

    /**
     * 排序：数字越小，越靠前
     */
    private Integer sort;

    /**
     * 状态：0、不可见；1、可见
     */
    private Integer state;

    /**
     * 创建管理员id
     */
    private Integer createAdminId;

    /**
     * 创建管理员名称
     */
    private String createAdminName;

    /**
     * 创建管理员名称,用于模糊查询
     */
    private String createAdminNameLike;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 更新管理员id
     */
    private Integer updateAdminId;

    /**
     * 更新管理员名称
     */
    private String updateAdminName;

    /**
     * 更新管理员名称,用于模糊查询
     */
    private String updateAdminNameLike;

    /**
     * 大于等于开始时间
     */
    private Date updateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date updateTimeBefore;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照linkId倒序排列
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