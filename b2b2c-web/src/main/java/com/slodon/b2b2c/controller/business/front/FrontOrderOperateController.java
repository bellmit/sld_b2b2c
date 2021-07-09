package com.slodon.b2b2c.controller.business.front;

import com.slodon.b2b2c.business.dto.OrderSubmitDTO;
import com.slodon.b2b2c.business.dto.OrderSubmitMqConsumerDTO;
import com.slodon.b2b2c.business.dto.OrderSubmitParamDTO;
import com.slodon.b2b2c.business.dto.PreOrderDTO;
import com.slodon.b2b2c.business.example.OrderExample;
import com.slodon.b2b2c.business.pojo.Order;
import com.slodon.b2b2c.business.pojo.OrderProduct;
import com.slodon.b2b2c.core.constant.*;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.uid.GoodsIdGenerator;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.goods.dto.CalculateExpressDTO;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.GoodsPromotion;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.member.pojo.MemberAddress;
import com.slodon.b2b2c.model.business.CartModel;
import com.slodon.b2b2c.model.business.OrderModel;
import com.slodon.b2b2c.model.goods.GoodsFreightTemplateModel;
import com.slodon.b2b2c.model.goods.GoodsModel;
import com.slodon.b2b2c.model.member.MemberAddressModel;
import com.slodon.b2b2c.model.member.MemberModel;
import com.slodon.b2b2c.model.promotion.*;
import com.slodon.b2b2c.model.system.ReasonModel;
import com.slodon.b2b2c.promotion.example.LadderGroupGoodsExample;
import com.slodon.b2b2c.promotion.example.LadderGroupOrderExtendExample;
import com.slodon.b2b2c.promotion.example.PresellGoodsExample;
import com.slodon.b2b2c.promotion.example.PresellOrderExtendExample;
import com.slodon.b2b2c.promotion.pojo.*;
import com.slodon.b2b2c.starter.mq.entity.MemberIntegralVO;
import com.slodon.b2b2c.starter.mq.entity.MessageSendProperty;
import com.slodon.b2b2c.starter.mq.entity.MessageSendVO;
import com.slodon.b2b2c.system.pojo.Reason;
import com.slodon.b2b2c.util.OrderSubmitUtil;
import com.slodon.b2b2c.vo.business.OrderSubmitCheckVO;
import com.slodon.b2b2c.vo.business.OrderSubmitPageVO;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

import static com.slodon.b2b2c.core.constant.StarterConfigConst.*;

@Api(tags = "front-订单操作")
@RestController
@RequestMapping("v3/business/front/orderOperate")
@Slf4j
public class FrontOrderOperateController {

    @Resource
    private CartModel cartModel;
    @Resource
    private OrderModel orderModel;
    @Resource
    private MemberModel memberModel;
    @Resource
    private MemberAddressModel memberAddressModel;
    @Resource
    private GoodsFreightTemplateModel goodsFreightTemplateModel;
    @Resource
    private PromotionCommonModel promotionCommonModel;
    @Resource
    private ReasonModel reasonModel;
    @Resource
    private GoodsModel goodsModel;
    @Resource
    private PresellModel presellModel;
    @Resource
    private PresellGoodsModel presellGoodsModel;
    @Resource
    private PresellOrderExtendModel presellOrderExtendModel;
    @Resource
    private LadderGroupModel ladderGroupModel;
    @Resource
    private LadderGroupGoodsModel ladderGroupGoodsModel;
    @Resource
    private LadderGroupOrderExtendModel ladderGroupOrderExtendModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private OrderSubmitUtil orderSubmitUtil;

    @PostMapping("check")
    @ApiOperation("检测购物车信息")
    public JsonResult<OrderSubmitCheckVO> check(HttpServletRequest request, @RequestBody OrderSubmitParamDTO dto) {
        Member member = UserUtil.getUser(request, Member.class);

        //构造计算优惠dto
        OrderSubmitDTO orderSubmitDTO = orderSubmitUtil.getOrderSubmitDTO(dto, member.getMemberId(), false);
        OrderSubmitCheckVO vo = cartModel.checkCart(orderSubmitDTO);
        if (vo.getState().equals(OrderSubmitCheckVO.STATE_0)) {
            //检测通过
            return SldResponse.success();
        } else {
            //检测不通过
            return SldResponse.failSpecial(vo);
        }

    }

