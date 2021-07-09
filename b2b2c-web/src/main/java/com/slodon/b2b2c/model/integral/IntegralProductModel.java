package com.slodon.b2b2c.model.integral;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.integral.IntegralProductReadMapper;
import com.slodon.b2b2c.dao.write.integral.IntegralProductWriteMapper;
import com.slodon.b2b2c.integral.example.IntegralProductExample;
import com.slodon.b2b2c.integral.pojo.IntegralProduct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 积分货品表（SKU），指定特定规格model
 */
@Component
@Slf4j
public class IntegralProductModel {
    @Resource
    private IntegralProductReadMapper integralProductReadMapper;

    @Resource
    private IntegralProductWriteMapper integralProductWriteMapper;

    /**
     * 新增积分货品表（SKU），指定特定规格
     *
     * @param integralProduct
     * @return
     */
    public Integer saveIntegralProduct(IntegralProduct integralProduct) {
        int count = integralProductWriteMapper.insert(integralProduct);
        if (count == 0) {
            throw new MallException("添加积分货品表（SKU），指定特定规格失败，请重试");
        }
        return count;
    }

    /**
     * 根据integralProductId删除积分货品表（SKU），指定特定规格
     *
     * @param integralProductId integralProductId
     * @return
     */
    public Integer deleteIntegralProduct(Long integralProductId) {
        if (StringUtils.isEmpty(integralProductId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = integralProductWriteMapper.deleteByPrimaryKey(integralProductId);
        if (count == 0) {
            log.error("根据integralProductId：" + integralProductId + "删除积分货品表（SKU），指定特定规格失败");
            throw new MallException("删除积分货品表（SKU），指定特定规格失败,请重试");
        }
        return count;
    }

    /**
     * 根据integralProductId更新积分货品表（SKU），指定特定规格
     *
     * @param integralProduct
     * @return
     */
    public Integer updateIntegralProduct(IntegralProduct integralProduct) {
        if (StringUtils.isEmpty(integralProduct.getIntegralProductId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = integralProductWriteMapper.updateByPrimaryKeySelective(integralProduct);
        if (count == 0) {
            log.error("根据integralProductId：" + integralProduct.getIntegralProductId() + "更新积分货品表（SKU），指定特定规格失败");
            throw new MallException("更新积分货品表（SKU），指定特定规格失败,请重试");
        }
        return count;
    }

    /**
     * 根据integralProductId获取积分货品表（SKU），指定特定规格详情
     *
     * @param integralProductId integralProductId
     * @return
     */
    public IntegralProduct getIntegralProductByIntegralProductId(Long integralProductId) {
        return integralProductReadMapper.getByPrimaryKey(integralProductId);
    }

    /**
     * 根据条件获取积分货品表（SKU），指定特定规格列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<IntegralProduct> getIntegralProductList(IntegralProductExample example, PagerInfo pager) {
        List<IntegralProduct> integralProductList;
        if (pager != null) {
            pager.setRowsCount(integralProductReadMapper.countByExample(example));
            integralProductList = integralProductReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            integralProductList = integralProductReadMapper.listByExample(example);
        }
        return integralProductList;
    }
}