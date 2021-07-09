package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.business.dto.OrderConfirmDTO;
import com.slodon.b2b2c.business.dto.OrderSubmitDTO;
import com.slodon.b2b2c.business.dto.PreOrderDTO;
import com.slodon.b2b2c.business.example.OrderProductExample;
import com.slodon.b2b2c.business.pojo.OrderProduct;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.constant.PromotionConst;
import com.slodon.b2b2c.core.constant.RedisConst;
import com.slodon.b2b2c.core.constant.SpellConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.dao.read.promotion.*;
import com.slodon.b2b2c.dao.write.business.OrderProductWriteMapper;
import com.slodon.b2b2c.dao.write.promotion.SpellGoodsWriteMapper;
import com.slodon.b2b2c.dao.write.promotion.SpellTeamMemberWriteMapper;
import com.slodon.b2b2c.dao.write.promotion.SpellTeamWriteMapper;
import com.slodon.b2b2c.dao.write.promotion.SpellWriteMapper;
import com.slodon.b2b2c.goods.example.GoodsExample;
import com.slodon.b2b2c.goods.example.GoodsPromotionExample;
import com.slodon.b2b2c.goods.example.ProductExample;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.GoodsPromotion;
import com.slodon.b2b2c.goods.pojo.GoodsPromotionHistory;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.model.goods.GoodsModel;
import com.slodon.b2b2c.model.goods.GoodsPromotionHistoryModel;
import com.slodon.b2b2c.model.goods.GoodsPromotionModel;
import com.slodon.b2b2c.model.goods.ProductModel;
import com.slodon.b2b2c.model.member.MemberModel;
import com.slodon.b2b2c.model.promotion.base.PromotionBaseModel;
import com.slodon.b2b2c.promotion.example.SpellExample;
import com.slodon.b2b2c.promotion.example.SpellGoodsExample;
import com.slodon.b2b2c.promotion.example.SpellTeamMemberExample;
import com.slodon.b2b2c.promotion.pojo.*;
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
public class SpellModel extends PromotionBaseModel {

    @Resource
    private SpellReadMapper spellReadMapper;
    @Resource
    private SpellWriteMapper spellWriteMapper;
    @Resource
    private SpellLabelReadMapper spellLabelReadMapper;
    @Resource
    private SpellGoodsReadMapper spellGoodsReadMapper;
    @Resource
    private SpellGoodsWriteMapper spellGoodsWriteMapper;
    @Resource
    private SpellTeamReadMapper spellTeamReadMapper;
    @Resource
    private SpellTeamWriteMapper spellTeamWriteMapper;
    @Resource
    private SpellTeamMemberWriteMapper spellTeamMemberWriteMapper;
    @Resource
    private SpellTeamMemberReadMapper spellTeamMemberReadMapper;

    @Resource
    private GoodsModel goodsModel;
    @Resource
    private ProductModel productModel;
    @Resource
    private GoodsPromotionModel goodsPromotionModel;
    @Resource
    private GoodsPromotionHistoryModel goodsPromotionHistoryModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private OrderProductWriteMapper orderProductWriteMapper;
    @Resource
    private MemberModel memberModel;

    /**
     * 新增拼团活动表，店铺创建活动，然后绑定活动商品
     *
     * @param spell
     * @return
     */
    public Integer saveSpell(Spell spell) {
        int count = spellWriteMapper.insert(spell);
        if (count == 0) {
            throw new MallException("添加拼团活动表，店铺创建活动，然后绑定活动商品失败，请重试");
        }
        return count;
    }

