package com.slodon.b2b2c.cms.example;


import java.io.Serializable;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

@Data
public class InformationIndexExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer indexIdNotEquals;

    /**
     * 用于批量操作
     */
    private String indexIdIn;

    /**
     * 首页id
     */
    private Integer indexId;

    /**
     * 资讯首页轮播广告数据
     */
    private String data;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照indexId倒序排列
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