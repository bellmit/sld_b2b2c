package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 优惠券详细日志表
 */
@Data
public class CouponUseLog implements Serializable {
    private static final long serialVersionUID = -1815873134306763086L;
    @ApiModelProperty("日志ID")
    private Integer logId;

    @ApiModelProperty("优惠券编码")
    private String couponCode;

    @ApiModelProperty("会员id")
    private Integer memberId;

    @ApiModelProperty("会员名称")
    private String memberName;

    @ApiModelProperty("使用订单号")
    private String orderSn;

    @ApiModelProperty("所属店铺")
    private Long storeId;

    @ApiModelProperty("优惠券操作类型：1-领取；2-下单消费；3-订单取消返回；4-商品退货返回；5-积分兑换")
    private Integer logType;

    @ApiModelProperty("时间记录")
    private Date logTime;

    @ApiModelProperty("详细内容描述")
    private String logContent;
}