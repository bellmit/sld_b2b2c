package com.slodon.b2b2c.business.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OrderReplacementExample implements Serializable {
    private static final long serialVersionUID = 1061058551028109931L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer replacementIdNotEquals;

    /**
     * 用于批量操作
     */
    private String replacementIdIn;

    /**
     * 换货id
     */
    private Integer replacementId;

    /**
     * 售后服务单号
     */
    private String afsSn;

    /**
     * 售后服务单号,用于模糊查询
     */
    private String afsSnLike;

    /**
     * 店铺id
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
     * 订单号
     */
    private String orderSn;

    /**
     * 订单号,用于模糊查询
     */
    private String orderSnLike;

    /**
     * 换货数量
     */
    private Integer replacementNum;

    /**
     * 新货品的ID（可能换同商品的不同sku）
     */
    private Long newProductId;

    /**
     * 收货详细地址
     */
    private String buyerReceiveAddress;

    /**
     * 用户收货人
     */
    private String buyerReceiveName;

    /**
     * 用户收货人,用于模糊查询
     */
    private String buyerReceiveNameLike;

    /**
     * 用户收货人电话
     */
    private String buyerReceivePhone;

    /**
     * 店铺发货物流公司
     */
    private String storeExpressName;

    /**
     * 店铺发货物流公司,用于模糊查询
     */
    private String storeExpressNameLike;

    /**
     * 店铺发货物流公司快递代码
     */
    private String storeExpressCode;

    /**
     * 店铺发货快递单号
     */
    private String storeDeliveryNumber;

    /**
     * 大于等于开始时间
     */
    private Date storeDeliveryTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date storeDeliveryTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date completeTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date completeTimeBefore;

    /**
     * 换货状态：100-买家申请；101-买家发货；200-卖家审核失败；201-卖家审核通过；202-卖家确认收货；203-卖家拒收；204-卖家发货；301-买家收货（已完成）
     */
    private Integer state;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照replacementId倒序排列
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