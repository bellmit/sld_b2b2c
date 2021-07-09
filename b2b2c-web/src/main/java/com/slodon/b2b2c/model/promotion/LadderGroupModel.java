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
import com.slodon.b2b2c.dao.read.goods.GoodsReadMapper;
import com.slodon.b2b2c.dao.read.goods.ProductReadMapper;
import com.slodon.b2b2c.dao.read.promotion.LadderGroupGoodsReadMapper;
import com.slodon.b2b2c.dao.read.promotion.LadderGroupLabelReadMapper;
import com.slodon.b2b2c.dao.read.promotion.LadderGroupReadMapper;
import com.slodon.b2b2c.dao.read.promotion.LadderGroupRuleReadMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsPromotionHistoryWriteMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsWriteMapper;
import com.slodon.b2b2c.dao.write.goods.ProductWriteMapper;
import com.slodon.b2b2c.dao.write.promotion.LadderGroupGoodsWriteMapper;
import com.slodon.b2b2c.dao.write.promotion.LadderGroupRuleWriteMapper;
import com.slodon.b2b2c.dao.write.promotion.LadderGroupWriteMapper;
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
import com.slodon.b2b2c.model.goods.GoodsPromotionModel;
import com.slodon.b2b2c.model.promotion.base.PromotionBaseModel;
import com.slodon.b2b2c.promotion.dto.LadderGroupAddDTO;
import com.slodon.b2b2c.promotion.dto.LadderGroupUpdateDTO;
import com.slodon.b2b2c.promotion.example.LadderGroupExample;
import com.slodon.b2b2c.promotion.example.LadderGroupGoodsExample;
import com.slodon.b2b2c.promotion.example.LadderGroupOrderExtendExample;
import com.slodon.b2b2c.promotion.example.LadderGroupRuleExample;
import com.slodon.b2b2c.promotion.pojo.*;
import com.slodon.b2b2c.seller.pojo.Vendor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 阶梯团活动表model
 */
@Component
@Slf4j
public class LadderGroupModel extends PromotionBaseModel {

    @Resource
    private LadderGroupReadMapper ladderGroupReadMapper;
    @Resource
    private LadderGroupWriteMapper ladderGroupWriteMapper;
    @Resource
    private LadderGroupGoodsReadMapper ladderGroupGoodsReadMapper;
    @Resource
    private LadderGroupGoodsWriteMapper ladderGroupGoodsWriteMapper;
    @Resource
    private LadderGroupLabelReadMapper ladderGroupLabelReadMapper;
    @Resource
    private LadderGroupRuleReadMapper ladderGroupRuleReadMapper;
    @Resource
    private LadderGroupRuleWriteMapper ladderGroupRuleWriteMapper;
    @Resource
    private LadderGroupOrderExtendModel ladderGroupOrderExtendModel;
    @Resource
    private GoodsReadMapper goodsReadMapper;
    @Resource
    private GoodsWriteMapper goodsWriteMapper;
    @Resource
    private ProductReadMapper productReadMapper;
    @Resource
    private ProductWriteMapper productWriteMapper;
    @Resource
    private GoodsPromotionHistoryWriteMapper goodsPromotionHistoryWriteMapper;
    @Resource
    private GoodsPromotionModel goodsPromotionModel;
    @Resource
    private OrderModel orderModel;
    @Resource
    private OrderProductModel orderProductModel;
    @Resource
    private OrderPayModel orderPayModel;
    @Resource
    private OrderLogModel orderLogModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 新增阶梯团活动表
     *
     * @param ladderGroup
     * @return
     */
    public Integer saveLadderGroup(LadderGroup ladderGroup) {
        int count = ladderGroupWriteMapper.insert(ladderGroup);
        if (count == 0) {
            throw new MallException("添加阶梯团活动表失败，请重试");
        }
        return count;
    }

