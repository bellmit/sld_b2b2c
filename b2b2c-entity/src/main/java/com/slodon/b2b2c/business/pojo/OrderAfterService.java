package com.slodon.b2b2c.business.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单售后服务表
 */
@Data
public class OrderAfterService implements Serializable {
    private static final long serialVersionUID = 3222519744548081465L;
    @ApiModelProperty("售后服务id")
    private Integer afsId;

    @ApiModelProperty("售后服务单号")
    private String afsSn;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("用户ID")
    private Integer memberId;

    @ApiModelProperty("用户名称")
    private String memberName;

    @ApiModelProperty("商品ID")
    private Long goodsId;

    @ApiModelProperty("订单货品明细ID")
    private Long orderProductId;

    @ApiModelProperty("申请售后的货品数量")
    private Integer afsNum;

    @ApiModelProperty("商户的售后服务详细收货地址，从store_address表获取")
    private String storeAfsAddress;

    @ApiModelProperty("换件/退件物流公司")
    private String buyerExpressName;

    @ApiModelProperty("换件/退件快递单号")
    private String buyerExpressNumber;

    @ApiModelProperty("换件/退件物流公司快递代码")
    private String buyerExpressCode;

    @ApiModelProperty("用户发货时间")
    private Date buyerDeliverTime;

    @ApiModelProperty("申请提交图片")
    private String applyImage;

    @ApiModelProperty("申请人姓名")
    private String contactName;

    @ApiModelProperty("申请人联系电话")
    private String contactPhone;

    @ApiModelProperty("售后服务端类型：1-退货退款单，2-换货单，3-仅退款单")
    private Integer afsType;

    @ApiModelProperty("申请售后服务原因")
    private String applyReasonContent;

    @ApiModelProperty("申请售后详细问题描述")
    private String afsDescription;

    @ApiModelProperty("用户申请时间")
    private Date buyerApplyTime;

    @ApiModelProperty("店铺审核时间")
    private Date storeAuditTime;

    @ApiModelProperty("平台审核时间")
    private Date platformAuditTime;

    @ApiModelProperty("用户备注")
    private String buyerRemark;

    @ApiModelProperty("平台备注")
    private String platformRemark;

    @ApiModelProperty("商户备注")
    private String storeRemark;

    @ApiModelProperty("商家收货/拒收时间")
    private Date storeReceiveTime;

    @ApiModelProperty("货物状态：0-未收到货，1-已收到货")
    private Integer goodsState;
}