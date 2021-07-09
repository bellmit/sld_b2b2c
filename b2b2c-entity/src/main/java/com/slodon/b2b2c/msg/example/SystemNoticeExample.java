package com.slodon.b2b2c.msg.example;

import java.io.Serializable;
import java.util.Date;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

@Data
public class SystemNoticeExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer noticeIdNotEquals;

    /**
     * 用于批量操作
     */
    private String noticeIdIn;

    /**
     * 公告id
     */
    private Integer noticeId;

    /**
     * 标题
     */
    private String title;

    /**
     * 标题,用于模糊查询
     */
    private String titleLike;

    /**
     * 描述
     */
    private String description;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 创建人（管理员id）
     */
    private Integer createAdminId;

    /**
     * 创建人（管理员）名称
     */
    private String createAdminName;

    /**
     * 创建人（管理员）名称,用于模糊查询
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
     * 更新人（管理员id）
     */
    private Integer updateAdminId;

    /**
     * 更新人（管理员）名称
     */
    private String updateAdminName;

    /**
     * 更新人（管理员）名称,用于模糊查询
     */
    private String updateAdminNameLike;

    /**
     * 是否置顶 0-否 1-是
     */
    private Integer isTop;

    /**
     * 排序号 越小越靠前
     */
    private Integer sort;

    /**
     * 内容
     */
    private String content;

    /**
     * 内容,用于模糊查询
     */
    private String contentLike;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照noticeId倒序排列
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