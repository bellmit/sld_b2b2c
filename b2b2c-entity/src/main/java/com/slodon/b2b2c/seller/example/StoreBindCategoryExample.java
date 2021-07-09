package com.slodon.b2b2c.seller.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class StoreBindCategoryExample implements Serializable {
    private static final long serialVersionUID = -3558291226426765582L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer bindIdNotEquals;

    /**
     * 用于批量操作
     */
    private String bindIdIn;

    /**
     * 绑定id
     */
    private Integer bindId;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 用于查询时的判断
     */
    private Integer storeIdNotEquals;

    /**
     * 申请人
     */
    private Long createVendorId;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 申请分类名称,提交类目组合
     */
    private String goodsCateName;

    /**
     * 申请分类名称,提交类目组合,用于模糊查询
     */
    private String goodsCateNameLike;

    /**
     * 分佣比例
     */
    private BigDecimal scaling;

    /**
     * 1-提交审核;2-审核通过;3-审核失败;
     */
    private Integer state;

    /**
     * 审核管理员ID
     */
    private Integer auditAdminId;

    /**
     * 大于等于开始时间
     */
    private Date auditTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date auditTimeBefore;

    /**
     * 审核拒绝理由
     */
    private String refuseReason;

    /**
     * 审核拒绝理由,用于模糊查询
     */
    private String refuseReasonLike;

    /**
     * 申请分类id（一级）
     */
    private Integer goodsCategoryId1;

    /**
     * 申请分类id（二级）
     */
    private Integer goodsCategoryId2;

    /**
     * 申请分类id（三级）
     */
    private Integer goodsCategoryId3;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照bindId倒序排列
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
     * 模糊查询，店铺名称
     */
    private String storeNameLike;

    /**
     * 1-提交审核;2-审核通过;3-审核失败;查询判断
     */
    private Integer stateNotEquals;
}