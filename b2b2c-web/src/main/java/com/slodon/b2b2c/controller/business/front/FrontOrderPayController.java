package com.slodon.b2b2c.controller.business.front;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.slodon.b2b2c.business.example.OrderExample;
import com.slodon.b2b2c.business.example.OrderProductExample;
import com.slodon.b2b2c.business.pojo.Order;
import com.slodon.b2b2c.business.pojo.OrderPay;
import com.slodon.b2b2c.business.pojo.OrderProduct;
import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.constant.MemberWxSignConst;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.constant.StarterConfigConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.*;
import com.slodon.b2b2c.member.example.MemberWxsignExample;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.member.pojo.MemberWxsign;
import com.slodon.b2b2c.model.business.OrderModel;
import com.slodon.b2b2c.model.business.OrderPayModel;
import com.slodon.b2b2c.model.business.OrderProductModel;
import com.slodon.b2b2c.model.member.MemberModel;
import com.slodon.b2b2c.model.member.MemberWxsignModel;
import com.slodon.b2b2c.starter.entity.SlodonPayRequest;
import com.slodon.b2b2c.starter.entity.SlodonPayResponse;
import com.slodon.b2b2c.starter.pay.SlodonPay;
import com.slodon.b2b2c.util.PayPropertiesUtil;
import com.slodon.b2b2c.vo.business.OrderPayInfoVO;
import com.slodon.b2b2c.vo.business.PayMethodVO;
import io.swagger.annotations.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.slodon.b2b2c.core.constant.PayConst.*;

@Slf4j
@Api(tags = "front-订单支付")
@RestController
@RequestMapping("v3/business/front/orderPay")
public class FrontOrderPayController {

    private final static String PAY_SUCCESS_URL = "/#/pages/order/list";
    private final static String PAY_SUCCESS_URL_PC = "/#/member/order/list";

