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


@Api(tags = "seller-????????????")
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

    @ApiOperation("????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "?????????", paramType = "query"),
            @ApiImplicitParam(name = "memberName", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "??????????????????", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "??????????????????", paramType = "query"),
            @ApiImplicitParam(name = "orderState", value = "???????????????0-????????????10-??????????????????20-??????????????????30-??????????????????40-?????????;50-?????????", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "????????????", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "??????????????????", defaultValue = "1", paramType = "query")
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
                //??????????????????????????????
                IntegralOrderProductExample orderProductExample = new IntegralOrderProductExample();
                orderProductExample.setOrderSn(integralOrder.getOrderSn());
                List<IntegralOrderProduct> orderProductList = integralOrderProductModel.getIntegralOrderProductList(orderProductExample, null);
                AssertUtil.notEmpty(orderProductList, "?????????????????????");
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

    @ApiOperation("????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "?????????", required = true)
    })
    @PostMapping("detail")
    public JsonResult<IntegralOrderDetailVO> detail(HttpServletRequest request, String orderSn) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        IntegralOrder order = integralOrderModel.getOrdersWithOpByOrderSn(orderSn);
        AssertUtil.notNull(order, "????????????");
        AssertUtil.isTrue(!vendor.getStoreId().equals(order.getStoreId()), "????????????????????????");

        //????????????????????????
        IntegralOrderPayExample orderPayExample = new IntegralOrderPayExample();
        orderPayExample.setPaySn(order.getPaySn());
        IntegralOrderPay orderPay = integralOrderPayModel.getIntegralOrderPayList(orderPayExample, null).get(0);

        IntegralOrderDetailVO detailVO = new IntegralOrderDetailVO(order, orderPay);
        List<IntegralOrderProductVO> productVOS = new ArrayList<>();
        //??????????????????
        order.getOrderProductList().forEach(orderProduct -> {
            productVOS.add(new IntegralOrderProductVO(orderProduct));
        });
        detailVO.setOrderProductList(productVOS);
        //????????????????????????
        IntegralOrderLogExample orderLogExample = new IntegralOrderLogExample();
        orderLogExample.setOrderSn(order.getOrderSn());
        orderLogExample.setOrderBy("log_time asc");
        List<IntegralOrderLog> orderLogList = integralOrderLogModel.getIntegralOrderLogList(orderLogExample, null);
        detailVO.setOrderLogList(orderLogList);
        return SldResponse.success(detailVO);
    }

    @ApiOperation("??????")
    @VendorLogger(option = "??????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "?????????", required = true, paramType = "query"),
            @ApiImplicitParam(name = "deliverType", value = "???????????????0-???????????????1-????????????", required = true, paramType = "query"),
            @ApiImplicitParam(name = "expressId", value = "????????????id(?????????????????????)", paramType = "query"),
            @ApiImplicitParam(name = "expressNumber", value = "????????????(?????????????????????)", paramType = "query"),
            @ApiImplicitParam(name = "deliverName", value = "?????????(?????????????????????)", paramType = "query"),
            @ApiImplicitParam(name = "deliverMobile", value = "????????????(?????????????????????)", paramType = "query")
    })
    @PostMapping("deliver")
    public JsonResult deliver(HttpServletRequest request, String orderSn, Integer deliverType, Integer expressId,
                              String expressNumber, String deliverName, String deliverMobile) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        IntegralOrder order = new IntegralOrder();
        order.setDeliverType(deliverType);
        if (deliverType == OrderConst.DELIVER_TYPE_0) {
            AssertUtil.notNullOrZero(expressId, "????????????id????????????");
            AssertUtil.notEmpty(expressNumber, "????????????????????????");

            Express express = expressModel.getExpressByExpressId(expressId);
            AssertUtil.notNull(express, "??????????????????????????????????????????");
            order.setExpressId(expressId);
            order.setExpressName(express.getExpressName());
            order.setExpressNumber(expressNumber);
        } else {
            AssertUtil.notEmpty(deliverName, "?????????????????????");
            AssertUtil.notEmpty(deliverMobile, "????????????????????????");
            order.setDeliverName(deliverName);
            order.setDeliverMobile(deliverMobile);
        }
        order.setDeliverTime(new Date());
        //??????????????????
        IntegralOrder orderDb = integralOrderModel.getIntegralOrderByOrderSn(orderSn);
        AssertUtil.isTrue(!orderDb.getStoreId().equals(vendor.getStoreId()), "????????????????????????");
        AssertUtil.isTrue(orderDb.getOrderState() != OrderConst.ORDER_STATE_20, "?????????????????????????????????");

        order.setOrderId(orderDb.getOrderId());
        order.setOrderSn(orderSn);
        order.setOrderState(OrderConst.ORDER_STATE_30);
        integralOrderModel.updateIntegralOrder(order);

        //??????????????????
        integralOrderLogModel.insertOrderLog(OrderConst.LOG_ROLE_VENDOR, vendor.getVendorId(), vendor.getVendorName(),
                orderSn, OrderConst.ORDER_STATE_30, "????????????");

        return SldResponse.success("??????????????????");
    }

    @ApiOperation("????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "?????????", required = true, paramType = "query")
    })
    @GetMapping("getTrace")
    public JsonResult<TracesResult> getTrace(HttpServletRequest request, String orderSn) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        //??????????????????
        IntegralOrder order = integralOrderModel.getIntegralOrderByOrderSn(orderSn);
        AssertUtil.notNull(order, "??????????????????");
        AssertUtil.isTrue(!vendor.getStoreId().equals(order.getStoreId()), "??????????????????????????????");

        if (order.getOrderState() > OrderConst.ORDER_STATE_20 && StringUtil.isNullOrZero(order.getExpressId())) {
            TracesResult tracesResult = new TracesResult();
            tracesResult.setExpressName(order.getDeliverName());
            tracesResult.setExpressNumber(order.getDeliverMobile());
            tracesResult.setType("1");
            return SldResponse.success(tracesResult);
        } else {
            AssertUtil.isTrue(StringUtil.isNullOrZero(order.getExpressId()), "????????????id????????????");
            //????????????????????????
            Express express = expressModel.getExpressByExpressId(order.getExpressId());
            AssertUtil.notNull(express, "?????????????????????????????????????????????????????????id");

            //??????????????????????????????EBusinessID???AppKey
            String EBusinessID = stringRedisTemplate.opsForValue().get("express_ebusinessid");
            AssertUtil.notNull(EBusinessID, "?????????????????????????????????");

            String AppKey = stringRedisTemplate.opsForValue().get("express_apikey");
            AssertUtil.notNull(AppKey, "?????????????????????????????????");

            TracesResult tracesResult = TrackUtil.getKdniaoTrack(order.getOrderSn(), express.getExpressCode(),
                    order.getExpressName(), order.getExpressNumber(), EBusinessID, AppKey);
            return SldResponse.success(tracesResult);
        }
    }

}
