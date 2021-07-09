package com.slodon.b2b2c.starter.client;

import com.github.binarywang.wxpay.bean.order.WxPayAppOrderResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.order.WxPayMwebOrderResult;
import com.github.binarywang.wxpay.bean.order.WxPayNativeOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayOrderReverseRequest;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.starter.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.slodon.b2b2c.core.constant.PayConst.*;

/**
 * 微信支付客户端
 */
@Slf4j
public class SlodonWxPayClient extends SlodonPayClient{

    /**
     * 支付服务类
     */
    private WxPayService wxPayService = new WxPayServiceImpl();


    /**
     * 支付构造函数
     * @param request
     */
    public SlodonWxPayClient(SlodonPayRequest request) {
        super(request);
        WxPayProperties wxPayProperties = request.getWxPayProperties();
        AssertUtil.notNull(wxPayProperties,"微信支付参数配置无效");

        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(StringUtils.trimToNull(wxPayProperties.getAppId()));
        payConfig.setMchId(StringUtils.trimToNull(wxPayProperties.getMchId()));
        payConfig.setMchKey(StringUtils.trimToNull(wxPayProperties.getMchKey()));
        payConfig.setKeyPath(StringUtils.trimToNull(wxPayProperties.getKeyPath()));
        // 可以指定是否使用沙箱环境
        payConfig.setUseSandboxEnv(false);

        this.wxPayService.setConfig(payConfig);
    }

    /**
     * 退款构造函数
     * @param refundRequest
     */
    public SlodonWxPayClient(SlodonRefundRequest refundRequest) {
        super(refundRequest);
        WxPayProperties wxPayProperties = refundRequest.getWxPayProperties();
        AssertUtil.notNull(wxPayProperties,"微信支付参数配置无效");

        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(StringUtils.trimToNull(wxPayProperties.getAppId()));
        payConfig.setMchId(StringUtils.trimToNull(wxPayProperties.getMchId()));
        payConfig.setMchKey(StringUtils.trimToNull(wxPayProperties.getMchKey()));
        payConfig.setKeyPath(StringUtils.trimToNull(wxPayProperties.getKeyPath()));
        // 可以指定是否使用沙箱环境
        payConfig.setUseSandboxEnv(false);

        this.wxPayService.setConfig(payConfig);
    }

    /**
     * 微信支付
     * @return
     * @throws Exception
     */
    public SlodonPayResponse doPay() throws Exception {
        String outTradeNo = this.payRequest.getPaySn() + "_" + this.payRequest.getPayType();//支付单号拼接支付类型，作为微信支付的商户订单号，回调时要截取支付单号
        //查询支付状态
        this.checkPayState(null,outTradeNo,payRequest.getPayType());

        SlodonPayResponse response = new SlodonPayResponse();

        //将支付金额转为分
        Integer totalFee = payRequest.getPayAmount().multiply(new BigDecimal(100)).intValue();

        WxPayUnifiedOrderRequest unifiedOrderRequest = new WxPayUnifiedOrderRequest();
        unifiedOrderRequest.setBody(this.payRequest.getBody());
        unifiedOrderRequest.setOutTradeNo(outTradeNo);
        unifiedOrderRequest.setTotalFee(totalFee);
        unifiedOrderRequest.setSpbillCreateIp(this.payRequest.getIp());
        unifiedOrderRequest.setNotifyUrl(this.payRequest.getNotifyUrl());
        unifiedOrderRequest.setTradeType(this.payRequest.getPayType());
        //订单起止时间设置
        Date timeStart = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timeStart);
        //设置订单过期时间为5分钟
        calendar.add(Calendar.MINUTE, 5);
        Date timeExpire = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        unifiedOrderRequest.setTimeStart(format.format(timeStart));
        unifiedOrderRequest.setTimeExpire(format.format(timeExpire));
        //处理几种支付类型所需的特殊参数
        switch (this.payRequest.getPayType()) {
            case WX_TYPE_JSAPI:
                //公众号支付/小程序支付，获取openid
                unifiedOrderRequest.setOpenid(this.payRequest.getOpenId());
                break;
            case WX_TYPE_MWEB:
                //h5支付，拼接场景信息
                String scene_info = "{\"h5_info\":{\"type\": \"Wap\",\"wap_url\":" + DomainUrlUtil.SLD_API_URL + ",\"wap_name\": \"linePay\"}}";
                unifiedOrderRequest.setSceneInfo(scene_info);
                break;
            case WX_TYPE_NATIVE:
                //原生扫码支付，配置product_id
                unifiedOrderRequest.setProductId(this.payRequest.getPaySn());
                break;
        }

