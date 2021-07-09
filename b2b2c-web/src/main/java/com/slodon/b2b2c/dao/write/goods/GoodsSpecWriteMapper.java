package com.slodon.b2b2c.dao.write.goods;

import com.slodon.b2b2c.core.database.BaseWriteMapper;
import com.slodon.b2b2c.goods.example.GoodsSpecExample;
import com.slodon.b2b2c.goods.pojo.GoodsSpec;

/**
 * 规格表（系统维护，平台可以看到店铺创建的规格，但不做修改）writeDao
 */
public interface GoodsSpecWriteMapper extends BaseWriteMapper<GoodsSpec, GoodsSpecExample> {
}