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
import com.slodon.b2b2c.dao.read.promotion.FullAldRuleReadMapper;
import com.slodon.b2b2c.dao.read.promotion.FullAmountLadderDiscountReadMapper;
import com.slodon.b2b2c.dao.write.promotion.FullAldRuleWriteMapper;
import com.slodon.b2b2c.dao.write.promotion.FullAmountLadderDiscountWriteMapper;
import com.slodon.b2b2c.goods.example.GoodsPromotionExample;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.GoodsPromotion;
import com.slodon.b2b2c.goods.pojo.GoodsPromotionHistory;
import com.slodon.b2b2c.model.goods.GoodsModel;
import com.slodon.b2b2c.model.goods.GoodsPromotionHistoryModel;
import com.slodon.b2b2c.model.goods.GoodsPromotionModel;
import com.slodon.b2b2c.model.promotion.base.PromotionBaseModel;
import com.slodon.b2b2c.promotion.example.FullAldRuleExample;
import com.slodon.b2b2c.promotion.example.FullAmountLadderDiscountExample;
import com.slodon.b2b2c.promotion.pojo.Coupon;
import com.slodon.b2b2c.promotion.pojo.FullAldRule;
import com.slodon.b2b2c.promotion.pojo.FullAmountLadderDiscount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 阶梯满折扣活动model
 */
@Component
@Slf4j
public class FullAmountLadderDiscountModel extends PromotionBaseModel {

    @Resource
    private FullAmountLadderDiscountReadMapper fullAmountLadderDiscountReadMapper;
    @Resource
    private FullAmountLadderDiscountWriteMapper fullAmountLadderDiscountWriteMapper;
    @Resource
    private FullAldRuleReadMapper fullAldRuleReadMapper;
    @Resource
    private FullAldRuleWriteMapper fullAldRuleWriteMapper;
    @Resource
    private CouponReadMapper couponReadMapper;
    @Resource
    private GoodsPromotionModel goodsPromotionModel;
    @Resource
    private GoodsPromotionHistoryModel goodsPromotionHistoryModel;
    @Resource
    private GoodsModel goodsModel;

    /**
     * 新增阶梯满折扣活动
     *
     * @param fullAmountLadderDiscount
     * @return
     */
    public Integer saveFullAmountLadderDiscount(FullAmountLadderDiscount fullAmountLadderDiscount) {
        int count = fullAmountLadderDiscountWriteMapper.insert(fullAmountLadderDiscount);
        if (count == 0) {
            throw new MallException("添加阶梯满折扣活动失败，请重试");
        }
        return count;
    }

