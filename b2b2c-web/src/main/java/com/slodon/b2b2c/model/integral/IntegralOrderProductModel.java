package com.slodon.b2b2c.model.integral;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.integral.IntegralGoodsReadMapper;
import com.slodon.b2b2c.dao.read.integral.IntegralOrderProductReadMapper;
import com.slodon.b2b2c.dao.read.integral.IntegralProductReadMapper;
import com.slodon.b2b2c.dao.write.integral.IntegralGoodsWriteMapper;
import com.slodon.b2b2c.dao.write.integral.IntegralOrderProductWriteMapper;
import com.slodon.b2b2c.dao.write.integral.IntegralProductWriteMapper;
import com.slodon.b2b2c.integral.dto.OrderSubmitParamDTO;
import com.slodon.b2b2c.integral.example.IntegralOrderProductExample;
import com.slodon.b2b2c.integral.pojo.IntegralGoods;
import com.slodon.b2b2c.integral.pojo.IntegralOrderProduct;
import com.slodon.b2b2c.integral.pojo.IntegralProduct;
import com.slodon.b2b2c.member.pojo.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单货品明细表model
 */
@Component
@Slf4j
public class IntegralOrderProductModel {

    @Resource
    private IntegralOrderProductReadMapper integralOrderProductReadMapper;
    @Resource
    private IntegralOrderProductWriteMapper integralOrderProductWriteMapper;
    @Resource
    private IntegralProductReadMapper integralProductReadMapper;
    @Resource
    private IntegralProductWriteMapper integralProductWriteMapper;
    @Resource
    private IntegralGoodsReadMapper integralGoodsReadMapper;
    @Resource
    private IntegralGoodsWriteMapper integralGoodsWriteMapper;

    /**
     * 新增订单货品明细表
     *
     * @param integralOrderProduct
     * @return
     */
    public Integer saveIntegralOrderProduct(IntegralOrderProduct integralOrderProduct) {
        int count = integralOrderProductWriteMapper.insert(integralOrderProduct);
        if (count == 0) {
            throw new MallException("添加订单货品明细表失败，请重试");
        }
        return count;
    }

    /**
     * 根据orderProductId删除订单货品明细表
     *
     * @param orderProductId orderProductId
     * @return
     */
    public Integer deleteIntegralOrderProduct(Long orderProductId) {
        if (StringUtils.isEmpty(orderProductId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = integralOrderProductWriteMapper.deleteByPrimaryKey(orderProductId);
        if (count == 0) {
            log.error("根据orderProductId：" + orderProductId + "删除订单货品明细表失败");
            throw new MallException("删除订单货品明细表失败,请重试");
        }
        return count;
    }

    /**
     * 根据orderProductId更新订单货品明细表
     *
     * @param integralOrderProduct
     * @return
     */
    public Integer updateIntegralOrderProduct(IntegralOrderProduct integralOrderProduct) {
        if (StringUtils.isEmpty(integralOrderProduct.getOrderProductId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = integralOrderProductWriteMapper.updateByPrimaryKeySelective(integralOrderProduct);
        if (count == 0) {
            log.error("根据orderProductId：" + integralOrderProduct.getOrderProductId() + "更新订单货品明细表失败");
            throw new MallException("更新订单货品明细表失败,请重试");
        }
        return count;
    }

    /**
     * 根据orderProductId获取订单货品明细表详情
     *
     * @param orderProductId orderProductId
     * @return
     */
    public IntegralOrderProduct getIntegralOrderProductByOrderProductId(Long orderProductId) {
        return integralOrderProductReadMapper.getByPrimaryKey(orderProductId);
    }

    /**
     * 根据条件获取订单货品明细表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<IntegralOrderProduct> getIntegralOrderProductList(IntegralOrderProductExample example, PagerInfo pager) {
        List<IntegralOrderProduct> integralOrderProductList;
        if (pager != null) {
            pager.setRowsCount(integralOrderProductReadMapper.countByExample(example));
            integralOrderProductList = integralOrderProductReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            integralOrderProductList = integralOrderProductReadMapper.listByExample(example);
        }
        return integralOrderProductList;
    }

    /**
     * 提交订单-保存订单货品
     *
     * @param integralProduct    货品信息
     * @param member             会员信息
     * @param orderSn            订单号
     * @param dto
     * @param integralCashAmount 积分转换金额
     * @param payAmount          支付金额
     * @return 订单货品id
     */
    public Long insertOrderProduct(IntegralProduct integralProduct, Member member, String orderSn, OrderSubmitParamDTO dto,
                                   BigDecimal integralCashAmount, BigDecimal payAmount) {
        IntegralOrderProduct orderProduct = new IntegralOrderProduct();
        orderProduct.setOrderSn(orderSn);
        orderProduct.setStoreId(integralProduct.getStoreId());
        orderProduct.setStoreName(integralProduct.getStoreName());
        orderProduct.setMemberId(member.getMemberId());
        orderProduct.setGoodsId(integralProduct.getGoodsId());
        orderProduct.setGoodsName(integralProduct.getGoodsName());
        orderProduct.setProductImage(integralProduct.getMainImage());
        orderProduct.setSpecValues(integralProduct.getSpecValues());
        orderProduct.setProductId(integralProduct.getIntegralProductId());
        orderProduct.setIntegralPrice(integralProduct.getIntegralPrice());
        orderProduct.setCashPrice(integralProduct.getCashPrice());
        orderProduct.setProductNum(dto.getNumber());
        orderProduct.setIntegral(dto.getIntegral());
        orderProduct.setIntegralCashAmount(integralCashAmount);
        orderProduct.setCashAmount(payAmount);
        this.saveIntegralOrderProduct(orderProduct);
        return orderProduct.getOrderProductId();
    }

    /**
     * 订单支付完成，增加商品销量
     *
     * @param orderSn 订单号
     */
    public void orderPaySuccessAddSales(String orderSn) {
        IntegralOrderProductExample example = new IntegralOrderProductExample();
        example.setOrderSn(orderSn);
        List<IntegralOrderProduct> orderProductList = integralOrderProductReadMapper.listByExample(example);
        orderProductList.forEach(orderProduct -> {
            //更新sku销量
            IntegralProduct productDb = integralProductReadMapper.getByPrimaryKey(orderProduct.getProductId());
            IntegralProduct updateProduct = new IntegralProduct();
            updateProduct.setIntegralProductId(productDb.getIntegralProductId());
            updateProduct.setActualSales(productDb.getActualSales() + orderProduct.getProductNum());
            integralProductWriteMapper.updateByPrimaryKeySelective(updateProduct);

            //更新spu销量
            IntegralGoods goodsDb = integralGoodsReadMapper.getByPrimaryKey(orderProduct.getGoodsId());
            IntegralGoods updateGoods = new IntegralGoods();
            updateGoods.setIntegralGoodsId(goodsDb.getIntegralGoodsId());
            updateGoods.setActualSales(goodsDb.getActualSales() + orderProduct.getProductNum());
            updateGoods.setUpdateTime(new Date());
            integralGoodsWriteMapper.updateByPrimaryKeySelective(updateGoods);
        });
    }
}