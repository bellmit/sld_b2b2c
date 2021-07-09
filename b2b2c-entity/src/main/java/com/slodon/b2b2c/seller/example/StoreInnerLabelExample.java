package com.slodon.b2b2c.seller.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class StoreInnerLabelExample implements Serializable {
    private static final long serialVersionUID = -6375375823445251934L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer innerLabelIdNotEquals;

    /**
     * 用于批量操作
     */
    private String innerLabelIdIn;

    /**
     * 店铺内分类ID
     */
    private Integer innerLabelId;

    /**
     * 分类名称
     */
    private String innerLabelName;

    /**
     * 分类名称,用于模糊查询
     */
    private String innerLabelNameLike;

    /**
     * 店铺内分类排序
     */
    private Integer innerLabelSort;

    /**
     * 是否显示，0-不显示，1-显示
     */
    private Integer isShow;

    /**
     * 父分类ID
     */
    private Integer parentInnerLabelId;

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 创建人ID
     */
    private Long createVendorId;

    /**
     * 创建人名字
     */
    private String createVendorName;

    /**
     * 创建人名字,用于模糊查询
     */
    private String createVendorNameLike;

    /**
     * 大于等于开始时间
     */
    private Date updateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date updateTimeBefore;

    /**
     * 更新人ID
     */
    private Long updateVendorId;

    /**
     * 更新人名字
     */
    private String updateVendorName;

    /**
     * 更新人名字,用于模糊查询
     */
    private String updateVendorNameLike;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照innerLabelId倒序排列
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