package com.slodon.b2b2c.system.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ReasonExample implements Serializable {
    private static final long serialVersionUID = -1262620594204984766L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer reasonIdNotEquals;

    /**
     * 用于批量操作
     */
    private String reasonIdIn;

    /**
     * 原因id
     */
    private Integer reasonId;

    /**
     * 原因id,用于模糊查询
     */
    private String reasonIdLike;

    /**
     * 原因类型：0-通用（不可编辑）；101-违规下架；102-商品审核拒绝；103-入驻审核拒绝；104-会员取消订单；105-仅退款-未收货；106-仅退款-已收货；107-退款退货原因；108-商户取消订单
     */
    private Integer type;

    /**
     * 理由
     */
    private String content;

    /**
     * 理由,用于模糊查询
     */
    private String contentLike;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否显示：1-显示，0-不显示
     */
    private Integer isShow;

    /**
     * 是否系统内置 1-内置（不可删除、不可修改），0-非内置（可删除、修改）
     */
    private Integer isInner;

    /**
     * 管理员ID
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
     * 排序条件，条件之间用逗号隔开，如果不传则按照reasonId倒序排列
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