    @PostMapping("confirm")
    @ApiOperation("确认订单接口（去结算），获取提交订单页数据(注：调用此接口之前需先调用[检测购物车信息]接口，state=200才可以调用此接口)")
    public JsonResult<OrderSubmitPageVO> confirm(HttpServletRequest request, @RequestBody OrderSubmitParamDTO dto) {
        Member member = UserUtil.getUser(request, Member.class);

        //构造返回数据
        OrderSubmitPageVO vo = null;

        //构造计算优惠dto
        OrderSubmitDTO orderSubmitDTO = orderSubmitUtil.getOrderSubmitDTO(dto, member.getMemberId(), true);

        //计算运费
        List<BigDecimal> expressFeeList = new ArrayList<>();
        MemberAddress memberAddress = null;
        if (!StringUtil.isNullOrZero(dto.getAddressId())) {
            memberAddress = memberAddressModel.getMemberAddressByAddressId(dto.getAddressId());
        }
        for (OrderSubmitDTO.OrderInfo orderInfo : orderSubmitDTO.getOrderInfoList()) {
            BigDecimal expressFee = new BigDecimal("0.00");
            if (memberAddress != null) {
                //传了地址信息，计算运费
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
                expressFee = goodsFreightTemplateModel.calculateExpressFee(calculateExpressDTO);
            }
            expressFeeList.add(expressFee.setScale(2));
        }

        GoodsPromotion singlePromotion = null;
        if (!dto.getIsCart()) {
            //立即购买，查询是否为预售
            singlePromotion = cartModel.getSinglePromotion(dto.getProductId());
        }
        if (singlePromotion != null && singlePromotion.getPromotionType().equals(PromotionConst.PROMOTION_TYPE_103)) {
            //预售确认
            vo = preSaleConfirm(dto, orderSubmitDTO, singlePromotion, expressFeeList);
        } else if (singlePromotion != null && singlePromotion.getPromotionType().equals(PromotionConst.PROMOTION_TYPE_105)) {
            //阶梯团确认
            vo = ladderGroupConfirm(dto, orderSubmitDTO, singlePromotion, expressFeeList);
        } else {
            //调用活动模块计算优惠
            orderSubmitDTO = promotionCommonModel.orderSubmitCalculationDiscount(orderSubmitDTO, dto.getSource());

            vo = new OrderSubmitPageVO(orderSubmitDTO, expressFeeList);

            //是否可以开增值税发票
            boolean isVatInvoice = true;
            Map<Long, Goods> goodsMap = new HashMap<>();//保存已查询的商品，减少查库次数
            for (OrderSubmitDTO.OrderInfo orderInfo : orderSubmitDTO.getOrderInfoList()) {
                for (OrderSubmitDTO.OrderInfo.OrderProductInfo orderProductInfo : orderInfo.getOrderProductInfoList()) {
                    if (goodsMap.containsKey(orderProductInfo.getGoodsId())) {
                        continue;
                    }
                    Goods goods = goodsModel.getGoodsByGoodsId(orderProductInfo.getGoodsId());
                    if (goods.getIsVatInvoice() != null && goods.getIsVatInvoice() == GoodsConst.IS_VAT_INVOICE_NO) {
                        //商品不允许开增值税发票,跳出循环
                        isVatInvoice = false;
                        break;
                    }
                    goodsMap.put(goods.getGoodsId(), goods);
                }
                if (!isVatInvoice) {
                    //有一个商品不允许开增值税发票,跳出循环
                    break;
                }
            }
            vo.setIsVatInvoice(isVatInvoice);
        }
        return SldResponse.success(vo);
    }