    /**
     * 新建拼团活动
     *
     * @param spell
     * @param spellGoodsList
     * @return
     */
    @Transactional
    public Integer saveSpell(Spell spell, List<SpellGoods> spellGoodsList) {
        //查询标签信息
        SpellLabel spellLabel = spellLabelReadMapper.getByPrimaryKey(spell.getSpellLabelId());
        AssertUtil.notNull(spellLabel, "获取拼团标签信息为空，请重试");

        spell.setSpellLabelName(spellLabel.getSpellLabelName());
        spell.setState(SpellConst.ACTIVITY_STATE_1);
        spell.setCreateTime(new Date());
        int count = spellWriteMapper.insert(spell);
        AssertUtil.isTrue(count == 0, "保存拼团活动失败，请重试");

        //拼接商品id
        StringBuilder productIds = new StringBuilder();
        StringBuilder goodsIds = new StringBuilder();
        for (SpellGoods goodsVO : spellGoodsList) {
            SpellGoods spellGoods = new SpellGoods();
            BeanUtils.copyProperties(goodsVO, spellGoods);

            //查询货品信息
            Product product = productModel.getProductByProductId(goodsVO.getProductId());
            AssertUtil.notNull(product, "获取货品信息为空，请重试");

            //查询商品信息
            Goods goods = goodsModel.getGoodsByGoodsId(product.getGoodsId());
            AssertUtil.notNull(goods, "获取商品信息为空，请重试");
            AssertUtil.isTrue(goods.getIsLock() == GoodsConst.IS_LOCK_YES, "商品名称为：" + goods.getGoodsName() + "的商品已锁定");

            spellGoods.setSpellId(spell.getSpellId());
            spellGoods.setGoodsId(product.getGoodsId());
            spellGoods.setGoodsName(goods.getGoodsName());
            spellGoods.setGoodsImage(goods.getMainImage());
            spellGoods.setSpecValues(product.getSpecValues());
            spellGoods.setProductPrice(product.getProductPrice());
            count = spellGoodsWriteMapper.insert(spellGoods);
            AssertUtil.isTrue(count == 0, "保存拼团商品失败，请重试");

            productIds.append(",").append(product.getProductId());
            goodsIds.append(",").append(product.getGoodsId());
        }
        //参与拼团时锁定商品状态
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
        return count;
    }

