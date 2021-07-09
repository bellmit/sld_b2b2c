package com.slodon.b2b2c.controller.business.seller;

import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.business.example.*;
import com.slodon.b2b2c.business.pojo.*;
import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.constant.*;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.*;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.model.business.*;
import com.slodon.b2b2c.model.member.MemberModel;
import com.slodon.b2b2c.model.promotion.LadderGroupOrderExtendModel;
import com.slodon.b2b2c.model.promotion.PresellOrderExtendModel;
import com.slodon.b2b2c.model.promotion.SpellTeamMemberModel;
import com.slodon.b2b2c.model.promotion.SpellTeamModel;
import com.slodon.b2b2c.model.system.ExpressModel;
import com.slodon.b2b2c.model.system.ReasonModel;
import com.slodon.b2b2c.promotion.example.LadderGroupOrderExtendExample;
import com.slodon.b2b2c.promotion.example.PresellOrderExtendExample;
import com.slodon.b2b2c.promotion.example.SpellTeamMemberExample;
import com.slodon.b2b2c.promotion.pojo.LadderGroupOrderExtend;
import com.slodon.b2b2c.promotion.pojo.PresellOrderExtend;
import com.slodon.b2b2c.promotion.pojo.SpellTeam;
import com.slodon.b2b2c.promotion.pojo.SpellTeamMember;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.starter.mq.entity.MessageSendProperty;
import com.slodon.b2b2c.starter.mq.entity.MessageSendVO;
import com.slodon.b2b2c.system.pojo.Express;
import com.slodon.b2b2c.system.pojo.Reason;
import com.slodon.b2b2c.util.OrderExportUtil;
import com.slodon.b2b2c.vo.business.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.slodon.b2b2c.core.constant.StarterConfigConst.MQ_EXCHANGE_NAME;
import static com.slodon.b2b2c.core.constant.StarterConfigConst.MQ_QUEUE_NAME_MEMBER_MSG;

@Api(tags = "seller-订单管理")
@RestController
@RequestMapping("v3/business/seller/orderInfo")
public class SellerOrderInfoController {

