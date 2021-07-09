package com.slodon.b2b2c.model.business;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.slodon.b2b2c.business.dto.OrderSubmitDTO;
import com.slodon.b2b2c.business.example.OrderProductExample;
import com.slodon.b2b2c.business.pojo.OrderProduct;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.dao.read.business.OrderProductReadMapper;
import com.slodon.b2b2c.dao.read.goods.GoodsCategoryReadMapper;
import com.slodon.b2b2c.dao.read.goods.GoodsReadMapper;
import com.slodon.b2b2c.dao.read.goods.ProductReadMapper;
import com.slodon.b2b2c.dao.read.seller.StoreBindCategoryReadMapper;
import com.slodon.b2b2c.dao.write.business.OrderProductWriteMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsWriteMapper;
import com.slodon.b2b2c.dao.write.goods.ProductWriteMapper;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.GoodsCategory;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.seller.example.StoreBindCategoryExample;
import com.slodon.b2b2c.seller.pojo.StoreBindCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class OrderProductModel {

    @Resource
    private OrderProductReadMapper orderProductReadMapper;
    @Resource
    private OrderProductWriteMapper orderProductWriteMapper;
    @Resource
    private StoreBindCategoryReadMapper storeBindCategoryReadMapper;
    @Resource
    private GoodsCategoryReadMapper goodsCategoryReadMapper;
    @Resource
    private ProductReadMapper productReadMapper;
    @Resource
    private ProductWriteMapper productWriteMapper;
    @Resource
    private GoodsReadMapper goodsReadMapper;
    @Resource
    private GoodsWriteMapper goodsWriteMapper;

    /**
     * 新增订单货品明细表
     *
     * @param orderProduct
     * @return
     */
    public Integer saveOrderProduct(OrderProduct orderProduct) {
        int count = orderProductWriteMapper.insert(orderProduct);
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
    public Integer deleteOrderProduct(Long orderProductId) {
        if (StringUtils.isEmpty(orderProductId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = orderProductWriteMapper.deleteByPrimaryKey(orderProductId);
        if (count == 0) {
            log.error("根据orderProductId：" + orderProductId + "删除订单货品明细表失败");
            throw new MallException("删除订单货品明细表失败,请重试");
        }
        return count;
    }

    /**
     * 根据orderProductId更新订单货品明细表
     *
     * @param orderProduct
     * @return
     */
    public Integer updateOrderProduct(OrderProduct orderProduct) {
        if (StringUtils.isEmpty(orderProduct.getOrderProductId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = orderProductWriteMapper.updateByPrimaryKeySelective(orderProduct);
        if (count == 0) {
            log.error("根据orderProductId：" + orderProduct.getOrderProductId() + "更新订单货品明细表失败");
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
    public OrderProduct getOrderProductByOrderProductId(Long orderProductId) {
        return orderProductReadMapper.getByPrimaryKey(orderProductId);
    }

    /**
     * 根据条件获取订单货品明细表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<OrderProduct> getOrderProductList(OrderProductExample example, PagerInfo pager) {
        List<OrderProduct> orderProductList;
        if (pager != null) {
            pager.setRowsCount(orderProductReadMapper.countByExample(example));
            orderProductList = orderProductReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            orderProductList = orderProductReadMapper.listByExample(example);
        }
        return orderProductList;
    }

    /**
     * 提交订单-保存订单货品
     *
     * @param orderSn 订单号
     * @param info    计算优惠后的订单货品信息
     * @param goodsDb 商品信息
     * @return 订单货品id
     */
    public Long insertOrderProduct(String orderSn, OrderSubmitDTO.OrderInfo.OrderProductInfo info, Goods goodsDb, Integer spellTeamId) {
        //查询商户分佣比例
        BigDecimal scaling;
        if (goodsDb.getIsSelf().equals(GoodsConst.IS_SELF_YES)) {
            //自营店铺，查询商品分类信息
            GoodsCategory goodsCategory = goodsCategoryReadMapper.getByPrimaryKey(goodsDb.getCategoryId3());
            scaling = goodsCategory.getScaling();
        } else {
            //入驻店铺，查询分类申请
            StoreBindCategoryExample storeBindCategoryExample = new StoreBindCategoryExample();
            storeBindCategoryExample.setStoreId(goodsDb.getStoreId());
            storeBindCategoryExample.setGoodsCategoryId3(goodsDb.getCategoryId3());
            storeBindCategoryExample.setState(StoreConst.STORE_CATEGORY_STATE_PASS);
            List<StoreBindCategory> storeBindCategoryList = storeBindCategoryReadMapper.listByExample(storeBindCategoryExample);
            AssertUtil.notEmpty(storeBindCategoryList, "店铺申请商品分类信息为空");
            scaling = storeBindCategoryList.get(0).getScaling();
        }
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrderSn(orderSn);
        orderProduct.setStoreId(info.getStoreId());
        orderProduct.setStoreName(info.getStoreName());
        orderProduct.setMemberId(info.getMemberId());
        orderProduct.setGoodsCategoryId(goodsDb.getCategoryId3());
        orderProduct.setGoodsId(info.getGoodsId());
        orderProduct.setGoodsName(info.getGoodsName());
        orderProduct.setProductImage(info.getProductImage());
        orderProduct.setSpecValues(info.getSpecValues());
        orderProduct.setProductId(info.getProductId());
        orderProduct.setProductShowPrice(info.getProductPrice());
        orderProduct.setProductNum(info.getBuyNum());
        orderProduct.setActivityDiscountAmount(info.getTotalDiscount());
        if (!CollectionUtils.isEmpty(info.getPromotionInfoList())) {
            //活动详情
            orderProduct.setActivityDiscountDetail(JSON.toJSONString(info.getPromotionInfoList(), SerializerFeature.SortField, SerializerFeature.MapSortField));
        }
        orderProduct.setStoreActivityAmount(info.getStoreActivityAmount());
        orderProduct.setPlatformActivityAmount(info.getPlatformActivityAmount());
        orderProduct.setStoreVoucherAmount(info.getStoreVoucherAmount());
        orderProduct.setPlatformVoucherAmount(info.getPlatformVoucherAmount());
        orderProduct.setIntegral(info.getIntegral());
        orderProduct.setIntegralCashAmount(info.getIntegralCashAmount());
        orderProduct.setMoneyAmount(info.getMoneyAmount());
        orderProduct.setCommissionRate(scaling);
        orderProduct.setCommissionAmount(orderProduct.getMoneyAmount().multiply(scaling).setScale(2, RoundingMode.HALF_UP));
        if (!StringUtil.isNullOrZero(spellTeamId)) {
            orderProduct.setSpellTeamId(spellTeamId);
        }
        this.saveOrderProduct(orderProduct);

        return orderProduct.getOrderProductId();
    }

    /**
     * 提交订单-保存订单货品
     *
     * @param orderSn 订单号
     * @param info    计算优惠后的订单货品信息
     * @return 订单货品id
     */
    public Long updateBalanceOrderProduct(String orderSn, OrderSubmitDTO.OrderInfo.OrderProductInfo info) {
        //查询订单货品信息
        OrderProductExample example = new OrderProductExample();
        example.setOrderSn(orderSn);
        example.setProductId(info.getProductId());
        List<OrderProduct> orderProductList = this.getOrderProductList(example, null);
        AssertUtil.notEmpty(orderProductList, "订单货品不存在");
        OrderProduct orderProductDb = orderProductList.get(0);

        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrderProductId(orderProductDb.getOrderProductId());
        orderProduct.setActivityDiscountAmount(info.getTotalDiscount());
        if (!CollectionUtils.isEmpty(info.getPromotionInfoList())) {
            //活动详情
            orderProduct.setActivityDiscountDetail(JSON.toJSONString(info.getPromotionInfoList(), SerializerFeature.SortField, SerializerFeature.MapSortField));
            //没有活动优惠就不做改动
            orderProduct.setMoneyAmount(info.getMoneyAmount());
        }
        orderProduct.setStoreActivityAmount(info.getStoreActivityAmount());
        orderProduct.setPlatformActivityAmount(info.getPlatformActivityAmount());
        orderProduct.setStoreVoucherAmount(info.getStoreVoucherAmount());
        orderProduct.setPlatformVoucherAmount(info.getPlatformVoucherAmount());
        orderProduct.setIntegral(info.getIntegral());
        orderProduct.setIntegralCashAmount(info.getIntegralCashAmount());
        this.updateOrderProduct(orderProduct);

        return orderProductDb.getOrderProductId();
    }

    /**
     * 提交订单-保存订单活动赠品
     *
     * @param orderSn   订单号
     * @param orderInfo 订单信息
     * @param memberId  会员id
     */
    public void insertSendOrderProduct(String orderSn, OrderSubmitDTO.OrderInfo orderInfo, Integer memberId) {
        List<OrderSubmitDTO.PromotionInfo> promotionInfoList = orderInfo.getPromotionInfoList();
        if (CollectionUtils.isEmpty(promotionInfoList)) {
            //未参与优惠活动
            return;
        }
        //按照货品id分组，各活动如果有相同的赠品，数量相加，只记录一条订单货品
        Map<Long/*productId*/, OrderSubmitDTO.PromotionInfo.SendProduct> mapByProductId = new HashMap<>();
        promotionInfoList.forEach(promotionInfo -> {
            List<OrderSubmitDTO.PromotionInfo.SendProduct> sendProductList = promotionInfo.getSendProductList();
            if (!CollectionUtils.isEmpty(sendProductList)) {
                sendProductList.forEach(sendProduct -> {
                    Long key = sendProduct.getProductId();
                    if (mapByProductId.containsKey(key)) {
                        //有相同的赠品，数量相加
                        mapByProductId.get(key).setNum(mapByProductId.get(key).getNum() + sendProduct.getNum());
                    } else {
                        //put
                        mapByProductId.put(key, sendProduct);
                    }
                });
            }
        });

        if (CollectionUtils.isEmpty(mapByProductId)) {
            //无赠品
            return;
        }
        mapByProductId.forEach((productId, sendProduct) -> {
            Product productDb = productReadMapper.getByPrimaryKey(productId);
            Goods goodsDb = goodsReadMapper.getByPrimaryKey(productDb.getGoodsId());
            if (goodsDb.getState().equals(GoodsConst.GOODS_STATE_UPPER) && productDb.getState().equals(GoodsConst.PRODUCT_STATE_1)) {
                //商品有效才能赠送
                //保存订单赠品
                OrderProduct orderProduct = new OrderProduct();
                orderProduct.setOrderSn(orderSn);
                orderProduct.setStoreId(orderInfo.getStoreId());
                orderProduct.setStoreName(orderInfo.getStoreName());
                orderProduct.setGoodsCategoryId(productDb.getCategoryId3());
                orderProduct.setMemberId(memberId);
                orderProduct.setGoodsId(productDb.getGoodsId());
                orderProduct.setGoodsName(productDb.getGoodsName());
                orderProduct.setProductImage(productDb.getMainImage());
                orderProduct.setSpecValues(productDb.getSpecValues());
                orderProduct.setProductId(productDb.getProductId());
                orderProduct.setProductShowPrice(new BigDecimal("0.00"));//赠品价格为0
                orderProduct.setProductNum(Integer.min(sendProduct.getNum(), productDb.getProductStock()));//赠品数量不能大于货品库存
                orderProduct.setIsGift(OrderConst.IS_GIFT_YES);//是赠品
                this.saveOrderProduct(orderProduct);

                //减赠品库存
                Product updateProduct = new Product();
                updateProduct.setProductId(productDb.getProductId());
                updateProduct.setProductStock(Integer.max(productDb.getProductStock() - sendProduct.getNum(), 0));//库存最低为0
                productWriteMapper.updateByPrimaryKeySelective(updateProduct);

                if (productDb.getIsDefault().equals(GoodsConst.PRODUCT_IS_DEFAULT_YES)
                        && (productDb.getState().equals(GoodsConst.PRODUCT_STATE_1) || productDb.getState().equals(GoodsConst.PRODUCT_STATE_3))) {
                    //默认货品，同步修改商品库存
                    Goods updateGoods = new Goods();
                    updateGoods.setGoodsId(productDb.getGoodsId());
                    updateGoods.setGoodsStock(updateProduct.getProductStock());
                    updateGoods.setUpdateTime(new Date());
                    goodsWriteMapper.updateByPrimaryKeySelective(updateGoods);
                }
            }
        });
    }

    /**
     * 订单支付完成，增加商品销量
     *
     * @param orderSn 订单号
     */
    public void orderPaySuccessAddSales(String orderSn) {
        OrderProductExample example = new OrderProductExample();
        example.setOrderSn(orderSn);
        List<OrderProduct> orderProductList = orderProductReadMapper.listByExample(example);
        orderProductList.forEach(orderProduct -> {
            //更新sku销量
            Product productDb = productReadMapper.getByPrimaryKey(orderProduct.getProductId());
            Product updateProduct = new Product();
            updateProduct.setProductId(productDb.getProductId());
            updateProduct.setActualSales(productDb.getActualSales() + orderProduct.getProductNum());
            productWriteMapper.updateByPrimaryKeySelective(updateProduct);

            //更新spu销量
            Goods goodsDb = goodsReadMapper.getByPrimaryKey(orderProduct.getGoodsId());
            Goods updateGoods = new Goods();
            updateGoods.setGoodsId(goodsDb.getGoodsId());
            updateGoods.setActualSales(goodsDb.getActualSales() + orderProduct.getProductNum());
            updateGoods.setUpdateTime(new Date());
            goodsWriteMapper.updateByPrimaryKeySelective(updateGoods);
        });
    }
}