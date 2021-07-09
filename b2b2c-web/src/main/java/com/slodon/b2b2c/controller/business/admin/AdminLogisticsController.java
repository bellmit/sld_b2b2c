package com.slodon.b2b2c.controller.business.admin;

import com.slodon.b2b2c.business.pojo.Order;
import com.slodon.b2b2c.business.pojo.OrderExtend;
import com.slodon.b2b2c.business.pojo.OrderProduct;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.express.TracesResult;
import com.slodon.b2b2c.core.express.TrackUtil;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.model.business.OrderExtendModel;
import com.slodon.b2b2c.model.business.OrderModel;
import com.slodon.b2b2c.model.system.ExpressModel;
import com.slodon.b2b2c.system.pojo.Express;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Api(tags = "admin-物流信息")
@RestController
@RequestMapping("v3/business/admin/logistics")
public class AdminLogisticsController {

    @Resource
    private OrderModel orderModel;
    @Resource
    private OrderExtendModel orderExtendModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ExpressModel expressModel;

    @ApiOperation("订单查看物流")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单号", required = true, paramType = "query")
    })
    @GetMapping("order/getTrace")
    public JsonResult<TracesResult> getOrderTrace(HttpServletRequest request, String orderSn) {
        //查询订单信息
        Order order = orderModel.getOrdersWithOpByOrderSn(orderSn);
        OrderProduct orderProduct = order.getOrderProductList().get(0);
        if (order.getOrderState() > OrderConst.ORDER_STATE_20 && StringUtil.isNullOrZero(order.getExpressId())) {
            OrderExtend orderExtend = orderExtendModel.getOrderExtendByOrderSn(orderSn);
            TracesResult tracesResult = new TracesResult();
            tracesResult.setGoodsImage(FileUrlUtil.getFileUrl(orderProduct.getProductImage(), null));
            tracesResult.setExpressName(orderExtend.getDeliverName());
            tracesResult.setExpressNumber(orderExtend.getDeliverMobile());
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
            tracesResult.setGoodsImage(FileUrlUtil.getFileUrl(orderProduct.getProductImage(), null));
            return SldResponse.success(tracesResult);
        }
    }

}
