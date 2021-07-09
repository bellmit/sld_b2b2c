package com.slodon.b2b2c.dao.write.business;

import com.slodon.b2b2c.business.example.OrderExample;
import com.slodon.b2b2c.business.pojo.Order;
import com.slodon.b2b2c.core.database.BaseWriteMapper;

/**
 * 订单writeDao
 */
public interface OrderWriteMapper extends BaseWriteMapper<Order, OrderExample> {
}