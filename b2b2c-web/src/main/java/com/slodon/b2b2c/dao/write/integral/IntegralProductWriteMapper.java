package com.slodon.b2b2c.dao.write.integral;

import com.slodon.b2b2c.core.database.BaseWriteMapper;
import com.slodon.b2b2c.integral.example.IntegralProductExample;
import com.slodon.b2b2c.integral.pojo.IntegralProduct;

/**
 * 积分货品表（SKU），指定特定规格writeDao
 */
public interface IntegralProductWriteMapper extends BaseWriteMapper<IntegralProduct, IntegralProductExample> {
}