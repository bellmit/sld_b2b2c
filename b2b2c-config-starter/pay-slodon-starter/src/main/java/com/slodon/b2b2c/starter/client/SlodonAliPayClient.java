package com.slodon.b2b2c.starter.client;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.*;
import com.alipay.api.request.*;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.exception.SLDException;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.starter.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import static com.slodon.b2b2c.core.constant.PayConst.*;

/**
 * 支付宝支付客户端
 */
@Slf4j
public class SlodonAliPayClient extends SlodonPayClient {

    /**
     * 支付宝client
     */
    private AlipayClient alipayClient;

    /**
     * 支付构造函数
     * @param payRequest
     */
    public SlodonAliPayClient(SlodonPayRequest payRequest) {
        super(payRequest);
        AliPayProperties alipayProperties = payRequest.getAlipayProperties();
        AssertUtil.notNull(alipayProperties,"支付宝支付参数配置无效");

        this.alipayClient = new DefaultAlipayClient(alipayProperties.url, alipayProperties.app_id,
                    alipayProperties.private_key, "json", alipayProperties.input_charset,
                    alipayProperties.public_key, alipayProperties.sign_type);

    }

    /**
     * 退款构造函数
     * @param refundRequest
     */
    public SlodonAliPayClient(SlodonRefundRequest refundRequest) {
        super(refundRequest);
        AliPayProperties alipayProperties = refundRequest.getAlipayProperties();
        AssertUtil.notNull(alipayProperties,"支付宝支付参数配置无效");

        this.alipayClient = new DefaultAlipayClient(alipayProperties.url, alipayProperties.app_id,
                alipayProperties.private_key, "json", alipayProperties.input_charset,
                alipayProperties.public_key, alipayProperties.sign_type);
    }

    /**
     * 执行支付宝支付
     *
     * @return
     * @throws Exception
     */
    @Override
    public SlodonPayResponse doPay() throws Exception {
        //查询是否已支付
        this.checkPayState(null,payRequest.getPaySn(),payRequest.getPayType());

        SlodonPayResponse response = new SlodonPayResponse();
        //建立请求
        String form;
        switch (payRequest.getPayType()) {
            case ALI_TYPE_MWEB:
                //h5支付
                response.setActionType("autopost");
                form = mwebPay();
                break;
            case ALI_TYPE_APP:
                //app支付
                response.setActionType(null);
                form = appPay();
                break;
            case ALI_TYPE_NATIVE:
                //扫码支付
                response.setActionType("autopost");
                form = nativePay();
                break;
            default:
                throw new MallException("支付方式有误");
        }

        response.setPayData(form);
        return response;
    }

