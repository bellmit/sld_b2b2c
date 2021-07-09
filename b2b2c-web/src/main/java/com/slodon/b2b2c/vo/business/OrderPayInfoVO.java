package com.slodon.b2b2c.vo.business;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * 订单支付页信息vo
 */
@Data
public class OrderPayInfoVO {
    @ApiModelProperty("订单处理进度，1-排队处理中，2-提交订单失败，3-提交成功")
    private Integer dealState;
    @ApiModelProperty("订单金额")
    private BigDecimal orderAmount = new BigDecimal("0.00");
    @ApiModelProperty("已支付金额")
    private BigDecimal alreadyPay = new BigDecimal("0.00");
    @ApiModelProperty("还需支付金额")
    private BigDecimal needPay;
    @ApiModelProperty("可用余额")
    private BigDecimal balanceAvailable;
    @ApiModelProperty("支付单号")
    private String paySn;
    @ApiModelProperty("是否可以使用余额")
    private Boolean canUseBalance;
    @ApiModelProperty("收货地址")
    private String receiveAddress;
    @ApiModelProperty("收货人")
    private String receiverName;
    @ApiModelProperty("收货人电话")
    private String receiverMobile;
    @ApiModelProperty("商品名称列表")
    private Set<String> goodsNameList = new HashSet<>();

    /**
     * 计算还需支付金额
     * @return
     */
    public BigDecimal getNeedPay() {
        return this.orderAmount.subtract(this.alreadyPay);
    }
}