    /**
     * 新增阶梯团活动
     *
     * @param ladderGroupAddDTO
     * @param vendor
     * @return
     */
    @Transactional
    public Integer saveLadderGroup(LadderGroupAddDTO ladderGroupAddDTO, Vendor vendor) {
        LadderGroup ladderGroup = new LadderGroup();
        BeanUtils.copyProperties(ladderGroupAddDTO, ladderGroup);
        LadderGroupLabel ladderGroupLabel = ladderGroupLabelReadMapper.getByPrimaryKey(ladderGroupAddDTO.getLabelId());
        AssertUtil.notNull(ladderGroupLabel, "阶梯团标签不存在");
        ladderGroup.setLabelName(ladderGroupLabel.getLabelName());
        ladderGroup.setState(LadderGroupConst.STATE_1);
        ladderGroup.setStoreId(vendor.getStoreId());
        ladderGroup.setStoreName(vendor.getStore().getStoreName());
        ladderGroup.setCreateVendorId(vendor.getVendorId());
        ladderGroup.setCreateTime(new Date());
        //查询货品信息
        Product productDb = productReadMapper.getByPrimaryKey(ladderGroupAddDTO.getProductList().get(0).getProductId());
        AssertUtil.notNull(productDb, "货品不存在");
        ladderGroup.setGoodsId(productDb.getGoodsId());
        ladderGroup.setGoodsName(productDb.getGoodsName());
        this.saveLadderGroup(ladderGroup);
        //保存规则信息
        for (LadderGroupAddDTO.LadderGroupRuleInfo rule : ladderGroupAddDTO.getRuleList()) {
            AssertUtil.isTrue(StringUtil.isNullOrZero(rule.getJoinGroupNum()), "参团人数不能为空且必须大于0");
            LadderGroupRule groupRule = new LadderGroupRule();
            groupRule.setGroupId(ladderGroup.getGroupId());
            groupRule.setJoinGroupNum(rule.getJoinGroupNum());
            groupRule.setLadderLevel(rule.getLadderLevel());
            int count = ladderGroupRuleWriteMapper.insert(groupRule);
            AssertUtil.isTrue(count == 0, "添加阶梯团规则失败，请重试");
        }
        //查询商品信息
        Goods goods = goodsReadMapper.getByPrimaryKey(productDb.getGoodsId());
        AssertUtil.notNull(goods, "商品不存在");
        AssertUtil.isTrue(goods.getIsLock() == GoodsConst.IS_LOCK_YES, "商品名称为：" + goods.getGoodsName() + "的商品已锁定");

        int count = 0;
        //拼接商品id
        StringBuilder productIds = new StringBuilder();
        StringBuilder goodsIds = new StringBuilder();
        //保存阶梯团商品信息
        for (LadderGroupAddDTO.LadderGroupProduct ladderGroupProduct : ladderGroupAddDTO.getProductList()) {
            if (ladderGroupAddDTO.getDiscountType() == LadderGroupConst.DISCOUNT_TYPE_2) {
                AssertUtil.isTrue(StringUtil.isNullOrZero(ladderGroupProduct.getLadderPrice1()), "第一阶梯不能为空且必须大于0");
                AssertUtil.isTrue(ladderGroupProduct.getLadderPrice1().compareTo(BigDecimal.ZERO) < 0
                        || ladderGroupProduct.getLadderPrice1().compareTo(new BigDecimal(10)) > 0, "请填写0到10之间的数字");
                AssertUtil.isTrue(!StringUtil.isNullOrZero(ladderGroupProduct.getLadderPrice2())
                        && (ladderGroupProduct.getLadderPrice2().compareTo(BigDecimal.ZERO) < 0
                        || ladderGroupProduct.getLadderPrice2().compareTo(new BigDecimal(10)) > 0), "请填写0到10之间的数字");
                AssertUtil.isTrue(!StringUtil.isNullOrZero(ladderGroupProduct.getLadderPrice3())
                        && (ladderGroupProduct.getLadderPrice3().compareTo(BigDecimal.ZERO) < 0
                        || ladderGroupProduct.getLadderPrice3().compareTo(new BigDecimal(10)) > 0), "请填写0到10之间的数字");
                AssertUtil.isTrue(!StringUtil.isNullOrZero(ladderGroupProduct.getLadderPrice2())
                        && ladderGroupProduct.getLadderPrice2().compareTo(ladderGroupProduct.getLadderPrice1()) > 0, "第二阶梯不能比第一阶梯大");
                AssertUtil.isTrue(!StringUtil.isNullOrZero(ladderGroupProduct.getLadderPrice3())
                        && ladderGroupProduct.getLadderPrice3().compareTo(ladderGroupProduct.getLadderPrice2()) > 0, "第三阶梯不能比第二阶梯大");
            }
            //查询货品信息
            Product product = productReadMapper.getByPrimaryKey(ladderGroupProduct.getProductId());
            AssertUtil.notNull(product, "货品不存在");
            LadderGroupGoods groupGoods = new LadderGroupGoods();
            groupGoods.setGroupId(ladderGroup.getGroupId());
            groupGoods.setGoodsId(goods.getGoodsId());
            groupGoods.setGoodsName(goods.getGoodsName());
            groupGoods.setGoodsBrief(goods.getGoodsBrief());
            groupGoods.setGoodsImage(goods.getMainImage());
            groupGoods.setSpecValues(product.getSpecValues());
            groupGoods.setProductId(product.getProductId());
            groupGoods.setProductPrice(product.getProductPrice());
            groupGoods.setStock(product.getProductStock());
            groupGoods.setAdvanceDeposit(ladderGroupProduct.getAdvanceDeposit());
            groupGoods.setLadderPrice1(ladderGroupProduct.getLadderPrice1());
            groupGoods.setLadderPrice2(ladderGroupProduct.getLadderPrice2());
            groupGoods.setLadderPrice3(ladderGroupProduct.getLadderPrice3());
            count = ladderGroupGoodsWriteMapper.insert(groupGoods);
            AssertUtil.isTrue(count == 0, "添加阶梯团商品失败，请重试");

            productIds.append(",").append(product.getProductId());
            goodsIds.append(",").append(product.getGoodsId());
        }
        //参与阶梯团时锁定商品状态
        ProductExample example = new ProductExample();
        example.setProductIdIn(productIds.substring(1));
        Product updateProduct = new Product();
        updateProduct.setState(GoodsConst.PRODUCT_STATE_3);
        count = productWriteMapper.updateByExampleSelective(updateProduct, example);
        AssertUtil.isTrue(count == 0, "修改商品锁定状态失败，请重试");

        GoodsExample goodsExample = new GoodsExample();
        goodsExample.setGoodsIdIn(goodsIds.substring(1));
        Goods updateGoods = new Goods();
        updateGoods.setIsLock(GoodsConst.IS_LOCK_YES);
        count = goodsWriteMapper.updateByExampleSelective(updateGoods, goodsExample);
        AssertUtil.isTrue(count == 0, "修改商品锁定状态失败，请重试");
        return ladderGroup.getGroupId();
    }

