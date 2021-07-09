package com.slodon.b2b2c.model.promotion;


import com.slodon.b2b2c.business.dto.OrderConfirmDTO;
import com.slodon.b2b2c.business.dto.OrderSubmitDTO;
import com.slodon.b2b2c.business.dto.PreOrderDTO;
import com.slodon.b2b2c.business.example.OrderProductExample;
import com.slodon.b2b2c.business.pojo.Order;
import com.slodon.b2b2c.business.pojo.OrderProduct;
import com.slodon.b2b2c.core.constant.PromotionConst;
import com.slodon.b2b2c.core.constant.RedisConst;
import com.slodon.b2b2c.core.constant.SeckillConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.dao.read.promotion.SeckillReadMapper;
import com.slodon.b2b2c.dao.read.promotion.SeckillStageProductBindMemberReadMapper;
import com.slodon.b2b2c.dao.read.promotion.SeckillStageProductReadMapper;
import com.slodon.b2b2c.dao.read.promotion.SeckillStageReadMapper;
import com.slodon.b2b2c.dao.write.promotion.SeckillStageProductWriteMapper;
import com.slodon.b2b2c.dao.write.promotion.SeckillWriteMapper;
import com.slodon.b2b2c.model.business.OrderModel;
import com.slodon.b2b2c.model.business.OrderProductModel;
import com.slodon.b2b2c.model.promotion.base.PromotionBaseModel;
import com.slodon.b2b2c.promotion.dto.SeckillAddDTO;
import com.slodon.b2b2c.promotion.dto.SeckillUpdateDTO;
import com.slodon.b2b2c.promotion.example.SeckillExample;
import com.slodon.b2b2c.promotion.example.SeckillStageExample;
import com.slodon.b2b2c.promotion.example.SeckillStageProductBindMemberExample;
import com.slodon.b2b2c.promotion.example.SeckillStageProductExample;
import com.slodon.b2b2c.promotion.pojo.Seckill;
import com.slodon.b2b2c.promotion.pojo.SeckillStage;
import com.slodon.b2b2c.promotion.pojo.SeckillStageProduct;
import com.slodon.b2b2c.promotion.pojo.SeckillStageProductBindMember;
import com.slodon.b2b2c.vo.promotion.FrontSeckillGoodsVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
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

/**
 * 秒杀活动表model
 */
@Component
@Slf4j
public class SeckillModel extends PromotionBaseModel {

    @Resource
    private SeckillReadMapper seckillReadMapper;
    @Resource
    private SeckillStageReadMapper seckillStageReadMapper;
    @Resource
    private SeckillStageProductReadMapper seckillStageProductReadMapper;
    @Resource
    private SeckillStageProductBindMemberReadMapper seckillStageProductBindMemberReadMapper;
    @Resource
    private SeckillStageProductWriteMapper seckillStageProductWriteMapper;
    @Resource
    private SeckillWriteMapper seckillWriteMapper;
    @Resource
    private SeckillStageModel seckillStageModel;
    @Resource
    private SeckillOrderExtendModel seckillOrderExtendModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private OrderModel orderModel;
    @Resource
    private OrderProductModel orderProductModel;

    /**
     * 新增秒杀活动表
     *
     * @param seckill
     * @return
     */
    public Integer saveSeckill(Seckill seckill) {
        int count = seckillWriteMapper.insert(seckill);
        if (count == 0) {
            throw new MallException("添加秒杀活动表失败，请重试");
        }
        return count;
    }

    /**
     * 新增秒杀活动表
     *
     * @param seckillAddDTO
     * @param adminId
     * @return
     */
    @Transactional
    public Integer saveSeckill(SeckillAddDTO seckillAddDTO, Integer adminId) throws Exception {
        //根据活动名称查重
        SeckillExample example = new SeckillExample();
        example.setSeckillName(seckillAddDTO.getSeckillName());
        List<Seckill> list = seckillReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(list)) {
            throw new MallException("活动名称重复,请重新填写");
        }

        Seckill insertOne = new Seckill();
        PropertyUtils.copyProperties(insertOne, seckillAddDTO);
        //秒杀活动结束时间为结束日期当天23:59:59
        insertOne.setEndTime(TimeUtil.getDayEndFormatYMDHMS(seckillAddDTO.getEndTime()));

