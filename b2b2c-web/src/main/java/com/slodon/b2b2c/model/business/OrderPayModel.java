package com.slodon.b2b2c.model.business;

import com.slodon.b2b2c.business.example.OrderExample;
import com.slodon.b2b2c.business.example.OrderPayExample;
import com.slodon.b2b2c.business.example.OrderPromotionSendCouponExample;
import com.slodon.b2b2c.business.pojo.Order;
import com.slodon.b2b2c.business.pojo.OrderPay;
import com.slodon.b2b2c.business.pojo.OrderPromotionSendCoupon;
import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.constant.*;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.uid.CouponCode;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.RedBagUtils;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.dao.read.business.OrderPayReadMapper;
import com.slodon.b2b2c.dao.read.business.OrderPromotionSendCouponReadMapper;
import com.slodon.b2b2c.dao.read.member.MemberReadMapper;
import com.slodon.b2b2c.dao.read.promotion.CouponReadMapper;
import com.slodon.b2b2c.dao.write.business.OrderPayWriteMapper;
import com.slodon.b2b2c.dao.write.business.OrderWriteMapper;
import com.slodon.b2b2c.dao.write.member.MemberBalanceLogWriteMapper;
import com.slodon.b2b2c.dao.write.member.MemberWriteMapper;
import com.slodon.b2b2c.dao.write.promotion.CouponMemberWriteMapper;
import com.slodon.b2b2c.dao.write.promotion.CouponUseLogWriteMapper;
import com.slodon.b2b2c.dao.write.promotion.CouponWriteMapper;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.member.pojo.MemberBalanceLog;
import com.slodon.b2b2c.model.promotion.PromotionCommonModel;
import com.slodon.b2b2c.promotion.pojo.Coupon;
import com.slodon.b2b2c.promotion.pojo.CouponMember;
import com.slodon.b2b2c.promotion.pojo.CouponUseLog;
import com.slodon.b2b2c.starter.mq.entity.MessageSendProperty;
import com.slodon.b2b2c.starter.mq.entity.MessageSendVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.slodon.b2b2c.core.constant.StarterConfigConst.MQ_EXCHANGE_NAME;
import static com.slodon.b2b2c.core.constant.StarterConfigConst.MQ_QUEUE_NAME_MEMBER_MSG;

@Component
@Slf4j
public class OrderPayModel {

    @Resource
    private OrderPayReadMapper orderPayReadMapper;
    @Resource
    private OrderPayWriteMapper orderPayWriteMapper;
    @Resource
    private OrderWriteMapper orderWriteMapper;
    @Resource
    private MemberReadMapper memberReadMapper;
    @Resource
    private MemberWriteMapper memberWriteMapper;
    @Resource
    private MemberBalanceLogWriteMapper memberBalanceLogWriteMapper;
    @Resource
    private OrderPromotionSendCouponReadMapper orderPromotionSendCouponReadMapper;
    @Resource
    private CouponReadMapper couponReadMapper;
    @Resource
    private CouponWriteMapper couponWriteMapper;
    @Resource
    private CouponMemberWriteMapper couponMemberWriteMapper;
    @Resource
    private CouponUseLogWriteMapper couponUseLogWriteMapper;
    @Resource
    private PromotionCommonModel promotionCommonModel;
    @Resource
    private OrderLogModel orderLogModel;
    @Resource
    private OrderProductModel orderProductModel;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 新增订单支付表
     *
     * @param orderPay
     * @return
     */
    public Integer saveOrderPay(OrderPay orderPay) {
        int count = orderPayWriteMapper.insert(orderPay);
        if (count == 0) {
            throw new MallException("添加订单支付表失败，请重试");
        }
        return count;
    }

