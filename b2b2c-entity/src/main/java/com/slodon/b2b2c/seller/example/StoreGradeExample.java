package com.slodon.b2b2c.seller.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public class StoreGradeExample implements Serializable {
    private static final long serialVersionUID = -4592228838844196963L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer gradeIdNotEquals;

    /**
     * 用于批量操作
     */
    private String gradeIdIn;

    /**
     * 等级ID
     */
    private Integer gradeId;

    /**
     * 等级名称
     */
    private String gradeName;

    /**
     * 等级名称,用于模糊查询
     */
    private String gradeNameLike;

    /**
     * 允许发布的商品数量
     */
    private Integer goodsLimit;

    /**
     * 允许推荐的商品数量
     */
    private Integer recommendLimit;

    /**
     * 费用
     */
    private String price;

    /**
     * 审核：0为否，1为是，默认为1
     */
    private Integer confirm;

    /**
     * 级别，数目越大级别越高
     */
    private Integer sort;

    /**
     * 申请说明
     */
    private String description;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照gradeId倒序排列
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