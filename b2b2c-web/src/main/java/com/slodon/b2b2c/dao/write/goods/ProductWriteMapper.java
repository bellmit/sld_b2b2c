package com.slodon.b2b2c.dao.write.goods;

import com.slodon.b2b2c.core.database.BaseWriteMapper;
import com.slodon.b2b2c.goods.example.ProductExample;
import com.slodon.b2b2c.goods.pojo.Product;

/**
 * 商品表（SKU），指定特定规格writeDao
 */
public interface ProductWriteMapper extends BaseWriteMapper<Product, ProductExample> {
}