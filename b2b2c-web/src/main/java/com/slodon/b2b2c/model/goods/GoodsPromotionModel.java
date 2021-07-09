package com.slodon.b2b2c.model.goods;

import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.constant.PromotionConst;
import com.slodon.b2b2c.core.constant.RedisConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.dao.read.goods.GoodsPromotionReadMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsPromotionHistoryWriteMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsPromotionWriteMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsWriteMapper;
import com.slodon.b2b2c.dao.write.goods.ProductWriteMapper;
import com.slodon.b2b2c.goods.example.GoodsExample;
import com.slodon.b2b2c.goods.example.GoodsPromotionExample;
import com.slodon.b2b2c.goods.example.ProductExample;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.GoodsPromotion;
import com.slodon.b2b2c.goods.pojo.GoodsPromotionHistory;
import com.slodon.b2b2c.goods.pojo.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class GoodsPromotionModel {

    @Resource
    private GoodsPromotionReadMapper goodsPromotionReadMapper;
    @Resource
    private GoodsPromotionWriteMapper goodsPromotionWriteMapper;
    @Resource
    private GoodsPromotionHistoryWriteMapper goodsPromotionHistoryWriteMapper;
    @Resource
    private ProductWriteMapper productWriteMapper;
    @Resource
    private GoodsWriteMapper goodsWriteMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 新增商品活动绑定关系
     *
     * @param goodsPromotion
     * @return
     */
    public Integer saveGoodsPromotion(GoodsPromotion goodsPromotion) {
        int count = goodsPromotionWriteMapper.insert(goodsPromotion);
        if (count == 0) {
            throw new MallException("添加商品活动绑定关系失败，请重试");
        }
        return count;
    }

    /**
     * 根据goodsPromotionId删除商品活动绑定关系
     *
     * @param goodsPromotionId goodsPromotionId
     * @return
     */
    public Integer deleteGoodsPromotion(Integer goodsPromotionId) {
        if (StringUtils.isEmpty(goodsPromotionId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = goodsPromotionWriteMapper.deleteByPrimaryKey(goodsPromotionId);
        if (count == 0) {
            log.error("根据goodsPromotionId：" + goodsPromotionId + "删除商品活动绑定关系失败");
            throw new MallException("删除商品活动绑定关系失败,请重试");
        }
        return count;
    }

    /**
     * 根据goodsPromotionId更新商品活动绑定关系
     *
     * @param goodsPromotion
     * @return
     */
    public Integer updateGoodsPromotion(GoodsPromotion goodsPromotion) {
        if (StringUtils.isEmpty(goodsPromotion.getGoodsPromotionId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = goodsPromotionWriteMapper.updateByPrimaryKeySelective(goodsPromotion);
        if (count == 0) {
            log.error("根据goodsPromotionId：" + goodsPromotion.getGoodsPromotionId() + "更新商品活动绑定关系失败");
            throw new MallException("更新商品活动绑定关系失败,请重试");
        }
        return count;
    }

    /**
     * 根据goodsPromotionId获取商品活动绑定关系详情
     *
     * @param goodsPromotionId goodsPromotionId
     * @return
     */
    public GoodsPromotion getGoodsPromotionByGoodsPromotionId(Integer goodsPromotionId) {
        return goodsPromotionReadMapper.getByPrimaryKey(goodsPromotionId);
    }

    /**
     * 根据promotionId获取商品活动绑定关系详情
     *
     * @param promotionId promotionId
     * @return
     */
    public GoodsPromotion getGoodsPromotionByPromotionId(Integer promotionId) {
        GoodsPromotionExample example = new GoodsPromotionExample();
        example.setPromotionId(promotionId);
        List<GoodsPromotion> list = goodsPromotionReadMapper.listByExample(example);
        AssertUtil.notEmpty(list, "商品活动信息不存在");
        return list.get(0);
    }

    /**
     * 根据productId获取 一级活动 绑定商品活动关系详情
     *
     * @param productId
     * @return
     */
    public GoodsPromotion getGoodsPromotionByProductId(Long productId) {
        GoodsPromotionExample example = new GoodsPromotionExample();
        //一级活动，绑定商品
        example.setBindType(PromotionConst.BIND_TYPE_1);
        example.setIsEffective(PromotionConst.IS_EFFECTIVE_YES);
        example.setProductId(productId);
        List<GoodsPromotion> list = goodsPromotionReadMapper.listByExample(example);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    /**
     * 根据条件获取商品活动绑定关系列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<GoodsPromotion> getGoodsPromotionList(GoodsPromotionExample example, PagerInfo pager) {
        List<GoodsPromotion> goodsPromotionList;
        if (pager != null) {
            pager.setRowsCount(goodsPromotionReadMapper.countByExample(example));
            goodsPromotionList = goodsPromotionReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            goodsPromotionList = goodsPromotionReadMapper.listByExample(example);
        }
        return goodsPromotionList;
    }

    /**
     * 定时清理已结束的商品活动
     *
     * @return
     */
    @Transactional
    public boolean jobClearGoodsPromotion() {
        GoodsPromotionExample example = new GoodsPromotionExample();
        example.setEndTimeBefore(new Date());
        List<GoodsPromotion> list = goodsPromotionReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(list)) {
            //拼接商品id
            StringBuilder productIds = new StringBuilder();
            StringBuilder goodsIds = new StringBuilder();
            //存储在redis的keys
            List<Set<String>> keyList = new ArrayList<>();
            list.forEach(promotion -> {
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
                int count = goodsPromotionHistoryWriteMapper.insert(history);
                AssertUtil.isTrue(count == 0, "记录已结束的商品活动绑定历史信息失败，请重试");

                //删除商品活动表数据
                count = goodsPromotionWriteMapper.deleteByPrimaryKey(promotion.getGoodsPromotionId());
                AssertUtil.isTrue(count == 0, "删除已结束的商品活动信息失败，请重试");

                //商品绑定类型时需要解锁商品锁定状态
                if (promotion.getBindType() == PromotionConst.BIND_TYPE_1) {
                    productIds.append(",").append(promotion.getProductId());
                    goodsIds.append(",").append(promotion.getGoodsId());

                    String purchasedKey = "";
                    if (promotion.getPromotionType().equals(PromotionConst.PROMOTION_TYPE_102)) {
                        //拼团key
                        purchasedKey = RedisConst.SPELL_PURCHASED_NUM_PREFIX + promotion.getGoodsId() + "*";
                    } else if (promotion.getPromotionType().equals(PromotionConst.PROMOTION_TYPE_103)) {
                        //预售key
                        purchasedKey = RedisConst.PRE_SELL_PURCHASED_NUM_PREFIX + promotion.getGoodsId() + "*";
                    } else if (promotion.getPromotionType().equals(PromotionConst.PROMOTION_TYPE_104)) {
                        //秒杀key
                        purchasedKey = RedisConst.REDIS_SECKILL_MEMBER_BUY_NUM_PREFIX + promotion.getProductId() + "*";
                    } else if (promotion.getPromotionType().equals(PromotionConst.PROMOTION_TYPE_105)) {
                        //阶梯团key
                        purchasedKey = RedisConst.LADDER_GROUP_PURCHASED_NUM_PREFIX + promotion.getGoodsId() + "*";
                    }
                    if (!StringUtil.isEmpty(purchasedKey)) {
                        Set<String> keys = stringRedisTemplate.keys(purchasedKey);
                        if (!CollectionUtils.isEmpty(keys)) {
                            keyList.add(keys);
                        }
                    }
                }
            });
            //解锁商品状态
            if (!StringUtil.isEmpty(productIds.toString())) {
                ProductExample productExample = new ProductExample();
                productExample.setProductIdIn(productIds.substring(1));
                Product updateProduct = new Product();
                updateProduct.setState(GoodsConst.PRODUCT_STATE_1);
                int count = productWriteMapper.updateByExampleSelective(updateProduct, productExample);
                AssertUtil.isTrue(count == 0, "修改商品锁定状态失败，请重试");
            }
            if (!StringUtil.isEmpty(goodsIds.toString())) {
                GoodsExample goodsExample = new GoodsExample();
                goodsExample.setGoodsIdIn(goodsIds.substring(1));
                Goods goodsNew = new Goods();
                goodsNew.setIsLock(GoodsConst.IS_LOCK_NO);
                int count = goodsWriteMapper.updateByExampleSelective(goodsNew, goodsExample);
                AssertUtil.isTrue(count == 0, "修改商品锁定状态失败，请重试");
            }

            //删除redis存储数据
            if (!CollectionUtils.isEmpty(keyList)) {
                for (Set<String> keySet : keyList) {
                    stringRedisTemplate.delete(keySet);
                }
            }
        }
        return true;
    }

    /**
     * 失效商品活动
     *
     * @param promotionType
     * @return
     */
    public Integer invalidGoodsPromotion(Integer promotionType) {
        int count = 0;
        GoodsPromotionExample example = new GoodsPromotionExample();
        example.setPromotionType(promotionType);
        List<GoodsPromotion> activityList = goodsPromotionReadMapper.listByExample(example);
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
                count = goodsPromotionHistoryWriteMapper.insert(history);
                AssertUtil.isTrue(count == 0, "记录商品活动绑定历史信息失败，请重试");

                //删除商品活动表数据
                count = goodsPromotionWriteMapper.deleteByPrimaryKey(promotion.getGoodsPromotionId());
                AssertUtil.isTrue(count == 0, "删除商品活动信息失败，请重试");

                productIds.append(",").append(promotion.getProductId());
                goodsIds.append(",").append(promotion.getGoodsId());

                String purchasedKey = "";
                if (promotionType.equals(PromotionConst.PROMOTION_TYPE_102)) {
                    //拼团key
                    purchasedKey = RedisConst.SPELL_PURCHASED_NUM_PREFIX + promotion.getGoodsId() + "*";
                } else if (promotionType.equals(PromotionConst.PROMOTION_TYPE_103)) {
                    //预售key
                    purchasedKey = RedisConst.PRE_SELL_PURCHASED_NUM_PREFIX + promotion.getGoodsId() + "*";
                } else if (promotionType.equals(PromotionConst.PROMOTION_TYPE_104)) {
                    //秒杀key
                    purchasedKey = RedisConst.REDIS_SECKILL_MEMBER_BUY_NUM_PREFIX + promotion.getProductId() + "*";
                } else if (promotionType.equals(PromotionConst.PROMOTION_TYPE_105)) {
                    //阶梯团key
                    purchasedKey = RedisConst.LADDER_GROUP_PURCHASED_NUM_PREFIX + promotion.getGoodsId() + "*";
                }
                if (!StringUtil.isEmpty(purchasedKey)) {
                    Set<String> keys = stringRedisTemplate.keys(purchasedKey);
                    if (!CollectionUtils.isEmpty(keys)) {
                        keyList.add(keys);
                    }
                }
            }
            //解锁商品状态
            ProductExample productExample = new ProductExample();
            productExample.setProductIdIn(productIds.substring(1));
            Product updateProduct = new Product();
            updateProduct.setState(GoodsConst.PRODUCT_STATE_1);
            count = productWriteMapper.updateByExampleSelective(updateProduct, productExample);
            AssertUtil.isTrue(count == 0, "修改商品锁定状态失败，请重试");

            GoodsExample goodsExample = new GoodsExample();
            goodsExample.setGoodsIdIn(goodsIds.substring(1));
            Goods goodsNew = new Goods();
            goodsNew.setIsLock(GoodsConst.IS_LOCK_NO);
            count = goodsWriteMapper.updateByExampleSelective(goodsNew, goodsExample);
            AssertUtil.isTrue(count == 0, "修改商品锁定状态失败，请重试");

            //删除redis存储数据
            if (!CollectionUtils.isEmpty(keyList)) {
                for (Set<String> keySet : keyList) {
                    stringRedisTemplate.delete(keySet);
                }
            }
        }
        return count;
    }
}