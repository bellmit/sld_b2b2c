package com.slodon.b2b2c.starter.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 退款响应结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlodonRefundResponse implements Serializable {

    private static final long serialVersionUID = 3015083325270016061L;

    @ApiModelProperty("是否成功")
    private Boolean success;

    @ApiModelProperty("失败返回信息")
    private String msg;
}
