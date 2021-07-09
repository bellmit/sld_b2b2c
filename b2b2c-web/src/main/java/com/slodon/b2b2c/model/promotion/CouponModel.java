package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.business.dto.OrderConfirmDTO;
import com.slodon.b2b2c.business.dto.OrderSubmitDTO;
import com.slodon.b2b2c.business.pojo.Cart;
import com.slodon.b2b2c.core.constant.CouponConst;
import com.slodon.b2b2c.core.constant.GoodsCategoryConst;
import com.slodon.b2b2c.core.constant.PromotionConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.dao.read.promotion.CouponGoodsCategoryReadMapper;
import com.slodon.b2b2c.dao.read.promotion.CouponGoodsReadMapper;
import com.slodon.b2b2c.dao.read.promotion.CouponMemberReadMapper;
import com.slodon.b2b2c.dao.read.promotion.CouponReadMapper;
import com.slodon.b2b2c.dao.write.promotion.CouponGoodsCategoryWriteMapper;
import com.slodon.b2b2c.dao.write.promotion.CouponGoodsWriteMapper;
import com.slodon.b2b2c.dao.write.promotion.CouponMemberWriteMapper;
import com.slodon.b2b2c.dao.write.promotion.CouponWriteMapper;
import com.slodon.b2b2c.goods.example.GoodsCategoryExample;
import com.slodon.b2b2c.goods.example.GoodsExample;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.GoodsCategory;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.model.goods.GoodsCategoryModel;
import com.slodon.b2b2c.model.goods.GoodsModel;
import com.slodon.b2b2c.model.goods.ProductModel;
import com.slodon.b2b2c.model.promotion.base.PromotionBaseModel;
import com.slodon.b2b2c.promotion.example.CouponExample;
import com.slodon.b2b2c.promotion.example.CouponGoodsCategoryExample;
import com.slodon.b2b2c.promotion.example.CouponGoodsExample;
import com.slodon.b2b2c.promotion.example.CouponMemberExample;
import com.slodon.b2b2c.promotion.pojo.Coupon;
import com.slodon.b2b2c.promotion.pojo.CouponGoods;
import com.slodon.b2b2c.promotion.pojo.CouponGoodsCategory;
import com.slodon.b2b2c.promotion.pojo.CouponMember;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 优惠券表model
 */
@Component
@Slf4j
public class CouponModel extends PromotionBaseModel {

    @Resource
    private CouponReadMapper couponReadMapper;
    @Resource
    private CouponWriteMapper couponWriteMapper;
    @Resource
    private CouponGoodsReadMapper couponGoodsReadMapper;
    @Resource
    private CouponGoodsWriteMapper couponGoodsWriteMapper;
    @Resource
    private CouponGoodsCategoryReadMapper couponGoodsCategoryReadMapper;
    @Resource
    private CouponGoodsCategoryWriteMapper couponGoodsCategoryWriteMapper;
    @Resource
    private CouponMemberReadMapper couponMemberReadMapper;
    @Resource
    private CouponMemberWriteMapper couponMemberWriteMapper;

    @Resource
    private GoodsModel goodsModel;
    @Resource
    private GoodsCategoryModel goodsCategoryModel;
    @Resource
    private ProductModel productModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 新增优惠券表
     *
     * @param coupon
     * @return
     */
    public Integer saveCoupon(Coupon coupon) {
        int count = couponWriteMapper.insert(coupon);
        if (count == 0) {
            throw new MallException("添加优惠券表失败，请重试");
        }
        return count;
    }

    /**
     * 查询店铺是否有可领取的优惠券
     *
     * @param storeId
     * @return
     */
    public Boolean isHasAbleCoupon(Long storeId) {
        Boolean flag = true;
        //根据storeId查询该店铺是否有可用的优惠券列表
        CouponExample couponExample = new CouponExample();
        couponExample.setStoreId(storeId);
        couponExample.setState(CouponConst.ACTIVITY_STATE_1);
        couponExample.setPublishType(CouponConst.PUBLISH_TYPE_1);
        couponExample.setPublishStartTimeBefore(new Date());
        couponExample.setPublishEndTimeAfter(new Date());
        List<Coupon> coupons = couponReadMapper.listByExample(couponExample);
        if (CollectionUtils.isEmpty(coupons)) {
            flag = false;
        }
        return flag;
    }

    /**
     * 新增优惠券
     *
     * @param coupon
     * @param goodsIds
     * @param categoryIds
     * @return
     */
    @Transactional
    public Integer saveCoupon(Coupon coupon, String goodsIds, String categoryIds) {
        coupon.setState(CouponConst.ACTIVITY_STATE_1);
        coupon.setCreateTime(new Date());
        int count = 0;
        //拼接描述内容
        StringBuilder description = new StringBuilder();
        if (coupon.getStoreId() > 0L) {
            description.append(coupon.getStoreName() + "店铺 ");
        } else {
            description.append("仅限 ");
        }
        List<Goods> goodsList = null;
        List<GoodsCategory> categoryList = null;
        if (coupon.getUseType() == CouponConst.USE_TYPE_2) {
            if (!StringUtil.isEmpty(goodsIds)) {
                GoodsExample example = new GoodsExample();
                example.setGoodsIdIn(goodsIds);
                goodsList = goodsModel.getGoodsList(example, null);
                AssertUtil.notEmpty(goodsList, "获取商品信息为空");
                goodsList.forEach(goods -> {
                    description.append(goods.getGoodsName()).append("、");
                });
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(description.toString().substring(0, description.length() - 1));
                stringBuffer.append("使用");
                coupon.setDescription(stringBuffer.toString());
            } else {
                throw new MallException("请指定商品！");
            }
        } else if (coupon.getUseType() == CouponConst.USE_TYPE_3) {
            if (!StringUtil.isEmpty(categoryIds)) {
                GoodsCategoryExample example = new GoodsCategoryExample();
                example.setCategoryIdIn(categoryIds);
                categoryList = goodsCategoryModel.getGoodsCategoryList(example, null);
                AssertUtil.notEmpty(categoryList, "获取商品分类信息为空");
                categoryList.forEach(goodsCategory -> {
                    description.append(goodsCategory.getCategoryName()).append("、");
                });
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(description.toString().substring(0, description.length() - 1));
                stringBuffer.append("可用");
                coupon.setDescription(stringBuffer.toString());
            } else {
                throw new MallException("请指定商品分类！");
            }
        } else {
            if (coupon.getStoreId() > 0L) {
                coupon.setDescription(coupon.getStoreName() + "店铺 全部商品可用");
            } else {
                coupon.setDescription("全部商品可用");
            }
        }
        count = couponWriteMapper.insert(coupon);
        AssertUtil.isTrue(count == 0, "添加优惠券失败，请重试");

        //绑定产品信息
        if (coupon.getUseType() == CouponConst.USE_TYPE_2) {
            for (Goods goods : goodsList) {
                CouponGoods couponGoods = new CouponGoods();
                couponGoods.setCouponId(coupon.getCouponId());
                couponGoods.setGoodsId(goods.getGoodsId());
                couponGoods.setGoodsName(goods.getGoodsName());
                couponGoods.setGoodsCategoryId(goods.getCategoryId3());
                count = couponGoodsWriteMapper.insert(couponGoods);
                AssertUtil.isTrue(count == 0, "保存优惠券和产品的绑定信息失败，请重试！");
            }
            //绑定产品分类
        } else if (coupon.getUseType() == CouponConst.USE_TYPE_3) {
            for (GoodsCategory goodsCategory : categoryList) {
                CouponGoodsCategory category = new CouponGoodsCategory();
                category.setCouponId(coupon.getCouponId());
                category.setCategoryId(goodsCategory.getCategoryId());
                category.setCategoryName(goodsCategory.getCategoryName());
                category.setCategoryGrade(goodsCategory.getGrade());
                count = couponGoodsCategoryWriteMapper.insert(category);
                AssertUtil.isTrue(count == 0, "保存优惠券和产品分类的绑定信息失败，请重试！");
            }
        }
        return coupon.getCouponId();
    }

