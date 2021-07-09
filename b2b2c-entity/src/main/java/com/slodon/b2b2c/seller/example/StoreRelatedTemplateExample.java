package com.slodon.b2b2c.seller.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public class StoreRelatedTemplateExample implements Serializable {
    private static final long serialVersionUID = -618888225321918183L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer templateIdNotEquals;

    /**
     * 用于批量操作
     */
    private String templateIdIn;

    /**
     * 模板id
     */
    private Integer templateId;

    /**
     * 模版名称
     */
    private String templateName;

    /**
     * 模版名称,用于模糊查询
     */
    private String templateNameLike;

    /**
     * 模版位置(1-顶部，2-底部）
     */
    private Integer templatePosition;

    /**
     * 模版内容
     */
    private String templateContent;

    /**
     * 模版内容,用于模糊查询
     */
    private String templateContentLike;

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照templateId倒序排列
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