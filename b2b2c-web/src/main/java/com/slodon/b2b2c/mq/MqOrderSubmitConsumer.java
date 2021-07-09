package com.slodon.b2b2c.mq;

import com.slodon.b2b2c.business.dto.OrderSubmitDTO;
import com.slodon.b2b2c.business.dto.OrderSubmitMqConsumerDTO;
import com.slodon.b2b2c.business.dto.OrderSubmitParamDTO;
import com.slodon.b2b2c.core.constant.PromotionConst;
import com.slodon.b2b2c.core.constant.RedisConst;
import com.slodon.b2b2c.core.constant.StarterConfigConst;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.model.business.OrderModel;
import com.slodon.b2b2c.model.member.MemberModel;
import com.slodon.b2b2c.model.promotion.PromotionCommonModel;
import com.slodon.b2b2c.starter.redisson.SlodonLock;
import com.slodon.b2b2c.util.OrderSubmitUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

/**
 * mq执行提交订单
 */
@Slf4j
@Component
public class MqOrderSubmitConsumer {

    @Resource
    private OrderModel orderModel;
    @Resource
    private OrderSubmitUtil orderSubmitUtil;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private PromotionCommonModel promotionCommonModel;
    @Resource
    private MemberModel memberModel;
    @Resource
    private SlodonLock slodonLock;

    /**
     * 提交订单
     *
     * @param consumerDTO 前端提交订单参数
     */
    @RabbitListener(queues = StarterConfigConst.MQ_QUEUE_NAME_ORDER_SUBMIT, containerFactory = StarterConfigConst.MQ_FACTORY_NAME_SINGLE_PASS_ERR, concurrency = "20-40")
    public void orderSubmitConsumer(OrderSubmitMqConsumerDTO consumerDTO) {
        OrderSubmitParamDTO paramDTO = consumerDTO.getParamDTO();
        try {
            //构造计算优惠dto
            OrderSubmitDTO orderSubmitDTO = orderSubmitUtil.getOrderSubmitDTO(paramDTO, consumerDTO.getMemberId(), true);
            //定金不计算优惠
            if (consumerDTO.getPreOrderDTO().getIsCalculateDiscount()) {
                //调用活动模块计算优惠
                orderSubmitDTO = promotionCommonModel.orderSubmitCalculationDiscount(orderSubmitDTO, paramDTO.getSource());
            }
            //查询用户信息
            Member member = memberModel.getMemberByMemberId(consumerDTO.getMemberId());
            //通用提交订单
            Set<String> lockSet = orderModel.submitOrder(orderSubmitDTO, member, consumerDTO);
            //释放此次获取的所有锁
            lockSet.forEach(lockName -> {
                slodonLock.unlock(lockName);
            });

        } catch (Exception e) {
            //订单提交失败
            log.error("提交订单失败，支付单号：{}", consumerDTO.getPaySn(), e);
            if (consumerDTO.getPreOrderDTO().getOrderType().equals(PromotionConst.PROMOTION_TYPE_102)) {
                //拼团订单保存异常，将redis中的购买数量减回去
                stringRedisTemplate.opsForValue().decrement(RedisConst.SPELL_PURCHASED_NUM_PREFIX
                        + consumerDTO.getPreOrderDTO().getGoodsId() + "_" + consumerDTO.getMemberId(), paramDTO.getNumber());
            }
            if (consumerDTO.getPreOrderDTO().getOrderType().equals(PromotionConst.PROMOTION_TYPE_103)) {
                //预售订单保存异常，将redis中的购买数量减回去
                stringRedisTemplate.opsForValue().decrement(RedisConst.PRE_SELL_PURCHASED_NUM_PREFIX
                        + consumerDTO.getPreOrderDTO().getGoodsId() + "_" + consumerDTO.getMemberId(), paramDTO.getNumber());
            }
            if (consumerDTO.getPreOrderDTO().getOrderType().equals(PromotionConst.PROMOTION_TYPE_104)) {
                //秒杀订单保存异常，将redis中的库存加回去
                stringRedisTemplate.opsForValue().increment(RedisConst.REDIS_SECKILL_PRODUCT_STOCK_PREFIX + paramDTO.getProductId(), paramDTO.getNumber());
            }
            if (consumerDTO.getPreOrderDTO().getOrderType().equals(PromotionConst.PROMOTION_TYPE_105)) {
                //阶梯团订单保存异常，将redis中的购买数量减回去
                stringRedisTemplate.opsForValue().decrement(RedisConst.LADDER_GROUP_PURCHASED_NUM_PREFIX
                        + consumerDTO.getPreOrderDTO().getGoodsId() + "_" + consumerDTO.getMemberId(), paramDTO.getNumber());
            }
            return;
        } finally {
            //订单处理成功或失败，都删除redis中的订单处理标识
            stringRedisTemplate.delete(StarterConfigConst.ORDER_SUBMIT_MQ_REDIS_PREFIX + consumerDTO.getPaySn());
        }
        if (consumerDTO.getPreOrderDTO().getOrderType().equals(PromotionConst.PROMOTION_TYPE_104)) {
            //秒杀订单提交完毕，增加会员购买数量
            String key = RedisConst.REDIS_SECKILL_MEMBER_BUY_NUM_PREFIX + paramDTO.getProductId() + "_" + consumerDTO.getMemberId();
            String memberAlreadyBuyNum = stringRedisTemplate.opsForValue().get(key);
            if (memberAlreadyBuyNum == null) {
                stringRedisTemplate.opsForValue().set(key, paramDTO.getNumber().toString());
            } else {
                stringRedisTemplate.opsForValue().set(key, (paramDTO.getNumber() + Integer.parseInt(memberAlreadyBuyNum)) + "");
            }
        }
    }

}
