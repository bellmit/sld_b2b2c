package com.slodon.b2b2c.business.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单信息扩展表
 */
@Data
public class OrderExtend implements Serializable {
    private static final long serialVersionUID = -8353312635429901376L;
    @ApiModelProperty("扩展id")
    private Integer extendId;

    @ApiModelProperty("关联的订单编号")
    private String orderSn;

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("发货类型：0-物流发货，1-无需物流")
    private Integer deliverType;

    @ApiModelProperty("发货人电话,自行配送时填写")
    private String deliverMobile;

    @ApiModelProperty("发货人,自行配送时填写")
    private String deliverName;

    @ApiModelProperty("配送公司ID")
    private Integer shippingExpressId;

    @ApiModelProperty("发货时间")
    private Date deliverTime;

    @ApiModelProperty("评价时间")
    private Date evaluationTime;

    @ApiModelProperty("用户订单备注")
    private String orderRemark;

    @ApiModelProperty("订单赠送积分")
    private Integer orderPointsCount;

    @ApiModelProperty("优惠券面额")
    private BigDecimal voucherPrice;

    @ApiModelProperty("优惠券编码")
    private String voucherCode;

    @ApiModelProperty("订单来源1、pc；2、H5；3、Android；4、IOS; 5-微信小程序")
    private Integer orderFrom;

    @ApiModelProperty("发货地址ID")
    private Integer deliverAddressId;

    @ApiModelProperty("收货省份编码")
    private String receiverProvinceCode;

    @ApiModelProperty("收货城市编码")
    private String receiverCityCode;

    @ApiModelProperty("收货邮政编码")
    private String receiverPostCode;

    @ApiModelProperty("收货人姓名")
    private String receiverName;

    @ApiModelProperty("收货人详细地址信息")
    private String receiverInfo;

    @ApiModelProperty("发票信息 json格式")
    private String invoiceInfo;

    @ApiModelProperty("促销信息备注")
    private String promotionInfo;

    @ApiModelProperty("是否是电子面单")
    private Integer isDzmd;

    @ApiModelProperty("发票状态0-未开、1-已开")
    private Integer invoiceStatus;

    @ApiModelProperty("商家优惠券优惠金额")
    private BigDecimal storeVoucherAmount;

    @ApiModelProperty("平台优惠券优惠金额")
    private BigDecimal platformVoucherAmount;

    @ApiModelProperty("发货备注")
    private String deliverRemark;
}