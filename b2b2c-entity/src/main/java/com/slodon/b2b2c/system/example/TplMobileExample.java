package com.slodon.b2b2c.system.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public class TplMobileExample implements Serializable {
    private static final long serialVersionUID = -3553063530030419765L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer tplIdNotEquals;

    /**
     * 用于批量操作
     */
    private String tplIdIn;

    /**
     * 模板id
     */
    private Integer tplId;

    /**
     * 名称
     */
    private String name;

    /**
     * 名称,用于模糊查询
     */
    private String nameLike;

    /**
     * 模板类型
     */
    private String type;

    /**
     * 图片地址
     */
    private String icon;

    /**
     * 是否展示 0--否，1--是
     */
    private Integer isUse;

    /**
     * 模板应用范围（商城首页==home,专题==topic,店铺首页==seller,活动==activity）
     */
    private String apply;

    /**
     * 排序，值越小级别越高
     */
    private String sort;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照tplId倒序排列
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