    /**
     * h5支付
     * @return
     * @throws AlipayApiException
     */
    private String mwebPay() throws AlipayApiException {
        //支付参数
        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
        model.setOutTradeNo(payRequest.getPaySn());
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        model.setBody(payRequest.getBody());
        model.setTotalAmount(payRequest.getPayAmount().toString());
        model.setSubject(StringUtils.abbreviate(payRequest.getSubject().replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5 ]", StringUtils.EMPTY), 60));

        //创建API对应的支付请求
        AlipayTradeWapPayRequest wapPayRequest = new AlipayTradeWapPayRequest();
        wapPayRequest.setReturnUrl(payRequest.getReturnUrl());
        wapPayRequest.setNotifyUrl(payRequest.getNotifyUrl());
        wapPayRequest.setBizModel(model);

        return alipayClient.pageExecute(wapPayRequest).getBody();//调用SDK生成表单
    }

    /**
     * app支付
     * @return
     * @throws AlipayApiException
     */
    private String appPay() throws AlipayApiException {
        //支付参数
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setOutTradeNo(payRequest.getPaySn());
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        model.setBody(payRequest.getBody());
        model.setTotalAmount(payRequest.getPayAmount().toString());
        model.setSubject(StringUtils.abbreviate(payRequest.getSubject().replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5 ]", StringUtils.EMPTY), 60));

        //创建API对应的支付请求
        AlipayTradeAppPayRequest appPayRequest = new AlipayTradeAppPayRequest();
        appPayRequest.setReturnUrl(payRequest.getReturnUrl());
        appPayRequest.setNotifyUrl(payRequest.getNotifyUrl());
        appPayRequest.setBizModel(model);

        return alipayClient.sdkExecute(appPayRequest).getBody();//调用SDK生成表单
    }

    /**
     * 扫码支付
     * @return
     * @throws AlipayApiException
     */
    private String nativePay() throws AlipayApiException {
        //支付参数
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setOutTradeNo(payRequest.getPaySn());
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        model.setBody(payRequest.getBody());
        model.setTotalAmount(payRequest.getPayAmount().toString());
        model.setSubject(StringUtils.abbreviate(payRequest.getSubject().replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5 ]", StringUtils.EMPTY), 60));

        //创建API对应的支付请求
        AlipayTradePagePayRequest pagePayRequest = new AlipayTradePagePayRequest();
        pagePayRequest.setReturnUrl(payRequest.getReturnUrl());
        pagePayRequest.setNotifyUrl(payRequest.getNotifyUrl());
        pagePayRequest.setBizModel(model);

        return alipayClient.pageExecute(pagePayRequest).getBody();//调用SDK生成表单
    }


    /**
     * 检测支付状态
     *
     * @param tradeNo 三方支付流水号
     * @param outTradeNo 商城的支付单号
     * @return
     */
    public void checkPayState(String tradeNo, String outTradeNo,String payType) throws AlipayApiException {
        //查询支付状态
        AlipayTradeQueryResponse alipayTradeQueryResponse = this.queryOrder(tradeNo, outTradeNo);
        if (alipayTradeQueryResponse.isSuccess()){
            log.error("查询支付宝订单结果：" + alipayTradeQueryResponse.getTradeStatus());
            switch (alipayTradeQueryResponse.getTradeStatus()){
                case "TRADE_SUCCESS":
                    //已支付，等待ali通知
                    throw new MallException("订单已支付，无需重复支付");
                case "TRADE_FINISHED":
                    //交易完成
                    throw SLDException.INTERNAL_ERROR;
                case "WAIT_BUYER_PAY":
                    //待付款，关闭订单,可以继续支付
//                    AlipayTradeCloseResponse alipayTradeCloseResponse = this.closeOrder(tradeNo, outTradeNo);
//                    if (!alipayTradeCloseResponse.isSuccess()){
//                        log.error(alipayTradeCloseResponse.getSubCode() + ":" + alipayTradeCloseResponse.getSubMsg());
//                        throw new MallException(alipayTradeCloseResponse.getSubMsg());
//                    }
            }
        }else {
            //查询错误信息
            log.error(alipayTradeQueryResponse.getSubCode() + ":" + alipayTradeQueryResponse.getSubMsg());
        }
    }

    /**
     * 执行退款
     */
    @Override
    public SlodonRefundResponse doRefund() throws Exception {
        //构造业务参数
        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        model.setOutRequestNo(this.refundRequest.getRefundSn());
        model.setOutTradeNo(this.refundRequest.getPaySn());
        model.setRefundAmount(this.refundRequest.getRefundAmount().toString());
        AlipayTradeRefundRequest alipayTradeRefundRequest = new AlipayTradeRefundRequest();
        alipayTradeRefundRequest.setBizModel(model);
        //执行退款
        AlipayTradeRefundResponse alipayTradeRefundResponse = this.alipayClient.execute(alipayTradeRefundRequest);
        //构造返回数据
        SlodonRefundResponse refundResponse = new SlodonRefundResponse();
        refundResponse.setSuccess(alipayTradeRefundResponse.isSuccess());
        refundResponse.setMsg(alipayTradeRefundResponse.getSubMsg());
        return refundResponse;
    }

    /**
     * 查询订单
     * @param tradeNo 交易流水号，与商户订单号不能同时为空
     * @param outTradeNo 商户订单号
     * @return
     */
    private AlipayTradeQueryResponse queryOrder(String tradeNo, String outTradeNo) throws AlipayApiException {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        model.setOutTradeNo(outTradeNo);
        model.setTradeNo(tradeNo);
        request.setBizModel(model);
        return alipayClient.execute(request);
    }

    /**
     * 关闭订单
     * @param tradeNo 交易流水号，与商户订单号不能同时为空
     * @param outTradeNo 商户订单号
     * @return
     */
    private AlipayTradeCloseResponse closeOrder(String tradeNo, String outTradeNo) throws AlipayApiException {
        AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
        AlipayTradeCloseModel model = new AlipayTradeCloseModel();
        model.setOutTradeNo(outTradeNo);
        model.setTradeNo(tradeNo);
        request.setBizModel(model);
        return alipayClient.execute(request);
    }
}
