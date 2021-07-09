package com.slodon.b2b2c.business.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: slodon
 * @Description 销售总额每日统计DTO
 * @Author wuxy
 */
@Data
public class SaleTotalDayDTO implements Serializable {

    private static final long serialVersionUID = 7662922633884582810L;
    private String day;
    private BigDecimal amount;
}
