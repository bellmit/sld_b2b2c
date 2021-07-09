package com.slodon.b2b2c.business.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ComplainExample implements Serializable {
    private static final long serialVersionUID = -9203496150230926153L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer complainIdNotEquals;

    /**
     * 用于批量操作
     */
    private String complainIdIn;

    /**
     * 投诉id
     */
    private Integer complainId;

    /**
     * 投诉主题id
     */
    private Integer complainSubjectId;

    /**
     * 售后服务单号
     */
    private String afsSn;

    /**
     * 售后服务单号,用于模糊查询
     */
    private String afsSnLike;

    /**
     * 订单编号
     */
    private String orderSn;

    /**
     * 订单编号,用于模糊查询
     */
    private String orderSnLike;

    /**
     * 订单货品明细id
     */
    private Long orderProductId;

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 商品货品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品名称,用于模糊查询
     */
    private String goodsNameLike;

    /**
     * 商品图片
     */
    private String goodsImage;

    /**
     * 商品规格
     */
    private String specValues;

    /**
     * 投诉会员id
     */
    private Integer complainMemberId;

    /**
     * 投诉会员名称
     */
    private String complainMemberName;

    /**
     * 投诉会员名称,用于模糊查询
     */
    private String complainMemberNameLike;

    /**
     * 被投诉店铺id
     */
    private Long storeId;

    /**
     * 被投诉店铺名称
     */
    private String storeName;

    /**
     * 被投诉店铺名称,用于模糊查询
     */
    private String storeNameLike;

    /**
     * 投诉内容
     */
    private String complainContent;

    /**
     * 投诉内容,用于模糊查询
     */
    private String complainContentLike;

    /**
     * 投诉图片（最多6张图片）
     */
    private String complainPic;

    /**
     * 大于等于开始时间
     */
    private Date complainTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date complainTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date complainAuditTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date complainAuditTimeBefore;

    /**
     * 投诉审核管理员id；0位超期系统自动处理
     */
    private Integer complainAuditAdminId;

    /**
     *  拒绝原因
     */
    private String refuseReason;

    /**
     * 商户申诉内容
     */
    private String appealContent;

    /**
     * 商户申诉内容,用于模糊查询
     */
    private String appealContentLike;

    /**
     * 商户申诉图片（最多6张图片）
     */
    private String appealImage;

    /**
     * 大于等于开始时间
     */
    private Date appealTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date appealTimeBefore;

    /**
     * 申诉的店铺管理员ID
     */
    private Long appealVendorId;

    /**
     * 平台仲裁意见
     */
    private String adminHandleContent;

    /**
     * 平台仲裁意见,用于模糊查询
     */
    private String adminHandleContentLike;

    /**
     * 大于等于开始时间
     */
    private Date adminHandleTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date adminHandleTimeBefore;

    /**
     * 平台仲裁管理员id
     */
    private Integer adminHandleId;

    /**
     * 投诉对话状态，1-新投诉/2-待申诉(投诉通过转给商家)/3-对话中(商家已申诉)/4-待仲裁/5-已撤销/6-会员胜诉/7-商家胜诉）
     */
    private Integer complainState;

//    /**
//     * 商户申诉时间
//     */
//    private Date handleDeadline;
    /**
     * 大于等于开始时间
     */
    private Date handleDeadlineAfter;

    /**
     * 小于等于结束时间
     */
    private Date handleDeadlineBefore;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照complainId倒序排列
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