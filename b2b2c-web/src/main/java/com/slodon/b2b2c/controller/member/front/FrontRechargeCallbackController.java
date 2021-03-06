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

@Api(tags = "front-εεΌεθ°")
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

    // εΎ?δΏ‘θΏε  fail ε€±θ΄₯οΌsuccess ζε
    private static final String RETURN_CODE_SUCCESS = "SUCCESS";
    private static final String RETURN_MSG_OK = "OK";
    private static final String RETURN_CODE_FAIL = "FAIL";

    /**
     * ζ―δ»ε?εεΌεθ°
     *
     * @param request
     * @param response
     */
    @PostMapping("ali")
    public void alipayNotify(HttpServletRequest request, HttpServletResponse response) {

        try {
            //θ·εζ―δ»ε?POSTθΏζ₯ει¦δΏ‘ζ―
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
            //θ°η¨SDK,ιͺθ―η­Ύε
            AliPayProperties aliPayProperties = payPropertiesUtil.getAliPayProperties();
            boolean signVerified = AlipaySignature.rsaCheckV1(params, aliPayProperties.public_key, aliPayProperties.input_charset, aliPayProperties.sign_type);
            if (signVerified) {//ιͺθ―ζε
                // TODO ιͺη­ΎζεεοΌζη§ζ―δ»η»ζεΌζ­₯ιη₯δΈ­ηζθΏ°οΌε―Ήζ―δ»η»ζδΈ­ηδΈε‘εε?ΉθΏθ‘δΊζ¬‘ζ ‘ιͺοΌζ ‘ιͺζεεε¨responseδΈ­θΏεsuccessεΉΆη»§η»­εζ·θͺθΊ«δΈε‘ε€ηοΌζ ‘ιͺε€±θ΄₯θΏεfailure
                //θ·εζ―δ»ε?ηιη₯θΏεεζ°οΌε―εθζζ―ζζ‘£δΈ­ι‘΅ι’θ·³θ½¬εΌζ­₯ιη₯εζ°εθ‘¨(δ»₯δΈδ»δΎεθ)//
                //εζ·θ?’εε·
                String out_trade_no = params.get("out_trade_no");
                //ζ―δ»ε?δΊ€ζε·
                String trade_no = params.get("trade_no");
                //δΊ€ζιι’
                String total_amount = params.get("total_amount");
                //δΊ€ζηΆζ
                String trade_status = params.get("trade_status");
                //ββθ―·ζ Ήζ?ζ¨ηδΈε‘ι»θΎζ₯ηΌεη¨εΊοΌδ»₯δΈδ»£η δ»δ½εθοΌββ
                if (trade_status.equals("TRADE_FINISHED")) {
                    log.debug("θ?’εε·²ε?ζ");
                    response.getWriter().println("success"); //θ―·δΈθ¦δΏ?ζΉζε ι€
                } else if (trade_status.equals("TRADE_SUCCESS")) {
                    log.debug("ζ―δ»ζε");

                    //ε€ζ­θ―₯η¬θ?’εζ―ε¦ε¨εζ·η½η«δΈ­ε·²η»εθΏε€η
                    MemberBalanceRecharge memberBalanceRecharge = this.getMemberBalanceRechargeBySn(out_trade_no);
                    if (memberBalanceRecharge.getPayState().equals(MemberConst.PAY_STATE_2)){
                        //θ?’εε·²ζ―δ»οΌζ§θ‘ιζ¬Ύζδ½
                        if (memberBalanceRecharge.getPaymentCode().contains(PayConst.METHOD_ALIPAY.toUpperCase())){
                            //ιε€εθ°οΌδΈε€ηοΌη΄ζ₯θΏεζε
                            response.getWriter().println("success");
                            return;
                        }
                        //εΆδ»ζΉεΌε·²ζ―δ»οΌε¦δ½ι’γεΎ?δΏ‘οΌζ§θ‘ιζ¬Ύ
                        this.aliRefund(out_trade_no,new BigDecimal(total_amount));
                        response.getWriter().println("success");
                        return;
                    }

                    if ("1".equals(stringRedisTemplate.opsForValue().get("alipay_test_enable_h5"))) {// 1-εΌε―ζ΅θ―, 0-ε³ι­ζ΅θ―
                        //δΈζ ‘ιͺζ―δ»ι’
                    } else {
                        //θ·εεεΌθ?’ε
                        if (!total_amount.equals(this.getPayAmountDb(out_trade_no).toString())) {
                            log.error("θ?’εζ―δ»ιι’δΈδΈθ΄paySn:" + out_trade_no);
                            response.getWriter().println("fail");
                            //θ?’ειι’δΈδΈθ΄δΈεΎδΈζ§θ‘
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
            log.error("ζ―δ»ε?εθ°ε€±θ΄₯",e);
            try {
                response.getWriter().println("fail");
            } catch (IOException e1) {
                log.error("ζ―δ»ε?εθ°ε€±θ΄₯",e1);
            }
        }
    }

    /**
     * εΎ?δΏ‘εεΌεθ°
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/wx", method = {RequestMethod.GET, RequestMethod.POST})
    public void wxNotify(HttpServletRequest request, HttpServletResponse response) {
        ServletInputStream in;

        try {
            log.debug("ιη₯εΌε§....");
            //εΎ?δΏ‘ζ―δ»ιη½?
            WxPayProperties wxPayProperties = payPropertiesUtil.getWxPayProperties(WX_TYPE_NATIVE, 1);
            WxPayServiceImpl wxPayService = new WxPayServiceImpl();
            WxPayConfig wxPayConfig = new WxPayConfig();
            wxPayConfig.setAppId(StringUtils.trimToNull(wxPayProperties.getAppId()));
            wxPayConfig.setMchId(StringUtils.trimToNull(wxPayProperties.getMchId()));
            wxPayConfig.setMchKey(StringUtils.trimToNull(wxPayProperties.getMchKey()));
            wxPayService.setConfig(wxPayConfig);
            // post θΏζ₯ηxml
            in = request.getInputStream();
            // θ½¬ζ’εΎ?δΏ‘postθΏζ₯ηxmlεε?Ή
            String notifyData = CommonTools.inputStream2String(in); // ζ―δ»η»ζιη₯ηxmlζ ΌεΌζ°ζ?
            //ε€ηxml
            WxPayOrderNotifyResult notifyResult = wxPayService.parseOrderNotifyResult(notifyData);
            log.info("ιη₯εΌε§...." + notifyResult.getOutTradeNo());
            //ιͺθ―η­Ύε
            notifyResult.checkResult(wxPayService, notifyResult.getSignType(), true);
            String outTradeNo = notifyResult.getOutTradeNo();
            String rechargeSn = outTradeNo.split("_")[0];//rechargeSn_payTypeοΌζͺεpaySn
            String payType = outTradeNo.split("_")[1];
            MemberBalanceRecharge memberBalanceRecharge = this.getMemberBalanceRechargeBySn(rechargeSn);
            if (memberBalanceRecharge.getPayState().equals(MemberConst.PAY_STATE_2)){
                //ε·²ζ―δ»
                if (memberBalanceRecharge.getPaymentCode().contains(PayConst.METHOD_WX.toUpperCase()) &&
                        memberBalanceRecharge.getPaymentCode().contains(rechargeSn)){
                    //εδΈε?’ζ·η«―ιε€εθ°οΌδΈεε€η
                    sendMsg(response);
                    return;
                }
                //δΈεε?’ζ·η«―ιε€ζ―δ»οΌζ§θ‘ιζ¬Ύ
                this.wxRefund(outTradeNo,payType,notifyResult.getTotalFee());
                sendMsg(response);
                return;
            }

            // η­Ύεζ­£η‘?
            // θΏθ‘ε€ηγ
            // ζ³¨ζηΉζ?ζε΅οΌθ?’εε·²η»ιζ¬ΎοΌδ½ζΆε°δΊζ―δ»η»ζζεηιη₯οΌδΈεΊζεζ·δΎ§θ?’εηΆζδ»ιζ¬ΎζΉζζ―δ»ζε
            //ε?ε¨ζ ‘ιͺ(ζ―θΎθ?’ειι’)
            BigDecimal totalFee = new BigDecimal(notifyResult.getTotalFee());
            totalFee = totalFee.divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
            if ("1".equals(stringRedisTemplate.opsForValue().get("wxpay_test_enable_h5"))) {// 1-εΌε―ζ΅θ―, 0-ε³ι­ζ΅θ―
                //δΈζ ‘ιͺζ―δ»ι’
            } else {
                BigDecimal totalFeeDb = memberBalanceRecharge.getPayAmount();
                if (totalFeeDb.compareTo(totalFee) != 0) {
                    log.error("θ?’εζ―δ»ιι’δΈδΈθ΄paySn:" + notifyResult.getOutTradeNo());
                    response.getWriter().println("fail");
                    return;
                }
            }
            //ζ΄ζΉθ?’εηΆζ
            boolean result = memberBalanceRechargeModel.payAfter(rechargeSn, notifyResult.getTransactionId());
            if (!result) {
                response.getWriter().println("fail");
            }

            sendMsg(response);

        } catch (Exception e) {
            log.error("εΎ?δΏ‘εθ°ε€±θ΄₯",e);
        }
    }

    /**
     * ζ₯θ―’εεΌδΏ‘ζ―
     *
     * @param rechargeSn
     * @return
     */
    private MemberBalanceRecharge getMemberBalanceRechargeBySn(String rechargeSn){
        MemberBalanceRechargeExample example = new MemberBalanceRechargeExample();
        example.setRechargeSn(rechargeSn);
        List<MemberBalanceRecharge> list = memberBalanceRechargeModel.getMemberBalanceRechargeList(example, null);
        AssertUtil.notEmpty(list, "ζͺηζεεΌθ?’ε");
        return list.get(0);
    }

    /**
     * θ·εδΈζΉζ―δ»ε?ιεΊζ―δ»ιι’
     *
     * @param paySn
     * @return
     */
    private BigDecimal getPayAmountDb(String paySn) {
        //ζ₯θ―’εεΌζη»
        return this.getMemberBalanceRechargeBySn(paySn).getPayAmount();
    }

    /**
     * η»εΎ?δΏ‘εεεΊη­ζ₯ζοΌι²ζ­’ιε€εθ°
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
     * ζ§θ‘εΎ?δΏ‘ιζ¬Ύ
     * @param outTradeNo εζ·ζ―δ»εε·
     * @param payType ζ―δ»η±»ε
     * @param totalFee ζ―δ»ιι’οΌεδ½ε
     */
    private void wxRefund(String outTradeNo, String payType, Integer totalFee){
        SlodonRefundRequest refundRequest = new SlodonRefundRequest();

        //εΎ?δΏ‘ιζ¬Ύ
        refundRequest.setWxPayProperties(payPropertiesUtil.getWxPayProperties(payType, 1));
        //εΎ?δΏ‘ιθ¦θ?Ύη½?εζ―δ»ιι’
        BigDecimal payAmount = new BigDecimal(totalFee).divide(new BigDecimal(100),2, RoundingMode.HALF_UP);
        refundRequest.setTotalAmount(payAmount);
        refundRequest.setRefundAmount(payAmount);
        refundRequest.setPaySn(outTradeNo);
        refundRequest.setRefundSn(outTradeNo);
        try {
            SlodonRefundResponse refundResponse = slodonPay.refund(refundRequest, PayConst.METHOD_WX.toUpperCase());
            if (!refundResponse.getSuccess()){
                log.error("ιε€ζ―δ»ιζ¬Ύε€±θ΄₯οΌεζ·ε·οΌ{}οΌε€±θ΄₯εε οΌ{}",outTradeNo,refundResponse.getMsg());
            }
        } catch (Exception e) {
            log.error("ιε€ζ―δ»ιζ¬Ύε€±θ΄₯οΌεζ·ε·οΌ{}",outTradeNo,e);
        }
    }

    /**
     * ζ―δ»ε?ιζ¬Ύ
     * @param outTradeNo εζ·ζ―δ»εε·
     * @param refundAmount ιζ¬Ύιι’
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
                log.error("ζ―δ»ε?ιε€ζ―δ»ιζ¬Ύε€±θ΄₯οΌεζ·ε·οΌ{}οΌε€±θ΄₯εε οΌ{}",outTradeNo,refundResponse.getMsg());
            }
        } catch (Exception e) {
            log.error("ζ―δ»ε?ιε€ζ―δ»ιζ¬Ύε€±θ΄₯οΌεζ·ε·οΌ{}",outTradeNo,e);
        }
    }
}
