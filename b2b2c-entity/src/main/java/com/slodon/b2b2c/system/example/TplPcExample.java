package com.slodon.b2b2c.system.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public class TplPcExample implements Serializable {
    private static final long serialVersionUID = 3860500158484615452L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer tplPcIdNotEquals;

    /**
     * 用于批量操作
     */
    private String tplPcIdIn;

    /**
     * 模板id
     */
    private Integer tplPcId;

    /**
     * 模板分类
     */
    private String type;

    /**
     * 模板唯一标识
     */
    private String code;

    /**
     * 模板分类名称
     */
    private String typeName;

    /**
     * 模板分类名称,用于模糊查询
     */
    private String typeNameLike;

    /**
     * 模板风格
     */
    private String name;

    /**
     * 模板风格,用于模糊查询
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
     * 应用位置 1==商城首页；2==店铺首页，3==通用
     */
    private Integer client;

    /**
     * 是否可实例化 0==否，1==是
     */
    private Integer isInstance;

    /**
     * 模板缩略图
     */
    private String image;

    /**
     * 模板描述
     */
    private String desc;

    /**
     * 模板数据
     */
    private String data;

    /**
     * 默认模板实例数据
     */
    private String defaultData;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照tplPcId倒序排列
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

    /**
     * client批量查询（应用位置 1==商城首页；2==店铺首页，3==通用）
     */
    private String clientIn;
}