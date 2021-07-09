package com.slodon.b2b2c.controller.member.front;

import cn.hutool.core.util.XmlUtil;
import com.alipay.api.internal.util.AlipaySignature;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.slodon.b2b2c.core.constant.MemberConst;
import com.slodon.b2b2c.core.constant.PayConst;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.CommonTools;
import com.slodon.b2b2c.member.example.MemberBalanceRechargeExample;
import com.slodon.b2b2c.member.pojo.MemberBalanceRecharge;
import com.slodon.b2b2c.model.member.MemberBalanceRechargeModel;
import com.slodon.b2b2c.starter.entity.AliPayProperties;
import com.slodon.b2b2c.starter.entity.SlodonRefundRequest;
import com.slodon.b2b2c.starter.entity.SlodonRefundResponse;
import com.slodon.b2b2c.starter.entity.WxPayProperties;
import com.slodon.b2b2c.starter.pay.SlodonPay;
import com.slodon.b2b2c.util.PayPropertiesUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

@Api(tags = "front-充值回调")
@Slf4j
@RestController
@RequestMapping("v3/member/front/rechargeCallback")
public class FrontRechargeCallbackController {

    @Resource
    private MemberBalanceRechargeModel memberBalanceRechargeModel;
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
     * 支付宝充值回调
     *
     * @param request
     * @param response
     */
    @PostMapping("ali")
    public void alipayNotify(HttpServletRequest request, HttpServletResponse response) {

        try {
            //获取支付宝POST过来反馈信息
            Map<String, String> params = new HashMap<>();
            Map requestParams = request.getParameterMap();
            for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                params.put(name, valueStr);
            }
            //调用SDK,验证签名
            AliPayProperties aliPayProperties = payPropertiesUtil.getAliPayProperties();
            boolean signVerified = AlipaySignature.rsaCheckV1(params, aliPayProperties.public_key, aliPayProperties.input_charset, aliPayProperties.sign_type);
            if (signVerified) {//验证成功
                // TODO 验签成功后，按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验，校验成功后在response中返回success并继续商户自身业务处理，校验失败返回failure
                //获取支付宝的通知返回参数，可参考技术文档中页面跳转异步通知参数列表(以下仅供参考)//
                //商户订单号
                String out_trade_no = params.get("out_trade_no");
                //支付宝交易号
                String trade_no = params.get("trade_no");
                //交易金额
                String total_amount = params.get("total_amount");
                //交易状态
                String trade_status = params.get("trade_status");
                //——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
                if (trade_status.equals("TRADE_FINISHED")) {
                    log.debug("订单已完成");
                    response.getWriter().println("success"); //请不要修改或删除
                } else if (trade_status.equals("TRADE_SUCCESS")) {
                    log.debug("支付成功");

                    //判断该笔订单是否在商户网站中已经做过处理
                    MemberBalanceRecharge memberBalanceRecharge = this.getMemberBalanceRechargeBySn(out_trade_no);
                    if (memberBalanceRecharge.getPayState().equals(MemberConst.PAY_STATE_2)){
                        //订单已支付，执行退款操作
                        if (memberBalanceRecharge.getPaymentCode().contains(PayConst.METHOD_ALIPAY.toUpperCase())){
                            //重复回调，不处理，直接返回成功
                            response.getWriter().println("success");
                            return;
                        }
                        //其他方式已支付，如余额、微信，执行退款
                        this.aliRefund(out_trade_no,new BigDecimal(total_amount));
                        response.getWriter().println("success");
                        return;
                    }

                    if ("1".equals(stringRedisTemplate.opsForValue().get("alipay_test_enable_h5"))) {// 1-开启测试, 0-关闭测试
                        //不校验支付额
                    } else {
                        //获取充值订单
                        if (!total_amount.equals(this.getPayAmountDb(out_trade_no).toString())) {
                            log.error("订单支付金额不一致paySn:" + out_trade_no);
                            response.getWriter().println("fail");
                            //订单金额不一致不往下执行
                            return;
                        }
                    }
                    boolean result = memberBalanceRechargeModel.payAfter(out_trade_no, trade_no);
                    if (!result) {
                        response.getWriter().println("fail");
                    }
                } else {
                    response.getWriter().println("fail");
                }
                response.getWriter().println("success");
            } else {
                response.getWriter().println("fail");
            }
        } catch (Exception e) {
            log.error("支付宝回调失败",e);
            try {
                response.getWriter().println("fail");
            } catch (IOException e1) {
                log.error("支付宝回调失败",e1);
            }
        }
    }

    /**
     * 微信充值回调
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/wx", method = {RequestMethod.GET, RequestMethod.POST})
    public void wxNotify(HttpServletRequest request, HttpServletResponse response) {
        ServletInputStream in;

        try {
            log.debug("通知开始....");
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
            // 转换微信post过来的xml内容
            String notifyData = CommonTools.inputStream2String(in); // 支付结果通知的xml格式数据
            //处理xml
            WxPayOrderNotifyResult notifyResult = wxPayService.parseOrderNotifyResult(notifyData);
            log.info("通知开始...." + notifyResult.getOutTradeNo());
            //验证签名
            notifyResult.checkResult(wxPayService, notifyResult.getSignType(), true);
            String outTradeNo = notifyResult.getOutTradeNo();
            String rechargeSn = outTradeNo.split("_")[0];//rechargeSn_payType，截取paySn
            String payType = outTradeNo.split("_")[1];
            MemberBalanceRecharge memberBalanceRecharge = this.getMemberBalanceRechargeBySn(rechargeSn);
            if (memberBalanceRecharge.getPayState().equals(MemberConst.PAY_STATE_2)){
                //已支付
                if (memberBalanceRecharge.getPaymentCode().contains(PayConst.METHOD_WX.toUpperCase()) &&
                        memberBalanceRecharge.getPaymentCode().contains(rechargeSn)){
                    //同一客户端重复回调，不做处理
                    sendMsg(response);
                    return;
                }
                //不同客户端重复支付，执行退款
                this.wxRefund(outTradeNo,payType,notifyResult.getTotalFee());
                sendMsg(response);
                return;
            }

            // 签名正确
            // 进行处理。
            // 注意特殊情况：订单已经退款，但收到了支付结果成功的通知，不应把商户侧订单状态从退款改成支付成功
            //安全校验(比较订单金额)
            BigDecimal totalFee = new BigDecimal(notifyResult.getTotalFee());
            totalFee = totalFee.divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
            if ("1".equals(stringRedisTemplate.opsForValue().get("wxpay_test_enable_h5"))) {// 1-开启测试, 0-关闭测试
                //不校验支付额
            } else {
                BigDecimal totalFeeDb = memberBalanceRecharge.getPayAmount();
                if (totalFeeDb.compareTo(totalFee) != 0) {
                    log.error("订单支付金额不一致paySn:" + notifyResult.getOutTradeNo());
                    response.getWriter().println("fail");
                    return;
                }
            }
            //更改订单状态
            boolean result = memberBalanceRechargeModel.payAfter(rechargeSn, notifyResult.getTransactionId());
            if (!result) {
                response.getWriter().println("fail");
            }

            sendMsg(response);

        } catch (Exception e) {
            log.error("微信回调失败",e);
        }
    }

    /**
     * 查询充值信息
     *
     * @param rechargeSn
     * @return
     */
    private MemberBalanceRecharge getMemberBalanceRechargeBySn(String rechargeSn){
        MemberBalanceRechargeExample example = new MemberBalanceRechargeExample();
        example.setRechargeSn(rechargeSn);
        List<MemberBalanceRecharge> list = memberBalanceRechargeModel.getMemberBalanceRechargeList(example, null);
        AssertUtil.notEmpty(list, "未生成充值订单");
        return list.get(0);
    }

    /**
     * 获取三方支付实际应支付金额
     *
     * @param paySn
     * @return
     */
    private BigDecimal getPayAmountDb(String paySn) {
        //查询充值明细
        return this.getMemberBalanceRechargeBySn(paySn).getPayAmount();
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