    /**
     * 预售确认
     *
     * @param dto
     * @param orderSubmitDTO
     * @param singlePromotion
     * @return
     */
    public OrderSubmitPageVO preSaleConfirm(OrderSubmitParamDTO dto, OrderSubmitDTO orderSubmitDTO, GoodsPromotion singlePromotion, List<BigDecimal> expressFeeList) {
        //构造返回数据
        OrderSubmitPageVO vo = new OrderSubmitPageVO(orderSubmitDTO, expressFeeList);
        vo.setTotalAmount(new BigDecimal(vo.getTotalAmount()).subtract(expressFeeList.get(0)).toString());
        vo.setPromotionType(PromotionConst.PROMOTION_TYPE_103);
        //查询预售活动
        Presell presell = presellModel.getPresellByPresellId(singlePromotion.getPromotionId());
        //查询预售商品
        PresellGoodsExample example = new PresellGoodsExample();
        example.setPresellId(singlePromotion.getPromotionId());
        example.setProductId(dto.getProductId());
        List<PresellGoods> presellGoodsList = presellGoodsModel.getPresellGoodsList(example, null);
        AssertUtil.notEmpty(presellGoodsList, "预售商品不存在");
        PresellGoods presellGoods = presellGoodsList.get(0);

        OrderSubmitPageVO.PresellInfo presellInfo = new OrderSubmitPageVO.PresellInfo();
        presellInfo.setPresellId(presell.getPresellId());
        presellInfo.setType(presell.getType());
        presellInfo.setPresellState(OrderConst.ORDER_SUB_STATE_101);
        presellInfo.setDeliverTime(presell.getDeliverTime());
        presellInfo.setProductId(presellGoods.getProductId());
        presellInfo.setPresellPrice(presellGoods.getPresellPrice().toString());
        presellInfo.setFirstMoney(presellGoods.getFirstMoney().multiply(new BigDecimal(dto.getNumber())).toString());
        presellInfo.setSecondMoney(presellGoods.getSecondMoney().multiply(new BigDecimal(dto.getNumber())).toString());
        if (!StringUtil.isNullOrZero(presellGoods.getFirstExpand())) {
            presellInfo.setFirstExpand(presellGoods.getFirstExpand().multiply(new BigDecimal(dto.getNumber())).toString());
            presellInfo.setFinalDiscount(presellGoods.getFirstExpand().multiply(new BigDecimal(dto.getNumber()))
                    .subtract(presellGoods.getFirstMoney().multiply(new BigDecimal(dto.getNumber()))).toString());
        }
        presellInfo.setRemainStartTime(presell.getRemainStartTime());
        vo.setPresellInfo(presellInfo);
        Goods goods = goodsModel.getGoodsByGoodsId(presellGoods.getGoodsId());
        vo.setIsVatInvoice(goods.getIsVatInvoice() == null || goods.getIsVatInvoice() == GoodsConst.IS_VAT_INVOICE_YES);
        return vo;
    }

    /**
     * 阶梯团确认
     *
     * @param dto
     * @param orderSubmitDTO
     * @param singlePromotion
     * @return
     */
    public OrderSubmitPageVO ladderGroupConfirm(OrderSubmitParamDTO dto, OrderSubmitDTO orderSubmitDTO, GoodsPromotion singlePromotion, List<BigDecimal> expressFeeList) {
        //构造返回数据
        OrderSubmitPageVO vo = new OrderSubmitPageVO(orderSubmitDTO, expressFeeList);
        vo.setTotalAmount(new BigDecimal(vo.getTotalAmount()).subtract(expressFeeList.get(0)).toString());
        vo.setPromotionType(PromotionConst.PROMOTION_TYPE_105);
        //查询阶梯团活动
        LadderGroup ladderGroup = ladderGroupModel.getLadderGroupByGroupId(singlePromotion.getPromotionId());
        //查询阶梯团商品
        LadderGroupGoodsExample example = new LadderGroupGoodsExample();
        example.setGroupId(singlePromotion.getPromotionId());
        example.setProductId(dto.getProductId());
        List<LadderGroupGoods> groupGoodsList = ladderGroupGoodsModel.getLadderGroupGoodsList(example, null);
        AssertUtil.notEmpty(groupGoodsList, "阶梯团商品不存在");
        LadderGroupGoods groupGoods = groupGoodsList.get(0);

        OrderSubmitPageVO.LadderGroupInfo ladderGroupInfo = new OrderSubmitPageVO.LadderGroupInfo();
        ladderGroupInfo.setGroupId(ladderGroup.getGroupId());
        ladderGroupInfo.setLadderGroupState(OrderConst.ORDER_SUB_STATE_101);
        ladderGroupInfo.setProductId(groupGoods.getProductId());
        ladderGroupInfo.setProductPrice(groupGoods.getProductPrice().toString());
        ladderGroupInfo.setAdvanceDeposit(groupGoods.getAdvanceDeposit().multiply(new BigDecimal(dto.getNumber())).toString());
        ladderGroupInfo.setRemainAmount((groupGoods.getProductPrice().subtract(groupGoods.getAdvanceDeposit())).multiply(new BigDecimal(dto.getNumber())).toString());
        ladderGroupInfo.setRemainStartTime(ladderGroup.getEndTime());
        vo.setLadderGroupInfo(ladderGroupInfo);
        Goods goods = goodsModel.getGoodsByGoodsId(groupGoods.getGoodsId());
        vo.setIsVatInvoice(goods.getIsVatInvoice() == null || goods.getIsVatInvoice() == GoodsConst.IS_VAT_INVOICE_YES);
        return vo;
    }

