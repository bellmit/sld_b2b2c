package com.slodon.b2b2c.starter.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 支付响应结果
 * @author lxk
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlodonPayResponse implements Serializable {

    private static final long serialVersionUID = 6846767723914587664L;

    public SlodonPayResponse(String actionType, Object payData) {
        this.actionType = actionType;
        this.payData = payData;
    }

    @ApiModelProperty("跳转类型")
    private String actionType;

    @ApiModelProperty("跳转地址")
    private Object payData;

    @ApiModelProperty("充值id")
    private Integer rechargeId;
}
