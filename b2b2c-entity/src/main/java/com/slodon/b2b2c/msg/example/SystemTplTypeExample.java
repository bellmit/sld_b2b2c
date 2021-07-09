package com.slodon.b2b2c.msg.example;

import java.io.Serializable;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

@Data
public class SystemTplTypeExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private String tplTypeCodeNotEquals;

    /**
     * 用于批量操作
     */
    private String tplTypeCodeIn;

    /**
     * 消息模板分类编码
     */
    private String tplTypeCode;

    /**
     * 消息模板分类名称
     */
    private String tplName;

    /**
     * 消息模板分类名称,用于模糊查询
     */
    private String tplNameLike;

    /**
     * 消息模板分类来源 0==系统消息, 1==会员消息, 2==商户消息
     */
    private Integer tplSource;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照tplTypeCode倒序排列
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