package com.slodon.b2b2c.model.integral;

import com.alibaba.fastjson.JSON;
import com.slodon.b2b2c.core.constant.*;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.uid.GoodsIdGenerator;
import com.slodon.b2b2c.core.util.*;
import com.slodon.b2b2c.dao.read.integral.IntegralGoodsReadMapper;
import com.slodon.b2b2c.dao.read.integral.IntegralOrderReadMapper;
import com.slodon.b2b2c.dao.read.integral.IntegralProductReadMapper;
import com.slodon.b2b2c.dao.write.integral.IntegralGoodsWriteMapper;
import com.slodon.b2b2c.dao.write.integral.IntegralOrderWriteMapper;
import com.slodon.b2b2c.dao.write.integral.IntegralProductWriteMapper;
import com.slodon.b2b2c.integral.dto.OrderSubmitParamDTO;
import com.slodon.b2b2c.integral.example.IntegralOrderExample;
import com.slodon.b2b2c.integral.example.IntegralOrderProductExample;
import com.slodon.b2b2c.integral.pojo.IntegralGoods;
import com.slodon.b2b2c.integral.pojo.IntegralOrder;
import com.slodon.b2b2c.integral.pojo.IntegralOrderProduct;
import com.slodon.b2b2c.integral.pojo.IntegralProduct;
import com.slodon.b2b2c.member.pojo.*;
import com.slodon.b2b2c.model.member.*;
import com.slodon.b2b2c.starter.entity.SlodonRefundRequest;
import com.slodon.b2b2c.starter.entity.SlodonRefundResponse;
import com.slodon.b2b2c.starter.mq.entity.MessageSendProperty;
import com.slodon.b2b2c.starter.mq.entity.MessageSendVO;
import com.slodon.b2b2c.starter.pay.SlodonPay;
import com.slodon.b2b2c.util.PayPropertiesUtil;
import com.slodon.b2b2c.vo.integral.IntegralOrderExportVO;
import com.slodon.b2b2c.vo.integral.IntegralOrderSubmitVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.slodon.b2b2c.core.constant.StarterConfigConst.MQ_EXCHANGE_NAME;
import static com.slodon.b2b2c.core.constant.StarterConfigConst.MQ_QUEUE_NAME_MEMBER_MSG;

/**
 * 积分商城订单表model
 */
@Component
@Slf4j
public class IntegralOrderModel {

    @Resource
    private IntegralOrderReadMapper integralOrderReadMapper;
    @Resource
    private IntegralOrderWriteMapper integralOrderWriteMapper;
    @Resource
    private IntegralProductReadMapper integralProductReadMapper;
    @Resource
    private IntegralProductWriteMapper integralProductWriteMapper;
    @Resource
    private IntegralGoodsReadMapper integralGoodsReadMapper;
    @Resource
    private IntegralGoodsWriteMapper integralGoodsWriteMapper;
    @Resource
    private IntegralOrderProductModel integralOrderProductModel;
    @Resource
    private IntegralOrderLogModel integralOrderLogModel;
    @Resource
    private IntegralOrderPayModel integralOrderPayModel;
    @Resource
    private MemberModel memberModel;
    @Resource
    private MemberIntegralLogModel memberIntegralLogModel;
    @Resource
    private MemberAddressModel memberAddressModel;
    @Resource
    private MemberBalanceLogModel memberBalanceLogModel;
    @Resource
    private MemberInvoiceModel memberInvoiceModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private PayPropertiesUtil payPropertiesUtil;
    @Resource
    private SlodonPay slodonPay;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 新增积分商城订单表
     *
     * @param integralOrder
     * @return
     */
    public Integer saveIntegralOrder(IntegralOrder integralOrder) {
        int count = integralOrderWriteMapper.insert(integralOrder);
        if (count == 0) {
            throw new MallException("添加积分商城订单表失败，请重试");
        }
        return count;
    }

