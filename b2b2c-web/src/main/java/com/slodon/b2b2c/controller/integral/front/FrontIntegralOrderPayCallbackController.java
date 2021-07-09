package com.slodon.b2b2c.controller.integral.front;

import cn.hutool.core.util.XmlUtil;
import com.alipay.api.internal.util.AlipaySignature;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.constant.PayConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.util.CommonTools;
import com.slodon.b2b2c.integral.example.IntegralOrderExample;
import com.slodon.b2b2c.integral.pojo.IntegralOrder;
import com.slodon.b2b2c.integral.pojo.IntegralOrderPay;
import com.slodon.b2b2c.model.integral.IntegralOrderModel;
import com.slodon.b2b2c.model.integral.IntegralOrderPayModel;
import com.slodon.b2b2c.starter.entity.AliPayProperties;
import com.slodon.b2b2c.starter.entity.SlodonRefundRequest;
import com.slodon.b2b2c.starter.entity.SlodonRefundResponse;
import com.slodon.b2b2c.starter.entity.WxPayProperties;
import com.slodon.b2b2c.starter.pay.SlodonPay;
import com.slodon.b2b2c.util.PayPropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static com.slodon.b2b2c.core.constant.PayConst.WX_TYPE_NATIVE;

/**
 * 订单支付回调controller
 */
@Slf4j
@RestController
@RequestMapping("v3/integral/front/integral/orderPayCallback")
public class FrontIntegralOrderPayCallbackController extends BaseController {

    @Resource
    private IntegralOrderPayModel integralOrderPayModel;
    @Resource
    private IntegralOrderModel integralOrderModel;
    @Resource
    private PayPropertiesUtil payPropertiesUtil;
    @Resource
    private SlodonPay slodonPay;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // 微信返回  fail 失败，success 成功
    private static final String RETURN_CODE_SUCCESS = "SUCCESS";
    private static final String RETURN_MSG_OK = "OK";
    private static final String RETURN_CODE_FAIL = "FAIL";

    /**
     * 订单支付支付宝回调
     *
     * @param request
     * @param response
     */
    @PostMapping("ali")
    public void aliPayCallback(HttpServletRequest request, HttpServletResponse response) {
        log.debug("-----------------------------------------------------------------------------");
        log.debug("支付宝通知开始");
        log.debug("-----------------------------------------------------------------------------");
        try {
            //获取支付宝post过来的反馈信息 map
            HashMap<String, String> params = new HashMap<>();
            //getParameterMap()一般多用于接收前台表单多参数传输的数据
            //支付宝的回调都是把信息放到request里面
            Map<String, String[]> parameterMap = request.getParameterMap();
            //迭代遍历,keySet()是获取所有的key值
            for (Iterator<String> iterator = parameterMap.keySet().iterator(); iterator.hasNext(); ) {
                String name = (String) iterator.next();
                String[] values = parameterMap.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //赋值
                params.put(name, valueStr);
            }

            //调用SDK,验证签名
            AliPayProperties aliPayProperties = payPropertiesUtil.getAliPayProperties();
            boolean signVerified = AlipaySignature.rsaCheckV1(params, aliPayProperties.public_key, aliPayProperties.input_charset, aliPayProperties.sign_type);
            if (signVerified) {
                // TODO 验签成功后，按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验，校验成功后在response中返回success并继续商户自身业务处理，校验失败返回failure
                //获取支付宝的通知返回参数，可参考技术文档中页面跳转异步通知参数列表(以下仅供参考)//补:参考跳转checkPayState-->AlipayTradeQueryResponse
                //商户订单号
                String paySn = params.get("out_trade_no");
                //支付宝交易号
                String trade_no = params.get("trade_no");
                //交易金额
                String total_amount = params.get("total_amount");
                //交易状态
                String trade_status = params.get("trade_status");
                //——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
                if ("TRADE_FINISHED".equals(trade_status)) {
                    //checkPayState 交易完成

//                    判断该笔订单是否在商户网站中已经做过处理
//                    如果没有做过处理，根据订单号（out_trade_no）在商户网站
//                    的订单系统中查到该笔订单的详细，并执行商户的业务程序
//                    请务必判断请求时的total_amount、seller_id与通知时获取的total_amount、seller_id为一致的
//                    如果有做过处理，不执行商户的业务程序
//
//                    注意：
//                    退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
                } else if ("TRADE_SUCCESS".equals(trade_status)) {
                    //已支付
                    //判断该笔订单是否在商户网站中已经做过处理
                    IntegralOrderPay orderpay = integralOrderPayModel.getIntegralOrderPayByPaySn(paySn);
                    if (OrderConst.API_PAY_STATE_1.equals(orderpay.getApiPayState())) {
                        //订单已支付，执行退款操作
                        if (orderpay.getPaymentCode().contains(PayConst.METHOD_ALIPAY.toUpperCase())){
                            //重复回调，不处理，直接返回成功
                            response.getWriter().println("success");
                            return;
                        }
                        //其他方式已支付，如余额、微信，执行退款
                        this.aliRefund(paySn,new BigDecimal(total_amount));
                        response.getWriter().println("success");
                        return;
                    }

                    //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                    //请务必判断请求时的total_amount、seller_id与通知时获取的total_amount、seller_id 为一致的
                    // 1-开启测试, 0-关闭测试
                    if ("1".equals(stringRedisTemplate.opsForValue().get("alipay_test_enable_h5"))) {//待定
                        //不校验支付额
                    } else {
                        if (!total_amount.equals(this.getPayAmountDb(paySn).toString())) {
                            log.error("订单支付金额不一致paySn:" + paySn);
                            response.getWriter().println("fail");
                            //订单金额不一致不往下执行
                            return;
                        }
                    }

                    //如果有做过处理，不执行商户的业务程序
                    //更改订单状态
                    IntegralOrderExample example = new IntegralOrderExample();
                    example.setPaySn(paySn);
                    List<IntegralOrder> list = integralOrderModel.getIntegralOrderList(example, null);
                    for (IntegralOrder order : list) {
                        //支付成功
                        integralOrderPayModel.orderPaySuccess(order.getOrderSn(), paySn, trade_no, order.getBalanceAmount(),
                                order.getOrderAmount().subtract(order.getBalanceAmount()).subtract(order.getPayAmount()),
                                order.getPaymentCode(), order.getPaymentName(), order.getMemberId(), order.getMemberName());
                    }
                    //请不要修改或删除
                    response.getWriter().println("success");
                }
            } else {
                // TODO 验签失败则记录异常日志，并在response中返回failure.
                log.error("验签失败");
                response.getWriter().println("fail");
            }
        } catch (Exception e) {
            log.error("支付宝通知失败", e);
        }
    }

