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

@Api(tags = "front-????????????")
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

    @ApiOperation("????????????(????????????????????????)")
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

    @ApiOperation("????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderState", value = "???????????????0-????????????10-??????????????????20-??????????????????30-??????????????????40-?????????;50-?????????", paramType = "query"),
            @ApiImplicitParam(name = "orderSn", value = "?????????", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "????????????", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "??????????????????", defaultValue = "1", paramType = "query")
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
        Member member = UserUtil.getUser(request, Member.class);

        IntegralOrder order = integralOrderModel.getOrdersWithOpByOrderSn(orderSn);
        AssertUtil.notNull(order, "????????????");
        AssertUtil.isTrue(!member.getMemberId().equals(order.getMemberId()), "????????????????????????");

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
        if (order.getOrderState() == OrderConst.ORDER_STATE_30) {
            //??????????????????????????????????????????
            int autoReceiveDay = Integer.parseInt(stringRedisTemplate.opsForValue().get("time_limit_of_auto_receive"));
            //????????????
            Date autoReceiveTime = TimeUtil.getDayAgoDate(order.getDeliverTime(), autoReceiveDay);
            detailVO.setAutoReceiveTime(autoReceiveTime);
        }
        //??????????????????
        Store store = storeModel.getStoreByStoreId(order.getStoreId());
        AssertUtil.notNull(store,"???????????????");
        detailVO.setServicePhone(store.getServicePhone());
        return SldResponse.success(detailVO);
    }

    @ApiOperation("????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "?????????", required = true, paramType = "query")
    })
    @GetMapping("getTrace")
    public JsonResult<TracesResult> getTrace(HttpServletRequest request, String orderSn) {
        Member member = UserUtil.getUser(request, Member.class);
        //??????????????????
        IntegralOrder order = integralOrderModel.getOrdersWithOpByOrderSn(orderSn);
        AssertUtil.notNull(order, "??????????????????");
        AssertUtil.isTrue(!member.getMemberId().equals(order.getMemberId()), "?????????????????????");

        IntegralOrderProduct orderProduct = order.getOrderProductList().get(0);
        if (order.getOrderState() > OrderConst.ORDER_STATE_20 && StringUtil.isNullOrZero(order.getExpressId())) {
            TracesResult tracesResult = new TracesResult();
            tracesResult.setGoodsImage(FileUrlUtil.getFileUrl(orderProduct.getProductImage(), null));
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
            tracesResult.setGoodsImage(FileUrlUtil.getFileUrl(orderProduct.getProductImage(), null));
            return SldResponse.success(tracesResult);
        }
    }

}