    @PostMapping("submit")
    @ApiOperation("提交订单接口(注：调用此接口之前需先调用[检测购物车信息]接口，state=200才可以调用此接口)")
    @ApiResponses(
            @ApiResponse(code = 200, message = "data.paySn:支付单号;data.needPay:是否需要支付，true==是")
    )
    public JsonResult submit(HttpServletRequest request, @RequestBody OrderSubmitParamDTO dto) {
        Member member = UserUtil.getUser(request, Member.class);
        AssertUtil.notNullOrZero(dto.getAddressId(), "请选择收货地址");

        PreOrderDTO preOrderDTO = new PreOrderDTO();
        if (!dto.getIsCart()) {
            //立即购买，查询订单类型、是否计算优惠
            GoodsPromotion singlePromotion = cartModel.getSinglePromotion(dto.getProductId());
            if (singlePromotion != null && !dto.getIsAloneBuy()) {
                preOrderDTO = promotionCommonModel.preOrderSubmit(singlePromotion.getPromotionType(),
                        singlePromotion.getPromotionId(), dto.getProductId(), dto.getNumber(), member.getMemberId());
            }
        }

        //构造入mq对象
        String paySn = GoodsIdGenerator.getPaySn();//统一生成支付单号
        // 将表标识放入redis，key=标识前缀+paySn，mq处理成功或失败后删除标识
        stringRedisTemplate.opsForValue().set(StarterConfigConst.ORDER_SUBMIT_MQ_REDIS_PREFIX + paySn, "");
        OrderSubmitMqConsumerDTO consumerDTO = new OrderSubmitMqConsumerDTO();
        consumerDTO.setParamDTO(dto);
        consumerDTO.setMemberId(member.getMemberId());
        consumerDTO.setPaySn(paySn);
        consumerDTO.setPreOrderDTO(preOrderDTO);
        //入队列
        rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_ORDER_SUBMIT, consumerDTO);

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("paySn", paySn);

        if (preOrderDTO.getOrderType().equals(PromotionConst.PROMOTION_TYPE_104)) {
            log.debug("秒杀订单---------------");
        } else {
            log.debug("普通订单--------------");
        }

