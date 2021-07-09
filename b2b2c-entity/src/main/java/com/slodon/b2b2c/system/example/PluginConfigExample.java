package com.slodon.b2b2c.system.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PluginConfigExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer pIdNotEquals;

    /**
     * 用于批量操作
     */
    private String pIdIn;

    /**
     * 自增主键
     */
    private Integer pId;

    /**
     * 插件ID
     */
    private String pluginId;

    /**
     * 插件相关配置
     */
    private String pluginConfig;

    /**
     * 插件版本
     */
    private String version;

    /**
     * 是否启用（0-关闭，1-启用）
     */
    private Integer isEnable;

    /**
     * 插件安装日期
     */
    private Date installDate;

    /**
     * 最后更新配置时间
     */
    private Date updateDate;

    /**
     * 插件显示排序
     */
    private Integer sort;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照pId倒序排列
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