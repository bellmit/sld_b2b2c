package com.slodon.b2b2c.model.business;

import com.alibaba.fastjson.JSON;
import com.slodon.b2b2c.business.dto.*;
import com.slodon.b2b2c.business.example.*;
import com.slodon.b2b2c.business.pojo.*;
import com.slodon.b2b2c.core.constant.*;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.uid.GoodsIdGenerator;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.dao.read.business.OrderExtendReadMapper;
import com.slodon.b2b2c.dao.read.business.OrderProductReadMapper;
import com.slodon.b2b2c.dao.read.business.OrderReadMapper;
import com.slodon.b2b2c.dao.read.promotion.SpellGoodsReadMapper;
import com.slodon.b2b2c.dao.read.promotion.SpellTeamMemberReadMapper;
import com.slodon.b2b2c.dao.read.promotion.SpellTeamReadMapper;
import com.slodon.b2b2c.dao.write.business.CartWriteMapper;
import com.slodon.b2b2c.dao.write.business.OrderExtendWriteMapper;
import com.slodon.b2b2c.dao.write.business.OrderProductWriteMapper;
import com.slodon.b2b2c.dao.write.business.OrderWriteMapper;
import com.slodon.b2b2c.dao.write.promotion.SpellGoodsWriteMapper;
import com.slodon.b2b2c.dao.write.promotion.SpellTeamWriteMapper;
import com.slodon.b2b2c.goods.dto.CalculateExpressDTO;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.GoodsComment;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.member.pojo.MemberAddress;
import com.slodon.b2b2c.member.pojo.MemberBalanceLog;
import com.slodon.b2b2c.member.pojo.MemberIntegralLog;
import com.slodon.b2b2c.model.goods.GoodsCommentModel;
import com.slodon.b2b2c.model.goods.GoodsFreightTemplateModel;
import com.slodon.b2b2c.model.goods.GoodsModel;
import com.slodon.b2b2c.model.goods.ProductModel;
import com.slodon.b2b2c.model.member.MemberAddressModel;
import com.slodon.b2b2c.model.member.MemberBalanceLogModel;
import com.slodon.b2b2c.model.member.MemberIntegralLogModel;
import com.slodon.b2b2c.model.member.MemberModel;
import com.slodon.b2b2c.model.promotion.*;
import com.slodon.b2b2c.model.seller.StoreCommentModel;
import com.slodon.b2b2c.promotion.example.*;
import com.slodon.b2b2c.promotion.pojo.*;
import com.slodon.b2b2c.seller.example.StoreCommentExample;
import com.slodon.b2b2c.seller.pojo.StoreComment;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.starter.entity.SlodonRefundRequest;
import com.slodon.b2b2c.starter.entity.SlodonRefundResponse;
import com.slodon.b2b2c.starter.mq.entity.MemberIntegralVO;
import com.slodon.b2b2c.starter.mq.entity.MessageSendProperty;
import com.slodon.b2b2c.starter.mq.entity.MessageSendVO;
import com.slodon.b2b2c.starter.pay.SlodonPay;
import com.slodon.b2b2c.starter.redisson.SlodonLock;
import com.slodon.b2b2c.util.PayPropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

import static com.alibaba.fastjson.serializer.SerializerFeature.MapSortField;
import static com.alibaba.fastjson.serializer.SerializerFeature.SortField;
import static com.slodon.b2b2c.core.constant.StarterConfigConst.*;

@Component
@Slf4j
public class OrderModel {

    @Resource
    private OrderReadMapper orderReadMapper;
    @Resource
    private OrderWriteMapper orderWriteMapper;
    @Resource
    private OrderProductReadMapper orderProductReadMapper;
    @Resource
    private OrderProductWriteMapper orderProductWriteMapper;
    @Resource
    private CartWriteMapper cartWriteMapper;
    @Resource
    private OrderExtendReadMapper orderExtendReadMapper;
    @Resource
    private OrderExtendWriteMapper orderExtendWriteMapper;
    @Resource
    private SpellTeamReadMapper spellTeamReadMapper;
    @Resource
    private SpellTeamWriteMapper spellTeamWriteMapper;
    @Resource
    private SpellTeamMemberReadMapper spellTeamMemberReadMapper;
    @Resource
    private SpellGoodsReadMapper spellGoodsReadMapper;
    @Resource
    private SpellGoodsWriteMapper spellGoodsWriteMapper;

    @Resource
    private GoodsModel goodsModel;
    @Resource
    private ProductModel productModel;
    @Resource
    private GoodsFreightTemplateModel goodsFreightTemplateModel;
    @Resource
    private MemberModel memberModel;
    @Resource
    private MemberIntegralLogModel memberIntegralLogModel;
    @Resource
    private MemberBalanceLogModel memberBalanceLogModel;
    @Resource
    private MemberAddressModel memberAddressModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private CouponMemberModel couponMemberModel;
    @Resource
    private CouponUseLogModel couponUseLogModel;
    @Resource
    private CouponModel couponModel;
    @Resource
    private StoreCommentModel storeCommentModel;
    @Resource
    private GoodsCommentModel goodsCommentModel;
    @Resource
    private PromotionCommonModel promotionCommonModel;
    @Resource
    private SeckillOrderExtendModel seckillOrderExtendModel;
    @Resource
    private SeckillStageProductModel seckillStageProductModel;
    @Resource
    private LadderGroupModel ladderGroupFeignClient;
    @Resource
    private LadderGroupOrderExtendModel ladderGroupOrderExtendModel;
    @Resource
    private PresellOrderExtendModel presellOrderExtendModel;
    @Resource
    private PresellDepositCompensationModel presellDepositCompensationModel;
    @Resource
    private PresellGoodsModel presellGoodsModel;

    @Resource
    private OrderExtendModel orderExtendModel;
    @Resource
    private OrderLogModel orderLogModel;
    @Resource
    private OrderProductModel orderProductModel;
    @Resource
    private OrderProductExtendModel orderProductExtendModel;
    @Resource
    private OrderPayModel orderPayModel;
    @Resource
    private OrderPromotionDetailModel orderPromotionDetailModel;
    @Resource
    private OrderPromotionSendCouponModel orderPromotionSendCouponModel;
    @Resource
    private OrderPromotionSendProductModel orderPromotionSendProductModel;
    @Resource
    private PayPropertiesUtil payPropertiesUtil;
    @Resource
    private SlodonPay slodonPay;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private SlodonLock slodonLock;

    /**
     * 新增订单
     *
     * @param order
     * @return
     */
    public Integer saveOrder(Order order) {
        int count = orderWriteMapper.insert(order);
        if (count == 0) {
            throw new MallException("添加订单失败，请重试");
        }
        return count;
    }

