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
     * ???????????????????????????
     *
     * @param orderProduct
     * @return
     */
    public Integer saveOrderProduct(OrderProduct orderProduct) {
        int count = orderProductWriteMapper.insert(orderProduct);
        if (count == 0) {
            throw new MallException("?????????????????????????????????????????????");
        }
        return count;
    }

    /**
     * ??????orderProductId???????????????????????????
     *
     * @param orderProductId orderProductId
     * @return
     */
    public Integer deleteOrderProduct(Long orderProductId) {
        if (StringUtils.isEmpty(orderProductId)) {
            throw new MallException("???????????????????????????");
        }
        int count = orderProductWriteMapper.deleteByPrimaryKey(orderProductId);
        if (count == 0) {
            log.error("??????orderProductId???" + orderProductId + "?????????????????????????????????");
            throw new MallException("?????????????????????????????????,?????????");
        }
        return count;
    }

    /**
     * ??????orderProductId???????????????????????????
     *
     * @param orderProduct
     * @return
     */
    public Integer updateOrderProduct(OrderProduct orderProduct) {
        if (StringUtils.isEmpty(orderProduct.getOrderProductId())) {
            throw new MallException("???????????????????????????");
        }
        int count = orderProductWriteMapper.updateByPrimaryKeySelective(orderProduct);
        if (count == 0) {
            log.error("??????orderProductId???" + orderProduct.getOrderProductId() + "?????????????????????????????????");
            throw new MallException("?????????????????????????????????,?????????");
        }
        return count;
    }

    /**
     * ??????orderProductId?????????????????????????????????
     *
     * @param orderProductId orderProductId
     * @return
     */
    public OrderProduct getOrderProductByOrderProductId(Long orderProductId) {
        return orderProductReadMapper.getByPrimaryKey(orderProductId);
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param example ??????????????????
     * @param pager   ????????????
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
     * ????????????-??????????????????
     *
     * @param orderSn ?????????
     * @param info    ????????????????????????????????????
     * @param goodsDb ????????????
     * @return ????????????id
     */
    public Long insertOrderProduct(String orderSn, OrderSubmitDTO.OrderInfo.OrderProductInfo info, Goods goodsDb, Integer spellTeamId) {
        //????????????????????????
        BigDecimal scaling;
        if (goodsDb.getIsSelf().equals(GoodsConst.IS_SELF_YES)) {
            //???????????????????????????????????????
            GoodsCategory goodsCategory = goodsCategoryReadMapper.getByPrimaryKey(goodsDb.getCategoryId3());
            scaling = goodsCategory.getScaling();
        } else {
            //?????????????????????????????????
            StoreBindCategoryExample storeBindCategoryExample = new StoreBindCategoryExample();
            storeBindCategoryExample.setStoreId(goodsDb.getStoreId());
            storeBindCategoryExample.setGoodsCategoryId3(goodsDb.getCategoryId3());
            storeBindCategoryExample.setState(StoreConst.STORE_CATEGORY_STATE_PASS);
            List<StoreBindCategory> storeBindCategoryList = storeBindCategoryReadMapper.listByExample(storeBindCategoryExample);
            AssertUtil.notEmpty(storeBindCategoryList, "????????????????????????????????????");
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
            //????????????
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
     * ????????????-??????????????????
     *
     * @param orderSn ?????????
     * @param info    ????????????????????????????????????
     * @return ????????????id
     */
    public Long updateBalanceOrderProduct(String orderSn, OrderSubmitDTO.OrderInfo.OrderProductInfo info) {
        //????????????????????????
        OrderProductExample example = new OrderProductExample();
        example.setOrderSn(orderSn);
        example.setProductId(info.getProductId());
        List<OrderProduct> orderProductList = this.getOrderProductList(example, null);
        AssertUtil.notEmpty(orderProductList, "?????????????????????");
        OrderProduct orderProductDb = orderProductList.get(0);

        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrderProductId(orderProductDb.getOrderProductId());
        orderProduct.setActivityDiscountAmount(info.getTotalDiscount());
        if (!CollectionUtils.isEmpty(info.getPromotionInfoList())) {
            //????????????
            orderProduct.setActivityDiscountDetail(JSON.toJSONString(info.getPromotionInfoList(), SerializerFeature.SortField, SerializerFeature.MapSortField));
            //?????????????????????????????????
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
     * ????????????-????????????????????????
     *
     * @param orderSn   ?????????
     * @param orderInfo ????????????
     * @param memberId  ??????id
     */
    public void insertSendOrderProduct(String orderSn, OrderSubmitDTO.OrderInfo orderInfo, Integer memberId) {
        List<OrderSubmitDTO.PromotionInfo> promotionInfoList = orderInfo.getPromotionInfoList();
        if (CollectionUtils.isEmpty(promotionInfoList)) {
            //?????????????????????
            return;
        }
        //????????????id???????????????????????????????????????????????????????????????????????????????????????
        Map<Long/*productId*/, OrderSubmitDTO.PromotionInfo.SendProduct> mapByProductId = new HashMap<>();
        promotionInfoList.forEach(promotionInfo -> {
            List<OrderSubmitDTO.PromotionInfo.SendProduct> sendProductList = promotionInfo.getSendProductList();
            if (!CollectionUtils.isEmpty(sendProductList)) {
                sendProductList.forEach(sendProduct -> {
                    Long key = sendProduct.getProductId();
                    if (mapByProductId.containsKey(key)) {
                        //?????????????????????????????????
                        mapByProductId.get(key).setNum(mapByProductId.get(key).getNum() + sendProduct.getNum());
                    } else {
                        //put
                        mapByProductId.put(key, sendProduct);
                    }
                });
            }
        });

        if (CollectionUtils.isEmpty(mapByProductId)) {
            //?????????
            return;
        }
        mapByProductId.forEach((productId, sendProduct) -> {
            Product productDb = productReadMapper.getByPrimaryKey(productId);
            Goods goodsDb = goodsReadMapper.getByPrimaryKey(productDb.getGoodsId());
            if (goodsDb.getState().equals(GoodsConst.GOODS_STATE_UPPER) && productDb.getState().equals(GoodsConst.PRODUCT_STATE_1)) {
                //????????????????????????
                //??????????????????
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
                orderProduct.setProductShowPrice(new BigDecimal("0.00"));//???????????????0
                orderProduct.setProductNum(Integer.min(sendProduct.getNum(), productDb.getProductStock()));//????????????????????????????????????
                orderProduct.setIsGift(OrderConst.IS_GIFT_YES);//?????????
                this.saveOrderProduct(orderProduct);

                //???????????????
                Product updateProduct = new Product();
                updateProduct.setProductId(productDb.getProductId());
                updateProduct.setProductStock(Integer.max(productDb.getProductStock() - sendProduct.getNum(), 0));//???????????????0
                productWriteMapper.updateByPrimaryKeySelective(updateProduct);

                if (productDb.getIsDefault().equals(GoodsConst.PRODUCT_IS_DEFAULT_YES)
                        && (productDb.getState().equals(GoodsConst.PRODUCT_STATE_1) || productDb.getState().equals(GoodsConst.PRODUCT_STATE_3))) {
                    //???????????????????????????????????????
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
     * ???????????????????????????????????????
     *
     * @param orderSn ?????????
     */
    public void orderPaySuccessAddSales(String orderSn) {
        OrderProductExample example = new OrderProductExample();
        example.setOrderSn(orderSn);
        List<OrderProduct> orderProductList = orderProductReadMapper.listByExample(example);
        orderProductList.forEach(orderProduct -> {
            //??????sku??????
            Product productDb = productReadMapper.getByPrimaryKey(orderProduct.getProductId());
            Product updateProduct = new Product();
            updateProduct.setProductId(productDb.getProductId());
            updateProduct.setActualSales(productDb.getActualSales() + orderProduct.getProductNum());
            productWriteMapper.updateByPrimaryKeySelective(updateProduct);

            //??????spu??????
            Goods goodsDb = goodsReadMapper.getByPrimaryKey(orderProduct.getGoodsId());
            Goods updateGoods = new Goods();
            updateGoods.setGoodsId(goodsDb.getGoodsId());
            updateGoods.setActualSales(goodsDb.getActualSales() + orderProduct.getProductNum());
            updateGoods.setUpdateTime(new Date());
            goodsWriteMapper.updateByPrimaryKeySelective(updateGoods);
        });
    }
}