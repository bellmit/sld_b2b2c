package com.slodon.b2b2c.seller.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class StoreCommentExample implements Serializable {
    private static final long serialVersionUID = 4077550114726996937L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer commentIdNotEquals;

    /**
     * 用于批量操作
     */
    private String commentIdIn;

    /**
     * 评论id
     */
    private Integer commentId;

    /**
     * 评价人ID
     */
    private Integer memberId;

    /**
     * 评价人账号
     */
    private String memberName;

    /**
     * 评价人账号,用于模糊查询
     */
    private String memberNameLike;

    /**
     * 评价商家服务
     */
    private String storeAttitude;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 所属商家
     */
    private Long storeId;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 店铺名称,用于模糊查询
     */
    private String storeNameLike;

    /**
     * 订单编号
     */
    private String orderSn;

    /**
     * 订单编号,用于模糊查询
     */
    private String orderSnLike;

    /**
     * 描述相符(1到5星)
     */
    private Integer description;

    /**
     * 服务态度(1到5星)
     */
    private Integer serviceAttitude;

    /**
     * 发货速度(1到5星)
     */
    private Integer deliverSpeed;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照commentId倒序排列
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