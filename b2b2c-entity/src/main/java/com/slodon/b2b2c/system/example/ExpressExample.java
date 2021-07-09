package com.slodon.b2b2c.system.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public class ExpressExample implements Serializable {
    private static final long serialVersionUID = -4058914000295769419L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer expressIdNotEquals;

    /**
     * 用于批量操作
     */
    private String expressIdIn;

    /**
     * 用于批量操作
     */
    private String expressIdNotIn;

    /**
     * 物流ID
     */
    private Integer expressId;

    /**
     * 物流名称
     */
    private String expressName;

    /**
     * 物流名称,用于模糊查询
     */
    private String expressNameLike;

    /**
     * 物流状态，平台是否启用：1-启用，0-不启用
     */
    private Integer expressState;

    /**
     * 物流编号
     */
    private String expressCode;

    /**
     * 首字母
     */
    private String expressLetter;

    /**
     * 1-512；数值小排序靠前
     */
    private Integer sort;

    /**
     * 公司网址
     */
    private String website;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照expressId倒序排列
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