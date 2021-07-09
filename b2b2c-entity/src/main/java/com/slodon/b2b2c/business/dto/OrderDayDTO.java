package com.slodon.b2b2c.business.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: slodon
 * @Description 订单量统计DTO
 * @Author wuxy
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class OrderDayDTO implements Serializable {

    private String orderDay;
    private BigDecimal goodsAmount;
    private BigDecimal expressFee;
    private BigDecimal orderAmount;
    private BigDecimal balanceAmount;
    private BigDecimal payAmount;
    private BigDecimal refundAmount;
    private Integer count;

    private String storeName;
}
