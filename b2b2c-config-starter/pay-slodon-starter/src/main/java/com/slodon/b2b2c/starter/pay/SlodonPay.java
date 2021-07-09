package com.slodon.b2b2c.starter.pay;

import com.slodon.b2b2c.core.constant.PayConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.starter.client.SlodonAliPayClient;
import com.slodon.b2b2c.starter.client.SlodonPayClient;
import com.slodon.b2b2c.starter.client.SlodonWxPayClient;
import com.slodon.b2b2c.starter.entity.*;

import java.math.BigDecimal;

import static com.slodon.b2b2c.core.constant.PayConst.METHOD_ALIPAY;
import static com.slodon.b2b2c.core.constant.PayConst.METHOD_WX;

/**
 * 三方支付统一入口类
 *
 * @author lxk
 */
public class SlodonPay {

    /**
     * 统一支付入口
     *
     * @param slodonPayRequest 统一支付封装参数
     * @return
     */
    public SlodonPayResponse unitePay(SlodonPayRequest slodonPayRequest) throws Exception {
        SlodonPayClient client;//支付客户端
        if (METHOD_WX.equalsIgnoreCase(slodonPayRequest.getPayMethod())) {
            //微信支付
            client = new SlodonWxPayClient(slodonPayRequest);
        } else if (METHOD_ALIPAY.equalsIgnoreCase(slodonPayRequest.getPayMethod())) {
            //支付宝支付
            client = new SlodonAliPayClient(slodonPayRequest);
        } else {
            throw new MallException("支付方式未开启");
        }
        //执行支付
        return client.doPay();
    }

    /**
     * 统一退款入口
     *
     * @param refundRequest 退款参数
     * @param paymentCode 支付编码 {@link com.slodon.b2b2c.core.constant.OrderPaymentConst#PAYMENT_CODE_PCWXPAY}
     * @return
     * @throws Exception
     */
    public SlodonRefundResponse refund(SlodonRefundRequest refundRequest,String paymentCode) throws Exception {
        SlodonPayClient client;//支付客户端
        if (paymentCode.toLowerCase().contains(METHOD_ALIPAY.toLowerCase())){
            //支付宝退款
            client = new SlodonAliPayClient(refundRequest);
        }else if (paymentCode.toLowerCase().contains(METHOD_WX.toLowerCase())){
            //微信支付时，支付单号格式为 paySn + '_' + 支付方式
            refundRequest.setPaySn(refundRequest.getPaySn() + "_" + paymentCode.replace(PayConst.METHOD_WX.toUpperCase(), ""));
            client = new SlodonWxPayClient(refundRequest);
        }else {
            throw new MallException("支付方式未开启");
        }
        return client.doRefund();
    }