    /**
     * 根据groupId删除阶梯团活动表
     *
     * @param groupId groupId
     * @return
     */
    public Integer deleteLadderGroup(Integer groupId) {
        if (StringUtils.isEmpty(groupId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = ladderGroupWriteMapper.deleteByPrimaryKey(groupId);
        if (count == 0) {
            log.error("根据groupId：" + groupId + "删除阶梯团活动表失败");
            throw new MallException("删除阶梯团活动表失败,请重试");
        }
        return count;
    }

    /**
     * 根据groupId删除阶梯团活动
     *
     * @param groupId groupId
     * @param groupId storeId
     * @return
     */
    @Transactional
    public Integer deleteLadderGroup(Integer groupId, Long storeId) {
        AssertUtil.notNullOrZero(groupId, "请选择要删除的数据");

        //查询阶梯团活动信息
        LadderGroup ladderGroupDb = this.getLadderGroupByGroupId(groupId);
        AssertUtil.notNull(ladderGroupDb, "获取阶梯团活动信息为空，请重试");
        if (!StringUtil.isNullOrZero(storeId)) {
            AssertUtil.isTrue(!ladderGroupDb.getStoreId().equals(storeId), "无权限");
        }

        Date date = new Date();
        AssertUtil.isTrue(ladderGroupDb.getState() == LadderGroupConst.STATE_2
                        && (date.after(ladderGroupDb.getStartTime()) && date.before(ladderGroupDb.getEndTime())),
                "正在进行中的的阶梯团活动不能删除");

        int count = 0;
        //查询阶梯团商品列表
        LadderGroupGoodsExample groupGoodsExample = new LadderGroupGoodsExample();
        groupGoodsExample.setGroupId(groupId);
        List<LadderGroupGoods> goodsList = ladderGroupGoodsReadMapper.listByExample(groupGoodsExample);
        if (!CollectionUtils.isEmpty(goodsList)) {
            //拼接商品id
            StringBuilder productIds = new StringBuilder();
            StringBuilder goodsIds = new StringBuilder();
            for (LadderGroupGoods groupGoods : goodsList) {
                productIds.append(",").append(groupGoods.getProductId());
                goodsIds.append(",").append(groupGoods.getGoodsId());
            }
            //解锁商品状态
            ProductExample example = new ProductExample();
            example.setProductIdIn(productIds.substring(1));
            Product updateProduct = new Product();
            updateProduct.setState(GoodsConst.PRODUCT_STATE_1);
            count = productWriteMapper.updateByExampleSelective(updateProduct, example);
            AssertUtil.isTrue(count == 0, "修改商品锁定状态失败，请重试");

            GoodsExample goodsExample = new GoodsExample();
            goodsExample.setGoodsIdIn(goodsIds.substring(1));
            Goods updateGoods = new Goods();
            updateGoods.setIsLock(GoodsConst.IS_LOCK_NO);
            count = goodsWriteMapper.updateByExampleSelective(updateGoods, goodsExample);
            AssertUtil.isTrue(count == 0, "修改商品锁定状态失败，请重试");
        }
        if (ladderGroupDb.getState() == LadderGroupConst.STATE_1) {
            //删除阶梯团活动信息
            this.deleteLadderGroup(groupId);
            //删除规则信息
            LadderGroupRuleExample example = new LadderGroupRuleExample();
            example.setGroupId(groupId);
            ladderGroupRuleWriteMapper.deleteByExample(example);
            //删除商品信息
            ladderGroupGoodsWriteMapper.deleteByExample(groupGoodsExample);
        } else {
            LadderGroup ladderGroup = new LadderGroup();
            ladderGroup.setGroupId(groupId);
            ladderGroup.setState(LadderGroupConst.STATE_4);
            count = ladderGroupWriteMapper.updateByPrimaryKeySelective(ladderGroup);
            AssertUtil.isTrue(count == 0, "删除阶梯团活动表失败,请重试");
        }
        return count;
    }

    /**
     * 根据groupId更新阶梯团活动表
     *
     * @param ladderGroup
     * @return
     */
    @Transactional
    public Integer updateLadderGroup(LadderGroup ladderGroup) {
        if (StringUtils.isEmpty(ladderGroup.getGroupId())) {
            throw new MallException("请选择要修改的数据");
        }
        //查询该阶梯团活动信息
        LadderGroup ladderGroupDb = this.getLadderGroupByGroupId(ladderGroup.getGroupId());
        AssertUtil.notNull(ladderGroupDb, "获取阶梯团活动信息为空，请重试");
        if (!StringUtil.isNullOrZero(ladderGroup.getStoreId())) {
            AssertUtil.isTrue(!ladderGroupDb.getStoreId().equals(ladderGroup.getStoreId()), "无权限");
        }
        ladderGroup.setUpdateTime(new Date());
        int count = ladderGroupWriteMapper.updateByPrimaryKeySelective(ladderGroup);
        if (count == 0) {
            log.error("根据groupId：" + ladderGroup.getGroupId() + "更新阶梯团活动表失败");
            throw new MallException("更新阶梯团活动表失败,请重试");
        }
        //发布时将阶梯团商品活动数据记录到商品活动表
        if (!StringUtil.isNullOrZero(ladderGroup.getState()) && ladderGroup.getState() == LadderGroupConst.STATE_2) {
            LadderGroupGoodsExample example = new LadderGroupGoodsExample();
            example.setGroupId(ladderGroup.getGroupId());
            List<LadderGroupGoods> goodsList = ladderGroupGoodsReadMapper.listByExample(example);
            AssertUtil.notEmpty(goodsList, "获取阶梯团商品信息为空，请重试");

            for (LadderGroupGoods groupGoods : goodsList) {
                GoodsPromotion promotion = new GoodsPromotion();
                promotion.setPromotionId(ladderGroupDb.getGroupId());
                promotion.setStoreId(ladderGroupDb.getStoreId());
                promotion.setStoreName(ladderGroupDb.getStoreName());
                promotion.setBindTime(new Date());
                promotion.setGoodsId(groupGoods.getGoodsId());
                promotion.setProductId(groupGoods.getProductId());
                promotion.setPromotionGrade(PromotionConst.PROMOTION_GRADE_1);
                promotion.setPromotionType(PromotionConst.PROMOTION_TYPE_105);
                promotion.setStartTime(ladderGroupDb.getStartTime());
                promotion.setEndTime(ladderGroupDb.getEndTime());
                promotion.setDescription("阶梯团商品活动");
                promotion.setIsEffective(PromotionConst.IS_EFFECTIVE_YES);
                promotion.setBindType(PromotionConst.BIND_TYPE_1);
                count = goodsPromotionModel.saveGoodsPromotion(promotion);
                AssertUtil.isTrue(count == 0, "记录商品活动信息失败，请重试");
            }
        }
        //失效时将阶梯团商品活动数据记录到商品活动历史表，并删除商品活动表的数据
        if (!StringUtil.isNullOrZero(ladderGroup.getState()) && ladderGroup.getState() == LadderGroupConst.STATE_3) {
            GoodsPromotionExample example = new GoodsPromotionExample();
            example.setPromotionId(ladderGroup.getGroupId());
            example.setPromotionType(PromotionConst.PROMOTION_TYPE_105);
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
                    count = goodsPromotionHistoryWriteMapper.insert(history);
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
                count = productWriteMapper.updateByExampleSelective(updateProduct, productExample);
                AssertUtil.isTrue(count == 0, "修改商品锁定状态失败，请重试");

                GoodsExample goodsExample = new GoodsExample();
                goodsExample.setGoodsIdIn(goodsIds.substring(1));
                Goods updateGoods = new Goods();
                updateGoods.setIsLock(GoodsConst.IS_LOCK_NO);
                count = goodsWriteMapper.updateByExampleSelective(updateGoods, goodsExample);
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
     * 根据groupId更新阶梯团活动
     *
     * @param ladderGroupUpdateDTO
     * @return
     */
    public Integer updateLadderGroup(LadderGroupUpdateDTO ladderGroupUpdateDTO) {
        if (StringUtils.isEmpty(ladderGroupUpdateDTO.getGroupId())) {
            throw new MallException("请选择要修改的数据");
        }
        LadderGroup ladderGroupDb = this.getLadderGroupByGroupId(ladderGroupUpdateDTO.getGroupId());
        AssertUtil.notNull(ladderGroupDb, "阶梯团活动不存在");
        LadderGroup ladderGroup = new LadderGroup();
        BeanUtils.copyProperties(ladderGroupUpdateDTO, ladderGroup);
        if (!StringUtil.isNullOrZero(ladderGroupUpdateDTO.getLabelId()) && !ladderGroupDb.getLabelId().equals(ladderGroupUpdateDTO.getLabelId())) {
            LadderGroupLabel ladderGroupLabel = ladderGroupLabelReadMapper.getByPrimaryKey(ladderGroupUpdateDTO.getLabelId());
            AssertUtil.notNull(ladderGroupLabel, "阶梯团标签不存在");
            ladderGroup.setLabelName(ladderGroupLabel.getLabelName());
        }
        //查询货品信息
        Product productDb = productReadMapper.getByPrimaryKey(ladderGroupUpdateDTO.getProductList().get(0).getProductId());
        AssertUtil.notNull(productDb, "货品不存在");
        ladderGroup.setGoodsId(productDb.getGoodsId());
        ladderGroup.setGoodsName(productDb.getGoodsName());
        ladderGroup.setUpdateTime(new Date());
        int count = ladderGroupWriteMapper.updateByPrimaryKeySelective(ladderGroup);
        AssertUtil.isTrue(count == 0, "更新阶梯团活动失败，请重试");
        //删除规则信息
        LadderGroupRuleExample ruleExample = new LadderGroupRuleExample();
        ruleExample.setGroupId(ladderGroupUpdateDTO.getGroupId());
        ladderGroupRuleWriteMapper.deleteByExample(ruleExample);
        //保存规则信息
        for (LadderGroupAddDTO.LadderGroupRuleInfo rule : ladderGroupUpdateDTO.getRuleList()) {
            LadderGroupRule groupRule = new LadderGroupRule();
            groupRule.setGroupId(ladderGroup.getGroupId());
            groupRule.setJoinGroupNum(rule.getJoinGroupNum());
            groupRule.setLadderLevel(rule.getLadderLevel());
            count = ladderGroupRuleWriteMapper.insert(groupRule);
            AssertUtil.isTrue(count == 0, "添加阶梯团规则失败，请重试");
        }
        //查询商品信息
        Goods goods = goodsReadMapper.getByPrimaryKey(productDb.getGoodsId());
        AssertUtil.notNull(goods, "商品不存在");
        //如果不是同一个商品，校验该商品是否被锁定
        if (!ladderGroupDb.getGoodsId().equals(goods.getGoodsId())) {
            AssertUtil.isTrue(goods.getIsLock() == GoodsConst.IS_LOCK_YES, "商品名称为：" + goods.getGoodsName() + "的商品已锁定");
        }
        //删除阶梯团商品信息
        LadderGroupGoodsExample groupGoodsExample = new LadderGroupGoodsExample();
        groupGoodsExample.setGroupId(ladderGroupUpdateDTO.getGroupId());
        ladderGroupGoodsWriteMapper.deleteByExample(groupGoodsExample);

        //拼接商品id
        StringBuilder productIds = new StringBuilder();
        StringBuilder goodsIds = new StringBuilder();
        //保存阶梯团商品信息
        for (LadderGroupAddDTO.LadderGroupProduct ladderGroupProduct : ladderGroupUpdateDTO.getProductList()) {
            //查询货品信息
            Product product = productReadMapper.getByPrimaryKey(ladderGroupProduct.getProductId());
            AssertUtil.notNull(product, "货品不存在");
            LadderGroupGoods groupGoods = new LadderGroupGoods();
            groupGoods.setGroupId(ladderGroup.getGroupId());
            groupGoods.setGoodsId(goods.getGoodsId());
            groupGoods.setGoodsName(goods.getGoodsName());
            groupGoods.setGoodsBrief(goods.getGoodsBrief());
            groupGoods.setGoodsImage(goods.getMainImage());
            groupGoods.setSpecValues(product.getSpecValues());
            groupGoods.setProductId(product.getProductId());
            groupGoods.setProductPrice(product.getProductPrice());
            groupGoods.setStock(product.getProductStock());
            groupGoods.setAdvanceDeposit(ladderGroupProduct.getAdvanceDeposit());
            groupGoods.setLadderPrice1(ladderGroupProduct.getLadderPrice1());
            groupGoods.setLadderPrice2(ladderGroupProduct.getLadderPrice2());
            groupGoods.setLadderPrice3(ladderGroupProduct.getLadderPrice3());
            count = ladderGroupGoodsWriteMapper.insert(groupGoods);
            AssertUtil.isTrue(count == 0, "添加阶梯团商品失败，请重试");

            productIds.append(",").append(product.getProductId());
            goodsIds.append(",").append(product.getGoodsId());
        }
        //参与阶梯团时锁定商品状态
        ProductExample example = new ProductExample();
        example.setProductIdIn(productIds.substring(1));
        Product updateProduct = new Product();
        updateProduct.setState(GoodsConst.PRODUCT_STATE_3);
        count = productWriteMapper.updateByExampleSelective(updateProduct, example);
        AssertUtil.isTrue(count == 0, "修改商品锁定状态失败，请重试");

        GoodsExample goodsExample = new GoodsExample();
        goodsExample.setGoodsIdIn(goodsIds.substring(1));
        Goods updateGoods = new Goods();
        updateGoods.setIsLock(GoodsConst.IS_LOCK_YES);
        count = goodsWriteMapper.updateByExampleSelective(updateGoods, goodsExample);
        AssertUtil.isTrue(count == 0, "修改商品锁定状态失败，请重试");
        return count;
    }

    /**
     * 根据groupId获取阶梯团活动表详情
     *
     * @param groupId groupId
     * @return
     */
    public LadderGroup getLadderGroupByGroupId(Integer groupId) {
        return ladderGroupReadMapper.getByPrimaryKey(groupId);
    }

    /**
     * 根据条件获取阶梯团活动表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<LadderGroup> getLadderGroupList(LadderGroupExample example, PagerInfo pager) {
        List<LadderGroup> ladderGroupList;
        if (pager != null) {
            pager.setRowsCount(ladderGroupReadMapper.countByExample(example));
            ladderGroupList = ladderGroupReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            ladderGroupList = ladderGroupReadMapper.listByExample(example);
        }
        return ladderGroupList;
    }

    /**
     * 复制阶梯团
     *
     * @param ladderGroup
     * @return
     */
    public Integer copyLadderGroup(LadderGroup ladderGroup) {
        //查询阶梯团活动信息
        LadderGroup ladderGroupDb = ladderGroupReadMapper.getByPrimaryKey(ladderGroup.getGroupId());
        AssertUtil.notNull(ladderGroupDb, "该阶梯团活动不存在，请重新选择");
        AssertUtil.isTrue(!ladderGroupDb.getStoreId().equals(ladderGroup.getStoreId()), "非法操作!");

        LadderGroup ladderGroupNew = new LadderGroup();
        BeanUtils.copyProperties(ladderGroupDb, ladderGroupNew);
        ladderGroupNew.setGroupName("copy-" + ladderGroupDb.getGroupName());
        ladderGroupNew.setState(LadderGroupConst.STATE_1);
        ladderGroupNew.setCreateTime(new Date());
        ladderGroupNew.setExecuteState(LadderGroupConst.EXECUTE_STATE_0);
        int count = ladderGroupWriteMapper.insert(ladderGroupNew);
        AssertUtil.isTrue(count == 0, "复制阶梯团活动失败，请重试");

        //查询阶梯团活动规则
        LadderGroupRuleExample ruleExample = new LadderGroupRuleExample();
        ruleExample.setGroupId(ladderGroup.getGroupId());
        ruleExample.setOrderBy("rule_id asc");
        List<LadderGroupRule> ruleList = ladderGroupRuleReadMapper.listByExample(ruleExample);
        AssertUtil.notEmpty(ruleList, "查询该阶梯团活动规则信息为空，请重试");
        for (LadderGroupRule rule : ruleList) {
            LadderGroupRule groupRule = new LadderGroupRule();
            BeanUtils.copyProperties(rule, groupRule);
            groupRule.setGroupId(ladderGroupNew.getGroupId());
            count = ladderGroupRuleWriteMapper.insert(groupRule);
            AssertUtil.isTrue(count == 0, "复制阶梯团活动规则失败，请重试");
        }

        //查询阶梯团活动下的商品
        LadderGroupGoodsExample example = new LadderGroupGoodsExample();
        example.setGroupId(ladderGroup.getGroupId());
        example.setOrderBy("group_goods_id asc");
        List<LadderGroupGoods> groupGoodsList = ladderGroupGoodsReadMapper.listByExample(example);
        AssertUtil.notEmpty(groupGoodsList, "查询该阶梯团活动下的商品信息为空，请重试");
        for (LadderGroupGoods goods : groupGoodsList) {
            LadderGroupGoods groupGoods = new LadderGroupGoods();
            BeanUtils.copyProperties(goods, groupGoods);
            groupGoods.setGroupId(ladderGroupNew.getGroupId());
            count = ladderGroupGoodsWriteMapper.insert(groupGoods);
            AssertUtil.isTrue(count == 0, "复制阶梯团商品失败，请重试");
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
        return "阶梯团";
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
        LadderGroupGoodsExample example = new LadderGroupGoodsExample();
        example.setGroupId(promotionId);
        example.setProductId(productId);
        List<LadderGroupGoods> list = ladderGroupGoodsReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(list)) {
            return comparePromotionPrice(list.get(0));
        } else {
            return BigDecimal.ZERO;
        }
    }

    /**
     * 取最优价格
     *
     * @param ladderGroupGoods
     * @return
     */
    public BigDecimal comparePromotionPrice(LadderGroupGoods ladderGroupGoods) {
        LadderGroup ladderGroup = this.getLadderGroupByGroupId(ladderGroupGoods.getGroupId());
        if (ladderGroup == null) {
            return BigDecimal.ZERO;
        }
        if (ladderGroup.getDiscountType() == LadderGroupConst.DISCOUNT_TYPE_2) {
            ladderGroupGoods.setLadderPrice1(ladderGroupGoods.getProductPrice().multiply(ladderGroupGoods.getLadderPrice1().divide(new BigDecimal(10), 2, RoundingMode.HALF_UP)));
            if (!StringUtil.isNullOrZero(ladderGroupGoods.getLadderPrice2())) {
                ladderGroupGoods.setLadderPrice2(ladderGroupGoods.getProductPrice().multiply(ladderGroupGoods.getLadderPrice2().divide(new BigDecimal(10), 2, RoundingMode.HALF_UP)));
            }
            if (!StringUtil.isNullOrZero(ladderGroupGoods.getLadderPrice3())) {
                ladderGroupGoods.setLadderPrice3(ladderGroupGoods.getProductPrice().multiply(ladderGroupGoods.getLadderPrice3().divide(new BigDecimal(10), 2, RoundingMode.HALF_UP)));
            }
        }
        return !StringUtil.isNullOrZero(ladderGroupGoods.getLadderPrice3()) ? ladderGroupGoods.getLadderPrice3()
                : !StringUtil.isNullOrZero(ladderGroupGoods.getLadderPrice2()) ? ladderGroupGoods.getLadderPrice2() : ladderGroupGoods.getLadderPrice1();
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
        AssertUtil.notEmpty(orderProductList, "获取阶梯团订单货品信息为空");
        //订单货品只有一个
        OrderProduct orderProduct = orderProductList.get(0);
        //查询商品活动信息
        GoodsPromotionExample promotionExample = new GoodsPromotionExample();
        promotionExample.setProductId(orderProduct.getProductId());
        promotionExample.setPromotionType(PromotionConst.PROMOTION_TYPE_105);
        List<GoodsPromotion> goodsPromotionList = goodsPromotionModel.getGoodsPromotionList(promotionExample, null);
        AssertUtil.notEmpty(goodsPromotionList, "获取阶梯团商品信息为空");
        GoodsPromotion goodsPromotion = goodsPromotionList.get(0);
        Date date = new Date();
        AssertUtil.isTrue(date.after(goodsPromotion.getEndTime()), "阶梯团活动已结束，请重试");
        //保存阶梯团订单扩展表
        return ladderGroupOrderExtendModel.insertLadderGroupOrder(order.getOrderSn(), order.getPaySn(), order.getMemberId(),
                order.getMemberName(), goodsPromotion.getPromotionId(), orderProduct.getProductId(), orderProduct.getProductNum());
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
                //阶梯团活动是否可用
                if (!isPromotionAvailable(orderProductInfo.getPromotionId().toString())) {
                    throw new MallException("阶梯团活动不可用");
                }
                //构造活动信息
                OrderSubmitDTO.PromotionInfo promotionInfo = new OrderSubmitDTO.PromotionInfo();
                promotionInfo.setPromotionType(PromotionConst.PROMOTION_TYPE_105);
                promotionInfo.setPromotionId(orderProductInfo.getPromotionId().toString());
                promotionInfo.setPromotionName(this.promotionName());
                promotionInfo.setIsStore(true);
                //查询阶梯团活动信息
                LadderGroup ladderGroup = this.getLadderGroupByGroupId(orderProductInfo.getPromotionId());
                //获取阶梯团商品信息
                LadderGroupGoodsExample example = new LadderGroupGoodsExample();
                example.setGroupId(orderProductInfo.getPromotionId());
                example.setGoodsId(orderProductInfo.getGoodsId());
                example.setProductId(orderProductInfo.getProductId());
                List<LadderGroupGoods> groupGoodsList = ladderGroupGoodsReadMapper.listByExample(example);
                AssertUtil.notEmpty(groupGoodsList, "获取阶梯团商品信息为空，请重试！");
                LadderGroupGoods groupGoods = groupGoodsList.get(0);

                //优惠金额
                BigDecimal discount = BigDecimal.ZERO;
                promotionInfo.setDiscount(discount);
                //订单货品添加活动
                orderProductInfo.getPromotionInfoList().add(promotionInfo);
                orderProductInfo.setProductPrice(groupGoods.getAdvanceDeposit());
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
        LadderGroup ladderGroup = ladderGroupReadMapper.getByPrimaryKey(promotionId);
        Date date = new Date();
        if (ladderGroup.getState() == LadderGroupConst.STATE_2
                && (date.after(ladderGroup.getStartTime()) && date.before(ladderGroup.getEndTime()))) {
            return true;
        } else {
            return false;
        }
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
        AssertUtil.isTrue(!isPromotionAvailable(promotionId.toString()), "阶梯团活动不可用");
        LadderGroup ladderGroupDb = this.getLadderGroupByGroupId(promotionId);
        AssertUtil.notNull(ladderGroupDb, "阶梯团活动不存在");
        AssertUtil.isTrue(ladderGroupDb.getBuyLimitNum() > 0 && buyNum.compareTo(ladderGroupDb.getBuyLimitNum()) > 0, "本单商品已达限购数量");
        //查询商品，校验库存
        Product product = productReadMapper.getByPrimaryKey(productId);
        AssertUtil.notNull(product, "货品不存在");
        AssertUtil.isTrue(buyNum.compareTo(product.getProductStock()) > 0, "库存不足");

        //阶梯团，先加购买数量
        String purchasedNum = stringRedisTemplate.opsForValue().get(RedisConst.LADDER_GROUP_PURCHASED_NUM_PREFIX + product.getGoodsId() + "_" + memberId);
        if (!StringUtil.isEmpty(purchasedNum)) {
            //redis不为空，阶梯团购买数量累加
            stringRedisTemplate.opsForValue().increment(RedisConst.LADDER_GROUP_PURCHASED_NUM_PREFIX + product.getGoodsId() + "_" + memberId, buyNum);
        } else {
            //设置过期时间（毫秒）
            long time1 = ladderGroupDb.getEndTime().getTime();
            long time2 = new Date().getTime();
            long remainTime = time1 - time2;
            stringRedisTemplate.opsForValue().set(RedisConst.LADDER_GROUP_PURCHASED_NUM_PREFIX + product.getGoodsId() + "_" + memberId,
                    buyNum.toString(), remainTime < 0 ? 0 : remainTime, TimeUnit.MILLISECONDS);
        }
        PreOrderDTO dto = new PreOrderDTO();
        dto.setIsCalculateDiscount(false);
        dto.setOrderType(PromotionConst.PROMOTION_TYPE_105);
        dto.setGoodsId(product.getGoodsId());
        return dto;
    }

    /**
     * 失效活动
     *
     * @return
     */
    @Transactional
    public Integer invalidPromotion() {
        LadderGroupExample example = new LadderGroupExample();
        example.setState(LadderGroupConst.STATE_2);
        LadderGroup ladderGroup = new LadderGroup();
        ladderGroup.setState(LadderGroupConst.STATE_3);
        ladderGroup.setUpdateTime(new Date());
        int count = ladderGroupWriteMapper.updateByExampleSelective(ladderGroup, example);

        goodsPromotionModel.invalidGoodsPromotion(PromotionConst.PROMOTION_TYPE_105);
        return count;
    }

    /**
     * 活动订单支付成功特殊处理
     *
     * @param orderSn 订单号
     */
    @Transactional
    public void orderPaySuccess(String orderSn, String tradeSn, String paySn, String paymentName, String paymentCode) {
        //查询阶梯团订单扩展信息
        LadderGroupOrderExtendExample extendExample = new LadderGroupOrderExtendExample();
        extendExample.setOrderSn(orderSn);
        List<LadderGroupOrderExtend> orderExtendList = ladderGroupOrderExtendModel.getLadderGroupOrderExtendList(extendExample, null);
        AssertUtil.notEmpty(orderExtendList, "获取阶梯团订单扩展信息为空");
        LadderGroupOrderExtend orderExtend = orderExtendList.get(0);
        LadderGroupOrderExtend orderExtendNew = new LadderGroupOrderExtend();
        //更新阶梯团订单扩展表
        orderExtendNew.setExtendId(orderExtend.getExtendId());
        if (orderExtend.getOrderSubState() == LadderGroupConst.ORDER_SUB_STATE_2) {
            Date date = new Date();
            AssertUtil.isTrue(date.before(orderExtend.getRemainStartTime()), "未到尾款支付时间");
            AssertUtil.notEmpty(orderExtend.getRemainPaySn(), "该订单尚未生成尾款信息，请稍后再试");

            orderExtendNew.setOrderSubState(LadderGroupConst.ORDER_SUB_STATE_3);
            orderExtendNew.setSuccessTime(new Date());
            ladderGroupOrderExtendModel.updateLadderGroupOrderExtend(orderExtendNew);

            //尾款支付订单更新支付状态
            Order orderNew = new Order();
            orderNew.setOrderSn(orderSn);
            orderNew.setOrderState(OrderConst.ORDER_STATE_20);
            orderNew.setPayTime(new Date());
            orderModel.updateOrder(orderNew);

            //记录订单日志
            this.insertOrderLog(OrderConst.LOG_ROLE_MEMBER, Long.valueOf(orderExtend.getMemberId()), orderExtend.getMemberName(), orderSn, OrderConst.ORDER_STATE_20, "阶梯团订单支付完成");

            //该支付单号下没有待支付的订单，修改支付表支付状态
            OrderPay orderPay = new OrderPay();
            orderPay.setPaySn(paySn);
            orderPay.setApiPayState(OrderConst.API_PAY_STATE_1);
            orderPay.setCallbackTime(new Date());
            orderPay.setTradeSn(tradeSn);
            orderPay.setPaymentName(paymentName);
            orderPay.setPaymentCode(paymentCode);
            orderPayModel.updateOrderPay(orderPay);
        } else {
            orderExtendNew.setOrderSubState(LadderGroupConst.ORDER_SUB_STATE_2);
            ladderGroupOrderExtendModel.updateLadderGroupOrderExtend(orderExtendNew);

            //修改订单货品展示价格
            OrderProductExample orderProductExample = new OrderProductExample();
            orderProductExample.setOrderSn(orderExtend.getOrderSn());
            orderProductExample.setProductId(orderExtend.getProductId());
            List<OrderProduct> orderProductList = orderProductModel.getOrderProductList(orderProductExample, null);
            AssertUtil.notEmpty(orderProductList, "订单货品不存在");

            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrderProductId(orderProductList.get(0).getOrderProductId());
            orderProduct.setProductShowPrice(orderExtend.getProductPrice());
            orderProductModel.updateOrderProduct(orderProduct);

            //记录订单日志
            this.insertOrderLog(OrderConst.LOG_ROLE_MEMBER, Long.valueOf(orderExtend.getMemberId()), orderExtend.getMemberName(), orderSn, LadderGroupConst.ORDER_SUB_STATE_2, "阶梯团定金支付完成");

            //该支付单号下没有待支付的订单，修改支付表支付状态
            OrderPay orderPay = new OrderPay();
            orderPay.setPaySn(paySn);
            orderPay.setApiPayState(OrderConst.API_PAY_STATE_1);
            orderPay.setCallbackTime(new Date());
            orderPay.setTradeSn(tradeSn);
            orderPay.setPaymentName(paymentName);
            orderPay.setPaymentCode(paymentCode);
            orderPayModel.updateOrderPay(orderPay);

            //增加销量
            LadderGroupGoodsExample groupGoodsExample = new LadderGroupGoodsExample();
            groupGoodsExample.setGroupId(orderExtend.getGroupId());
            groupGoodsExample.setProductId(orderExtend.getProductId());
            List<LadderGroupGoods> goodsList = ladderGroupGoodsReadMapper.listByExample(groupGoodsExample);
            AssertUtil.notEmpty(goodsList, "获取阶梯团商品信息为空，请重试");
            LadderGroupGoods groupGoodsDb = goodsList.get(0);
            LadderGroupGoods groupGoods = new LadderGroupGoods();
            groupGoods.setGroupGoodsId(groupGoodsDb.getGroupGoodsId());
            groupGoods.setSalesVolume(groupGoodsDb.getSalesVolume() + orderProductList.get(0).getProductNum());
            int count = ladderGroupGoodsWriteMapper.updateByPrimaryKeySelective(groupGoods);
            AssertUtil.isTrue(count == 0, "更新阶梯团商品表失败,请重试");
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

    /**
     * 定时修改阶梯团尾款信息
     *
     * @return
     */
    @Transactional
    public boolean jobUpdateLadderGroup() {
        //查询已结束并且未生成尾款信息的活动
        LadderGroupExample example = new LadderGroupExample();
        example.setState(LadderGroupConst.STATE_2);
        example.setEndTimeBefore(new Date());
        example.setExecuteState(LadderGroupConst.EXECUTE_STATE_0);
        List<LadderGroup> ladderGroupList = ladderGroupReadMapper.listByExample(example);
        //没有数据就结束执行
        if (CollectionUtils.isEmpty(ladderGroupList)) {
            return true;
        }
        for (LadderGroup ladderGroup : ladderGroupList) {
            //查询已支付定金的订单
            LadderGroupOrderExtendExample orderExtendExample = new LadderGroupOrderExtendExample();
            orderExtendExample.setGroupId(ladderGroup.getGroupId());
            orderExtendExample.setOrderSubState(LadderGroupConst.ORDER_SUB_STATE_2);
            List<LadderGroupOrderExtend> groupOrderExtendList = ladderGroupOrderExtendModel.getLadderGroupOrderExtendList(orderExtendExample, null);
            //没有订单就跳过该循环执行下一个
            if (CollectionUtils.isEmpty(groupOrderExtendList)) {
                continue;
            }
            //查询阶梯规则
            LadderGroupRuleExample ruleExample = new LadderGroupRuleExample();
            ruleExample.setGroupId(ladderGroup.getGroupId());
            List<LadderGroupRule> groupRuleList = ladderGroupRuleReadMapper.listByExample(ruleExample);
            //没有规则说明数据有问题，跳过
            if (CollectionUtils.isEmpty(groupRuleList)) {
                continue;
            }
            //比对是否满足规则要求处理尾款
            comparisonRule(groupOrderExtendList, groupRuleList);
        }
        //更新阶梯团活动已生成尾款状态
        LadderGroup ladderGroupNew = new LadderGroup();
        ladderGroupNew.setExecuteState(LadderGroupConst.EXECUTE_STATE_1);
        ladderGroupNew.setUpdateTime(new Date());
        ladderGroupWriteMapper.updateByExampleSelective(ladderGroupNew, example);
        return true;
    }

    /**
     * 比对是否满足规则要求处理尾款
     *
     * @param groupOrderExtendList
     * @param groupRuleList
     */
    private void comparisonRule(List<LadderGroupOrderExtend> groupOrderExtendList, List<LadderGroupRule> groupRuleList) {
        //满足阶梯等级,0代表一个规则也没有满足，则取原价减去预付定金
        Integer ladderLevel = 0;
        for (LadderGroupRule groupRule : groupRuleList) {
            if (groupOrderExtendList.size() >= groupRule.getJoinGroupNum()) {
                ladderLevel = groupRule.getLadderLevel();
                break;
            }
        }
        //将阶梯团商品信息放map,key为productId，value为阶梯团商品信息
        Map<Long, LadderGroupGoods> groupGoodsMap = new HashMap<>();
        //阶梯团商品
        LadderGroupGoods groupGoods = null;
        //处理尾款金额
        for (LadderGroupOrderExtend groupOrderExtend : groupOrderExtendList) {
            if (groupGoodsMap.containsKey(groupOrderExtend.getProductId())) {
                groupGoods = groupGoodsMap.get(groupOrderExtend.getProductId());
            } else {
                //查询购买的阶梯团商品信息
                LadderGroupGoodsExample groupGoodsExample = new LadderGroupGoodsExample();
                groupGoodsExample.setGroupId(groupOrderExtend.getGroupId());
                groupGoodsExample.setProductId(groupOrderExtend.getProductId());
                List<LadderGroupGoods> groupGoodsList = ladderGroupGoodsReadMapper.listByExample(groupGoodsExample);
                groupGoods = groupGoodsList.get(0);
                groupGoodsMap.put(groupOrderExtend.getProductId(), groupGoods);
            }
            //生成支付单号
            String paySn = GoodsIdGenerator.getPaySn();
            //更新阶梯团订单扩展表
            LadderGroupOrderExtend orderExtendNew = new LadderGroupOrderExtend();
            orderExtendNew.setExtendId(groupOrderExtend.getExtendId());
            //计算尾款金额
            if (ladderLevel == LadderGroupConst.LADDER_LEVEL_1) {
                orderExtendNew.setRemainAmount(groupGoods.getLadderPrice1().subtract(groupGoods.getAdvanceDeposit()));
            } else if (ladderLevel == LadderGroupConst.LADDER_LEVEL_2) {
                orderExtendNew.setRemainAmount(groupGoods.getLadderPrice2().subtract(groupGoods.getAdvanceDeposit()));
            } else if (ladderLevel == LadderGroupConst.LADDER_LEVEL_3) {
                orderExtendNew.setRemainAmount(groupGoods.getLadderPrice3().subtract(groupGoods.getAdvanceDeposit()));
            } else {
                orderExtendNew.setRemainAmount(groupGoods.getProductPrice().subtract(groupGoods.getAdvanceDeposit()));
            }
            orderExtendNew.setRemainPaySn(paySn);
            ladderGroupOrderExtendModel.updateLadderGroupOrderExtend(orderExtendNew);

            //查询订单信息
            Order order = orderModel.getOrderByOrderSn(groupOrderExtend.getOrderSn());
            //生成一条尾款支付信息（阶梯团尾款待处理）
            OrderPay orderPay = new OrderPay();
            orderPay.setPaySn(paySn);
            //保证定金支付单和尾款支付单的父订单号一致
            orderPay.setOrderSn(order.getParentSn());
            orderPay.setPayAmount(orderExtendNew.getRemainAmount().multiply(new BigDecimal(groupOrderExtend.getProductNum())));
            orderPay.setMemberId(order.getMemberId());
            orderPay.setPaymentName(OrderPaymentConst.PAYMENT_NAME_ONLINE);
            orderPay.setPaymentCode(OrderPaymentConst.PAYMENT_CODE_ONLINE);
            orderPayModel.saveOrderPay(orderPay);

            //尾款支付订单更新支付状态
            Order orderNew = new Order();
            orderNew.setOrderSn(groupOrderExtend.getOrderSn());
            orderNew.setPaySn(paySn);
            //定金支付成功修改订单金额为阶梯团价格
            BigDecimal orderAmount = groupOrderExtend.getAdvanceDeposit().add(orderExtendNew.getRemainAmount());
            orderNew.setGoodsAmount(orderAmount.multiply(new BigDecimal(groupOrderExtend.getProductNum())));
            orderNew.setOrderAmount(orderAmount.multiply(new BigDecimal(groupOrderExtend.getProductNum())).add(order.getExpressFee())); //加上运费
            orderModel.updateOrder(orderNew);

            //修改订单货品展示价格
            OrderProductExample orderProductExample = new OrderProductExample();
            orderProductExample.setOrderSn(groupOrderExtend.getOrderSn());
            orderProductExample.setProductId(groupOrderExtend.getProductId());
            List<OrderProduct> orderProductList = orderProductModel.getOrderProductList(orderProductExample, null);
            AssertUtil.notEmpty(orderProductList, "订单货品不存在");

            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrderProductId(orderProductList.get(0).getOrderProductId());
            orderProduct.setMoneyAmount(orderAmount.multiply(new BigDecimal(groupOrderExtend.getProductNum())));
            orderProductModel.updateOrderProduct(orderProduct);
        }
    }
}