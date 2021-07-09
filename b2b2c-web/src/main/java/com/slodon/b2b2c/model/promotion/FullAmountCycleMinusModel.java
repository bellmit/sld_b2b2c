package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.business.dto.OrderConfirmDTO;
import com.slodon.b2b2c.business.dto.OrderSubmitDTO;
import com.slodon.b2b2c.business.pojo.Cart;
import com.slodon.b2b2c.core.constant.PromotionConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.dao.read.promotion.CouponReadMapper;
import com.slodon.b2b2c.dao.read.promotion.FullAmountCycleMinusReadMapper;
import com.slodon.b2b2c.dao.write.promotion.FullAmountCycleMinusWriteMapper;
import com.slodon.b2b2c.goods.example.GoodsPromotionExample;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.GoodsPromotion;
import com.slodon.b2b2c.goods.pojo.GoodsPromotionHistory;
import com.slodon.b2b2c.model.goods.GoodsModel;
import com.slodon.b2b2c.model.goods.GoodsPromotionHistoryModel;
import com.slodon.b2b2c.model.goods.GoodsPromotionModel;
import com.slodon.b2b2c.model.promotion.base.PromotionBaseModel;
import com.slodon.b2b2c.promotion.example.FullAmountCycleMinusExample;
import com.slodon.b2b2c.promotion.pojo.Coupon;
import com.slodon.b2b2c.promotion.pojo.FullAmountCycleMinus;
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
 * 循环满减活动model
 */
@Component
@Slf4j
public class FullAmountCycleMinusModel extends PromotionBaseModel {

    @Resource
    private FullAmountCycleMinusReadMapper fullAmountCycleMinusReadMapper;
    @Resource
    private FullAmountCycleMinusWriteMapper fullAmountCycleMinusWriteMapper;
    @Resource
    private CouponReadMapper couponReadMapper;
    @Resource
    private GoodsPromotionModel goodsPromotionModel;
    @Resource
    private GoodsPromotionHistoryModel goodsPromotionHistoryModel;
    @Resource
    private GoodsModel goodsModel;

    /**
     * 新增循环满减活动表
     *
     * @param fullAmountCycleMinus
     * @return
     */
    public Integer saveFullAmountCycleMinus(FullAmountCycleMinus fullAmountCycleMinus) {
        fullAmountCycleMinus.setState(PromotionConst.STATE_CREATED_1);
        fullAmountCycleMinus.setCreateTime(new Date());
        int count = fullAmountCycleMinusWriteMapper.insert(fullAmountCycleMinus);
        if (count == 0) {
            throw new MallException("添加循环满减活动表失败，请重试");
        }
        return count;
    }

    /**
     * 根据fullId删除循环满减活动表
     *
     * @param fullId fullId
     * @return
     */
    public Integer deleteFullAmountCycleMinus(Integer fullId) {
        if (StringUtils.isEmpty(fullId)) {
            throw new MallException("请选择要删除的数据");
        }
        FullAmountCycleMinus fullDb = fullAmountCycleMinusReadMapper.getByPrimaryKey(fullId);
        AssertUtil.notNull(fullDb, "获取循环满减信息为空，请重试。");
        Date date = new Date();
        AssertUtil.isTrue(fullDb.getState() == PromotionConst.STATE_RELEASE_2 && !date.after(fullDb.getEndTime()), "已经发布的循环满减活动不能删除");

        int count = 0;
        if (fullDb.getState() == PromotionConst.STATE_CREATED_1) {
            count = fullAmountCycleMinusWriteMapper.deleteByPrimaryKey(fullId);
            if (count == 0) {
                log.error("根据fullId：" + fullId + "删除循环满减活动表失败");
                throw new MallException("删除循环满减活动表失败,请重试");
            }
        } else {
            FullAmountCycleMinus full = new FullAmountCycleMinus();
            full.setFullId(fullId);
            full.setState(PromotionConst.STATE_DELETED_6);
            count = fullAmountCycleMinusWriteMapper.updateByPrimaryKeySelective(full);
            if (count == 0) {
                throw new MallException("删除循环满减活动失败,请重试！");
            }
        }
        return count;
    }

