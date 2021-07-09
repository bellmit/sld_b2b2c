package com.slodon.b2b2c.dao.read.goods;

import com.slodon.b2b2c.core.database.BaseReadMapper;
import com.slodon.b2b2c.goods.example.GoodsServiceLabelExample;
import com.slodon.b2b2c.goods.pojo.GoodsServiceLabel;

/**
 * 商品服务标签（比如：7天无理由退货、急速发货）readDao
 */
public interface GoodsServiceLabelReadMapper extends BaseReadMapper<GoodsServiceLabel, GoodsServiceLabelExample> {
}