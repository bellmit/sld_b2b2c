package com.slodon.b2b2c.promotion.example;

import java.io.Serializable;
import java.util.Date;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

@Data
public class CouponUseLogExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer logIdNotEquals;

    /**
     * 用于批量操作
     */
    private String logIdIn;

    /**
     * 日志ID
     */
    private Integer logId;

    /**
     * 优惠券编码
     */
    private String couponCode;

    /**
     * 会员id
     */
    private Integer memberId;

    /**
     * 会员名称
     */
    private String memberName;

    /**
     * 会员名称,用于模糊查询
     */
    private String memberNameLike;

    /**
     * 使用订单号
     */
    private String orderSn;

    /**
     * 使用订单号,用于模糊查询
     */
    private String orderSnLike;

    /**
     * 所属店铺
     */
    private Long storeId;

    /**
     * 优惠券操作类型：1-领取；2-下单消费；3-订单取消返回；4-商品退货返回；5-积分兑换
     */
    private Integer logType;

    /**
     * 大于等于开始时间
     */
    private Date logTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date logTimeBefore;

    /**
     * 详细内容描述
     */
    private String logContent;

    /**
     * 详细内容描述,用于模糊查询
     */
    private String logContentLike;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照logId倒序排列
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