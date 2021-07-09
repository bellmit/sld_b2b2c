package com.slodon.b2b2c.controller.integral.seller;

import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.express.TracesResult;
import com.slodon.b2b2c.core.express.TrackUtil;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.integral.example.IntegralOrderExample;
import com.slodon.b2b2c.integral.example.IntegralOrderLogExample;
import com.slodon.b2b2c.integral.example.IntegralOrderPayExample;
import com.slodon.b2b2c.integral.example.IntegralOrderProductExample;
import com.slodon.b2b2c.integral.pojo.IntegralOrder;
import com.slodon.b2b2c.integral.pojo.IntegralOrderLog;
import com.slodon.b2b2c.integral.pojo.IntegralOrderPay;
import com.slodon.b2b2c.integral.pojo.IntegralOrderProduct;
import com.slodon.b2b2c.model.integral.IntegralOrderLogModel;
import com.slodon.b2b2c.model.integral.IntegralOrderModel;
import com.slodon.b2b2c.model.integral.IntegralOrderPayModel;
import com.slodon.b2b2c.model.integral.IntegralOrderProductModel;
import com.slodon.b2b2c.model.system.ExpressModel;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.system.pojo.Express;
import com.slodon.b2b2c.vo.integral.IntegralOrderDetailVO;
import com.slodon.b2b2c.vo.integral.IntegralOrderProductVO;
import com.slodon.b2b2c.vo.integral.IntegralOrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Api(tags = "seller-订单管理")
@RestController
@RequestMapping("v3/integral/seller/integral/order")
public class SellerIntegralOrderController extends BaseController {

    @Resource
    private IntegralOrderModel integralOrderModel;
    @Resource
    private IntegralOrderProductModel integralOrderProductModel;
    @Resource
    private IntegralOrderLogModel integralOrderLogModel;
    @Resource
    private IntegralOrderPayModel integralOrderPayModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private ExpressModel expressModel;

    @ApiOperation("订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单号", paramType = "query"),
            @ApiImplicitParam(name = "memberName", value = "会员名称", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "下单开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "下单结束时间", paramType = "query"),
            @ApiImplicitParam(name = "orderState", value = "订单状态：0-已取消；10-待付款订单；20-代发货订单；30-待收货订单；40-已完成;50-已关闭", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<IntegralOrderVO>> list(HttpServletRequest request, String orderSn, String memberName,
                                                    Date startTime, Date endTime, Integer orderState) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        IntegralOrderExample example = new IntegralOrderExample();
        example.setStoreId(vendor.getStoreId());
        example.setOrderSnLike(orderSn);
        example.setMemberNameLike(memberName);
        example.setCreateTimeAfter(startTime);
        example.setCreateTimeBefore(endTime);
        example.setOrderState(orderState);
        example.setDeleteState(OrderConst.DELETE_STATE_0);
        List<IntegralOrder> list = integralOrderModel.getIntegralOrderList(example, pager);
        List<IntegralOrderVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            for (IntegralOrder integralOrder : list) {
                IntegralOrderVO vo = new IntegralOrderVO(integralOrder);
                //获取订单货品列表信息
                IntegralOrderProductExample orderProductExample = new IntegralOrderProductExample();
                orderProductExample.setOrderSn(integralOrder.getOrderSn());
                List<IntegralOrderProduct> orderProductList = integralOrderProductModel.getIntegralOrderProductList(orderProductExample, null);
                AssertUtil.notEmpty(orderProductList, "订单货品不存在");
                List<IntegralOrderProductVO> productListVOS = new ArrayList<>();
                orderProductList.forEach(orderProduct -> {
                    productListVOS.add(new IntegralOrderProductVO(orderProduct));
                });
                vo.setOrderProductList(productListVOS);
                vos.add(vo);
            }
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("订单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单号", required = true)
    })
    @PostMapping("detail")
    public JsonResult<IntegralOrderDetailVO> detail(HttpServletRequest request, String orderSn) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        IntegralOrder order = integralOrderModel.getOrdersWithOpByOrderSn(orderSn);
        AssertUtil.notNull(order, "无此订单");
        AssertUtil.isTrue(!vendor.getStoreId().equals(order.getStoreId()), "您无权操作此订单");

        //获取订单支付信息
        IntegralOrderPayExample orderPayExample = new IntegralOrderPayExample();
        orderPayExample.setPaySn(order.getPaySn());
        IntegralOrderPay orderPay = integralOrderPayModel.getIntegralOrderPayList(orderPayExample, null).get(0);

