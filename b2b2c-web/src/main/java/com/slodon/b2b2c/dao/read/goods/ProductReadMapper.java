package com.slodon.b2b2c.dao.read.goods;

import com.slodon.b2b2c.core.database.BaseReadMapper;
import com.slodon.b2b2c.goods.example.ProductExample;
import com.slodon.b2b2c.goods.pojo.Product;

/**
 * 商品表（SKU），指定特定规格readDao
 */
public interface ProductReadMapper extends BaseReadMapper<Product, ProductExample> {
}