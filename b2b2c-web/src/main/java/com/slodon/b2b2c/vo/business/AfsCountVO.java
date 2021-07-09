package com.slodon.b2b2c.vo.business;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 售后费用计算vo
 */
@Data
public class AfsCountVO implements Serializable {
    private static final long serialVersionUID = 853201963097850155L;
    @ApiModelProperty("最大可退金额（可能包含运费）")
    private BigDecimal maxReturnMoney;

    @ApiModelProperty("最大可退积分")
    private Integer maxReturnIntegral;

    @ApiModelProperty("是否包含运费")
    private Boolean containsFee;

    @ApiModelProperty("运费金额")
    private BigDecimal returnExpressFee;

    @ApiModelProperty("申请件数")
    private Integer number;
}