    /**
     * 根据spellId删除拼团活动表，店铺创建活动，然后绑定活动商品
     *
     * @param spellId spellId
     * @return
     */
    @Transactional
    public Integer deleteSpell(Integer spellId) {
        // 查询拼团活动信息
        Spell spellDB = spellReadMapper.getByPrimaryKey(spellId);
        AssertUtil.notNull(spellDB, "获取拼团活动信息为空，请重试。");

        Date date = new Date();
        AssertUtil.isTrue(spellDB.getState() == SpellConst.ACTIVITY_STATE_2
                        && (date.after(spellDB.getStartTime()) && date.before(spellDB.getEndTime())),
                "正在进行中的的拼团活动不能删除");

        int count = 0;
        //查询拼团商品列表
        SpellGoodsExample example = new SpellGoodsExample();
        example.setSpellId(spellId);
        List<SpellGoods> goodsList = spellGoodsReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(goodsList)) {
            //拼接商品id
            StringBuilder productIds = new StringBuilder();
            StringBuilder goodsIds = new StringBuilder();
            for (SpellGoods spellGoods : goodsList) {
                productIds.append(",").append(spellGoods.getProductId());
                goodsIds.append(",").append(spellGoods.getGoodsId());
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
        if (spellDB.getState() == SpellConst.ACTIVITY_STATE_1) {
            count = spellWriteMapper.deleteByPrimaryKey(spellId);
            if (count == 0) {
                throw new MallException("删除拼团活动失败，请重试");
            }
            spellGoodsWriteMapper.deleteByExample(example);
        } else {
            Spell spell = new Spell();
            spell.setSpellId(spellId);
            spell.setState(SpellConst.ACTIVITY_STATE_4);
            spell.setUpdateTime(new Date());
            count = spellWriteMapper.updateByPrimaryKeySelective(spell);
            if (count == 0) {
                throw new MallException("删除失败，请重试！");
            }
        }
        return count;
    }

    /**
     * 根据spellId更新拼团活动表，店铺创建活动，然后绑定活动商品
     *
     * @param spell
     * @return
     */
    @Transactional
    public Integer updateSpell(Spell spell) {
        // 查询该拼团活动
        Spell spellDB = spellReadMapper.getByPrimaryKey(spell.getSpellId());
        AssertUtil.notNull(spellDB, "获取拼团活动信息为空，请重试");

        spell.setUpdateTime(new Date());
        int count = spellWriteMapper.updateByPrimaryKeySelective(spell);
        if (count == 0) {
            log.error("根据spellId：" + spell.getSpellId() + "更新拼团活动表，店铺创建活动，然后绑定活动商品失败");
            throw new MallException("更新拼团活动表，店铺创建活动，然后绑定活动商品失败,请重试");
        }
        //发布时将拼团商品活动数据记录到商品活动表
        if (!StringUtil.isNullOrZero(spell.getState()) && spell.getState() == SpellConst.ACTIVITY_STATE_2) {
            SpellGoodsExample example = new SpellGoodsExample();
            example.setSpellId(spell.getSpellId());
            List<SpellGoods> goodsList = spellGoodsReadMapper.listByExample(example);
            AssertUtil.notEmpty(goodsList, "获取货品信息为空，请重试");

            for (SpellGoods spellGoods : goodsList) {
                GoodsPromotion promotion = new GoodsPromotion();
                promotion.setPromotionId(spell.getSpellId());
                promotion.setStoreId(spellDB.getStoreId());
                promotion.setStoreName(spellDB.getStoreName());
                promotion.setBindTime(new Date());
                promotion.setGoodsId(spellGoods.getGoodsId());
                promotion.setProductId(spellGoods.getProductId());
                promotion.setPromotionGrade(PromotionConst.PROMOTION_GRADE_1);
                promotion.setPromotionType(PromotionConst.PROMOTION_TYPE_102);
                promotion.setStartTime(spellDB.getStartTime());
                promotion.setEndTime(spellDB.getEndTime());
                promotion.setDescription("拼团商品活动");
                promotion.setIsEffective(PromotionConst.IS_EFFECTIVE_YES);
                promotion.setBindType(PromotionConst.BIND_TYPE_1);
                count = goodsPromotionModel.saveGoodsPromotion(promotion);
                AssertUtil.isTrue(count == 0, "记录商品活动信息失败，请重试");
            }
        }
        //失效时将拼团商品活动数据记录到商品活动历史表，并删除商品活动表的数据
        if (!StringUtil.isNullOrZero(spell.getState()) && spell.getState() == SpellConst.ACTIVITY_STATE_3) {
            GoodsPromotionExample example = new GoodsPromotionExample();
            example.setPromotionId(spell.getSpellId());
            example.setPromotionType(PromotionConst.PROMOTION_TYPE_102);
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

                    Set<String> keys = stringRedisTemplate.keys(RedisConst.SPELL_PURCHASED_NUM_PREFIX + promotion.getGoodsId() + "*");
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
     * 编辑拼团活动
     *
     * @param spell
     * @param spellGoodsList
     * @return
     */
    @Transactional
    public Integer updateSpell(Spell spell, List<SpellGoods> spellGoodsList) {
        // 查询该拼团活动
        Spell spellDB = spellReadMapper.getByPrimaryKey(spell.getSpellId());
        AssertUtil.notNull(spellDB, "获取拼团活动信息为空，请重试");
        AssertUtil.isTrue(spellDB.getState() != SpellConst.ACTIVITY_STATE_1, "非法操作！只有刚创建的拼团活动信息允许编辑，请重试");
        if (!spell.getSpellLabelId().equals(spellDB.getSpellLabelId())) {
            //查询标签信息
            SpellLabel spellLabel = spellLabelReadMapper.getByPrimaryKey(spell.getSpellLabelId());
            AssertUtil.notNull(spellLabel, "获取拼团标签信息为空，请重试");

            spell.setSpellLabelName(spellLabel.getSpellLabelName());
        }
        spell.setUpdateTime(new Date());
        int count = spellWriteMapper.updateByPrimaryKeySelective(spell);
        AssertUtil.isTrue(count == 0, "编辑拼团活动失败,请重试");

        if (!CollectionUtils.isEmpty(spellGoodsList)) {
            //删除旧的拼团商品信息
            SpellGoodsExample example = new SpellGoodsExample();
            example.setSpellId(spell.getSpellId());
            spellGoodsWriteMapper.deleteByExample(example);

            //拼接商品id
            StringBuilder productIds = new StringBuilder();
            StringBuilder goodsIds = new StringBuilder();
            for (SpellGoods goodsVO : spellGoodsList) {
                SpellGoods spellGoods = new SpellGoods();
                BeanUtils.copyProperties(goodsVO, spellGoods);

                //查询货品信息
                Product product = productModel.getProductByProductId(goodsVO.getProductId());
                AssertUtil.notNull(product, "获取货品信息为空，请重试");

                //查询商品信息
                Goods goods = goodsModel.getGoodsByGoodsId(product.getGoodsId());
                AssertUtil.notNull(goods, "获取商品信息为空，请重试");

                spellGoods.setSpellId(spell.getSpellId());
                spellGoods.setGoodsId(product.getGoodsId());
                spellGoods.setGoodsName(goods.getGoodsName());
                spellGoods.setGoodsImage(goods.getMainImage());
                spellGoods.setSpecValues(product.getSpecValues());
                spellGoods.setProductPrice(product.getProductPrice());
                count = spellGoodsWriteMapper.insert(spellGoods);
                AssertUtil.isTrue(count == 0, "编辑拼团商品失败,请重试");

                productIds.append(",").append(product.getProductId());
                goodsIds.append(",").append(product.getGoodsId());
            }
            //参与拼团时锁定商品状态
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
     * 根据spellId获取拼团活动表，店铺创建活动，然后绑定活动商品详情
     *
     * @param spellId spellId
     * @return
     */
    public Spell getSpellBySpellId(Integer spellId) {
        return spellReadMapper.getByPrimaryKey(spellId);
    }

    /**
     * 根据条件获取拼团活动表，店铺创建活动，然后绑定活动商品列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<Spell> getSpellList(SpellExample example, PagerInfo pager) {
        List<Spell> spellList;
        if (pager != null) {
            pager.setRowsCount(spellReadMapper.countByExample(example));
            spellList = spellReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            spellList = spellReadMapper.listByExample(example);
        }
        return spellList;
    }

    /**
     * 复制拼团
     *
     * @param spell
     * @return
     */
    public Integer copySpell(Spell spell) {
        // 查询拼团活动信息
        Spell spellDB = spellReadMapper.getByPrimaryKey(spell.getSpellId());
        AssertUtil.notNull(spellDB, "该拼团活动不存在，请重新选择");
        AssertUtil.isTrue(!spellDB.getStoreId().equals(spell.getStoreId()), "非法操作!");

        Spell spellNew = new Spell();
        BeanUtils.copyProperties(spellDB, spellNew);
        spellNew.setSpellName("copy-" + spellDB.getSpellName());
        spellNew.setState(SpellConst.ACTIVITY_STATE_1);
        spellNew.setCreateTime(new Date());
        int count = spellWriteMapper.insert(spellNew);
        AssertUtil.isTrue(count == 0, "复制拼团活动失败，请重试");

        // 查询拼团活动下的商品
        SpellGoodsExample example = new SpellGoodsExample();
        example.setSpellId(spell.getSpellId());
        List<SpellGoods> spellGoodsList = spellGoodsReadMapper.listByExample(example);
        AssertUtil.notEmpty(spellGoodsList, "查询该拼团活动下的商品信息为空，请重试");

        for (SpellGoods spellGoods : spellGoodsList) {
            SpellGoods spellGoodsNew = new SpellGoods();
            BeanUtils.copyProperties(spellGoods, spellGoodsNew);
            spellGoodsNew.setSpellId(spellNew.getSpellId());
            count = spellGoodsWriteMapper.insert(spellGoodsNew);
            AssertUtil.isTrue(count == 0, "复制拼团商品失败，请重试");
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
        return "拼团";
    }

    /**
     * 活动价格
     *
     * @param promotionId
     * @param productId
     * @return
     */
    public BigDecimal promotionPrice(Integer promotionId, Long productId) {
        //拼团活动是否可用
        if (!isPromotionAvailable(promotionId.toString())) {
            return BigDecimal.ZERO;
        }
        SpellGoodsExample example = new SpellGoodsExample();
        example.setSpellId(promotionId);
        example.setProductId(productId);
        List<SpellGoods> list = spellGoodsReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0).getSpellPrice();
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
        //查询订单货品信息
        OrderProductExample productExample = new OrderProductExample();
        productExample.setOrderSn(orderSn);
        List<OrderProduct> orderProductList = orderProductWriteMapper.listByExample(productExample);
        AssertUtil.notEmpty(orderProductList, "获取拼团订单货品信息为空");
        //拼团订单货品只有一个
        OrderProduct orderProduct = orderProductList.get(0);
        //查询商品活动信息
        GoodsPromotionExample promotionExample = new GoodsPromotionExample();
        promotionExample.setProductId(orderProduct.getProductId());
        promotionExample.setPromotionType(PromotionConst.PROMOTION_TYPE_102);
        List<GoodsPromotion> goodsPromotionList = goodsPromotionModel.getGoodsPromotionList(promotionExample, null);
        AssertUtil.notEmpty(goodsPromotionList, "获取拼团活动信息为空");
        GoodsPromotion goodsPromotion = goodsPromotionList.get(0);
        Date date = new Date();
        AssertUtil.isTrue(date.after(goodsPromotion.getEndTime()), "拼团活动已结束，请重试");
        // 获取拼团商品信息
        SpellGoodsExample example = new SpellGoodsExample();
        example.setSpellId(goodsPromotion.getPromotionId());
        example.setProductId(orderProduct.getProductId());
        List<SpellGoods> spellGoodsList = spellGoodsReadMapper.listByExample(example);
        AssertUtil.notEmpty(spellGoodsList, "获取拼团商品信息为空，请重试！");
        SpellGoods spellGoods = spellGoodsList.get(0);

        //查询拼团活动信息
        Spell spell = spellReadMapper.getByPrimaryKey(goodsPromotion.getPromotionId());
        AssertUtil.notNull(spell, "获取拼团活动信息为空，请重试！");
        //查询会员信息
        Member member = memberModel.getMemberByMemberId(orderProduct.getMemberId());
        AssertUtil.notNull(member, "获取会员信息为空，请重试！");

        // 第一个创建团队
        if (StringUtil.isNullOrZero(orderProduct.getSpellTeamId())) {
            SpellTeam spellTeam = new SpellTeam();
            spellTeam.setSpellId(spell.getSpellId());
            spellTeam.setSpellName(spell.getSpellName());
            spellTeam.setStoreId(spell.getStoreId());
            spellTeam.setGoodsId(orderProduct.getGoodsId());
            spellTeam.setGoodsName(orderProduct.getGoodsName());
            spellTeam.setGoodsImage(orderProduct.getProductImage());
            spellTeam.setCreateTime(new Date());
            spellTeam.setEndTime(TimeUtil.getHourAgoDate(new Date(), spell.getCycle()));
            spellTeam.setLeaderMemberId(member.getMemberId());
            spellTeam.setLeaderMemberName(member.getMemberName());
            spellTeam.setRequiredNum(spell.getRequiredNum());
            spellTeam.setJoinedNum(0);
            spellTeam.setState(SpellConst.SPELL_GROUP_STATE_1);
            int count = spellTeamWriteMapper.insert(spellTeam);
            AssertUtil.isTrue(count == 0, "发起拼团失败，请重试");

            // 修改团队id
            OrderProduct productNew = new OrderProduct();
            productNew.setOrderProductId(orderProduct.getOrderProductId());
            productNew.setSpellTeamId(spellTeam.getSpellTeamId());
            count = orderProductWriteMapper.updateByPrimaryKeySelective(productNew);
            AssertUtil.isTrue(count == 0, "修改订单货品团队id信息失败，请重试");

            //保存团队成员信息
            SpellTeamMember teamMember = new SpellTeamMember();
            teamMember.setSpellTeamId(spellTeam.getSpellTeamId());
            teamMember.setSpellId(goodsPromotion.getPromotionId());
            teamMember.setSpellGoodsId(spellGoods.getSpellGoodsId());
            teamMember.setOrderSn(orderSn);
            teamMember.setProductId(orderProduct.getProductId());
            teamMember.setMemberId(member.getMemberId());
            teamMember.setMemberName(member.getMemberName());
            //默认头像
            if (StringUtil.isEmpty(member.getMemberAvatar())) {
                String value = stringRedisTemplate.opsForValue().get("default_image_user_portrait");
                teamMember.setMemberAvatar(StringUtils.isEmpty(member.getWxAvatarImg()) ? value : member.getWxAvatarImg());
            } else {
                teamMember.setMemberAvatar(member.getMemberAvatar());
            }
            teamMember.setIsLeader(SpellConst.IS_LEADER_YES);
            teamMember.setParticipateTime(new Date());
            count = spellTeamMemberWriteMapper.insert(teamMember);
            AssertUtil.isTrue(count == 0, "保存拼团团队成员信息失败，请重试");

        } else {
            //查询团队信息进行校验
            SpellTeam team = spellTeamReadMapper.getByPrimaryKey(orderProduct.getSpellTeamId());
            AssertUtil.notNull(team, "获取拼团团队信息为空，请重试");
            AssertUtil.isTrue(date.after(team.getEndTime()), "该团队已到截团时间，请重新选择");

            //加入参团信息
            SpellTeamMember teamMember = new SpellTeamMember();
            teamMember.setSpellTeamId(orderProduct.getSpellTeamId());
            teamMember.setSpellId(team.getSpellId());
            teamMember.setSpellGoodsId(spellGoods.getSpellGoodsId());
            teamMember.setOrderSn(orderSn);
            teamMember.setProductId(orderProduct.getProductId());
            teamMember.setMemberId(member.getMemberId());
            teamMember.setMemberName(member.getMemberName());
            //默认头像
            if (StringUtil.isEmpty(member.getMemberAvatar())) {
                String value = stringRedisTemplate.opsForValue().get("default_image_user_portrait");
                teamMember.setMemberAvatar(StringUtils.isEmpty(member.getWxAvatarImg()) ? value : member.getWxAvatarImg());
            } else {
                teamMember.setMemberAvatar(member.getMemberAvatar());
            }
            teamMember.setIsLeader(SpellConst.IS_LEADER_NO);
            teamMember.setParticipateTime(new Date());
            int count = spellTeamMemberWriteMapper.insert(teamMember);
            AssertUtil.isTrue(count == 0, "保存拼团团队成员信息失败，请重试");
        }
        //修改库存
        SpellGoods spellGoodsNew = new SpellGoods();
        spellGoodsNew.setSpellGoodsId(spellGoods.getSpellGoodsId());
        spellGoodsNew.setSalesVolume(spellGoods.getSalesVolume() + orderProduct.getProductNum());
        spellGoodsNew.setSpellStock(spellGoods.getSpellStock() - orderProduct.getProductNum());
        return spellGoodsWriteMapper.updateByPrimaryKeySelective(spellGoodsNew);
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
                //拼团活动是否可用
                if (!isPromotionAvailable(orderProductInfo.getPromotionId().toString())) {
                    throw new MallException("拼团活动不可用");
                }
                //构造活动信息
                OrderSubmitDTO.PromotionInfo promotionInfo = new OrderSubmitDTO.PromotionInfo();
                promotionInfo.setPromotionType(PromotionConst.PROMOTION_TYPE_102);
                promotionInfo.setPromotionId(orderProductInfo.getPromotionId().toString());
                promotionInfo.setPromotionName(this.promotionName());
                promotionInfo.setIsStore(true);
                //查询拼团活动信息
                Spell spell = this.getSpellBySpellId(orderProductInfo.getPromotionId());
                // 获取拼团商品信息
                SpellGoodsExample example = new SpellGoodsExample();
                example.setSpellId(orderProductInfo.getPromotionId());
                example.setGoodsId(orderProductInfo.getGoodsId());
                example.setProductId(orderProductInfo.getProductId());
                List<SpellGoods> spellGoodsList = spellGoodsReadMapper.listByExample(example);
                AssertUtil.notEmpty(spellGoodsList, "获取拼团商品信息为空，请重试！");
                SpellGoods spellGoods = spellGoodsList.get(0);

                // 优惠金额
                BigDecimal discount = BigDecimal.ZERO;
                // 拼团团队ID为空说明是拼团发起人，判断是否有团长优惠
                if (StringUtil.isNullOrZero(orderProductInfo.getSpellTeamId())) {
                    if (spell.getLeaderIsPromotion() == SpellConst.LEADER_IS_PROMOTION_1) {
                        discount = spellGoods.getLeaderPrice();
                    }
                }
                promotionInfo.setDiscount(discount);
                //订单货品添加活动
                orderProductInfo.getPromotionInfoList().add(promotionInfo);
                orderProductInfo.setProductPrice(spellGoods.getSpellPrice());
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
        //校验拼团开关是否开启，开启则校验活动状态
        if ("1".equals(stringRedisTemplate.opsForValue().get("spell_is_enable"))) {
            Spell spellDB = spellReadMapper.getByPrimaryKey(promotionId);
            Date date = new Date();
            if (spellDB.getState() == SpellConst.ACTIVITY_STATE_2
                    && (date.after(spellDB.getStartTime()) && date.before(spellDB.getEndTime()))) {
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
        AssertUtil.isTrue(!isPromotionAvailable(promotionId.toString()), "拼团活动不可用");
        Spell spell = this.getSpellBySpellId(promotionId);
        AssertUtil.notNull(spell, "拼团活动不存在");
        //查询拼团商品，校验库存
        SpellGoodsExample example = new SpellGoodsExample();
        example.setSpellId(promotionId);
        example.setProductId(productId);
        List<SpellGoods> goodsList = spellGoodsReadMapper.listByExample(example);
        AssertUtil.notEmpty(goodsList, "拼团商品不存在");
        SpellGoods spellGoods = goodsList.get(0);
        AssertUtil.isTrue(buyNum.compareTo(spellGoods.getSpellStock()) > 0, "库存不足");

        //拼团，先加购买数量
        String purchasedNum = stringRedisTemplate.opsForValue().get(RedisConst.SPELL_PURCHASED_NUM_PREFIX + spellGoods.getGoodsId() + "_" + memberId);
        if (!StringUtil.isEmpty(purchasedNum)) {
            //redis不为空，拼团购买数量累加
            stringRedisTemplate.opsForValue().increment(RedisConst.SPELL_PURCHASED_NUM_PREFIX + spellGoods.getGoodsId() + "_" + memberId, buyNum);
        } else {
            //设置过期时间（毫秒）
            long time1 = spell.getEndTime().getTime();
            long time2 = new Date().getTime();
            long remainTime = time1 - time2;
            stringRedisTemplate.opsForValue().set(RedisConst.SPELL_PURCHASED_NUM_PREFIX + spellGoods.getGoodsId() + "_" + memberId,
                    buyNum.toString(), remainTime < 0 ? 0 : remainTime, TimeUnit.MILLISECONDS);
        }

        PreOrderDTO dto = new PreOrderDTO();
        dto.setIsCalculateDiscount(true);
        dto.setOrderType(PromotionConst.PROMOTION_TYPE_102);
        dto.setGoodsId(spellGoods.getGoodsId());
        return dto;
    }

    /**
     * 失效活动
     *
     * @return
     */
    @Transactional
    public Integer invalidPromotion() {
        SpellExample example = new SpellExample();
        example.setState(SpellConst.ACTIVITY_STATE_2);
        Spell spell = new Spell();
        spell.setState(SpellConst.ACTIVITY_STATE_3);
        spell.setUpdateTime(new Date());
        int count = spellWriteMapper.updateByExampleSelective(spell, example);

        goodsPromotionModel.invalidGoodsPromotion(PromotionConst.PROMOTION_TYPE_102);
        return count;
    }

    /**
     * 活动订单支付成功特殊处理
     *
     * @param orderSn 订单号
     */
    @Override
    public void orderPaySuccess(String orderSn, String tradeSn, String paySn, String paymentName, String paymentCode) {
        //查询拼团成员信息
        SpellTeamMemberExample teamMemberExample = new SpellTeamMemberExample();
        teamMemberExample.setOrderSn(orderSn);
        List<SpellTeamMember> teamMemberList = spellTeamMemberReadMapper.listByExample(teamMemberExample);
        AssertUtil.notEmpty(teamMemberList, "获取拼团成员信息为空");
        SpellTeamMember teamMember = teamMemberList.get(0);
        //修改支付状态
        SpellTeamMember teamMemberNew = new SpellTeamMember();
        teamMemberNew.setSpellTeamMemberId(teamMember.getSpellTeamMemberId());
        teamMemberNew.setPayState(SpellConst.PAY_STATE_1);
        int count = spellTeamMemberWriteMapper.updateByPrimaryKeySelective(teamMemberNew);
        AssertUtil.isTrue(count == 0, "更新拼团活动团队成员表失败,请重试");

        //查询拼团活动信息
        Spell spell = spellReadMapper.getByPrimaryKey(teamMember.getSpellId());
        AssertUtil.notNull(spell, "获取拼团活动信息为空，请重试！");

        //查询团队信息进行校验
        SpellTeam team = spellTeamReadMapper.getByPrimaryKey(teamMember.getSpellTeamId());
        AssertUtil.notNull(team, "获取拼团团队信息为空，请重试");
//            AssertUtil.isTrue(team.getJoinedNum() >= spell.getRequiredNum(), "该团队人数已满，请重新选择");
        //修改团队信息
        SpellTeam teamNew = new SpellTeam();
        teamNew.setSpellTeamId(team.getSpellTeamId());
        teamNew.setJoinedNum(team.getJoinedNum() + 1);
        if (teamMember.getMemberId().equals(team.getLeaderMemberId())) {
            teamNew.setLeaderPayState(SpellConst.PAY_STATE_1);
        }
        Date date = new Date();
        if (teamNew.getJoinedNum() >= spell.getRequiredNum() && date.before(team.getEndTime())) {
            teamNew.setState(SpellConst.SPELL_GROUP_STATE_2);
            teamNew.setFinishType(SpellConst.FINISH_TYPE_1);
            teamNew.setFinishTime(date);
        }
        count = spellTeamWriteMapper.updateByPrimaryKeySelective(teamNew);
        AssertUtil.isTrue(count == 0, "更新拼团团队信息失败，请重试");
    }
}