    /**
     * 支付测试
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        WxPayProperties wxPayProperties = new WxPayProperties("wx9019a5f5fb0a0844","1484872062",
                "15556f6f736f74bb8fe6ca10ae491c4c","classpath:wx_cert/apiclient_cert.p12");
        AliPayProperties aliPayProperties = new AliPayProperties("2088721779427806",
                "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC2gb6hgNzXJBKeIPlSoPObGrjYTI+WxVsn4e0yFwDYrPYWi2cbNyexG0nRbMIHPjANI/BbqqAkZoep+DZKIiQgvdTlnnkvOGUhbbAHgO82PW9pnq9hIxPE1o+NZZt8sAa0ybiAVxINEmqKVkLMu4ET0hSJ3qweQkFs9PKhYEP/volB9IPUxzJ1a/zSylgS8Ek7oKgcU6sM9nI7LyazeDNfZaLRRLN+ge+gNfoIIkJjHmEIEkEIYup+2lnY6PWAGF7PQxciAn9Al5ZVkM+AjkdNeG+noMcJkzzK1Ji8tgLDMFUXlTNDKt4r2JRJuRRxEhtq3uu/wor2mus9hWhl4q0vAgMBAAECggEBAK+clqbmzRlC/5silWWZs+46P0T6HmxwSK9OqKM7GThk7YJx7ut0HgBABr66TNUVVL1AKKa7vs5nzgCR8wNQaL0MjI/J9keml0EcP3Zo7USexta1t4Hbc8lCv+zJqVzIMBvDwoHTxm3Hm7azHCHOBnzg95xWI/4VuMizxcfcTnIE8CgmgoMLI91oeFBIZBl7qPFGMsL1c4NUft3duAJP3mcJjPJ7MTxwMAdWJ4pnrkBRJbBJKz66zyJkZF2Aepfki8UcN3pTLpmMw6w6VRYfkmBcbNyI9CMO3Wk1hamOVKeIsOurnwku73lVwSv3vses/1RmSG9LqUoJNKFekkrs0lECgYEA2UsO7Lu++FYr/vr09fIROQSn4nMvjMtxca0rmd+u+bYlz17A8GOlUeV6H4HRorDWYJa59RPwgnEXYKLGd+9LnnGc/7JAG+zyV7Qh0d5V28GQOJ7GRxs5JsHOYb2bwsJJLMx0SLrYTXUo7JgpdjpVHBtgDSLYlYU6fzt2OOo5VkcCgYEA1wReKRsUXptwT0I0DxD0bEtBvyPyLfrA075r+Hyad+cXxj7ZHIrpfbzqx1ENdr1QQ05YDQxeow8El90VtvRCLPTX2OJ3uY+fwEwNhwxIKTrThswIrsEF2B5XX8U5s+BKhXzDPB5JceFRmNYz9EM8CHvIbOoaHW5IAD2XiyApndkCgYB5yo0x+UVfsgT05Lp6Q9SNbeOflEo5kyohuhsvIuXTBtoIciaYVnGqBoI9uKjElHOELddK24M5yaN9wZ6WSKTMyQdjdghi3psytz8hf4oGI2bvpWajIkoVRBmmRP1R203iczJWNYvZVmq6ucq7fpSHrXxXwIk+vS79c1mfKwE6LwKBgEOpNTQ8OEG5KjNWwukGzdOSLwNF/XyojqtTHcjY9QbMKrgQqOOJdwZBP6QvBkyaMDqt2ZlpjhJDtI9RW/bxkC1zcsTmJwS2hG/PvuYDYGXRBCVD6WuMhBlJGvehKZJH/0tYQluVsEjsvW+Z4El6KtcElMJpPISMYo3o09pM6aupAoGBAJ6LDMouPOn+Lbucft35nEfqE1pDBztR/PhSjUA8S7RS/M7XB7iRQITo9haPmdMKwGG/JlB1zOIBLb3RMM2+lk+tBlrCJ0PUs36EVS0CoGP3BAsHTIJ01T3EIfCr9vfMGT5bic0AkSHmpVck1CUQCJ1wLUqd/8WeZhydntBCX2wP",
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsSah6GBhd7JM7zaot3OlaGm2W38xTsywQpyh3XjijOnjlbxyoSvXRMix4/VEKqhBlRRRh7L+f1Csqn2oZB6H19juie0ER93l/zjpEs+bMcfB3QE5qeV62RTCswo7LEGY8XQZx1DysN1U/O5nvgG5yzIuVGk+08LTYaISqV1s5pD3/kFk/rrbIRmUVpr3leb/sRMpyGIxM+tslV/oRvsjW59RR4nkkPVWHVXddQ15VgRSmBPDPPF2ALDFQt7KTDaE9nKJ6IlnVhDenKANT1FTj7KRBJLRFZyEN8YkuQssu4C4naUQrdCFFUEIkyTeHeUbtWhlY9Depp9BX1LzxYzykQIDAQAB",
                "2017112900241969");
        SlodonRefundRequest refundRequest = new SlodonRefundRequest();
        refundRequest.setAlipayProperties(aliPayProperties);
        refundRequest.setWxPayProperties(wxPayProperties);
        refundRequest.setPaySn("400000470002");
        refundRequest.setRefundAmount(new BigDecimal("0.01"));
        refundRequest.setRefundSn("22222");
        refundRequest.setTotalAmount(new BigDecimal("0.01"));

        SlodonPay slodonPay = new SlodonPay();
        SlodonRefundResponse slodonPayResponse = slodonPay.refund(refundRequest, "MWEBALIPAY");
        System.out.println(slodonPayResponse);
    }
}