    /**
     * 根据paySn删除订单支付表
     *
     * @param paySn paySn
     * @return
     */
    public Integer deleteOrderPay(String paySn) {
        if (StringUtils.isEmpty(paySn)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = orderPayWriteMapper.deleteByPrimaryKey(paySn);
        if (count == 0) {
            log.error("根据paySn：" + paySn + "删除订单支付表失败");
            throw new MallException("删除订单支付表失败,请重试");
        }
        return count;
    }

    /**
     * 根据paySn更新订单支付表
     *
     * @param orderPay
     * @return
     */
    public Integer updateOrderPay(OrderPay orderPay) {
        if (StringUtils.isEmpty(orderPay.getPaySn())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = orderPayWriteMapper.updateByPrimaryKeySelective(orderPay);
        if (count == 0) {
            log.error("根据paySn：" + orderPay.getPaySn() + "更新订单支付表失败");
            throw new MallException("更新订单支付表失败,请重试");
        }
        return count;
    }

    /**
     * 根据paySn获取订单支付表详情
     *
     * @param paySn paySn
     * @return
     */
    public OrderPay getOrderPayByPaySn(String paySn) {
        return orderPayReadMapper.getByPrimaryKey(paySn);
    }

    /**
     * 根据条件获取订单支付表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<OrderPay> getOrderPayList(OrderPayExample example, PagerInfo pager) {
        List<OrderPay> orderPayList;
        if (pager != null) {
            pager.setRowsCount(orderPayReadMapper.countByExample(example));
            orderPayList = orderPayReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            orderPayList = orderPayReadMapper.listByExample(example);
        }
        return orderPayList;
    }

    /**
     * 提交订单-保存支付信息
     *
     * @param pOrderSn
     * @param paySn
     * @param payAmount
     * @param memberId
     */
    public void insertOrderPay(String pOrderSn, String paySn, BigDecimal payAmount, Integer memberId) {
        OrderPay orderPay = new OrderPay();
        orderPay.setPaySn(paySn);
        orderPay.setOrderSn(pOrderSn);
        orderPay.setPayAmount(payAmount);
        orderPay.setMemberId(memberId);
        orderPay.setPaymentName(OrderPaymentConst.PAYMENT_NAME_ONLINE);
        orderPay.setPaymentCode(OrderPaymentConst.PAYMENT_CODE_ONLINE);
        this.saveOrderPay(orderPay);
    }

    /**
     * 订单余额支付，
     * 1.使用用户余额顺序支付订单，能支付几单就支付几单，剩余未能支付的使用三方支付
     * 2.计算需要三方支付的金额
     *
     * @param orderList 要支付的订单列表
     * @param memberDb  会员
     * @return 需要三方支付的金额
     */
    @Transactional
    public BigDecimal balancePay(List<Order> orderList, Member memberDb) {
        BigDecimal needPay = new BigDecimal("0.00");//最终返回值，订单仍需支付金额
        BigDecimal balanceAvailable = memberDb.getBalanceAvailable();//会员可用余额
        BigDecimal balanceFrozen = memberDb.getBalanceFrozen();//会员冻结金额

        for (Order order : orderList) {
            //当前订单还需支付金额
            BigDecimal currentNeedPay = order.getOrderAmount().subtract(order.getBalanceAmount()).subtract(order.getPayAmount());
            if (currentNeedPay.compareTo(BigDecimal.ZERO) == 0) {
                //订单已支付
                this.orderPaySuccess(order.getOrderSn(), order.getPaySn(), null, order.getBalanceAmount(), order.getPayAmount(),
                        order.getPaymentCode(), order.getPaymentName(), order.getOrderType(), memberDb.getMemberId(), memberDb.getMemberName());
                break;
            }
            if (balanceAvailable.compareTo(BigDecimal.ZERO) <= 0) {
                //可用余额用尽 needPay增加
                needPay = needPay.add(currentNeedPay);

            } else if (balanceAvailable.compareTo(currentNeedPay) < 0) {
                /*
                 *余额不够支付当前订单:
                 * 1.修改订单余额使用数量,
                 * 2.记录余额冻结日志，
                 * 3.needPay增加
                 * 4.冻结金额增加，
                 * 5.可用金额减少
                 */

                //1修改订单余额使用数量,
                Order updateOrder = new Order();
                updateOrder.setBalanceAmount(balanceAvailable);
                OrderExample orderExample = new OrderExample();
                orderExample.setOrderSn(order.getOrderSn());
                int count = orderWriteMapper.updateByExampleSelective(updateOrder, orderExample);
                AssertUtil.isTrue(count == 0, "更新订单支付金额失败");

                //2记录余额冻结日志，
                MemberBalanceLog memberBalanceLog = new MemberBalanceLog();
                memberBalanceLog.setMemberId(memberDb.getMemberId());
                memberBalanceLog.setMemberName(memberDb.getMemberName());
                memberBalanceLog.setAfterChangeAmount(balanceFrozen.add(balanceAvailable));
                memberBalanceLog.setChangeValue(BigDecimal.ZERO);
                memberBalanceLog.setFreezeAmount(balanceFrozen.add(balanceAvailable));
                memberBalanceLog.setFreezeValue(balanceAvailable);
                memberBalanceLog.setCreateTime(new Date());
                memberBalanceLog.setType(MemberConst.TYPE_7);
                memberBalanceLog.setDescription("订单支付冻结，订单号：" + order.getOrderSn());
                memberBalanceLog.setAdminId(0);
                memberBalanceLog.setAdminName(memberDb.getMemberName());
                memberBalanceLogWriteMapper.insert(memberBalanceLog);

                //3.needPay增加
                needPay = needPay.add(currentNeedPay.subtract(balanceAvailable));

                //4.冻结金额增加，
                balanceFrozen = balanceFrozen.add(balanceAvailable);

                //5.可用金额减少
                balanceAvailable = BigDecimal.ZERO;

            } else {
                /*
                 *余额够支付当前订单,直接扣除余额:
                 * 1.记录余额使用日志，
                 * 2.完成订单，
                 * 3.减少余额，
                 */

                //1.记录余额使用日志，
                MemberBalanceLog memberBalanceLog = new MemberBalanceLog();
                memberBalanceLog.setMemberId(memberDb.getMemberId());
                memberBalanceLog.setMemberName(memberDb.getMemberName());
                memberBalanceLog.setAfterChangeAmount(balanceFrozen.add(balanceAvailable).subtract(currentNeedPay));
                memberBalanceLog.setChangeValue(currentNeedPay);
                memberBalanceLog.setFreezeAmount(balanceFrozen);
                memberBalanceLog.setFreezeValue(BigDecimal.ZERO);
                memberBalanceLog.setCreateTime(new Date());
                memberBalanceLog.setType(MemberConst.TYPE_3);
                memberBalanceLog.setDescription("订单消费扣除，订单号：" + order.getOrderSn());
                memberBalanceLog.setAdminId(0);
                memberBalanceLog.setAdminName(memberDb.getMemberName());
                memberBalanceLogWriteMapper.insert(memberBalanceLog);

                //2.完成订单，
                this.orderPaySuccess(order.getOrderSn(), order.getPaySn(), null, order.getBalanceAmount().add(currentNeedPay), order.getPayAmount(),
                        OrderPaymentConst.PAYMENT_CODE_BALANCE, OrderPaymentConst.PAYMENT_NAME_BALANCE,
                        order.getOrderType(), memberDb.getMemberId(), memberDb.getMemberName());

                //3.减少余额，
                balanceAvailable = balanceAvailable.subtract(currentNeedPay);
            }
        }

        //统一更新用户余额
        Member updateMember = new Member();
        updateMember.setMemberId(memberDb.getMemberId());
        updateMember.setBalanceAvailable(balanceAvailable);
        updateMember.setBalanceFrozen(balanceFrozen);
        updateMember.setLastPaymentCode(OrderPaymentConst.PAYMENT_CODE_BALANCE);
        updateMember.setUpdateTime(new Date());
        memberWriteMapper.updateByPrimaryKeySelective(updateMember);

        //发送余额变动消息通知
        this.sendMsgAccountChange(updateMember, memberDb.getBalanceAvailable().subtract(updateMember.getBalanceAvailable()));

        return needPay;
    }


    /**
     * 订单支付完成：
     * 1.更改订单状态
     * 2.记录订单日志
     * 3.增加货品销量
     * 4.解冻会员余额（订单为部分余额支付时）
     * 5.记录余额日志（订单为部分余额支付时）
     * 6.支付单号下所有订单都已支付，修改订单支付表状态
     * 7.非普通订单，执行自定义操作
     * 8.活动赠送优惠券，记录优惠券日志
     *
     * @param orderSn       订单号
     * @param paySn         支付单号
     * @param tradeSn       第三方支付流水号
     * @param balanceAmount 订单最终的余额支付金额（更新订单使用）
     * @param payAmount     订单最终的三方支付金额（更新订单使用）
     */
    public void orderPaySuccess(String orderSn, String paySn, String tradeSn, BigDecimal balanceAmount, BigDecimal payAmount,
                                String paymentCode, String paymentName, Integer orderType, Integer memberId, String memberName) {
        //1.更改订单状态
        Order updateOrder = new Order();
        //预售和阶梯团订单状态额外处理
        if (!orderType.equals(PromotionConst.PROMOTION_TYPE_103) && !orderType.equals(PromotionConst.PROMOTION_TYPE_105)) {
            updateOrder.setPayTime(new Date());
            updateOrder.setOrderState(OrderConst.ORDER_STATE_20);
        }
        updateOrder.setPaymentCode(paymentCode);
        updateOrder.setPaymentName(paymentName);
        updateOrder.setBalanceAmount(balanceAmount);
        updateOrder.setPayAmount(payAmount);
        OrderExample orderExample = new OrderExample();
        orderExample.setOrderSn(orderSn);
        int count = orderWriteMapper.updateByExampleSelective(updateOrder, orderExample);
        AssertUtil.isTrue(count == 0, "更新订单支付金额失败！");

        //2.记录订单日志（预售和阶梯团订单额外处理）
        if (!orderType.equals(PromotionConst.PROMOTION_TYPE_103) && !orderType.equals(PromotionConst.PROMOTION_TYPE_105)) {
            orderLogModel.insertOrderLog(OrderConst.LOG_ROLE_MEMBER, Long.valueOf(memberId), memberName, orderSn, OrderConst.ORDER_STATE_20, "订单支付完成");
        }

        //3.增加货品销量
        orderProductModel.orderPaySuccessAddSales(orderSn);

        if (!StringUtil.isNullOrZero(balanceAmount) && !StringUtil.isNullOrZero(payAmount)) {
            //余额部分支付，解冻用户余额,记录日志
            Member memberDb = memberReadMapper.getByPrimaryKey(memberId);

            //4.解冻会员余额（订单为部分余额支付时）
            Member updateMember = new Member();
            updateMember.setMemberId(memberId);
            updateMember.setBalanceFrozen(memberDb.getBalanceFrozen().subtract(balanceAmount));
            updateMember.setUpdateTime(new Date());
            memberWriteMapper.updateByPrimaryKeySelective(updateMember);

            //5.记录余额日志（订单为部分余额支付时）
            MemberBalanceLog memberBalanceLog = new MemberBalanceLog();
            memberBalanceLog.setMemberId(memberDb.getMemberId());
            memberBalanceLog.setMemberName(memberDb.getMemberName());
            memberBalanceLog.setAfterChangeAmount(memberDb.getBalanceAvailable().add(updateMember.getBalanceFrozen()));
            memberBalanceLog.setChangeValue(balanceAmount);
            memberBalanceLog.setFreezeAmount(updateMember.getBalanceFrozen());
            memberBalanceLog.setFreezeValue(balanceAmount);
            memberBalanceLog.setCreateTime(new Date());
            memberBalanceLog.setType(MemberConst.TYPE_3);
            memberBalanceLog.setDescription("订单消费扣除，订单号：" + orderSn);
            memberBalanceLog.setAdminId(0);
            memberBalanceLog.setAdminName(memberName);
            memberBalanceLogWriteMapper.insert(memberBalanceLog);
        }

        if (!orderType.equals(PromotionConst.PROMOTION_TYPE_103) && !orderType.equals(PromotionConst.PROMOTION_TYPE_105)) {
            //6.支付单号下所有订单都已支付，修改订单支付表状态
            OrderExample orderExample1 = new OrderExample();
            orderExample1.setPaySn(paySn);
            orderExample1.setOrderState(OrderConst.ORDER_STATE_10);
            List<Order> orderList = orderWriteMapper.listByExample(orderExample1);
            if (CollectionUtils.isEmpty(orderList)) {
                //该支付单号下没有待支付的订单，修改支付表支付状态
                OrderPay orderPay = new OrderPay();
                orderPay.setPaySn(paySn);
                orderPay.setApiPayState(OrderConst.API_PAY_STATE_1);
                orderPay.setCallbackTime(new Date());
                orderPay.setTradeSn(tradeSn);
                orderPay.setPaymentName(paymentName);
                orderPay.setPaymentCode(paymentCode);
                count = orderPayWriteMapper.updateByPrimaryKeySelective(orderPay);
                AssertUtil.isTrue(count == 0, "更新支付状态失败");
            }
        }

        if (!orderType.equals(OrderConst.ORDER_TYPE_1)) {
            //7.非普通订单，执行自定义操作
            promotionCommonModel.orderPaySuccess(orderSn, orderType, paySn, tradeSn, paymentName, paymentCode);
        }

        //8.活动赠送优惠券，记录优惠券日志
        this.sendCouponToMember(orderSn, memberId, memberName);

        //发送付款成功消息通知
        this.sendMsgPaySuccess(memberId, orderSn, balanceAmount.add(payAmount));

    }

    /**
     * 支付成功赠送优惠券
     *
     * @param orderSn
     * @param memberId
     * @param memberName
     */
    private void sendCouponToMember(String orderSn, Integer memberId, String memberName) {
        OrderPromotionSendCouponExample sendCouponExample = new OrderPromotionSendCouponExample();
        sendCouponExample.setOrderSn(orderSn);
        List<OrderPromotionSendCoupon> orderPromotionSendCoupons = orderPromotionSendCouponReadMapper.listByExample(sendCouponExample);
        if (CollectionUtils.isEmpty(orderPromotionSendCoupons)) {
            //未赠送优惠券
            return;
        }
        for (OrderPromotionSendCoupon orderPromotionSendCoupon : orderPromotionSendCoupons) {//查询优惠券信息
            Coupon coupon = couponReadMapper.getByPrimaryKey(orderPromotionSendCoupon.getCouponId());
            if (!coupon.getState().equals(CouponConst.ACTIVITY_STATE_1)) {
                //优惠券不可用
                log.error("优惠券失效，不赠送，couponId:" + coupon.getCouponId());
                continue;
            }
            if (coupon.getEffectiveTimeType() == CouponConst.EFFECTIVE_TIME_TYPE_1 && new Date().after(coupon.getEffectiveEnd())) {
                //固定起止时间，使用截止时间已过，优惠券不可用
                log.error("优惠券过期，不赠送，couponId:" + coupon.getCouponId());
                continue;
            }

            if (coupon.getReceivedNum().compareTo(coupon.getPublishNum()) >= 0) {
                //优惠券已领完
                log.error("优惠券已领完，不赠送，couponId:" + coupon.getCouponId());
                continue;
            }

            //赠送优惠券张数，不能超过发行数量-已领数量
            int sendNum = Integer.min(orderPromotionSendCoupon.getNumber(), coupon.getPublishNum() - coupon.getReceivedNum());
            for (int i = 0; i < sendNum; i++) {
                //保存优惠券领取信息
                CouponMember couponMember = new CouponMember();
                couponMember.setCouponId(coupon.getCouponId());
                couponMember.setCouponCode(CouponCode.getKey());
                couponMember.setStoreId(coupon.getStoreId());
                couponMember.setMemberId(memberId);
                couponMember.setMemberName(memberName);
                couponMember.setReceiveTime(new Date());
                couponMember.setUseState(CouponConst.USE_STATE_1);
                if (coupon.getEffectiveTimeType() == CouponConst.EFFECTIVE_TIME_TYPE_2) {
                    couponMember.setEffectiveStart(new Date());
                    couponMember.setEffectiveEnd(TimeUtil.getDateApartDay(coupon.getCycle()));
                } else {
                    couponMember.setEffectiveStart(coupon.getEffectiveStart());
                    couponMember.setEffectiveEnd(coupon.getEffectiveEnd());
                }
                couponMember.setUseType(coupon.getUseType());
                if (coupon.getCouponType() == CouponConst.COUPON_TYPE_3) {
                    BigDecimal randomAmount = RedBagUtils.createRandomKey(coupon.getRandomMin(), coupon.getRandomMax());
                    couponMember.setRandomAmount(randomAmount);
                }
                couponMemberWriteMapper.insert(couponMember);

                //记录优惠券领取日志
                CouponUseLog couponUseLog = new CouponUseLog();
                couponUseLog.setCouponCode(couponMember.getCouponCode());
                couponUseLog.setMemberId(memberId);
                couponUseLog.setMemberName(memberName);
                couponUseLog.setStoreId(coupon.getStoreId());
                couponUseLog.setLogType(CouponConst.LOG_TYPE_1);
                couponUseLog.setLogTime(new Date());
                couponUseLog.setLogContent("下单赠送优惠券，订单号：" + orderSn);
                couponUseLogWriteMapper.insert(couponUseLog);
            }

            //更新优惠券领取数量
            Coupon updateCoupon = new Coupon();
            updateCoupon.setCouponId(coupon.getCouponId());
            updateCoupon.setReceivedNum(coupon.getReceivedNum() + sendNum);
            couponWriteMapper.updateByPrimaryKeySelective(updateCoupon);
        }

    }

    /**
     * 发送付款成功消息通知
     *
     * @param memberId  会员id
     * @param orderSn   订单号
     * @param payAmount 支付金额
     */
    public void sendMsgPaySuccess(Integer memberId, String orderSn, BigDecimal payAmount) {
        //消息通知
        List<MessageSendProperty> messageSendPropertyList = new ArrayList<>();
        messageSendPropertyList.add(new MessageSendProperty("orderSn", orderSn));
        //微信消息通知
        List<MessageSendProperty> messageSendPropertyList4Wx = new ArrayList<>();
        messageSendPropertyList4Wx.add(new MessageSendProperty("first", "您的订单已付款成功。"));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword1", orderSn));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword2", TimeUtil.getDateTimeString(new Date())));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword3", payAmount.toString()));
        messageSendPropertyList4Wx.add(new MessageSendProperty("remark", "若有问题，请联系我们。"));
        messageSendPropertyList4Wx.add(new MessageSendProperty("url", DomainUrlUtil.SLD_H5_URL + "/#/pages/order/list"));
        String msgLinkInfo = "{\"orderSn\":\"" + orderSn + "\",\"type\":\"order_news\"}";
        MessageSendVO messageSendVO = new MessageSendVO(messageSendPropertyList, messageSendPropertyList4Wx, "paymentTime", memberId, MemberTplConst.PAYMENT_SUCCESS_REMINDER, msgLinkInfo);
        //发送到mq
        rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_MEMBER_MSG, messageSendVO);
    }

    /**
     * 发送余额变动消息通知
     *
     * @param member       会员信息
     * @param changeAmount 交易金额
     */
    public void sendMsgAccountChange(Member member, BigDecimal changeAmount) {
        //消息通知
        List<MessageSendProperty> messageSendPropertyList = new ArrayList<>();
        messageSendPropertyList.add(new MessageSendProperty("description", "下单支付"));
        messageSendPropertyList.add(new MessageSendProperty("availableBalance", member.getBalanceAvailable().toString()));
        messageSendPropertyList.add(new MessageSendProperty("frozenBalance", member.getBalanceFrozen().toString()));
        //微信消息通知
        List<MessageSendProperty> messageSendPropertyList4Wx = new ArrayList<>();
        messageSendPropertyList4Wx.add(new MessageSendProperty("first", "您的账户发生了资金变动。"));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword1", "下单扣除余额"));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword2", changeAmount.toString()));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword3", TimeUtil.getDateTimeString(new Date())));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword4", member.getBalanceAvailable().toString()));
        String msgLinkInfo = "{\"type\":\"balance_change\"}";
        MessageSendVO messageSendVO = new MessageSendVO(messageSendPropertyList, messageSendPropertyList4Wx, "changeTime", member.getMemberId(), MemberTplConst.BALANCE_CHANGE_REMINDER, msgLinkInfo);
        //发送到mq
        rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_MEMBER_MSG, messageSendVO);
    }
}