        IntegralOrderDetailVO detailVO = new IntegralOrderDetailVO(order, orderPay);
        List<IntegralOrderProductVO> productVOS = new ArrayList<>();
        //获取货品信息
        order.getOrderProductList().forEach(orderProduct -> {
            productVOS.add(new IntegralOrderProductVO(orderProduct));
        });
        detailVO.setOrderProductList(productVOS);
        //获取订单日志信息
        IntegralOrderLogExample orderLogExample = new IntegralOrderLogExample();
        orderLogExample.setOrderSn(order.getOrderSn());
        orderLogExample.setOrderBy("log_time asc");
        List<IntegralOrderLog> orderLogList = integralOrderLogModel.getIntegralOrderLogList(orderLogExample, null);
        detailVO.setOrderLogList(orderLogList);
        return SldResponse.success(detailVO);
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
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        IntegralOrder order = new IntegralOrder();
        order.setDeliverType(deliverType);
        if (deliverType == OrderConst.DELIVER_TYPE_0) {
            AssertUtil.notNullOrZero(expressId, "快递公司id不能为空");
            AssertUtil.notEmpty(expressNumber, "快递单号不能为空");

            Express express = expressModel.getExpressByExpressId(expressId);
            AssertUtil.notNull(express, "获取快递公司信息为空，请重试");
            order.setExpressId(expressId);
            order.setExpressName(express.getExpressName());
            order.setExpressNumber(expressNumber);
        } else {
            AssertUtil.notEmpty(deliverName, "联系人不能为空");
            AssertUtil.notEmpty(deliverMobile, "联系电话不能为空");
            order.setDeliverName(deliverName);
            order.setDeliverMobile(deliverMobile);
        }
        order.setDeliverTime(new Date());
        //查询订单信息
        IntegralOrder orderDb = integralOrderModel.getIntegralOrderByOrderSn(orderSn);
        AssertUtil.isTrue(!orderDb.getStoreId().equals(vendor.getStoreId()), "您无权处理此订单");
        AssertUtil.isTrue(orderDb.getOrderState() != OrderConst.ORDER_STATE_20, "非待发货订单，无法发货");

        order.setOrderId(orderDb.getOrderId());
        order.setOrderSn(orderSn);
        order.setOrderState(OrderConst.ORDER_STATE_30);
        integralOrderModel.updateIntegralOrder(order);

        //保存订单日志
        integralOrderLogModel.insertOrderLog(OrderConst.LOG_ROLE_VENDOR, vendor.getVendorId(), vendor.getVendorName(),
                orderSn, OrderConst.ORDER_STATE_30, "商品发货");

        return SldResponse.success("订单发货成功");
    }

    @ApiOperation("查看物流")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单号", required = true, paramType = "query")
    })
    @GetMapping("getTrace")
    public JsonResult<TracesResult> getTrace(HttpServletRequest request, String orderSn) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        //查询订单信息
        IntegralOrder order = integralOrderModel.getIntegralOrderByOrderSn(orderSn);
        AssertUtil.notNull(order, "订单信息为空");
        AssertUtil.isTrue(!vendor.getStoreId().equals(order.getStoreId()), "您无权查看此订单物流");

        if (order.getOrderState() > OrderConst.ORDER_STATE_20 && StringUtil.isNullOrZero(order.getExpressId())) {
            TracesResult tracesResult = new TracesResult();
            tracesResult.setExpressName(order.getDeliverName());
            tracesResult.setExpressNumber(order.getDeliverMobile());
            tracesResult.setType("1");
            return SldResponse.success(tracesResult);
        } else {
            AssertUtil.isTrue(StringUtil.isNullOrZero(order.getExpressId()), "快递公司id信息为空");
            //获取快递公司编码
            Express express = expressModel.getExpressByExpressId(order.getExpressId());
            AssertUtil.notNull(express, "没有对应的快递公司，请确认订单快递公司id");

            //获取配置表内快递鸟的EBusinessID和AppKey
            String EBusinessID = stringRedisTemplate.opsForValue().get("express_ebusinessid");
            AssertUtil.notNull(EBusinessID, "请完善快递鸟配置信息：");

            String AppKey = stringRedisTemplate.opsForValue().get("express_apikey");
            AssertUtil.notNull(AppKey, "请完善快递鸟配置信息：");

            TracesResult tracesResult = TrackUtil.getKdniaoTrack(order.getOrderSn(), express.getExpressCode(),
                    order.getExpressName(), order.getExpressNumber(), EBusinessID, AppKey);
            return SldResponse.success(tracesResult);
        }
    }

}