    @Resource
    private OrderModel orderModel;
    @Resource
    private OrderExtendModel orderExtendModel;
    @Resource
    private OrderProductModel orderProductModel;
    @Resource
    private OrderLogModel orderLogModel;
    @Resource
    private MemberModel memberModel;
    @Resource
    private ExpressModel expressModel;
    @Resource
    private ReasonModel reasonModel;
    @Resource
    private OrderReturnModel orderReturnModel;
    @Resource
    private PresellOrderExtendModel presellOrderExtendModel;
    @Resource
    private LadderGroupOrderExtendModel ladderGroupOrderExtendModel;
    @Resource
    private SpellTeamModel spellTeamModel;
    @Resource
    private SpellTeamMemberModel spellTeamMemberModel;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("订单列表相关接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单号", paramType = "query"),
            @ApiImplicitParam(name = "memberName", value = "会员名称", paramType = "query"),
            @ApiImplicitParam(name = "goodsName", value = "商品名称", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "下单开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "下单结束时间", paramType = "query"),
            @ApiImplicitParam(name = "orderState", value = "订单状态：0-已取消；10-待付款订单；20-代发货订单；30-待收货订单；40-已完成;50-已关闭", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<OrderListVO>> getList(HttpServletRequest request, String orderSn, String memberName, String goodsName,
                                                   Date startTime, Date endTime, Integer orderState) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        OrderExample orderExample = new OrderExample();
        orderExample.setOrderSnLikes(orderSn);
        orderExample.setMemberNameLike(memberName);
        orderExample.setGoodsNameLike(goodsName);
        orderExample.setCreateTimeAfter(startTime);
        orderExample.setCreateTimeBefore(endTime);
        orderExample.setOrderState(orderState);
        orderExample.setStoreId(vendor.getStoreId());

        List<Order> orderList = orderModel.getOrderList(orderExample, pager);
        List<OrderListVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(orderList)) {
            orderList.forEach(order -> {
                //是否有定金
                boolean isHasDeposit = false;
                OrderListVO vo = new OrderListVO(order);
                //获取订单货品列表信息
                OrderProductExample orderProductExample = new OrderProductExample();
                orderProductExample.setOrderSn(order.getOrderSn());
                orderProductExample.setGoodsNameLike(goodsName);
                List<OrderProduct> orderProductList = orderProductModel.getOrderProductList(orderProductExample, null);
                List<OrderProductListVO> productListVOS = new ArrayList<>();
                if (!CollectionUtils.isEmpty(orderProductList)) {
                    for (OrderProduct orderProduct : orderProductList) {
                        OrderProductListVO orderProductListVO = new OrderProductListVO(orderProduct);
                        productListVOS.add(orderProductListVO);
                    }
                }
                vo.setOrderProductListVOList(productListVOS);
                //拼团订单
                if (order.getOrderType() == PromotionConst.PROMOTION_TYPE_102) {
                    SpellTeamMemberExample teamMemberExample = new SpellTeamMemberExample();
                    teamMemberExample.setOrderSn(order.getOrderSn());
                    List<SpellTeamMember> teamMemberList = spellTeamMemberModel.getSpellTeamMemberList(teamMemberExample, null);
                    AssertUtil.notEmpty(teamMemberList, "获取拼团成员信息为空");
                    //查询拼团团队状态
                    SpellTeam spellTeam = spellTeamModel.getSpellTeamBySpellTeamId(teamMemberList.get(0).getSpellTeamId());
                    AssertUtil.notNull(spellTeam, "获取拼团团队信息为空，请重试");
                    //拼团没有成功就不显示发货按钮
                    if (spellTeam.getState() != SpellConst.SPELL_GROUP_STATE_2) {
                        vo.setIsShowDeliverButton(false);
                    }
                }
                //查询预售订单扩展信息
                if (order.getOrderType() == PromotionConst.PROMOTION_TYPE_103) {
                    PresellOrderExtendExample presellOrderExtendExample = new PresellOrderExtendExample();
                    presellOrderExtendExample.setOrderSn(order.getOrderSn());
                    List<PresellOrderExtend> presellOrderExtendList = presellOrderExtendModel.getPresellOrderExtendList(presellOrderExtendExample, null);
                    AssertUtil.notEmpty(presellOrderExtendList, "获取预售订单扩展信息为空");
                    PresellOrderExtend presellOrderExtend = presellOrderExtendList.get(0);
                    if (presellOrderExtend.getIsAllPay() == OrderConst.IS_ALL_PAY_0) {
                        isHasDeposit = true;
                    }
                    vo.setOrderSubState(presellOrderExtend.getOrderSubState());
                    //未付定金处理
                    if (presellOrderExtend.getOrderSubState() == OrderConst.ORDER_SUB_STATE_101) {
                        //商品实际总额
                        BigDecimal goodsAmount = (presellOrderExtend.getDepositAmount().add(presellOrderExtend.getRemainAmount())).multiply(new BigDecimal(presellOrderExtend.getProductNum()));
                        vo.setOrderAmount(goodsAmount.add(order.getExpressFee()));
                        //订单货品价格处理
                        for (OrderProductListVO orderProductListVO : vo.getOrderProductListVOList()) {
                            orderProductListVO.setProductShowPrice(presellOrderExtend.getPresellPrice());
                        }
                    }
                }
                //阶梯团订单
                if (order.getOrderType() == PromotionConst.PROMOTION_TYPE_105) {
                    LadderGroupOrderExtendExample groupOrderExtendExample = new LadderGroupOrderExtendExample();
                    groupOrderExtendExample.setOrderSn(order.getOrderSn());
                    List<LadderGroupOrderExtend> groupOrderExtendList = ladderGroupOrderExtendModel.getLadderGroupOrderExtendList(groupOrderExtendExample, null);
                    AssertUtil.notEmpty(groupOrderExtendList, "获取阶梯团订单扩展信息为空");
                    LadderGroupOrderExtend groupOrderExtend = groupOrderExtendList.get(0);
                    vo.setOrderSubState(groupOrderExtend.getOrderSubState());
                    //未付定金处理
                    if (groupOrderExtend.getOrderSubState() != OrderConst.ORDER_SUB_STATE_103) {
                        //商品实际总额
                        BigDecimal goodsAmount;
                        if (!StringUtil.isNullOrZero(groupOrderExtend.getRemainAmount())) {
                            goodsAmount = (groupOrderExtend.getAdvanceDeposit().add(groupOrderExtend.getRemainAmount())).multiply(new BigDecimal(groupOrderExtend.getProductNum()));
                        } else {
                            goodsAmount = groupOrderExtend.getAdvanceDeposit().multiply(new BigDecimal(groupOrderExtend.getProductNum()));
                        }
                        vo.setOrderAmount(goodsAmount.add(order.getExpressFee()));
                        //订单货品价格处理
                        for (OrderProductListVO orderProductListVO : vo.getOrderProductListVOList()) {
                            orderProductListVO.setProductShowPrice(groupOrderExtend.getProductPrice());
                        }
                    }
                }
                vo.setOrderTypeValue(MemberOrderListVO.dealOrderTypeValue(order.getOrderType(), isHasDeposit));
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("获取订单详情接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单号", required = true)
    })
    @GetMapping("detail")
    public JsonResult<SellerOrderVO> getOrderDetail(HttpServletRequest request, @RequestParam("orderSn") String orderSn) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        Order order = orderModel.getOrdersWithOpByOrderSn(orderSn);
        AssertUtil.notNull(order, "无此订单");
        AssertUtil.isTrue(!vendor.getStoreId().equals(order.getStoreId()), "您无权操作此订单");

        //获取订单扩展表信息
        OrderExtendExample orderExtendExample = new OrderExtendExample();
        orderExtendExample.setStoreId(vendor.getStoreId());
        orderExtendExample.setOrderSn(orderSn);
        OrderExtend orderExtend = orderExtendModel.getOrderExtendList(orderExtendExample, null).get(0);

        SellerOrderVO sellerOrderVO = new SellerOrderVO(order, orderExtend);

        //获取会员信息
        Member member = memberModel.getMemberByMemberId(order.getMemberId());
        sellerOrderVO.setMemberEmail(member.getMemberEmail());

        //获取订单日志信息
        OrderLogExample orderLogExample = new OrderLogExample();
        orderLogExample.setOrderSn(orderSn);
        orderLogExample.setOrderBy("log_time asc");
        List<OrderLog> orderLogList = orderLogModel.getOrderLogList(orderLogExample, null);
        sellerOrderVO.setOrderLogs(orderLogList);

        //售后按钮展示
        dealAfsButton(order);

        //获取货品列表信息
        for (OrderProduct orderProduct : order.getOrderProductList()) {
            OrderProductListVO orderProductListVO = new OrderProductListVO(orderProduct);
            sellerOrderVO.getOrderProductList().add(orderProductListVO);
        }
        //拼团订单
        if (order.getOrderType() == PromotionConst.PROMOTION_TYPE_102) {
            SpellTeamMemberExample teamMemberExample = new SpellTeamMemberExample();
            teamMemberExample.setOrderSn(order.getOrderSn());
            List<SpellTeamMember> teamMemberList = spellTeamMemberModel.getSpellTeamMemberList(teamMemberExample, null);
            AssertUtil.notEmpty(teamMemberList, "获取拼团成员信息为空");
            //查询拼团团队状态
            SpellTeam spellTeam = spellTeamModel.getSpellTeamBySpellTeamId(teamMemberList.get(0).getSpellTeamId());
            AssertUtil.notNull(spellTeam, "获取拼团团队信息为空，请重试");
            //拼团没有成功就不显示发货按钮
            if (spellTeam.getState() != SpellConst.SPELL_GROUP_STATE_2) {
                sellerOrderVO.setIsShowDeliverButton(false);
            }
        }
        //预售订单
        if (order.getOrderType() == PromotionConst.PROMOTION_TYPE_103) {
            PresellOrderExtendExample presellOrderExtendExample = new PresellOrderExtendExample();
            presellOrderExtendExample.setOrderSn(orderSn);
            List<PresellOrderExtend> presellOrderExtendList = presellOrderExtendModel.getPresellOrderExtendList(presellOrderExtendExample, null);
            AssertUtil.notEmpty(presellOrderExtendList, "获取预售订单扩展信息为空");
            PresellOrderExtend presellOrderExtend = presellOrderExtendList.get(0);
            sellerOrderVO.setPresellInfo(new MemberOrderDetailVO.PresellDetailInfo(presellOrderExtend));
            if (presellOrderExtend.getIsAllPay() == OrderConst.IS_ALL_PAY_0) {
                sellerOrderVO.setOrderTypeValue(MemberOrderListVO.dealOrderTypeValue(order.getOrderType(), true));
                //未付定金处理
                if (presellOrderExtend.getOrderSubState() == OrderConst.ORDER_SUB_STATE_101) {
                    //商品实际总额
                    BigDecimal goodsAmount = (presellOrderExtend.getDepositAmount().add(presellOrderExtend.getRemainAmount())).multiply(new BigDecimal(presellOrderExtend.getProductNum()));
                    sellerOrderVO.setGoodsAmount(goodsAmount);
                    sellerOrderVO.setOrderAmount(goodsAmount.add(order.getExpressFee()));
                    //订单货品价格处理
                    for (OrderProductListVO orderProductListVO : sellerOrderVO.getOrderProductList()) {
                        orderProductListVO.setProductShowPrice(presellOrderExtend.getPresellPrice());
                    }
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
            sellerOrderVO.setLadderGroupDetailInfo(new MemberOrderDetailVO.LadderGroupDetailInfo(groupOrderExtend, order.getActivityDiscountAmount()));
            //未付定金处理
            if (groupOrderExtend.getOrderSubState() != OrderConst.ORDER_SUB_STATE_103) {
                //商品实际总额
                BigDecimal goodsAmount;
                if (!StringUtil.isNullOrZero(groupOrderExtend.getRemainAmount())) {
                    goodsAmount = (groupOrderExtend.getAdvanceDeposit().add(groupOrderExtend.getRemainAmount())).multiply(new BigDecimal(groupOrderExtend.getProductNum()));
                } else {
                    goodsAmount = groupOrderExtend.getAdvanceDeposit().multiply(new BigDecimal(groupOrderExtend.getProductNum()));
                }
                sellerOrderVO.setGoodsAmount(goodsAmount);
                sellerOrderVO.setOrderAmount(goodsAmount.add(order.getExpressFee()));
                //订单货品价格处理
                for (OrderProductListVO orderProductListVO : sellerOrderVO.getOrderProductList()) {
                    orderProductListVO.setProductShowPrice(groupOrderExtend.getProductPrice());
                }
            }
        }
        return SldResponse.success(sellerOrderVO);
    }

    @ApiOperation("发货")
    @VendorLogger(option = "发货")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "deliverType", value = "发货类型：0-物流发货，1-无需物流", required = true, paramType = "query"),
            @ApiImplicitParam(name = "expressId", value = "快递公司id(物流发货时必填)", paramType = "query"),
            @ApiImplicitParam(name = "expressNumber", value = "快递单号(物流发货时必填)", paramType = "query"),
            @ApiImplicitParam(name = "deliverName", value = "联系人(无需物流时必填)", paramType = "query"),
            @ApiImplicitParam(name = "deliverMobile", value = "联系电话(无需物流时必填)", paramType = "query")
    })
    @PostMapping("deliver")
    public JsonResult deliver(HttpServletRequest request, String orderSn, Integer deliverType, Integer expressId,
                              String expressNumber, String deliverName, String deliverMobile) {
        Order order = new Order();
        String expressName, expressNo;

        if (deliverType == OrderConst.DELIVER_TYPE_0) {
            AssertUtil.notNullOrZero(expressId, "快递公司id不能为空");
            AssertUtil.notEmpty(expressNumber, "快递单号不能为空");

            Express express = expressModel.getExpressByExpressId(expressId);
            AssertUtil.notNull(express, "获取快递公司信息为空，请重试");
            order.setExpressId(expressId);
            order.setExpressName(express.getExpressName());
            order.setExpressNumber(expressNumber);
            expressName = express.getExpressName();
            expressNo = expressNumber;
        } else {
            AssertUtil.notEmpty(deliverName, "联系人不能为空");
            AssertUtil.notEmpty(deliverMobile, "联系电话不能为空");
            expressName = "商家自行配送";
            expressNo = "-";
        }

        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        Order orderDb = orderModel.getOrderByOrderSn(orderSn);
        AssertUtil.notNull(orderDb, "订单不存在，请重试");
        AssertUtil.isTrue(!orderDb.getStoreId().equals(vendor.getStoreId()), "您无权处理此订单");
        AssertUtil.isTrue(orderDb.getOrderState() != OrderConst.ORDER_STATE_20, "非待发货订单，无法发货");
        AssertUtil.isTrue(orderDb.getLockState() > 0, "该订单已被锁定，无法进行发货");

        order.setOrderId(orderDb.getOrderId());
        order.setOrderSn(orderSn);
        order.setOrderState(OrderConst.ORDER_STATE_30);
        orderModel.deliverGoods(order, vendor, deliverType, deliverName, deliverMobile);

        //会员消息通知
        List<MessageSendProperty> messageSendPropertyList = new ArrayList<>();
        messageSendPropertyList.add(new MessageSendProperty("orderSn", orderDb.getOrderSn()));
        //微信通知
        List<MessageSendProperty> messageSendPropertyList4CashWx = new ArrayList<>();
        messageSendPropertyList4CashWx.add(new MessageSendProperty("first", "您的订单已发货"));
        messageSendPropertyList4CashWx.add(new MessageSendProperty("keyword1", orderSn));
        messageSendPropertyList4CashWx.add(new MessageSendProperty("keyword2", expressName));
        messageSendPropertyList4CashWx.add(new MessageSendProperty("keyword3", expressNo));
        messageSendPropertyList4CashWx.add(new MessageSendProperty("remark", "点击查看订单详情。"));
        messageSendPropertyList4CashWx.add(new MessageSendProperty("url", DomainUrlUtil.SLD_H5_URL + "/#/pages/order/detail?orderSn=" + orderSn));
        String msgLinkInfo = "{\"type\":\"order_news\",\"orderSn\":\"" + orderDb.getOrderSn() + "\"}";
        MessageSendVO messageSendVO = new MessageSendVO(messageSendPropertyList, messageSendPropertyList4CashWx,
                "deliveryTime", orderDb.getMemberId(), MemberTplConst.GOODS_DELIVERY_REMINDER, msgLinkInfo);
        //发送到mq
        rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_MEMBER_MSG, messageSendVO);