        insertOne.setCreateTime(new Date());
        insertOne.setCreateAdminId(adminId);

        int count = seckillWriteMapper.insert(insertOne);
        if (count == 0) {
            throw new MallException("添加秒杀活动失败，请重试");
        }
        seckillStageModel.saveSeckillStage(insertOne, seckillAddDTO.getStages());
        return count;
    }

    /**
     * 根据seckillId删除秒杀活动表
     *
     * @param seckillId seckillId
     * @return
     */
    public Integer deleteSeckill(Integer seckillId) {
        if (StringUtils.isEmpty(seckillId)) {
            throw new MallException("请选择要删除的数据");
        }
        seckillStageModel.deleteSeckillStageBySeckillId(seckillId);
        int count = seckillWriteMapper.deleteByPrimaryKey(seckillId);
        if (count == 0) {
            log.error("根据seckillId：" + seckillId + "删除秒杀活动表失败");
            throw new MallException("删除秒杀活动表失败,请重试");
        }
        return count;
    }

    /**
     * 根据seckillIds批量删除秒杀标签
     *
     * @param seckillIds
     * @return
     */
    @Transactional
    public void batchDeleteSeckill(String seckillIds) {
        SeckillExample example = new SeckillExample();
        example.setSeckillIdIn(seckillIds);
        int count = seckillWriteMapper.deleteByExample(example);
        if (count == 0) {
            log.error("根据seckillIds:" + seckillIds + "批量删除秒杀活动失败");
            throw new MallException("删除秒杀活动失败,请重试!");
        }
    }

    /**
     * 根据seckillId更新秒杀活动表
     *
     * @param seckill
     * @return
     */
    public Integer updateSeckill(Seckill seckill) {
        if (StringUtils.isEmpty(seckill.getSeckillId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = seckillWriteMapper.updateByPrimaryKeySelective(seckill);
        if (count == 0) {
            log.error("根据seckillId：" + seckill.getSeckillId() + "更新秒杀活动表失败");
            throw new MallException("更新秒杀活动表失败,请重试");
        }
        return count;
    }

    /**
     * 根据seckillUpdateDTO更新秒杀活动表
     *
     * @param seckillUpdateDTO
     * @param adminId
     * @return
     */
    public Integer updateseckill(SeckillUpdateDTO seckillUpdateDTO, Integer adminId) throws Exception {
        Seckill updateOne = new Seckill();
        PropertyUtils.copyProperties(updateOne, seckillUpdateDTO);
        updateOne.setUpdateTime(new Date());
        updateOne.setUpdateAdminId(adminId);
        int count = seckillWriteMapper.updateByPrimaryKeySelective(updateOne);
        if (count == 0) {
            log.error("根据seckillUpdateDTO：" + seckillUpdateDTO.getSeckillName() + "更新秒杀活动失败");
            throw new MallException("更新秒杀活动失败,请重试");
        }
        return count;
    }

    /**
     * 根据seckillId获取秒杀活动表详情
     *
     * @param seckillId seckillId
     * @return
     */
    public Seckill getSeckillBySeckillId(Integer seckillId) {
        return seckillReadMapper.getByPrimaryKey(seckillId);
    }

    /**
     * 根据条件获取秒杀活动表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<Seckill> getSeckillList(SeckillExample example, PagerInfo pager) {
        List<Seckill> seckillList;
        if (pager != null) {
            pager.setRowsCount(seckillReadMapper.countByExample(example));
            seckillList = seckillReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            seckillList = seckillReadMapper.listByExample(example);
        }
        return seckillList;
    }

    /**
     * 根据条件获取秒杀活动列表，自定义排序
     *
     * @param fields  查询字段，逗号分隔
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<Seckill> getSeckillFieldList(String fields, SeckillExample example, PagerInfo pager) {
        List<Seckill> seckillList;
        if (pager != null) {
            pager.setRowsCount(seckillReadMapper.countByExample(example));
            seckillList = seckillReadMapper.listPageByFieldsExample(fields, example, pager.getStart(), pager.getPageSize());
        } else {
            seckillList = seckillReadMapper.listByFieldsExample(fields, example);
        }
        return seckillList;
    }

    /**
     * 根据条件获取秒杀商品表列表
     *
     * @param fields  查询字段，逗号分隔
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<SeckillStageProduct> getSeckillStageProductFieldList(String fields, SeckillStageProductExample example, PagerInfo pager) {
        List<SeckillStageProduct> seckillStageProductList;
        if (pager != null) {
            pager.setRowsCount(seckillStageProductReadMapper.countByGoodsIdExample(fields, example));
            seckillStageProductList = seckillStageProductReadMapper.listFieldsPageByExample(fields, example, pager.getStart(), pager.getPageSize());
        } else {
            seckillStageProductList = seckillStageProductReadMapper.listFieldsByExample(fields, example);
        }
        return seckillStageProductList;
    }

    /**
     * 秒杀商品列表
     *
     * @return
     */
    public List<FrontSeckillGoodsVO> getSeckillGoodsList(PagerInfo pager, Integer seckillId, Integer labelId, Integer stageId, Integer memberId) {
        List<FrontSeckillGoodsVO> vos = new ArrayList<>();
        if (StringUtil.isNullOrZero(labelId)) {
            //labelId为空,则查询全部标签下,展示时间场次下进行中和未开始的商品
            //根据stageId查询秒杀场次
            SeckillStage seckillStage = seckillStageReadMapper.getByPrimaryKey(stageId);
            AssertUtil.notNull(seckillStage, "查询秒杀场次信息失败");

            //查询秒杀商品列表
            //根据spu查询商品列表
//            String fields = "goods_id,stage_product_id,start_time,end_time,product_id,main_image,goods_name,store_id,store_name,product_price,seckill_price,seckill_stock,upper_limit,buyer_count,buy_quantity,product_stock";
            String fields = "goods_id,stage_id";
            SeckillStageProductExample example = new SeckillStageProductExample();
            example.setStageId(stageId);
            example.setVerifyState(SeckillConst.SECKILL_AUDIT_STATE_2);
            example.setGroupBy(fields);
            example.setOrderBy("goods_id desc");
            List<SeckillStageProduct> list = getSeckillStageProductFieldList(fields, example, pager);
            if (!CollectionUtils.isEmpty(list)) {
                list.forEach(seckillStageProduct -> {
                    SeckillStageProduct product = null;
                    SeckillStageProductExample productExample = new SeckillStageProductExample();
                    productExample.setGoodsId(seckillStageProduct.getGoodsId());
                    productExample.setStageId(seckillStageProduct.getStageId());
                    List<SeckillStageProduct> productList = seckillStageProductReadMapper.listByExample(productExample);
                    if (!CollectionUtils.isEmpty(productList)) {
                        for (SeckillStageProduct stageProduct : productList) {
                            //判断哪个规格有库存，展示有库存规格，否则默认取第一个
                            if (!StringUtils.isEmpty(stageProduct.getBuyQuantity()) && stageProduct.getBuyQuantity() < stageProduct.getSeckillStock()) {
                                product = stageProduct;
                                break;
                            }
                        }
                        if (product == null) {
                            product = productList.get(0);
                        }
                        //未开始要设置提醒
                        Boolean notice = false;
                        SeckillStageProductBindMemberExample productBindMemberExample = new SeckillStageProductBindMemberExample();
                        productBindMemberExample.setMemberId(memberId);
                        productBindMemberExample.setStageProductId(product.getStageProductId());
                        List<SeckillStageProductBindMember> productBindMemberList = seckillStageProductBindMemberReadMapper.listByExample(productBindMemberExample);
                        if (!StringUtils.isEmpty(memberId) && !CollectionUtils.isEmpty(productBindMemberList) && product.getStartTime().after(new Date())) {
                            //登录后,未开始的商品，更改设置提醒,展示取消提醒
                            notice = true;
                            //重新设值秒杀库存
                            String stock = stringRedisTemplate.opsForValue().get(RedisConst.REDIS_SECKILL_PRODUCT_STOCK_PREFIX + product.getProductId());
                            if (!StringUtil.isEmpty(stock)) {
                                seckillStageProduct.setSeckillStock(Integer.parseInt(stock));
                            }
                        }
                        FrontSeckillGoodsVO vo = new FrontSeckillGoodsVO(product, notice);
                        vos.add(vo);
                    }
                });
            }
        } else {
            //查询正在进行的秒杀场次
            SeckillStageExample stageExample = new SeckillStageExample();
            stageExample.setSeckillId(seckillId);
            stageExample.setStartTimeBefore(new Date());
            stageExample.setEndTimeAfter(new Date());
            List<SeckillStage> stageList = seckillStageReadMapper.listByExample(stageExample);

            //查询秒杀商品列表
            //根据spu查询商品列表
//            String fields = "goods_id,stage_product_id,start_time,end_time,product_id,main_image,goods_name,store_id,store_name,product_price,seckill_price,seckill_stock,upper_limit,buyer_count,buy_quantity,product_stock";
            String fields = "goods_id,stage_id";
            if (!CollectionUtils.isEmpty(stageList)) {
                SeckillStageProductExample example = new SeckillStageProductExample();
                example.setLabelId(labelId);
                example.setStageId(stageList.get(0).getStageId());
                example.setVerifyState(SeckillConst.SECKILL_AUDIT_STATE_2);
                example.setGroupBy(fields);
                example.setOrderBy("goods_id desc");
                List<SeckillStageProduct> list = getSeckillStageProductFieldList(fields, example, pager);
                if (!CollectionUtils.isEmpty(list)) {
                    list.forEach(seckillStageProduct -> {
                        SeckillStageProduct product = null;
                        SeckillStageProductExample productExample = new SeckillStageProductExample();
                        productExample.setGoodsId(seckillStageProduct.getGoodsId());
                        productExample.setStageId(seckillStageProduct.getStageId());
                        List<SeckillStageProduct> productList = seckillStageProductReadMapper.listByExample(productExample);
                        if (!CollectionUtils.isEmpty(productList)) {
                            for (SeckillStageProduct stageProduct : productList) {
                                //判断哪个规格有库存，展示有库存规格，否则默认取第一个
                                if (!StringUtils.isEmpty(stageProduct.getBuyQuantity()) && stageProduct.getBuyQuantity() < stageProduct.getSeckillStock()) {
                                    product = stageProduct;
                                    break;
                                }
                            }
                            if (product == null) {
                                product = productList.get(0);
                            }
                            //重新设值秒杀库存
                            String stock = stringRedisTemplate.opsForValue().get(RedisConst.REDIS_SECKILL_PRODUCT_STOCK_PREFIX + product.getProductId());
                            if (!StringUtil.isEmpty(stock)) {
                                seckillStageProduct.setSeckillStock(Integer.parseInt(stock));
                            }
                            FrontSeckillGoodsVO vo = new FrontSeckillGoodsVO(product, false);
                            vos.add(vo);
                        }
                    });
                }
            }
        }
        return vos;
    }

    /**
     * 活动名称
     *
     * @return
     */
    @Override
    public String promotionName() {
        return "秒杀";
    }

    /**
     * 活动价格
     *
     * @param promotionId
     * @param productId
     * @return
     */
    public BigDecimal promotionPrice(Integer promotionId, Long productId) {
        //秒杀活动是否可用
        if (!isPromotionAvailable(productId.toString())) {
            return BigDecimal.ZERO;
        }
        SeckillStageProductExample example = new SeckillStageProductExample();
        example.setSeckillId(promotionId);
        example.setProductId(productId);
        List<SeckillStageProduct> list = seckillStageProductReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0).getSeckillPrice();
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
                //秒杀活动是否可用
                if (!isPromotionAvailable(orderProductInfo.getProductId().toString())) {
                    throw new MallException("秒杀活动不可用");
                }
                //构造活动信息
                OrderSubmitDTO.PromotionInfo promotionInfo = new OrderSubmitDTO.PromotionInfo();
                promotionInfo.setPromotionType(PromotionConst.PROMOTION_TYPE_104);
                promotionInfo.setPromotionId(orderProductInfo.getPromotionId().toString());
                promotionInfo.setPromotionName(this.promotionName());
                promotionInfo.setIsStore(true);
                // 获取秒杀商品信息
                SeckillStageProductExample example = new SeckillStageProductExample();
                example.setStageId(orderProductInfo.getPromotionId());
                example.setGoodsId(orderProductInfo.getGoodsId());
                example.setProductId(orderProductInfo.getProductId());
                List<SeckillStageProduct> seckillStageProductList = seckillStageProductReadMapper.listByExample(example);
                AssertUtil.notEmpty(seckillStageProductList, "获取秒杀商品信息为空，请重试！");
                SeckillStageProduct seckillStageProduct = seckillStageProductList.get(0);

                // 优惠金额
                promotionInfo.setDiscount(seckillStageProduct.getSeckillPrice());
                //订单货品添加活动
                orderProductInfo.getPromotionInfoList().add(promotionInfo);
                orderProductInfo.setProductPrice(seckillStageProduct.getSeckillPrice());
            });
        });
    }

    /**
     * 校验活动是否可用
     *
     * @param promotionId 货品ID
     * @return
     */

    @Override
    public Boolean isPromotionAvailable(String promotionId) {
        //校验秒杀开关是否开启，开启则校验活动状态
        if ("1".equals(stringRedisTemplate.opsForValue().get("seckill_is_enable"))) {
            //查询秒杀商品是否结束
            SeckillStageProductExample example = new SeckillStageProductExample();
            example.setProductId(Long.parseLong(promotionId));
            example.setStartTimeBefore(new Date());
            example.setEndTimeAfter(new Date());
            List<SeckillStageProduct> list = seckillStageProductReadMapper.listByExample(example);
            if (!CollectionUtils.isEmpty(list)) {
                //活动在有效时间内,活动可用,返回true
                return true;
            } else {
                return false;
            }
        }
        return false;
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
        AssertUtil.isTrue(CollectionUtils.isEmpty(orderProductList), "获取秒杀订单货品信息为空");
        //秒杀订单货品只有一个
        OrderProduct product = orderProductList.get(0);

        //获取秒杀商品信息
        SeckillStageProductExample example = new SeckillStageProductExample();
        example.setProductId(product.getProductId());
        List<SeckillStageProduct> seckillStageProductList = seckillStageProductReadMapper.listByExample(example);
        AssertUtil.isTrue(CollectionUtils.isEmpty(seckillStageProductList), "获取秒杀商品信息为空，请重试！");
        SeckillStageProduct seckillStageProduct = seckillStageProductList.get(0);
        //保存秒杀订单扩展表
        seckillOrderExtendModel.saveSeckillOrderExtend(orderSn, order.getPaySn(), seckillStageProduct.getStageId(), product.getProductId());
        //修改库存
        SeckillStageProduct updateSeckillStageProduct = new SeckillStageProduct();
        updateSeckillStageProduct.setStageProductId(seckillStageProduct.getStageProductId());
        updateSeckillStageProduct.setProductId(seckillStageProduct.getProductId());
        updateSeckillStageProduct.setBuyQuantity(product.getProductNum() + seckillStageProduct.getBuyQuantity());
        //TODO 购买人数需要去重
        updateSeckillStageProduct.setBuyerCount(1 + seckillStageProduct.getBuyerCount());
        return seckillStageProductWriteMapper.updateByPrimaryKeySelective(updateSeckillStageProduct);
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
        AssertUtil.isTrue(!isPromotionAvailable(promotionId.toString()), "秒杀活动不可用");
        //todo 使用redis事务
        //秒杀，先减库存
        //[检测购物车信息]接口中已做限购校验，此处不需要再处理限购
        Long decrement = stringRedisTemplate.opsForValue().decrement(RedisConst.REDIS_SECKILL_PRODUCT_STOCK_PREFIX + productId, buyNum);
        AssertUtil.notNull(decrement, "秒杀活动未开始");
        if (decrement.compareTo(0L) < 0) {
            //库存已秒杀完,将库存加回去
            stringRedisTemplate.opsForValue().increment(RedisConst.REDIS_SECKILL_PRODUCT_STOCK_PREFIX + productId, buyNum);
            //秒杀库存不足
            throw new MallException("商品已被抢光");
        }
        PreOrderDTO dto = new PreOrderDTO();
        dto.setIsCalculateDiscount(true);
        dto.setOrderType(PromotionConst.PROMOTION_TYPE_104);
        return dto;
    }

}