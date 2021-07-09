package com.slodon.b2b2c.dao.write.goods;

import com.slodon.b2b2c.core.database.BaseWriteMapper;
import com.slodon.b2b2c.goods.example.GoodsBindAttributeValueExample;
import com.slodon.b2b2c.goods.pojo.GoodsBindAttributeValue;

/**
 * 商品对应属性表(保存商品时插入)writeDao
 */
public interface GoodsBindAttributeValueWriteMapper extends BaseWriteMapper<GoodsBindAttributeValue, GoodsBindAttributeValueExample> {
}