        return SldResponse.success("订单发货成功");
    }

    @ApiOperation("取消订单")
    @VendorLogger(option = "取消订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "reasonId", value = "取消原因id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "remark", value = "取消备注", paramType = "query")
    })
    @PostMapping("cancel")
    public JsonResult cancelOrder(HttpServletRequest request, String orderSn, Integer reasonId, String remark) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //订单信息
        Order orderDb = orderModel.getOrderByOrderSn(orderSn);
        AssertUtil.notNull(orderDb, "订单信息为空");
        AssertUtil.isTrue(!orderDb.getStoreId().equals(vendor.getStoreId()), "非法操作");

        //查询取消原因
        Reason reason = reasonModel.getReasonByReasonId(reasonId);
        AssertUtil.notNull(reason, "取消原因不存在");

        //构造订单列表
        List<Order> orderList = new ArrayList<>();
        orderList.add(orderDb);

        //订单取消
        orderModel.cancelOrder(orderList, reason.getContent(), remark, OrderConst.LOG_ROLE_VENDOR, vendor.getVendorId(), vendor.getVendorName(), "商户取消订单");

        return SldResponse.success("订单取消成功");
    }

    @ApiOperation("订单导出")
    @VendorLogger(option = "订单导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单号", paramType = "query"),
            @ApiImplicitParam(name = "memberName", value = "会员名称", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "下单开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "下单结束时间", paramType = "query"),
            @ApiImplicitParam(name = "orderState", value = "订单状态：0-已取消；10-未付款订单；20-已付款；30-已发货；40-已完成;50-已关闭", paramType = "query")
    })
    @GetMapping("export")
    public JsonResult export(HttpServletRequest request, HttpServletResponse response, String orderSn,
                             String memberName, Date startTime, Date endTime, Integer orderState) throws InterruptedException, IOException {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        OrderExample example = new OrderExample();
        example.setStoreId(vendor.getStoreId());
        example.setOrderSnLike(orderSn);
        example.setMemberNameLike(memberName);
        example.setCreateTimeAfter(startTime);
        example.setCreateTimeBefore(endTime);
        example.setOrderState(orderState);
        String orderCode = stringRedisTemplate.opsForValue().get("order_list_code");
        OrderExportUtil util = new OrderExportUtil(orderModel, example, orderCode);
        Map<String, SXSSFWorkbook> excelMap = util.doExport();
        if (excelMap.size() == 1) {
            //导出一个excel文件，无需压缩
            FileDownloadUtils.setExcelHeadInfo(response, request, "导出订单-" + ExcelExportUtil.getSystemDate() + ".xls");
            excelMap.entrySet().iterator().next().getValue().write(response.getOutputStream());
        } else {
            //压缩
            FileDownloadUtils.setZipDownLoadHeadInfo(response, request, "导出订单-" + ExcelExportUtil.getSystemDate() + ".zip");
            ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());

            for (Map.Entry<String, SXSSFWorkbook> entry : excelMap.entrySet()) {
                String fileName = entry.getKey();
                SXSSFWorkbook sxssfWorkbook = entry.getValue();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                sxssfWorkbook.write(os);
                zipOutputStream.putNextEntry(new ZipEntry(fileName));
                zipOutputStream.write(os.toByteArray());
                zipOutputStream.closeEntry();
                os.flush();
                os.close();
            }
            zipOutputStream.flush();
            zipOutputStream.close();
        }

        return null;
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
            order.getOrderProductList().forEach(orderProduct -> {
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
            });
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

    @ApiOperation("获取聊天用户订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberId", value = "会员id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("userOrders")
    public JsonResult<PageVO<ChatOrdersVO>> userOrders(HttpServletRequest request, Integer memberId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        OrderExample example = new OrderExample();
        example.setStoreId(vendor.getStoreId());
        example.setMemberId(memberId);
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
