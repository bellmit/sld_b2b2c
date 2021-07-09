package com.slodon.b2b2c.model.integral;

import com.slodon.b2b2c.core.constant.MemberConst;
import com.slodon.b2b2c.core.constant.MemberTplConst;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.constant.OrderPaymentConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.dao.read.integral.IntegralOrderPayReadMapper;
import com.slodon.b2b2c.dao.write.integral.IntegralOrderPayWriteMapper;
import com.slodon.b2b2c.dao.write.integral.IntegralOrderWriteMapper;
import com.slodon.b2b2c.integral.example.IntegralOrderExample;
import com.slodon.b2b2c.integral.example.IntegralOrderPayExample;
import com.slodon.b2b2c.integral.pojo.IntegralOrder;
import com.slodon.b2b2c.integral.pojo.IntegralOrderPay;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.member.pojo.MemberBalanceLog;
import com.slodon.b2b2c.model.member.MemberBalanceLogModel;
import com.slodon.b2b2c.model.member.MemberModel;
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

/**
 * 积分商城订单支付表model
 */
@Component
@Slf4j
public class IntegralOrderPayModel {

    @Resource
    private IntegralOrderPayReadMapper integralOrderPayReadMapper;
    @Resource
    private IntegralOrderPayWriteMapper integralOrderPayWriteMapper;
    @Resource
    private IntegralOrderWriteMapper integralOrderWriteMapper;
    @Resource
    private IntegralOrderLogModel integralOrderLogModel;
    @Resource
    private IntegralOrderProductModel integralOrderProductModel;
    @Resource
    private MemberModel memberModel;
    @Resource
    private MemberBalanceLogModel memberBalanceLogModel;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 新增积分商城订单支付表
     *
     * @param integralOrderPay
     * @return
     */
    public Integer saveIntegralOrderPay(IntegralOrderPay integralOrderPay) {
        int count = integralOrderPayWriteMapper.insert(integralOrderPay);
        if (count == 0) {
            throw new MallException("添加积分商城订单支付表失败，请重试");
        }
        return count;
    }

