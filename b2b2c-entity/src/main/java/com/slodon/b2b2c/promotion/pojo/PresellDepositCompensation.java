package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 预售定金赔偿表
 */
@Data
public class PresellDepositCompensation implements Serializable {
    private static final long serialVersionUID = 6871535906063982551L;

    @ApiModelProperty("赔偿id")
    private Integer compensationId;

    @ApiModelProperty("赔偿金额(定金倍数)")
    private BigDecimal compensationAmount;

    @ApiModelProperty("定金金额")
    private BigDecimal depositAmount;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("会员id")
    private Integer memberId;

    @ApiModelProperty("会员名称")
    private String memberName;

    @ApiModelProperty("创建时间")
    private Date createTime;
}