    /**
     * 新建阶梯满折扣活动
     *
     * @param fullAmountLadderDiscount
     * @param ruleJson
     * @return
     */
    @Transactional
    public Integer saveFullAmountLadderDiscount(FullAmountLadderDiscount fullAmountLadderDiscount, String ruleJson) {
        fullAmountLadderDiscount.setState(PromotionConst.STATE_CREATED_1);
        fullAmountLadderDiscount.setCreateTime(new Date());
        int count = fullAmountLadderDiscountWriteMapper.insert(fullAmountLadderDiscount);
        AssertUtil.isTrue(count == 0, "添加阶梯满折扣活动失败，请重试");

        //保存阶梯满折扣活动规则
        JSONArray jsonArray = JSONObject.parseArray(ruleJson);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            FullAldRule rule = new FullAldRule();
            rule.setFullId(fullAmountLadderDiscount.getFullId());
            rule.setFullValue(new BigDecimal(jsonObject.getString("fullValue")));
            rule.setMinusValue(new BigDecimal(jsonObject.getString("minusValue")));
            rule.setSendIntegral(StringUtil.isEmpty(jsonObject.getString("sendIntegral")) ? 0 : Integer.parseInt(jsonObject.getString("sendIntegral")));
            rule.setSendCouponIds(jsonObject.getString("sendCouponIds"));
            rule.setSendGoodsIds(jsonObject.getString("sendGoodsIds"));
            count = fullAldRuleWriteMapper.insert(rule);
            AssertUtil.isTrue(count == 0, "保存阶梯满折扣活动规则失败，请重试");
        }
        return count;
    }

    /**
     * 根据fullId删除阶梯满折扣活动
     *
     * @param fullId fullId
     * @return
     */
    public Integer deleteFullAmountLadderDiscount(Integer fullId) {
        if (StringUtils.isEmpty(fullId)) {
            throw new MallException("请选择要删除的数据");
        }
        FullAmountLadderDiscount fullDb = fullAmountLadderDiscountReadMapper.getByPrimaryKey(fullId);
        AssertUtil.notNull(fullDb, "获取阶梯满折扣信息为空，请重试。");
        Date date = new Date();
        AssertUtil.isTrue(fullDb.getState() == PromotionConst.STATE_RELEASE_2 && !date.after(fullDb.getEndTime()), "已经发布的阶梯满折扣活动不能删除");

        int count = 0;
        if (fullDb.getState() == PromotionConst.STATE_CREATED_1) {
            count = fullAmountLadderDiscountWriteMapper.deleteByPrimaryKey(fullId);
            if (count == 0) {
                log.error("根据fullId：" + fullId + "删除阶梯满折扣活动失败");
                throw new MallException("删除阶梯满折扣活动失败,请重试");
            }
            //再删除阶梯满折扣活动规则
            FullAldRuleExample example = new FullAldRuleExample();
            example.setFullId(fullId);
            fullAldRuleWriteMapper.deleteByExample(example);
        } else {
            FullAmountLadderDiscount full = new FullAmountLadderDiscount();
            full.setFullId(fullId);
            full.setState(PromotionConst.STATE_DELETED_6);
            count = fullAmountLadderDiscountWriteMapper.updateByPrimaryKeySelective(full);
            if (count == 0) {
                throw new MallException("删除阶梯满折扣活动失败，请重试！");
            }
        }
        return count;
    }

    /**
     * 根据fullId更新阶梯满折扣活动
     *
     * @param fullAmountLadderDiscount
     * @return
     */
    @Transactional
    public Integer updateFullAmountLadderDiscount(FullAmountLadderDiscount fullAmountLadderDiscount) {
        if (StringUtils.isEmpty(fullAmountLadderDiscount.getFullId())) {
            throw new MallException("请选择要修改的数据");
        }
        fullAmountLadderDiscount.setUpdateTime(new Date());
        int count = fullAmountLadderDiscountWriteMapper.updateByPrimaryKeySelective(fullAmountLadderDiscount);
        if (count == 0) {
            log.error("根据fullId：" + fullAmountLadderDiscount.getFullId() + "更新阶梯满折扣活动失败");
            throw new MallException("更新阶梯满折扣活动失败,请重试");
        }
        //发布时将阶梯满折扣活动数据记录到商品活动表
        if (!StringUtil.isNullOrZero(fullAmountLadderDiscount.getState()) && fullAmountLadderDiscount.getState() == PromotionConst.STATE_RELEASE_2) {
            FullAmountLadderDiscount amountLadderDiscount = fullAmountLadderDiscountReadMapper.getByPrimaryKey(fullAmountLadderDiscount.getFullId());
            AssertUtil.notNull(amountLadderDiscount, "获取阶梯满折扣活动信息为空，请重试");

            GoodsPromotion promotion = new GoodsPromotion();
            promotion.setPromotionId(fullAmountLadderDiscount.getFullId());
            promotion.setStoreId(amountLadderDiscount.getStoreId());
            promotion.setStoreName(amountLadderDiscount.getStoreName());
            promotion.setBindTime(new Date());
            promotion.setPromotionGrade(PromotionConst.PROMOTION_GRADE_2);
            promotion.setPromotionType(PromotionConst.PROMOTION_TYPE_203);
            promotion.setStartTime(amountLadderDiscount.getStartTime());
            promotion.setEndTime(amountLadderDiscount.getEndTime());
            //阶梯满折扣活动
            String description = this.getPromotionDescription(fullAmountLadderDiscount.getFullId());
            promotion.setDescription(description);
            promotion.setIsEffective(PromotionConst.IS_EFFECTIVE_YES);
            promotion.setBindType(PromotionConst.BIND_TYPE_2);
            count = goodsPromotionModel.saveGoodsPromotion(promotion);
            AssertUtil.isTrue(count == 0, "记录阶梯满折扣活动信息失败，请重试");
        }
        //失效时将阶梯满折扣活动数据记录到商品活动历史表，并删除商品活动表的数据
        if (!StringUtil.isNullOrZero(fullAmountLadderDiscount.getState()) && fullAmountLadderDiscount.getState() == PromotionConst.STATE_EXPIRED_5) {
            GoodsPromotionExample example = new GoodsPromotionExample();
            example.setPromotionId(fullAmountLadderDiscount.getFullId());
            example.setPromotionType(PromotionConst.PROMOTION_TYPE_203);
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
                AssertUtil.isTrue(count == 0, "记录阶梯满折扣活动绑定历史信息失败，请重试");

                //删除商品活动表数据
                count = goodsPromotionModel.deleteGoodsPromotion(promotion.getGoodsPromotionId());
                AssertUtil.isTrue(count == 0, "删除阶梯满折扣活动信息失败，请重试");
            }
        }
        return count;
    }

    /**
     * 编辑阶梯满折扣
     *
     * @param fullAmountLadderDiscount
     * @param ruleJson
     * @return
     */
    @Transactional
    public Integer updateFullAmountLadderDiscount(FullAmountLadderDiscount fullAmountLadderDiscount, String ruleJson) {
        fullAmountLadderDiscount.setUpdateTime(new Date());
        int count = fullAmountLadderDiscountWriteMapper.updateByPrimaryKeySelective(fullAmountLadderDiscount);
        AssertUtil.isTrue(count == 0, "编辑阶梯满折扣活动失败，请重试");

        //先删除再重新插入
        FullAldRuleExample ruleExample = new FullAldRuleExample();
        ruleExample.setFullId(fullAmountLadderDiscount.getFullId());
        fullAldRuleWriteMapper.deleteByExample(ruleExample);

        JSONArray jsonArray = JSONObject.parseArray(ruleJson);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            FullAldRule rule = new FullAldRule();
            rule.setFullId(fullAmountLadderDiscount.getFullId());
            rule.setFullValue(new BigDecimal(jsonObject.getString("fullValue")));
            rule.setMinusValue(new BigDecimal(jsonObject.getString("minusValue")));
            rule.setSendIntegral(Integer.parseInt(jsonObject.getString("sendIntegral")));
            rule.setSendCouponIds(jsonObject.getString("sendCouponIds"));
            rule.setSendGoodsIds(jsonObject.getString("sendGoodsIds"));
            count = fullAldRuleWriteMapper.insert(rule);
            AssertUtil.isTrue(count == 0, "保存阶梯满折扣活动规则失败，请重试");
        }
        return count;
    }

    /**
     * 根据fullId获取阶梯满折扣活动详情
     *
     * @param fullId fullId
     * @return
     */
    public FullAmountLadderDiscount getFullAmountLadderDiscountByFullId(Integer fullId) {
        return fullAmountLadderDiscountReadMapper.getByPrimaryKey(fullId);
    }

    /**
     * 根据条件获取阶梯满折扣活动列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<FullAmountLadderDiscount> getFullAmountLadderDiscountList(FullAmountLadderDiscountExample example, PagerInfo pager) {
        List<FullAmountLadderDiscount> fullAmountLadderDiscountList;
        if (pager != null) {
            pager.setRowsCount(fullAmountLadderDiscountReadMapper.countByExample(example));
            fullAmountLadderDiscountList = fullAmountLadderDiscountReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            fullAmountLadderDiscountList = fullAmountLadderDiscountReadMapper.listByExample(example);
        }
        return fullAmountLadderDiscountList;
    }

    /**
     * 复制阶梯满折扣活动
     *
     * @param fullAmountLadderDiscount
     * @return
     */
    public Integer copyFullAld(FullAmountLadderDiscount fullAmountLadderDiscount) {
        FullAmountLadderDiscount fullDb = fullAmountLadderDiscountReadMapper.getByPrimaryKey(fullAmountLadderDiscount.getFullId());
        AssertUtil.notNull(fullDb, "获取阶梯满折扣信息为空，请重试。");
        AssertUtil.isTrue(!fullDb.getStoreId().equals(fullAmountLadderDiscount.getStoreId()), "非法操作!");

        FullAmountLadderDiscount newFull = new FullAmountLadderDiscount();
        newFull.setFullName("copy-" + fullDb.getFullName());
        newFull.setStartTime(fullDb.getStartTime());
        newFull.setEndTime(fullDb.getEndTime());
        newFull.setState(PromotionConst.STATE_CREATED_1);
        newFull.setStoreId(fullAmountLadderDiscount.getStoreId());
        newFull.setStoreName(fullAmountLadderDiscount.getStoreName());
        newFull.setCreateVendorId(fullAmountLadderDiscount.getCreateVendorId());
        newFull.setCreateTime(new Date());
        //保存信息
        int count = fullAmountLadderDiscountWriteMapper.insert(newFull);
        AssertUtil.isTrue(count == 0, "复制阶梯满折扣信息失败，请重试！");

        //查询阶梯满折扣规则列表
        FullAldRuleExample example = new FullAldRuleExample();
        example.setFullId(fullAmountLadderDiscount.getFullId());
        List<FullAldRule> ruleList = fullAldRuleReadMapper.listByExample(example);
        for (FullAldRule rule : ruleList) {
            FullAldRule newRule = new FullAldRule();
            newRule.setFullId(newFull.getFullId());
            newRule.setFullValue(rule.getFullValue());
            newRule.setMinusValue(rule.getMinusValue());
            newRule.setSendIntegral(rule.getSendIntegral());
            newRule.setSendCouponIds(rule.getSendCouponIds());
            newRule.setSendGoodsIds(rule.getSendGoodsIds());
            count = fullAldRuleWriteMapper.insert(newRule);
            AssertUtil.isTrue(count == 0, "复制阶梯满折扣规则信息失败，请重试！");
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
        FullAldRuleExample example = new FullAldRuleExample();
        example.setFullId(promotionId);
        example.setOrderBy("rule_id ASC");
        List<FullAldRule> ruleList = fullAldRuleReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(ruleList)) {
            for (FullAldRule rule : ruleList) {
                StringBuilder description = new StringBuilder("满" + rule.getFullValue() + "元打" + rule.getMinusValue().divide(new BigDecimal(10)) + "折");
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
        FullAldRuleExample example = new FullAldRuleExample();
        example.setFullId(promotionId);
        example.setOrderBy("rule_id ASC");
        List<FullAldRule> ruleList = fullAldRuleReadMapper.listByExample(example);
        StringBuilder description = new StringBuilder();
        if (!CollectionUtils.isEmpty(ruleList)) {
            for (FullAldRule rule : ruleList) {
                description.append("满" + rule.getFullValue() + "元打" + rule.getMinusValue().divide(new BigDecimal(10)) + "折");
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
    private void dealSendDes(StringBuilder description,FullAldRule rule){
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
        if (!promotionCartGroup.getPromotionType().equals(PromotionConst.PROMOTION_TYPE_203)) {
            //活动类型不符
            return;
        }
        if (!isPromotionAvailable(promotionCartGroup.getPromotionId() + "")) {
            //活动不可用
            return;
        }

        //查询规则
        //查询最低级别的满指定金额,
        FullAldRuleExample example = new FullAldRuleExample();
        example.setFullId(promotionCartGroup.getPromotionId());
        example.setOrderBy("minus_value asc");//按折扣从低到高排序
        List<FullAldRule> ruleList = fullAldRuleReadMapper.listByExample(example);
        if (CollectionUtils.isEmpty(ruleList)) {
            log.error("阶梯满折扣活动规则有误:type-" + promotionCartGroup.getPromotionType() + ",id-" + promotionCartGroup.getPromotionId());
            return;
        }

        for (FullAldRule fullAldRule : ruleList) {
            if (promotionCartGroup.getGoodsAmount().compareTo(fullAldRule.getFullValue()) < 0) {
                //商品金额不满足活动要求
                continue;
            }

            //商品金额满足活动要求，计算优惠，按照商品原价计算折扣
            BigDecimal totalDiscount =                                   // 总优惠金额 =
                    promotionCartGroup.getGoodsAmount()                  // 活动分组的商品总金额
                            .subtract(promotionCartGroup.getGoodsAmount().multiply(fullAldRule.getMinusValue()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)); // 减去活动折扣后金额
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
            if (!promotionCartGroup.getPromotionType().equals(PromotionConst.PROMOTION_TYPE_203)) {
                //不是阶梯满折扣活动，不计算
                continue;
            }
            if (!isPromotionAvailable(promotionCartGroup.getPromotionId().toString())) {
                //阶梯满折扣活动不可用，不计算
                continue;
            }

            //查询规则
            FullAldRuleExample example = new FullAldRuleExample();
            example.setFullId(promotionCartGroup.getPromotionId());
            example.setOrderBy("minus_value asc");//按折扣从低到高排序
            List<FullAldRule> ruleList = fullAldRuleReadMapper.listByExample(example);
            if (CollectionUtils.isEmpty(ruleList)) {
                log.error("阶梯满折扣活动规则有误:type-" + promotionCartGroup.getPromotionType() + ",id-" + promotionCartGroup.getPromotionId());
                continue;
            }

            for (FullAldRule fullAldRule : ruleList) {

                if (promotionCartGroup.getGoodsAmount().compareTo(fullAldRule.getFullValue()) < 0) {
                    //商品金额不满足活动要求
                    continue;
                }

                //商品金额满足活动要求，计算优惠，按照商品原价计算折扣
                BigDecimal totalDiscount =                                   // 总优惠金额 =
                        promotionCartGroup.getGoodsAmount()                  // 活动分组的商品总金额
                                .subtract(promotionCartGroup.getGoodsAmount().multiply(fullAldRule.getMinusValue()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)); // 减去活动折扣后金额
                BigDecimal totalAmount = promotionCartGroup.getTotalAmount(); // 用于拆分计算的分母（优惠后金额）
                //将优惠金额拆分到活动分组中的各个货品
                this.orderSubmitAssignDiscount(totalDiscount, totalAmount, promotionCartGroup.getCartList(),
                        promotionCartGroup.getPromotionType(), promotionCartGroup.getPromotionId().toString(),
                        this.promotionName(), true, fullAldRule.getSendIntegral(),
                        fullAldRule.getSendCouponIds(), fullAldRule.getSendGoodsIds(), goodsModel);

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
        FullAmountLadderDiscountExample example = new FullAmountLadderDiscountExample();
        example.setFullId(Integer.valueOf(promotionId));
        example.setStartTimeBefore(new Date());
        example.setEndTimeAfter(new Date());
        example.setState(PromotionConst.STATE_RELEASE_2);
        List<FullAmountLadderDiscount> list = fullAmountLadderDiscountReadMapper.listByExample(example);
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
        FullAldRuleExample example = new FullAldRuleExample();
        example.setFullId(cart.getPromotionId());
        example.setOrderBy("minus_value asc");//按折扣从低到高排序
        List<FullAldRule> list = fullAldRuleReadMapper.listByExample(example);
        if (!CollectionUtil.isEmpty(list)) {
            BigDecimal totalAmount = cart.getProductPrice().multiply(new BigDecimal(cart.getBuyNum()));
            for (FullAldRule rule : list) {
                if (totalAmount.compareTo(rule.getFullValue()) > 0) {
                    return totalAmount.subtract(totalAmount.multiply(rule.getMinusValue()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
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
    private String getPromotionDiscountDes(List<FullAldRule> ruleList,OrderConfirmDTO.StoreCartGroup.PromotionCartGroup promotionCartGroup) {
        int availableRuleIndex = ruleList.size();//满足阶梯的下标
        for (int i = 0; i < ruleList.size(); i++) {
            FullAldRule rule = ruleList.get(i);
            if (promotionCartGroup.getGoodsAmount().compareTo(rule.getFullValue()) >= 0) {
                //满足当前优惠,跳出循环
                availableRuleIndex = i;
                break;
            }
        }
        FullAldRule desRule = availableRuleIndex == 0 ? ruleList.get(availableRuleIndex) : ruleList.get(availableRuleIndex - 1);
        StringBuilder des = new StringBuilder();//优惠描述
        if (availableRuleIndex == 0){
            //已达最大优惠
            des.append("已满").append(desRule.getFullValue()).append("元,已减")
                    .append(promotionCartGroup.getTotalDiscount()).append("元");
        }else {
            //还有更高的优惠
            des.append("满").append(desRule.getFullValue()).append("元,打")
                    .append(desRule.getMinusValue().divide(new BigDecimal(10))).append("折");
        }
        this.dealSendDes(des,desRule);
        return des.toString();
    }
}