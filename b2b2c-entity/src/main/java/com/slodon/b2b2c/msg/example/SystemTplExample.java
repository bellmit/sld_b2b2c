package com.slodon.b2b2c.msg.example;

import java.io.Serializable;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

@Data
public class SystemTplExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private String tplCodeNotEquals;

    /**
     * 用于批量操作
     */
    private String tplCodeIn;

    /**
     * 模板编码
     */
    private String tplCode;

    /**
     * 模板名称
     */
    private String tplName;

    /**
     * 模板名称,用于模糊查询
     */
    private String tplNameLike;

    /**
     * 模板类型
     */
    private String tplTypeCode;

    /**
     * 模板数据
     */
    private String tplContent;

    /**
     * 模板数据,用于模糊查询
     */
    private String tplContentLike;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照tplCode倒序排列
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