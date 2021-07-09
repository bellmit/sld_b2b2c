package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.business.dto.OrderConfirmDTO;
import com.slodon.b2b2c.business.dto.OrderSubmitDTO;
import com.slodon.b2b2c.business.dto.PreOrderDTO;
import com.slodon.b2b2c.business.example.OrderProductExample;
import com.slodon.b2b2c.business.pojo.Order;
import com.slodon.b2b2c.business.pojo.OrderLog;
import com.slodon.b2b2c.business.pojo.OrderPay;
import com.slodon.b2b2c.business.pojo.OrderProduct;
import com.slodon.b2b2c.core.constant.*;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.uid.GoodsIdGenerator;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.dao.read.promotion.PresellGoodsReadMapper;
import com.slodon.b2b2c.dao.read.promotion.PresellLabelReadMapper;
import com.slodon.b2b2c.dao.read.promotion.PresellReadMapper;
import com.slodon.b2b2c.dao.write.promotion.PresellGoodsWriteMapper;
import com.slodon.b2b2c.dao.write.promotion.PresellWriteMapper;
import com.slodon.b2b2c.goods.example.GoodsExample;
import com.slodon.b2b2c.goods.example.GoodsPromotionExample;
import com.slodon.b2b2c.goods.example.ProductExample;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.GoodsPromotion;
import com.slodon.b2b2c.goods.pojo.GoodsPromotionHistory;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.model.business.OrderLogModel;
import com.slodon.b2b2c.model.business.OrderModel;
import com.slodon.b2b2c.model.business.OrderPayModel;
import com.slodon.b2b2c.model.business.OrderProductModel;
import com.slodon.b2b2c.model.goods.GoodsModel;
import com.slodon.b2b2c.model.goods.GoodsPromotionHistoryModel;
import com.slodon.b2b2c.model.goods.GoodsPromotionModel;
import com.slodon.b2b2c.model.goods.ProductModel;
import com.slodon.b2b2c.model.promotion.base.PromotionBaseModel;
import com.slodon.b2b2c.promotion.example.PresellExample;
import com.slodon.b2b2c.promotion.example.PresellGoodsExample;
import com.slodon.b2b2c.promotion.example.PresellOrderExtendExample;
import com.slodon.b2b2c.promotion.pojo.Presell;
import com.slodon.b2b2c.promotion.pojo.PresellGoods;
import com.slodon.b2b2c.promotion.pojo.PresellLabel;
import com.slodon.b2b2c.promotion.pojo.PresellOrderExtend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class PresellModel extends PromotionBaseModel {

    @Resource
    private PresellReadMapper presellReadMapper;
    @Resource
    private PresellWriteMapper presellWriteMapper;
    @Resource
    private PresellGoodsReadMapper presellGoodsReadMapper;
    @Resource
    private PresellGoodsWriteMapper presellGoodsWriteMapper;
    @Resource
    private PresellLabelReadMapper presellLabelReadMapper;

    @Resource
    private PresellOrderExtendModel presellOrderExtendModel;
    @Resource
    private ProductModel productModel;
    @Resource
    private GoodsModel goodsModel;
    @Resource
    private GoodsPromotionModel goodsPromotionModel;
    @Resource
    private GoodsPromotionHistoryModel goodsPromotionHistoryModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private OrderModel orderModel;
    @Resource
    private OrderProductModel orderProductModel;
    @Resource
    private OrderPayModel orderPayModel;
    @Resource
    private OrderLogModel orderLogModel;

    /**
     * 新增预售活动表
     *
     * @param presell
     * @return
     */
    public Integer savePresell(Presell presell) {
        int count = presellWriteMapper.insert(presell);
        if (count == 0) {
            throw new MallException("添加预售活动表失败，请重试");
        }
        return count;
    }

    /**
     * 新增预售活动
     *
     * @param presell
     * @param preSellGoodsList
     * @return
     */
    @Transactional
    public Integer savePresell(Presell presell, List<PresellGoods> preSellGoodsList) {
        //查询标签信息
        PresellLabel presellLabel = presellLabelReadMapper.getByPrimaryKey(presell.getPresellLabelId());
        AssertUtil.notNull(presellLabel, "获取标签信息为空，请重试");

        presell.setPresellLabelName(presellLabel.getPresellLabelName());
        presell.setState(PreSellConst.ACTIVITY_STATE_1);
        presell.setCreateTime(new Date());
        int count = presellWriteMapper.insert(presell);
        AssertUtil.isTrue(count == 0, "保存预售活动失败，请重试");

        //拼接商品id
        StringBuilder productIds = new StringBuilder();
        StringBuilder goodsIds = new StringBuilder();
        for (PresellGoods goodsVO : preSellGoodsList) {
            PresellGoods presellGoods = new PresellGoods();
            BeanUtils.copyProperties(goodsVO, presellGoods);
            if (presell.getType() == PreSellConst.PRE_SELL_TYPE_1) {
                if (!StringUtil.isNullOrZero(goodsVO.getFirstExpand())) {
                    AssertUtil.isTrue(goodsVO.getFirstExpand().compareTo(goodsVO.getFirstMoney()) < 0, "定金抵现金额不能小于预售定金");

                    presellGoods.setFirstExpand(goodsVO.getFirstExpand());
                    presellGoods.setSecondMoney(goodsVO.getPresellPrice().subtract(goodsVO.getFirstExpand()));
                } else {
                    presellGoods.setSecondMoney(goodsVO.getPresellPrice().subtract(goodsVO.getFirstMoney()));
                }
            }

            //查询货品信息
            Product product = productModel.getProductByProductId(goodsVO.getProductId());
            AssertUtil.notNull(product, "获取货品信息为空，请重试");
            AssertUtil.isTrue(goodsVO.getPresellPrice().compareTo(product.getProductPrice()) > 0, "预售价不能大于原价");

            //查询商品信息
            Goods goods = goodsModel.getGoodsByGoodsId(product.getGoodsId());
            AssertUtil.notNull(goods, "获取商品信息为空，请重试");
            AssertUtil.isTrue(goods.getIsLock() == GoodsConst.IS_LOCK_YES, "商品名称为：" + goods.getGoodsName() + "的商品已锁定");

            presellGoods.setPresellId(presell.getPresellId());
            presellGoods.setGoodsId(product.getGoodsId());
            presellGoods.setGoodsName(goods.getGoodsName());
            presellGoods.setGoodsImage(goods.getMainImage());
            presellGoods.setSpecValues(product.getSpecValues());
            presellGoods.setStock(product.getProductStock());
            presellGoods.setProductPrice(product.getProductPrice());
            presellGoods.setVirtualSale(0);
            presellGoods.setActualSale(0);
            count = presellGoodsWriteMapper.insert(presellGoods);
            AssertUtil.isTrue(count == 0, "保存预售商品失败，请重试");

            productIds.append(",").append(product.getProductId());
            goodsIds.append(",").append(product.getGoodsId());
        }
        //参与预售时锁定商品状态
        ProductExample example = new ProductExample();
        example.setProductIdIn(productIds.substring(1));
        Product updateProduct = new Product();
        updateProduct.setState(GoodsConst.PRODUCT_STATE_3);
        count = productModel.updateProductByExample(updateProduct, example);
        AssertUtil.isTrue(count == 0, "修改商品锁定状态失败，请重试");

        GoodsExample goodsExample = new GoodsExample();
        goodsExample.setGoodsIdIn(goodsIds.substring(1));
        Goods updateGoods = new Goods();
        updateGoods.setIsLock(GoodsConst.IS_LOCK_YES);
        count = goodsModel.updateGoodsByExample(updateGoods, goodsExample);
        AssertUtil.isTrue(count == 0, "修改商品锁定状态失败，请重试");
        return presell.getPresellId();
    }

    /**
     * 根据presellId删除预售活动表
     *
     * @param presellId presellId
     * @return
     */
    @Transactional
    public Integer deletePresell(Integer presellId) {
        if (StringUtils.isEmpty(presellId)) {
            throw new MallException("请选择要删除的数据");
        }
        // 查询预售活动信息
        Presell preSellDB = presellReadMapper.getByPrimaryKey(presellId);
        AssertUtil.notNull(preSellDB, "获取预售活动信息为空，请重试。");

        Date date = new Date();
        AssertUtil.isTrue(preSellDB.getState() == PreSellConst.ACTIVITY_STATE_2
                        && (date.after(preSellDB.getStartTime()) && date.before(preSellDB.getEndTime())),
                "正在进行中的的预售活动不能删除");

        int count = 0;
        //查询预售商品列表
        PresellGoodsExample example = new PresellGoodsExample();
        example.setPresellId(presellId);
        List<PresellGoods> goodsList = presellGoodsReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(goodsList)) {
            //拼接商品id
            StringBuilder productIds = new StringBuilder();
            StringBuilder goodsIds = new StringBuilder();
            for (PresellGoods presellGoods : goodsList) {
                productIds.append(",").append(presellGoods.getProductId());
                goodsIds.append(",").append(presellGoods.getGoodsId());
            }
            //解锁商品状态
            ProductExample productExample = new ProductExample();
            productExample.setProductIdIn(productIds.substring(1));
            Product updateProduct = new Product();
            updateProduct.setState(GoodsConst.PRODUCT_STATE_1);
            count = productModel.updateProductByExample(updateProduct, productExample);
            AssertUtil.isTrue(count == 0, "修改商品锁定状态失败，请重试");

            GoodsExample goodsExample = new GoodsExample();
            goodsExample.setGoodsIdIn(goodsIds.substring(1));
            Goods goodsNew = new Goods();
            goodsNew.setIsLock(GoodsConst.IS_LOCK_NO);
            count = goodsModel.updateGoodsByExample(goodsNew, goodsExample);
            AssertUtil.isTrue(count == 0, "修改商品锁定状态失败，请重试");
        }
        if (preSellDB.getState() == PreSellConst.ACTIVITY_STATE_1) {
            count = presellWriteMapper.deleteByPrimaryKey(presellId);
            if (count == 0) {
                throw new MallException("删除预售活动失败，请重试");
            }
            presellGoodsWriteMapper.deleteByExample(example);
        } else {
            Presell presell = new Presell();
            presell.setPresellId(presellId);
            presell.setState(PreSellConst.ACTIVITY_STATE_4);
            count = presellWriteMapper.updateByPrimaryKeySelective(presell);
            if (count == 0) {
                throw new MallException("删除预售活动失败，请重试！");
            }
        }
        return count;
    }

    /**
     * 根据presellId更新预售活动表
     *
     * @param presell
     * @return
     */
    @Transactional
    public Integer updatePresell(Presell presell) {
        if (StringUtils.isEmpty(presell.getPresellId())) {
            throw new MallException("请选择要修改的数据");
        }
        // 查询该预售活动
        Presell presellDB = presellReadMapper.getByPrimaryKey(presell.getPresellId());
        AssertUtil.notNull(presellDB, "获取预售活动信息为空，请重试");

        presell.setUpdateTime(new Date());
        int count = presellWriteMapper.updateByPrimaryKeySelective(presell);
        if (count == 0) {
            log.error("根据presellId：" + presell.getPresellId() + "更新预售活动表失败");
            throw new MallException("更新预售活动表失败,请重试");
        }
        //发布时将预售商品活动数据记录到商品活动表
        if (!StringUtil.isNullOrZero(presell.getState()) && presell.getState() == PreSellConst.ACTIVITY_STATE_2) {
            PresellGoodsExample example = new PresellGoodsExample();
            example.setPresellId(presell.getPresellId());
            List<PresellGoods> goodsList = presellGoodsReadMapper.listByExample(example);
            AssertUtil.notEmpty(goodsList, "获取货品信息为空，请重试");

            for (PresellGoods presellGoods : goodsList) {
                GoodsPromotion promotion = new GoodsPromotion();
                promotion.setPromotionId(presell.getPresellId());
                promotion.setStoreId(presellDB.getStoreId());
                promotion.setStoreName(presellDB.getStoreName());
                promotion.setBindTime(new Date());
                promotion.setGoodsId(presellGoods.getGoodsId());
                promotion.setProductId(presellGoods.getProductId());
                promotion.setPromotionGrade(PromotionConst.PROMOTION_GRADE_1);
                promotion.setPromotionType(PromotionConst.PROMOTION_TYPE_103);
                promotion.setStartTime(presellDB.getStartTime());
                promotion.setEndTime(presellDB.getEndTime());
                promotion.setDescription("预售商品活动");
                promotion.setIsEffective(PromotionConst.IS_EFFECTIVE_YES);
                promotion.setBindType(PromotionConst.BIND_TYPE_1);
                count = goodsPromotionModel.saveGoodsPromotion(promotion);
                AssertUtil.isTrue(count == 0, "记录商品活动信息失败，请重试");
            }
        }
        //失效时将预售商品活动数据记录到商品活动历史表，并删除商品活动表的数据
        if (!StringUtil.isNullOrZero(presell.getState()) && presell.getState() == PreSellConst.ACTIVITY_STATE_3) {
            GoodsPromotionExample example = new GoodsPromotionExample();
            example.setPromotionId(presell.getPresellId());
            example.setPromotionType(PromotionConst.PROMOTION_TYPE_103);
            List<GoodsPromotion> activityList = goodsPromotionModel.getGoodsPromotionList(example, null);
            if (!CollectionUtils.isEmpty(activityList)) {
                //拼接商品id
                StringBuilder productIds = new StringBuilder();
                StringBuilder goodsIds = new StringBuilder();
                //存储在redis的keys
                List<Set<String>> keyList = new ArrayList<>();
                for (GoodsPromotion promotion : activityList) {
                    GoodsPromotionHistory history = new GoodsPromotionHistory();
                    history.setGoodsPromotionId(promotion.getGoodsPromotionId());
                    history.setBindTime(promotion.getBindTime());
                    history.setStoreId(promotion.getStoreId());
                    history.setStoreName(promotion.getStoreName());
                    history.setGoodsId(promotion.getGoodsId());
                    history.setProductId(promotion.getProductId());
                    history.setPromotionId(promotion.getPromotionId());
                    history.setPromotionType(promotion.getPromotionType());
                    history.setStartTime(promotion.getStartTime());
                    history.setEndTime(promotion.getEndTime());
                    history.setDescription(promotion.getDescription());
                    //记录商品活动历史
                    count = goodsPromotionHistoryModel.saveGoodsPromotionHistory(history);
                    AssertUtil.isTrue(count == 0, "记录商品活动绑定历史信息失败，请重试");

                    //删除商品活动表数据
                    count = goodsPromotionModel.deleteGoodsPromotion(promotion.getGoodsPromotionId());
                    AssertUtil.isTrue(count == 0, "删除商品活动信息失败，请重试");

                    productIds.append(",").append(promotion.getProductId());
                    goodsIds.append(",").append(promotion.getGoodsId());

                    Set<String> keys = stringRedisTemplate.keys(RedisConst.PRE_SELL_PURCHASED_NUM_PREFIX + promotion.getGoodsId() + "*");
                    if (!CollectionUtils.isEmpty(keys)) {
                        keyList.add(keys);
                    }
                }
                //解锁商品状态
                ProductExample productExample = new ProductExample();
                productExample.setProductIdIn(productIds.substring(1));
                Product updateProduct = new Product();
                updateProduct.setState(GoodsConst.PRODUCT_STATE_1);
                count = productModel.updateProductByExample(updateProduct, productExample);
                AssertUtil.isTrue(count == 0, "修改商品锁定状态失败，请重试");

                GoodsExample goodsExample = new GoodsExample();
                goodsExample.setGoodsIdIn(goodsIds.substring(1));
                Goods goodsNew = new Goods();
                goodsNew.setIsLock(GoodsConst.IS_LOCK_NO);
                count = goodsModel.updateGoodsByExample(goodsNew, goodsExample);
                AssertUtil.isTrue(count == 0, "修改商品锁定状态失败，请重试");
                //删除redis存储数据
                if (!CollectionUtils.isEmpty(keyList)) {
                    for (Set<String> keySet : keyList) {
                        stringRedisTemplate.delete(keySet);
                    }
                }
            }
        }
        return count;
    }

    /**
     * 编辑预售活动
     *
     * @param presell
     * @param preSellGoodsList
     * @return
     */
    @Transactional
    public Integer updatePresell(Presell presell, List<PresellGoods> preSellGoodsList) {
        // 查询该预售活动
        Presell presellDB = presellReadMapper.getByPrimaryKey(presell.getPresellId());
        AssertUtil.notNull(presellDB, "获取预售活动信息为空，请重试");

        if (StringUtils.isEmpty(presell.getState())) {
            AssertUtil.isTrue(presellDB.getState() != PreSellConst.ACTIVITY_STATE_1, "非法操作！只有刚创建的预售活动信息允许编辑，请重试");
        }
        if (!presell.getPresellLabelId().equals(presellDB.getPresellLabelId())) {
            //查询标签信息
            PresellLabel presellLabel = presellLabelReadMapper.getByPrimaryKey(presell.getPresellLabelId());
            AssertUtil.notNull(presellLabel, "获取标签信息为空，请重试");

            presell.setPresellLabelName(presellLabel.getPresellLabelName());
        }
        presell.setUpdateTime(new Date());
        int count = presellWriteMapper.updateByPrimaryKeySelective(presell);
        AssertUtil.isTrue(count == 0, "编辑预售活动失败,请重试");

        if (!CollectionUtils.isEmpty(preSellGoodsList)) {
            //删除旧的预售商品信息
            PresellGoodsExample example = new PresellGoodsExample();
            example.setPresellId(presell.getPresellId());
            presellGoodsWriteMapper.deleteByExample(example);

            //拼接商品id
            StringBuilder productIds = new StringBuilder();
            StringBuilder goodsIds = new StringBuilder();
            for (PresellGoods goodsVO : preSellGoodsList) {
                PresellGoods presellGoods = new PresellGoods();
                BeanUtils.copyProperties(goodsVO, presellGoods);
                if (presell.getType() == PreSellConst.PRE_SELL_TYPE_1) {
                    if (!StringUtil.isNullOrZero(goodsVO.getFirstExpand())) {
                        AssertUtil.isTrue(goodsVO.getFirstExpand().compareTo(goodsVO.getFirstMoney()) < 0, "定金抵现金额不能小于预售定金");

                        presellGoods.setFirstExpand(goodsVO.getFirstExpand());
                        presellGoods.setSecondMoney(goodsVO.getPresellPrice().subtract(goodsVO.getFirstExpand()));
                    } else {
                        presellGoods.setSecondMoney(goodsVO.getPresellPrice().subtract(goodsVO.getFirstMoney()));
                    }
                }

                //查询货品信息
                Product product = productModel.getProductByProductId(goodsVO.getProductId());
                AssertUtil.notNull(product, "获取货品信息为空，请重试");

                //查询商品信息
                Goods goods = goodsModel.getGoodsByGoodsId(product.getGoodsId());
                AssertUtil.notNull(goods, "获取商品信息为空，请重试");

                presellGoods.setPresellId(presell.getPresellId());
                presellGoods.setGoodsId(product.getGoodsId());
                presellGoods.setGoodsName(goods.getGoodsName());
                presellGoods.setGoodsImage(goods.getMainImage());
                presellGoods.setSpecValues(product.getSpecValues());
                presellGoods.setStock(product.getProductStock());
                presellGoods.setProductPrice(product.getProductPrice());
                presellGoods.setVirtualSale(0);
                presellGoods.setActualSale(0);
                count = presellGoodsWriteMapper.insert(presellGoods);
                AssertUtil.isTrue(count == 0, "编辑预售商品失败,请重试");

                productIds.append(",").append(product.getProductId());
                goodsIds.append(",").append(product.getGoodsId());
            }
            //参与预售时锁定商品状态
            ProductExample productExample = new ProductExample();
            productExample.setProductIdIn(productIds.substring(1));
            Product updateProduct = new Product();
            updateProduct.setState(GoodsConst.PRODUCT_STATE_3);
            count = productModel.updateProductByExample(updateProduct, productExample);
            AssertUtil.isTrue(count == 0, "修改商品锁定状态失败，请重试");

            GoodsExample goodsExample = new GoodsExample();
            goodsExample.setGoodsIdIn(goodsIds.substring(1));
            Goods updateGoods = new Goods();
            updateGoods.setIsLock(GoodsConst.IS_LOCK_YES);
            count = goodsModel.updateGoodsByExample(updateGoods, goodsExample);
            AssertUtil.isTrue(count == 0, "修改商品锁定状态失败，请重试");
        }
        return count;
    }

    /**
     * 根据presellId获取预售活动表详情
     *
     * @param presellId presellId
     * @return
     */
    public Presell getPresellByPresellId(Integer presellId) {
        return presellReadMapper.getByPrimaryKey(presellId);
    }

    /**
     * 根据条件获取预售活动表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<Presell> getPresellList(PresellExample example, PagerInfo pager) {
        List<Presell> presellList;
        if (pager != null) {
            pager.setRowsCount(presellReadMapper.countByExample(example));
            presellList = presellReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            presellList = presellReadMapper.listByExample(example);
        }
        return presellList;
    }

    /**
     * 复制预售
     *
     * @param presell
     */
    @Transactional
    public Integer copyPreSell(Presell presell) {
        // 查询预售活动信息
        Presell presellDB = presellReadMapper.getByPrimaryKey(presell.getPresellId());
        AssertUtil.notNull(presellDB, "该预售活动不存在，请重新选择");
        AssertUtil.isTrue(!presellDB.getStoreId().equals(presell.getStoreId()), "非法操作!");

        Presell presellNew = new Presell();
        BeanUtils.copyProperties(presellDB, presellNew);
        presellNew.setPresellName("copy-" + presellDB.getPresellName());
        presellNew.setState(PreSellConst.ACTIVITY_STATE_1);
        presellNew.setCreateTime(new Date());
        int count = presellWriteMapper.insert(presellNew);
        AssertUtil.isTrue(count == 0, "复制预售活动失败，请重试");

        // 查询预售活动下的商品
        PresellGoodsExample example = new PresellGoodsExample();
        example.setPresellId(presell.getPresellId());
        List<PresellGoods> presellGoodsList = presellGoodsReadMapper.listByExample(example);
        AssertUtil.notEmpty(presellGoodsList, "查询该预售活动下的商品信息为空，请重试");

        for (PresellGoods goods : presellGoodsList) {
            PresellGoods presellGoods = new PresellGoods();
            BeanUtils.copyProperties(goods, presellGoods);
            presellGoods.setPresellId(presellNew.getPresellId());
            count = presellGoodsWriteMapper.insert(presellGoods);
            AssertUtil.isTrue(count == 0, "复制预售商品失败，请重试");
        }
        return count;
    }

    /**
     * 活动名称
     *
     * @return
     */
    @Override
    public String promotionName() {
        return "预售";
    }

    /**
     * 活动价格
     *
     * @param promotionId
     * @param productId
     * @return
     */
    public BigDecimal promotionPrice(Integer promotionId, Long productId) {
        //预售活动是否可用
        if (!isPromotionAvailable(promotionId.toString())) {
            return BigDecimal.ZERO;
        }
        PresellGoodsExample example = new PresellGoodsExample();
        example.setPresellId(promotionId);
        example.setProductId(productId);
        List<PresellGoods> list = presellGoodsReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0).getPresellPrice();
        } else {
            return BigDecimal.ZERO;
        }
    }

    /**
     * 是否有扩展方法
     *
     * @return
     */
    @Override
    public Boolean specialOrder() {
        return true;
    }

    /**
     * 提交订单扩展方法
     *
     * @param orderSn 订单号
     * @return
     */
    public Integer submitOrderExtend(String orderSn) {
        //查询订单信息
        Order order = orderModel.getOrderByOrderSn(orderSn);
        //查询订单货品信息
        OrderProductExample productExample = new OrderProductExample();
        productExample.setOrderSn(orderSn);
        List<OrderProduct> orderProductList = orderProductModel.getOrderProductList(productExample, null);
        AssertUtil.notEmpty(orderProductList, "获取拼团订单货品信息为空");
        //预售订单货品只有一个
        OrderProduct product = orderProductList.get(0);
        //查询商品活动信息
        GoodsPromotionExample promotionExample = new GoodsPromotionExample();
        promotionExample.setProductId(product.getProductId());
        promotionExample.setPromotionType(PromotionConst.PROMOTION_TYPE_103);
        List<GoodsPromotion> goodsPromotionList = goodsPromotionModel.getGoodsPromotionList(promotionExample, null);
        AssertUtil.notEmpty(goodsPromotionList, "获取预售商品信息为空");
        GoodsPromotion goodsPromotion = goodsPromotionList.get(0);
        Date date = new Date();
        AssertUtil.isTrue(date.after(goodsPromotion.getEndTime()), "预售活动已结束，请重试");
        //获取预售商品信息
        PresellGoodsExample example = new PresellGoodsExample();
        example.setPresellId(goodsPromotion.getPromotionId());
        example.setProductId(product.getProductId());
        List<PresellGoods> goodsList = presellGoodsReadMapper.listByExample(example);
        AssertUtil.notEmpty(goodsList, "获取预售商品信息为空，请重试！");
        PresellGoods presellGoods = goodsList.get(0);
        //保存预售订单扩展表
        presellOrderExtendModel.insertOrderPreSale(orderSn, order.getPaySn(), goodsPromotion.getPromotionId(),
                product.getProductId(), product.getProductNum(), order.getMemberId());
        //修改库存
        PresellGoods presellGoodsNew = new PresellGoods();
        presellGoodsNew.setPresellGoodsId(presellGoods.getPresellGoodsId());
        presellGoodsNew.setActualSale(presellGoods.getActualSale() + product.getProductNum());
        presellGoodsNew.setPresellStock(presellGoods.getPresellStock() - product.getProductNum());
        return presellGoodsWriteMapper.updateByPrimaryKeySelective(presellGoodsNew);
    }

    /**
     * 确认订单计算活动优惠
     *
     * @param promotionCartGroup 按照活动类型-活动id分组的购物车列表
     */
    @Override
    public void orderConfirmCalculationDiscount(OrderConfirmDTO.StoreCartGroup.PromotionCartGroup promotionCartGroup) {

    }

    /**
     * 提交订单计算活动优惠
     *
     * @param dto 拆单后的订单提交信息
     */
    @Override
    public void orderSubmitCalculationDiscount(OrderSubmitDTO dto) {
        dto.getOrderInfoList().forEach(orderInfo -> {
            orderInfo.getOrderProductInfoList().forEach(orderProductInfo -> {
                //预售活动是否可用
                if (!isPromotionAvailable(orderProductInfo.getPromotionId().toString())) {
                    throw new MallException("预售活动不可用");
                }
                //构造活动信息
                OrderSubmitDTO.PromotionInfo promotionInfo = new OrderSubmitDTO.PromotionInfo();
                promotionInfo.setPromotionType(PromotionConst.PROMOTION_TYPE_103);
                promotionInfo.setPromotionId(orderProductInfo.getPromotionId().toString());
                promotionInfo.setPromotionName(this.promotionName());
                promotionInfo.setIsStore(true);
                //查询预售活动信息
                Presell presell = this.getPresellByPresellId(orderProductInfo.getPromotionId());
                //获取预售商品信息
                PresellGoodsExample example = new PresellGoodsExample();
                example.setPresellId(orderProductInfo.getPromotionId());
                example.setGoodsId(orderProductInfo.getGoodsId());
                example.setProductId(orderProductInfo.getProductId());
                List<PresellGoods> presellGoodsList = presellGoodsReadMapper.listByExample(example);
                AssertUtil.notEmpty(presellGoodsList, "获取预售商品信息为空，请重试！");
                PresellGoods presellGoods = presellGoodsList.get(0);

                //优惠金额
                BigDecimal discount = BigDecimal.ZERO;
                if (presell.getType() == PreSellConst.PRE_SELL_TYPE_1) {
                    //定金预售优惠
                    if (!StringUtil.isNullOrZero(presellGoods.getFirstExpand())) {
                        discount = presellGoods.getFirstExpand().subtract(presellGoods.getFirstMoney());
                    }
                }
                promotionInfo.setDiscount(discount);
                //订单货品添加活动
                orderProductInfo.getPromotionInfoList().add(promotionInfo);
                orderProductInfo.setProductPrice(presellGoods.getPresellPrice());
            });
        });
    }

    /**
     * 校验活动是否可用
     *
     * @param promotionId 活动id
     * @return
     */
    @Override
    public Boolean isPromotionAvailable(String promotionId) {
        //校验预售开关是否开启，开启则校验活动状态
        if ("1".equals(stringRedisTemplate.opsForValue().get("presale_is_enable"))) {
            Presell preSellDB = presellReadMapper.getByPrimaryKey(promotionId);
            Date date = new Date();
            if (preSellDB.getState() == PreSellConst.ACTIVITY_STATE_2
                    && (date.after(preSellDB.getStartTime()) && date.before(preSellDB.getEndTime()))) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 订单提交预处理
     *
     * @param promotionId 活动id
     * @param productId   货品id
     * @param buyNum      购买数量
     * @return
     */
    public PreOrderDTO preOrderSubmit(Integer promotionId, Long productId, Integer buyNum, Integer memberId) {
        AssertUtil.isTrue(!isPromotionAvailable(promotionId.toString()), "预售活动不可用");
        Presell presell = this.getPresellByPresellId(promotionId);
        AssertUtil.notNull(presell, "预售活动不存在");
        //查询预售商品，校验库存
        PresellGoodsExample example = new PresellGoodsExample();
        example.setPresellId(promotionId);
        example.setProductId(productId);
        List<PresellGoods> presellGoodsList = presellGoodsReadMapper.listByExample(example);
        AssertUtil.notEmpty(presellGoodsList, "预售商品不存在");
        PresellGoods presellGoods = presellGoodsList.get(0);
        AssertUtil.isTrue(buyNum.compareTo(presellGoods.getPresellStock()) > 0, "库存不足");

        //预售，先加购买数量
        String purchasedNum = stringRedisTemplate.opsForValue().get(RedisConst.PRE_SELL_PURCHASED_NUM_PREFIX + presellGoods.getGoodsId() + "_" + memberId);
        if (!StringUtil.isEmpty(purchasedNum)) {
            //redis不为空，预售购买数量累加
            stringRedisTemplate.opsForValue().increment(RedisConst.PRE_SELL_PURCHASED_NUM_PREFIX + presellGoods.getGoodsId() + "_" + memberId, buyNum);
        } else {
            //设置过期时间（毫秒）
            long time1 = presell.getEndTime().getTime();
            long time2 = new Date().getTime();
            long remainTime = time1 - time2;
            stringRedisTemplate.opsForValue().set(RedisConst.PRE_SELL_PURCHASED_NUM_PREFIX + presellGoods.getGoodsId() + "_" + memberId,
                    buyNum.toString(), remainTime < 0 ? 0 : remainTime, TimeUnit.MILLISECONDS);
        }

        PreOrderDTO dto = new PreOrderDTO();
        dto.setIsCalculateDiscount(presell.getType() != PreSellConst.PRE_SELL_TYPE_1);
        dto.setOrderType(PromotionConst.PROMOTION_TYPE_103);
        dto.setGoodsId(presellGoods.getGoodsId());
        return dto;
    }

    /**
     * 失效活动
     *
     * @return
     */
    @Transactional
    public Integer invalidPromotion() {
        PresellExample example = new PresellExample();
        example.setState(PreSellConst.ACTIVITY_STATE_2);
        Presell presell = new Presell();
        presell.setState(PreSellConst.ACTIVITY_STATE_3);
        presell.setUpdateTime(new Date());
        int count = presellWriteMapper.updateByExampleSelective(presell, example);

        goodsPromotionModel.invalidGoodsPromotion(PromotionConst.PROMOTION_TYPE_103);
        return count;
    }

    /**
     * 活动订单支付成功特殊处理
     *
     * @param orderSn 订单号
     */
    public void orderPaySuccess(String orderSn, String tradeSn, String paySn, String paymentName, String paymentCode) {
        //查询订单信息
        Order order = orderModel.getOrderByOrderSn(orderSn);
        //查询预售订单扩展信息
        PresellOrderExtendExample extendExample = new PresellOrderExtendExample();
        extendExample.setOrderSn(orderSn);
        List<PresellOrderExtend> presellOrderExtendList = presellOrderExtendModel.getPresellOrderExtendList(extendExample, null);
        AssertUtil.notEmpty(presellOrderExtendList, "获取预售订单扩展信息为空");
        PresellOrderExtend presellOrderExtendDb = presellOrderExtendList.get(0);
        PresellOrderExtend presellOrderExtend = new PresellOrderExtend();
        //更新预售订单扩展表
        presellOrderExtend.setExtendId(presellOrderExtendDb.getExtendId());
        //更新订单信息
        Order orderNew = new Order();
        orderNew.setOrderSn(orderSn);
        //定金预售
        if (presellOrderExtendDb.getIsAllPay() == OrderConst.IS_ALL_PAY_0) {
            if (presellOrderExtendDb.getOrderSubState() == OrderConst.ORDER_SUB_STATE_102) {
                Date date = new Date();
                AssertUtil.isTrue(date.before(presellOrderExtendDb.getRemainStartTime()), "未到尾款支付时间");

                presellOrderExtend.setOrderSubState(OrderConst.ORDER_SUB_STATE_103);
                presellOrderExtendModel.updatePresellOrderExtend(presellOrderExtend);

                //记录订单日志
                this.insertOrderLog(OrderConst.LOG_ROLE_MEMBER, Long.valueOf(order.getMemberId()), order.getMemberName(), orderSn, OrderConst.ORDER_STATE_20, "预售订单支付完成");

                //尾款支付订单更新支付状态
                orderNew.setOrderState(OrderConst.ORDER_STATE_20);
                orderNew.setPayTime(new Date());
                //付完尾款再减去膨胀或定金
                BigDecimal depositAmount = BigDecimal.ZERO;
                if (!StringUtil.isNullOrZero(presellOrderExtendDb.getFirstExpand())) {
                    depositAmount = presellOrderExtendDb.getFirstExpand().multiply(new BigDecimal(presellOrderExtendDb.getProductNum()));
                } else {
                    depositAmount = presellOrderExtendDb.getDepositAmount().multiply(new BigDecimal(presellOrderExtendDb.getProductNum()));
                }
                order.setOrderAmount(order.getOrderAmount().subtract(depositAmount));
                orderModel.updateOrder(orderNew);

                //修改支付表尾款支付状态
                OrderPay oldOrderPay = new OrderPay();
                oldOrderPay.setPaySn(paySn);
                oldOrderPay.setApiPayState(OrderConst.API_PAY_STATE_1);
                oldOrderPay.setCallbackTime(new Date());
                oldOrderPay.setTradeSn(tradeSn);
                oldOrderPay.setPaymentName(paymentName);
                oldOrderPay.setPaymentCode(paymentCode);
                orderPayModel.updateOrderPay(oldOrderPay);
            } else {
                String remainPaySn = GoodsIdGenerator.getPaySn();
                presellOrderExtend.setRemainPaySn(remainPaySn);
                presellOrderExtend.setOrderSubState(OrderConst.ORDER_SUB_STATE_102);
                presellOrderExtendModel.updatePresellOrderExtend(presellOrderExtend);

                //记录订单日志
                this.insertOrderLog(OrderConst.LOG_ROLE_MEMBER, Long.valueOf(order.getMemberId()), order.getMemberName(), orderSn, OrderConst.ORDER_SUB_STATE_102, "预售定金支付完成");

                //生成一条尾款支付信息
                OrderPay orderPay = new OrderPay();
                orderPay.setPaySn(remainPaySn);
                //保证定金支付单和尾款支付单的父订单号一致
                orderPay.setOrderSn(order.getParentSn());
                BigDecimal payAmount = BigDecimal.ZERO;
                if (!StringUtil.isNullOrZero(presellOrderExtendDb.getFirstExpand())) {
                    payAmount = presellOrderExtendDb.getPresellPrice().subtract(presellOrderExtendDb.getFirstExpand());
                    orderPay.setPayAmount(payAmount.multiply(new BigDecimal(presellOrderExtendDb.getProductNum())));
                } else {
                    payAmount = presellOrderExtendDb.getPresellPrice().subtract(presellOrderExtendDb.getDepositAmount());
                    orderPay.setPayAmount(payAmount.multiply(new BigDecimal(presellOrderExtendDb.getProductNum())));
                }
                orderPay.setMemberId(order.getMemberId());
                orderPay.setPaymentName(OrderPaymentConst.PAYMENT_NAME_ONLINE);
                orderPay.setPaymentCode(OrderPaymentConst.PAYMENT_CODE_ONLINE);
                orderPayModel.saveOrderPay(orderPay);

                orderNew.setPaySn(remainPaySn);
                //定金支付成功修改订单金额为定金+尾款
                BigDecimal orderAmount = presellOrderExtendDb.getDepositAmount().add(presellOrderExtendDb.getRemainAmount());
                orderNew.setGoodsAmount(orderAmount.multiply(new BigDecimal(presellOrderExtendDb.getProductNum())));
                orderNew.setOrderAmount(orderAmount.multiply(new BigDecimal(presellOrderExtendDb.getProductNum())).add(order.getExpressFee())); //加上运费
                orderModel.updateOrder(orderNew);

                //修改订单货品展示价格
                OrderProductExample orderProductExample = new OrderProductExample();
                orderProductExample.setOrderSn(presellOrderExtendDb.getOrderSn());
                orderProductExample.setProductId(presellOrderExtendDb.getProductId());
                List<OrderProduct> orderProductList = orderProductModel.getOrderProductList(orderProductExample, null);
                AssertUtil.notEmpty(orderProductList, "订单货品不存在");

                OrderProduct orderProduct = new OrderProduct();
                orderProduct.setOrderProductId(orderProductList.get(0).getOrderProductId());
                orderProduct.setMoneyAmount(orderAmount.multiply(new BigDecimal(presellOrderExtendDb.getProductNum())));
                orderProduct.setProductShowPrice(presellOrderExtendDb.getPresellPrice());
                orderProductModel.updateOrderProduct(orderProduct);

                //修改支付表定金支付状态
                OrderPay oldOrderPay = new OrderPay();
                oldOrderPay.setPaySn(paySn);
                oldOrderPay.setApiPayState(OrderConst.API_PAY_STATE_1);
                oldOrderPay.setCallbackTime(new Date());
                oldOrderPay.setTradeSn(tradeSn);
                oldOrderPay.setPaymentName(paymentName);
                oldOrderPay.setPaymentCode(paymentCode);
                orderPayModel.updateOrderPay(oldOrderPay);
            }
        } else {
            presellOrderExtend.setOrderSubState(OrderConst.ORDER_SUB_STATE_103);
            presellOrderExtendModel.updatePresellOrderExtend(presellOrderExtend);

            //记录订单日志
            this.insertOrderLog(OrderConst.LOG_ROLE_MEMBER, Long.valueOf(order.getMemberId()), order.getMemberName(), orderSn, OrderConst.ORDER_STATE_20, "预售订单支付完成");

            //全款订单更新支付状态
            orderNew.setOrderState(OrderConst.ORDER_STATE_20);
            orderNew.setPayTime(new Date());
            orderModel.updateOrder(orderNew);

            //该支付单号下没有待支付的订单，修改支付表支付状态
            OrderPay orderPay = new OrderPay();
            orderPay.setPaySn(paySn);
            orderPay.setApiPayState(OrderConst.API_PAY_STATE_1);
            orderPay.setCallbackTime(new Date());
            orderPay.setTradeSn(tradeSn);
            orderPay.setPaymentName(paymentName);
            orderPay.setPaymentCode(paymentCode);
            orderPayModel.updateOrderPay(orderPay);
        }
    }

    /**
     * 保存订单日志
     *
     * @param logRole     操作人角色(1-系统管理员，2-商户，3-会员）
     * @param logUserId   操作人ID，结合log_role使用
     * @param logUserName 操作人名称
     * @param orderSn     订单号
     * @param orderState  订单状态：0-已取消；10-未付款订单；102-已付定金；20-已付款；30-已发货；40-已完成;50-已关闭
     * @param content     文字描述
     */
    public void insertOrderLog(Integer logRole, Long logUserId, String logUserName, String orderSn, Integer orderState, String content) {
        OrderLog orderLog = new OrderLog();
        orderLog.setLogRole(logRole);
        orderLog.setLogUserId(logUserId);
        orderLog.setLogUserName(logUserName);
        orderLog.setOrderSn(orderSn);
        orderLog.setOrderStateLog(orderState);
        orderLog.setLogTime(new Date());
        orderLog.setLogContent(content);
        orderLogModel.saveOrderLog(orderLog);
    }
}