    /**
     * 订单支付微信回调
     *
     * @param request
     * @param response
     */
    @PostMapping("wx")
    public void wxPayCallback(HttpServletRequest request, HttpServletResponse response) {
        log.debug("-----------------------------------------------------------------------------");
        log.debug("微信通知开始");
        log.debug("-----------------------------------------------------------------------------");
        ServletInputStream in;

        try {
            //微信支付配置
            WxPayProperties wxPayProperties = payPropertiesUtil.getWxPayProperties(WX_TYPE_NATIVE, 1);
            WxPayServiceImpl wxPayService = new WxPayServiceImpl();
            WxPayConfig wxPayConfig = new WxPayConfig();
            wxPayConfig.setAppId(StringUtils.trimToNull(wxPayProperties.getAppId()));
            wxPayConfig.setMchId(StringUtils.trimToNull(wxPayProperties.getMchId()));
            wxPayConfig.setMchKey(StringUtils.trimToNull(wxPayProperties.getMchKey()));
            wxPayService.setConfig(wxPayConfig);
            // post 过来的xml
            in = request.getInputStream();
            //转换微信post过来的xml内容
            String notifyData = CommonTools.inputStream2String(in);
            //处理xml
            WxPayOrderNotifyResult notifyResult = wxPayService.parseOrderNotifyResult(notifyData);
            String outTradeNo = notifyResult.getOutTradeNo();
            String paySn = outTradeNo.split("_")[0];//格式为paySn_payType，截取paySn
            String payType = outTradeNo.split("_")[1];
            log.error("通知开始...." + paySn);

            //调用SDK,验证签名
            notifyResult.checkResult(wxPayService, notifyResult.getSignType(), true);

            // 签名正确
            // 进行处理。
            // 注意特殊情况：订单已经退款，但收到了支付结果成功的通知，不应把商户侧订单状态从退款改成支付成功
            //安全校验(比较订单金额)二次校验
            IntegralOrderPay orderpay = integralOrderPayModel.getIntegralOrderPayByPaySn(paySn);
            if (OrderConst.API_PAY_STATE_1.equals(orderpay.getApiPayState())) {
                //订单已支付
                if (orderpay.getPaymentCode().contains(PayConst.METHOD_WX.toUpperCase()) &&
                        orderpay.getPaymentCode().contains(payType)){
                    //同一客户端重复回调，不做处理
                    sendMsg(response);
                    return;
                }
                //不同客户端重复支付，执行退款
                this.wxRefund(outTradeNo,payType,notifyResult.getTotalFee());
                sendMsg(response);
                return;
            }
            // 1-开启测试, 0-关闭测试
            if ("1".equals(stringRedisTemplate.opsForValue().get("wxpay_test_enable_h5"))) {
                //不校验支付额
            } else {
                int totalFeeDb = this.getPayAmountDb(paySn).multiply(new BigDecimal(100)).intValue();
                if (totalFeeDb != notifyResult.getTotalFee()) {
                    log.error("订单支付金额不一致paySn:" + paySn);
                    response.getWriter().println("fail");
                    //订单金额不一致不往下执行
                    return;
                }
            }
            //如果有做过处理，不执行商户的业务程序
            //更改订单状态
            IntegralOrderExample example = new IntegralOrderExample();
            example.setPaySn(paySn);
            List<IntegralOrder> list = integralOrderModel.getIntegralOrderList(example, null);
            for (IntegralOrder order : list) {
                //支付成功
                integralOrderPayModel.orderPaySuccess(order.getOrderSn(), paySn, notifyResult.getTransactionId(), order.getBalanceAmount(),
                        order.getOrderAmount().subtract(order.getBalanceAmount()).subtract(order.getPayAmount()),
                        order.getPaymentCode(), order.getPaymentName(), order.getMemberId(), order.getMemberName());
            }

            //返回微信应答报文,防止重复回调
            sendMsg(response);
        } catch (Exception e) {
            log.error("微信通知失败", e);
        }
    }

