package com.slodon.b2b2c.controller.integral.front;

import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.express.TracesResult;
import com.slodon.b2b2c.core.express.TrackUtil;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.*;
import com.slodon.b2b2c.integral.example.IntegralOrderExample;
import com.slodon.b2b2c.integral.example.IntegralOrderLogExample;
import com.slodon.b2b2c.integral.example.IntegralOrderPayExample;
import com.slodon.b2b2c.integral.example.IntegralOrderProductExample;
import com.slodon.b2b2c.integral.pojo.IntegralOrder;
import com.slodon.b2b2c.integral.pojo.IntegralOrderLog;
import com.slodon.b2b2c.integral.pojo.IntegralOrderPay;
import com.slodon.b2b2c.integral.pojo.IntegralOrderProduct;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.model.integral.IntegralOrderLogModel;
import com.slodon.b2b2c.model.integral.IntegralOrderModel;
import com.slodon.b2b2c.model.integral.IntegralOrderPayModel;
import com.slodon.b2b2c.model.integral.IntegralOrderProductModel;
import com.slodon.b2b2c.model.seller.StoreModel;
import com.slodon.b2b2c.model.system.ExpressModel;
import com.slodon.b2b2c.seller.pojo.Store;
import com.slodon.b2b2c.system.pojo.Express;
import com.slodon.b2b2c.vo.integral.IntegralOrderDetailVO;
import com.slodon.b2b2c.vo.integral.IntegralOrderProductVO;
import com.slodon.b2b2c.vo.integral.IntegralOrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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

@Api(tags = "front-订单管理")
@RestController
@RequestMapping("v3/integral/front/integral/order")
public class FrontIntegralOrderController extends BaseController {

    @Resource
    private IntegralOrderModel integralOrderModel;
    @Resource
    private IntegralOrderProductModel integralOrderProductModel;
    @Resource
    private IntegralOrderLogModel integralOrderLogModel;
    @Resource
    private IntegralOrderPayModel integralOrderPayModel;
    @Resource
    private StoreModel storeModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ExpressModel expressModel;

    @ApiOperation("订单数量(我的模块展示使用)")
    @GetMapping("orderCount")
    public JsonResult orderCount(HttpServletRequest request) {
        Member member = UserUtil.getUser(request, Member.class);

        IntegralOrderExample example = new IntegralOrderExample();
        example.setMemberId(member.getMemberId());
        example.setOrderStateIn(OrderConst.ORDER_STATE_10 + "," + OrderConst.ORDER_STATE_20 + "," + OrderConst.ORDER_STATE_30);
        example.setDeleteState(OrderConst.DELETE_STATE_0);
        Integer orderCount = integralOrderModel.getIntegralOrderCount(example);
        return SldResponse.success(orderCount);
    }

    @ApiOperation("订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderState", value = "订单状态：0-已取消；10-待付款订单；20-待发货订单；30-待收货订单；40-已完成;50-已关闭", paramType = "query"),
            @ApiImplicitParam(name = "orderSn", value = "订单号", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<IntegralOrderVO>> list(HttpServletRequest request, Integer orderState, String orderSn) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        Member member = UserUtil.getUser(request, Member.class);

        IntegralOrderExample example = new IntegralOrderExample();
        example.setOrderState(orderState);
        example.setOrderSnLike(orderSn);
        example.setMemberId(member.getMemberId());
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
        Member member = UserUtil.getUser(request, Member.class);

        IntegralOrder order = integralOrderModel.getOrdersWithOpByOrderSn(orderSn);
        AssertUtil.notNull(order, "无此订单");
        AssertUtil.isTrue(!member.getMemberId().equals(order.getMemberId()), "您无权操作此订单");

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
        if (order.getOrderState() == OrderConst.ORDER_STATE_30) {
            //待收货状态，计算自动收货时间
            int autoReceiveDay = Integer.parseInt(stringRedisTemplate.opsForValue().get("time_limit_of_auto_receive"));
            //计算时间
            Date autoReceiveTime = TimeUtil.getDayAgoDate(order.getDeliverTime(), autoReceiveDay);
            detailVO.setAutoReceiveTime(autoReceiveTime);
        }
        //店铺客服电话
        Store store = storeModel.getStoreByStoreId(order.getStoreId());
        AssertUtil.notNull(store,"店铺不存在");
        detailVO.setServicePhone(store.getServicePhone());
        return SldResponse.success(detailVO);
    }

    @ApiOperation("查看物流")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单号", required = true, paramType = "query")
    })
    @GetMapping("getTrace")
    public JsonResult<TracesResult> getTrace(HttpServletRequest request, String orderSn) {
        Member member = UserUtil.getUser(request, Member.class);
        //查询订单信息
        IntegralOrder order = integralOrderModel.getOrdersWithOpByOrderSn(orderSn);
        AssertUtil.notNull(order, "订单信息为空");
        AssertUtil.isTrue(!member.getMemberId().equals(order.getMemberId()), "您无权查看物流");

        IntegralOrderProduct orderProduct = order.getOrderProductList().get(0);
        if (order.getOrderState() > OrderConst.ORDER_STATE_20 && StringUtil.isNullOrZero(order.getExpressId())) {
            TracesResult tracesResult = new TracesResult();
            tracesResult.setGoodsImage(FileUrlUtil.getFileUrl(orderProduct.getProductImage(), null));
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
            tracesResult.setGoodsImage(FileUrlUtil.getFileUrl(orderProduct.getProductImage(), null));
            return SldResponse.success(tracesResult);
        }
    }

}
