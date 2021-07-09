package com.slodon.b2b2c.model.goods;

import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.dao.read.goods.ProductReadMapper;
import com.slodon.b2b2c.dao.write.goods.ProductWriteMapper;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.goods.example.ProductExample;
import com.slodon.b2b2c.goods.pojo.Product;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ProductModel {

    @Resource
    private ProductReadMapper productReadMapper;
    @Resource
    private ProductWriteMapper productWriteMapper;

    /**
     * 新增商品表（SKU），指定特定规格
     *
     * @param product
     * @return
     */
    public Integer saveProduct(Product product) {
        int count = productWriteMapper.insert(product);
        if (count == 0) {
            throw new MallException("添加商品表（SKU），指定特定规格失败，请重试");
        }
        return count;
    }

    /**
     * 查询符合条件的记录数
     *
     * @param example
     * @return
     */
    public Integer countByExample(ProductExample example) {
        return productReadMapper.countByExample(example);
    }

    /**
     * 根据productId删除商品表（SKU），指定特定规格
     *
     * @param productId productId
     * @return
     */
    public Integer deleteProduct(Long productId) {
        if (StringUtils.isEmpty(productId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = productWriteMapper.deleteByPrimaryKey(productId);
        if (count == 0) {
            log.error("根据productId：" + productId + "删除商品表（SKU），指定特定规格失败");
            throw new MallException("删除商品表（SKU），指定特定规格失败,请重试");
        }
        return count;
    }

    /**
     * 根据productId更新商品表（SKU），指定特定规格
     *
     * @param product
     * @return
     */
    public Integer updateProduct(Product product) {
        if (StringUtils.isEmpty(product.getProductId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = productWriteMapper.updateByPrimaryKeySelective(product);
        if (count == 0) {
            log.error("根据productId：" + product.getProductId() + "更新商品表（SKU），指定特定规格失败");
            throw new MallException("更新商品表（SKU），指定特定规格失败,请重试");
        }
        return count;
    }

    /**
     * 根据条件更新商品表（SKU）
     *
     * @param product
     * @param example
     * @return
     */
    public Integer updateProductByExample(Product product, ProductExample example) {
        return productWriteMapper.updateByExampleSelective(product, example);
    }

    /**
     * 根据productId获取商品表（SKU），指定特定规格详情
     *
     * @param productId productId
     * @return
     */
    public Product getProductByProductId(Long productId) {
        return productReadMapper.getByPrimaryKey(productId);
    }

    /**
     * 根据条件获取商品表（SKU），指定特定规格列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<Product> getProductList(ProductExample example, PagerInfo pager) {
        List<Product> productList;
        if (pager != null) {
            pager.setRowsCount(productReadMapper.countByExample(example));
            productList = productReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            productList = productReadMapper.listByExample(example);
        }
        return productList;
    }

    /**
     * 系统定时检查库存
     *
     * @return
     */
    @Transactional
    public List<Product> jobSystemCheckProductStock() {
        List<Product> productList = new ArrayList<>();
        //获取所有还未通知过的货品
        ProductExample productExample = new ProductExample();
        productExample.setState(GoodsConst.PRODUCT_STATE_1);
        productExample.setProductStockWarningState(GoodsConst.PRODUCT_STOCK_WARNING_STATE_1);
        List<Product> products = productReadMapper.listByExample(productExample);
        if (!CollectionUtils.isEmpty(products)) {
            for (Product product : products) {
                if (null == product.getProductStock() || null == product.getProductStockWarning()) {
                    continue;
                }
                if (product.getProductStock() <= product.getProductStockWarning()) {
                    //更新通知状态
                    Product productUpdate = new Product();
                    productUpdate.setProductId(product.getProductId());
                    productUpdate.setProductStockWarningState(GoodsConst.PRODUCT_STOCK_WARNING_STATE_2);
                    int result = productWriteMapper.updateByPrimaryKeySelective(productUpdate);
                    AssertUtil.isTrue(result == 0, "更新货品信息失败，请确认");

                    productList.add(product);
                }
            }
        }
        return productList;
    }
}