package com.slodon.b2b2c.util;

import com.slodon.b2b2c.starter.entity.AliPayProperties;
import com.slodon.b2b2c.starter.entity.WxPayProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.slodon.b2b2c.core.constant.PayConst.*;

/**
 * 支付配置构造工具
 */
@Component
public class PayPropertiesUtil {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 微信支付配置
     *
     * @param payType    支付类型
     * @param codeSource 1==小程序支付，2==公众号、微信内部浏览器
     * @return
     */
    public WxPayProperties getWxPayProperties(String payType, Integer codeSource) {
        WxPayProperties wxPayProperties = new WxPayProperties();
        switch (payType) {
            case WX_TYPE_MWEB:
                //h5支付
                wxPayProperties.setAppId(stringRedisTemplate.opsForValue().get("wxpay_h5_appid"));
                wxPayProperties.setMchId(stringRedisTemplate.opsForValue().get("wxpay_h5_merchantid"));
                wxPayProperties.setMchKey(stringRedisTemplate.opsForValue().get("wxpay_h5_apikey"));
                break;
            case WX_TYPE_APP:
                //app
                wxPayProperties.setAppId(stringRedisTemplate.opsForValue().get("wxpay_app_appid"));
                wxPayProperties.setMchId(stringRedisTemplate.opsForValue().get("wxpay_app_merchantid"));
                wxPayProperties.setMchKey(stringRedisTemplate.opsForValue().get("wxpay_app_apikey"));
                break;
            case WX_TYPE_JSAPI:
                if (codeSource == 1) {
                    //小程序支付
                    wxPayProperties.setAppId(stringRedisTemplate.opsForValue().get("wxpay_miniapp_appid"));
                    wxPayProperties.setMchId(stringRedisTemplate.opsForValue().get("wxpay_miniapp_merchantid"));
                    wxPayProperties.setMchKey(stringRedisTemplate.opsForValue().get("wxpay_miniapp_apikey"));
                } else {
                    //公众号支付
                    wxPayProperties.setAppId(stringRedisTemplate.opsForValue().get("wxpay_dev_appid"));
                    wxPayProperties.setMchId(stringRedisTemplate.opsForValue().get("wxpay_dev_merchantid"));
                    wxPayProperties.setMchKey(stringRedisTemplate.opsForValue().get("wxpay_dev_apikey"));
                }
                break;
            case WX_TYPE_NATIVE:
                //原生扫码支付，生成二维码所需的参数
                wxPayProperties.setAppId(stringRedisTemplate.opsForValue().get("wxpay_pc_appid"));
                wxPayProperties.setMchId(stringRedisTemplate.opsForValue().get("wxpay_pc_merchantid"));
                wxPayProperties.setMchKey(stringRedisTemplate.opsForValue().get("wxpay_pc_apikey"));
                break;
        }
        return wxPayProperties;
    }

    /**
     * 支付宝配置
     *
     * @return
     */
    public AliPayProperties getAliPayProperties() {
        return new AliPayProperties(stringRedisTemplate.opsForValue().get("alipay_config_partnerid"),
                stringRedisTemplate.opsForValue().get("alipay_config_private_key"),
                stringRedisTemplate.opsForValue().get("alipay_config_public_key"),
                stringRedisTemplate.opsForValue().get("alipay_config_appid"));
    }

}
