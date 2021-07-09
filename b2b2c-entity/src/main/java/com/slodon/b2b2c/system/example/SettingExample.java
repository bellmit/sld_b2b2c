package com.slodon.b2b2c.system.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public class SettingExample implements Serializable {
    private static final long serialVersionUID = 3807782901269302490L;
    /**
     * 用于编辑时的重复判断
     */
    private String nameNotEquals;

    /**
     * 用于批量操作
     */
    private String nameIn;

    /**
     * 名称
     */
    private String name;

    /**
     * 名称,用于模糊查询
     */
    private String nameLike;

    /**
     * 主题，前端展示使用
     */
    private String title;

    /**
     * 主题，前端展示使用,用于模糊查询
     */
    private String titleLike;

    /**
     * 名称描述
     */
    private String description;

    /**
     * 类型，1-字符串，2-图片，3-固定不能修改，4-开关
     */
    private Integer type;

    /**
     * 值
     */
    private String value;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照name倒序排列
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