package com.slodon.b2b2c.controller.business.front;

import com.slodon.b2b2c.business.example.*;
import com.slodon.b2b2c.business.pojo.*;
import com.slodon.b2b2c.core.constant.*;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.*;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.model.business.*;
import com.slodon.b2b2c.model.promotion.LadderGroupModel;
import com.slodon.b2b2c.model.promotion.LadderGroupOrderExtendModel;
import com.slodon.b2b2c.model.promotion.PresellOrderExtendModel;
import com.slodon.b2b2c.model.seller.StoreModel;
import com.slodon.b2b2c.promotion.example.LadderGroupOrderExtendExample;
import com.slodon.b2b2c.promotion.example.PresellOrderExtendExample;
import com.slodon.b2b2c.promotion.pojo.LadderGroup;
import com.slodon.b2b2c.promotion.pojo.LadderGroupOrderExtend;
import com.slodon.b2b2c.promotion.pojo.PresellOrderExtend;
import com.slodon.b2b2c.seller.pojo.Store;
import com.slodon.b2b2c.vo.business.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Api(tags = "front-订单管理")
@RestController
@RequestMapping("v3/business/front/orderInfo")
public class FrontOrderInfoController {

    @Resource
    private OrderModel orderModel;
    @Resource
    private OrderExtendModel orderExtendModel;
    @Resource
    private OrderProductModel orderProductModel;
    @Resource
    private OrderLogModel orderLogModel;
    @Resource
    private OrderPayModel orderPayModel;
    @Resource
    private OrderReturnModel orderReturnModel;
    @Resource
    private StoreModel storeModel;
    @Resource
    private PresellOrderExtendModel presellOrderExtendModel;
    @Resource
    private LadderGroupOrderExtendModel ladderGroupOrderExtendModel;
    @Resource
    private LadderGroupModel ladderGroupModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("订单列表相关接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单号", paramType = "query"),
            @ApiImplicitParam(name = "orderState", value = "订单状态：0-已取消；10-待付款订单；20-代发货订单；30-待收货订单；40-已完成;50-已关闭", paramType = "query"),
            @ApiImplicitParam(name = "evaluateState", value = "订单评价状态：1.未评价", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<MemberOrderListVO>> getList(HttpServletRequest request, String orderSn, Integer orderState, Integer evaluateState) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        Member member = UserUtil.getUser(request, Member.class);

        List<MemberOrderListVO> vos = new ArrayList<>();
        //根据父订单分组
        String fields = "parent_sn";
        OrderExample orderExample = new OrderExample();
        orderExample.setOrderState(orderState);
        orderExample.setOrderSnLike(orderSn);
        orderExample.setEvaluateState(evaluateState);
        orderExample.setMemberId(member.getMemberId());
        orderExample.setGroupBy("parent_sn");
        orderExample.setOrderBy("parent_sn desc");
        orderExample.setDeleteState(OrderConst.DELETE_STATE_0);
        List<Order> orderListParent = orderModel.getOrderFieldList(fields, orderExample, pager);

        //遍历父订单列表
        for (Order orderParent : orderListParent) {
            OrderExample orderExample1 = new OrderExample();
            orderExample1.setParentSn(orderParent.getParentSn());
            orderExample1.setOrderState(orderState);
            //获取子订单列表
            List<Order> orderListChild = orderModel.getOrderList(orderExample1, null);

            List<Order> showList = new ArrayList<>();
            //合计金额
            BigDecimal totalMoney = BigDecimal.ZERO;
            //未付款未拆单共计商品数
            Integer goodsNum = 0;
            //订单子状态
            Integer orderSubState = 0;
            //定金剩余时间
            Date depositRemainTime = null;
            //尾款开始时间
            Date remainTime = null;
            //预售发货时间
            Date deliverTime = null;
            //是否有定金
            boolean isHasDeposit = false;
            //是否退还定金
            Boolean isRefundDeposit = false;
            //遍历子订单
            for (Order orderChild : orderListChild) {
                if (!orderChild.getOrderState().equals(OrderConst.ORDER_STATE_10)) {
                    //已付款订单共计商品数
                    Integer productNum = 0;
                    //查询预售订单扩展信息
                    if (orderChild.getOrderType() == PromotionConst.PROMOTION_TYPE_103) {
                        PresellOrderExtendExample presellOrderExtendExample = new PresellOrderExtendExample();
                        presellOrderExtendExample.setOrderSn(orderChild.getOrderSn());
                        List<PresellOrderExtend> presellOrderExtendList = presellOrderExtendModel.getPresellOrderExtendList(presellOrderExtendExample, null);
                        AssertUtil.notEmpty(presellOrderExtendList, "获取预售订单扩展信息为空");
                        PresellOrderExtend presellOrderExtend = presellOrderExtendList.get(0);
                        orderSubState = presellOrderExtend.getOrderSubState();
                        if (presellOrderExtend.getIsAllPay() == OrderConst.IS_ALL_PAY_0) {
                            depositRemainTime = presellOrderExtend.getDepositEndTime();
                            remainTime = presellOrderExtend.getRemainStartTime();
                            isHasDeposit = true;
                        }
                        deliverTime = presellOrderExtend.getDeliverTime();
                    }
                    //查询阶梯团订单扩展信息
                    if (orderChild.getOrderType() == PromotionConst.PROMOTION_TYPE_105) {
                        LadderGroupOrderExtendExample groupOrderExtendExample = new LadderGroupOrderExtendExample();
                        groupOrderExtendExample.setOrderSn(orderChild.getOrderSn());
                        List<LadderGroupOrderExtend> groupOrderExtendList = ladderGroupOrderExtendModel.getLadderGroupOrderExtendList(groupOrderExtendExample, null);
                        AssertUtil.notEmpty(groupOrderExtendList, "获取阶梯团订单扩展信息为空");
                        LadderGroupOrderExtend groupOrderExtend = groupOrderExtendList.get(0);
                        orderSubState = groupOrderExtend.getOrderSubState();
                        remainTime = groupOrderExtend.getRemainStartTime();
                        isHasDeposit = true;
                        //查询是否退还定金
                        LadderGroup ladderGroup = ladderGroupModel.getLadderGroupByGroupId(groupOrderExtend.getGroupId());
                        AssertUtil.notNull(ladderGroup, "获取阶梯团活动信息为空，请重试！");
                        if (ladderGroup.getIsRefundDeposit() == LadderGroupConst.IS_REFUND_DEPOSIT_1) {
                            isRefundDeposit = true;
                        }
                    }
                    MemberOrderListVO vo = new MemberOrderListVO(orderChild, orderSubState, remainTime, isHasDeposit);
                    vo.setIsRefundDeposit(isRefundDeposit);
                    if (orderChild.getOrderType() == PromotionConst.PROMOTION_TYPE_103) {
                        if (depositRemainTime != null) {
                            long time1 = depositRemainTime.getTime();
                            long time2 = new Date().getTime();
                            long differTime = (time1 - time2) / 1000;
                            vo.setDepositRemainTime(differTime < 0 ? 0 : differTime);
                            long time3 = remainTime.getTime();
                            long remainEndTime = (time3 - time2) / 1000;
                            vo.setRemainEndTime(remainEndTime < 0 ? 0 : remainEndTime);
                        }
                    }
                    vo.setDeliverTime(TimeUtil.getZDDay(deliverTime));
                    vo.setOrderSubState(orderSubState);
                    //获取商家名称
                    vo.setStoreId(orderChild.getStoreId());
                    vo.setStoreName(orderChild.getStoreName());
                    Store store = storeModel.getStoreByStoreId(orderChild.getStoreId());
                    //默认店铺logo
                    if (StringUtils.isEmpty(store.getStoreLogo())) {
                        store.setStoreLogo(stringRedisTemplate.opsForValue().get("default_image_store_logo"));
                    }
                    vo.setStoreLogo(FileUrlUtil.getFileUrl(store.getStoreLogo(), null));
                    //合计金额
                    totalMoney = totalMoney.add(orderChild.getOrderAmount());
                    vo.setTotalMoney(totalMoney);
                    //获取订单货品列表信息
                    OrderProductExample orderProductExample = new OrderProductExample();
                    orderProductExample.setOrderSn(orderChild.getOrderSn());
                    List<OrderProduct> orderProductList = orderProductModel.getOrderProductList(orderProductExample, null);
                    List<OrderProductListVO> productListVOS = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(orderProductList)) {
                        for (OrderProduct orderProduct : orderProductList) {
                            //有退货,查询退货是否已完成
                            OrderReturnExample returnExample = new OrderReturnExample();
                            returnExample.setOrderSn(orderProduct.getOrderSn());
                            returnExample.setOrderProductId(orderProduct.getOrderProductId());
                            List<OrderReturn> returnList = orderReturnModel.getOrderReturnList(returnExample, null);
                            if (!CollectionUtils.isEmpty(returnList)) {
                                OrderReturn orderReturn = returnList.get(0);
                                orderProduct.setAfsSn(orderReturn.getAfsSn());
                                orderProduct.setAfsState(orderReturn.getState());
                                if (orderReturn.getState().equals(OrdersAfsConst.RETURN_STATE_300)) {
                                    //退款完成
                                    orderProduct.setAfsButton(OrderProductConst.AFS_BUTTON_402);
                                } else {
                                    //没有退款完成的，展示退款中
                                    orderProduct.setAfsButton(OrderProductConst.AFS_BUTTON_401);
                                }
                            }
                            OrderProductListVO orderProductListVO = new OrderProductListVO(orderProduct);
                            productListVOS.add(orderProductListVO);
                            //合计商品数
                            productNum += orderProductListVO.getProductNum();
                        }
                    }
                    vo.setGoodsNum(productNum);
                    vo.setOrderProductListVOList(productListVOS);
                    vos.add(vo);
                } else {
                    //获取订单货品列表信息
                    OrderProductExample orderProductExample = new OrderProductExample();
                    orderProductExample.setOrderSn(orderChild.getOrderSn());
                    List<OrderProduct> orderProductList = orderProductModel.getOrderProductList(orderProductExample, null);
                    orderChild.setOrderProductList(orderProductList);
                    showList.add(orderChild);
                }
            }
            if (!CollectionUtils.isEmpty(showList)) {
                //查询预售订单扩展信息
                if (showList.get(0).getOrderType() == PromotionConst.PROMOTION_TYPE_103) {
                    PresellOrderExtendExample presellOrderExtendExample = new PresellOrderExtendExample();
                    presellOrderExtendExample.setOrderSn(showList.get(0).getOrderSn());
                    List<PresellOrderExtend> presellOrderExtendList = presellOrderExtendModel.getPresellOrderExtendList(presellOrderExtendExample, null);
                    AssertUtil.notEmpty(presellOrderExtendList, "获取预售订单扩展信息为空");
                    PresellOrderExtend presellOrderExtend = presellOrderExtendList.get(0);
                    orderSubState = presellOrderExtend.getOrderSubState();
                    if (presellOrderExtend.getIsAllPay() == OrderConst.IS_ALL_PAY_0) {
                        depositRemainTime = presellOrderExtend.getDepositEndTime();
                        remainTime = presellOrderExtend.getRemainStartTime();
                        isHasDeposit = true;
                    }
                    deliverTime = presellOrderExtend.getDeliverTime();
                }
                //查询阶梯团订单扩展信息
                if (showList.get(0).getOrderType() == PromotionConst.PROMOTION_TYPE_105) {
                    LadderGroupOrderExtendExample groupOrderExtendExample = new LadderGroupOrderExtendExample();
                    groupOrderExtendExample.setOrderSn(showList.get(0).getOrderSn());
                    List<LadderGroupOrderExtend> groupOrderExtendList = ladderGroupOrderExtendModel.getLadderGroupOrderExtendList(groupOrderExtendExample, null);
                    AssertUtil.notEmpty(groupOrderExtendList, "获取阶梯团订单扩展信息为空");
                    LadderGroupOrderExtend groupOrderExtend = groupOrderExtendList.get(0);
                    orderSubState = groupOrderExtend.getOrderSubState();
                    remainTime = groupOrderExtend.getRemainStartTime();
                    isHasDeposit = true;
                    //查询是否退还定金
                    LadderGroup ladderGroup = ladderGroupModel.getLadderGroupByGroupId(groupOrderExtend.getGroupId());
                    AssertUtil.notNull(ladderGroup, "获取阶梯团活动信息为空，请重试！");
                    if (ladderGroup.getIsRefundDeposit() == LadderGroupConst.IS_REFUND_DEPOSIT_1) {
                        isRefundDeposit = true;
                    }
                }
                MemberOrderListVO vo = new MemberOrderListVO(showList.get(0), orderSubState, remainTime, isHasDeposit);
                vo.setIsRefundDeposit(isRefundDeposit);
                if (showList.get(0).getOrderType() == PromotionConst.PROMOTION_TYPE_103) {
                    if (depositRemainTime != null) {
                        long time1 = depositRemainTime.getTime();
                        long time2 = new Date().getTime();
                        long differTime = (time1 - time2) / 1000;
                        vo.setDepositRemainTime(differTime < 0 ? 0 : differTime);
                        long time3 = remainTime.getTime();
                        long remainEndTime = (time3 - time2) / 1000;
                        vo.setRemainEndTime(remainEndTime < 0 ? 0 : remainEndTime);
                    }
                }
                vo.setDeliverTime(TimeUtil.getZDDay(deliverTime));
                vo.setOrderSubState(orderSubState);
                List<OrderProductListVO> productListVOS = new ArrayList<>();

                if (showList.size() > 1) {
                    //获取商家名称
                    vo.setStoreId(0L);
                    vo.setStoreName(stringRedisTemplate.opsForValue().get("basic_site_name"));

                    ArrayList<OrderProduct> orderProducts = new ArrayList<>();
                    for (Order order : showList) {
                        orderProducts.addAll(order.getOrderProductList());
                        //获取合计金额
                        totalMoney = totalMoney.add(order.getOrderAmount());
                    }
                    //合计金额
                    vo.setTotalMoney(totalMoney);
                    //获取订单货品列表信息
                    for (OrderProduct orderProduct : orderProducts) {
                        OrderProductListVO orderProductListVO = new OrderProductListVO(orderProduct);
                        productListVOS.add(orderProductListVO);
                        //获取共计商品数
                        goodsNum += orderProduct.getProductNum();
                    }
                    //共计商品数
                    vo.setGoodsNum(goodsNum);
                    //订单货品列表信息
                    vo.setOrderProductListVOList(productListVOS);
                    vos.add(vo);
                } else {
                    Order order = showList.get(0);
                    vo.setStoreId(order.getStoreId());
                    vo.setStoreName(order.getStoreName());
                    Store store = storeModel.getStoreByStoreId(order.getStoreId());
                    //默认店铺logo
                    if (StringUtils.isEmpty(store.getStoreLogo())) {
                        store.setStoreLogo(stringRedisTemplate.opsForValue().get("default_image_store_logo"));
                    }
                    vo.setStoreLogo(FileUrlUtil.getFileUrl(store.getStoreLogo(),null));
                    //合计金额
                    vo.setTotalMoney(order.getOrderAmount());
                    //获取订单货品列表信息
                    List<OrderProduct> orderProductList = order.getOrderProductList();
                    for (OrderProduct orderProduct : orderProductList) {
                        OrderProductListVO orderProductListVO = new OrderProductListVO(orderProduct);
                        productListVOS.add(orderProductListVO);
                        //共计商品数
                        goodsNum += orderProduct.getProductNum();
                    }
                    vo.setGoodsNum(goodsNum);
                    vo.setOrderProductListVOList(productListVOS);
                    vos.add(vo);

                }
            }
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("获取订单详情接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单号", required = true)
    })
    @PostMapping("detail")
    public JsonResult<MemberOrderDetailVO> getOrderDetail(HttpServletRequest request, @RequestParam("orderSn") String orderSn) {
        Member member = UserUtil.getUser(request, Member.class);

        Order order = orderModel.getOrdersWithOpByOrderSn(orderSn);
        AssertUtil.notNull(order, "无此订单");
        AssertUtil.isTrue(!member.getMemberId().equals(order.getMemberId()), "您无权操作此订单");

        //获取订单扩展表信息
        OrderExtendExample orderExtendExample = new OrderExtendExample();
        orderExtendExample.setOrderSn(order.getOrderSn());
        OrderExtend orderExtend = orderExtendModel.getOrderExtendList(orderExtendExample, null).get(0);

        //获取订单支付信息
        OrderPayExample orderPayExample = new OrderPayExample();
        orderPayExample.setPaySn(order.getPaySn());
        OrderPay orderPay = orderPayModel.getOrderPayList(orderPayExample, null).get(0);

        MemberOrderDetailVO orderVO = new MemberOrderDetailVO(order, orderExtend, orderPay);
        orderVO.setOrderTypeValue(MemberOrderListVO.dealOrderTypeValue(order.getOrderType(), false));
        //获取会员邮箱信息
        orderVO.setMemberEmail(member.getMemberEmail());

        //获取订单日志信息
        OrderLogExample orderLogExample = new OrderLogExample();
        orderLogExample.setOrderSn(order.getOrderSn());
        orderLogExample.setOrderBy("log_time asc");
        List<OrderLog> orderLogList = orderLogModel.getOrderLogList(orderLogExample, null);
        orderVO.setOrderLogs(orderLogList);

        //总运费
        BigDecimal totalExpress = BigDecimal.ZERO;
        //实付款(含运费)
        BigDecimal actualPayment = BigDecimal.ZERO;

        ArrayList<ChildOrdersVO> childOrdersVOS = new ArrayList<>();
        //判断该订单的状态
        if (!order.getOrderState().equals(OrderConst.ORDER_STATE_10)) {
            //售后按钮展示
            dealAfsButton(order);

            //查询店铺logo
            Store store = storeModel.getStoreByStoreId(order.getStoreId());
            AssertUtil.notNull(store, "店铺不存在");
            //默认店铺logo
            if (StringUtil.isEmpty(store.getStoreLogo())) {
                store.setStoreLogo(stringRedisTemplate.opsForValue().get("default_image_store_logo"));
            }
            ChildOrdersVO childOrdersVO = new ChildOrdersVO(order, orderExtend, store.getStoreLogo());
            childOrdersVO.setServicePhone(store.getServicePhone());
            //获取货品信息
            List<OrderProductListVO> productListVOS = new ArrayList<>();
            if (!CollectionUtils.isEmpty(order.getOrderProductList())) {
                for (OrderProduct orderProduct : order.getOrderProductList()) {
                    OrderProductListVO orderProductListVO = new OrderProductListVO(orderProduct);
                    productListVOS.add(orderProductListVO);
                }
            }
            childOrdersVO.setOrderProductListVOList(productListVOS);

            //实付款(含运费)
            actualPayment = actualPayment.add(order.getOrderAmount());
            orderVO.setActualPayment(actualPayment);
            //总运费
            totalExpress = totalExpress.add(order.getExpressFee());
            orderVO.setTotalExpress(totalExpress);
            //商品总额
            orderVO.setTotalMoney(actualPayment.subtract(totalExpress));

            childOrdersVOS.add(childOrdersVO);
            orderVO.setChildOrdersVOS(childOrdersVOS);
        } else {
            //获得该订单号的父订单号下的所有子订单
            OrderExample orderExample = new OrderExample();
            orderExample.setParentSn(order.getParentSn());
            List<Order> orderList = orderModel.getOrderList(orderExample, null);
            //订单列表大于1说明是多个店铺
            if (orderList.size() > 1) {
                orderVO.setIsManyStore(true);
            }

            //遍历所有子订单
            for (Order orderChild : orderList) {
                //查询店铺logo
                Store store = storeModel.getStoreByStoreId(orderChild.getStoreId());
                AssertUtil.notNull(store, "店铺不存在");
                //默认店铺logo
                if (StringUtil.isEmpty(store.getStoreLogo())) {
                    store.setStoreLogo(stringRedisTemplate.opsForValue().get("default_image_store_logo"));
                }
                ChildOrdersVO childOrdersVO = new ChildOrdersVO(orderChild, orderExtend, store.getStoreLogo());
                childOrdersVO.setServicePhone(store.getServicePhone());

                //获取货品信息
                OrderProductExample orderProductExample = new OrderProductExample();
                orderProductExample.setOrderSn(orderChild.getOrderSn());
                List<OrderProduct> orderProductList = orderProductModel.getOrderProductList(orderProductExample, null);
                List<OrderProductListVO> productListVOS = new ArrayList<>();
                if (!CollectionUtils.isEmpty(orderProductList)) {
                    orderChild.setOrderProductList(orderProductList);

                    //售后按钮展示
                    dealAfsButton(orderChild);

                    for (OrderProduct orderProduct : orderProductList) {
                        OrderProductListVO orderProductListVO = new OrderProductListVO(orderProduct);
                        productListVOS.add(orderProductListVO);

                    }
                }
                childOrdersVO.setOrderProductListVOList(productListVOS);

                //实付款(含运费)
                actualPayment = actualPayment.add(orderChild.getOrderAmount());
                orderVO.setActualPayment(actualPayment);

                //总运费
                totalExpress = totalExpress.add(orderChild.getExpressFee());
                orderVO.setTotalExpress(totalExpress);

                //商品总额
                orderVO.setTotalMoney(actualPayment.subtract(totalExpress));

                childOrdersVOS.add(childOrdersVO);
            }
            orderVO.setChildOrdersVOS(childOrdersVOS);
        }
        if (order.getOrderState() == OrderConst.ORDER_STATE_30) {
            //待收货状态，计算自动收货时间
            String autoReceive = stringRedisTemplate.opsForValue().get("time_limit_of_auto_receive");
            int autoReceiveDay = StringUtil.isEmpty(autoReceive) ? 7 : Integer.parseInt(autoReceive);
            //计算时间
            Date autoReceiveTime = TimeUtil.getDayAgoDate(orderExtend.getDeliverTime(), autoReceiveDay);
            orderVO.setAutoReceiveTime(autoReceiveTime);
        }
        //拼团订单倒计时（秒）
        if (order.getOrderType() == PromotionConst.PROMOTION_TYPE_102 && order.getOrderState() == OrderConst.ORDER_STATE_10) {
            orderVO.setRemainTime(dealRemainTime("spell_order_auto_cancel_time", order.getCreateTime(), 30));
        }
        //秒杀订单倒计时（秒）
        if (order.getOrderType() == PromotionConst.PROMOTION_TYPE_104 && order.getOrderState() == OrderConst.ORDER_STATE_10) {
            orderVO.setRemainTime(dealRemainTime("seckill_order_cancle", order.getCreateTime(), 5));
        }
        //预售订单
        if (order.getOrderType() == PromotionConst.PROMOTION_TYPE_103) {
            PresellOrderExtendExample presellOrderExtendExample = new PresellOrderExtendExample();
            presellOrderExtendExample.setOrderSn(orderSn);
            List<PresellOrderExtend> presellOrderExtendList = presellOrderExtendModel.getPresellOrderExtendList(presellOrderExtendExample, null);
            AssertUtil.notEmpty(presellOrderExtendList, "获取预售订单扩展信息为空");
            PresellOrderExtend presellOrderExtend = presellOrderExtendList.get(0);
            orderVO.setPresellInfo(new MemberOrderDetailVO.PresellDetailInfo(presellOrderExtend));
            if (order.getOrderState() == OrderConst.ORDER_STATE_10) {
                orderVO.setOrderSubState(presellOrderExtend.getOrderSubState());
                if (presellOrderExtend.getIsAllPay() == OrderConst.IS_ALL_PAY_1) {
                    orderVO.setOrderStateValue(MemberOrderListVO.getRealOrderStateValue(order.getOrderState(),
                            0, 0L, order.getOrderType()));
                } else {
                    long time1 = presellOrderExtend.getRemainStartTime().getTime();
                    long time2 = new Date().getTime();
                    long depositRemainTime = (time1 - time2) / 1000;
                    depositRemainTime = depositRemainTime < 0 ? 0 : depositRemainTime;

                    orderVO.setOrderStateValue(MemberOrderListVO.getRealOrderStateValue(order.getOrderState(),
                            presellOrderExtend.getOrderSubState(), depositRemainTime, order.getOrderType()));
                    //预售订单倒计时（秒）
                    if (presellOrderExtend.getOrderSubState() == OrderConst.ORDER_SUB_STATE_101) {
                        //付定金倒计时
                        orderVO.setRemainTime(dealRemainTime("deposit_order_auto_cancel_time", order.getCreateTime(), 30));
                    } else if (presellOrderExtend.getOrderSubState() == OrderConst.ORDER_SUB_STATE_102) {
                        //付尾款倒计时
                        long time3 = presellOrderExtend.getRemainEndTime().getTime();
                        long endRemainTime = (time3 - time2) / 1000;
                        orderVO.setRemainTime(endRemainTime < 0 ? 0 : endRemainTime);
                    }
                    orderVO.setOrderTypeValue(MemberOrderListVO.dealOrderTypeValue(order.getOrderType(), true));
                }
            }
        }
        //阶梯团订单
        if (order.getOrderType() == PromotionConst.PROMOTION_TYPE_105) {
            LadderGroupOrderExtendExample groupOrderExtendExample = new LadderGroupOrderExtendExample();
            groupOrderExtendExample.setOrderSn(orderSn);
            List<LadderGroupOrderExtend> groupOrderExtendList = ladderGroupOrderExtendModel.getLadderGroupOrderExtendList(groupOrderExtendExample, null);
            AssertUtil.notEmpty(groupOrderExtendList, "获取阶梯团订单扩展信息为空");
            LadderGroupOrderExtend groupOrderExtend = groupOrderExtendList.get(0);
            MemberOrderDetailVO.LadderGroupDetailInfo groupDetailInfo = new MemberOrderDetailVO.LadderGroupDetailInfo(groupOrderExtend, order.getActivityDiscountAmount());
            //查询是否退还定金
            LadderGroup ladderGroup = ladderGroupModel.getLadderGroupByGroupId(groupOrderExtend.getGroupId());
            AssertUtil.notNull(ladderGroup, "获取阶梯团活动信息为空，请重试！");
            if (ladderGroup.getIsRefundDeposit() == LadderGroupConst.IS_REFUND_DEPOSIT_1) {
                groupDetailInfo.setIsRefundDeposit(true);
            }
            orderVO.setLadderGroupDetailInfo(groupDetailInfo);
            if (order.getOrderState() == OrderConst.ORDER_STATE_10) {
                long time1 = groupOrderExtend.getRemainStartTime().getTime();
                long time2 = new Date().getTime();
                long depositRemainTime = (time1 - time2) / 1000;
                depositRemainTime = depositRemainTime < 0 ? 0 : depositRemainTime;

                orderVO.setOrderSubState(groupOrderExtend.getOrderSubState());
                orderVO.setOrderStateValue(MemberOrderListVO.getRealOrderStateValue(order.getOrderState(),
                        groupOrderExtend.getOrderSubState(), depositRemainTime, order.getOrderType()));
                //阶梯团订单倒计时（秒）
                if (groupOrderExtend.getOrderSubState() == LadderGroupConst.ORDER_SUB_STATE_1) {
                    //付定金倒计时
                    orderVO.setRemainTime(dealRemainTime("ladder_group_deposit_order_auto_cancel_time", order.getCreateTime(), 30));
                } else if (groupOrderExtend.getOrderSubState() == LadderGroupConst.ORDER_SUB_STATE_2) {
                    if (depositRemainTime > 0) {
                        //生成尾款倒计时
                        orderVO.setRemainTime(depositRemainTime);
                    } else {
                        //付尾款倒计时
                        long time3 = groupOrderExtend.getRemainEndTime().getTime();
                        long endRemainTime = (time3 - time2) / 1000;
                        orderVO.setRemainTime(endRemainTime < 0 ? 0 : endRemainTime);
                    }
                }
            }
        }
        return SldResponse.success(orderVO);
    }

    /**
     * 处理订单支付剩余时间
     *
     * @param key          redis存储的key
     * @param createTime   订单创建时间
     * @param defaultValue 默认值
     * @return
     */
    private long dealRemainTime(String key, Date createTime, Integer defaultValue) {
        //买家几分钟未支付订单，订单取消
        String value = stringRedisTemplate.opsForValue().get(key);
        int limitMinute = value == null ? defaultValue : Integer.parseInt(value);
        // 获取当前时间limitHour小时之前的时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -limitMinute);
        Date cancelTime = calendar.getTime();

        long time1 = createTime.getTime();
        long time2 = cancelTime.getTime();
        long remainTime = (time1 - time2) / 1000;
        return remainTime < 0 ? 0 : remainTime;
    }

    /**
     * 处理订单货品售后展示按钮
     *
     * @param order
     */
    private void dealAfsButton(Order order) {
        Integer orderState = order.getOrderState();
        if (orderState.equals(OrderConst.ORDER_STATE_20)
                || orderState.equals(OrderConst.ORDER_STATE_30)
                || orderState.equals(OrderConst.ORDER_STATE_40)) {
            //可以售后的状态，遍历订单货品，设置售后按钮
            for (OrderProduct orderProduct : order.getOrderProductList()) {
                if (orderProduct.getIsGift() == OrderConst.IS_GIFT_YES) {
                    continue;
                }
                switch (orderState) {
                    case OrderConst.ORDER_STATE_20://待发货，只能申请仅退款
                        dealReturnInfo(orderProduct, OrderProductConst.AFS_BUTTON_100);
                        break;
                    case OrderConst.ORDER_STATE_30://待买家收货，显示退款
                        dealReturnInfo(orderProduct, OrderProductConst.AFS_BUTTON_200);
                        break;
                    case OrderConst.ORDER_STATE_40:
                        dealReturnInfo(orderProduct, OrderProductConst.AFS_BUTTON_300);
                        break;
                }
            }
        }
    }

    /**
     * 处理退货信息
     *
     * @param orderProduct
     * @param button       没有申请过退换货时展示的状态
     */
    private void dealReturnInfo(OrderProduct orderProduct, Integer button) {
        //有退货,查询退货是否已完成
        OrderReturnExample returnExample = new OrderReturnExample();
        returnExample.setOrderSn(orderProduct.getOrderSn());
        returnExample.setOrderProductId(orderProduct.getOrderProductId());
        List<OrderReturn> returnList = orderReturnModel.getOrderReturnList(returnExample, null);
        if (CollectionUtils.isEmpty(returnList)) {
            orderProduct.setAfsButton(button);
            return;
        }
        OrderReturn orderReturn = returnList.get(0);
        orderProduct.setAfsSn(orderReturn.getAfsSn());
        orderProduct.setAfsState(orderReturn.getState());
        if (orderReturn.getState().equals(OrdersAfsConst.RETURN_STATE_300)) {
            //退款完成
            orderProduct.setAfsButton(OrderProductConst.AFS_BUTTON_402);
        } else if (orderReturn.getState().equals(OrdersAfsConst.RETURN_STATE_202)) {
            //退款失败
            orderProduct.setAfsButton(OrderProductConst.AFS_BUTTON_301);
        } else {
            //没有退款完成的，展示退款中
            orderProduct.setAfsButton(OrderProductConst.AFS_BUTTON_401);
        }
    }

    @ApiOperation("获取聊天界面我的订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeId", value = "店铺id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("myOrders")
    public JsonResult<PageVO<ChatOrdersVO>> myOrders(HttpServletRequest request, Long storeId) {
        Member member = UserUtil.getUser(request, Member.class);

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        OrderExample example = new OrderExample();
        example.setStoreId(storeId);
        example.setMemberId(member.getMemberId());
        List<Order> list = orderModel.getOrderList(example, pager);
        ArrayList<ChatOrdersVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(order -> {
                //查询货品列表
                OrderProductExample productExample = new OrderProductExample();
                productExample.setOrderSn(order.getOrderSn());
                List<OrderProduct> productList = orderProductModel.getOrderProductList(productExample, null);
                vos.add(new ChatOrdersVO(order, productList));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }
}
