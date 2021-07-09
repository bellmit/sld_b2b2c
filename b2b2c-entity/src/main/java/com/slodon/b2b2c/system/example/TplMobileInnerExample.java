package com.slodon.b2b2c.system.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public class TplMobileInnerExample implements Serializable {
    private static final long serialVersionUID = 8508872264211234732L;
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
     * 模板类型
     */
    private String type;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 模板名称,用于模糊查询
     */
    private String nameLike;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否启用，0==不启用；1==启用
     */
    private Integer isEnable;

    /**
     * 模板缩略图
     */
    private String image;

    /**
     * 模板描述
     */
    private String desc;

    /**
     * 模板应用范围（商城首页==home,专题==topic,店铺首页==seller,活动==activity）
     */
    private String apply;

    /**
     * 模板数据
     */
    private String data;

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