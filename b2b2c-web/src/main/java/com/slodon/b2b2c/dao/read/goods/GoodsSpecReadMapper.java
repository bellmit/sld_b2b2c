package com.slodon.b2b2c.dao.read.goods;

import com.slodon.b2b2c.core.database.BaseReadMapper;
import com.slodon.b2b2c.goods.example.GoodsSpecExample;
import com.slodon.b2b2c.goods.pojo.GoodsSpec;

/**
 * 规格表（系统维护，平台可以看到店铺创建的规格，但不做修改）readDao
 */
public interface GoodsSpecReadMapper extends BaseReadMapper<GoodsSpec, GoodsSpecExample> {
}