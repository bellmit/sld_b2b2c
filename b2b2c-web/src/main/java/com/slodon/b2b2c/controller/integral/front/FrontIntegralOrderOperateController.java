package com.slodon.b2b2c.controller.integral.front;

import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.integral.dto.OrderSubmitParamDTO;
import com.slodon.b2b2c.integral.example.IntegralOrderExample;
import com.slodon.b2b2c.integral.pojo.IntegralOrder;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.model.integral.IntegralOrderModel;
import com.slodon.b2b2c.model.system.ReasonModel;
import com.slodon.b2b2c.system.pojo.Reason;
import com.slodon.b2b2c.vo.integral.IntegralOrderSubmitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Api(tags = "front-订单操作")
@RestController
@RequestMapping("v3/integral/front/integral/orderOperate")
public class FrontIntegralOrderOperateController extends BaseController {

    @Resource
    private IntegralOrderModel integralOrderModel;
    @Resource
    private ReasonModel reasonModel;

    @ApiOperation("确认订单接口（去结算），获取提交订单页数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "货品id", required = true),
            @ApiImplicitParam(name = "number", value = "购买数量", required = true),
            @ApiImplicitParam(name = "useIntegral", value = "使用积分")
    })
    @PostMapping("confirm")
    public JsonResult<IntegralOrderSubmitVO> confirm(HttpServletRequest request, Long productId, Integer number, Integer useIntegral) {
        AssertUtil.isTrue(StringUtil.isNullOrZero(productId), "请选择要购买的商品");
        AssertUtil.isTrue(StringUtil.isNullOrZero(number), "请选择要购买的数量");

        Member member = UserUtil.getUser(request, Member.class);

        IntegralOrderSubmitVO vo = integralOrderModel.OrderConfirm(productId, number, member, useIntegral);
        return SldResponse.success(vo);
    }

    @ApiOperation("提交订单接口")
    @PostMapping("submit")
    public JsonResult submit(HttpServletRequest request, OrderSubmitParamDTO dto) {

        Member member = UserUtil.getUser(request, Member.class);

        //通用提交订单
        Map<String, Object> dataMap = integralOrderModel.submitOrder(dto, member);

        return SldResponse.success(dataMap);
    }

    @PostMapping("cancel")
    @ApiOperation("取消订单接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "reasonId", value = "取消原因", required = true, paramType = "query")
    })
    public JsonResult cancelOrder(HttpServletRequest request, String orderSn, Integer reasonId) {
        Member member = UserUtil.getUser(request, Member.class);

        //订单信息
        IntegralOrderExample orderExample = new IntegralOrderExample();
        orderExample.setOrderSn(orderSn);
        orderExample.setMemberId(member.getMemberId());
        orderExample.setOrderState(OrderConst.ORDER_STATE_10);
        List<IntegralOrder> orderList = integralOrderModel.getIntegralOrderList(orderExample, null);

        AssertUtil.notEmpty(orderList, "请选择要取消的订单");

        //查询取消原因
        Reason reason = reasonModel.getReasonByReasonId(reasonId);
        AssertUtil.notNull(reason, "取消原因不存在");

        //订单取消
        integralOrderModel.cancelOrder(orderList, reason.getContent(), null, OrderConst.LOG_ROLE_MEMBER, Long.valueOf(member.getMemberId()), member.getMemberName(), "会员取消订单");

        return SldResponse.success("订单取消成功");
    }

    @PostMapping("receive")
    @ApiOperation("确认收货接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单号", required = true, paramType = "query")
    })
    public JsonResult cancelOrder(HttpServletRequest request, String orderSn) {
        Member member = UserUtil.getUser(request, Member.class);

        //订单信息
        IntegralOrder order = integralOrderModel.getIntegralOrderByOrderSn(orderSn);
        AssertUtil.isTrue(!order.getMemberId().equals(member.getMemberId()), "不能操作他人的订单");

        //确认收货
        integralOrderModel.receiveOrder(order, OrderConst.LOG_ROLE_MEMBER, Long.valueOf(member.getMemberId()), member.getMemberName(), "完成");

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
        IntegralOrder order = integralOrderModel.getIntegralOrderByOrderSn(orderSn);
        AssertUtil.isTrue(!order.getMemberId().equals(member.getMemberId()), "您无权删除他人订单");
        AssertUtil.isTrue(!order.getOrderState().equals(OrderConst.ORDER_STATE_0)
                && !order.getOrderState().equals(OrderConst.ORDER_STATE_40), "您无权删除他人订单");
        //修改订单状态
        IntegralOrder updateOrder = new IntegralOrder();
        updateOrder.setDeleteState(OrderConst.DELETE_STATE_1);
        IntegralOrderExample example = new IntegralOrderExample();
        example.setOrderSn(orderSn);
        integralOrderModel.updateIntegralOrderByExample(updateOrder, example);
        return SldResponse.success("删除成功");
    }

}
