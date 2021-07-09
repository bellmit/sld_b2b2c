package com.slodon.b2b2c.controller.business.front;

import com.slodon.b2b2c.business.dto.OrderAfterDTO;
import com.slodon.b2b2c.business.example.OrderProductExample;
import com.slodon.b2b2c.business.pojo.Order;
import com.slodon.b2b2c.business.pojo.OrderProduct;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.model.business.OrderAfterServiceModel;
import com.slodon.b2b2c.model.business.OrderModel;
import com.slodon.b2b2c.model.business.OrderProductModel;
import com.slodon.b2b2c.vo.business.AfsApplyInfoVO;
import com.slodon.b2b2c.vo.business.AfsCountVO;
import com.slodon.b2b2c.vo.business.AfsOrderProductVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "front-售后申请")
@RestController
@RequestMapping("v3/business/front/after/sale/apply")
public class FrontAfterSaleApplyController extends BaseController {

    @Resource
    private OrderModel orderModel;
    @Resource
    private OrderProductModel orderProductModel;
    @Resource
    private OrderAfterServiceModel orderAfterServiceModel;

    @ApiOperation("退款申请信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "orderProductId", value = "订单货品id", required = true, paramType = "query")
    })
    @GetMapping("applyInfo")
    public JsonResult<AfsApplyInfoVO> applyInfo(HttpServletRequest request, String orderSn, Long orderProductId) {
        Member member = UserUtil.getUser(request, Member.class);

        AfsApplyInfoVO vo = orderAfterServiceModel.getAfterSaleApplyInfo(member.getMemberId(), orderSn, orderProductId);
        return SldResponse.success(vo);
    }

    @ApiOperation("售后订单货品详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderProductId", value = "订单货品id", required = true, paramType = "query")
    })
    @GetMapping("getOrderProductDetail")
    public JsonResult<AfsOrderProductVO> getOrderProductDetail(HttpServletRequest request, Long orderProductId) {
        Member member = UserUtil.getUser(request, Member.class);
        OrderProduct orderProduct = orderProductModel.getOrderProductByOrderProductId(orderProductId);
        AssertUtil.notNull(orderProduct, "订单货品信息为空");
        AssertUtil.isTrue(!orderProduct.getMemberId().equals(member.getMemberId()), "无权限");
        return SldResponse.success(new AfsOrderProductVO(orderProduct));
    }

    @ApiOperation("计算售后退款信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "orderProductInfos", value = "退换的订单货品列表，格式为：id1-num1,id2-num2...num为空时表示此订单货品全部退换", required = true, paramType = "query")
    })
    @GetMapping("countReturnMoney")
    public JsonResult<AfsCountVO> countReturnMoney(HttpServletRequest request, String orderSn, String orderProductInfos) {
        Member member = UserUtil.getUser(request, Member.class);
        String[] orderProductArray = orderProductInfos.split(",");
        //是否计算运费
        boolean isCalculateFee = false;
        //计算是否包含运费,未发货状态，并且此次售后为最后一次售后时，才退运费
        boolean containsFee = true;
        //查询订单货品并且未退过货的
        OrderProductExample orderProductExample = new OrderProductExample();
        orderProductExample.setMemberId(member.getMemberId());
        orderProductExample.setOrderSn(orderSn);
        orderProductExample.setReturnNumber(0);
        List<OrderProduct> orderProductList = orderProductModel.getOrderProductList(orderProductExample, null);
        //退货商品数量等于订单货品明细数量，则判断是否计算运费
        if (orderProductArray.length == orderProductList.size()) {
            isCalculateFee = true;
        }
        AfsCountVO vo = new AfsCountVO();//返回信息
        BigDecimal maxReturnMoney = new BigDecimal("0.00");//最大可退金额
        Integer maxReturnIntegral = 0;//最大可退积分
        Integer number = 0;//申请总数
        StringBuilder orderProductIds = new StringBuilder();//此次计算的订单id集合，用于查询是否是本订单最后一次售后
        for (String orderProductInfo : orderProductArray) {
            String[] split = orderProductInfo.split("-");
            Long orderProductId = Long.valueOf(split[0]);
            orderProductIds.append(",").append(orderProductId);
            OrderProduct orderProduct = orderProductModel.getOrderProductByOrderProductId(orderProductId);
            if (split.length == 1 || StringUtils.isEmpty(split[1]) || Integer.valueOf(split[1]).compareTo(orderProduct.getProductNum()) >= 0) {
                //未传商品数量,或者要退换的数量大于等于商品数量，默认计算所有商品数量
                maxReturnMoney = maxReturnMoney.add(orderProduct.getMoneyAmount());
                maxReturnIntegral += orderProduct.getIntegral();
                number += orderProduct.getProductNum();
                //如果不计算运费，设置false
                if (!isCalculateFee) {
                    containsFee = false;
                }
            } else {
                //根据传来的数量计算要退的金额
                int actualNum = Integer.parseInt(split[1]);
                if (isCalculateFee) {
                    //如果购买商品数量等于退货数量
                    if (orderProduct.getProductNum() != actualNum) {
                        containsFee = false;
                    }
                } else {
                    //如果没有全部退货，不计算运费
                    containsFee = false;
                }
                BigDecimal returnMoney = orderProduct.getMoneyAmount()
                        .multiply(BigDecimal.valueOf(actualNum))
                        .divide(BigDecimal.valueOf(orderProduct.getProductNum()), 2, RoundingMode.DOWN);//舍去第三位小数
                Integer returnIntegral = orderProduct.getIntegral() * actualNum / orderProduct.getProductNum();//只保留整数位
                maxReturnMoney = maxReturnMoney.add(returnMoney);
                maxReturnIntegral += returnIntegral;
                number += actualNum;
            }
        }
        //查询订单信息
        Order orders = orderModel.getOrderByOrderSn(orderSn);
        AssertUtil.isTrue(!orders.getMemberId().equals(member.getMemberId()), "您无权操作此订单");
        if (orders.getOrderState().equals(OrderConst.ORDER_STATE_20)) {
            //未发货状态才退运费
            if (containsFee) {
                //是最后一次退，退还运费
                maxReturnMoney = maxReturnMoney.add(orders.getExpressFee());
                vo.setReturnExpressFee(orders.getExpressFee());
            } else {
                vo.setReturnExpressFee(BigDecimal.ZERO);
            }
            vo.setContainsFee(containsFee);
        } else {
            //不退运费设置false
            vo.setContainsFee(false);
            vo.setReturnExpressFee(BigDecimal.ZERO);
        }
        vo.setMaxReturnIntegral(maxReturnIntegral);
        vo.setMaxReturnMoney(maxReturnMoney);
        vo.setNumber(number);
        return SldResponse.success(vo);
    }

    @ApiOperation("可以申请售后订单货品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "orderProductId", value = "订单货品id，默认列表第一个元素，默认选中", required = true, paramType = "query")
    })
    @GetMapping("getOrderProductList")
    public JsonResult<List<AfsOrderProductVO>> getOrderProductList(HttpServletRequest request, String orderSn, Long orderProductId) {
        Member member = UserUtil.getUser(request, Member.class);

        //查询订单下退换数量均为0的订单货品
        OrderProductExample example = new OrderProductExample();
        example.setMemberId(member.getMemberId());
        example.setOrderSn(orderSn);
        example.setReturnNumber(0);
        example.setReplacementNumber(0);

        List<OrderProduct> orderProductList = orderProductModel.getOrderProductList(example, null);
        AssertUtil.notEmpty(orderProductList, "订单号有误");
        List<AfsOrderProductVO> vos = new ArrayList<>();
        //遍历找到默认的货品
        for (OrderProduct orderProduct : orderProductList) {
            if (orderProduct.getOrderProductId().equals(orderProductId)) {
                vos.add(new AfsOrderProductVO(orderProduct));
                break;
            }
        }
        AssertUtil.isTrue(vos.size() != 1, "订单货品不存在");
        if (vos.size() != 1) {
            //没找到默认货品
            return SldResponse.fail("订单货品不存在");
        }
        //剩余订单货品
        orderProductList.forEach(orderProduct -> {
            if (!orderProduct.getOrderProductId().equals(orderProductId)) {
                vos.add(new AfsOrderProductVO(orderProduct));
            }
        });
        return SldResponse.success(vos);
    }

    @ApiOperation("退款申请提交")
    @PostMapping("submit")
    public JsonResult returnedGoods(HttpServletRequest request, @RequestBody OrderAfterDTO orderAfterDTO) {
        Member member = UserUtil.getUser(request, Member.class);
        //返回单号
        orderAfterServiceModel.submitAfterSaleApply(orderAfterDTO, member.getMemberId());
        return SldResponse.success("提交成功");
    }

    @ApiOperation("根据申请件数获取退款金额")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderProductId", value = "订单货品id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "applyNum", value = "applyNum", required = true, paramType = "query")
    })
    @GetMapping("getReturnMoney")
    public JsonResult getReturnMoney(String orderProductId, Integer applyNum) {
        BigDecimal moneyCanReturn = orderAfterServiceModel.getReturnMoney(orderProductId, applyNum);
        return SldResponse.success(moneyCanReturn);
    }
}
