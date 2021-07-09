package com.slodon.b2b2c.integral.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 积分商城订单表
 */
@Data
public class IntegralOrder implements Serializable {
    private static final long serialVersionUID = -9144608323472712562L;
    @ApiModelProperty("订单id")
    private Integer orderId;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("支付单号")
    private String paySn;

    @ApiModelProperty("商家ID")
    private Long storeId;

    @ApiModelProperty("商家名称")
    private String storeName;

    @ApiModelProperty("买家ID")
    private Integer memberId;

    @ApiModelProperty("买家name")
    private String memberName;

    @ApiModelProperty("支付成功时间")
    private Date payTime;

    @ApiModelProperty("订单创建时间")
    private Date createTime;

    @ApiModelProperty("订单完成时间")
    private Date finishTime;

    @ApiModelProperty("订单状态：0-已取消；10-未付款订单；20-已付款；30-已发货；40-已完成;50-已关闭")
    private Integer orderState;

    @ApiModelProperty("支付方式名称，参考OrderPaymentConst类")
    private String paymentName;

    @ApiModelProperty("支付方式code, 参考OrderPaymentConst类")
    private String paymentCode;

    @ApiModelProperty("订单总金额(用户需要支付的现金金额)")
    private BigDecimal orderAmount;

    @ApiModelProperty("余额账户支付总金额")
    private BigDecimal balanceAmount;

    @ApiModelProperty("三方支付金额")
    private BigDecimal payAmount;

    @ApiModelProperty("积分抵扣金额")
    private BigDecimal integralCashAmount;

    @ApiModelProperty("积分支付数量")
    private Integer integral;

    @ApiModelProperty("收货人")
    private String receiverName;

    @ApiModelProperty("省市区组合")
    private String receiverAreaInfo;

    @ApiModelProperty("收货人详细地址")
    private String receiverAddress;

    @ApiModelProperty("收货人手机号")
    private String receiverMobile;

    @ApiModelProperty("用户订单备注")
    private String orderRemark;

    @ApiModelProperty("订单来源1、pc；2、H5；3、Android；4、IOS; 5-微信小程序")
    private Integer orderFrom;

    @ApiModelProperty("发票信息 json格式")
    private String invoiceInfo;

    @ApiModelProperty("发货类型：0-物流发货，1-无需物流")
    private Integer deliverType;

    @ApiModelProperty("发货人电话,自行配送时填写")
    private String deliverMobile;

    @ApiModelProperty("发货人,自行配送时填写")
    private String deliverName;

    @ApiModelProperty("物流公司ID")
    private Integer expressId;

    @ApiModelProperty("物流公司")
    private String expressName;

    @ApiModelProperty("快递单号")
    private String expressNumber;

    @ApiModelProperty("发货地址ID")
    private Integer deliverAddressId;

    @ApiModelProperty("是否是电子面单")
    private Integer isDzmd;

    @ApiModelProperty("取消原因")
    private String refuseReason;

    @ApiModelProperty("取消备注")
    private String refuseRemark;

    @ApiModelProperty("是否结算：0-未结算；1-已结算")
    private Integer isSettlement;

    @ApiModelProperty("发货备注")
    private String deliverRemark;

    @ApiModelProperty("发货时间")
    private Date deliverTime;

    @ApiModelProperty("删除状态：0-未删除；1-放入回收站；2-彻底删除")
    private Integer deleteState;

    @ApiModelProperty("货品列表[数据库之外的额外属性]")
    private List<IntegralOrderProduct> orderProductList;
}