package com.slodon.b2b2c.dao.read.integral;

import com.slodon.b2b2c.core.database.BaseReadMapper;
import com.slodon.b2b2c.integral.example.IntegralProductExample;
import com.slodon.b2b2c.integral.pojo.IntegralProduct;

/**
 * 积分货品表（SKU），指定特定规格readDao
 */
public interface IntegralProductReadMapper extends BaseReadMapper<IntegralProduct, IntegralProductExample> {
}