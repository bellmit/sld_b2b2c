package com.slodon.b2b2c.cms.example;

import java.io.Serializable;
import java.util.Date;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

@Data
public class ArticleExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer articleIdNotEquals;

    /**
     * 用于批量操作
     */
    private String articleIdIn;

    /**
     * 文章id
     */
    private Integer articleId;

    /**
     * 分类id
     */
    private Integer categoryId;

    /**
     * 新闻标题
     */
    private String title;

    /**
     * 新闻标题,用于模糊查询
     */
    private String titleLike;

    /**
     * 外部链接的URL
     */
    private String outUrl;

    /**
     * 显示状态：0、不显示；1、显示
     */
    private Integer state;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建人id
     */
    private Integer createId;

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
     * 内容
     */
    private String content;

    /**
     * 内容,用于模糊查询
     */
    private String contentLike;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照articleId倒序排列
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