        //订单支付
        Object o = this.wxPayService.createOrder(unifiedOrderRequest);

        //跳转处理
        switch (this.payRequest.getPayType()) {
            case WX_TYPE_MWEB:
                //h5支付
                WxPayMwebOrderResult mwebResult = (WxPayMwebOrderResult) o;
                String mwebUrl = mwebResult.getMwebUrl();
                String encode = URLEncoder.encode(this.payRequest.getReturnUrl(), "GBK");//返回地址
                response.setActionType("redirect");
                response.setPayData(mwebUrl + "&redirect_url=" + encode);
                break;
            case WX_TYPE_APP:
                //app
                WxPayAppOrderResult appResult = (WxPayAppOrderResult) o;
                response.setActionType(null);
                response.setPayData(appResult);
                break;
            case WX_TYPE_JSAPI:
                //公众号支付/小程序支付,生成签名
                WxPayMpOrderResult jsResult = (WxPayMpOrderResult) o;
                response.setActionType(null);
                response.setPayData(jsResult);
                break;
            case WX_TYPE_NATIVE:
                //原生扫码支付，生成二维码所需的参数
                WxPayNativeOrderResult nativeOrderResult = (WxPayNativeOrderResult) o;
                response.setActionType("native");
                response.setPayData(this.paySuccess(nativeOrderResult.getCodeUrl()));
                break;
        }

        return response;
    }

    /**
     * 检测支付状态
     *
     * @param tradeNo 三方支付流水号
     * @param outTradeNo 商城的支付单号
     * @return
     */
    @Override
    public void checkPayState(String tradeNo, String outTradeNo,String payType) throws Exception {
        WxPayOrderQueryResult result = null;
        try {
            result = this.wxPayService.queryOrder(tradeNo, outTradeNo);
        }catch (Exception e){
            //微信侧未创建订单，可以继续支付
        }

        if (result != null){
            log.error("查询微信订单结果：" + result.getTradeState());
            //说明在微信侧已经创建了订单
            switch (result.getTradeState()){
                case "SUCCESS":
                    //已支付，等待wx通知
                    throw new MallException("订单已支付，无需重复支付");
                case "USERPAYING":
                    //用户支付中
                    throw new MallException("正在支付中，请稍后重试");
                case "REFUND":
                    //转入退款，此订单为重复支付的订单
                    throw new MallException("订单无法支付,请联系管理员");
                case "NOTPAY":
                case "PAYERROR":
                    break;
                default:
                    //如果需要，执行关闭订单操作
            }
        }
    }

    /**
     * 执行退款
     */
    @Override
    public SlodonRefundResponse doRefund() throws Exception {
        //将支付金额转为分
        Integer refundFee = this.refundRequest.getRefundAmount().multiply(new BigDecimal(100)).intValue();
        Integer totalFee = this.refundRequest.getTotalAmount().multiply(new BigDecimal(100)).intValue();

        //构造业务参数
        WxPayRefundRequest refundRequest = new WxPayRefundRequest();
        refundRequest.setOutTradeNo(this.refundRequest.getPaySn());
        refundRequest.setOutRefundNo(this.refundRequest.getRefundSn());
        refundRequest.setRefundFee(refundFee);
        refundRequest.setTotalFee(totalFee);
        //执行退款
        WxPayRefundResult wxPayRefundResult = this.wxPayService.refund(refundRequest);
        SlodonRefundResponse refundResponse = new SlodonRefundResponse();
        refundResponse.setSuccess("SUCCESS".equalsIgnoreCase(wxPayRefundResult.getResultCode()));
        refundResponse.setMsg(wxPayRefundResult.getErrCodeDes());
        return refundResponse;
    }


    /**
     * 生成二维码并转为base64格式输出
     * @param codeUrl 二维码链接
     * @return
     * @throws IOException
     * @throws WriterException
     */
    private String paySuccess(String codeUrl) throws IOException, WriterException {
        //将图片转化成base64进行输出
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Map<EncodeHintType, String> hints = new HashMap<>();
        // 内容所使用编码
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix bitMatrix = new MultiFormatWriter().encode(codeUrl, BarcodeFormat.QR_CODE, 300,
                300, hints);
        // 生成二维码
        String codeImg = "";
        MatrixToImageWriter.writeToStream(bitMatrix, "jpg", baos);
        codeImg = new String(Base64.encodeBase64(baos.toByteArray()));
        return codeImg;
    }
}
