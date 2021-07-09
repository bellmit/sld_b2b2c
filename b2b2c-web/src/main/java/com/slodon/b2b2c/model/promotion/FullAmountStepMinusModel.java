package com.slodon.b2b2c.model.promotion;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.slodon.b2b2c.business.dto.OrderConfirmDTO;
import com.slodon.b2b2c.business.dto.OrderSubmitDTO;
import com.slodon.b2b2c.business.pojo.Cart;
import com.slodon.b2b2c.core.constant.PromotionConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.dao.read.promotion.CouponReadMapper;
import com.slodon.b2b2c.dao.read.promotion.FullAmountStepMinusReadMapper;
import com.slodon.b2b2c.dao.read.promotion.FullAsmRuleReadMapper;
import com.slodon.b2b2c.dao.write.promotion.FullAmountStepMinusWriteMapper;
import com.slodon.b2b2c.dao.write.promotion.FullAsmRuleWriteMapper;
import com.slodon.b2b2c.goods.example.GoodsPromotionExample;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.GoodsPromotion;
import com.slodon.b2b2c.goods.pojo.GoodsPromotionHistory;
import com.slodon.b2b2c.model.goods.GoodsModel;
import com.slodon.b2b2c.model.goods.GoodsPromotionHistoryModel;
import com.slodon.b2b2c.model.goods.GoodsPromotionModel;
import com.slodon.b2b2c.model.promotion.base.PromotionBaseModel;
import com.slodon.b2b2c.promotion.example.FullAmountStepMinusExample;
import com.slodon.b2b2c.promotion.example.FullAsmRuleExample;
import com.slodon.b2b2c.promotion.pojo.Coupon;
import com.slodon.b2b2c.promotion.pojo.FullAmountStepMinus;
import com.slodon.b2b2c.promotion.pojo.FullAsmRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 阶梯满金额减活动model
 */
@Component
@Slf4j
public class FullAmountStepMinusModel extends PromotionBaseModel {

    @Resource
    private FullAmountStepMinusReadMapper fullAmountStepMinusReadMapper;
    @Resource
    private FullAmountStepMinusWriteMapper fullAmountStepMinusWriteMapper;
    @Resource
    private FullAsmRuleReadMapper fullAsmRuleReadMapper;
    @Resource
    private FullAsmRuleWriteMapper fullAsmRuleWriteMapper;
    @Resource
    private CouponReadMapper couponReadMapper;
    @Resource
    private GoodsPromotionModel goodsPromotionModel;
    @Resource
    private GoodsPromotionHistoryModel goodsPromotionHistoryModel;
    @Resource
    private GoodsModel goodsModel;

    /**
     * 新增阶梯满金额减活动表
     *
     * @param fullAmountStepMinus
     * @return
     */
    public Integer saveFullAmountStepMinus(FullAmountStepMinus fullAmountStepMinus) {
        int count = fullAmountStepMinusWriteMapper.insert(fullAmountStepMinus);
        if (count == 0) {
            throw new MallException("添加阶梯满金额减活动表失败，请重试");
        }
        return count;
    }