    @Resource
    private OrderPayModel orderPayModel;
    @Resource
    private OrderProductModel orderProductModel;
    @Resource
    private OrderModel orderModel;
    @Resource
    private MemberModel memberModel;
    @Resource
    private MemberWxsignModel memberWxsignModel;
    @Resource
    private SlodonPay slodonPay;
    @Resource
    private PayPropertiesUtil payPropertiesUtil;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("payInfo")
    @ApiOperation("支付页面信息接口,状态码267==订单已支付")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paySn", value = "支付单号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "payFrom", defaultValue = "1", paramType = "query",
                    value = "支付来源，默人1==下单跳转支付，可以使用余额；2==订单列表跳转支付，不可使用余额")
    })
    public JsonResult<OrderPayInfoVO> payInfo(HttpServletRequest request,
                                              @RequestParam(value = "paySn") String paySn,
                                              @RequestParam(value = "payFrom", required = false, defaultValue = "1") Integer payFrom) {
        Member member = UserUtil.getUser(request, Member.class);
        OrderPayInfoVO vo = new OrderPayInfoVO();

        //查询支付单是否生成
        OrderPay orderPay = orderPayModel.getOrderPayByPaySn(paySn);
        if (orderPay == null) {
            //未生成订单支付信息，查询mq处理结果
            String mqResult = stringRedisTemplate.opsForValue().get(StarterConfigConst.ORDER_SUBMIT_MQ_REDIS_PREFIX + paySn);
            if (mqResult == null) {
                //redis中的订单入队信息已删除，订单生成失败
                vo.setDealState(OrderConst.ORDER_SUBMIT_DEAL_STATE_2);
                return SldResponse.success(vo);
            }
            //redis中的订单入队信息存在，还在排队处理中
            vo.setDealState(OrderConst.ORDER_SUBMIT_DEAL_STATE_1);
            return SldResponse.success(vo);
        }

        //支付单已生成，可以去支付
        vo.setDealState(OrderConst.ORDER_SUBMIT_DEAL_STATE_3);
        //查询支付单号下所有待支付的订单
        OrderExample orderExample = new OrderExample();
        orderExample.setPaySn(paySn);
        orderExample.setOrderState(OrderConst.ORDER_STATE_10);
        orderExample.setMemberId(member.getMemberId());
        List<Order> orderList = orderModel.getOrderList(orderExample, null);
        if (CollectionUtils.isEmpty(orderList)) {
            return SldResponse.failSpecial("订单已支付");
        }

        orderList.forEach(order -> {
            vo.setOrderAmount(vo.getOrderAmount().add(order.getOrderAmount()));
            vo.setAlreadyPay(vo.getAlreadyPay().add(order.getBalanceAmount()).add(order.getPayAmount()));
            //查询货品列表
            OrderProductExample orderProductExample = new OrderProductExample();
            orderProductExample.setOrderSn(order.getOrderSn());
            List<OrderProduct> orderProductList = orderProductModel.getOrderProductList(orderProductExample, null);
            orderProductList.forEach(orderProduct -> {
                vo.getGoodsNameList().add(orderProduct.getGoodsName());
            });
        });

        vo.setReceiveAddress(orderList.get(0).getReceiverAreaInfo() + orderList.get(0).getReceiverAddress());
        vo.setReceiverName(orderList.get(0).getReceiverName());
        vo.setReceiverMobile(CommonUtil.dealMobile(orderList.get(0).getReceiverMobile()));
        vo.setPaySn(paySn);
        vo.setCanUseBalance(payFrom == 1);//下单跳转支付，可以使用余额

        //查询会员可用余额
        Member memberDb = memberModel.getMemberByMemberId(member.getMemberId());
        vo.setBalanceAvailable(memberDb.getBalanceAvailable());

        return SldResponse.success(vo);
    }


    @GetMapping("payMethod")
    @ApiOperation("获取可用的支付方式接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "source", value = "支付发起来源 pc==pc,mbrowser==移动设备浏览器,app==app,wxxcx==微信小程序,wxbrowser==微信内部浏览器",
                    required = true, paramType = "query"),
            @ApiImplicitParam(name = "type", value = "支付发起类型，1==订单支付，2==充值", required = true, paramType = "query")
    })
    public JsonResult<List<PayMethodVO>> payMethod(HttpServletRequest request,
                                                   @RequestParam(value = "source") String source,
                                                   @RequestParam(value = "type") Integer type) {
        List<PayMethodVO> vos = new ArrayList<>();

        //查询平台开启的所有支付方式

        //三方支付方式
        switch (source) {
            case SOURCE_PC:
                if ("1".equals(stringRedisTemplate.opsForValue().get("wxpay_is_enable_pc"))) {
                    vos.add(new PayMethodVO(METHOD_WX, NAME_WX, WX_TYPE_NATIVE));
                }
                if ("1".equals(stringRedisTemplate.opsForValue().get("alipay_is_enable_pc"))) {
                    vos.add(new PayMethodVO(METHOD_ALIPAY, NAME_ALIPAY, ALI_TYPE_NATIVE));
                }
                break;
            case SOURCE_MBROWSER:
                if ("1".equals(stringRedisTemplate.opsForValue().get("wxpay_is_enable_h5"))) {
                    vos.add(new PayMethodVO(METHOD_WX, NAME_WX, WX_TYPE_MWEB));
                }
                if ("1".equals(stringRedisTemplate.opsForValue().get("alipay_is_enable_h5"))) {
                    vos.add(new PayMethodVO(METHOD_ALIPAY, NAME_ALIPAY, ALI_TYPE_MWEB));
                }
                break;
            case SOURCE_APP:
                if ("1".equals(stringRedisTemplate.opsForValue().get("wxpay_is_enable_app"))) {
                    vos.add(new PayMethodVO(METHOD_WX, NAME_WX, WX_TYPE_APP));
                }
                if ("1".equals(stringRedisTemplate.opsForValue().get("alipay_is_enable_h5"))) {
                    vos.add(new PayMethodVO(METHOD_ALIPAY, NAME_ALIPAY, ALI_TYPE_APP));
                }
                break;
            case SOURCE_WXXCX:
            case SOURCE_WXBROWSER:
                if ("1".equals(stringRedisTemplate.opsForValue().get("wxpay_is_enable_miniapp"))) {
                    vos.add(new PayMethodVO(METHOD_WX, NAME_WX, WX_TYPE_JSAPI));
                }
                break;
        }

        //余额支付方式
        if (type == 1) {
            if (SOURCE_PC.equals(source)) {
                //查看pc余额是否开启
                if ("1".equals(stringRedisTemplate.opsForValue().get("balance_pay_is_enable_pc"))) {
                    vos.add(new PayMethodVO(METHOD_BALANCE, NAME_BALANCE, TYPE_BALANCE));
                }
            } else {
                //查看移动端余额支付是否开启
                if ("1".equals(stringRedisTemplate.opsForValue().get("balance_pay_is_enable_h5"))) {
                    vos.add(new PayMethodVO(METHOD_BALANCE, NAME_BALANCE, TYPE_BALANCE));
                }
            }
        }

        return SldResponse.success(vos);
    }

    @GetMapping("payPwdCheck")
    @ApiOperation("选择余额支付校验是否设置了支付密码接口")
    @ApiResponses(
            @ApiResponse(code = 200, message = "data:true==设置了支付密码；false==未设置支付密码")
    )
    public JsonResult payInfo(HttpServletRequest request) {
        Member member = UserUtil.getUser(request, Member.class);
        Member memberDb = memberModel.getMemberByMemberId(member.getMemberId());
        return SldResponse.success(!StringUtils.isEmpty(memberDb.getPayPwd()));
    }


    @SneakyThrows
    @PostMapping("doPay")
    @ApiOperation("去支付接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paySn", value = "支付单号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "payMethod", value = "支付方式，balance==余额支付；wx==微信支付；alipay==支付宝", required = true, paramType = "query"),
            @ApiImplicitParam(name = "payType", required = true, paramType = "query",
                    value = "支付类型，单一支付类型：[BALANCE,NATIVE,APP,JSAPI,MWEB]  组合支付类型：[BALANCE+NATIVE,BALANCE+APP,BALANCE+JSAPI,BALANCE+MWEB]"),
            @ApiImplicitParam(name = "payPwd", value = "支付密码，余额支付必传", paramType = "query"),
            @ApiImplicitParam(name = "code", value = "用户code（JSAPI支付时必填）", paramType = "query"),
            @ApiImplicitParam(name = "codeSource", value = "用户code来源（JSAPI支付时必填）：1==小程序，2==微信内部浏览器", paramType = "query")
    })
    public JsonResult<SlodonPayResponse> doPay(HttpServletRequest request,
                                               @RequestParam(value = "paySn") String paySn,
                                               @RequestParam(value = "payMethod") String payMethod,
                                               @RequestParam(value = "payType") String payType,
                                               @RequestParam(value = "payPwd", required = false) String payPwd,
                                               @RequestParam(value = "code", required = false) String code,
                                               @RequestParam(value = "codeSource", required = false) Integer codeSource) {
        Member member = UserUtil.getUser(request, Member.class);
        Member memberDb = memberModel.getMemberByMemberId(member.getMemberId());

        //余额支付时密码校验
        if (METHOD_BALANCE.equals(payMethod) || payType.contains("BALANCE")) {
            //使用余额，密码必传
            AssertUtil.notEmpty(payPwd, "请输入支付密码");

            //校验密码
            AssertUtil.isTrue(!Md5.getMd5String(payPwd).equals(memberDb.getPayPwd()), "支付密码有误");
        }

        //查询支付状态
        OrderPay orderPay = orderPayModel.getOrderPayByPaySn(paySn);
        AssertUtil.isTrue(!orderPay.getMemberId().equals(member.getMemberId()), "您不能操作他人订单");
        AssertUtil.isTrue(OrderConst.API_PAY_STATE_1.equals(orderPay.getApiPayState()), "订单已支付，请勿重复支付");

        //查询支付单号下所有待支付的订单
        OrderExample orderExample = new OrderExample();
        orderExample.setPaySn(paySn);
        orderExample.setMemberId(member.getMemberId());
        orderExample.setOrderState(OrderConst.ORDER_STATE_10);
        List<Order> orderList = orderModel.getOrderList(orderExample, null);
        AssertUtil.notEmpty(orderList, "订单已支付");

        BigDecimal needPay = new BigDecimal("0.00");//需要支付的总金额
        for (Order order : orderList) {
            needPay = needPay.add(order.getOrderAmount().subtract(order.getBalanceAmount()).subtract(order.getPayAmount()));
        }

        if (METHOD_BALANCE.equals(payMethod)) {
            //余额单独支付，校验余额是否充足
            AssertUtil.isTrue(needPay.compareTo(memberDb.getBalanceAvailable()) > 0, "余额不足，请选择其他支付方式");
        }

        if (payType.contains("BALANCE")) {
            //处理余额支付,计算出需要三方支付的金额
            needPay = orderPayModel.balancePay(orderList, memberDb);
        }
        if (needPay.compareTo(BigDecimal.ZERO) > 0) {
            //还需支付金额大于0，执行三方支付
            //将组合支付中的 BALANCE+去除
            payType = payType.replace("BALANCE+", "");

            //修改订单支付方式
            String paymentCode = (payType + payMethod).toUpperCase();
            String paymentName = payType + (payMethod.equalsIgnoreCase(METHOD_WX) ? NAME_WX : payMethod.equalsIgnoreCase(METHOD_ALIPAY) ? NAME_ALIPAY : NAME_BALANCE);
            Order order = new Order();
            order.setPaymentCode(paymentCode);
            order.setPaymentName(paymentName);
            orderModel.updateOrderByExample(order, orderExample);

            //构造三方支付请求参数
            SlodonPayRequest payRequest = this.getPayRequest(member.getMemberId(), payMethod, payType, paySn, needPay, code, codeSource, WebUtil.getRealIp(request));

            return SldResponse.success(slodonPay.unitePay(payRequest));
        } else {
            //余额足够支付,直接跳转支付成功页面
            return SldResponse.success(new SlodonPayResponse("redirect", DomainUrlUtil.SLD_H5_URL + PAY_SUCCESS_URL));
        }

    }

    /**
     * 构造支付请求对象
     *
     * @param memberId   会员id
     * @param payMethod  支付方式
     * @param payType    支付类型
     * @param paySn      支付单号
     * @param payAmount  支付金额
     * @param code       用户code（JSAPI支付时必填）
     * @param codeSource 用户code来源（JSAPI支付时必填）：1==小程序，2==微信内部浏览器
     * @param ip         ip
     * @return 支付请求对象
     */
    private SlodonPayRequest getPayRequest(Integer memberId, String payMethod, String payType, String paySn,
                                           BigDecimal payAmount, String code, Integer codeSource, String ip) {
        SlodonPayRequest request = new SlodonPayRequest();
        AssertUtil.isTrue(!payMethod.equals(METHOD_WX) && !payMethod.equals(METHOD_ALIPAY), "支付方式有误");
        request.setPayMethod(payMethod);
        request.setPayType(payType);
        if (payType.equalsIgnoreCase(ALI_TYPE_NATIVE)) {
            //pc扫码支付
            request.setReturnUrl(DomainUrlUtil.SLD_PC_URL + PAY_SUCCESS_URL_PC);
        } else {
            //移动支付
            request.setReturnUrl(DomainUrlUtil.SLD_H5_URL + PAY_SUCCESS_URL);
        }
        if (payMethod.equals(METHOD_WX)) {
            request.setWxPayProperties(payPropertiesUtil.getWxPayProperties(payType, codeSource));
            request.setNotifyUrl(DomainUrlUtil.SLD_API_URL + "/v3/business/front/orderPayCallback/wx");
            request.setBody(stringRedisTemplate.opsForValue().get("wxpay_order_name"));
            if (payType.equals(WX_TYPE_JSAPI)) {
                //小程序、微信内部浏览器支付，获取openid
                request.setOpenId(this.getOpenId(codeSource, memberId, code));
            }
        } else {
            request.setAlipayProperties(payPropertiesUtil.getAliPayProperties());
            //todo 支付宝支付返回地址待修改
            request.setNotifyUrl(DomainUrlUtil.SLD_API_URL + "/v3/business/front/orderPayCallback/ali");
            request.setSubject(stringRedisTemplate.opsForValue().get("alipay_all_subject"));
            request.setBody(request.getSubject());
        }
        request.setPaySn(paySn);
        if ("1".equals(stringRedisTemplate.opsForValue().get("alipay_test_enable_h5"))) {
            request.setPayAmount(new BigDecimal("0.01"));
        } else {
            request.setPayAmount(payAmount);
        }
        request.setIp(ip);

        return request;
    }


    /**
     * 公众号支付/小程序支付，获取openid
     *
     * @param codeSource 1.小程序，2-微信内部浏览器
     * @param memberId   会员id
     * @param code       用户code
     * @return
     */
    private String getOpenId(Integer codeSource, Integer memberId, String code) {
        MemberWxsignExample memberWxsignExample = new MemberWxsignExample();
        memberWxsignExample.setMemberId(memberId);
        if (codeSource == 1) {
            //小程序
            memberWxsignExample.setResource(MemberWxSignConst.XCX);
        } else {
            //微信内部浏览器
            memberWxsignExample.setResource(MemberWxSignConst.GZH);
        }
        List<MemberWxsign> memberWxsignList = memberWxsignModel.getMemberWxsignList(memberWxsignExample, null);
        if (!CollectionUtils.isEmpty(memberWxsignList)) {
            //有记录
            return memberWxsignList.get(0).getOpenid();
        }

        //没有授权记录，根据code获取openid
        String token_url = "";//获取openid的url
        if (codeSource == 1) {
            //小程序
            token_url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + stringRedisTemplate.opsForValue().get("login_wx_mini_appid") +
                    "&secret=" + stringRedisTemplate.opsForValue().get("login_wx_mini_appsecret") + "&js_code=" + code + "&grant_type=authorization_code";
        } else {
            //微信内部浏览器
            token_url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + stringRedisTemplate.opsForValue().get("login_wx_dev_appid") +
                    "&secret=" + stringRedisTemplate.opsForValue().get("login_wx_dev_appsecret") + "&code=" + code + "&grant_type=authorization_code";
        }
        String resp = HttpUtil.get(token_url);
        if (0 == JSONObject.parseObject(resp).getIntValue("errcode")) {
            return JSONObject.parseObject(resp).getString("openid");
        } else {
            log.error("获取openid出错：" + resp);
            throw new MallException("获取openid出错：");
        }
    }
}
