package com.slodon.b2b2c.business.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单换货表
 */
@Data
public class OrderReplacement implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("换货id")
    private Integer replacementId;

    @ApiModelProperty("售后服务单号")
    private String afsSn;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("换货数量")
    private Integer replacementNum;

    @ApiModelProperty("新货品的ID（可能换同商品的不同sku）")
    private Long newProductId;

    @ApiModelProperty("收货详细地址")
    private String buyerReceiveAddress;

    @ApiModelProperty("用户收货人")
    private String buyerReceiveName;

    @ApiModelProperty("用户收货人电话")
    private String buyerReceivePhone;

    @ApiModelProperty("店铺发货物流公司")
    private String storeExpressName;

    @ApiModelProperty("店铺发货物流公司快递代码")
    private String storeExpressCode;

    @ApiModelProperty("店铺发货快递单号")
    private String storeDeliveryNumber;

    @ApiModelProperty("商户发货时间")
    private Date storeDeliveryTime;

    @ApiModelProperty("换货完成时间（买家收货时间）")
    private Date completeTime;

    @ApiModelProperty("换货状态：100-买家申请；101-买家发货；200-卖家审核失败；201-卖家审核通过；202-卖家确认收货；203-卖家拒收；204-卖家发货；301-买家收货（已完成）")
    private Integer state;
}