    /**
     * 新增阶梯满减活动
     *
     * @param fullAmountStepMinus
     * @param ruleJson
     * @return
     */
    @Transactional
    public Integer saveFullAmountStepMinus(FullAmountStepMinus fullAmountStepMinus, String ruleJson) {
        fullAmountStepMinus.setState(PromotionConst.STATE_CREATED_1);
        fullAmountStepMinus.setCreateTime(new Date());
        int count = fullAmountStepMinusWriteMapper.insert(fullAmountStepMinus);
        AssertUtil.isTrue(count == 0, "添加阶梯满减活动失败，请重试");

        //保存阶梯满减活动规则
        JSONArray jsonArray = JSONObject.parseArray(ruleJson);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            FullAsmRule rule = new FullAsmRule();
            rule.setFullId(fullAmountStepMinus.getFullId());
            rule.setFullValue(new BigDecimal(jsonObject.getString("fullValue")));
            rule.setMinusValue(new BigDecimal(jsonObject.getString("minusValue")));
            rule.setSendIntegral(StringUtil.isEmpty(jsonObject.getString("sendIntegral")) ? 0 : Integer.parseInt(jsonObject.getString("sendIntegral")));
            rule.setSendCouponIds(jsonObject.getString("sendCouponIds"));
            rule.setSendGoodsIds(jsonObject.getString("sendGoodsIds"));
            count = fullAsmRuleWriteMapper.insert(rule);
            AssertUtil.isTrue(count == 0, "保存阶梯满减活动规则失败，请重试");
        }
        return count;
    }

    /**
     * 根据fullId删除阶梯满金额减活动表
     *
     * @param fullId fullId
     * @return
     */
    public Integer deleteFullAmountStepMinus(Integer fullId) {
        if (StringUtils.isEmpty(fullId)) {
            throw new MallException("请选择要删除的数据");
        }
        FullAmountStepMinus fullDb = fullAmountStepMinusReadMapper.getByPrimaryKey(fullId);
        AssertUtil.notNull(fullDb, "获取阶梯满减信息为空，请重试。");
        Date date = new Date();
        AssertUtil.isTrue(fullDb.getState() == PromotionConst.STATE_RELEASE_2 && !date.after(fullDb.getEndTime()), "已经发布的阶梯满减活动不能删除");

        int count = 0;
        if (fullDb.getState() == PromotionConst.STATE_CREATED_1) {
            count = fullAmountStepMinusWriteMapper.deleteByPrimaryKey(fullId);
            if (count == 0) {
                throw new MallException("删除阶梯满减活动失败，请重试！");
            }
            //再删除阶梯满减活动规则
            FullAsmRuleExample example = new FullAsmRuleExample();
            example.setFullId(fullId);
            fullAsmRuleWriteMapper.deleteByExample(example);
        } else {
            FullAmountStepMinus full = new FullAmountStepMinus();
            full.setFullId(fullId);
            full.setState(PromotionConst.STATE_DELETED_6);
            count = fullAmountStepMinusWriteMapper.updateByPrimaryKeySelective(full);
            if (count == 0) {
                log.error("根据fullId：" + fullId + "删除阶梯满减活动表失败");
                throw new MallException("删除阶梯满减活动失败，请重试！");
            }
        }
        return count;
    }

    /**
     * 根据fullId更新阶梯满金额减活动表
     *
     * @param fullAmountStepMinus
     * @return
     */
    public Integer updateFullAmountStepMinus(FullAmountStepMinus fullAmountStepMinus) {
        if (StringUtils.isEmpty(fullAmountStepMinus.getFullId())) {
            throw new MallException("请选择要修改的数据");
        }
        fullAmountStepMinus.setUpdateTime(new Date());
        int count = fullAmountStepMinusWriteMapper.updateByPrimaryKeySelective(fullAmountStepMinus);
        if (count == 0) {
            log.error("根据fullId：" + fullAmountStepMinus.getFullId() + "更新阶梯满减活动表失败");
            throw new MallException("更新阶梯满减活动表失败,请重试");
        }
        //发布时将阶梯满减活动数据记录到商品活动表
        if (!StringUtil.isNullOrZero(fullAmountStepMinus.getState()) && fullAmountStepMinus.getState() == PromotionConst.STATE_RELEASE_2) {
            FullAmountStepMinus amountStepMinus = fullAmountStepMinusReadMapper.getByPrimaryKey(fullAmountStepMinus.getFullId());
            AssertUtil.notNull(amountStepMinus, "获取阶梯满减活动信息为空，请重试");

            GoodsPromotion promotion = new GoodsPromotion();
            promotion.setPromotionId(fullAmountStepMinus.getFullId());
            promotion.setStoreId(amountStepMinus.getStoreId());
            promotion.setStoreName(amountStepMinus.getStoreName());
            promotion.setBindTime(new Date());
            promotion.setPromotionGrade(PromotionConst.PROMOTION_GRADE_2);
            promotion.setPromotionType(PromotionConst.PROMOTION_TYPE_201);
            promotion.setStartTime(amountStepMinus.getStartTime());
            promotion.setEndTime(amountStepMinus.getEndTime());
            //阶梯满减活动
            String description = this.getPromotionDescription(fullAmountStepMinus.getFullId());
            promotion.setDescription(description);
            promotion.setIsEffective(PromotionConst.IS_EFFECTIVE_YES);
            promotion.setBindType(PromotionConst.BIND_TYPE_2);
            count = goodsPromotionModel.saveGoodsPromotion(promotion);
            AssertUtil.isTrue(count == 0, "记录阶梯满减活动信息失败，请重试");
        }
        //失效时将阶梯满减活动数据记录到商品活动历史表，并删除商品活动表的数据
        if (!StringUtil.isNullOrZero(fullAmountStepMinus.getState()) && fullAmountStepMinus.getState() == PromotionConst.STATE_EXPIRED_5) {
            GoodsPromotionExample example = new GoodsPromotionExample();
            example.setPromotionId(fullAmountStepMinus.getFullId());
            example.setPromotionType(PromotionConst.PROMOTION_TYPE_201);
            List<GoodsPromotion> promotionList = goodsPromotionModel.getGoodsPromotionList(example, null);
            if (!CollectionUtils.isEmpty(promotionList)) {
                GoodsPromotion promotion = promotionList.get(0);
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
                AssertUtil.isTrue(count == 0, "记录阶梯满减活动绑定历史信息失败，请重试");

                //删除商品活动表数据
                count = goodsPromotionModel.deleteGoodsPromotion(promotion.getGoodsPromotionId());
                AssertUtil.isTrue(count == 0, "删除阶梯满减活动信息失败，请重试");
            }
        }
        return count;
    }

    /**
     * 编辑阶梯满减
     *
     * @param fullAmountStepMinus
     * @param ruleJson
     * @return
     */
    @Transactional
    public Integer updateFullAmountStepMinus(FullAmountStepMinus fullAmountStepMinus, String ruleJson) {
        fullAmountStepMinus.setUpdateTime(new Date());
        int count = fullAmountStepMinusWriteMapper.updateByPrimaryKeySelective(fullAmountStepMinus);
        AssertUtil.isTrue(count == 0, "编辑阶梯满减活动失败，请重试");

        //阶梯满减活动规则
        //先删除再重新插入
        FullAsmRuleExample ruleExample = new FullAsmRuleExample();
        ruleExample.setFullId(fullAmountStepMinus.getFullId());
        fullAsmRuleWriteMapper.deleteByExample(ruleExample);

        JSONArray jsonArray = JSONObject.parseArray(ruleJson);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            FullAsmRule rule = new FullAsmRule();
            rule.setFullId(fullAmountStepMinus.getFullId());
            rule.setFullValue(new BigDecimal(jsonObject.getString("fullValue")));
            rule.setMinusValue(new BigDecimal(jsonObject.getString("minusValue")));
            rule.setSendIntegral(Integer.parseInt(jsonObject.getString("sendIntegral")));
            rule.setSendCouponIds(jsonObject.getString("sendCouponIds"));
            rule.setSendGoodsIds(jsonObject.getString("sendGoodsIds"));
            count = fullAsmRuleWriteMapper.insert(rule);
            AssertUtil.isTrue(count == 0, "保存阶梯满减活动规则失败，请重试");
        }
        return count;
    }

    /**
     * 根据fullId获取阶梯满金额减活动表详情
     *
     * @param fullId fullId
     * @return
     */
    public FullAmountStepMinus getFullAmountStepMinusByFullId(Integer fullId) {
        return fullAmountStepMinusReadMapper.getByPrimaryKey(fullId);
    }

    /**
     * 根据条件获取阶梯满金额减活动表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<FullAmountStepMinus> getFullAmountStepMinusList(FullAmountStepMinusExample example, PagerInfo pager) {
        List<FullAmountStepMinus> fullAmountStepMinusList;
        if (pager != null) {
            pager.setRowsCount(fullAmountStepMinusReadMapper.countByExample(example));
            fullAmountStepMinusList = fullAmountStepMinusReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            fullAmountStepMinusList = fullAmountStepMinusReadMapper.listByExample(example);
        }
        return fullAmountStepMinusList;
    }

    public Integer copyFullAsm(FullAmountStepMinus fullAmountStepMinus) {
        FullAmountStepMinus fullDb = fullAmountStepMinusReadMapper.getByPrimaryKey(fullAmountStepMinus.getFullId());
        AssertUtil.notNull(fullDb, "获取阶梯满减信息为空，请重试。");
        AssertUtil.isTrue(!fullDb.getStoreId().equals(fullAmountStepMinus.getStoreId()), "非法操作!");

        FullAmountStepMinus newFull = new FullAmountStepMinus();
        newFull.setFullName("copy-" + fullDb.getFullName());
        newFull.setStartTime(fullDb.getStartTime());
        newFull.setEndTime(fullDb.getEndTime());
        newFull.setState(PromotionConst.STATE_CREATED_1);
        newFull.setStoreId(fullAmountStepMinus.getStoreId());
        newFull.setStoreName(fullAmountStepMinus.getStoreName());
        newFull.setCreateVendorId(fullAmountStepMinus.getCreateVendorId());
        newFull.setCreateTime(new Date());
        //保存信息
        int count = fullAmountStepMinusWriteMapper.insert(newFull);
        AssertUtil.isTrue(count == 0, "复制阶梯满减信息失败，请重试！");

        //查询阶梯满减规则列表
        FullAsmRuleExample example = new FullAsmRuleExample();
        example.setFullId(fullAmountStepMinus.getFullId());
        List<FullAsmRule> ruleList = fullAsmRuleReadMapper.listByExample(example);
        for (FullAsmRule rule : ruleList) {
            FullAsmRule newRule = new FullAsmRule();
            newRule.setFullId(newFull.getFullId());
            newRule.setFullValue(rule.getFullValue());
            newRule.setMinusValue(rule.getMinusValue());
            newRule.setSendIntegral(rule.getSendIntegral());
            newRule.setSendCouponIds(rule.getSendCouponIds());
            newRule.setSendGoodsIds(rule.getSendGoodsIds());
            count = fullAsmRuleWriteMapper.insert(newRule);
            AssertUtil.isTrue(count == 0, "复制阶梯满减规则信息失败，请重试！");
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
        return "满优惠";
    }

    /**
     * 活动描述
     *
     * @return
     */
    @Override
    public List<String> promotionDescription(Integer promotionId) {
        List<String> list = new ArrayList<>();
        FullAsmRuleExample example = new FullAsmRuleExample();
        example.setFullId(promotionId);
        example.setOrderBy("rule_id ASC");
        List<FullAsmRule> ruleList = fullAsmRuleReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(ruleList)) {
            for (FullAsmRule rule : ruleList) {
                StringBuilder description = new StringBuilder("满" + rule.getFullValue() + "元减" + rule.getMinusValue() + "元");
                this.dealSendDes(description,rule);
                list.add(description.toString());
            }
        }
        return list;
    }

    /**
     * 活动描述
     *
     * @return
     */
    public String getPromotionDescription(Integer promotionId) {
        FullAsmRuleExample example = new FullAsmRuleExample();
        example.setFullId(promotionId);
        example.setOrderBy("rule_id ASC");
        List<FullAsmRule> ruleList = fullAsmRuleReadMapper.listByExample(example);
        StringBuilder description = new StringBuilder();
        if (!CollectionUtils.isEmpty(ruleList)) {
            for (FullAsmRule rule : ruleList) {
                description.append("满" + rule.getFullValue() + "元减" + rule.getMinusValue() + "元");
                this.dealSendDes(description,rule);
                description.append(";");
            }
        }
        return description.toString();
    }

    /**
     * 处理活动赠送描述信息
     * @param description
     * @param rule
     */
    private void dealSendDes(StringBuilder description,FullAsmRule rule){
        if (!StringUtil.isNullOrZero(rule.getSendIntegral())) {
            description.append(",").append("赠").append(rule.getSendIntegral()).append("积分");
        }
        if (!StringUtil.isEmpty(rule.getSendCouponIds())) {
            String[] couponIdArray = rule.getSendCouponIds().split(",");
            for (String couponId : couponIdArray) {
                if (StringUtil.isEmpty(couponId)) {
                    continue;
                }
                Coupon coupon = couponReadMapper.getByPrimaryKey(couponId);
                AssertUtil.notNull(coupon, "获取优惠券信息为空");
                description.append(",").append("赠优惠券：").append(coupon.getCouponName()).append("x1");
            }
        }
        if (!StringUtil.isEmpty(rule.getSendGoodsIds())) {
            String[] goodsIdArray = rule.getSendGoodsIds().split(",");
            for (String goodsId : goodsIdArray) {
                if (StringUtil.isEmpty(goodsId)) {
                    continue;
                }
                Goods goods = goodsModel.getGoodsByGoodsId(Long.parseLong(goodsId));
                AssertUtil.notNull(goods, "获取商品信息为空");
                description.append(",").append("赠商品：").append(goods.getGoodsName()).append("x1");
            }
        }
    }

    /**
     * 是否有单独的提交订单方法
     *
     * @return
     */
    @Override
    public Boolean specialOrder() {
        return false;
    }

    /**
     * 确认订单计算活动优惠
     *
     * @param promotionCartGroup 按照活动类型-活动id分组的购物车列表
     */
    @Override
    public void orderConfirmCalculationDiscount(OrderConfirmDTO.StoreCartGroup.PromotionCartGroup promotionCartGroup) {

        //按活动分组的购物车列表
        //判断是否为阶梯满减
        if (!promotionCartGroup.getPromotionType().equals(PromotionConst.PROMOTION_TYPE_201)) {
            //活动类型不符
            return;
        }
        if (!isPromotionAvailable(promotionCartGroup.getPromotionId() + "")) {
            //活动不可用
            return;
        }

        //查询规则
        FullAsmRuleExample example = new FullAsmRuleExample();
        example.setFullId(promotionCartGroup.getPromotionId());
        example.setOrderBy("minus_value DESC");
        List<FullAsmRule> ruleList = fullAsmRuleReadMapper.listByExample(example);
        if (CollectionUtils.isEmpty(ruleList)) {
            log.error("阶梯满减活动规则有误:type-" + promotionCartGroup.getPromotionType() + ",id-" + promotionCartGroup.getPromotionId());
            return;
        }

        for (FullAsmRule fullAsmRule : ruleList) {
            if (promotionCartGroup.getGoodsAmount().compareTo(fullAsmRule.getFullValue()) < 0) {
                //商品金额不满足活动要求
                continue;
            }

            //商品金额满足活动要求，计算优惠，按照商品原价计算折扣
            BigDecimal totalDiscount = fullAsmRule.getMinusValue();
            BigDecimal totalAmount = promotionCartGroup.getTotalAmount(); // 用于拆分计算的分母（优惠后金额）
            //将优惠金额拆分到活动分组中的各个货品
            List<Cart> cartList = promotionCartGroup.getCartList();
            this.orderConfirmAssignDiscount(totalDiscount, totalAmount, cartList);

            //满足其中一个阶梯，跳出循环
            break;
        }

        //构造活动描述
        promotionCartGroup.setPromotionDes(this.getPromotionDiscountDes(ruleList,promotionCartGroup));
    }

    /**
     * 提交订单计算活动优惠
     *
     * @param dto 拆单后的订单提交信息
     */
    @Override
    public void orderSubmitCalculationDiscount(OrderSubmitDTO dto) {

        //按活动分组的购物车列表
        List<OrderSubmitDTO.PromotionCartGroup> promotionCartGroupList = dto.getPromotionCartGroupList();
        for (OrderSubmitDTO.PromotionCartGroup promotionCartGroup : promotionCartGroupList) {
            if (!promotionCartGroup.getPromotionType().equals(PromotionConst.PROMOTION_TYPE_201)) {
                //不是阶梯满减活动，不计算
                continue;
            }
            if (!isPromotionAvailable(promotionCartGroup.getPromotionId().toString())) {
                //阶梯满减活动不可用，不计算
                continue;
            }

            //查询规则
            FullAsmRuleExample example = new FullAsmRuleExample();
            example.setFullId(promotionCartGroup.getPromotionId());
            example.setOrderBy("minus_value DESC");
            List<FullAsmRule> ruleList = fullAsmRuleReadMapper.listByExample(example);
            if (CollectionUtils.isEmpty(ruleList)) {
                log.error("阶梯满减活动规则有误:type-" + promotionCartGroup.getPromotionType() + ",id-" + promotionCartGroup.getPromotionId());
                continue;
            }
            for (FullAsmRule fullAsmRule : ruleList) {
                if (promotionCartGroup.getGoodsAmount().compareTo(fullAsmRule.getFullValue()) < 0) {
                    //商品金额不满足活动要求
                    continue;
                }
                //商品金额满足活动要求，计算优惠，按照商品原价计算折扣
                BigDecimal totalDiscount = fullAsmRule.getMinusValue(); // 活动优惠总额
                BigDecimal totalAmount = promotionCartGroup.getTotalAmount(); // 用于拆分计算的分母（优惠后金额）
                //将优惠金额拆分到活动分组中的各个货品
                this.orderSubmitAssignDiscount(totalDiscount, totalAmount, promotionCartGroup.getCartList(),
                        promotionCartGroup.getPromotionType(), promotionCartGroup.getPromotionId().toString(),
                        this.promotionName(), true, fullAsmRule.getSendIntegral(),
                        fullAsmRule.getSendCouponIds(), fullAsmRule.getSendGoodsIds(), goodsModel);

                //满足其中一个阶梯，跳出循环
                break;
            }
        }
    }


    /**
     * 校验活动是否可用
     *
     * @param promotionId
     * @return
     */
    @Override
    public Boolean isPromotionAvailable(String promotionId) {
        //查询活动是否在存在
        FullAmountStepMinusExample example = new FullAmountStepMinusExample();
        example.setFullId(Integer.valueOf(promotionId));
        example.setStartTimeBefore(new Date());
        example.setEndTimeAfter(new Date());
        example.setState(PromotionConst.STATE_RELEASE_2);
        List<FullAmountStepMinus> list = fullAmountStepMinusReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(list)) {
            //活动在有效时间内且活动状态为"进行中",活动可用,返回true
            return true;
        } else {
            return false;
        }
    }

    /**
     * 计算单条购物车优惠价格
     * 平台满减和店铺满减类活动需要重写此方法，用于添加购物车默认最优活动
     *
     * @return
     */
    public BigDecimal calculationCartDiscount(Cart cart) {
        if (!isPromotionAvailable(cart.getPromotionId().toString())) {
            //活动不可用
            return BigDecimal.ZERO;
        }
        //倒序查询规则，满足条件直接返回最高优惠
        FullAsmRuleExample example = new FullAsmRuleExample();
        example.setFullId(cart.getPromotionId());
        example.setOrderBy("minus_value DESC");
        List<FullAsmRule> list = fullAsmRuleReadMapper.listByExample(example);
        if (!CollectionUtil.isEmpty(list)) {
            BigDecimal totalAmount = cart.getProductPrice().multiply(new BigDecimal(cart.getBuyNum()));
            for (FullAsmRule rule : list) {
                if (totalAmount.compareTo(rule.getFullValue()) > 0) {
                    return rule.getMinusValue();
                }
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * 构造活动优惠描述
     * 用于购物车列表展示活动优惠描述
     *
     * @param ruleList 规则列表，按优惠力度从高到低排序
     * @param promotionCartGroup 活动分组
     * @return
     */
    private String getPromotionDiscountDes(List<FullAsmRule> ruleList, OrderConfirmDTO.StoreCartGroup.PromotionCartGroup promotionCartGroup) {
        int availableRuleIndex = ruleList.size();//满足阶梯的下标
        for (int i = 0; i < ruleList.size(); i++) {
            FullAsmRule rule = ruleList.get(i);
            if (promotionCartGroup.getGoodsAmount().compareTo(rule.getFullValue()) >= 0) {
                //满足当前优惠,跳出循环
                availableRuleIndex = i;
                break;
            }
        }
        FullAsmRule desRule = availableRuleIndex == 0 ? ruleList.get(availableRuleIndex) : ruleList.get(availableRuleIndex - 1);
        String tip = availableRuleIndex == 0 ? "已" : "";
        StringBuilder des = new StringBuilder();//优惠描述
        des.append(tip).append("满").append(desRule.getFullValue()).append("元,")
                .append(tip).append("减").append(desRule.getMinusValue()).append("元");
        this.dealSendDes(des,desRule);
        return des.toString();
    }
}