    /**
     * 获取三方支付实际应支付金额
     *
     * @param paySn
     * @return
     */
    private BigDecimal getPayAmountDb(String paySn) {
        //查询订单
        IntegralOrderExample orderExample = new IntegralOrderExample();
        orderExample.setPaySn(paySn);
        orderExample.setOrderState(OrderConst.ORDER_STATE_10);
        List<IntegralOrder> list = integralOrderModel.getIntegralOrderList(orderExample, null);
        //三方支付实际应支付金额
        BigDecimal payAmount = BigDecimal.ZERO;
        for (IntegralOrder order : list) {
            payAmount = order.getOrderAmount().subtract(order.getBalanceAmount()).subtract(order.getPayAmount());
        }
        return payAmount;
    }

    /**
     * 给微信反回应答报文，防止重复回调
     * @param response
     */
    private void sendMsg(HttpServletResponse response) {
        LinkedHashMap<String, String> responseMap = new LinkedHashMap<>();
        responseMap.put("return_code", RETURN_CODE_SUCCESS);
        responseMap.put("return_msg", RETURN_MSG_OK);
        String mapToXml = XmlUtil.mapToXmlStr(responseMap);
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.print(mapToXml);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * 执行微信退款
     * @param outTradeNo 商户支付单号
     * @param payType 支付类型
     * @param totalFee 支付金额，单位分
     */
    private void wxRefund(String outTradeNo, String payType, Integer totalFee){
        SlodonRefundRequest refundRequest = new SlodonRefundRequest();

        //微信退款
        refundRequest.setWxPayProperties(payPropertiesUtil.getWxPayProperties(payType, 1));
        //微信需要设置原支付金额
        BigDecimal payAmount = new BigDecimal(totalFee).divide(new BigDecimal(100),2, RoundingMode.HALF_UP);
        refundRequest.setTotalAmount(payAmount);
        refundRequest.setRefundAmount(payAmount);
        refundRequest.setPaySn(outTradeNo);
        refundRequest.setRefundSn(outTradeNo);
        try {
            SlodonRefundResponse refundResponse = slodonPay.refund(refundRequest, PayConst.METHOD_WX.toUpperCase());
            if (!refundResponse.getSuccess()){
                log.error("重复支付退款失败，商户号：{}，失败原因：{}",outTradeNo,refundResponse.getMsg());
            }
        } catch (Exception e) {
            log.error("重复支付退款失败，商户号：{}",outTradeNo,e);
        }
    }

    /**
     * 支付宝退款
     * @param outTradeNo 商户支付单号
     * @param refundAmount 退款金额
     */
    private void aliRefund(String outTradeNo,BigDecimal refundAmount){
        SlodonRefundRequest refundRequest = new SlodonRefundRequest();
        refundRequest.setAlipayProperties(payPropertiesUtil.getAliPayProperties());
        refundRequest.setPaySn(outTradeNo);
        refundRequest.setRefundSn(outTradeNo);
        refundRequest.setRefundAmount(refundAmount);
        try {
            SlodonRefundResponse refundResponse = slodonPay.refund(refundRequest, PayConst.METHOD_ALIPAY.toUpperCase());
            if (!refundResponse.getSuccess()){
                log.error("支付宝重复支付退款失败，商户号：{}，失败原因：{}",outTradeNo,refundResponse.getMsg());
            }
        } catch (Exception e) {
            log.error("支付宝重复支付退款失败，商户号：{}",outTradeNo,e);
        }
    }
}
