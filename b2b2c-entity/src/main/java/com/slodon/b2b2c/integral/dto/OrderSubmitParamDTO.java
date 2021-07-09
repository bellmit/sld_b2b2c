package com.slodon.b2b2c.integral.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单提交前后端参数传递类
 */
@Data
public class OrderSubmitParamDTO implements Serializable {

    private static final long serialVersionUID = 501911750367500874L;
    @ApiModelProperty(value ="订单来源1、pc；2、H5；3、Android；4、IOS; 5-微信小程序", required = true)
    private Integer orderFrom;
    @ApiModelProperty(value = "货品id", required = true)
    private Long productId;
    @ApiModelProperty(value = "购买数量", required = true)
    private Integer number;
    @ApiModelProperty(value = "使用积分数量", required = true)
    private Integer integral;
    @ApiModelProperty(value = "收货地址id", required = true)
    private Integer addressId;
    @ApiModelProperty(value = "发票id")
    private Integer invoiceId;
    @ApiModelProperty(value = "订单备注")
    private String remark;

}