    /**
     * 根据paySn删除积分商城订单支付表
     *
     * @param paySn paySn
     * @return
     */
    public Integer deleteIntegralOrderPay(String paySn) {
        if (StringUtils.isEmpty(paySn)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = integralOrderPayWriteMapper.deleteByPrimaryKey(paySn);
        if (count == 0) {
            log.error("根据paySn：" + paySn + "删除积分商城订单支付表失败");
            throw new MallException("删除积分商城订单支付表失败,请重试");
        }
        return count;
    }

    /**
     * 根据paySn更新积分商城订单支付表
     *
     * @param integralOrderPay
     * @return
     */
    public Integer updateIntegralOrderPay(IntegralOrderPay integralOrderPay) {
        if (StringUtils.isEmpty(integralOrderPay.getPaySn())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = integralOrderPayWriteMapper.updateByPrimaryKeySelective(integralOrderPay);
        if (count == 0) {
            log.error("根据paySn：" + integralOrderPay.getPaySn() + "更新积分商城订单支付表失败");
            throw new MallException("更新积分商城订单支付表失败,请重试");
        }
        return count;
    }

    /**
     * 根据paySn获取积分商城订单支付表详情
     *
     * @param paySn paySn
     * @return
     */
    public IntegralOrderPay getIntegralOrderPayByPaySn(String paySn) {
        return integralOrderPayReadMapper.getByPrimaryKey(paySn);
    }

    /**
     * 根据条件获取积分商城订单支付表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<IntegralOrderPay> getIntegralOrderPayList(IntegralOrderPayExample example, PagerInfo pager) {
        List<IntegralOrderPay> integralOrderPayList;
        if (pager != null) {
            pager.setRowsCount(integralOrderPayReadMapper.countByExample(example));
            integralOrderPayList = integralOrderPayReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            integralOrderPayList = integralOrderPayReadMapper.listByExample(example);
        }
        return integralOrderPayList;
    }

    /**
     * 提交订单-保存支付信息
     *
     * @param orderSn
     * @param paySn
     * @param payAmount
     * @param memberId
     */
    public void insertOrderPay(String orderSn, String paySn, BigDecimal payAmount, Integer memberId) {
        IntegralOrderPay orderPay = new IntegralOrderPay();
        orderPay.setPaySn(paySn);
        orderPay.setOrderSn(orderSn);
        orderPay.setPayAmount(payAmount);
        orderPay.setMemberId(memberId);
        orderPay.setPaymentName(OrderPaymentConst.PAYMENT_NAME_ONLINE);
        orderPay.setPaymentCode(OrderPaymentConst.PAYMENT_CODE_ONLINE);
        this.saveIntegralOrderPay(orderPay);
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
     *
     * @param orderSn       订单号
     * @param paySn         支付单号
     * @param tradeSn       第三方支付流水号
     * @param balanceAmount 订单最终的余额支付金额（更新订单使用）
     * @param payAmount     订单最终的三方支付金额（更新订单使用）
     */
    public void orderPaySuccess(String orderSn, String paySn, String tradeSn, BigDecimal balanceAmount, BigDecimal payAmount,
                                String paymentCode, String paymentName, Integer memberId, String memberName) {
        //1.更改订单状态
        IntegralOrder updateOrder = new IntegralOrder();
        updateOrder.setPayTime(new Date());
        updateOrder.setOrderState(OrderConst.ORDER_STATE_20);
        //第三方支付流水号为空，说明是余额支付
        if (StringUtil.isEmpty(tradeSn)) {
            //如果余额为0，就是单纯积分支付
            if (balanceAmount == null || balanceAmount.compareTo(BigDecimal.ZERO) == 0) {
                updateOrder.setPaymentCode(OrderPaymentConst.PAYMENT_CODE_INTEGRAL);
                updateOrder.setPaymentName(OrderPaymentConst.PAYMENT_NAME_INTEGRAL);
            } else {
                updateOrder.setPaymentCode(OrderPaymentConst.PAYMENT_CODE_INTEGRAL + "+" + OrderPaymentConst.PAYMENT_CODE_BALANCE);
                updateOrder.setPaymentName(OrderPaymentConst.PAYMENT_NAME_INTEGRAL + "+" + OrderPaymentConst.PAYMENT_NAME_BALANCE);
            }
        } else {
            updateOrder.setPaymentCode(OrderPaymentConst.PAYMENT_CODE_INTEGRAL + "+" + paymentCode);
            updateOrder.setPaymentName(OrderPaymentConst.PAYMENT_NAME_INTEGRAL + "+" + paymentName);
        }
        updateOrder.setBalanceAmount(balanceAmount);
        updateOrder.setPayAmount(payAmount);
        IntegralOrderExample orderExample = new IntegralOrderExample();
        orderExample.setOrderSn(orderSn);
        int count = integralOrderWriteMapper.updateByExampleSelective(updateOrder, orderExample);
        AssertUtil.isTrue(count == 0, "更新订单支付金额失败！");

        //2.记录订单日志
        integralOrderLogModel.insertOrderLog(OrderConst.LOG_ROLE_MEMBER, Long.valueOf(memberId), memberName, orderSn, OrderConst.ORDER_STATE_20, "付款成功");

        //3.增加货品销量
        integralOrderProductModel.orderPaySuccessAddSales(orderSn);

        if (!StringUtil.isNullOrZero(balanceAmount) && !StringUtil.isNullOrZero(payAmount)) {
            //余额部分支付，解冻用户余额,记录日志
            Member memberDb = memberModel.getMemberByMemberId(memberId);

            //4.解冻会员余额（订单为部分余额支付时）
            Member updateMember = new Member();
            updateMember.setMemberId(memberId);
            updateMember.setBalanceFrozen(memberDb.getBalanceFrozen().subtract(balanceAmount));
            updateMember.setUpdateTime(new Date());
            memberModel.updateMember(updateMember);

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
            memberBalanceLogModel.saveMemberBalanceLog(memberBalanceLog);
        }

        //6.支付单号下所有订单都已支付，修改订单支付表状态
        IntegralOrderExample orderExample1 = new IntegralOrderExample();
        orderExample1.setPaySn(paySn);
        orderExample1.setOrderState(OrderConst.ORDER_STATE_10);
        List<IntegralOrder> orderList = integralOrderWriteMapper.listByExample(orderExample1);
        if (CollectionUtils.isEmpty(orderList)) {
            //该支付单号下没有待支付的订单，修改支付表支付状态
            IntegralOrderPay orderPay = new IntegralOrderPay();
            orderPay.setPaySn(paySn);
            orderPay.setApiPayState(OrderConst.API_PAY_STATE_1);
            orderPay.setCallbackTime(new Date());
            orderPay.setTradeSn(tradeSn);
            orderPay.setPaymentName(paymentName);
            orderPay.setPaymentCode(paymentCode);
            count = integralOrderPayWriteMapper.updateByPrimaryKeySelective(orderPay);
            AssertUtil.isTrue(count == 0, "更新支付状态失败");
        }
    }

    /**
     * 订单余额支付，
     * 1.使用用户余额顺序支付订单，能支付几单就支付几单，剩余未能支付的使用三方支付
     * 2.计算需要三方支付的金额
     *
     * @param order    要支付的订单
     * @param memberDb 会员
     * @return 需要三方支付的金额
     */
    @Transactional
    public BigDecimal balancePay(IntegralOrder order, Member memberDb) {
        BigDecimal needPay = new BigDecimal("0.00");//最终返回值，订单仍需支付金额
        BigDecimal balanceAvailable = memberDb.getBalanceAvailable();//会员可用余额
        BigDecimal balanceFrozen = memberDb.getBalanceFrozen();//会员冻结金额

        //当前订单还需支付金额
        BigDecimal currentNeedPay = order.getOrderAmount().subtract(order.getBalanceAmount()).subtract(order.getPayAmount());
        if (currentNeedPay.compareTo(BigDecimal.ZERO) == 0) {
            //订单已支付
            this.orderPaySuccess(order.getOrderSn(), order.getPaySn(), null, order.getBalanceAmount(), order.getPayAmount(),
                    order.getPaymentCode(), order.getPaymentName(), memberDb.getMemberId(), memberDb.getMemberName());
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
            IntegralOrder updateOrder = new IntegralOrder();
            updateOrder.setBalanceAmount(balanceAvailable);
            IntegralOrderExample orderExample = new IntegralOrderExample();
            orderExample.setOrderSn(order.getOrderSn());
            int count = integralOrderWriteMapper.updateByExampleSelective(updateOrder, orderExample);
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
            memberBalanceLogModel.saveMemberBalanceLog(memberBalanceLog);

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
            memberBalanceLogModel.saveMemberBalanceLog(memberBalanceLog);

            //2.完成订单，
            this.orderPaySuccess(order.getOrderSn(), order.getPaySn(), null, order.getBalanceAmount().add(currentNeedPay), order.getPayAmount(),
                    OrderPaymentConst.PAYMENT_CODE_BALANCE, OrderPaymentConst.PAYMENT_NAME_BALANCE, memberDb.getMemberId(), memberDb.getMemberName());

            //3.减少余额，
            balanceAvailable = balanceAvailable.subtract(currentNeedPay);
        }

        //统一更新用户余额
        Member updateMember = new Member();
        updateMember.setMemberId(memberDb.getMemberId());
        updateMember.setBalanceAvailable(balanceAvailable);
        updateMember.setBalanceFrozen(balanceFrozen);
        updateMember.setLastPaymentCode(OrderPaymentConst.PAYMENT_CODE_BALANCE);
        updateMember.setUpdateTime(new Date());
        memberModel.updateMember(updateMember);

        //发送余额变动消息通知
        this.sendMsgAccountChange(updateMember, memberDb.getBalanceAvailable().subtract(updateMember.getBalanceAvailable()));

        return needPay;
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
        messageSendPropertyList.add(new MessageSendProperty("description", "积分订单下单支付"));
        messageSendPropertyList.add(new MessageSendProperty("availableBalance", member.getBalanceAvailable().toString()));
        messageSendPropertyList.add(new MessageSendProperty("frozenBalance", member.getBalanceFrozen().toString()));
        //微信消息通知
        List<MessageSendProperty> messageSendPropertyList4Wx = new ArrayList<>();
        messageSendPropertyList4Wx.add(new MessageSendProperty("first", "您的账户发生了资金变动。"));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword1", "积分订单下单扣除余额"));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword2", changeAmount.toString()));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword3", TimeUtil.getDateTimeString(new Date())));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword4", member.getBalanceAvailable().toString()));
        String msgLinkInfo = "{\"type\":\"balance_change\"}";
        MessageSendVO messageSendVO = new MessageSendVO(messageSendPropertyList, messageSendPropertyList4Wx, "changeTime", member.getMemberId(), MemberTplConst.BALANCE_CHANGE_REMINDER, msgLinkInfo);
        //发送到mq
        rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_MEMBER_MSG, messageSendVO);
    }
}