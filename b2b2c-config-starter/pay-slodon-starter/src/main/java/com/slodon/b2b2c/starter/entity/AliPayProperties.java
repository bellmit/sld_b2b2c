package com.slodon.b2b2c.starter.entity;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 支付宝支付配置信息
 */
@Data
@NoArgsConstructor
public class AliPayProperties {

    public AliPayProperties(String partner, String private_key, String public_key, String app_id) {
        this.partner = partner;
        this.private_key = private_key;
        this.public_key = public_key;
        this.app_id = app_id;
    }

    //网关地址
    public final String url = "https://openapi.alipay.com/gateway.do";
    // 合作身份者ID，以2088开头由16位纯数字组成的字符串
    public String partner;  //SettingManager.getSetting("alipay_mobile_partnerid");

    // 商户的私钥
    public String private_key;           // SettingManager.getSetting("alipay_mobile_private_key");

    //商户的公钥
    public String public_key;           // SettingManager.getSetting("alipay_mobile_public_key");

    // 商户的app_id
    public String app_id;           // SettingManager.getSetting("alipay_mobile_appid");
    //↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    // 调试用，创建TXT日志文件夹路径
    public String log_path = "/Users/java/Documents/alipay";

    // 字符编码格式 目前支持 gbk 或 utf-8
    public String input_charset = "utf-8";

    // 签名方式 不需修改
    public String sign_type = "RSA2";

}