    /**
     * 根据couponId删除优惠券表
     *
     * @param couponId couponId
     * @return
     */
    public Integer deleteCoupon(Integer couponId) {
        if (StringUtils.isEmpty(couponId)) {
            throw new MallException("请选择要删除的数据");
        }
        //查询优惠券信息
        Coupon couponDb = couponReadMapper.getByPrimaryKey(couponId);
        AssertUtil.notNull(couponDb, "获取优惠券信息为空，请重试。");

        Date date = new Date();
        AssertUtil.isTrue(couponDb.getState() == CouponConst.ACTIVITY_STATE_1 &&
                        date.after(couponDb.getPublishStartTime()) && date.before(couponDb.getPublishEndTime()),
                "正在进行中的的优惠券不能删除");

        int count = 0;
        if (date.before(couponDb.getPublishStartTime())) {
            count = couponWriteMapper.deleteByPrimaryKey(couponId);
            if (count == 0) {
                log.error("根据couponId：" + couponId + "删除优惠券失败");
                throw new MallException("删除优惠券失败，请重试！");
            }
        } else {
            Coupon coupon = new Coupon();
            coupon.setCouponId(couponId);
            coupon.setState(CouponConst.ACTIVITY_STATE_3);
            count = couponWriteMapper.updateByPrimaryKeySelective(coupon);
            if (count == 0) {
                throw new MallException("删除优惠券失败,请重试！");
            }
        }
        return count;
    }