    /**
     * 根据orderId删除积分商城订单表
     *
     * @param orderId orderId
     * @return
     */
    public Integer deleteIntegralOrder(Integer orderId) {
        if (StringUtils.isEmpty(orderId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = integralOrderWriteMapper.deleteByPrimaryKey(orderId);
        if (count == 0) {
            log.error("根据orderId：" + orderId + "删除积分商城订单表失败");
            throw new MallException("删除积分商城订单表失败,请重试");
        }
        return count;
    }

    /**
     * 根据orderId更新积分商城订单表
     *
     * @param integralOrder
     * @return
     */
    public Integer updateIntegralOrder(IntegralOrder integralOrder) {
        if (StringUtils.isEmpty(integralOrder.getOrderId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = integralOrderWriteMapper.updateByPrimaryKeySelective(integralOrder);
        if (count == 0) {
            log.error("根据orderId：" + integralOrder.getOrderId() + "更新积分商城订单表失败");
            throw new MallException("更新积分商城订单表失败,请重试");
        }
        return count;
    }

    /**
     * 根据条件更新订单
     *
     * @param integralOrder
     * @param example
     * @return
     */
    public Integer updateIntegralOrderByExample(IntegralOrder integralOrder, IntegralOrderExample example) {
        return integralOrderWriteMapper.updateByExampleSelective(integralOrder, example);
    }

    /**
     * 根据orderId获取积分商城订单表详情
     *
     * @param orderId orderId
     * @return
     */
    public IntegralOrder getIntegralOrderByOrderId(Integer orderId) {
        return integralOrderReadMapper.getByPrimaryKey(orderId);
    }

    /**
     * 根据订单号获取订单详情
     *
     * @param orderSn 订单号
     * @return
     */
    public IntegralOrder getIntegralOrderByOrderSn(String orderSn) {
        IntegralOrderExample orderExample = new IntegralOrderExample();
        orderExample.setOrderSn(orderSn);
        List<IntegralOrder> orderList = integralOrderReadMapper.listByExample(orderExample);
        AssertUtil.notEmpty(orderList, "订单不存在");
        return orderList.get(0);
    }

    /**
     * 根据订单号获取订单，WithOp查询订单关联的货品列表
     *
     * @param orderSn
     * @return
     */
    public IntegralOrder getOrdersWithOpByOrderSn(String orderSn) {
        IntegralOrder order = getIntegralOrderByOrderSn(orderSn);
        IntegralOrderProductExample productExample = new IntegralOrderProductExample();
        productExample.setOrderSn(orderSn);
        List<IntegralOrderProduct> orderProductList = integralOrderProductModel.getIntegralOrderProductList(productExample, null);
        AssertUtil.notEmpty(orderProductList, "订单有误");
        order.setOrderProductList(orderProductList);
        return order;
    }

    /**
     * 根据条件获取积分商城订单表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<IntegralOrder> getIntegralOrderList(IntegralOrderExample example, PagerInfo pager) {
        List<IntegralOrder> integralOrderList;
        if (pager != null) {
            pager.setRowsCount(integralOrderReadMapper.countByExample(example));
            integralOrderList = integralOrderReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            integralOrderList = integralOrderReadMapper.listByExample(example);
        }
        return integralOrderList;
    }

    /**
     * 根据条件获取积分商城订单数量
     *
     * @param example 查询条件信息
     * @return
     */
    public Integer getIntegralOrderCount(IntegralOrderExample example) {
        return integralOrderReadMapper.countByExample(example);
    }

    /**
     * 订单确认信息
     *
     * @param productId
     * @param number
     * @param member
     * @return
     */
    public IntegralOrderSubmitVO OrderConfirm(Long productId, Integer number, Member member, Integer useIntegral) {
        IntegralOrderSubmitVO vo = new IntegralOrderSubmitVO();
        IntegralProduct integralProduct = integralProductReadMapper.getByPrimaryKey(productId);
        AssertUtil.notNull(integralProduct, "积分货品不存在");
        AssertUtil.isTrue(number.compareTo(integralProduct.getProductStock()) > 0, "库存不足");

        //是否可以开增值税发票
        boolean isVatInvoice = true;
        IntegralGoods goods = integralGoodsReadMapper.getByPrimaryKey(integralProduct.getGoodsId());
        AssertUtil.notNull(goods, "积分商品不存在，请重试！");
        AssertUtil.isTrue(!goods.getState().equals(GoodsConst.GOODS_STATE_UPPER), "该商品已下架");
        if (goods.getIsVatInvoice() != null && goods.getIsVatInvoice() == GoodsConst.IS_VAT_INVOICE_NO) {
            isVatInvoice = false;
        }
        vo.setIsVatInvoice(isVatInvoice);

        vo.setStoreId(integralProduct.getStoreId());
        vo.setStoreName(integralProduct.getStoreName());
        if (useIntegral == null) {
            vo.setIntegralAmount(integralProduct.getIntegralPrice() * number);
            vo.setCashAmount(integralProduct.getCashPrice().multiply(new BigDecimal(number)));
        } else {
            //总支付金额(积分支付金额+现金支付金额)
            BigDecimal payAmount = new BigDecimal("0.00");
            //积分抵扣金额
            BigDecimal integralCashAmount = new BigDecimal("0.00");
            //使用积分校验
            //判断用户的积分是否够填入的积分数
            AssertUtil.isTrue(useIntegral != 0 && useIntegral.compareTo(member.getMemberIntegral()) > 0, "积分不足，请重新填写积分数量！");
            //货品原价
            Integer integralPrice = integralProduct.getIntegralPrice() * number;
            //如果积分相等说明是正常积分支付，没有现金的递增
            if (integralPrice.equals(useIntegral)) {
                payAmount = payAmount.add(integralProduct.getCashPrice().multiply(new BigDecimal(number)));
            } else {
                //进入这里说明现金递增，使用积分减少，计算差距
                Integer differIntegral = integralPrice - useIntegral;
                //消耗积分换算比例的积分数量
                String integralScale = stringRedisTemplate.opsForValue().get("integral_conversion_ratio");
                AssertUtil.notEmpty(integralScale, "积分转换金额失败，请联系系统管理员！");
                //积分可以为0，所以这条判断暂时不需要了
//                AssertUtil.isTrue(Integer.parseInt(integralScale) > useIntegral, "积分必须大于换算比例" + integralScale + ",并且是整数倍");
                AssertUtil.isTrue(useIntegral % Integer.parseInt(integralScale) != 0, "积分必须是换算比例" + integralScale + "的整数倍");
                //积分换算成金额的总值
                integralCashAmount = integralCashAmount.add(BigDecimal.valueOf(differIntegral).divide(new BigDecimal(integralScale), 2, RoundingMode.HALF_UP));
                payAmount = payAmount.add(integralCashAmount).add(integralProduct.getCashPrice().multiply(new BigDecimal(number)));
            }
            vo.setIntegralAmount(useIntegral);
            vo.setCashAmount(payAmount);
        }
        IntegralOrderSubmitVO.ProductVO productVO = new IntegralOrderSubmitVO.ProductVO(integralProduct);
        productVO.setBuyNum(number);
        vo.setProduct(productVO);
        //查询数据库会员信息
        Member memberDb = memberModel.getMemberByMemberId(member.getMemberId());
        AssertUtil.notNull(memberDb, "会员不存在");
        vo.setMemberIntegral(memberDb.getMemberIntegral());
        //获取多少积分换算成1元
        String integralScale = stringRedisTemplate.opsForValue().get("integral_conversion_ratio");
        vo.setIntegralScale(integralScale);
        vo.setIntegralList(this.getIntegralList(memberDb.getMemberIntegral(), integralProduct.getIntegralPrice() * number, integralScale));
        return vo;
    }

    /**
     * 可用积分列表
     *
     * @param memberIntegral
     * @param integralAmount
     * @param integralScale
     * @return
     */
    public List<Integer> getIntegralList(Integer memberIntegral, Integer integralAmount, String integralScale) {
        List<Integer> list = new ArrayList<>();
        //多少积分=1元
        Integer scale = Integer.valueOf(integralScale);
        //最多使用积分
        Integer availableIntegral = Integer.min(memberIntegral / scale * scale, integralAmount / scale * scale);

        int times = availableIntegral / scale;//可以使用的积分档位数
        int maxTime = 10;//最高档位数
        if (times <= maxTime) {
            //未达到最高档位
            for (int i = 1; i <= times; i++) {
                list.add(scale * i);
            }
        } else {
            //比最高档位多
            int space = times / maxTime * scale;//档位间隔
            for (int i = maxTime - 1; i >= 0; i--) {
                list.add(availableIntegral - space * i);
            }
        }

        return list;
    }

    /**
     * 提交订单
     *
     * @param dto
     * @param member
     * @return
     */
    @Transactional
    public Map<String, Object> submitOrder(OrderSubmitParamDTO dto, Member member) {
        //会员信息
        Member memberDb = memberModel.getMemberByMemberId(member.getMemberId());
        //积分货品
        IntegralProduct product = integralProductReadMapper.getByPrimaryKey(dto.getProductId());
        //积分商品
        IntegralGoods goods = integralGoodsReadMapper.getByPrimaryKey(product.getGoodsId());
        //消耗积分换算比例的积分数量
        String integralScaleStr = stringRedisTemplate.opsForValue().get("integral_conversion_ratio");
        if (integralScaleStr == null) {
            throw new MallException("获取积分换算比例失败，请联系系统管理员！");
        }
        Integer integralScale = Integer.parseInt(integralScaleStr);

        AssertUtil.isTrue(dto.getNumber().compareTo(product.getProductStock()) > 0, "库存不足");
        //校验积分合理性
        if (!StringUtil.isNullOrZero(dto.getIntegral())) {
            //使用积分数必须是换算比例的整数倍
            AssertUtil.isTrue(dto.getIntegral() % integralScale != 0, "积分使用数量有误，请重新选择");
            //判断用户的积分是否够填入的积分数
            AssertUtil.isTrue(dto.getIntegral().compareTo(memberDb.getMemberIntegral()) > 0, "积分不足，请重新填写积分数量！");
            //货品原价
            Integer integralPrice = product.getIntegralPrice() * dto.getNumber();
            AssertUtil.isTrue(dto.getIntegral() > integralPrice, "最多可使用" + integralPrice + "积分");
        }


        Map<String, Object> dataMap = new HashMap<>();
        //订单号
        String orderSn = GoodsIdGenerator.getOrderSn();
        //支付单号
        String paySn = GoodsIdGenerator.getPaySn();
        //收货地址
        MemberAddress memberAddress = memberAddressModel.getMemberAddressByAddressId(dto.getAddressId());

        //用户需要支付的现金金额 = 商品现金原价*商品购买数量+积分不足需使用现金抵扣的金额
        BigDecimal payAmount = product.getCashPrice().multiply(new BigDecimal(dto.getNumber()))
                .add(new BigDecimal((product.getIntegralPrice() * dto.getNumber() - dto.getIntegral()) / integralScale));
        //积分抵扣金额
        BigDecimal integralCashAmount = new BigDecimal(dto.getIntegral() / integralScale);

        //保存订单
        IntegralOrder order = this.insertOrder(orderSn, paySn, dto, integralCashAmount, payAmount, product, memberDb, memberAddress);

        //保存订单货品
        integralOrderProductModel.insertOrderProduct(product, memberDb, orderSn, dto, integralCashAmount, payAmount);

        //记录订单日志
        integralOrderLogModel.insertOrderLog(OrderConst.LOG_ROLE_MEMBER, Long.valueOf(member.getMemberId()),
                member.getMemberName(), orderSn, OrderConst.ORDER_STATE_10, "用户提交订单");

        //-member 减积分;member_integral_log 记录积分使用日志
        this.reduceMemberIntegral(member.getMemberId(), dto.getIntegral(), orderSn);

        //-product、goods 减库存
        this.reduceStock(goods, product, dto.getNumber());

        //支付信息
        integralOrderPayModel.insertOrderPay(orderSn, paySn, payAmount, member.getMemberId());

        //订单金额为0时，直接支付成功
        //如果订单金额为0
        if (order.getOrderAmount().compareTo(BigDecimal.ZERO) == 0) {
            //订单已支付
            integralOrderPayModel.orderPaySuccess(order.getOrderSn(), order.getPaySn(), null, order.getBalanceAmount(), order.getPayAmount(),
                    order.getPaymentCode(), order.getPaymentName(), member.getMemberId(), member.getMemberName());
        }
        dataMap.put("paySn", paySn);
        dataMap.put("needPay", payAmount.compareTo(BigDecimal.ZERO) > 0);
        return dataMap;
    }

    /**
     * 提交订单-保存订单
     *
     * @param orderSn       订单号
     * @param paySn         支付单号
     * @param product       货品信息
     * @param member        会员信息
     * @param memberAddress 收货地址
     * @return 订单号
     */
    private IntegralOrder insertOrder(String orderSn, String paySn, OrderSubmitParamDTO dto, BigDecimal integralCashAmount, BigDecimal payAmount,
                                      IntegralProduct product, Member member, MemberAddress memberAddress) {
        IntegralOrder order = new IntegralOrder();
        order.setOrderSn(orderSn);
        order.setPaySn(paySn);
        order.setStoreId(product.getStoreId());
        order.setStoreName(product.getStoreName());
        order.setMemberName(member.getMemberName());
        order.setMemberId(member.getMemberId());
        order.setCreateTime(new Date());
        order.setOrderState(OrderConst.ORDER_STATE_10);
        order.setPaymentName(OrderPaymentConst.PAYMENT_NAME_ONLINE);
        order.setPaymentCode(OrderPaymentConst.PAYMENT_CODE_ONLINE);
        order.setOrderAmount(payAmount);
        order.setIntegralCashAmount(integralCashAmount);
        order.setIntegral(dto.getIntegral());
        order.setOrderRemark(dto.getRemark());
        order.setOrderFrom(dto.getOrderFrom());
        if (!StringUtil.isNullOrZero(dto.getInvoiceId())) {
            //发票信息
            MemberInvoice memberInvoice = memberInvoiceModel.getMemberInvoiceByInvoiceId(dto.getInvoiceId());
            order.setInvoiceInfo(JSON.toJSONString(memberInvoice));
        }
        order.setReceiverName(memberAddress.getMemberName());
        order.setReceiverAreaInfo(memberAddress.getAddressAll());
        order.setReceiverAddress(memberAddress.getDetailAddress());
        order.setReceiverMobile(memberAddress.getTelMobile());
        this.saveIntegralOrder(order);
        return order;
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
            this.sendMsgIntegralChange(updateMember, "积分订单下单支付");
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
        messageSendPropertyList.add(new MessageSendProperty("description", "积分订单取消订单返还余额"));
        messageSendPropertyList.add(new MessageSendProperty("availableBalance", memberBalanceLog.getAfterChangeAmount().toString()));
        messageSendPropertyList.add(new MessageSendProperty("frozenBalance", memberBalanceLog.getFreezeAmount().toString()));
        //微信消息通知
        List<MessageSendProperty> messageSendPropertyList4Wx = new ArrayList<>();
        messageSendPropertyList4Wx.add(new MessageSendProperty("first", "您的账户发生了资金变动。"));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword1", "积分订单取消订单返还余额"));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword2", memberBalanceLog.getChangeValue().toString()));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword3", TimeUtil.getDateTimeString(memberBalanceLog.getCreateTime())));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword4", memberBalanceLog.getAfterChangeAmount().toString()));
        String msgLinkInfo = "{\"type\":\"balance_change\"}";
        MessageSendVO messageSendVO = new MessageSendVO(messageSendPropertyList, messageSendPropertyList4Wx, "changeTime", memberBalanceLog.getMemberId(), MemberTplConst.BALANCE_CHANGE_REMINDER, msgLinkInfo);
        //发送到mq
        rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_MEMBER_MSG, messageSendVO);
    }

    /**
     * 提交订单-更新商品库存
     *
     * @param goodsDb   商品
     * @param productDb 货品
     * @param reduceNum 购买数量
     */
    private void reduceStock(IntegralGoods goodsDb, IntegralProduct productDb, Integer reduceNum) {
        //减sku库存
        IntegralProduct updateProduct = new IntegralProduct();
        updateProduct.setIntegralProductId(productDb.getIntegralProductId());
        updateProduct.setProductStock(productDb.getProductStock() - reduceNum);
        integralProductWriteMapper.updateByPrimaryKeySelective(updateProduct);

        if (goodsDb.getDefaultProductId().equals(productDb.getIntegralProductId())) {
            //此次购买的是默认货品，更新商品库存
            IntegralGoods updateGoods = new IntegralGoods();
            updateGoods.setIntegralGoodsId(goodsDb.getIntegralGoodsId());
            updateGoods.setGoodsStock(updateProduct.getProductStock());
            updateGoods.setUpdateTime(new Date());
            integralGoodsWriteMapper.updateByPrimaryKeySelective(goodsDb);
        }
    }

    /**
     * 取消订单（整单取消）
     * 1.修改订单状态
     * 2.记录订单日志
     * 3.返还订单使用的积分，记录积分日志
     * 4.增加货品库存，增加商品库存
     * 5.处理用户余额，记录余额日志
     *
     * @param orderList    取消的订单列表
     * @param cancelReason 取消原因
     * @param optRole      操作角色{@link OrderConst#LOG_ROLE_ADMIN}
     * @param optUserId    操作人id
     * @param optUserName  操作人名称
     * @param optRemark    操作备注
     */
    @Transactional
    public void cancelOrder(List<IntegralOrder> orderList, String cancelReason, String cancelRemark, Integer optRole, Long optUserId, String optUserName, String optRemark) {
        orderList.forEach(orderDb -> {
            //1.修改订单状态
            IntegralOrder updateOrder = new IntegralOrder();
            updateOrder.setOrderState(OrderConst.ORDER_STATE_0);
            updateOrder.setRefuseReason(cancelReason);
            updateOrder.setRefuseRemark(cancelRemark);
            IntegralOrderExample orderExample = new IntegralOrderExample();
            orderExample.setOrderSn(orderDb.getOrderSn());
            int count = integralOrderWriteMapper.updateByExampleSelective(updateOrder, orderExample);
            AssertUtil.isTrue(count == 0, "取消订单失败");

            //2.记录订单日志
            integralOrderLogModel.insertOrderLog(optRole, optUserId, optUserName, orderDb.getOrderSn(), OrderConst.ORDER_STATE_0, optRemark);

            //3.返还订单使用的积分，记录积分日志
            this.orderCancelReturnIntegral(orderDb, optUserId, optUserName);

            //4.增加货品库存，增加商品库存
            this.orderCancelAddGoodsStock(orderDb.getOrderSn());

            //5.处理用户余额，记录余额日志
            this.orderCancelDealMemberBalance(orderDb, optRole, optUserId, optUserName);
        });

    }

    /**
     * 取消订单返还订单使用的积分，记录积分日志
     *
     * @param orderDb     订单
     * @param optUserId   操作人id
     * @param optUserName 操作人名称
     */
    private void orderCancelReturnIntegral(IntegralOrder orderDb, Long optUserId, String optUserName) {
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

        //发送积分变动消息通知
        this.sendMsgIntegralChange(updateMember, "积分订单取消订单返还积分");
    }

    /**
     * 订单取消-增加货品库存，增加商品库存
     *
     * @param orderSn
     */
    private void orderCancelAddGoodsStock(String orderSn) {
        //查询订单货品
        IntegralOrderProductExample orderProductExample = new IntegralOrderProductExample();
        orderProductExample.setOrderSn(orderSn);
        List<IntegralOrderProduct> orderProductList = integralOrderProductModel.getIntegralOrderProductList(orderProductExample, null);
        orderProductList.forEach(orderProduct -> {
            //查询现有库存
            IntegralProduct productDb = integralProductReadMapper.getByPrimaryKey(orderProduct.getProductId());
            //更新sku库存
            IntegralProduct updateProduct = new IntegralProduct();
            updateProduct.setIntegralProductId(productDb.getIntegralProductId());
            updateProduct.setProductStock(productDb.getProductStock() + orderProduct.getProductNum());
            integralProductWriteMapper.updateByPrimaryKeySelective(updateProduct);

            if (productDb.getIsDefault().equals(GoodsConst.PRODUCT_IS_DEFAULT_YES)
                    && (productDb.getState().equals(GoodsConst.PRODUCT_STATE_1) || productDb.getState().equals(GoodsConst.PRODUCT_STATE_3))) {
                //默认货品，同步修改商品库存
                IntegralGoods updateGoods = new IntegralGoods();
                updateGoods.setIntegralGoodsId(productDb.getGoodsId());
                updateGoods.setGoodsStock(updateProduct.getProductStock());
                updateGoods.setUpdateTime(new Date());
                integralGoodsWriteMapper.updateByPrimaryKeySelective(updateGoods);

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
    private void orderCancelDealMemberBalance(IntegralOrder orderDb, Integer optRole, Long optUserId, String optUserName) {
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
                    IntegralOrderExample orderExample = new IntegralOrderExample();
                    orderExample.setPaySn(orderDb.getPaySn());
                    List<IntegralOrder> orderList = integralOrderReadMapper.listByExample(orderExample);
                    BigDecimal totalPayAmount = new BigDecimal("0.00");//支付单号下总支付金额
                    for (IntegralOrder order : orderList) {
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
    public void receiveOrder(IntegralOrder orderDb, Integer optRole, Long optUserId, String optUserName, String optRemark) {
        AssertUtil.isTrue(!orderDb.getOrderState().equals(OrderConst.ORDER_STATE_30), "未到收货状态");
        //1.修改订单状态
        IntegralOrder updateOrder = new IntegralOrder();
        updateOrder.setOrderState(OrderConst.ORDER_STATE_40);
        updateOrder.setFinishTime(new Date());
        IntegralOrderExample orderExample = new IntegralOrderExample();
        orderExample.setOrderSn(orderDb.getOrderSn());
        int count = integralOrderWriteMapper.updateByExampleSelective(updateOrder, orderExample);
        AssertUtil.isTrue(count == 0, "确认收货失败");

        //2.记录订单日志
        integralOrderLogModel.insertOrderLog(optRole, optUserId, optUserName, orderDb.getOrderSn(), OrderConst.ORDER_STATE_40, optRemark);
    }

    /**
     * 系统自动取消24小时没有付款的积分订单
     *
     * @return
     */
    public boolean jobSystemCancelIntegralOrder() {
        //获取自动取消时间限制（小时）
        String value = stringRedisTemplate.opsForValue().get("time_limit_of_auto_cancle_order");
        int limitHour = value == null ? 24 : Integer.parseInt(value);

        // 获取当前时间limitHour小时之前的时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -limitHour);

        Date cancelTime = calendar.getTime();

        // 获取超时未付款的订单，此处查询的都是待付款状态的父订单
        IntegralOrderExample example = new IntegralOrderExample();
        example.setCreateTimeBefore(cancelTime);
        example.setOrderState(OrderConst.ORDER_STATE_10);
        List<IntegralOrder> orderList = integralOrderReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(orderList)) {
            //取消订单
            this.cancelOrder(orderList, "支付超时系统自动取消", null, OrderConst.LOG_ROLE_ADMIN, 0L, "system", "系统自动取消订单");
        }
        return true;
    }

    /**
     * 系统自动完成积分订单
     *
     * @return
     */
    public boolean jobSystemFinishIntegralOrder() {
        //取setting表配置信息
        String value = stringRedisTemplate.opsForValue().get("time_limit_of_auto_receive");
        int limit = StringUtils.isEmpty(value) ? 10 : Integer.parseInt(value);

        // 获取当前时间limit天之前的时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -limit);

        Date deliverTime = calendar.getTime();

        //获取发货时间超过limit天的订单
        IntegralOrderExample example = new IntegralOrderExample();
        example.setOrderState(OrderConst.ORDER_STATE_30);
        example.setDeliverTimeBefore(deliverTime);
        List<IntegralOrder> ordersList = integralOrderReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(ordersList)) {
            // 单条数据处理异常不影响其他数据执行
            ordersList.forEach(order -> {
                this.receiveOrder(order, OrderConst.LOG_ROLE_ADMIN, 0L, "system", "系统自动完成订单");
            });
        }
        return true;
    }

    //region 订单导出

    /**
     * 积分订单导出
     *
     * @param request
     * @param response
     * @param example
     */
    public boolean OrderExport(HttpServletRequest request, HttpServletResponse response, IntegralOrderExample example) {
        List<IntegralOrder> orderList = integralOrderReadMapper.listByExample(example);
        List<IntegralOrderExportVO> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(orderList)) {
            orderList.forEach(order -> {
                //获取货品列表
                IntegralOrderProductExample productExample = new IntegralOrderProductExample();
                productExample.setOrderSn(order.getOrderSn());
                List<IntegralOrderProduct> productList = integralOrderProductModel.getIntegralOrderProductList(productExample, null);
                AssertUtil.notEmpty(productList, "获取积分订单货品信息为空，请重试");
                productList.forEach(orderProduct -> {
                    IntegralOrderExportVO vo = new IntegralOrderExportVO(order, orderProduct);
                    list.add(vo);
                });
            });
        }

        // 用于存放生成的excel文件名称
        List<File> fileNames = new ArrayList<>();
        // 每次导出的excel的文件名
        String fileNameS = "";
        // 将excel导出的文件位置
        String filePath = "/upload/";
        File files = new File(filePath);
        // 如果文件夹不存在则创建
        if (!files.exists() && !files.isDirectory()) {
            files.mkdirs();
        }

        //excel标题
        String titles = "订单号,会员名称,店铺名称,兑换积分,兑换现金,兑换时间,支付方式,状态,收货人,手机号码,收货地址,物流公司,物流单号,发票,商品名称,商品规格,商品数量,订单备注";
        String[] titleArray = titles.split(",");

        // 创建Excel文件,每个excel限制2000条数据,超过则打包zip
        int count = list.size();
        int max = 2000;
        int num = count % max;
        int num1;
        if (num == 0) {
            num1 = count / max;
        } else {
            num1 = count / max + 1;
        }

        try {
            if (list.size() <= max) {
                // 导出的excel的文件名
                fileNameS = "订单-" + ExcelExportUtil.getSystemDate() + "-" + list.size() + ".xls";

                //下载单个excle
                byte b[] = ExcelUtil.export("订单", titleArray, list);

                File f = new File(filePath + fileNameS);
                FileUtils.writeByteArrayToFile(f, b, true);

                // 下载
                FileDownloadUtils.setExcelHeadInfo(response, request, fileNameS);

                ServletOutputStream out = response.getOutputStream();

                String path = "/upload/" + fileNameS; //Excel在服务器上的路径

                File file = new File(path);

                FileInputStream fis = new FileInputStream(file);
                ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(4096);

                byte[] cache = new byte[4096];
                for (int offset = fis.read(cache); offset != -1; offset = fis.read(cache)) {
                    byteOutputStream.write(cache, 0, offset);
                }

                byte[] bt = byteOutputStream.toByteArray();

                out.write(bt);
                out.flush();
                out.close();
                fis.close();
                if (file.exists()) {
                    file.delete();
                }

            } else {
                for (int i = 1; i <= num1; i++) {
                    fileNameS = "订单-" + ExcelExportUtil.getSystemDate() + "-" + i + ".xls";
                    List<IntegralOrderExportVO> exportDatas = new ArrayList<>();
                    if (i == num1) {
                        for (int j = max * (i - 1); j < list.size(); j++) {
                            exportDatas.add(list.get(j));
                        }
                    } else {
                        for (int j = max * (i - 1); j < 2000 * i; j++) {
                            exportDatas.add(list.get(j));
                        }
                    }

                    //下载单个excle
                    byte b[] = ExcelUtil.export("订单", titleArray, exportDatas);

                    File f = new File(filePath + fileNameS);
                    FileUtils.writeByteArrayToFile(f, b, true);

                    fileNames.add(new File(filePath + fileNameS + ".xlsx"));
                }

                String zipName = "订单-" + ExcelExportUtil.getSystemDate() + ".zip";
                // 导出压缩文件的全路径
                String zipFilePath = filePath + zipName;
                System.out.println(zipFilePath);
                // 导出zip
                File zip = new File(zipFilePath);

                // 下载
                FileDownloadUtils.setZipDownLoadHeadInfo(response, request, zipName);

                // 将excel文件生成压缩文件
                File srcFile[] = new File[fileNames.size()];
                for (int j = 0, n1 = fileNames.size(); j < n1; j++) {
                    srcFile[j] = new File(String.valueOf(fileNames.get(j)));
                }
                FileDownloadUtils.ZipFiles(srcFile, zip);

                OutputStream outputStream = response.getOutputStream();
                InputStream inputStream = new FileInputStream(zipFilePath);
                byte[] buffer = new byte[8192];
                int i = -1;
                while ((i = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, i);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();

                FileDownloadUtils.deleteDir(new File(filePath));
            }

        } catch (Exception e) {
            try {
                if (!response.isCommitted()) {
                    response.setContentType("text/html;charset=utf-8");
                    response.setHeader("Content-Disposition", "");
                    String html = FileDownloadUtils.getErrorHtml("下载失败");
                    response.getOutputStream().write(html.getBytes(StandardCharsets.UTF_8));
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return true;
    }
    //endregion 订单导出
}