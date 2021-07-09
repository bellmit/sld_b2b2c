package com.slodon.b2b2c.dao.read.goods;

import com.slodon.b2b2c.core.database.BaseReadMapper;
import com.slodon.b2b2c.goods.example.GoodsBindAttributeValueExample;
import com.slodon.b2b2c.goods.pojo.GoodsBindAttributeValue;

/**
 * 商品对应属性表(保存商品时插入)readDao
 */
public interface GoodsBindAttributeValueReadMapper extends BaseReadMapper<GoodsBindAttributeValue, GoodsBindAttributeValueExample> {
}