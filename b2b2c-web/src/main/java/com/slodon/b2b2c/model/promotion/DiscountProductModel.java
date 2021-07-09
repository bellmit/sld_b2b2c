package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.promotion.DiscountProductReadMapper;
import com.slodon.b2b2c.dao.write.promotion.DiscountProductWriteMapper;
import com.slodon.b2b2c.promotion.example.DiscountProductExample;
import com.slodon.b2b2c.promotion.pojo.DiscountProduct;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class DiscountProductModel {
    @Resource
    private DiscountProductReadMapper discountProductReadMapper;

    @Resource
    private DiscountProductWriteMapper discountProductWriteMapper;

    /**
     * 新增折扣活动货品关联表
     *
     * @param discountProduct
     * @return
     */
    public Integer saveDiscountProduct(DiscountProduct discountProduct) {
        int count = discountProductWriteMapper.insert(discountProduct);
        if (count == 0) {
            throw new MallException("添加折扣活动货品关联表失败，请重试");
        }
        return count;
    }

    /**
     * 根据discountProductId删除折扣活动货品关联表
     *
     * @param discountProductId discountProductId
     * @return
     */
    public Integer deleteDiscountProduct(Integer discountProductId) {
        if (StringUtils.isEmpty(discountProductId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = discountProductWriteMapper.deleteByPrimaryKey(discountProductId);
        if (count == 0) {
            log.error("根据discountProductId：" + discountProductId + "删除折扣活动货品关联表失败");
            throw new MallException("删除折扣活动货品关联表失败,请重试");
        }
        return count;
    }

    /**
     * 根据discountProductId更新折扣活动货品关联表
     *
     * @param discountProduct
     * @return
     */
    public Integer updateDiscountProduct(DiscountProduct discountProduct) {
        if (StringUtils.isEmpty(discountProduct.getDiscountProductId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = discountProductWriteMapper.updateByPrimaryKeySelective(discountProduct);
        if (count == 0) {
            log.error("根据discountProductId：" + discountProduct.getDiscountProductId() + "更新折扣活动货品关联表失败");
            throw new MallException("更新折扣活动货品关联表失败,请重试");
        }
        return count;
    }

    /**
     * 根据discountProductId获取折扣活动货品关联表详情
     *
     * @param discountProductId discountProductId
     * @return
     */
    public DiscountProduct getDiscountProductByDiscountProductId(Integer discountProductId) {
        return discountProductReadMapper.getByPrimaryKey(discountProductId);
    }

    /**
     * 根据条件获取折扣活动货品关联表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<DiscountProduct> getDiscountProductList(DiscountProductExample example, PagerInfo pager) {
        List<DiscountProduct> discountProductList;
        if (pager != null) {
            pager.setRowsCount(discountProductReadMapper.countByExample(example));
            discountProductList = discountProductReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            discountProductList = discountProductReadMapper.listByExample(example);
        }
        return discountProductList;
    }
}