    /**
     * 根据fullId更新循环满减活动表
     *
     * @param fullAmountCycleMinus
     * @return
     */
    @Transactional
    public Integer updateFullAmountCycleMinus(FullAmountCycleMinus fullAmountCycleMinus) {
        if (StringUtils.isEmpty(fullAmountCycleMinus.getFullId())) {
            throw new MallException("请选择要修改的数据");
        }
        fullAmountCycleMinus.setUpdateTime(new Date());
        int count = fullAmountCycleMinusWriteMapper.updateByPrimaryKeySelective(fullAmountCycleMinus);
        if (count == 0) {
            log.error("根据fullId：" + fullAmountCycleMinus.getFullId() + "更新循环满减活动表失败");
            throw new MallException("更新循环满减活动表失败,请重试");
        }
        //发布时将循环满减活动数据记录到商品活动表
        if (!StringUtil.isNullOrZero(fullAmountCycleMinus.getState()) && fullAmountCycleMinus.getState() == PromotionConst.STATE_RELEASE_2) {
            FullAmountCycleMinus amountCycleMinus = fullAmountCycleMinusReadMapper.getByPrimaryKey(fullAmountCycleMinus.getFullId());
            AssertUtil.notNull(amountCycleMinus, "获取循环满减活动信息为空，请重试");

            GoodsPromotion promotion = new GoodsPromotion();
            promotion.setPromotionId(fullAmountCycleMinus.getFullId());
            promotion.setStoreId(amountCycleMinus.getStoreId());
            promotion.setStoreName(amountCycleMinus.getStoreName());
            promotion.setBindTime(new Date());
            promotion.setPromotionGrade(PromotionConst.PROMOTION_GRADE_2);
            promotion.setPromotionType(PromotionConst.PROMOTION_TYPE_202);
            promotion.setStartTime(amountCycleMinus.getStartTime());
            promotion.setEndTime(amountCycleMinus.getEndTime());
            //循环满减活动
            String description = this.getPromotionDescription(fullAmountCycleMinus.getFullId());
            promotion.setDescription(description);
            promotion.setIsEffective(PromotionConst.IS_EFFECTIVE_YES);
            promotion.setBindType(PromotionConst.BIND_TYPE_2);
            count = goodsPromotionModel.saveGoodsPromotion(promotion);
            AssertUtil.isTrue(count == 0, "记录循环满减活动信息失败，请重试");
        }
        //失效时将循环满减活动数据记录到商品活动历史表，并删除商品活动表的数据
        if (!StringUtil.isNullOrZero(fullAmountCycleMinus.getState()) && fullAmountCycleMinus.getState() == PromotionConst.STATE_EXPIRED_5) {
            GoodsPromotionExample example = new GoodsPromotionExample();
            example.setPromotionId(fullAmountCycleMinus.getFullId());
            example.setPromotionType(PromotionConst.PROMOTION_TYPE_202);
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
                AssertUtil.isTrue(count == 0, "记录循环满减活动绑定历史信息失败，请重试");

                //删除商品活动表数据
                count = goodsPromotionModel.deleteGoodsPromotion(promotion.getGoodsPromotionId());
                AssertUtil.isTrue(count == 0, "删除循环满减活动信息失败，请重试");
            }
        }
        return count;
    }

    /**
     * 根据fullId获取循环满减活动表详情
     *
     * @param fullId fullId
     * @return
     */
    public FullAmountCycleMinus getFullAmountCycleMinusByFullId(Integer fullId) {
        return fullAmountCycleMinusReadMapper.getByPrimaryKey(fullId);
    }

    /**
     * 根据条件获取循环满减活动表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<FullAmountCycleMinus> getFullAmountCycleMinusList(FullAmountCycleMinusExample example, PagerInfo pager) {
        List<FullAmountCycleMinus> fullAmountCycleMinusList;
        if (pager != null) {
            pager.setRowsCount(fullAmountCycleMinusReadMapper.countByExample(example));
            fullAmountCycleMinusList = fullAmountCycleMinusReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            fullAmountCycleMinusList = fullAmountCycleMinusReadMapper.listByExample(example);
        }
        return fullAmountCycleMinusList;
    }

    /**
     * 复制循环满减活动
     *
     * @param fullAmountCycleMinus
     * @return
     */
    public Integer copyFullAcm(FullAmountCycleMinus fullAmountCycleMinus) {
        FullAmountCycleMinus fullDb = fullAmountCycleMinusReadMapper.getByPrimaryKey(fullAmountCycleMinus.getFullId());
        AssertUtil.notNull(fullDb, "获取循环满减信息为空，请重试。");
        AssertUtil.isTrue(!fullDb.getStoreId().equals(fullAmountCycleMinus.getStoreId()), "非法操作!");

        FullAmountCycleMinus newFull = new FullAmountCycleMinus();
        newFull.setFullName("copy-" + fullDb.getFullName());
        newFull.setStartTime(fullDb.getStartTime());
        newFull.setEndTime(fullDb.getEndTime());
        newFull.setFullValue(fullDb.getFullValue());
        newFull.setMinusValue(fullDb.getMinusValue());
        newFull.setState(PromotionConst.STATE_CREATED_1);
        newFull.setStoreId(fullAmountCycleMinus.getStoreId());
        newFull.setStoreName(fullAmountCycleMinus.getStoreName());
        newFull.setCreateVendorId(fullAmountCycleMinus.getCreateVendorId());
        newFull.setCreateTime(new Date());
        newFull.setSendIntegral(fullDb.getSendIntegral());
        newFull.setSendCouponIds(fullDb.getSendCouponIds());
        newFull.setSendGoodsIds(fullDb.getSendGoodsIds());
        int count = fullAmountCycleMinusWriteMapper.insert(newFull);
        AssertUtil.isTrue(count == 0, "复制循环满减信息失败，请重试！");

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
        FullAmountCycleMinus fullAmountCycleMinus = this.getFullAmountCycleMinusByFullId(promotionId);
        AssertUtil.notNull(fullAmountCycleMinus, "获取循环满减活动信息为空");
        StringBuilder description = new StringBuilder("每满" + fullAmountCycleMinus.getFullValue() + "元减" + fullAmountCycleMinus.getMinusValue());
        this.dealSendDes(description,fullAmountCycleMinus);
        list.add(description.toString());
        return list;
    }

    /**
     * 活动描述
     *
     * @return
     */
    public String getPromotionDescription(Integer promotionId) {

        FullAmountCycleMinus fullAmountCycleMinus = this.getFullAmountCycleMinusByFullId(promotionId);
        AssertUtil.notNull(fullAmountCycleMinus, "获取循环满减活动信息为空");
        StringBuilder description = new StringBuilder("每满" + fullAmountCycleMinus.getFullValue() + "元减" + fullAmountCycleMinus.getMinusValue());
        this.dealSendDes(description,fullAmountCycleMinus);
        description.append(";");
        return description.toString();
    }

    /**
     * 处理活动赠送描述信息
     * @param description
     * @param fullAmountCycleMinus
     */
    private void dealSendDes(StringBuilder description,FullAmountCycleMinus fullAmountCycleMinus){
        if (!StringUtil.isNullOrZero(fullAmountCycleMinus.getSendIntegral())) {
            description.append(",").append("赠").append(fullAmountCycleMinus.getSendIntegral()).append("积分");
        }
        if (!StringUtil.isEmpty(fullAmountCycleMinus.getSendCouponIds())) {
            String[] couponIdArray = fullAmountCycleMinus.getSendCouponIds().split(",");
            for (String couponId : couponIdArray) {
                if (StringUtil.isEmpty(couponId)) {
                    continue;
                }
                Coupon coupon = couponReadMapper.getByPrimaryKey(couponId);
                AssertUtil.notNull(coupon, "获取优惠券信息为空");
                description.append(",").append("赠优惠券：").append(coupon.getCouponName()).append("x1");
            }
        }
        if (!StringUtil.isEmpty(fullAmountCycleMinus.getSendGoodsIds())) {
            String[] goodsIdArray = fullAmountCycleMinus.getSendGoodsIds().split(",");
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

        //判断是否为循环满减
        if (!promotionCartGroup.getPromotionType().equals(PromotionConst.PROMOTION_TYPE_202)) {
            //活动类型不符
            return;
        }
        if (!isPromotionAvailable(promotionCartGroup.getPromotionId() + "")) {
            //活动不可用
            return;
        }

        FullAmountCycleMinus fullAmountCycleMinus = fullAmountCycleMinusReadMapper.getByPrimaryKey(promotionCartGroup.getPromotionId());
        int cycle = promotionCartGroup.getGoodsAmount().divide(fullAmountCycleMinus.getFullValue(), 2, RoundingMode.HALF_UP).intValue();//商品金额可满足的循环减次数
        if (cycle == 0) {
            //不满足活动条件
            return;
        }
        BigDecimal totalDiscount = fullAmountCycleMinus.getMinusValue().multiply(BigDecimal.valueOf(cycle));//优惠总金额
        BigDecimal totalAmount = promotionCartGroup.getTotalAmount(); // 用于拆分计算的分母（优惠后金额）

        //将优惠金额拆分到活动分组中的各个货品
        List<Cart> cartList = promotionCartGroup.getCartList();
        this.orderConfirmAssignDiscount(totalDiscount, totalAmount, cartList);

        //构造活动描述
        promotionCartGroup.setPromotionDes(this.getPromotionDiscountDes(fullAmountCycleMinus,promotionCartGroup));
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
            if (!promotionCartGroup.getPromotionType().equals(PromotionConst.PROMOTION_TYPE_202)) {
                //不是循环满减活动，不计算
                continue;
            }
            if (!isPromotionAvailable(promotionCartGroup.getPromotionId().toString())) {
                //循环满减活动不可用，不计算
                continue;
            }

            FullAmountCycleMinus fullAmountCycleMinus = fullAmountCycleMinusReadMapper.getByPrimaryKey(promotionCartGroup.getPromotionId());
            int cycle = promotionCartGroup.getGoodsAmount().divide(fullAmountCycleMinus.getFullValue(), 2, RoundingMode.HALF_UP).intValue();//商品金额可满足的循环减次数
            if (cycle == 0) {
                //不满足活动条件
                continue;
            }
            BigDecimal totalDiscount = fullAmountCycleMinus.getMinusValue().multiply(BigDecimal.valueOf(cycle));//优惠总金额
            BigDecimal totalAmount = promotionCartGroup.getTotalAmount(); // 用于拆分计算的分母（优惠后金额）
            int sendIntegral = fullAmountCycleMinus.getSendIntegral() == null ? 0 : cycle * fullAmountCycleMinus.getSendIntegral();//赠送积分数
            String sendCouponIds = null;//赠送优惠券id
            String sendGoodsIds = null;//赠送商品id
            if (!StringUtils.isEmpty(fullAmountCycleMinus.getSendCouponIds())) {
                //构造赠送的优惠券
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < cycle; i++) {
                    stringBuilder.append(",").append(fullAmountCycleMinus.getSendCouponIds());
                }
                sendCouponIds = stringBuilder.toString().substring(1);
            }
            if (!StringUtils.isEmpty(fullAmountCycleMinus.getSendGoodsIds())) {
                //构造赠送的商品
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < cycle; i++) {
                    stringBuilder.append(",").append(fullAmountCycleMinus.getSendGoodsIds());
                }
                sendGoodsIds = stringBuilder.toString().substring(1);
            }

            //将优惠金额拆分到活动分组中的各个货品
            List<OrderSubmitDTO.OrderInfo.OrderProductInfo> cartList = promotionCartGroup.getCartList();
            this.orderSubmitAssignDiscount(totalDiscount, totalAmount, cartList,
                    promotionCartGroup.getPromotionType(), promotionCartGroup.getPromotionId().toString(),
                    this.promotionName(), true, sendIntegral,
                    sendCouponIds, sendGoodsIds, goodsModel);
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
        //查询循环满减活动是否在存在
        FullAmountCycleMinusExample example = new FullAmountCycleMinusExample();
        example.setFullId(Integer.parseInt(promotionId));
        example.setStartTimeBefore(new Date());
        example.setEndTimeAfter(new Date());
        example.setState(PromotionConst.STATE_RELEASE_2);
        List<FullAmountCycleMinus> list = fullAmountCycleMinusReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(list)) {
            //循环满减活动在有效时间内且活动状态为"进行中",活动可用,返回true
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
        FullAmountCycleMinus fullAmountCycleMinus = this.getFullAmountCycleMinusByFullId(cart.getPromotionId());
        if (fullAmountCycleMinus != null) {
            BigDecimal totalAmount = cart.getProductPrice().multiply(new BigDecimal(cart.getBuyNum()));
            if (totalAmount.compareTo(fullAmountCycleMinus.getFullValue()) > 0) {
                //满足几次循环
                BigDecimal cycleNum = totalAmount.divide(fullAmountCycleMinus.getFullValue(), 0, RoundingMode.DOWN);
                return fullAmountCycleMinus.getMinusValue().multiply(cycleNum);
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * 构造活动优惠描述
     * 用于购物车列表展示活动优惠描述
     *
     * @param fullAmountCycleMinus 活动信息
     * @param promotionCartGroup 活动分组
     * @return
     */
    private String getPromotionDiscountDes(FullAmountCycleMinus fullAmountCycleMinus, OrderConfirmDTO.StoreCartGroup.PromotionCartGroup promotionCartGroup) {
        StringBuilder des = new StringBuilder();//优惠描述
        //还有更高的优惠
        des.append("每满").append(fullAmountCycleMinus.getFullValue()).append("元,减")
                .append(fullAmountCycleMinus.getMinusValue()).append("元");

        this.dealSendDes(des,fullAmountCycleMinus);
        return des.toString();
    }
}