        return SldResponse.success(dataMap);
    }

    @PostMapping("cancel")
    @ApiOperation("取消订单接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parentSn", value = "父订单号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "reasonId", value = "取消原因", required = true, paramType = "query")
    })
    public JsonResult cancelOrder(HttpServletRequest request, String parentSn, Integer reasonId) {
        Member member = UserUtil.getUser(request, Member.class);

        //订单信息
        OrderExample orderExample = new OrderExample();
        orderExample.setParentSn(parentSn);
        orderExample.setMemberId(member.getMemberId());
        orderExample.setOrderState(OrderConst.ORDER_STATE_10);
        List<Order> orderList = orderModel.getOrderList(orderExample, null);

        AssertUtil.notEmpty(orderList, "请选择要取消的订单");

        //查询取消原因
        Reason reason = reasonModel.getReasonByReasonId(reasonId);
        AssertUtil.notNull(reason, "取消原因不存在");

        //订单取消
        orderModel.cancelOrder(orderList, reason.getContent(), null, OrderConst.LOG_ROLE_MEMBER, Long.valueOf(member.getMemberId()), member.getMemberName(), "会员取消订单");

        return SldResponse.success("订单取消成功");
    }

    @PostMapping("receive")
    @ApiOperation("确认收货接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单号", required = true, paramType = "query")
    })
    public JsonResult receive(HttpServletRequest request, String orderSn) {
        Member member = UserUtil.getUser(request, Member.class);

        //订单信息
        Order order = orderModel.getOrderByOrderSn(orderSn);
        AssertUtil.isTrue(!order.getMemberId().equals(member.getMemberId()), "不能操作他人的订单");
        AssertUtil.isTrue(order.getLockState() > 0, "售后中的订单无法操作");


        //确认收货
        orderModel.receiveOrder(order, OrderConst.LOG_ROLE_MEMBER, Long.valueOf(member.getMemberId()), member.getMemberName(), "会员确认收货");

        //购买商品赠送积分比例
        String goodsBuy = stringRedisTemplate.opsForValue().get("integral_present_goods_buy");
        int buyGoodsIntegral = StringUtil.isEmpty(goodsBuy) ? 0 : Integer.parseInt(goodsBuy);
        //订单最多赠送积分
        String orderMax = stringRedisTemplate.opsForValue().get("integral_present_order_max");
        int orderMaxIntegral = StringUtil.isEmpty(orderMax) ? 0 : Integer.parseInt(orderMax);
        if (buyGoodsIntegral > 0) {
            Integer sendIntegral;
            int orderIntegral = order.getOrderAmount().intValue() / buyGoodsIntegral;
            if (orderIntegral > 0) {
                if (orderMaxIntegral > 0 && orderIntegral > orderMaxIntegral) {
                    sendIntegral = orderMaxIntegral;
                } else {
                    sendIntegral = orderIntegral;
                }
                MemberIntegralVO memberIntegralVO = new MemberIntegralVO(member.getMemberId(), member.getMemberName(), sendIntegral,
                        MemberIntegralLogConst.TYPE_3, "商品购买（订单号：" + orderSn + "）", orderSn, 0, "");
                rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_MEMBER_INTEGRAL, memberIntegralVO);
                //查询会员信息
                Member memberDb = memberModel.getMemberByMemberId(member.getMemberId());
                memberDb.setMemberIntegral(memberDb.getMemberIntegral() + sendIntegral);
                //发送积分变动消息通知
                this.sendMsgIntegralChange(memberDb, "购买商品赠送积分（订单号：" + orderSn + "）");
            }
        }
        return SldResponse.success("确认收货成功");
    }

    @ApiOperation("删除订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单号", required = true, paramType = "query")
    })
    @PostMapping("delete")
    public JsonResult deleteOrder(HttpServletRequest request, String orderSn) {
        Member member = UserUtil.getUser(request, Member.class);

        //订单信息
        Order order = orderModel.getOrderByOrderSn(orderSn);
        AssertUtil.isTrue(!order.getMemberId().equals(member.getMemberId()), "您无权删除他人订单");
        AssertUtil.isTrue(!order.getOrderState().equals(OrderConst.ORDER_STATE_0)
                && !order.getOrderState().equals(OrderConst.ORDER_STATE_40), "您无权删除他人订单");
        //修改订单状态
        Order updateOrder = new Order();
        updateOrder.setDeleteState(OrderConst.DELETE_STATE_1);
        OrderExample example = new OrderExample();
        example.setOrderSn(orderSn);
        orderModel.updateOrderByExample(updateOrder, example);
        return SldResponse.success("删除成功");
    }

    @ApiOperation("修改地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "addressId", value = "地址id", required = true, paramType = "query")
    })
    @PostMapping("updateAddress")
    public JsonResult updateAddress(HttpServletRequest request, String orderSn, Integer addressId) {
        Member member = UserUtil.getUser(request, Member.class);

        Order order = orderModel.getOrdersWithOpByOrderSn(orderSn);
        AssertUtil.isTrue(!order.getMemberId().equals(member.getMemberId()), "您无权修改他人订单");
        AssertUtil.isTrue(!order.getOrderState().equals(OrderConst.ORDER_STATE_10)
                && !order.getOrderState().equals(OrderConst.ORDER_STATE_20), "该订单地址无法修改");

        MemberAddress memberAddress = memberAddressModel.getMemberAddressByAddressId(addressId);
        AssertUtil.notNull(memberAddress, "地址不存在");

        //计算新运费
        BigDecimal totalFee = BigDecimal.ZERO;
        //构造计算运费的dto
        CalculateExpressDTO calculateExpressDTO = new CalculateExpressDTO();
        calculateExpressDTO.setCityCode(memberAddress.getCityCode());
        for (OrderProduct orderProduct : order.getOrderProductList()) {
            calculateExpressDTO.getProductList()
                    .add(new CalculateExpressDTO.ProductInfo(
                            orderProduct.getGoodsId(),
                            orderProduct.getProductId(),
                            orderProduct.getProductShowPrice(),
                            orderProduct.getProductNum()));
            BigDecimal fee = goodsFreightTemplateModel.calculateExpressFee(calculateExpressDTO);
            totalFee = totalFee.add(fee);
        }
        AssertUtil.isTrue(totalFee.compareTo(order.getExpressFee()) != 0, "地址修改后运费发生变化，暂不支持修改");

        Order updateOrder = new Order();
        updateOrder.setReceiverName(memberAddress.getMemberName());
        updateOrder.setReceiverAreaInfo(memberAddress.getAddressAll());
        updateOrder.setReceiverAddress(memberAddress.getDetailAddress());
        updateOrder.setReceiverMobile(memberAddress.getTelMobile());
        OrderExample example = new OrderExample();
        example.setOrderSn(orderSn);
        orderModel.updateOrderByExample(updateOrder, example);
        return SldResponse.success("修改成功");
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

    @ApiOperation("定金预售和阶梯团订单尾款确认")
    @PostMapping("balanceConfirm")
    public JsonResult<OrderSubmitPageVO> balanceConfirm(HttpServletRequest request, @RequestBody OrderSubmitParamDTO dto) {
        Member member = UserUtil.getUser(request, Member.class);

        Order order = orderModel.getOrderByOrderSn(dto.getOrderSn());

        //构造计算优惠dto
        OrderSubmitDTO orderSubmitDTO = orderSubmitUtil.getOrderSubmitDTO(dto, member.getMemberId(), order);

        //调用活动模块计算优惠
        orderSubmitDTO = promotionCommonModel.orderSubmitCalculationDiscount(orderSubmitDTO, dto.getSource());

        //计算运费
        List<BigDecimal> expressFeeList = new ArrayList<>();
        BigDecimal expressFee = order.getExpressFee();
        expressFeeList.add(expressFee);

        //构造返回数据
        OrderSubmitPageVO vo = new OrderSubmitPageVO(orderSubmitDTO, expressFeeList);
        if (order.getOrderType() == PromotionConst.PROMOTION_TYPE_103) {
            vo.setPromotionType(PromotionConst.PROMOTION_TYPE_103);
            vo = preSaleBalanceConfirm(vo, order.getOrderSn());
        } else if (order.getOrderType() == PromotionConst.PROMOTION_TYPE_105) {
            vo.setPromotionType(PromotionConst.PROMOTION_TYPE_105);
            vo = ladderGroupBalanceConfirm(vo, order.getOrderSn());
        }
        return SldResponse.success(vo);
    }

    /**
     * 预售尾款确认
     *
     * @param vo
     * @param orderSn
     * @return
     */
    public OrderSubmitPageVO preSaleBalanceConfirm(OrderSubmitPageVO vo, String orderSn) {
        //查询预售订单扩展信息
        PresellOrderExtendExample example = new PresellOrderExtendExample();
        example.setOrderSn(orderSn);
        List<PresellOrderExtend> orderExtendList = presellOrderExtendModel.getPresellOrderExtendList(example, null);
        AssertUtil.notEmpty(orderExtendList, "获取预售订单扩展信息为空");
        PresellOrderExtend orderExtend = orderExtendList.get(0);

        OrderSubmitPageVO.PresellInfo presellInfo = new OrderSubmitPageVO.PresellInfo();
        presellInfo.setPresellId(orderExtend.getPresellId());
        if (orderExtend.getIsAllPay() == OrderConst.IS_ALL_PAY_1) {
            presellInfo.setType(PreSellConst.PRE_SELL_TYPE_2);
        } else {
            presellInfo.setType(PreSellConst.PRE_SELL_TYPE_1);
        }
        presellInfo.setPresellState(orderExtend.getOrderSubState());
        presellInfo.setDeliverTime(orderExtend.getDeliverTime());
        presellInfo.setProductId(orderExtend.getProductId());
        presellInfo.setPresellPrice(StringUtil.isNullOrZero(orderExtend.getPresellPrice()) ? null : orderExtend.getPresellPrice().toString());
        presellInfo.setFirstMoney(orderExtend.getDepositAmount().multiply(new BigDecimal(orderExtend.getProductNum())).toString());
        if (!StringUtil.isNullOrZero(orderExtend.getFirstExpand())) {
            presellInfo.setFirstExpand(orderExtend.getFirstExpand().multiply(new BigDecimal(orderExtend.getProductNum())).toString());
            presellInfo.setFinalDiscount((orderExtend.getFirstExpand().subtract(orderExtend.getDepositAmount())).multiply(new BigDecimal(orderExtend.getProductNum())).toString());
        }
        presellInfo.setSecondMoney(StringUtil.isNullOrZero(orderExtend.getRemainAmount()) ? null : orderExtend.getRemainAmount().multiply(new BigDecimal(orderExtend.getProductNum())).toString());
        vo.setPresellInfo(presellInfo);
        Goods goods = goodsModel.getGoodsByGoodsId(orderExtend.getGoodsId());
        vo.setIsVatInvoice(goods.getIsVatInvoice() == null || goods.getIsVatInvoice() == GoodsConst.IS_VAT_INVOICE_YES);

        if (!StringUtil.isNullOrZero(orderExtend.getFirstExpand())) {
            vo.setTotalDiscount(orderExtend.getFirstExpand().multiply(new BigDecimal(orderExtend.getProductNum())).toString());
        } else {
            vo.setTotalDiscount(orderExtend.getDepositAmount().multiply(new BigDecimal(orderExtend.getProductNum())).toString());
        }
        vo.setTotalAmount(new BigDecimal(vo.getTotalAmount()).subtract(new BigDecimal(vo.getTotalDiscount())).toString());
        return vo;
    }

    /**
     * 阶梯团尾款确认
     *
     * @param vo
     * @param orderSn
     * @return
     */
    public OrderSubmitPageVO ladderGroupBalanceConfirm(OrderSubmitPageVO vo, String orderSn) {
        //查询阶梯团订单扩展信息
        LadderGroupOrderExtendExample example = new LadderGroupOrderExtendExample();
        example.setOrderSn(orderSn);
        List<LadderGroupOrderExtend> orderExtendList = ladderGroupOrderExtendModel.getLadderGroupOrderExtendList(example, null);
        AssertUtil.notEmpty(orderExtendList, "获取阶梯团订单扩展信息为空");
        LadderGroupOrderExtend orderExtend = orderExtendList.get(0);

        OrderSubmitPageVO.LadderGroupInfo ladderGroupInfo = new OrderSubmitPageVO.LadderGroupInfo();
        ladderGroupInfo.setGroupId(orderExtend.getGroupId());
        ladderGroupInfo.setLadderGroupState(orderExtend.getOrderSubState());
        ladderGroupInfo.setProductId(orderExtend.getProductId());
        ladderGroupInfo.setProductPrice(orderExtend.getProductPrice().toString());
        ladderGroupInfo.setAdvanceDeposit(orderExtend.getAdvanceDeposit().multiply(new BigDecimal(orderExtend.getProductNum())).toString());
        ladderGroupInfo.setRemainAmount(orderExtend.getRemainAmount().multiply(new BigDecimal(orderExtend.getProductNum())).toString());
        if (orderExtend.getOrderSubState() != LadderGroupConst.ORDER_SUB_STATE_1) {
            if (!StringUtils.isEmpty(vo.getTotalDiscount()) && !StringUtil.isNullOrZero(new BigDecimal(vo.getTotalDiscount()))) {
                ladderGroupInfo.setRealRemainAmount(new BigDecimal(ladderGroupInfo.getRemainAmount()).subtract(new BigDecimal(vo.getTotalDiscount())).toString());
            } else {
                ladderGroupInfo.setRealRemainAmount(ladderGroupInfo.getRemainAmount());
            }
        }
        ladderGroupInfo.setRemainStartTime(orderExtend.getRemainStartTime());
        vo.setLadderGroupInfo(ladderGroupInfo);
        Goods goods = goodsModel.getGoodsByGoodsId(orderExtend.getGoodsId());
        vo.setIsVatInvoice(goods.getIsVatInvoice() == null || goods.getIsVatInvoice() == GoodsConst.IS_VAT_INVOICE_YES);
        return vo;
    }

    @ApiOperation("尾款提交订单接口")
    @ApiResponses(
            @ApiResponse(code = 200, message = "data.paySn:支付单号")
    )
    @PostMapping("balanceSubmit")
    public JsonResult balanceSubmit(HttpServletRequest request, @RequestBody OrderSubmitParamDTO dto) {
        AssertUtil.notNullOrZero(dto.getAddressId(), "请选择收货地址");
        AssertUtil.notEmpty(dto.getOrderSn(), "订单号不能为空");

        Member member = UserUtil.getUser(request, Member.class);

        Order order = orderModel.getOrderByOrderSn(dto.getOrderSn());

        //构造计算优惠dto
        OrderSubmitDTO orderSubmitDTO = orderSubmitUtil.getOrderSubmitDTO(dto, member.getMemberId(), order);

        if (order.getOrderType() == PromotionConst.PROMOTION_TYPE_103) {
            //查询预售订单扩展信息
            PresellOrderExtendExample example = new PresellOrderExtendExample();
            example.setOrderSn(dto.getOrderSn());
            List<PresellOrderExtend> orderExtendList = presellOrderExtendModel.getPresellOrderExtendList(example, null);
            AssertUtil.notEmpty(orderExtendList, "获取预售订单扩展信息为空");
            PresellOrderExtend orderExtend = orderExtendList.get(0);
            if (!StringUtil.isNullOrZero(orderExtend.getFirstExpand())) {
                orderSubmitDTO.setTotalDiscount(orderExtend.getFirstExpand().multiply(new BigDecimal(orderExtend.getProductNum())));
            } else {
                orderSubmitDTO.setTotalDiscount(orderExtend.getDepositAmount().multiply(new BigDecimal(orderExtend.getProductNum())));
            }
            orderSubmitDTO.setTotalAmount(orderSubmitDTO.getTotalAmount().subtract(orderSubmitDTO.getTotalDiscount()));
        } else if (order.getOrderType() == PromotionConst.PROMOTION_TYPE_105) {
            //查询阶梯团订单扩展信息
            LadderGroupOrderExtendExample example = new LadderGroupOrderExtendExample();
            example.setOrderSn(dto.getOrderSn());
            List<LadderGroupOrderExtend> orderExtendList = ladderGroupOrderExtendModel.getLadderGroupOrderExtendList(example, null);
            AssertUtil.notEmpty(orderExtendList, "获取阶梯团订单扩展信息为空");
            LadderGroupOrderExtend orderExtend = orderExtendList.get(0);
            orderSubmitDTO.setTotalDiscount(orderExtend.getAdvanceDeposit().multiply(new BigDecimal(orderExtend.getProductNum())));
            orderSubmitDTO.setTotalAmount(orderSubmitDTO.getTotalAmount().subtract(orderSubmitDTO.getTotalDiscount()));
        }

        //调用活动模块计算优惠
        orderSubmitDTO = promotionCommonModel.orderSubmitCalculationDiscount(orderSubmitDTO, dto.getSource());

        orderModel.submitBalanceOrder(orderSubmitDTO, dto, member, order);

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("paySn", order.getPaySn());
        return SldResponse.success(dataMap);
    }
}