    /**
     * 根据couponId更新优惠券表
     *
     * @param coupon
     * @return
     */
    public Integer updateCoupon(Coupon coupon) {
        if (StringUtils.isEmpty(coupon.getCouponId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = couponWriteMapper.updateByPrimaryKeySelective(coupon);
        if (count == 0) {
            log.error("根据couponId：" + coupon.getCouponId() + "更新优惠券表失败");
            throw new MallException("更新优惠券表失败,请重试");
        }
        //执行失效处理时，同时处理会员已领取未使用的优惠券状态为失效状态
        if (coupon.getState() != null && coupon.getState() == CouponConst.ACTIVITY_STATE_2) {
            CouponMemberExample example = new CouponMemberExample();
            example.setCouponId(coupon.getCouponId());
            example.setUseState(CouponConst.USE_STATE_1);
            List<CouponMember> list = couponMemberReadMapper.listByExample(example);
            if (!CollectionUtils.isEmpty(list)) {
                CouponMember couponMember = new CouponMember();
                couponMember.setUseState(CouponConst.USE_STATE_3);
                couponMemberWriteMapper.updateByExampleSelective(couponMember, example);
            }
        }
        return count;
    }

    /**
     * 编辑优惠券
     *
     * @param coupon
     * @param goodsIds
     * @param categoryIds
     * @return
     */
    @Transactional
    public Integer updateCoupon(Coupon coupon, String goodsIds, String categoryIds) {
        Coupon couponDb = couponReadMapper.getByPrimaryKey(coupon.getCouponId());
        AssertUtil.notNull(couponDb, "获取优惠券信息为空，请重试!");

        if (StringUtils.isEmpty(coupon.getState())) {
            Date date = new Date();
            AssertUtil.isTrue(couponDb.getState() != CouponConst.ACTIVITY_STATE_1 && date.before(couponDb.getPublishStartTime()), "只能修改活动未开始的优惠券!");
        }
        //拼接描述内容
        StringBuilder description = new StringBuilder();
        if (coupon.getStoreId() > 0L) {
            description.append(coupon.getStoreName() + "店铺 ");
        } else {
            description.append("仅限 ");
        }
        List<Goods> goodsList = null;
        List<GoodsCategory> categoryList = null;
        if (coupon.getUseType() == CouponConst.USE_TYPE_2) {
            if (!StringUtil.isEmpty(goodsIds)) {
                GoodsExample example = new GoodsExample();
                example.setGoodsIdIn(goodsIds);
                goodsList = goodsModel.getGoodsList(example, null);
                AssertUtil.notEmpty(goodsList, "获取商品信息为空");
                goodsList.forEach(goods -> {
                    description.append(goods.getGoodsName()).append("、");
                });
                description.substring(0, description.length() - 1);
                description.append("使用");
                coupon.setDescription(description.toString());
            } else {
                throw new MallException("请指定商品！");
            }
        } else if (coupon.getUseType() == CouponConst.USE_TYPE_3) {
            if (!StringUtil.isEmpty(categoryIds)) {
                GoodsCategoryExample example = new GoodsCategoryExample();
                example.setCategoryIdIn(categoryIds);
                categoryList = goodsCategoryModel.getGoodsCategoryList(example, null);
                AssertUtil.notEmpty(categoryList, "获取商品分类信息为空");
                categoryList.forEach(goodsCategory -> {
                    description.append(goodsCategory.getCategoryName()).append("、");
                });
                description.substring(0, description.length() - 1);
                description.append("可用");
                coupon.setDescription(description.toString());
            } else {
                throw new MallException("请指定商品分类！");
            }
        } else {
            if (coupon.getStoreId() > 0L) {
                coupon.setDescription(coupon.getStoreName() + "店铺 全部商品可用");
            } else {
                coupon.setDescription("全部商品可用");
            }
        }
        int count = couponWriteMapper.updateByPrimaryKeySelective(coupon);
        if (count == 0) {
            log.error("根据couponId：" + coupon.getCouponId() + "更新优惠券表失败");
            throw new MallException("更新优惠券表失败,请重试");
        }
        //绑定产品信息
        if (coupon.getUseType() == CouponConst.USE_TYPE_2) {
            if (!StringUtil.isEmpty(goodsIds)) {
                //更新之前，删除旧的
                CouponGoodsExample example = new CouponGoodsExample();
                example.setCouponId(coupon.getCouponId());
                couponGoodsWriteMapper.deleteByExample(example);

                String[] split = goodsIds.split(",");
                for (String str : split) {
                    if (StringUtil.isEmpty(str)) {
                        continue;
                    }
                    Goods goods = goodsModel.getGoodsByGoodsId(Long.parseLong(str.trim()));
                    AssertUtil.notNull(goods, "获取商品信息为空，请重试！");

                    CouponGoods couponGoods = new CouponGoods();
                    couponGoods.setCouponId(coupon.getCouponId());
                    couponGoods.setGoodsId(goods.getGoodsId());
                    couponGoods.setGoodsName(goods.getGoodsName());
                    couponGoods.setGoodsCategoryId(goods.getCategoryId3());
                    count = couponGoodsWriteMapper.insert(couponGoods);
                    if (count == 0) {
                        throw new MallException("保存优惠券和产品的绑定信息失败，请重试！");
                    }
                }
            } else {
                throw new MallException("请指定商品！");
            }
        }
        //绑定产品分类
        if (coupon.getUseType() == CouponConst.USE_TYPE_3) {
            if (!StringUtil.isEmpty(categoryIds)) {
                //更新之前，删除旧的
                CouponGoodsCategoryExample example = new CouponGoodsCategoryExample();
                example.setCouponId(coupon.getCouponId());
                couponGoodsCategoryWriteMapper.deleteByExample(example);

                String[] split = categoryIds.split(",");
                for (String str : split) {
                    if (StringUtil.isEmpty(str)) {
                        continue;
                    }
                    GoodsCategory goodsCategory = goodsCategoryModel.getGoodsCategoryByCategoryId(Integer.parseInt(str.trim()));
                    AssertUtil.notNull(goodsCategory, "获取商品分类信息为空，请重试！");

                    CouponGoodsCategory category = new CouponGoodsCategory();
                    category.setCouponId(coupon.getCouponId());
                    category.setCategoryId(goodsCategory.getCategoryId());
                    category.setCategoryName(goodsCategory.getCategoryName());
                    category.setCategoryGrade(goodsCategory.getGrade());
                    count = couponGoodsCategoryWriteMapper.insert(category);
                    if (count == 0) {
                        throw new MallException("保存优惠券和产品分类的绑定信息失败，请重试！");
                    }
                }
            } else {
                throw new MallException("请指定商品分类！");
            }
        }
        return count;
    }

    /**
     * 根据couponId获取优惠券表详情
     *
     * @param couponId couponId
     * @return
     */
    public Coupon getCouponByCouponId(Integer couponId) {
        return couponReadMapper.getByPrimaryKey(couponId);
    }

    /**
     * 根据条件获取优惠券表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<Coupon> getCouponList(CouponExample example, PagerInfo pager) {
        List<Coupon> couponList;
        if (pager != null) {
            pager.setRowsCount(couponReadMapper.countByExample(example));
            couponList = couponReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            couponList = couponReadMapper.listByExample(example);
        }
        return couponList;
    }

    /**
     * 复制优惠券
     *
     * @param coupon
     * @return
     */
    @Transactional
    public Integer copyCoupon(Coupon coupon) {
        Coupon couponDb = couponReadMapper.getByPrimaryKey(coupon.getCouponId());
        AssertUtil.notNull(couponDb, "获取优惠券信息为空，请重试!");
        AssertUtil.isTrue(!couponDb.getStoreId().equals(coupon.getStoreId()), "非法操作!");

        Coupon couponNew = new Coupon();
        BeanUtils.copyProperties(couponDb, couponNew);
        couponNew.setState(CouponConst.ACTIVITY_STATE_1);
        couponNew.setCouponName("copy-" + couponDb.getCouponName());
        couponNew.setCreateTime(new Date());
        int count = couponWriteMapper.insert(couponNew);
        AssertUtil.isTrue(count == 0, "复制优惠券表失败,请重试");

        //查询是否有指定商品,如果有则复制指定商品
        CouponGoodsExample goodsExample = new CouponGoodsExample();
        goodsExample.setCouponId(coupon.getCouponId());
        List<CouponGoods> goodsList = couponGoodsReadMapper.listByExample(goodsExample);
        if (!CollectionUtils.isEmpty(goodsList)) {
            for (CouponGoods goods : goodsList) {
                CouponGoods couponGoods = new CouponGoods();
                BeanUtils.copyProperties(goods, couponGoods);
                couponGoods.setCouponId(couponNew.getCouponId());
                count = couponGoodsWriteMapper.insert(couponGoods);
                AssertUtil.isTrue(count == 0, "复制指定商品失败,请重试");
            }
        }

        //查询是否有指定分类,如果有则复制指定分类
        CouponGoodsCategoryExample categoryExample = new CouponGoodsCategoryExample();
        categoryExample.setCouponId(coupon.getCouponId());
        List<CouponGoodsCategory> categoryList = couponGoodsCategoryReadMapper.listByExample(categoryExample);
        if (!CollectionUtils.isEmpty(categoryList)) {
            for (CouponGoodsCategory category : categoryList) {
                CouponGoodsCategory couponGoodsCategory = new CouponGoodsCategory();
                BeanUtils.copyProperties(category, couponGoodsCategory);
                couponGoodsCategory.setCouponId(couponNew.getCouponId());
                count = couponGoodsCategoryWriteMapper.insert(couponGoodsCategory);
                AssertUtil.isTrue(count == 0, "复制指定分类失败,请重试");
            }
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
        return "优惠券";
    }

    /**
     * 是否有扩展方法
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

    }

    /**
     * 提交订单计算活动优惠
     *
     * @param dto 拆单后的订单提交信息
     */
    @Override
    public void orderSubmitCalculationDiscount(OrderSubmitDTO dto) {
        //参加优惠券的商品
        List<OrderSubmitDTO.OrderInfo.OrderProductInfo> currentList = new ArrayList<>();

        List<OrderSubmitDTO.OrderInfo> orderInfoList = dto.getOrderInfoList();
        if (this.mark == CouponConst.STORE_COUPON) {
            if (!StringUtil.isEmpty(dto.getCouponCode())) {
                //如果选中平台优惠券，校验是否可与店铺优惠券累加
                CouponMemberExample couponMemberExample = new CouponMemberExample();
                couponMemberExample.setCouponCode(dto.getCouponCode());
                List<CouponMember> memberList = couponMemberReadMapper.listByExample(couponMemberExample);
                AssertUtil.notEmpty(memberList, "获取用户优惠券信息为空");
                CouponMember couponMemberDb = memberList.get(0);
                Coupon coupon = this.getCouponByCouponId(couponMemberDb.getCouponId());
                AssertUtil.notNull(coupon, "获取优惠券信息为空，请重试！");
                if (coupon.getPlusQualification() != CouponConst.PLUS_QUALIFICATION_1) {
                    log.debug("平台优惠不可累加使用：{}", dto.getCouponCode());
                    return;
                }
            }
            //处理店铺优惠券
            for (OrderSubmitDTO.OrderInfo orderInfo : orderInfoList) {
                if (StringUtils.isEmpty(orderInfo.getVoucherCode())) {
                    //未使用优惠券
                    continue;
                }
                //优惠券是否可用
                if (!isPromotionAvailable(orderInfo.getVoucherCode())) {
                    continue;
                }

                List<OrderSubmitDTO.OrderInfo.OrderProductInfo> orderProductInfoList = orderInfo.getOrderProductInfoList();
                //查询用户优惠券信息
                CouponMemberExample example = new CouponMemberExample();
                example.setStoreId(orderInfo.getStoreId());
                example.setCouponCode(orderInfo.getVoucherCode());
                List<CouponMember> couponMemberList = couponMemberReadMapper.listByExample(example);
                AssertUtil.notEmpty(couponMemberList, "获取用户优惠券信息为空");
                CouponMember couponMember = couponMemberList.get(0);
                //查询优惠券信息
                Coupon couponDb = this.getCouponByCouponId(couponMember.getCouponId());

                if (couponDb.getUseType().equals(CouponConst.USE_TYPE_1)) {
                    //全部商品
                    currentList.addAll(orderProductInfoList);
                } else if (couponDb.getUseType().equals(CouponConst.USE_TYPE_2)) {
                    //指定商品
                    for (OrderSubmitDTO.OrderInfo.OrderProductInfo orderProductVO : orderProductInfoList) {
                        CouponGoodsExample couponGoodsExample = new CouponGoodsExample();
                        couponGoodsExample.setGoodsId(orderProductVO.getGoodsId());
                        couponGoodsExample.setCouponId(couponDb.getCouponId());
                        List<CouponGoods> couponGoodsList = couponGoodsReadMapper.listByExample(couponGoodsExample);
                        if (!CollectionUtils.isEmpty(couponGoodsList)) {
                            currentList.add(orderProductVO);
                        }
                    }
                }
                AssertUtil.notEmpty(currentList, "编码为" + orderInfo.getVoucherCode() + "的优惠券选中的商品不可用");

                BigDecimal totalUseGoodsAmount = new BigDecimal("0.00");//符合优惠券使用条件的商品总金额
                BigDecimal totalUseOrderAmount = new BigDecimal("0.00");//符合优惠券使用条件的订单总金额（其他活动优惠后金额）,用于拆分优惠基数
                for (OrderSubmitDTO.OrderInfo.OrderProductInfo productInfo : currentList) {
                    totalUseGoodsAmount = totalUseGoodsAmount.add(productInfo.getProductPrice().multiply(new BigDecimal(productInfo.getBuyNum())));
                    totalUseOrderAmount = totalUseOrderAmount.add(productInfo.getMoneyAmount());
                }
                if (totalUseGoodsAmount.compareTo(couponDb.getLimitQuota()) < 0) {
                    //商品总金额未达到优惠券使用要求，不计算优惠
                    return;
                }

                BigDecimal couponDiscount;//优惠券优惠金额
                switch (couponDb.getCouponType()) {
                    case CouponConst.COUPON_TYPE_1:
                        //1-固定金额券；
                        couponDiscount = couponDb.getPublishValue();
                        break;
                    case CouponConst.COUPON_TYPE_2:
                        //2-折扣券（折扣比例）；按照商品原价*折扣比例
                        couponDiscount = totalUseGoodsAmount
                                .subtract(totalUseGoodsAmount.multiply(couponDb.getPublishValue()).divide(new BigDecimal("100.00"), 2, RoundingMode.HALF_UP));
                        if (couponDiscount.compareTo(couponDb.getDiscountLimitAmount()) >= 0) {
                            couponDiscount = couponDb.getDiscountLimitAmount();
                        }
                        break;
                    case CouponConst.COUPON_TYPE_3:
                        //3-随机金额券
                        couponDiscount = couponMember.getRandomAmount();
                        break;
                    default:
                        return;
                }

                //拆分优惠开始
                BigDecimal totalAssign = BigDecimal.ZERO;//已分配的优惠金额
                for (int j = 0, orderProductInfoListSize = currentList.size(); j < orderProductInfoListSize; j++) {
                    OrderSubmitDTO.OrderInfo.OrderProductInfo orderProductInfo = currentList.get(j);
                    //当前货品拆分到的金额
                    if (couponDiscount.subtract(totalAssign).compareTo(BigDecimal.ZERO) <= 0) {
                        //优惠金额分配完了
                        break;
                    }
                    BigDecimal currentAssign;
                    if (j == orderProductInfoListSize - 1) {
                        //当前订单下的最后一个货品，分配剩余金额
                        currentAssign = couponDiscount.subtract(totalAssign).min(orderProductInfo.getMoneyAmount());
                    } else {
                        //不是最后一个货品，按比例分配
                        currentAssign = orderProductInfo.getMoneyAmount()
                                .multiply(couponDiscount)
                                .divide(totalUseOrderAmount, 2, RoundingMode.HALF_UP)
                                .min(orderProductInfo.getMoneyAmount());
                        totalAssign = totalAssign.add(currentAssign);
                    }

                    //构造活动信息
                    OrderSubmitDTO.PromotionInfo promotionInfo = new OrderSubmitDTO.PromotionInfo();
                    promotionInfo.setPromotionType(PromotionConst.PROMOTION_TYPE_402);
                    promotionInfo.setPromotionId(orderInfo.getVoucherCode());
                    promotionInfo.setPromotionName(this.promotionName());
                    promotionInfo.setIsStore(true);
                    promotionInfo.setDiscount(currentAssign);
                    orderProductInfo.getPromotionInfoList().add(promotionInfo);
                }
            }
        } else {
            //处理平台优惠券
            if (StringUtils.isEmpty(dto.getCouponCode())) {
                //未使用优惠券
                return;
            }
            //优惠券是否可用
            if (!isPromotionAvailable(dto.getCouponCode())) {
                return;
            }

            //查询用户优惠券信息
            CouponMemberExample example = new CouponMemberExample();
            example.setCouponCode(dto.getCouponCode());
            List<CouponMember> couponMemberList = couponMemberReadMapper.listByExample(example);
            AssertUtil.notEmpty(couponMemberList, "获取用户优惠券信息为空");
            CouponMember couponMember = couponMemberList.get(0);
            //查询优惠券信息
            Coupon couponDb = this.getCouponByCouponId(couponMember.getCouponId());
            for (OrderSubmitDTO.OrderInfo orderInfo : orderInfoList) {
                List<OrderSubmitDTO.OrderInfo.OrderProductInfo> orderProductInfoList = orderInfo.getOrderProductInfoList();
                if (couponDb.getUseType().equals(CouponConst.USE_TYPE_1)) {
                    //全部商品
                    currentList.addAll(orderProductInfoList);
                } else if (couponDb.getUseType().equals(CouponConst.USE_TYPE_2)) {
                    //指定商品
                    for (OrderSubmitDTO.OrderInfo.OrderProductInfo orderProductVO : orderProductInfoList) {
                        CouponGoodsExample couponGoodsExample = new CouponGoodsExample();
                        couponGoodsExample.setGoodsId(orderProductVO.getGoodsId());
                        couponGoodsExample.setCouponId(couponDb.getCouponId());
                        List<CouponGoods> couponGoodsList = couponGoodsReadMapper.listByExample(couponGoodsExample);
                        if (!CollectionUtils.isEmpty(couponGoodsList)) {
                            currentList.add(orderProductVO);
                        }
                    }
                } else {
                    //指定分类
                    for (OrderSubmitDTO.OrderInfo.OrderProductInfo orderProductVO : orderProductInfoList) {
                        Product product = productModel.getProductByProductId(orderProductVO.getProductId());
                        CouponGoodsCategoryExample couponGoodsCategoryExample = new CouponGoodsCategoryExample();
                        couponGoodsCategoryExample.setCouponId(couponDb.getCouponId());
                        //一级分类列表
                        couponGoodsCategoryExample.setCategoryId(product.getCategoryId1());
                        couponGoodsCategoryExample.setCategoryGrade(GoodsCategoryConst.CATEGORY_GRADE_1);
                        List<CouponGoodsCategory> categoryList1 = couponGoodsCategoryReadMapper.listByExample(couponGoodsCategoryExample);
                        if (!CollectionUtils.isEmpty(categoryList1)) {
                            currentList.add(orderProductVO);
                            continue;
                        }
                        //二级分类列表
                        couponGoodsCategoryExample.setCategoryId(product.getCategoryId2());
                        couponGoodsCategoryExample.setCategoryGrade(GoodsCategoryConst.CATEGORY_GRADE_2);
                        List<CouponGoodsCategory> categoryList2 = couponGoodsCategoryReadMapper.listByExample(couponGoodsCategoryExample);
                        if (!CollectionUtils.isEmpty(categoryList2)) {
                            currentList.add(orderProductVO);
                            continue;
                        }
                        //三级分类列表
                        couponGoodsCategoryExample.setCategoryId(product.getCategoryId3());
                        couponGoodsCategoryExample.setCategoryGrade(GoodsCategoryConst.CATEGORY_GRADE_3);
                        List<CouponGoodsCategory> categoryList3 = couponGoodsCategoryReadMapper.listByExample(couponGoodsCategoryExample);
                        if (!CollectionUtils.isEmpty(categoryList3)) {
                            currentList.add(orderProductVO);
                        }
                    }
                }
                AssertUtil.notEmpty(currentList, "编码为" + dto.getCouponCode() + "的优惠券选中的商品不可用");
            }

            BigDecimal totalUseGoodsAmount = new BigDecimal("0.00");//符合优惠券使用条件的商品总金额
            BigDecimal totalUseOrderAmount = new BigDecimal("0.00");//符合优惠券使用条件的订单总金额（其他活动优惠后金额）,用于拆分优惠基数
            for (OrderSubmitDTO.OrderInfo.OrderProductInfo productInfo : currentList) {
                totalUseGoodsAmount = totalUseGoodsAmount.add(productInfo.getProductPrice().multiply(new BigDecimal(productInfo.getBuyNum())));
                totalUseOrderAmount = totalUseOrderAmount.add(productInfo.getMoneyAmount());
            }
            if (totalUseGoodsAmount.compareTo(couponDb.getLimitQuota()) < 0) {
                //商品总金额未达到优惠券使用要求，不计算优惠
                return;
            }

            BigDecimal couponDiscount;//优惠券优惠金额
            switch (couponDb.getCouponType()) {
                case CouponConst.COUPON_TYPE_1:
                    //1-固定金额券；
                    couponDiscount = couponDb.getPublishValue();
                    break;
                case CouponConst.COUPON_TYPE_2:
                    //2-折扣券（折扣比例）；
                    couponDiscount = totalUseGoodsAmount
                            .subtract(totalUseGoodsAmount.multiply(couponDb.getPublishValue()).divide(new BigDecimal("100.00"), 2, RoundingMode.HALF_UP));
                    if (couponDiscount.compareTo(couponDb.getDiscountLimitAmount()) >= 0) {
                        couponDiscount = couponDb.getDiscountLimitAmount();
                    }
                    break;
                case CouponConst.COUPON_TYPE_3:
                    //3-随机金额券
                    couponDiscount = couponMember.getRandomAmount();
                    break;
                default:
                    return;
            }

            //拆分优惠开始
            BigDecimal totalAssign = BigDecimal.ZERO;//已分配的优惠金额
            for (int j = 0, orderProductInfoListSize = currentList.size(); j < orderProductInfoListSize; j++) {
                OrderSubmitDTO.OrderInfo.OrderProductInfo orderProductInfo = currentList.get(j);
                //当前货品拆分到的金额
                if (couponDiscount.subtract(totalAssign).compareTo(BigDecimal.ZERO) <= 0) {
                    //优惠金额分配完了
                    break;
                }
                BigDecimal currentAssign;
                if (j == orderProductInfoListSize - 1) {
                    //当前订单下的最后一个货品，分配剩余金额
                    currentAssign = couponDiscount.subtract(totalAssign).min(orderProductInfo.getMoneyAmount());
                } else {
                    //不是最后一个货品，按比例分配
                    currentAssign = orderProductInfo.getMoneyAmount()
                            .multiply(couponDiscount)
                            .divide(totalUseOrderAmount, 2, RoundingMode.HALF_UP)
                            .min(orderProductInfo.getMoneyAmount());
                    totalAssign = totalAssign.add(currentAssign);
                }

                //构造活动信息
                OrderSubmitDTO.PromotionInfo promotionInfo = new OrderSubmitDTO.PromotionInfo();
                promotionInfo.setPromotionType(PromotionConst.PROMOTION_TYPE_402);
                promotionInfo.setPromotionId(dto.getCouponCode());
                promotionInfo.setPromotionName(this.promotionName());
                promotionInfo.setIsStore(false);
                promotionInfo.setDiscount(currentAssign);
                orderProductInfo.getPromotionInfoList().add(promotionInfo);
            }
        }
    }

    /**
     * 校验活动是否可用
     *
     * @param promotionId 活动id(此处promotionId是优惠券编码)
     * @return
     */
    @Override
    public Boolean isPromotionAvailable(String promotionId) {
        //校验优惠券开关是否开启，开启则校验活动状态
        if ("1".equals(stringRedisTemplate.opsForValue().get("coupon_is_enable"))) {
            CouponMemberExample example = new CouponMemberExample();
            example.setCouponCode(promotionId);
            List<CouponMember> couponMemberList = couponMemberReadMapper.listByExample(example);
            AssertUtil.notEmpty(couponMemberList, "获取用户优惠券信息为空");
            CouponMember couponMember = couponMemberList.get(0);
            if (couponMember.getUseState() != CouponConst.USE_STATE_1) {
                log.debug("优惠券已使用：{}", promotionId);
                return false;
            }
            Coupon couponDb = this.getCouponByCouponId(couponMember.getCouponId());
            Date date = new Date();
            if (couponDb.getState() == CouponConst.ACTIVITY_STATE_1 &&
                    date.after(couponMember.getEffectiveStart()) && date.before(couponMember.getEffectiveEnd())) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 获取店铺优惠券
     *
     * @param dto
     */
    public void getStoreCouponList(OrderSubmitDTO dto, Integer source) {
        //校验优惠券开关是否开启，未开启直接返回
        if ("0".equals(stringRedisTemplate.opsForValue().get("coupon_is_enable"))) {
            return;
        }
        if (!StringUtil.isEmpty(dto.getCouponCode())) {
            //如果选中平台优惠券，校验是否可与店铺优惠券累加
            CouponMemberExample couponMemberExample = new CouponMemberExample();
            couponMemberExample.setCouponCode(dto.getCouponCode());
            List<CouponMember> memberList = couponMemberReadMapper.listByExample(couponMemberExample);
            AssertUtil.notEmpty(memberList, "获取用户优惠券信息为空");
            CouponMember couponMemberDb = memberList.get(0);
            Coupon coupon = this.getCouponByCouponId(couponMemberDb.getCouponId());
            AssertUtil.notNull(coupon, "获取优惠券信息为空，请重试！");
            if (coupon.getPlusQualification() != CouponConst.PLUS_QUALIFICATION_1) {
                log.debug("平台优惠不可累加使用：{}", dto.getCouponCode());
                return;
            }
        }
        List<OrderSubmitDTO.OrderInfo> orderInfoList = dto.getOrderInfoList();
        for (int i = 0, orderInfoListSize = orderInfoList.size(); i < orderInfoListSize; i++) {
            OrderSubmitDTO.OrderInfo orderInfo = orderInfoList.get(i);
            //查询可用优惠券
            CouponMemberExample example = new CouponMemberExample();
            example.setMemberId(dto.getMemberId());
            example.setStoreId(orderInfo.getStoreId());
            example.setEffectiveStartBefore(new Date());
            example.setEffectiveEndAfter(new Date());
            example.setUseState(CouponConst.USE_STATE_1);
            List<CouponMember> couponMembers = couponMemberReadMapper.listByExample(example);
            if (!CollectionUtils.isEmpty(couponMembers)) {
                //购物车所有货品的总价格
                BigDecimal productAmount = BigDecimal.ZERO;
                StringBuilder sb = new StringBuilder();
                StringBuilder sb2 = new StringBuilder();
                for (Cart cart : orderInfo.getOrderProductInfoList()) {
                    productAmount = productAmount.add(cart.getProductPrice().multiply(BigDecimal.valueOf(cart.getBuyNum())));
                    sb.append(",").append(cart.getGoodsId());
                    Goods goods = goodsModel.getGoodsByGoodsId(cart.getGoodsId());
                    AssertUtil.notNull(goods, "获取商品信息失败，请重试");
                    sb2.append(",").append(goods.getCategoryId1())
                            .append(",").append(goods.getCategoryId2())
                            .append(",").append(goods.getCategoryId3());
                }
                String goodsIds = sb.toString().substring(1);
                String categoryIds = sb2.toString().substring(1);
                //最大优惠和对应的会员优惠券id
                BigDecimal maxDiscount = BigDecimal.ZERO;
                Integer maxDiscountId = null;
                for (CouponMember member : couponMembers) {
                    Coupon coupon = couponReadMapper.getByPrimaryKey(member.getCouponId());
                    AssertUtil.notNull(coupon, "获取优惠券信息失败，请重试");
                    //组装优惠券信息
                    OrderSubmitDTO.CouponVO couponVO = new OrderSubmitDTO.CouponVO();
                    couponVO.setCouponMemberId(member.getCouponMemberId());
                    couponVO.setCouponId(member.getCouponId());
                    couponVO.setCouponCode(member.getCouponCode());
                    couponVO.setCouponName(coupon.getCouponName());
                    couponVO.setCouponType(coupon.getCouponType());
                    couponVO.setContent(coupon.getCouponContent());
                    couponVO.setDescription(coupon.getDescription());
                    String startTime = TimeUtil.dealTime(member.getEffectiveStart());
                    String endTime = TimeUtil.dealTime(member.getEffectiveEnd());
                    couponVO.setUseTime(startTime + "~" + endTime);
                    //判断是否满足金额使用限制
                    if (productAmount.compareTo(coupon.getLimitQuota()) >= 0) {
                        if (source == 1 || (source == 2 && !StringUtil.isEmpty(orderInfo.getVoucherCode()))) {
                            //会员选择的优惠券
                            if (!StringUtil.isEmpty(orderInfo.getVoucherCode()) && orderInfo.getVoucherCode().equals(member.getCouponCode())) {
                                couponVO.setChecked(true);
                                orderInfo.setVoucherCode(member.getCouponCode());
                            }
                        }
                        //优惠金额
                        BigDecimal discount = BigDecimal.ZERO;
                        if (coupon.getCouponType() == CouponConst.COUPON_TYPE_1) {
                            discount = coupon.getPublishValue();
                            couponVO.setValue(coupon.getPublishValue());
                        } else if (coupon.getCouponType() == CouponConst.COUPON_TYPE_2) {
                            discount = productAmount.subtract(productAmount.multiply(coupon.getPublishValue()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
                            if (discount.compareTo(coupon.getDiscountLimitAmount()) >= 0) {
                                discount = coupon.getDiscountLimitAmount();
                            }
                            couponVO.setValue(coupon.getPublishValue().divide(new BigDecimal(10)));
                        } else if (coupon.getCouponType() == CouponConst.COUPON_TYPE_3) {
                            discount = member.getRandomAmount();
                            couponVO.setValue(member.getRandomAmount());
                        }
                        //比较最大优惠
                        if (discount.compareTo(maxDiscount) >= 0) {
                            maxDiscount = discount;
                            maxDiscountId = member.getCouponMemberId();
                        }
                        couponVO.setDiscount(discount);
                        if (member.getUseType() == CouponConst.USE_TYPE_1) {
                            orderInfo.getAvailableCouponList().add(couponVO);
                        } else if (member.getUseType() == CouponConst.USE_TYPE_2) {
                            CouponGoodsExample goodsExample = new CouponGoodsExample();
                            goodsExample.setCouponId(member.getCouponId());
                            goodsExample.setGoodsIdIn(goodsIds);
                            List<CouponGoods> goodsList = couponGoodsReadMapper.listByExample(goodsExample);
                            if (!CollectionUtils.isEmpty(goodsList)) {
                                orderInfo.getAvailableCouponList().add(couponVO);
                            } else {
                                orderInfo.getDisableCouponList().add(couponVO);
                            }
                        } else if (member.getUseType() == CouponConst.USE_TYPE_3) {
                            CouponGoodsCategoryExample categoryExample = new CouponGoodsCategoryExample();
                            categoryExample.setCouponId(member.getCouponId());
                            categoryExample.setCategoryIdIn(categoryIds);
                            List<CouponGoodsCategory> categoryList = couponGoodsCategoryReadMapper.listByExample(categoryExample);
                            if (!CollectionUtils.isEmpty(categoryList)) {
                                orderInfo.getAvailableCouponList().add(couponVO);
                            } else {
                                orderInfo.getDisableCouponList().add(couponVO);
                            }
                        }
                    }
                }
                if (source == 1) {
                    //如果比较出最大值，并且不是用户选中的优惠券，默认自动选择最大优惠
                    if (!StringUtil.isNullOrZero(maxDiscountId) && StringUtil.isEmpty(orderInfo.getVoucherCode())) {
                        for (OrderSubmitDTO.CouponVO couponVO : orderInfo.getAvailableCouponList()) {
                            if (couponVO.getCouponMemberId().equals(maxDiscountId)) {
                                couponVO.setChecked(true);
                                orderInfo.setVoucherCode(couponVO.getCouponCode());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取平台优惠券
     *
     * @param dto
     */
    public void getPlatformCouponList(OrderSubmitDTO dto, Integer source) {
        //校验优惠券开关是否开启，未开启直接返回
        if ("0".equals(stringRedisTemplate.opsForValue().get("coupon_is_enable"))) {
            return;
        }
        //查询可用优惠券
        CouponMemberExample example = new CouponMemberExample();
        example.setMemberId(dto.getMemberId());
        example.setStoreId(0L);
        example.setEffectiveStartBefore(new Date());
        example.setEffectiveEndAfter(new Date());
        example.setUseState(CouponConst.USE_STATE_1);
        List<CouponMember> couponMembers = couponMemberReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(couponMembers)) {
            //最大优惠和对应的会员优惠券id
            BigDecimal maxDiscount = BigDecimal.ZERO;
            Integer maxDiscountId = null;
            for (CouponMember member : couponMembers) {
                Coupon coupon = couponReadMapper.getByPrimaryKey(member.getCouponId());
                AssertUtil.notNull(coupon, "获取优惠券信息失败，请重试");
                //组装优惠券信息
                OrderSubmitDTO.CouponVO couponVO = new OrderSubmitDTO.CouponVO();
                couponVO.setCouponMemberId(member.getCouponMemberId());
                couponVO.setCouponId(member.getCouponId());
                couponVO.setCouponCode(member.getCouponCode());
                couponVO.setCouponName(coupon.getCouponName());
                couponVO.setCouponType(coupon.getCouponType());
                couponVO.setContent(coupon.getCouponContent());
                couponVO.setDescription(coupon.getDescription());
                String startTime = TimeUtil.dealTime(member.getEffectiveStart());
                String endTime = TimeUtil.dealTime(member.getEffectiveEnd());
                couponVO.setUseTime(startTime + "~" + endTime);
                List<OrderSubmitDTO.OrderInfo> orderInfoList = dto.getOrderInfoList();
                for (int i = 0, orderInfoListSize = orderInfoList.size(); i < orderInfoListSize; i++) {
                    OrderSubmitDTO.OrderInfo orderInfo = orderInfoList.get(i);
                    if (coupon.getPlusQualification() == CouponConst.PLUS_QUALIFICATION_1) {
                        //是否允许平台优惠券和店铺叠加使用
                    } else {
                        //优惠券不为空说明使用了店铺优惠券
                        if (!StringUtil.isEmpty(orderInfo.getVoucherCode())) {
                            continue;
                        }
                    }
                    //购物车所有货品的总价格
                    BigDecimal productAmount = BigDecimal.ZERO;
                    StringBuilder sb = new StringBuilder();
                    StringBuilder sb2 = new StringBuilder();
                    for (Cart cart : orderInfo.getOrderProductInfoList()) {
                        productAmount = productAmount.add(cart.getProductPrice().multiply(BigDecimal.valueOf(cart.getBuyNum())));
                        sb.append(",").append(cart.getGoodsId());
                        Goods goods = goodsModel.getGoodsByGoodsId(cart.getGoodsId());
                        AssertUtil.notNull(goods, "获取商品信息失败，请重试");
                        sb2.append(",").append(goods.getCategoryId1())
                                .append(",").append(goods.getCategoryId2())
                                .append(",").append(goods.getCategoryId3());
                    }
                    String goodsIds = sb.toString().substring(1);
                    String categoryIds = sb2.toString().substring(1);
                    //判断是否满足金额使用限制
                    if (productAmount.compareTo(coupon.getLimitQuota()) >= 0) {
                        if (source == 1 || (source == 2 && !StringUtil.isEmpty(dto.getCouponCode()))) {
                            //会员选择的优惠券
                            if (!StringUtil.isEmpty(dto.getCouponCode()) && dto.getCouponCode().equals(member.getCouponCode())) {
                                couponVO.setChecked(true);
                                dto.setCouponCode(member.getCouponCode());
                            }
                        }
                        //优惠金额
                        BigDecimal discount = BigDecimal.ZERO;
                        if (coupon.getCouponType() == CouponConst.COUPON_TYPE_1) {
                            discount = coupon.getPublishValue();
                            couponVO.setValue(coupon.getPublishValue());
                        } else if (coupon.getCouponType() == CouponConst.COUPON_TYPE_2) {
                            discount = productAmount.subtract(productAmount.multiply(coupon.getPublishValue()).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
                            if (discount.compareTo(coupon.getDiscountLimitAmount()) >= 0) {
                                discount = coupon.getDiscountLimitAmount();
                            }
                            couponVO.setValue(coupon.getPublishValue().divide(new BigDecimal(10)));
                        } else if (coupon.getCouponType() == CouponConst.COUPON_TYPE_3) {
                            discount = member.getRandomAmount();
                            couponVO.setValue(member.getRandomAmount());
                        }
                        //比较最大优惠
                        if (discount.compareTo(maxDiscount) >= 0) {
                            maxDiscount = discount;
                            maxDiscountId = member.getCouponMemberId();
                        }
                        couponVO.setDiscount(discount);
                        if (member.getUseType() == CouponConst.USE_TYPE_1) {
                            dto.getAvailableCouponList().add(couponVO);
                        } else if (member.getUseType() == CouponConst.USE_TYPE_2) {
                            CouponGoodsExample goodsExample = new CouponGoodsExample();
                            goodsExample.setCouponId(member.getCouponId());
                            goodsExample.setGoodsIdIn(goodsIds);
                            List<CouponGoods> goodsList = couponGoodsReadMapper.listByExample(goodsExample);
                            if (!CollectionUtils.isEmpty(goodsList)) {
                                dto.getAvailableCouponList().add(couponVO);
                            } else {
                                dto.getDisableCouponList().add(couponVO);
                            }
                        } else if (member.getUseType() == CouponConst.USE_TYPE_3) {
                            CouponGoodsCategoryExample categoryExample = new CouponGoodsCategoryExample();
                            categoryExample.setCouponId(member.getCouponId());
                            categoryExample.setCategoryIdIn(categoryIds);
                            List<CouponGoodsCategory> categoryList = couponGoodsCategoryReadMapper.listByExample(categoryExample);
                            if (!CollectionUtils.isEmpty(categoryList)) {
                                dto.getAvailableCouponList().add(couponVO);
                            } else {
                                dto.getDisableCouponList().add(couponVO);
                            }
                        }
                    }
                }
            }
            if (source == 1) {
                //如果比较出最大值，并且不是用户选中的优惠券，默认自动选择最大优惠
                if (!StringUtil.isNullOrZero(maxDiscountId) && StringUtil.isEmpty(dto.getCouponCode())) {
                    for (OrderSubmitDTO.CouponVO couponVO1 : dto.getAvailableCouponList()) {
                        if (couponVO1.getCouponMemberId().equals(maxDiscountId)) {
                            couponVO1.setChecked(true);
                            dto.setCouponCode(couponVO1.getCouponCode());
                        }
                    }
                }
            }
        }
    }
}