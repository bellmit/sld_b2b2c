package com.slodon.b2b2c.integral.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 积分商城商品标签example
 */
@Data
public class IntegralGoodsLabelExample implements Serializable {
    private static final long serialVersionUID = 1142817318719179715L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer labelIdNotEquals;

    /**
     * 用于批量操作
     */
    private String labelIdIn;

    /**
     * 标签id
     */
    private Integer labelId;

    /**
     * 标签名称
     */
    private String labelName;

    /**
     * 标签名称,用于模糊查询
     */
    private String labelNameLike;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 上级标签id
     */
    private Integer parentLabelId;

    /**
     * 级别，1-一级，2-二级
     */
    private Integer grade;

    /**
     * 二级标签图片
     */
    private String image;

    /**
     * 标签状态：0、不显示；1、显示
     */
    private Integer state;

    /**
     * stateIn，用于批量操作
     */
    private String stateIn;

    /**
     * stateNotIn，用于批量操作
     */
    private String stateNotIn;

    /**
     * stateNotEquals，用于批量操作
     */
    private Integer stateNotEquals;

    /**
     * 创建人ID
     */
    private Integer createAdminId;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 更新人ID
     */
    private Integer updateAdminId;

    /**
     * 大于等于开始时间
     */
    private Date updateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date updateTimeBefore;

    /**
     * 一级分类广告图
     */
    private String data;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照labelId倒序排列
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
     * 用于处理一级标签商品不为空标识
     */
    private String queryGoods;

    /**
     * 用于处理二级标签商品不为空标识
     */
    private String queryGoods2;
}