    /**
     * 根据orderId删除订单
     *
     * @param orderId orderId
     * @return
     */
    public Integer deleteOrder(Integer orderId) {
        if (StringUtils.isEmpty(orderId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = orderWriteMapper.deleteByPrimaryKey(orderId);
        if (count == 0) {
            log.error("根据orderId：" + orderId + "删除订单失败");
            throw new MallException("删除订单失败,请重试");
        }
        return count;
    }

    /**
     * 根据orderSn更新订单
     *
     * @param order
     * @return
     */
    public Integer updateOrder(Order order) {
        if (StringUtils.isEmpty(order.getOrderSn())) {
            throw new MallException("请选择要修改的数据");
        }
        OrderExample example = new OrderExample();
        example.setOrderSn(order.getOrderSn());
        int count = orderWriteMapper.updateByExampleSelective(order, example);
        if (count == 0) {
            log.error("根据orderId：" + order.getOrderId() + "更新订单失败");
            throw new MallException("更新订单失败,请重试");
        }
        return count;
    }

    /**
     * 根据条件更新订单
     *
     * @param order
     * @return
     */
    public Integer updateOrderByExample(Order order, OrderExample example) {
        return orderWriteMapper.updateByExampleSelective(order, example);
    }

    /**
     * 根据订单号获取订单详情
     *
     * @param orderSn 订单号
     * @return
     */
    public Order getOrderByOrderSn(String orderSn) {
        OrderExample orderExample = new OrderExample();
        orderExample.setOrderSn(orderSn);
        List<Order> orderList = orderReadMapper.listByExample(orderExample);
        AssertUtil.notEmpty(orderList, "订单不存在");
        return orderList.get(0);
    }

    /**
     * 根据orderSn获取详情
     *
     * @param orderSn
     * @return
     */
    public OrderExtend getOrderExtendByOrderSn(String orderSn) {
        OrderExtendExample extendExample = new OrderExtendExample();
        extendExample.setOrderSn(orderSn);
        return orderExtendReadMapper.listByExample(extendExample).get(0);
    }

    /**
     * 根据条件获取订单列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<Order> getOrderList(OrderExample example, PagerInfo pager) {
        List<Order> orderList;
        if (pager != null) {
            pager.setRowsCount(orderReadMapper.countByExample(example));
            orderList = orderReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            orderList = orderReadMapper.listByExample(example);
        }
        return orderList;
    }

    /**
     * 根据条件获取订单字段列表
     *
     * @param fields  查询字段，逗号分隔
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<Order> getOrderFieldList(String fields, OrderExample example, PagerInfo pager) {
        List<Order> orderList;
        if (pager != null) {
            pager.setRowsCount(orderReadMapper.countGroupFieldsByExample(fields, example));
            orderList = orderReadMapper.listFieldsPageByExample(fields, example, pager.getStart(), pager.getPageSize());
        } else {
            orderList = orderReadMapper.listFieldsByExample(fields, example);
        }
        return orderList;
    }

    /**
     * 获取条件获取订单数量
     *
     * @param example 查询条件信息
     * @return
     */
    public Integer getOrderCount(OrderExample example) {
        return orderReadMapper.countByExample(example);
    }

    /**
     * 根据订单号获取订单，WithOp查询订单关联的货品列表
     *
     * @param orderSn
     * @return
     */
    public Order getOrdersWithOpByOrderSn(String orderSn) {
        Order order = getOrderByOrderSn(orderSn);
        OrderProductExample productExample = new OrderProductExample();
        productExample.setOrderSn(orderSn);
        List<OrderProduct> orderProductList = orderProductReadMapper.listByExample(productExample);
        AssertUtil.notEmpty(orderProductList, "订单有误");
        order.setOrderProductList(orderProductList);
        return order;
    }

    /**
     * 根据订单号获取修改订单是否生成电子面单状态
     *
     * @param orderSn
     * @return
     */
    public Integer updateFaceSheet(String orderSn) {
        Order order = new Order();
        order.setIsGenerateFacesheet(OrderConst.IS_GENERATE_FACE__SHEET_1);
        OrderExample orderExample = new OrderExample();
        orderExample.setOrderSn(orderSn);
        return orderWriteMapper.updateByExampleSelective(order, orderExample);
    }

    //region 提交订单

    /**
     * 提交订单,操作数据表：
     * -bz_order    存订单
     * -bz_order_extend  保存订单扩展信息
     * -bz_order_promotion_detail  保存订单活动优惠明细表
     * -bz_order_promotion_send_coupon  保存订单活动赠送优惠券表
     * -bz_order_promotion_send_product  保存订单活动赠送货品表
     * -bz_order_log 记录订单日志
     * -member 减积分;member_integral_log 记录积分使用日志
     * -promotion_coupon_member 扣优惠券；promotion_coupon_use_log 记录优惠券使用日志；promotion_coupon 统计优惠券使用量
     * <p>
     * -bz_order_product 保存订单货品
     * -bz_order_product_extend 保存订单货品扩展
     * -product、goods    减库存
     * <p>
     * -bz_order_product 保存完订单货品之后，保存活动赠品
     * <p>
     * -bz_order_pay 支付信息
     * -bz_cart        删购物车
     *
     * @param orderSubmitDTO 计算优惠后的提交订单信息
     * @param member         会员信息
     * @param consumerDTO    前端提交订单参数
     * @return
     */
    @Transactional
    public Set<String> submitOrder(OrderSubmitDTO orderSubmitDTO, Member member, OrderSubmitMqConsumerDTO consumerDTO) {
        //记录此次提交获取的所有锁，在事务提交后统一释放
        Set<String> lockSet = new HashSet<>();
        try {
            //父订单号，一批订单共有一个
            String pOrderSn = GoodsIdGenerator.getOrderSn();
            //支付单号，一批订单共有一个
            String paySn = consumerDTO.getPaySn();
            OrderSubmitParamDTO orderSubmitParamDTO = consumerDTO.getParamDTO();
            //收货地址
            MemberAddress memberAddress = memberAddressModel.getMemberAddressByAddressId(orderSubmitParamDTO.getAddressId());
            //总支付金额，含运费
            BigDecimal payAmount = new BigDecimal("0.00");

            List<Order> orderList = new ArrayList<>();//插入的订单列表
            for (OrderSubmitDTO.OrderInfo orderInfo : orderSubmitDTO.getOrderInfoList()) {//每次循环为一个店铺的订单
                //-bz_order    存订单
                Order order = this.insertOrder(pOrderSn, paySn, orderInfo, member, memberAddress);
                String orderSn = order.getOrderSn();
                payAmount = payAmount.add(order.getOrderAmount());

                //-bz_order_extend  订单扩展信息
                orderExtendModel.insertOrderExtend(orderSn, orderInfo, memberAddress, orderSubmitParamDTO);

                //-bz_order_promotion_detail  保存订单活动优惠明细表
                orderPromotionDetailModel.insertOrderPromotionDetails(orderInfo, orderSn);

                //-bz_order_promotion_send_coupon  保存订单活动赠送优惠券表
                orderPromotionSendCouponModel.insertOrderPromotionSendCoupons(orderInfo, orderSn);

                //-bz_order_promotion_send_product  保存订单活动赠送货品表
                orderPromotionSendProductModel.insertOrderPromotionSendProducts(orderInfo, orderSn);

                //-bz_order_log 记录订单日志
                orderLogModel.insertOrderLog(OrderConst.LOG_ROLE_MEMBER, Long.valueOf(member.getMemberId()),
                        member.getMemberName(), orderSn, OrderConst.ORDER_STATE_10, "用户提交订单");

                //-member 减积分;member_integral_log 记录积分使用日志
                this.reduceMemberIntegral(member.getMemberId(), orderInfo.getIntegral(), orderSn);

                //-promotion_coupon_member 扣优惠券；promotion_coupon_use_log 记录优惠券使用日志；promotion_coupon 统计优惠券使用量(店铺优惠券)
                this.deductMemberCoupon(member, orderInfo.getVoucherCode(), orderSn, orderInfo.getStoreId());

                orderInfo.getOrderProductInfoList().forEach(orderProductInfo -> {//每次循环为一个订单货品

                    //锁名称
                    String lockKey = "lock-" + orderProductInfo.getProductId();
                    lockSet.add(lockKey);
                    slodonLock.lock(lockKey);//获取分布式锁

                    //product信息
                    Product productDb = productModel.getProductByProductId(orderProductInfo.getProductId());
                    //商品信息
                    Goods goodsDb = goodsModel.getGoodsByGoodsId(orderProductInfo.getGoodsId());

                    //-bz_order_product 保存订单货品
                    Long orderProductId = orderProductModel.insertOrderProduct(orderSn, orderProductInfo, goodsDb, orderProductInfo.getSpellTeamId());

                    if (!CollectionUtils.isEmpty(orderProductInfo.getPromotionInfoList())) { //有活动优惠
                        orderProductInfo.getPromotionInfoList().forEach(promotionInfo -> {
                            //-bz_order_product_extend 保存订单货品扩展
                            orderProductExtendModel.insertOrderProductExtend(orderSn, orderProductId, promotionInfo);
                        });
                    }

                    //-product、goods    减库存
                    this.reduceStock(goodsDb, productDb, orderProductInfo.getBuyNum());

                });
                //活动特殊处理
                if (!StringUtil.isNullOrZero(orderInfo.getPromotionType())
                        && promotionCommonModel.specialOrder(orderInfo.getPromotionType())) {
                    promotionCommonModel.submitPromotionOrder(orderInfo.getPromotionType(), orderSn);
                }

                //-bz_order_product 保存完订单货品之后，保存活动赠品
                orderProductModel.insertSendOrderProduct(orderSn, orderInfo, member.getMemberId());

                orderList.add(order);
                //消息通知
                List<MessageSendProperty> messageSendPropertyList = new ArrayList<>();
                messageSendPropertyList.add(new MessageSendProperty("orderSn", orderSn));
                String msgLinkInfo = "{\"orderSn\":\"" + orderSn + "\",\"type\":\"order_news\"}";
                MessageSendVO messageSendVO = new MessageSendVO(messageSendPropertyList, null, order.getStoreId(), StoreTplConst.NEW_ORDER_REMINDER, msgLinkInfo);
                //发送到mq
                rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_SELLER_MSG, messageSendVO);
            }

            //-promotion_coupon_member 扣优惠券；promotion_coupon_use_log 记录优惠券使用日志；promotion_coupon 统计优惠券使用量(平台优惠券)
            this.deductMemberCoupon(member, orderSubmitDTO.getCouponCode(), pOrderSn, 0L);

            //-bz_order_pay 支付信息
            orderPayModel.insertOrderPay(pOrderSn, paySn, payAmount, member.getMemberId());

            if (orderSubmitParamDTO.getIsCart()) {
                //-bz_cart  删购物车
                CartExample cartExample = new CartExample();
                cartExample.setMemberId(member.getMemberId());
                cartExample.setIsChecked(CartConst.IS_CHECKED_YES);
                cartWriteMapper.deleteByExample(cartExample);
            }
            //订单金额为0时，直接支付成功
            orderList.forEach(order -> {
                //如果订单金额为0
                if (order.getOrderAmount().compareTo(BigDecimal.ZERO) == 0) {
                    //订单已支付
                    orderPayModel.orderPaySuccess(order.getOrderSn(), order.getPaySn(), null, order.getBalanceAmount(), order.getPayAmount(),
                            order.getPaymentCode(), order.getPaymentName(), order.getOrderType(), member.getMemberId(), member.getMemberName());
                }
            });
        } catch (Exception e) {
            //发生异常，释放此次获取的所有锁
            lockSet.forEach(lockName -> {
                slodonLock.unlock(lockName);
            });
            throw e;
        }

        return lockSet;
    }

    /**
     * 提交尾款订单
     *
     * @param orderSubmitDTO
     * @param orderSubmitParamDTO
     * @param member
     * @param orderDb
     * @return
     */
    @Transactional
    public boolean submitBalanceOrder(OrderSubmitDTO orderSubmitDTO, OrderSubmitParamDTO orderSubmitParamDTO, Member member, Order orderDb) {
        //收货地址
        MemberAddress memberAddress = memberAddressModel.getMemberAddressByAddressId(orderSubmitParamDTO.getAddressId());
        //总支付金额，含运费
        BigDecimal payAmount = new BigDecimal("0.00");

        for (OrderSubmitDTO.OrderInfo orderInfo : orderSubmitDTO.getOrderInfoList()) {//每次循环为一个店铺的订单
            //-bz_order    存订单
            Order order = this.updateBalanceOrder(orderDb, orderInfo, memberAddress);
            String orderSn = orderDb.getOrderSn();
            payAmount = payAmount.add(order.getOrderAmount());

            //-bz_order_extend  订单扩展信息
            orderExtendModel.updateOrderExtend(orderSn, orderInfo, memberAddress, orderSubmitParamDTO);

            //-bz_order_promotion_detail  保存订单活动优惠明细表
            orderPromotionDetailModel.insertOrderPromotionDetails(orderInfo, orderSn);

            //-bz_order_promotion_send_coupon  保存订单活动赠送优惠券表
            orderPromotionSendCouponModel.insertOrderPromotionSendCoupons(orderInfo, orderSn);

            //-bz_order_promotion_send_product  保存订单活动赠送货品表
            orderPromotionSendProductModel.insertOrderPromotionSendProducts(orderInfo, orderSn);

            //-member 减积分;member_integral_log 记录积分使用日志
            this.reduceMemberIntegral(member.getMemberId(), orderInfo.getIntegral(), orderSn);

            //-promotion_coupon_member 扣优惠券；promotion_coupon_use_log 记录优惠券使用日志；promotion_coupon 统计优惠券使用量(店铺优惠券)
            this.deductMemberCoupon(member, orderInfo.getVoucherCode(), orderSn, orderInfo.getStoreId());

            orderInfo.getOrderProductInfoList().forEach(orderProductInfo -> {//每次循环为一个订单货品

                //修改订单货品信息
                Long orderProductId = orderProductModel.updateBalanceOrderProduct(orderSn, orderProductInfo);

                if (!CollectionUtils.isEmpty(orderProductInfo.getPromotionInfoList())) { //有活动优惠
                    orderProductInfo.getPromotionInfoList().forEach(promotionInfo -> {
                        //-bz_order_product_extend 保存订单货品扩展
                        orderProductExtendModel.insertOrderProductExtend(orderSn, orderProductId, promotionInfo);
                    });
                }
            });

            //-bz_order_product 保存完订单货品之后，保存活动赠品
            orderProductModel.insertSendOrderProduct(orderSn, orderInfo, member.getMemberId());
        }

        //-promotion_coupon_member 扣优惠券；promotion_coupon_use_log 记录优惠券使用日志；promotion_coupon 统计优惠券使用量(平台优惠券)
        this.deductMemberCoupon(member, orderSubmitDTO.getCouponCode(), orderDb.getOrderSn(), 0L);

        return true;
    }

    /**
     * 提交订单-保存订单
     *
     * @param pOrderSn      父订单号
     * @param paySn         支付单号
     * @param orderInfo     订单信息
     * @param member        会员信息
     * @param memberAddress 收货地址
     * @return 订单号
     */
    private Order insertOrder(String pOrderSn, String paySn, OrderSubmitDTO.OrderInfo orderInfo, Member member, MemberAddress memberAddress) {
        //订单号
        String orderSn = GoodsIdGenerator.getOrderSn();

        //计算运费
        //构造计算运费的dto
        CalculateExpressDTO calculateExpressDTO = new CalculateExpressDTO();
        calculateExpressDTO.setCityCode(memberAddress.getCityCode());
        orderInfo.getOrderProductInfoList().forEach(orderProductInfo -> {
            calculateExpressDTO.getProductList()
                    .add(new CalculateExpressDTO.ProductInfo(
                            orderProductInfo.getGoodsId(),
                            orderProductInfo.getProductId(),
                            orderProductInfo.getProductPrice(),
                            orderProductInfo.getBuyNum()));
        });
        BigDecimal expressFee = goodsFreightTemplateModel.calculateExpressFee(calculateExpressDTO);

        Order order = new Order();
        order.setOrderSn(orderSn);
        order.setPaySn(paySn);
        order.setParentSn(pOrderSn);
        order.setStoreId(orderInfo.getStoreId());
        order.setStoreName(orderInfo.getStoreName());
        order.setMemberName(member.getMemberName());
        order.setMemberId(member.getMemberId());
        order.setCreateTime(new Date());
        order.setOrderState(OrderConst.ORDER_STATE_10);
        order.setPaymentName(OrderPaymentConst.PAYMENT_NAME_ONLINE);
        order.setPaymentCode(OrderPaymentConst.PAYMENT_CODE_ONLINE);
        order.setGoodsAmount(orderInfo.getGoodsAmount());
        order.setExpressFee(expressFee);
        order.setActivityDiscountAmount(orderInfo.getTotalDiscount());
        //活动优惠明细
        List<OrderSubmitDTO.PromotionInfo> promotionInfoList = orderInfo.getPromotionInfoList();
        if (!CollectionUtils.isEmpty(promotionInfoList)) {
            order.setActivityDiscountDetail(JSON.toJSONString(orderInfo.getPromotionInfoList(), SortField, MapSortField));
        }
        //预售和阶梯团提交订单时定金不计算运费，付尾款时计算
        if (orderInfo.getPromotionType() == null || (!orderInfo.getPromotionType().equals(PromotionConst.PROMOTION_TYPE_103)
                && !orderInfo.getPromotionType().equals(PromotionConst.PROMOTION_TYPE_105))) {
            order.setOrderAmount(orderInfo.getTotalAmount().add(expressFee));
        } else {
            order.setOrderAmount(orderInfo.getTotalAmount());
        }
        order.setIntegral(orderInfo.getIntegral());
        order.setIntegralCashAmount(orderInfo.getIntegralCashAmount());
        order.setReceiverName(memberAddress.getMemberName());
        order.setReceiverAreaInfo(memberAddress.getAddressAll());
        order.setReceiverAddress(memberAddress.getDetailAddress());
        order.setReceiverMobile(memberAddress.getTelMobile());
        order.setOrderType(orderInfo.getOrderType());

        this.saveOrder(order);
        return order;
    }

    /**
     * 修改尾款订单
     *
     * @param orderDb
     * @param orderInfo
     * @param memberAddress
     * @return
     */
    private Order updateBalanceOrder(Order orderDb, OrderSubmitDTO.OrderInfo orderInfo, MemberAddress memberAddress) {
        Order order = new Order();
        order.setOrderSn(orderDb.getOrderSn());
        order.setActivityDiscountAmount(orderInfo.getTotalDiscount());
        //活动优惠明细
        List<OrderSubmitDTO.PromotionInfo> promotionInfoList = orderInfo.getPromotionInfoList();
        if (!CollectionUtils.isEmpty(promotionInfoList)) {
            order.setActivityDiscountDetail(JSON.toJSONString(orderInfo.getPromotionInfoList(), SortField, MapSortField));
            //没有活动优惠就不做改动
            order.setGoodsAmount(orderInfo.getGoodsAmount());
            order.setOrderAmount(orderInfo.getTotalAmount().add(orderDb.getExpressFee()));
        } else {
            //默认金额
            order.setGoodsAmount(orderDb.getGoodsAmount());
            order.setOrderAmount(orderDb.getOrderAmount());
        }
        order.setIntegral(orderInfo.getIntegral());
        order.setIntegralCashAmount(orderInfo.getIntegralCashAmount());
        order.setReceiverName(memberAddress.getMemberName());
        order.setReceiverAreaInfo(memberAddress.getAddressAll());
        order.setReceiverAddress(memberAddress.getDetailAddress());
        order.setReceiverMobile(memberAddress.getTelMobile());

        this.updateOrder(order);
        return order;
    }

    /**
     * 提交订单-更新商品库存
     *
     * @param goodsDb   商品
     * @param productDb 货品
     * @param reduceNum 购买数量
     */
    private void reduceStock(Goods goodsDb, Product productDb, Integer reduceNum) {
        //减sku库存
        Product updateProduct = new Product();
        updateProduct.setProductId(productDb.getProductId());
        updateProduct.setProductStock(productDb.getProductStock() - reduceNum);
        productModel.updateProduct(updateProduct);

        if (goodsDb.getDefaultProductId().equals(productDb.getProductId())) {
            //此次购买的是默认货品，更新商品库存
            Goods updateGoods = new Goods();
            updateGoods.setGoodsId(goodsDb.getGoodsId());
            updateGoods.setGoodsStock(updateProduct.getProductStock());
            updateGoods.setUpdateTime(new Date());
            goodsModel.updateGoods(goodsDb);
        }
    }

    /**
     * 提交订单-扣除用户使用积分，记录积分日志
     *
     * @param memberId 会员id
     * @param integral 使用积分数
     * @param orderSn  订单号
     */
    private void reduceMemberIntegral(Integer memberId, Integer integral, String orderSn) {
        if (!StringUtil.isNullOrZero(integral)) {
            //查询会员信息,此处不能用登录缓存的会员信息，需要查询最新的积分数量
            Member memberDb = memberModel.getMemberByMemberId(memberId);
            //更新会员积分
            Member updateMember = new Member();
            updateMember.setMemberId(memberDb.getMemberId());
            updateMember.setMemberIntegral(memberDb.getMemberIntegral() - integral);
            updateMember.setUpdateTime(new Date());
            memberModel.updateMember(updateMember);

            //记录积分日志
            MemberIntegralLog memberIntegralLog = new MemberIntegralLog();
            memberIntegralLog.setMemberId(memberDb.getMemberId());
            memberIntegralLog.setMemberName(memberDb.getMemberName());
            memberIntegralLog.setValue(integral);
            memberIntegralLog.setCreateTime(new Date());
            memberIntegralLog.setType(MemberIntegralLogConst.TYPE_7);
            memberIntegralLog.setDescription("商品下单使用积分，订单号：" + orderSn + "，使用积分数量：" + integral);
            memberIntegralLog.setRefCode(orderSn);
            memberIntegralLog.setOptId(memberDb.getMemberId());
            memberIntegralLog.setOptName(memberDb.getMemberName());
            memberIntegralLogModel.saveMemberIntegralLog(memberIntegralLog);

            //发送积分变动消息通知
            this.sendMsgIntegralChange(updateMember, "下单支付");
        }
    }

    /**
     * 发送积分变动消息通知
     *
     * @param member 会员信息
     */
    public void sendMsgIntegralChange(Member member, String description) {
        //消息通知
        List<MessageSendProperty> messageSendPropertyList = new ArrayList<>();
        messageSendPropertyList.add(new MessageSendProperty("description", description));
        messageSendPropertyList.add(new MessageSendProperty("availableIntegral", member.getMemberIntegral().toString()));
        //微信消息通知
        List<MessageSendProperty> messageSendPropertyList4Wx = new ArrayList<>();
        messageSendPropertyList4Wx.add(new MessageSendProperty("first", "您的账户发生了积分变动。"));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword1", TimeUtil.getDateTimeString(new Date())));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword2", description));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword3", member.getMemberIntegral().toString()));
        String msgLinkInfo = "{\"type\":\"integral_change\"}";
        MessageSendVO messageSendVO = new MessageSendVO(messageSendPropertyList, messageSendPropertyList4Wx, "changeTime", member.getMemberId(), MemberTplConst.INTEGRAL_CHANGE_REMINDER, msgLinkInfo);
        //发送到mq
        rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_MEMBER_MSG, messageSendVO);
    }

    /**
     * 发送余额变动消息通知
     *
     * @param memberBalanceLog 余额变动信息
     */
    public void sendMsgAccountChange(MemberBalanceLog memberBalanceLog) {
        //消息通知
        List<MessageSendProperty> messageSendPropertyList = new ArrayList<>();
        messageSendPropertyList.add(new MessageSendProperty("description", "取消订单返还余额"));
        messageSendPropertyList.add(new MessageSendProperty("availableBalance", memberBalanceLog.getAfterChangeAmount().toString()));
        messageSendPropertyList.add(new MessageSendProperty("frozenBalance", memberBalanceLog.getFreezeAmount().toString()));
        //微信消息通知
        List<MessageSendProperty> messageSendPropertyList4Wx = new ArrayList<>();
        messageSendPropertyList4Wx.add(new MessageSendProperty("first", "您的账户发生了资金变动。"));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword1", "取消订单返还余额"));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword2", memberBalanceLog.getChangeValue().toString()));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword3", TimeUtil.getDateTimeString(memberBalanceLog.getCreateTime())));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword4", memberBalanceLog.getAfterChangeAmount().toString()));
        String msgLinkInfo = "{\"type\":\"balance_change\"}";
        MessageSendVO messageSendVO = new MessageSendVO(messageSendPropertyList, messageSendPropertyList4Wx, "changeTime", memberBalanceLog.getMemberId(), MemberTplConst.BALANCE_CHANGE_REMINDER, msgLinkInfo);
        //发送到mq
        rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_MEMBER_MSG, messageSendVO);
    }

    /**
     * 提交订单-扣除用户优惠券，记录优惠券使用日志，统计优惠券使用数量
     *
     * @param memberDb   会员信息
     * @param couponCode 优惠券编码
     * @param orderSn    订单号
     * @param storeId    店铺id
     */
    private void deductMemberCoupon(Member memberDb, String couponCode, String orderSn, Long storeId) {
        if (!StringUtils.isEmpty(couponCode)) {
            //查询用户优惠券
            CouponMemberExample couponMemberExample = new CouponMemberExample();
            couponMemberExample.setMemberId(memberDb.getMemberId());
            couponMemberExample.setCouponCode(couponCode);
            CouponMember couponMemberDb = couponMemberModel.getCouponMemberList(couponMemberExample, null).get(0);

            //更新优惠券使用状态
            CouponMember updateCouponMember = new CouponMember();
            updateCouponMember.setCouponMemberId(couponMemberDb.getCouponMemberId());
            updateCouponMember.setUseState(CouponConst.USE_STATE_2);
            updateCouponMember.setUseTime(new Date());
            updateCouponMember.setOrderSn(orderSn);
            couponMemberModel.updateCouponMember(updateCouponMember);

            //记录优惠券使用日志
            CouponUseLog couponUseLog = new CouponUseLog();
            couponUseLog.setCouponCode(couponCode);
            couponUseLog.setMemberId(memberDb.getMemberId());
            couponUseLog.setMemberName(memberDb.getMemberName());
            couponUseLog.setOrderSn(orderSn);
            couponUseLog.setStoreId(storeId);
            couponUseLog.setLogType(CouponConst.LOG_TYPE_2);
            couponUseLog.setLogTime(new Date());
            couponUseLog.setLogContent("商品下单使用优惠券，订单号：" + orderSn);
            couponUseLogModel.saveCouponUseLog(couponUseLog);

            //统计优惠券使用数量
            Coupon couponDb = couponModel.getCouponByCouponId(couponMemberDb.getCouponId());
            Coupon updateCoupon = new Coupon();
            updateCoupon.setCouponId(couponDb.getCouponId());
            updateCoupon.setUsedNum(couponDb.getUsedNum() + 1);
            couponModel.updateCoupon(updateCoupon);
        }
    }
    //endregion

    //region 订单支付

    //endregion

    //region 取消订单

    /**
     * 取消订单（整单取消）
     * 1.修改订单状态
     * 2.记录订单日志
     * 3.返还订单使用的积分，记录积分日志
     * 4.返还使用的店铺优惠券，记录优惠券日志
     * 5.返还使用的平台优惠券，记录优惠券日志（最后一个订单才退）
     * 6.增加货品库存，增加商品库存，秒杀订单增加redis中的秒杀库存
     * 7.处理用户余额，记录余额日志
     *
     * @param orderList    取消的订单列表
     * @param cancelReason 取消原因
     * @param optRole      操作角色{@link OrderConst#LOG_ROLE_ADMIN}
     * @param optUserId    操作人id
     * @param optUserName  操作人名称
     * @param optRemark    操作备注
     */
    @Transactional
    public void cancelOrder(List<Order> orderList, String cancelReason, String cancelRemark, Integer optRole, Long optUserId, String optUserName, String optRemark) {
        //预售定金赔偿倍数
        String value = stringRedisTemplate.opsForValue().get("presale_compensate");
        int compensateValue = StringUtil.isEmpty(value) ? 0 : Integer.parseInt(value);
        orderList.forEach(orderDb -> {
            //1.修改订单状态
            Order updateOrder = new Order();
            updateOrder.setOrderState(OrderConst.ORDER_STATE_0);
            updateOrder.setRefuseReason(cancelReason);
            updateOrder.setRefuseRemark(cancelRemark);
            OrderExample orderExample = new OrderExample();
            orderExample.setOrderSn(orderDb.getOrderSn());
            int count = orderWriteMapper.updateByExampleSelective(updateOrder, orderExample);
            AssertUtil.isTrue(count == 0, "取消订单失败");

            //2.记录订单日志
            orderLogModel.insertOrderLog(optRole, optUserId, optUserName, orderDb.getOrderSn(), OrderConst.ORDER_STATE_0, optRemark);

            //3.返还订单使用的积分，记录积分日志
            this.orderCancelReturnIntegral(orderDb, optUserId, optUserName);

            //4.返还使用的店铺优惠券，记录优惠券日志
            this.orderCancelReturnStoreCoupon(orderDb);

            //6.增加货品库存，增加商品库存
            this.orderCancelAddGoodsStock(orderDb.getOrderSn(), orderDb.getOrderType());

            //7.处理用户余额，记录余额日志
            if (orderDb.getOrderType() == PromotionConst.PROMOTION_TYPE_103) {
                //预售处理用户余额
                this.preSellOrderCancelDeal(orderDb, optRole, optUserId, optUserName, compensateValue);
            } else if (orderDb.getOrderType() == PromotionConst.PROMOTION_TYPE_105) {
                //阶梯团处理用户余额
                this.ladderGroupOrderCancelDealMemberBalance(orderDb, optRole, optUserId, optUserName);
            } else {
                this.orderCancelDealMemberBalance(orderDb, optRole, optUserId, optUserName);
            }
        });

        //5.返还使用的平台优惠券，记录优惠券日志
        this.orderCancelReturnPlatformCoupon(orderList);

    }

    /**
     * 取消订单返还订单使用的积分，记录积分日志
     *
     * @param orderDb     订单
     * @param optUserId   操作人id
     * @param optUserName 操作人名称
     */
    private void orderCancelReturnIntegral(Order orderDb, Long optUserId, String optUserName) {
        if (StringUtil.isNullOrZero(orderDb.getIntegral())) {
            //未使用积分
            return;
        }
        //查询会员信息
        Member memberDb = memberModel.getMemberByMemberId(orderDb.getMemberId());

        //更新积分数量
        Member updateMember = new Member();
        updateMember.setMemberId(memberDb.getMemberId());
        updateMember.setMemberIntegral(memberDb.getMemberIntegral() + orderDb.getIntegral());
        memberModel.updateMember(updateMember);

        //记录积分日志
        MemberIntegralLog memberIntegralLog = new MemberIntegralLog();
        memberIntegralLog.setMemberId(memberDb.getMemberId());
        memberIntegralLog.setMemberName(memberDb.getMemberName());
        memberIntegralLog.setValue(orderDb.getIntegral());
        memberIntegralLog.setCreateTime(new Date());
        memberIntegralLog.setType(MemberIntegralLogConst.TYPE_11);
        memberIntegralLog.setDescription("订单取消退回积分，订单号：" + orderDb.getOrderSn());
        memberIntegralLog.setRefCode(orderDb.getOrderSn());
        memberIntegralLog.setOptId(Math.toIntExact(optUserId));
        memberIntegralLog.setOptName(optUserName);
        memberIntegralLogModel.saveMemberIntegralLog(memberIntegralLog);
    }

    /**
     * 订单取消返还使用的店铺优惠券，记录优惠券日志
     *
     * @param orderDb
     */
    private void orderCancelReturnStoreCoupon(Order orderDb) {
        //查询订单使用的优惠券
        OrderPromotionDetailExample example = new OrderPromotionDetailExample();
        example.setOrderSn(orderDb.getOrderSn());
        example.setPromotionType(PromotionConst.PROMOTION_TYPE_402);
        example.setIsStore(OrderConst.IS_STORE_PROMOTION_YES);
        List<OrderPromotionDetail> orderPromotionDetailList = orderPromotionDetailModel.getOrderPromotionDetailList(example, null);
        if (CollectionUtils.isEmpty(orderPromotionDetailList)) {
            //未使用优惠券
            return;
        }
        orderPromotionDetailList.forEach(orderPromotionDetail -> {
            this.doReturnCoupon(orderPromotionDetail.getPromotionId(), orderPromotionDetail.getOrderSn());
        });
    }

    /**
     * 订单取消返还使用的平台优惠券，记录优惠券日志（最后一个订单才退）
     *
     * @param orderList 订单列表
     */
    private void orderCancelReturnPlatformCoupon(List<Order> orderList) {
        //同一张优惠券记录多次，只返还一次
        Map<String/*优惠券编码*/, OrderPromotionDetail/*订单活动优惠*/> mapByCouponCode = new HashMap<>();
        orderList.forEach(orderDb -> {
            OrderPromotionDetailExample example = new OrderPromotionDetailExample();
            example.setOrderSn(orderDb.getOrderSn());
            example.setPromotionType(PromotionConst.PROMOTION_TYPE_402);
            example.setIsStore(OrderConst.IS_STORE_PROMOTION_NO);
            List<OrderPromotionDetail> orderPromotionDetailList = orderPromotionDetailModel.getOrderPromotionDetailList(example, null);
            if (!CollectionUtils.isEmpty(orderPromotionDetailList)) {
                orderPromotionDetailList.forEach(orderPromotionDetail -> {
                    if (!mapByCouponCode.containsKey(orderPromotionDetail.getPromotionId())) {
                        //不同的优惠券编码，放入map
                        mapByCouponCode.put(orderPromotionDetail.getPromotionId(), orderPromotionDetail);
                    }
                    //重复的优惠券编码，不做任何处理
                });
            }
        });
        if (CollectionUtils.isEmpty(mapByCouponCode)) {
            //没有使用平台优惠券
            return;
        }
        mapByCouponCode.forEach((couponCode, orderPromotionDetail) -> {
            this.doReturnCoupon(couponCode, orderPromotionDetail.getOrderSn());
        });
    }

    /**
     * 返还优惠券，记录优惠券日志
     *
     * @param couponCode 优惠券编码
     * @param orderSn    订单号
     */
    public void doReturnCoupon(String couponCode, String orderSn) {
        //获取优惠券code，修改使用状态
        CouponMemberExample couponMemberExample = new CouponMemberExample();
        couponMemberExample.setCouponCode(couponCode);
        List<CouponMember> couponMemberList = couponMemberModel.getCouponMemberList(couponMemberExample, null);
        AssertUtil.notEmpty(couponMemberList, "用户优惠券不存在");
        CouponMember couponMemberDb = couponMemberList.get(0);
        if (!couponMemberDb.getUseState().equals(CouponConst.USE_STATE_2)) {
            //优惠券使用状态不等于已使用，可能是其他情况已经退还了优惠券，不再记录
            return;
        }
        CouponMember updateCouponMember = new CouponMember();
        updateCouponMember.setCouponMemberId(couponMemberDb.getCouponMemberId());
        updateCouponMember.setUseState(CouponConst.USE_STATE_1);//修改为未使用
        couponMemberModel.updateCouponMember(updateCouponMember);

        //修改优惠券使用数量
        Coupon couponDb = couponModel.getCouponByCouponId(couponMemberDb.getCouponId());
        Coupon updateCoupon = new Coupon();
        updateCoupon.setCouponId(couponDb.getCouponId());
        updateCoupon.setUsedNum(couponDb.getUsedNum() - 1);
        couponModel.updateCoupon(updateCoupon);

        //记录优惠券使用日志
        CouponUseLog couponUseLog = new CouponUseLog();
        couponUseLog.setCouponCode(couponMemberDb.getCouponCode());
        couponUseLog.setMemberId(couponMemberDb.getMemberId());
        couponUseLog.setMemberName(couponMemberDb.getMemberName());
        couponUseLog.setStoreId(couponMemberDb.getStoreId());
        couponUseLog.setOrderSn(orderSn);
        couponUseLog.setLogType(CouponConst.LOG_TYPE_3);//订单取消返回
        couponUseLog.setLogContent("订单取消返还优惠券，订单号：" + orderSn);
        couponUseLog.setLogTime(new Date());
        couponUseLogModel.saveCouponUseLog(couponUseLog);
    }

    /**
     * 订单取消-增加货品库存，增加商品库存
     *
     * @param orderSn
     * @param orderType 订单类型，1-普通订单，其他为活动类型
     */
    private void orderCancelAddGoodsStock(String orderSn, Integer orderType) {
        //查询订单货品
        OrderProductExample orderProductExample = new OrderProductExample();
        orderProductExample.setOrderSn(orderSn);
        List<OrderProduct> orderProductList = orderProductModel.getOrderProductList(orderProductExample, null);
        orderProductList.forEach(orderProduct -> {
            //查询现有库存
            Product productDb = productModel.getProductByProductId(orderProduct.getProductId());
            //更新sku库存
            Product updateProduct = new Product();
            updateProduct.setProductId(productDb.getProductId());
            updateProduct.setProductStock(productDb.getProductStock() + orderProduct.getProductNum());
            productModel.updateProduct(updateProduct);

            if (orderType.equals(PromotionConst.PROMOTION_TYPE_102)) {
                //拼团订单，修改redis中的已购买数量
                String stockKey = RedisConst.SPELL_PURCHASED_NUM_PREFIX + productDb.getGoodsId() + "_" + orderProduct.getMemberId();
                if (stringRedisTemplate.opsForValue().get(stockKey) != null) {
                    stringRedisTemplate.opsForValue().decrement(stockKey, orderProduct.getProductNum());
                }
                //查询拼团订单扩展信息
                SpellTeamMemberExample teamMemberExample = new SpellTeamMemberExample();
                teamMemberExample.setOrderSn(orderSn);
                List<SpellTeamMember> orderExtendList = spellTeamMemberReadMapper.listByExample(teamMemberExample);
                AssertUtil.notEmpty(orderExtendList, "获取拼团活动信息为空");
                SpellTeamMember orderExtend = orderExtendList.get(0);
                //查询拼团商品信息
                SpellGoods spellGoods = spellGoodsReadMapper.getByPrimaryKey(orderExtend.getSpellGoodsId());
                AssertUtil.notNull(spellGoods, "拼团商品不存在");
                //修改拼团商品库存
                SpellGoods spellGoodsNew = new SpellGoods();
                spellGoodsNew.setSpellGoodsId(spellGoods.getSpellGoodsId());
                spellGoodsNew.setSpellStock(spellGoods.getSpellStock() + orderProduct.getProductNum());
                spellGoodsWriteMapper.updateByPrimaryKeySelective(spellGoodsNew);
            }
            if (orderType.equals(PromotionConst.PROMOTION_TYPE_103)) {
                //预售订单，修改redis中的已购买数量
                String stockKey = RedisConst.PRE_SELL_PURCHASED_NUM_PREFIX + productDb.getGoodsId() + "_" + orderProduct.getMemberId();
                if (stringRedisTemplate.opsForValue().get(stockKey) != null) {
                    stringRedisTemplate.opsForValue().decrement(stockKey, orderProduct.getProductNum());
                }
                //查询预售订单扩展信息
                PresellOrderExtendExample orderExtendExample = new PresellOrderExtendExample();
                orderExtendExample.setOrderSn(orderSn);
                List<PresellOrderExtend> orderExtendList = presellOrderExtendModel.getPresellOrderExtendList(orderExtendExample, null);
                AssertUtil.notEmpty(orderExtendList, "获取预售订单扩展信息为空");
                PresellOrderExtend orderExtend = orderExtendList.get(0);
                //查询预售商品信息
                PresellGoodsExample goodsExample = new PresellGoodsExample();
                goodsExample.setPresellId(orderExtend.getPresellId());
                goodsExample.setProductId(orderExtend.getProductId());
                List<PresellGoods> goodsList = presellGoodsModel.getPresellGoodsList(goodsExample, null);
                AssertUtil.notEmpty(goodsList, "预售商品不存在");
                PresellGoods presellGoods = goodsList.get(0);
                //修改预售商品库存
                PresellGoods presellGoodsNew = new PresellGoods();
                presellGoodsNew.setPresellGoodsId(presellGoods.getPresellGoodsId());
                presellGoodsNew.setPresellStock(presellGoods.getPresellStock() + orderProduct.getProductNum());
                presellGoodsModel.updatePresellGoods(presellGoodsNew);
            }
            if (orderType.equals(PromotionConst.PROMOTION_TYPE_104)) {
                //秒杀订单，修改redis中的秒杀库存
                String stockKey = RedisConst.REDIS_SECKILL_PRODUCT_STOCK_PREFIX + productDb.getProductId();
                String memberLimitKey = RedisConst.REDIS_SECKILL_MEMBER_BUY_NUM_PREFIX + productDb.getProductId() + "_" + orderProduct.getMemberId();
                if (stringRedisTemplate.opsForValue().get(stockKey) != null) {
                    //加库存
                    stringRedisTemplate.opsForValue().increment(stockKey, orderProduct.getProductNum());
                }
                if (stringRedisTemplate.opsForValue().get(memberLimitKey) != null) {
                    stringRedisTemplate.opsForValue().decrement(memberLimitKey, orderProduct.getProductNum());
                }
                //查询秒杀订单扩展信息
                SeckillOrderExtendExample extendExample = new SeckillOrderExtendExample();
                extendExample.setOrderSn(orderSn);
                List<SeckillOrderExtend> seckillOrderExtendList = seckillOrderExtendModel.getSeckillOrderExtendList(extendExample, null);
                AssertUtil.notEmpty(seckillOrderExtendList, "秒杀订单扩展信息不存在");
                SeckillOrderExtend seckillOrderExtend = seckillOrderExtendList.get(0);
                //查询秒杀商品信息
                SeckillStageProduct stageProduct = seckillStageProductModel.getSeckillStageProductByStageProductId(seckillOrderExtend.getStageProductstageProductId());
                AssertUtil.notNull(stageProduct, "秒杀商品不存在");
                //修改秒杀商品表的已购买人数和购买数量
                SeckillStageProduct seckillStageProduct = new SeckillStageProduct();
                seckillStageProduct.setStageProductId(stageProduct.getStageProductId());
                seckillStageProduct.setBuyerCount(stageProduct.getBuyerCount() - 1);
                seckillStageProduct.setBuyQuantity(stageProduct.getBuyQuantity() - orderProduct.getProductNum());
                seckillStageProductModel.updateSeckillStageProduct(seckillStageProduct);
            }
            if (orderType.equals(PromotionConst.PROMOTION_TYPE_105)) {
                //阶梯团订单，修改redis中的已购买数量
                String stockKey = RedisConst.LADDER_GROUP_PURCHASED_NUM_PREFIX + productDb.getGoodsId() + "_" + orderProduct.getMemberId();
                if (stringRedisTemplate.opsForValue().get(stockKey) != null) {
                    stringRedisTemplate.opsForValue().decrement(stockKey, orderProduct.getProductNum());
                }
            }

            if (productDb.getIsDefault().equals(GoodsConst.PRODUCT_IS_DEFAULT_YES)
                    && (productDb.getState().equals(GoodsConst.PRODUCT_STATE_1) || productDb.getState().equals(GoodsConst.PRODUCT_STATE_3))) {
                //默认货品，同步修改商品库存
                Goods updateGoods = new Goods();
                updateGoods.setGoodsId(productDb.getGoodsId());
                updateGoods.setGoodsStock(updateProduct.getProductStock());
                updateGoods.setUpdateTime(new Date());
                goodsModel.updateGoods(updateGoods);
            }
        });
    }

    /**
     * 取消订单-处理用户余额，记录余额日志
     *
     * @param orderDb     订单
     * @param optRole     操作角色
     * @param optUserId   操作人id
     * @param optUserName 操作人名称
     */
    private void orderCancelDealMemberBalance(Order orderDb, Integer optRole, Long optUserId, String optUserName) {
        if (orderDb.getOrderState().equals(OrderConst.ORDER_STATE_10)) {
            if (!StringUtil.isNullOrZero(orderDb.getBalanceAmount())) {
                //待付款，余额支付金额大于0,说明使用了余额部分支付，取消时需要解冻
                Member memberDb = memberModel.getMemberByMemberId(orderDb.getMemberId());

                Member updateMember = new Member();
                updateMember.setMemberId(memberDb.getMemberId());
                updateMember.setBalanceAvailable(memberDb.getBalanceAvailable().add(orderDb.getBalanceAmount()));//增加可用余额
                updateMember.setBalanceFrozen(memberDb.getBalanceFrozen().subtract(orderDb.getBalanceAmount()));//减少冻结金额
                memberModel.updateMember(updateMember);

                //记录余额日志
                MemberBalanceLog memberBalanceLog = new MemberBalanceLog();
                memberBalanceLog.setMemberId(memberDb.getMemberId());
                memberBalanceLog.setMemberName(memberDb.getMemberName());
                memberBalanceLog.setAfterChangeAmount(updateMember.getBalanceAvailable().add(updateMember.getBalanceFrozen()));
                memberBalanceLog.setChangeValue(new BigDecimal("0.00"));
                memberBalanceLog.setFreezeAmount(updateMember.getBalanceFrozen());
                memberBalanceLog.setFreezeValue(orderDb.getBalanceAmount());
                memberBalanceLog.setCreateTime(new Date());
                memberBalanceLog.setType(MemberConst.TYPE_8);
                if (optRole.equals(OrderConst.LOG_ROLE_ADMIN)) {
                    //系统取消订单
                    memberBalanceLog.setDescription("支付超时，系统自动取消订单解冻余额，订单号：" + orderDb.getOrderSn());
                    memberBalanceLog.setAdminId(Math.toIntExact(optUserId));
                    memberBalanceLog.setAdminName(optUserName);
                } else if (optRole.equals(OrderConst.LOG_ROLE_VENDOR)) {
                    //商家取消，默人为系统操作
                    memberBalanceLog.setDescription("商家取消订单解冻余额，订单号：" + orderDb.getOrderSn());
                    memberBalanceLog.setAdminId(0);
                    memberBalanceLog.setAdminName("system");
                } else {
                    memberBalanceLog.setDescription("会员取消订单解冻余额，订单号：" + orderDb.getOrderSn());
                    memberBalanceLog.setAdminId(0);
                    memberBalanceLog.setAdminName(optUserName);
                }
                memberBalanceLogModel.saveMemberBalanceLog(memberBalanceLog);

                //发送余额变动消息通知
                this.sendMsgAccountChange(memberBalanceLog);
            }
            return;
        }
        if (optRole != OrderConst.LOG_ROLE_VENDOR) {
            //其他订单状态，只有商家可以取消订单
            throw new MallException("非法操作");
        }
        //已支付的订单，商家取消，执行退款操作
        BigDecimal returnBalance;//要退的余额
        BigDecimal returnPay = new BigDecimal("0.00");//要退的三方金额

        //退款方式，1为退回到余额，0为原路退回
        String value = stringRedisTemplate.opsForValue().get("refund_setting_switch");
        if ("1".equals(value)) {
            //退回到余额
            returnBalance = orderDb.getBalanceAmount().add(orderDb.getPayAmount());
        } else {
            //原路退回
            returnPay = orderDb.getPayAmount();
            returnBalance = orderDb.getBalanceAmount();
        }

        if (returnBalance.compareTo(BigDecimal.ZERO) > 0) {
            //返还用户余额
            Member memberDb = memberModel.getMemberByMemberId(orderDb.getMemberId());

            Member updateMember = new Member();
            updateMember.setMemberId(memberDb.getMemberId());
            updateMember.setBalanceAvailable(memberDb.getBalanceAvailable().add(returnBalance));//增加可用余额
            memberModel.updateMember(updateMember);

            //记录余额日志
            MemberBalanceLog memberBalanceLog = new MemberBalanceLog();
            memberBalanceLog.setMemberName(memberDb.getMemberName());
            memberBalanceLog.setMemberId(memberDb.getMemberId());
            memberBalanceLog.setAfterChangeAmount(updateMember.getBalanceAvailable().add(memberDb.getBalanceFrozen()));
            memberBalanceLog.setChangeValue(returnBalance);
            memberBalanceLog.setFreezeAmount(memberDb.getBalanceFrozen());
            memberBalanceLog.setFreezeValue(new BigDecimal("0.00"));
            memberBalanceLog.setCreateTime(new Date());
            memberBalanceLog.setType(MemberConst.TYPE_2);
            //商家取消，默人为系统操作
            memberBalanceLog.setDescription("商家取消订单返还余额，订单号：" + orderDb.getOrderSn());
            memberBalanceLog.setAdminId(0);
            memberBalanceLog.setAdminName("system");

            memberBalanceLogModel.saveMemberBalanceLog(memberBalanceLog);

            //发送余额变动消息通知
            updateMember.setBalanceFrozen(memberDb.getBalanceFrozen());
            this.sendMsgAccountChange(memberBalanceLog);
        }

        if (returnPay.compareTo(BigDecimal.ZERO) > 0) {
            SlodonRefundRequest refundRequest = new SlodonRefundRequest();
            //执行原路退回
            if (orderDb.getPaymentCode().toLowerCase().contains(PayConst.METHOD_ALIPAY.toLowerCase())) {
                //支付宝退款
                refundRequest.setAlipayProperties(payPropertiesUtil.getAliPayProperties());
            } else if (orderDb.getPaymentCode().toLowerCase().contains(PayConst.METHOD_WX.toLowerCase())) {
                //微信退款
                refundRequest.setWxPayProperties(payPropertiesUtil.getWxPayProperties(orderDb.getPaymentCode().replace(PayConst.METHOD_WX.toUpperCase(), ""), 1));

                //微信需要设置原支付金额
                if ("1".equals(stringRedisTemplate.opsForValue().get("wxpay_test_enable_h5"))) {
                    refundRequest.setTotalAmount(new BigDecimal("0.01"));
                } else {
                    OrderExample orderExample = new OrderExample();
                    orderExample.setPaySn(orderDb.getPaySn());
                    List<Order> orderList = orderReadMapper.listByExample(orderExample);
                    BigDecimal totalPayAmount = new BigDecimal("0.00");//支付单号下总支付金额
                    for (Order order : orderList) {
                        totalPayAmount = totalPayAmount.add(order.getPayAmount());
                    }
                    refundRequest.setTotalAmount(totalPayAmount);
                }
            } else {
                throw new MallException("支付方式未开启");
            }
            refundRequest.setPaySn(orderDb.getPaySn());
            refundRequest.setRefundSn(orderDb.getOrderSn());
            if ("1".equals(stringRedisTemplate.opsForValue().get("wxpay_test_enable_h5"))) {
                refundRequest.setRefundAmount(new BigDecimal("0.01"));
            } else {
                refundRequest.setRefundAmount(returnPay);
            }
            try {
                SlodonRefundResponse refundResponse = slodonPay.refund(refundRequest, orderDb.getPaymentCode());
                if (!refundResponse.getSuccess()) {
                    log.error("原路退回失败：" + refundResponse.getMsg());
                    throw new MallException("原路退回失败");
                }
            } catch (Exception e) {
                log.error("原路退回失败：", e);
                throw new MallException("原路退回失败");
            }
        }
    }

    /**
     * 系统自动取消24小时没有付款订单
     *
     * @return
     */
    public boolean jobSystemCancelOrder() {
        //获取自动取消时间限制（小时）
        String value = stringRedisTemplate.opsForValue().get("time_limit_of_auto_cancle_order");
        int limitHour = value == null ? 24 : Integer.parseInt(value);

        // 获取当前时间limitHour小时之前的时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -limitHour);

        Date cancelTime = calendar.getTime();

        // 获取超时未付款的订单，此处查询的都是待付款状态的父订单
        OrderExample example = new OrderExample();
        example.setCreateTimeBefore(cancelTime);
        example.setOrderState(OrderConst.ORDER_STATE_10);
        example.setOrderType(OrderConst.ORDER_TYPE_1);//普通订单
        example.setOrderBy("parent_sn");//按照父订单号分组
        List<Order> parentOrderList = orderReadMapper.listByExample(example);

        if (!CollectionUtils.isEmpty(parentOrderList)) {
            parentOrderList.forEach(parentOrder -> {
                //根据父订单号查询待付款的子订单
                OrderExample example1 = new OrderExample();
                example1.setParentSn(parentOrder.getParentSn());
                example1.setOrderState(OrderConst.ORDER_STATE_10);
                List<Order> orderList = orderReadMapper.listByExample(example1);

                //取消订单
                this.cancelOrder(orderList, "支付超时系统自动取消", null, OrderConst.LOG_ROLE_ADMIN, 0L, "system", "系统自动取消订单");
            });
        }
        return true;
    }

    /**
     * 系统自动取消没有付款的秒杀订单
     *
     * @return
     */
    public boolean jobSystemCancelSeckillOrder() {
        //买家几分钟未支付订单，订单取消
        String value = stringRedisTemplate.opsForValue().get("seckill_order_cancle");
        int limitMinute = value == null ? 5 : Integer.parseInt(value);

        // 获取当前时间limitHour小时之前的时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -limitMinute);

        Date cancelTime = calendar.getTime();

        // 获取超时未付款的订单，此处查询的都是待付款状态的父订单
        OrderExample example = new OrderExample();
        example.setCreateTimeBefore(cancelTime);
        example.setOrderState(OrderConst.ORDER_STATE_10);
        example.setOrderType(PromotionConst.PROMOTION_TYPE_104);//秒杀订单
        example.setOrderBy("parent_sn");//按照父订单号分组
        List<Order> parentOrderList = orderReadMapper.listByExample(example);

        if (!CollectionUtils.isEmpty(parentOrderList)) {
            parentOrderList.forEach(parentOrder -> {
                //根据父订单号查询待付款的子订单
                OrderExample example1 = new OrderExample();
                example1.setParentSn(parentOrder.getParentSn());
                example1.setOrderState(OrderConst.ORDER_STATE_10);
                List<Order> orderList = orderReadMapper.listByExample(example1);

                //取消订单
                this.cancelOrder(orderList, "支付超时系统自动取消秒杀订单", null, OrderConst.LOG_ROLE_ADMIN, 0L, "system", "系统自动取消秒杀订单");
            });
        }
        return true;
    }

    /**
     * 系统自动取消没有付款的阶梯团订单
     *
     * @return
     */
    public boolean jobSystemCancelLadderGroupOrder() {
        //买家几分钟未支付订单，订单取消
        String value = stringRedisTemplate.opsForValue().get("ladder_group_deposit_order_auto_cancel_time");
        int limitMinute = value == null ? 30 : Integer.parseInt(value);

        // 获取当前时间limitMinute分钟之前的时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -limitMinute);

        Date cancelTime = calendar.getTime();

        LadderGroupOrderExtendExample extendExample = new LadderGroupOrderExtendExample();
        extendExample.setOrderSubState(LadderGroupConst.ORDER_SUB_STATE_1);
        extendExample.setParticipateTimeBefore(cancelTime);
        List<LadderGroupOrderExtend> orderExtendList = ladderGroupOrderExtendModel.getLadderGroupOrderExtendList(extendExample, null);
        log.info("支付超时系统自动取消阶梯团订单:" + orderExtendList.size());
        if (CollectionUtils.isEmpty(orderExtendList)) {
            return true;
        }
        //拼接订单号
        StringBuilder orderSns = new StringBuilder();
        for (LadderGroupOrderExtend orderExtend : orderExtendList) {
            orderSns.append(",").append(orderExtend.getOrderSn());
        }
        String orderSnIn = orderSns.toString().substring(1);
        //查询订单列表
        OrderExample orderExample = new OrderExample();
        orderExample.setOrderSnIn("'" + orderSnIn.replace(",", "','") + "'");
        orderExample.setOrderState(OrderConst.ORDER_STATE_10);
        List<Order> orderList = orderReadMapper.listByExample(orderExample);
        //取消订单
        this.cancelOrder(orderList, "支付超时系统自动取消阶梯团订单", null, OrderConst.LOG_ROLE_ADMIN, 0L, "system", "系统自动取消阶梯团订单");
        //修改扩展表状态
        LadderGroupOrderExtend groupOrderExtend = new LadderGroupOrderExtend();
        groupOrderExtend.setOrderSubState(LadderGroupConst.ORDER_SUB_STATE_0);
        ladderGroupOrderExtendModel.updateLadderGroupOrderExtendByOrderSn(groupOrderExtend, orderSnIn);
        return true;
    }

    /**
     * 系统自动取消没有付款的阶梯团尾款订单
     *
     * @return
     */
    public boolean jobSystemCancelLadderGroupBalanceOrder() {
        LadderGroupOrderExtendExample extendExample = new LadderGroupOrderExtendExample();
        extendExample.setOrderSubState(LadderGroupConst.ORDER_SUB_STATE_2);
        extendExample.setRemainEndTimeBefore(new Date());
        List<LadderGroupOrderExtend> orderExtendList = ladderGroupOrderExtendModel.getLadderGroupOrderExtendList(extendExample, null);
        log.info("支付超时系统自动取消阶梯团尾款订单:" + orderExtendList.size());
        if (CollectionUtils.isEmpty(orderExtendList)) {
            return true;
        }
        //拼接订单号
        StringBuilder orderSns = new StringBuilder();
        for (LadderGroupOrderExtend orderExtend : orderExtendList) {
            orderSns.append(",").append(orderExtend.getOrderSn());
        }
        String orderSnIn = orderSns.toString().substring(1);
        //查询订单列表
        OrderExample orderExample = new OrderExample();
        orderExample.setOrderSnIn("'" + orderSnIn.replace(",", "','") + "'");
        orderExample.setOrderState(OrderConst.ORDER_STATE_10);
        List<Order> orderList = orderReadMapper.listByExample(orderExample);
        //取消订单
        this.cancelOrder(orderList, "支付超时系统自动取消阶梯团尾款订单", null, OrderConst.LOG_ROLE_ADMIN, 0L, "system", "系统自动取消阶梯团尾款订单");
        //修改扩展表状态
        LadderGroupOrderExtend groupOrderExtend = new LadderGroupOrderExtend();
        groupOrderExtend.setOrderSubState(LadderGroupConst.ORDER_SUB_STATE_0);
        ladderGroupOrderExtendModel.updateLadderGroupOrderExtendByOrderSn(groupOrderExtend, orderSnIn);
        return true;
    }

    /**
     * 系统自动取消没有付款的预售订单
     *
     * @return
     */
    public boolean jobSystemCancelPreSellOrder() {
        //买家几分钟未支付订单，订单取消
        String value = stringRedisTemplate.opsForValue().get("deposit_order_auto_cancel_time");
        int limitMinute = value == null ? 30 : Integer.parseInt(value);

        // 获取当前时间limitMinute分钟之前的时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -limitMinute);

        Date cancelTime = calendar.getTime();

        PresellOrderExtendExample extendExample = new PresellOrderExtendExample();
        extendExample.setOrderSubState(OrderConst.ORDER_SUB_STATE_101);
        extendExample.setCreateTimeBefore(cancelTime);
        List<PresellOrderExtend> orderExtendList = presellOrderExtendModel.getPresellOrderExtendList(extendExample, null);
        log.info("支付超时系统自动取消预售订单:" + orderExtendList.size());
        if (CollectionUtils.isEmpty(orderExtendList)) {
            return true;
        }
        //拼接订单号
        StringBuilder orderSns = new StringBuilder();
        for (PresellOrderExtend orderExtend : orderExtendList) {
            orderSns.append(",").append(orderExtend.getOrderSn());
        }
        String orderSnIn = orderSns.substring(1);
        //查询订单列表
        OrderExample orderExample = new OrderExample();
        orderExample.setOrderSnIn("'" + orderSnIn.replace(",", "','") + "'");
        orderExample.setOrderState(OrderConst.ORDER_STATE_10);
        List<Order> orderList = orderReadMapper.listByExample(orderExample);
        //取消订单
        this.cancelOrder(orderList, "支付超时系统自动取消预售订单", null, OrderConst.LOG_ROLE_ADMIN, 0L, "system", "系统自动取消预售订单");
        //修改扩展表状态
        PresellOrderExtend orderExtend = new PresellOrderExtend();
        orderExtend.setOrderSubState(OrderConst.ORDER_STATE_0);
        presellOrderExtendModel.updatePresellOrderExtendByOrderSn(orderExtend, orderSnIn);
        return true;
    }

    /**
     * 系统自动取消没有付款的预售尾款订单
     *
     * @return
     */
    public boolean jobSystemCancelPreSellBalanceOrder() {
        PresellOrderExtendExample extendExample = new PresellOrderExtendExample();
        extendExample.setOrderSubState(OrderConst.ORDER_SUB_STATE_102);
        extendExample.setRemainEndTimeBefore(new Date());
        List<PresellOrderExtend> orderExtendList = presellOrderExtendModel.getPresellOrderExtendList(extendExample, null);
        log.info("支付超时系统自动取消预售尾款订单:" + orderExtendList.size());
        if (CollectionUtils.isEmpty(orderExtendList)) {
            return true;
        }
        //拼接订单号
        StringBuilder orderSns = new StringBuilder();
        for (PresellOrderExtend orderExtend : orderExtendList) {
            orderSns.append(",").append(orderExtend.getOrderSn());
        }
        String orderSnIn = orderSns.toString().substring(1);
        //查询订单列表
        OrderExample orderExample = new OrderExample();
        orderExample.setOrderSnIn("'" + orderSnIn.replace(",", "','") + "'");
        orderExample.setOrderState(OrderConst.ORDER_STATE_10);
        List<Order> orderList = orderReadMapper.listByExample(orderExample);
        //取消订单
        this.cancelOrder(orderList, "支付超时系统自动取消预售尾款订单", null, OrderConst.LOG_ROLE_ADMIN, 0L, "system", "系统自动取消预售尾款订单");
        //修改扩展表状态
        PresellOrderExtend orderExtend = new PresellOrderExtend();
        orderExtend.setOrderSubState(OrderConst.ORDER_STATE_0);
        presellOrderExtendModel.updatePresellOrderExtendByOrderSn(orderExtend, orderSnIn);
        return true;
    }

    /**
     * 商家取消预售订单赔偿定金
     *
     * @param orderDb         订单
     * @param compensateValue 赔偿倍数
     */
    private void preSellOrderCancelDeal(Order orderDb, Integer optRole, Long optUserId, String optUserName, int compensateValue) {
        //预售订单只有商家可以取消订单，如果不是，直接返回不处理
        if (optRole != OrderConst.LOG_ROLE_VENDOR) {
            return;
        }
        //已支付的订单，商家取消，执行定金赔偿操作
        //查询预售订单扩展表
        PresellOrderExtendExample example = new PresellOrderExtendExample();
        example.setOrderSn(orderDb.getOrderSn());
        example.setOrderSubStateNotEquals(OrderConst.ORDER_SUB_STATE_101);  //只查已付款的
        List<PresellOrderExtend> orderExtendList = presellOrderExtendModel.getPresellOrderExtendList(example, null);
        //预售订单扩展表没有满足条件的数据，不处理直接返回
        if (CollectionUtils.isEmpty(orderExtendList)) {
            return;
        }
        PresellOrderExtend orderExtend = orderExtendList.get(0);
        //支付定金
        BigDecimal compensateBalance = orderExtend.getDepositAmount().multiply(new BigDecimal(orderExtend.getProductNum()));
        //大于0表示设置了定金赔偿,必须是定金预售才走这一步
        if (compensateValue > 0 && orderExtend.getIsAllPay() == OrderConst.IS_ALL_PAY_0) {
            //返还用户余额
            Member memberDb = memberModel.getMemberByMemberId(orderDb.getMemberId());

            Member updateMember = new Member();
            updateMember.setMemberId(memberDb.getMemberId());
            //赔偿金额要减去上面已经退还的本金
            BigDecimal compensateAmount = compensateBalance.multiply(new BigDecimal(compensateValue));
            updateMember.setBalanceAvailable(memberDb.getBalanceAvailable().add(compensateAmount));//增加可用余额
            memberModel.updateMember(updateMember);

            //记录余额日志
            MemberBalanceLog memberBalanceLog = new MemberBalanceLog();
            memberBalanceLog.setMemberName(memberDb.getMemberName());
            memberBalanceLog.setMemberId(memberDb.getMemberId());
            memberBalanceLog.setAfterChangeAmount(updateMember.getBalanceAvailable().add(memberDb.getBalanceFrozen()));
            memberBalanceLog.setChangeValue(compensateAmount);
            memberBalanceLog.setFreezeAmount(memberDb.getBalanceFrozen());
            memberBalanceLog.setFreezeValue(new BigDecimal("0.00"));
            memberBalanceLog.setCreateTime(new Date());
            memberBalanceLog.setType(MemberConst.TYPE_2);
            memberBalanceLog.setDescription("商家取消预售订单赔偿，订单号：" + orderDb.getOrderSn());
            //商家取消，默人为系统操作
            memberBalanceLog.setAdminId(0);
            memberBalanceLog.setAdminName("system");

            memberBalanceLogModel.saveMemberBalanceLog(memberBalanceLog);

            //发送余额变动消息通知
            updateMember.setBalanceFrozen(memberDb.getBalanceFrozen());
            this.sendMsgAccountChange(memberBalanceLog);

            //赔偿记录
            PresellDepositCompensation depositCompensation = new PresellDepositCompensation();
            depositCompensation.setCompensationAmount(compensateBalance.multiply(new BigDecimal(compensateValue)));
            depositCompensation.setDepositAmount(orderExtend.getDepositAmount());
            depositCompensation.setOrderSn(orderDb.getOrderSn());
            depositCompensation.setStoreId(orderDb.getStoreId());
            depositCompensation.setStoreName(orderDb.getStoreName());
            depositCompensation.setMemberId(orderDb.getMemberId());
            depositCompensation.setMemberName(orderDb.getMemberName());
            depositCompensation.setCreateTime(new Date());
            presellDepositCompensationModel.savePresellDepositCompensation(depositCompensation);
        }
        //不等于103说明只支付了定金，只赔偿定金就行
        if (orderExtend.getOrderSubState() != OrderConst.ORDER_SUB_STATE_103) {
            return;
        }

        //已支付的订单，商家取消，执行退款操作（这里只退尾款）
        BigDecimal returnBalance;//要退的余额
        BigDecimal returnPay = new BigDecimal("0.00");//要退的三方金额

        //退款方式，1为退回到余额，0为原路退回
        String value = stringRedisTemplate.opsForValue().get("refund_setting_switch");
        if ("1".equals(value)) {
            if (orderExtend.getIsAllPay() == OrderConst.IS_ALL_PAY_0) {
                //定金预售退回到余额(减去支付的定金)
                returnBalance = orderDb.getBalanceAmount().add(orderDb.getPayAmount()).subtract(compensateBalance);
            } else {
                //全款预售，正常全部退回余额
                returnBalance = orderDb.getBalanceAmount().add(orderDb.getPayAmount());
            }
        } else {
            if (orderExtend.getIsAllPay() == OrderConst.IS_ALL_PAY_0) {
                //剩余可退的钱
                BigDecimal remainMoney = orderDb.getBalanceAmount().add(orderDb.getPayAmount()).subtract(compensateBalance);
                //查询尾款支付方式
                OrderPay orderPay = orderPayModel.getOrderPayByPaySn(orderDb.getPaySn());
                //脏数据不处理
                if (orderPay.getPaymentCode().equals(OrderPaymentConst.PAYMENT_CODE_ONLINE)) {
                    return;
                }
                //定金预售原路退回（要么余额，要么现金，只退一次）
                if (orderPay.getPaymentCode().equals(OrderPaymentConst.PAYMENT_CODE_BALANCE)) {
                    returnBalance = remainMoney;
                    returnPay = BigDecimal.ZERO;
                } else {
                    returnPay = remainMoney;
                    returnBalance = BigDecimal.ZERO;
                }
            } else {
                //全款预售原路退回
                returnPay = orderDb.getPayAmount();
                returnBalance = orderDb.getBalanceAmount();
            }
        }

        if (returnBalance.compareTo(BigDecimal.ZERO) > 0) {
            //返还用户余额
            Member memberDb = memberModel.getMemberByMemberId(orderDb.getMemberId());

            Member updateMember = new Member();
            updateMember.setMemberId(memberDb.getMemberId());
            updateMember.setBalanceAvailable(memberDb.getBalanceAvailable().add(returnBalance));//增加可用余额
            memberModel.updateMember(updateMember);

            //记录余额日志
            MemberBalanceLog memberBalanceLog = new MemberBalanceLog();
            memberBalanceLog.setMemberName(memberDb.getMemberName());
            memberBalanceLog.setMemberId(memberDb.getMemberId());
            memberBalanceLog.setAfterChangeAmount(updateMember.getBalanceAvailable().add(memberDb.getBalanceFrozen()));
            memberBalanceLog.setChangeValue(returnBalance);
            memberBalanceLog.setFreezeAmount(memberDb.getBalanceFrozen());
            memberBalanceLog.setFreezeValue(new BigDecimal("0.00"));
            memberBalanceLog.setCreateTime(new Date());
            memberBalanceLog.setType(MemberConst.TYPE_2);
            memberBalanceLog.setDescription("商家取消预售订单返还尾款，订单号：" + orderDb.getOrderSn());
            memberBalanceLog.setAdminId(optUserId.intValue());
            memberBalanceLog.setAdminName(optUserName);

            memberBalanceLogModel.saveMemberBalanceLog(memberBalanceLog);

            //发送余额变动消息通知
            updateMember.setBalanceFrozen(memberDb.getBalanceFrozen());
            this.sendMsgAccountChange(memberBalanceLog);
        }

        if (returnPay.compareTo(BigDecimal.ZERO) > 0) {
            SlodonRefundRequest refundRequest = new SlodonRefundRequest();
            //执行原路退回
            if (orderDb.getPaymentCode().toLowerCase().contains(PayConst.METHOD_ALIPAY.toLowerCase())) {
                //支付宝退款
                refundRequest.setAlipayProperties(payPropertiesUtil.getAliPayProperties());
            } else if (orderDb.getPaymentCode().toLowerCase().contains(PayConst.METHOD_WX.toLowerCase())) {
                //微信退款
                refundRequest.setWxPayProperties(payPropertiesUtil.getWxPayProperties(orderDb.getPaymentCode().replace(PayConst.METHOD_WX.toUpperCase(), ""), 1));

                //微信需要设置原支付金额
                if ("1".equals(stringRedisTemplate.opsForValue().get("wxpay_test_enable_h5"))) {
                    refundRequest.setTotalAmount(new BigDecimal("0.01"));
                } else {
                    OrderExample orderExample = new OrderExample();
                    orderExample.setPaySn(orderDb.getPaySn());
                    List<Order> orderList = orderReadMapper.listByExample(orderExample);
                    BigDecimal totalPayAmount = new BigDecimal("0.00");//支付单号下总支付金额
                    for (Order order : orderList) {
                        totalPayAmount = totalPayAmount.add(order.getPayAmount());
                    }
                    refundRequest.setTotalAmount(totalPayAmount);
                }
            } else {
                throw new MallException("支付方式未开启");
            }
            refundRequest.setPaySn(orderDb.getPaySn());
            refundRequest.setRefundSn(orderDb.getOrderSn());
            if ("1".equals(stringRedisTemplate.opsForValue().get("wxpay_test_enable_h5"))) {
                refundRequest.setRefundAmount(new BigDecimal("0.01"));
            } else {
                refundRequest.setRefundAmount(returnPay);
            }
            try {
                SlodonRefundResponse refundResponse = slodonPay.refund(refundRequest, orderDb.getPaymentCode());
                if (!refundResponse.getSuccess()) {
                    log.error("原路退回失败：" + refundResponse.getMsg());
                    throw new MallException("原路退回失败");
                }
            } catch (Exception e) {
                log.error("原路退回失败：", e);
                throw new MallException("原路退回失败");
            }
        }
    }

    /**
     * 阶梯团取消订单-处理用户余额，记录余额日志
     *
     * @param orderDb     订单
     * @param optRole     操作角色
     * @param optUserId   操作人id
     * @param optUserName 操作人名称
     */
    private void ladderGroupOrderCancelDealMemberBalance(Order orderDb, Integer optRole, Long optUserId, String optUserName) {
        //阶梯团订单只有商家可以取消订单，如果不是，直接返回不处理
        //现在允许用户取消，如果用户不能取消，修改为LOG_ROLE_VENDOR
        if (optRole == OrderConst.LOG_ROLE_ADMIN) {
            return;
        }
        //查询阶梯团订单扩展表
        LadderGroupOrderExtendExample orderExtendExample = new LadderGroupOrderExtendExample();
        orderExtendExample.setOrderSn(orderDb.getOrderSn());
        List<LadderGroupOrderExtend> groupOrderExtendList = ladderGroupOrderExtendModel.getLadderGroupOrderExtendList(orderExtendExample, null);
        AssertUtil.notEmpty(groupOrderExtendList, "获取阶梯团订单扩展信息为空");
        LadderGroupOrderExtend groupOrderExtend = groupOrderExtendList.get(0);
        if (groupOrderExtend.getOrderSubState() == LadderGroupConst.ORDER_SUB_STATE_1) {
            //未付定金直接返回
            return;
        }
        //查询阶梯团活动
        LadderGroup ladderGroup = ladderGroupFeignClient.getLadderGroupByGroupId(groupOrderExtend.getGroupId());
        AssertUtil.notNull(ladderGroup, "获取阶梯团活动信息为空");

        //已支付的订单，商家取消，执行退款操作
        BigDecimal returnBalance;//要退的余额
        BigDecimal returnPay = new BigDecimal("0.00");//要退的三方金额

        //退款方式，1为退回到余额，0为原路退回
        String value = stringRedisTemplate.opsForValue().get("refund_setting_switch");
        if ("1".equals(value)) {
            //退回到余额
            if (ladderGroup.getIsRefundDeposit() == LadderGroupConst.IS_REFUND_DEPOSIT_0) {
                //不退定金需要减去定金
                returnBalance = orderDb.getBalanceAmount().add(orderDb.getPayAmount())
                        .subtract(groupOrderExtend.getAdvanceDeposit().multiply(new BigDecimal(groupOrderExtend.getProductNum())));
            } else {
                returnBalance = orderDb.getBalanceAmount().add(orderDb.getPayAmount());
            }
        } else {
            //原路退回
            returnPay = orderDb.getPayAmount();
            returnBalance = orderDb.getBalanceAmount();
        }

        if (returnBalance.compareTo(BigDecimal.ZERO) > 0) {
            //返还用户余额
            Member memberDb = memberModel.getMemberByMemberId(orderDb.getMemberId());

            Member updateMember = new Member();
            updateMember.setMemberId(memberDb.getMemberId());
            updateMember.setBalanceAvailable(memberDb.getBalanceAvailable().add(returnBalance));//增加可用余额
            memberModel.updateMember(updateMember);

            //记录余额日志
            MemberBalanceLog memberBalanceLog = new MemberBalanceLog();
            memberBalanceLog.setMemberName(memberDb.getMemberName());
            memberBalanceLog.setMemberId(memberDb.getMemberId());
            memberBalanceLog.setAfterChangeAmount(updateMember.getBalanceAvailable().add(memberDb.getBalanceFrozen()));
            memberBalanceLog.setChangeValue(returnBalance);
            memberBalanceLog.setFreezeAmount(memberDb.getBalanceFrozen());
            memberBalanceLog.setFreezeValue(new BigDecimal("0.00"));
            memberBalanceLog.setCreateTime(new Date());
            memberBalanceLog.setType(MemberConst.TYPE_2);
            //商家取消，默人为系统操作
            memberBalanceLog.setDescription("商家取消阶梯团订单返还余额，订单号：" + orderDb.getOrderSn());
            memberBalanceLog.setAdminId(optUserId.intValue());
            memberBalanceLog.setAdminName(optUserName);

            memberBalanceLogModel.saveMemberBalanceLog(memberBalanceLog);

            //发送余额变动消息通知
            updateMember.setBalanceFrozen(memberDb.getBalanceFrozen());
            this.sendMsgAccountChange(memberBalanceLog);
        }

        if (returnPay.compareTo(BigDecimal.ZERO) > 0) {
            OrderPay orderPay = null;
            if (groupOrderExtend.getOrderSubState() == LadderGroupConst.ORDER_SUB_STATE_2) {
                if (ladderGroup.getIsRefundDeposit() == LadderGroupConst.IS_REFUND_DEPOSIT_0) {
                    //不退定金
                    return;
                }
                //说明已付定金
                orderPay = orderPayModel.getOrderPayByPaySn(groupOrderExtend.getDepositPaySn());
                //定金金额以阶梯团订单扩展表为准
                orderPay.setPayAmount(groupOrderExtend.getAdvanceDeposit().multiply(new BigDecimal(groupOrderExtend.getProductNum())));
                cancelOrderRefund(orderPay);
            } else {
                //进入这里说明已经付完尾款，需要退两次（定金和尾款）
                //剩余可退的钱（减去定金）
                BigDecimal remainMoney = orderDb.getBalanceAmount().add(orderDb.getPayAmount())
                        .subtract(groupOrderExtend.getAdvanceDeposit().multiply(new BigDecimal(groupOrderExtend.getProductNum())));
                if (ladderGroup.getIsRefundDeposit() == LadderGroupConst.IS_REFUND_DEPOSIT_0) {
                    //不退定金，只退尾款
                    orderPay = orderPayModel.getOrderPayByPaySn(groupOrderExtend.getRemainPaySn());
                    orderPay.setPayAmount(remainMoney);
                    cancelOrderRefund(orderPay);
                } else {
                    //定金
                    orderPay = orderPayModel.getOrderPayByPaySn(groupOrderExtend.getDepositPaySn());
                    //定金金额以阶梯团订单扩展表为准
                    orderPay.setPayAmount(groupOrderExtend.getAdvanceDeposit().multiply(new BigDecimal(groupOrderExtend.getProductNum())));
                    cancelOrderRefund(orderPay);
                    //尾款
                    orderPay = orderPayModel.getOrderPayByPaySn(groupOrderExtend.getRemainPaySn());
                    orderPay.setPayAmount(remainMoney);
                    cancelOrderRefund(orderPay);
                }
            }
        }
    }

    /**
     * 取消订单退还现金
     *
     * @param orderPay
     */
    private void cancelOrderRefund(OrderPay orderPay) {
        SlodonRefundRequest refundRequest = new SlodonRefundRequest();
        //执行原路退回
        if (orderPay.getPaymentCode().toLowerCase().contains(PayConst.METHOD_ALIPAY.toLowerCase())) {
            //支付宝退款
            refundRequest.setAlipayProperties(payPropertiesUtil.getAliPayProperties());
        } else if (orderPay.getPaymentCode().toLowerCase().contains(PayConst.METHOD_WX.toLowerCase())) {
            //微信退款
            refundRequest.setWxPayProperties(payPropertiesUtil.getWxPayProperties(orderPay.getPaymentCode().replace(PayConst.METHOD_WX.toUpperCase(), ""), 1));

            //微信需要设置原支付金额
            if ("1".equals(stringRedisTemplate.opsForValue().get("wxpay_test_enable_h5"))) {
                refundRequest.setTotalAmount(new BigDecimal("0.01"));
            } else {
                refundRequest.setTotalAmount(orderPay.getPayAmount());
            }
        } else {
            //如果都没有，说明是余额支付，上面已处理
            return;
        }
        refundRequest.setPaySn(orderPay.getPaySn());
        refundRequest.setRefundSn(orderPay.getOrderSn());
        if ("1".equals(stringRedisTemplate.opsForValue().get("wxpay_test_enable_h5"))) {
            refundRequest.setRefundAmount(new BigDecimal("0.01"));
        } else {
            refundRequest.setRefundAmount(orderPay.getPayAmount());
        }
        try {
            SlodonRefundResponse refundResponse = slodonPay.refund(refundRequest, orderPay.getPaymentCode());
            if (!refundResponse.getSuccess()) {
                log.error("原路退回失败：" + refundResponse.getMsg());
                throw new MallException("原路退回失败");
            }
        } catch (Exception e) {
            log.error("原路退回失败：", e);
            throw new MallException("原路退回失败");
        }
    }

    /**
     * 系统自动取消没有付款的拼团订单
     *
     * @return
     */
    public boolean jobSystemCancelSpellOrder() {
        // 买家多少分钟内未支付订单，订单取消
        String value = stringRedisTemplate.opsForValue().get("spell_order_auto_cancel_time");
        int limitMinute = value == null ? 30 : Integer.parseInt(value);

        // 获取当前时间limitMinute分钟之前的时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -limitMinute);

        Date cancelTime = calendar.getTime();

        // 获取超时未付款的订单
        OrderExample example = new OrderExample();
        example.setCreateTimeBefore(cancelTime);
        example.setOrderState(OrderConst.ORDER_STATE_10);
        example.setOrderType(PromotionConst.PROMOTION_TYPE_102);
        List<Order> orderList = orderReadMapper.listByExample(example);
        //取消订单
        this.cancelOrder(orderList, "支付超时系统自动取消拼团订单", null, OrderConst.LOG_ROLE_ADMIN, 0L, "system", "系统自动取消订单拼团订单");
        return true;
    }

    /**
     * 系统自动发起对拼团失败的退款
     *
     * @return
     */
    public boolean jobSystemLaunchSpellRefund() {
        //获取拼团超过截团时间的团队
        SpellTeamExample example = new SpellTeamExample();
        example.setEndTimeBefore(new Date());
        example.setState(SpellConst.SPELL_GROUP_STATE_1);
        List<SpellTeam> teamList = spellTeamReadMapper.listByExample(example);
        if (CollectionUtils.isEmpty(teamList)) {
            return true;
        }
        //拼接团队id和订单号
        StringBuilder spellTeamIds = new StringBuilder();
        StringBuilder orderSns = new StringBuilder();
        for (SpellTeam team : teamList) {
            spellTeamIds.append(",").append(team.getSpellTeamId());

            //判断团长是否支付，支付了走下一步
            if (team.getLeaderPayState() == SpellConst.PAY_STATE_1) {
                //查询支付成功的成员信息
                SpellTeamMemberExample teamMemberExample = new SpellTeamMemberExample();
                teamMemberExample.setSpellTeamId(team.getSpellTeamId());
                teamMemberExample.setPayState(SpellConst.PAY_STATE_1);
                List<SpellTeamMember> memberList = spellTeamMemberReadMapper.listByExample(teamMemberExample);
                if (CollectionUtils.isEmpty(memberList)) {
                    continue;
                }
                for (SpellTeamMember member : memberList) {
                    orderSns.append(",").append(member.getOrderSn());
                }
            }
        }
        //不管团长支付或未支付都修改状态
        SpellTeamExample teamExample = new SpellTeamExample();
        teamExample.setSpellTeamIdIn(spellTeamIds.substring(1));
        SpellTeam spellTeam = new SpellTeam();
        spellTeam.setState(SpellConst.SPELL_GROUP_STATE_3);
        int count = spellTeamWriteMapper.updateByExampleSelective(spellTeam, teamExample);
        AssertUtil.isTrue(count == 0, "更新拼团团队状态失败，请重试!");

        if (StringUtil.isEmpty(orderSns.toString())) {
            return true;
        }
        String orderSnIn = orderSns.substring(1);
        //查询订单列表
        OrderExample orderExample = new OrderExample();
        orderExample.setOrderSnIn("'" + orderSnIn.replace(",", "','") + "'");
        List<Order> orderList = orderReadMapper.listByExample(orderExample);
        //取消订单(拼团支付成功，但是拼团失败了，所以这里角色传商家方便退款处理)
        this.cancelOrder(orderList, "系统自动取消拼团失败订单", null, OrderConst.LOG_ROLE_VENDOR, 0L, "system", "系统自动取消拼团失败订单");
        return true;
    }

    //endregion


    //region 会员收货，订单完成

    /**
     * 确认收货
     * 1.修改订单状态
     * 2.记录订单日志
     *
     * @param orderDb     订单
     * @param optRole     操作人角色
     * @param optUserId   操作人id
     * @param optUserName 操作人名称
     * @param optRemark   操作备注
     */
    @Transactional
    public void receiveOrder(Order orderDb, Integer optRole, Long optUserId, String optUserName, String optRemark) {
        AssertUtil.isTrue(!orderDb.getOrderState().equals(OrderConst.ORDER_STATE_30), "未到收货状态");
        //1.修改订单状态
        Order updateOrder = new Order();
        updateOrder.setOrderState(OrderConst.ORDER_STATE_40);
        updateOrder.setFinishTime(new Date());
        OrderExample orderExample = new OrderExample();
        orderExample.setOrderSn(orderDb.getOrderSn());
        int count = orderWriteMapper.updateByExampleSelective(updateOrder, orderExample);
        AssertUtil.isTrue(count == 0, "确认收货失败");

        //2.记录订单日志
        orderLogModel.insertOrderLog(optRole, optUserId, optUserName, orderDb.getOrderSn(), OrderConst.ORDER_STATE_40, optRemark);
    }

    /**
     * 系统自动完成订单
     *
     * @return
     */
    public boolean jobSystemFinishOrder() {
        //取setting表配置信息
        String value = stringRedisTemplate.opsForValue().get("time_limit_of_auto_receive");
        int limit = StringUtils.isEmpty(value) ? 10 : Integer.parseInt(value);

        // 获取当前时间limit天之前的时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -limit);

        String deliverTime = TimeUtil.getDateTimeString(calendar.getTime());

        //获取发货时间超过limit天的订单
        OrderExample example = new OrderExample();
        example.setOrderState(OrderConst.ORDER_STATE_30);
        example.setDeliverTimeEnd(deliverTime);
        List<Order> ordersList = orderReadMapper.getOrdersList4AutoFinish(example);
        if (!CollectionUtils.isEmpty(ordersList)) {
            // 单条数据处理异常不影响其他数据执行
            ordersList.forEach(order -> {
                this.receiveOrder(order, OrderConst.LOG_ROLE_ADMIN, 0L, "system", "系统自动完成订单");
            });
        }
        return true;
    }
    //endregion

    /**
     * 订单量统计
     *
     * @param example
     * @return
     */
    public List<OrderDayDTO> getOrderDayDto(OrderExample example) {
        return orderReadMapper.getOrderDayDto(example);
    }

    /**
     * @param orderCommentAddDTO
     * @return
     */
    @Transactional
    public Boolean addOrderComment(OrderCommentAddDTO orderCommentAddDTO, Member member) throws Exception {

        Order order = getOrdersWithOpByOrderSn(orderCommentAddDTO.getOrderSn());
        //1.查询是否评价店铺,如果为空则插入数据
        StoreCommentExample example = new StoreCommentExample();
        example.setMemberId(member.getMemberId());
        example.setOrderSn(orderCommentAddDTO.getOrderSn());
        List<StoreComment> storeCommentList = storeCommentModel.getStoreCommentList(example, null);
        if (CollectionUtils.isEmpty(storeCommentList)) {
            StoreComment storeComment = new StoreComment();
            PropertyUtils.copyProperties(storeComment, orderCommentAddDTO);
            storeComment.setMemberId(member.getMemberId());
            storeComment.setMemberName(member.getMemberName());
            storeComment.setStoreId(order.getStoreId());
            storeComment.setStoreName(order.getStoreName());
            storeComment.setCreateTime(new Date());
            storeCommentModel.saveStoreComment(storeComment);
        }

        //评价赠送积分
        Integer evaluateIntegral = Integer.parseInt(stringRedisTemplate.opsForValue().get("integral_present_order_evaluate"));

        //2.新增商品评论
        //2.1获取商品评论集合
        List<OrderCommentAddDTO.GoodsCommentInfo> goodsCommentInfoList = orderCommentAddDTO.getGoodsCommentInfoList();
        //2.2遍历,然后新增商品评论
        for (OrderCommentAddDTO.GoodsCommentInfo goodsCommentInfo : goodsCommentInfoList) {
            //通过订单货品明细id获取订单货品明细信息
            OrderProduct orderProduct = orderProductReadMapper.getByPrimaryKey(goodsCommentInfo.getOrderProductId());
            //新增商品评论
            GoodsComment goodsComment = new GoodsComment();
            goodsComment.setMemberId(member.getMemberId());
            goodsComment.setMemberName(member.getMemberName());
            goodsComment.setScore(goodsCommentInfo.getScore());
            goodsComment.setContent(goodsCommentInfo.getContent());
            goodsComment.setGoodsId(orderProduct.getGoodsId());
            goodsComment.setGoodsName(orderProduct.getGoodsName());
            goodsComment.setProductId(orderProduct.getProductId());
            goodsComment.setSpecValues(orderProduct.getSpecValues());
            goodsComment.setCreateTime(new Date());
            goodsComment.setStoreId(orderProduct.getStoreId());
            goodsComment.setStoreName(orderProduct.getStoreName());
            goodsComment.setOrderSn(orderCommentAddDTO.getOrderSn());
            goodsComment.setOrderProductId(goodsCommentInfo.getOrderProductId());
            goodsComment.setImage(goodsCommentInfo.getImage());
            goodsComment.setState(GoodsConst.COMMENT_AUDIT);
            goodsCommentModel.saveGoodsComment(goodsComment);

            //获取该订单货品明细所对应订单下所有未评价的订单货品明细
            OrderProductExample productExample = new OrderProductExample();
            productExample.setOrderSn(orderCommentAddDTO.getOrderSn());
            productExample.setIsComment(OrderProductConst.IS_EVALUATE_0);
            productExample.setOrderProductIdNotEquals(goodsCommentInfo.getOrderProductId());//排除此次评价的订单
            List<OrderProduct> ordersGoodsList = orderProductReadMapper.listByExample(productExample);
            if (CollectionUtils.isEmpty(ordersGoodsList)) {
                //该订单下的所有货品都已评价，修改订单评价时间
                OrderExtendExample extendExample = new OrderExtendExample();
                extendExample.setOrderSn(orderCommentAddDTO.getOrderSn());
                List<OrderExtend> orderExtendList = orderExtendReadMapper.listByExample(extendExample);
                AssertUtil.notEmpty(orderExtendList, "查询订单扩展信息失败");
                OrderExtend dbExtend = orderExtendList.get(0);

                OrderExtend updateExtend = new OrderExtend();
                updateExtend.setExtendId(dbExtend.getExtendId());
                updateExtend.setEvaluationTime(new Date());
                int update = orderExtendWriteMapper.updateByPrimaryKeySelective(updateExtend);
                AssertUtil.isTrue(update == 0, "更新订单扩展信息失败");

                //更新订单评价状态为全部评价
                Order updateOrders = new Order();
                updateOrders.setEvaluateState(OrderConst.EVALUATE_STATE_3);
                OrderExample orderExample = new OrderExample();
                orderExample.setOrderSn(orderCommentAddDTO.getOrderSn());
                update = orderWriteMapper.updateByExampleSelective(updateOrders, orderExample);
                AssertUtil.isTrue(update == 0, "更新订单评价状态失败");

            } else {
                //还有未评价的订单货品，更新订单评价状态为部分评价
                Order updateOrders = new Order();
                updateOrders.setEvaluateState(OrderConst.EVALUATE_STATE_2);
                OrderExample orderExample = new OrderExample();
                orderExample.setOrderSn(orderCommentAddDTO.getOrderSn());
                int update = orderWriteMapper.updateByExampleSelective(updateOrders, orderExample);
                AssertUtil.isTrue(update == 0, "更新订单评价状态失败");
            }

            //修改订单货品明细评论状态
            OrderProduct updateProduct = new OrderProduct();
            updateProduct.setOrderProductId(goodsCommentInfo.getOrderProductId());
            updateProduct.setIsComment(OrderProductConst.IS_EVALUATE_1);
            updateProduct.setCommentTime(new Date());
            int upOrderProduct = orderProductWriteMapper.updateByPrimaryKeySelective(updateProduct);
            AssertUtil.isTrue(upOrderProduct == 0, "修改订单货品明细评价失败，请重试");

            //查询商品信息
            Goods goodsDb = goodsModel.getGoodsByGoodsId(orderProduct.getGoodsId());
            //更新商品评论数量
            Goods goods = new Goods();
            goods.setGoodsId(goodsDb.getGoodsId());
            goods.setCommentNumber(goodsDb.getCommentNumber() + 1);
            goods.setUpdateTime(new Date());
            goodsModel.updateGoods(goods);

            if (evaluateIntegral > 0) {
                MemberIntegralVO memberIntegralVO = new MemberIntegralVO(member.getMemberId(), member.getMemberName(), evaluateIntegral,
                        MemberIntegralLogConst.TYPE_4, "订单商品评价（商品名称：" + orderProduct.getGoodsName() + "）", goodsCommentInfo.getOrderProductId().toString(), 0, "");
                rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_MEMBER_INTEGRAL, memberIntegralVO);
                //查询会员信息
                Member memberDb = memberModel.getMemberByMemberId(member.getMemberId());
                memberDb.setMemberIntegral(memberDb.getMemberIntegral() + evaluateIntegral);
                //发送积分变动消息通知
                this.sendMsgIntegralChange(memberDb, "订单商品评价赠送积分（商品名称：" + orderProduct.getGoodsName() + "）");
            }
        }
        return true;
    }

    /**
     * 商户发货
     *
     * @param order
     * @param vendor
     * @param deliverType
     * @param deliverName
     * @param deliverMobile
     * @return
     */
    @Transactional
    public Integer deliverGoods(Order order, Vendor vendor, Integer deliverType, String deliverName, String deliverMobile) {
        OrderExtendExample extendExample = new OrderExtendExample();
        extendExample.setOrderSn(order.getOrderSn());
        List<OrderExtend> orderExtendList = orderExtendReadMapper.listByExample(extendExample);
        AssertUtil.notEmpty(orderExtendList, "订单信息有误");
        OrderExtend orderExtendDb = orderExtendList.get(0);

        //1.更新订单
        int result = orderWriteMapper.updateByPrimaryKeySelective(order);
        AssertUtil.isTrue(result == 0, "修改发货信息失败");

        //2.更新订单扩展信息
        OrderExtend extend = new OrderExtend();
        extend.setExtendId(orderExtendDb.getExtendId());
        extend.setDeliverType(deliverType);
        extend.setDeliverName(deliverName);
        extend.setDeliverMobile(deliverMobile);
        extend.setShippingExpressId(order.getExpressId());
        extend.setDeliverTime(new Date());
        result = orderExtendWriteMapper.updateByPrimaryKeySelective(extend);
        AssertUtil.isTrue(result == 0, "修改订单扩展发货信息失败");

        //3.写订单日志
        orderLogModel.insertOrderLog(OrderConst.LOG_ROLE_VENDOR, vendor.getVendorId(), vendor.getVendorName(),
                order.getOrderSn(), OrderConst.ORDER_STATE_30, "商品发货");

        return result;
    }

    //region 订单导出

    /**
     * 获取导出订单列表
     *
     * @param example
     * @return
     */
    public List<OrderExportDTO> getExportOrderList(OrderExample example) {
        return orderReadMapper.getOrderExportList(example);
    }
    //endregion 订单导出
}