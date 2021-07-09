package com.slodon.b2b2c.model.business;

import com.slodon.b2b2c.business.dto.OrderSubmitDTO;
import com.slodon.b2b2c.business.example.OrderPromotionSendProductExample;
import com.slodon.b2b2c.business.pojo.OrderPromotionSendProduct;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.business.OrderPromotionSendProductReadMapper;
import com.slodon.b2b2c.dao.read.goods.GoodsReadMapper;
import com.slodon.b2b2c.dao.read.goods.ProductReadMapper;
import com.slodon.b2b2c.dao.write.business.OrderPromotionSendProductWriteMapper;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 订单活动赠送货品表model
 */
@Component
@Slf4j
public class OrderPromotionSendProductModel {

    @Resource
    private OrderPromotionSendProductReadMapper orderPromotionSendProductReadMapper;
    @Resource
    private OrderPromotionSendProductWriteMapper orderPromotionSendProductWriteMapper;
    @Resource
    private ProductReadMapper productReadMapper;
    @Resource
    private GoodsReadMapper goodsReadMapper;

    /**
     * 新增订单活动赠送货品表
     *
     * @param orderPromotionSendProduct
     * @return
     */
    public Integer saveOrderPromotionSendProduct(OrderPromotionSendProduct orderPromotionSendProduct) {
        int count = orderPromotionSendProductWriteMapper.insert(orderPromotionSendProduct);
        if (count == 0) {
            throw new MallException("添加订单活动赠送货品表失败，请重试");
        }
        return count;
    }

    /**
     * 根据sendProductId删除订单活动赠送货品表
     *
     * @param sendProductId sendProductId
     * @return
     */
    public Integer deleteOrderPromotionSendProduct(Integer sendProductId) {
        if (StringUtils.isEmpty(sendProductId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = orderPromotionSendProductWriteMapper.deleteByPrimaryKey(sendProductId);
        if (count == 0) {
            log.error("根据sendProductId：" + sendProductId + "删除订单活动赠送货品表失败");
            throw new MallException("删除订单活动赠送货品表失败,请重试");
        }
        return count;
    }

    /**
     * 根据sendProductId更新订单活动赠送货品表
     *
     * @param orderPromotionSendProduct
     * @return
     */
    public Integer updateOrderPromotionSendProduct(OrderPromotionSendProduct orderPromotionSendProduct) {
        if (StringUtils.isEmpty(orderPromotionSendProduct.getSendProductId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = orderPromotionSendProductWriteMapper.updateByPrimaryKeySelective(orderPromotionSendProduct);
        if (count == 0) {
            log.error("根据sendProductId：" + orderPromotionSendProduct.getSendProductId() + "更新订单活动赠送货品表失败");
            throw new MallException("更新订单活动赠送货品表失败,请重试");
        }
        return count;
    }

    /**
     * 根据sendProductId获取订单活动赠送货品表详情
     *
     * @param sendProductId sendProductId
     * @return
     */
    public OrderPromotionSendProduct getOrderPromotionSendProductBySendProductId(Integer sendProductId) {
        return orderPromotionSendProductReadMapper.getByPrimaryKey(sendProductId);
    }

    /**
     * 根据条件获取订单活动赠送货品表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<OrderPromotionSendProduct> getOrderPromotionSendProductList(OrderPromotionSendProductExample example, PagerInfo pager) {
        List<OrderPromotionSendProduct> orderPromotionSendProductList;
        if (pager != null) {
            pager.setRowsCount(orderPromotionSendProductReadMapper.countByExample(example));
            orderPromotionSendProductList = orderPromotionSendProductReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            orderPromotionSendProductList = orderPromotionSendProductReadMapper.listByExample(example);
        }
        return orderPromotionSendProductList;
    }

    /**
     * 提交订单-保存订单活动赠送货品
     *
     * @param orderInfo
     * @param orderSn
     */
    public void insertOrderPromotionSendProducts(OrderSubmitDTO.OrderInfo orderInfo, String orderSn) {
        List<OrderSubmitDTO.PromotionInfo> promotionInfoList = orderInfo.getPromotionInfoList();
        if (!CollectionUtils.isEmpty(promotionInfoList)) {
            //参与了优惠活动
            promotionInfoList.forEach(promotionInfo -> {
                List<OrderSubmitDTO.PromotionInfo.SendProduct> sendProductList = promotionInfo.getSendProductList();
                if (!CollectionUtils.isEmpty(sendProductList)) {
                    //此活动赠送了货品
                    sendProductList.forEach(sendProduct -> {
                        Product productDb = productReadMapper.getByPrimaryKey(sendProduct.getProductId());
                        Goods goodsDb = goodsReadMapper.getByPrimaryKey(productDb.getGoodsId());
                        if (goodsDb.getState().equals(GoodsConst.GOODS_STATE_UPPER) && productDb.getState().equals(GoodsConst.PRODUCT_STATE_1)) {
                            OrderPromotionSendProduct orderPromotionSendProduct = new OrderPromotionSendProduct();
                            orderPromotionSendProduct.setOrderSn(orderSn);
                            orderPromotionSendProduct.setPromotionGrade(promotionInfo.getPromotionType() / 100);
                            orderPromotionSendProduct.setPromotionType(promotionInfo.getPromotionType());
                            orderPromotionSendProduct.setPromotionId(promotionInfo.getPromotionId());
                            orderPromotionSendProduct.setIsStore(promotionInfo.getIsStore() ? OrderConst.IS_STORE_PROMOTION_YES : OrderConst.IS_STORE_PROMOTION_NO);
                            orderPromotionSendProduct.setProductId(sendProduct.getProductId());
                            orderPromotionSendProduct.setNumber(sendProduct.getNum());
                            this.saveOrderPromotionSendProduct(orderPromotionSendProduct);
                        }
                    });
                }
            });
        }
    }
}