package com.slodon.b2b2c.controller.seller.seller;

import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.seller.StoreApplyModel;
import com.slodon.b2b2c.model.seller.StoreRenewModel;
import com.slodon.b2b2c.seller.example.StoreApplyExample;
import com.slodon.b2b2c.seller.example.StoreRenewExample;
import com.slodon.b2b2c.seller.pojo.StoreApply;
import com.slodon.b2b2c.seller.pojo.StoreRenew;
import com.slodon.b2b2c.starter.entity.AliPayProperties;
import com.slodon.b2b2c.starter.entity.SlodonPayRequest;
import com.slodon.b2b2c.starter.pay.SlodonPay;
import com.slodon.b2b2c.util.PayPropertiesUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

import static com.slodon.b2b2c.core.constant.PayConst.*;

@Api(tags = "seller-付款接口")
@RestController
@RequestMapping("v3/seller/seller/pay")
public class PayController extends BaseController {

    private final static String PAY_SUCCESS_URL = "/base/#/apply/open_up";
    private final static String PAY_SUCCESS_URL_RENEW = "/base/#/store/info";

    @Resource
    private StoreRenewModel storeRenewModel;
    @Resource
    private StoreApplyModel storeApplyModel;

    @Resource
    private SlodonPay slodonPay;
    @Resource
    private PayPropertiesUtil payPropertiesUtil;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("商户续签支付")
    @VendorLogger(option = "商户续签支付")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "payMethod", value = "支付方式,三方支付时必传  wx-微信支付，alipay-支付宝支付", required = true),
            @ApiImplicitParam(name = "paySn", value = "支付单号", required = true)
    })
    @PostMapping("renewPay")
    public JsonResult renewPay(HttpServletRequest request, String payMethod, String paySn) throws Exception {
        //统一支付功能
        AliPayProperties alipayProperties = new AliPayProperties();
        String partnerid = stringRedisTemplate.opsForValue().get("alipay_config_partnerid");
        alipayProperties.setPartner(partnerid);
        String appid = stringRedisTemplate.opsForValue().get("alipay_config_appid");
        alipayProperties.setApp_id(appid);
        String privateKey = stringRedisTemplate.opsForValue().get("alipay_config_private_key");
        alipayProperties.setPrivate_key(privateKey);
        String publicKey = stringRedisTemplate.opsForValue().get("alipay_config_public_key");
        alipayProperties.setPublic_key(publicKey);

        //获取付款金额
        StoreRenewExample storeRenewExample = new StoreRenewExample();
        storeRenewExample.setPaySn(paySn);
        StoreRenew storeRenew = storeRenewModel.getStoreRenewList(storeRenewExample, null).get(0);
        BigDecimal payAmount = storeRenew.getPayAmount();

        //实际支付金额
        BigDecimal payMoney = BigDecimal.ZERO; //支付金额（分）
        if (METHOD_WX.equalsIgnoreCase(payMethod)) {
            if ("1".equals(stringRedisTemplate.opsForValue().get("wxpay_is_enable_h5"))) {// 1-开启测试, 0-关闭测试
                payMoney = new BigDecimal("1");
            } else {
                payMoney = StringUtil.isNullOrZero(payAmount) ? new BigDecimal("1") : payAmount.multiply(new BigDecimal(100));
            }
        } else if (METHOD_ALIPAY.equalsIgnoreCase(payMethod)) {
            if ("1".equals(stringRedisTemplate.opsForValue().get("alipay_is_enable_h5"))) {  // 1-开启测试, 0-关闭测试
                payMoney = new BigDecimal(stringRedisTemplate.opsForValue().get("alipay_test_total_fee"));
            } else {
                payMoney = payAmount;
            }
        }

        //构造三方支付请求参数
        SlodonPayRequest payRequest = this.getPayRequest(payMethod, paySn, payMoney, WebUtil.getRealIp(request), "renew");

        return SldResponse.success(slodonPay.unitePay(payRequest));
    }

    @ApiOperation("商户入驻支付")
    @VendorLogger(option = "商户入驻支付")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "payMethod", value = "支付方式,三方支付时必传  wx-微信支付，alipay-支付宝支付", required = true),
            @ApiImplicitParam(name = "paySn", value = "支付单号", required = true)
    })
    @PostMapping("registerPay")
    public JsonResult registerPay(HttpServletRequest request, String payMethod, String paySn) throws Exception {
        //统一支付功能
        AliPayProperties alipayProperties = new AliPayProperties();
        String partnerid = stringRedisTemplate.opsForValue().get("alipay_config_partnerid");
        alipayProperties.setPartner(partnerid);
        String appid = stringRedisTemplate.opsForValue().get("alipay_config_appid");
        alipayProperties.setApp_id(appid);
        String privateKey = stringRedisTemplate.opsForValue().get("alipay_config_private_key");
        alipayProperties.setPrivate_key(privateKey);
        String publicKey = stringRedisTemplate.opsForValue().get("alipay_config_public_key");
        alipayProperties.setPublic_key(publicKey);

        //获取付款金额
        StoreApplyExample storeApplyExample = new StoreApplyExample();
        storeApplyExample.setPaySn(paySn);
        StoreApply storeApply = storeApplyModel.getStoreApplyList(storeApplyExample, null).get(0);
        BigDecimal payAmount = storeApply.getPayAmount();

        //实际支付金额
        BigDecimal payMoney = BigDecimal.ZERO; //支付金额（分）
        if (METHOD_WX.equalsIgnoreCase(payMethod)) {
            if ("1".equals(stringRedisTemplate.opsForValue().get("wxpay_is_enable_h5"))) {// 1-开启测试, 0-关闭测试
                payMoney = new BigDecimal("1");
            } else {
                payMoney = StringUtil.isNullOrZero(payAmount) ? new BigDecimal("1") : payAmount.multiply(new BigDecimal(100));
            }
        } else if (METHOD_ALIPAY.equalsIgnoreCase(payMethod)) {
            if ("1".equals(stringRedisTemplate.opsForValue().get("alipay_is_enable_h5"))) {  // 1-开启测试, 0-关闭测试
                payMoney = new BigDecimal(stringRedisTemplate.opsForValue().get("alipay_test_total_fee"));
            } else {
                payMoney = payAmount;
            }
        }
        //构造三方支付请求参数
        SlodonPayRequest payRequest = this.getPayRequest(payMethod, paySn, payMoney, WebUtil.getRealIp(request), "register");

        return SldResponse.success(slodonPay.unitePay(payRequest));
    }

    /**
     * 构造支付请求对象
     *
     * @param payMethod 支付方式
     * @param paySn     支付单号
     * @param payAmount 支付金额
     * @param ip        ip
     * @return 支付请求对象
     */
    private SlodonPayRequest getPayRequest(String payMethod, String paySn, BigDecimal payAmount, String ip, String type) {
        SlodonPayRequest request = new SlodonPayRequest();
        AssertUtil.isTrue(!payMethod.equals(METHOD_WX) && !payMethod.equals(METHOD_ALIPAY), "支付方式有误");
        request.setPayMethod(payMethod);
        if (payMethod.equals(METHOD_WX)) {
            request.setWxPayProperties(payPropertiesUtil.getWxPayProperties(WX_TYPE_NATIVE,1));
            if ("renew".equals(type)) {
                request.setNotifyUrl(DomainUrlUtil.SLD_API_URL + "/v3/seller/seller/renewPayCallback/wx");
            } else if ("register".equals(type)) {
                request.setNotifyUrl(DomainUrlUtil.SLD_API_URL + "/v3/seller/seller/registerPayCallback/wx");
            }
            request.setBody(stringRedisTemplate.opsForValue().get("wxpay_order_name"));
            request.setPayType(WX_TYPE_NATIVE);
        } else {
            request.setAlipayProperties(payPropertiesUtil.getAliPayProperties());
            //todo 支付宝支付返回地址待修改
            if ("renew".equals(type)) {
                request.setNotifyUrl(DomainUrlUtil.SLD_API_URL + "/v3/seller/seller/renewPayCallback/ali");
                request.setReturnUrl(DomainUrlUtil.SLD_SELLER_URL + PAY_SUCCESS_URL_RENEW);
            } else if ("register".equals(type)) {
                request.setNotifyUrl(DomainUrlUtil.SLD_API_URL + "/v3/seller/seller/registerPayCallback/ali");
                request.setReturnUrl(DomainUrlUtil.SLD_SELLER_URL + PAY_SUCCESS_URL);
            }
            request.setSubject(stringRedisTemplate.opsForValue().get("alipay_all_subject"));
            request.setBody(request.getSubject());
            request.setPayType(ALI_TYPE_NATIVE);
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
}
