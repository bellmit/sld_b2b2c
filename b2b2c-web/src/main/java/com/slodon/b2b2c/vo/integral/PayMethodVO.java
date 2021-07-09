package com.slodon.b2b2c.vo.integral;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 支付方式vo
 */
@Data
@AllArgsConstructor
public class PayMethodVO implements Serializable {

    private static final long serialVersionUID = -3879002216251035064L;
    @ApiModelProperty("支付方式code")
    private String payMethod;
    @ApiModelProperty("支付方式名称")
    private String payMethodName;
    @ApiModelProperty("支付类型（每种支付方式，根据不同客户端，有不同的支付类型）")
    private String payType;
}
