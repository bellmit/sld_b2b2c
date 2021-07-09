package com.slodon.b2b2c.vo.business;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: slodon
 * @Description 封装售后申请信息VO对象
 * @Author wuxy
 */
@Data
public class AfsApplyInfoVO implements Serializable {
    private static final long serialVersionUID = 6146795730672764516L;
    @ApiModelProperty("剩余可退金额")
    private BigDecimal moneyCanReturn;

    @ApiModelProperty("剩余可退积分")
    private BigDecimal integralCanReturn;

    @ApiModelProperty("剩余可扣积分（购物赠送积分）")
    private BigDecimal integralCanDeduct;

    @ApiModelProperty("数量")
    private Integer number;

    @ApiModelProperty("订单货品列表")
    private OrderProductListVO orderProduct;
}
