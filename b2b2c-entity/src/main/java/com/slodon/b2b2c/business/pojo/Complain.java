package com.slodon.b2b2c.business.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 投诉表
 */
@Data
public class Complain implements Serializable {
    private static final long serialVersionUID = -3937284632802808437L;
    @ApiModelProperty("投诉id")
    private Integer complainId;

    @ApiModelProperty("投诉主题id")
    private Integer complainSubjectId;

    @ApiModelProperty("售后服务单号")
    private String afsSn;

    @ApiModelProperty("订单编号")
    private String orderSn;

    @ApiModelProperty("订单货品明细id")
    private Long orderProductId;

    @ApiModelProperty("商品id")
    private Long goodsId;

    @ApiModelProperty("商品货品ID")
    private Long productId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品图片")
    private String goodsImage;

    @ApiModelProperty("商品规格")
    private String specValues;

    @ApiModelProperty("投诉会员id")
    private Integer complainMemberId;

    @ApiModelProperty("投诉会员名称")
    private String complainMemberName;

    @ApiModelProperty("被投诉店铺id")
    private Long storeId;

    @ApiModelProperty("被投诉店铺名称")
    private String storeName;

    @ApiModelProperty("投诉内容")
    private String complainContent;

    @ApiModelProperty("投诉图片（最多6张图片）")
    private String complainPic;

    @ApiModelProperty("投诉时间")
    private Date complainTime;

    @ApiModelProperty("投诉审核时间")
    private Date complainAuditTime;

    @ApiModelProperty("投诉审核管理员id；0位超期系统自动处理")
    private Integer complainAuditAdminId;

    @ApiModelProperty("拒绝原因")
    private String refuseReason;

    @ApiModelProperty("商户申诉内容")
    private String appealContent;

    @ApiModelProperty("商户申诉图片（最多6张图片）")
    private String appealImage;

    @ApiModelProperty("商户申诉时间")
    private Date appealTime;

    @ApiModelProperty("申诉的店铺管理员ID")
    private Long appealVendorId;

    @ApiModelProperty("平台仲裁意见")
    private String adminHandleContent;

    @ApiModelProperty("平台仲裁时间")
    private Date adminHandleTime;

    @ApiModelProperty("平台仲裁管理员id")
    private Integer adminHandleId;

    @ApiModelProperty("投诉对话状态，1-新投诉/2-待申诉(投诉通过转给商家)/3-对话中(商家已申诉)/4-待仲裁/5-已撤销/6-会员胜诉/7-商家胜诉）")
    private Integer complainState;

    @ApiModelProperty("处理截止时间（用于前台展示)")
    private Date handleDeadline;
}