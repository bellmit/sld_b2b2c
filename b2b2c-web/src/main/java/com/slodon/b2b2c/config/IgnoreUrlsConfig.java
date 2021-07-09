package com.slodon.b2b2c.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 白名单配置
 * 不同白名单需要做不同的请求头处理
 */
@Data
@Component
@ConfigurationProperties(prefix = "secure.ignore")
public class IgnoreUrlsConfig {

    /**
     * 不需要验证登录状态的url（不包含登录接口）
     */
    private List<String> commonUrls;

    /**
     * 登录接口，
     * 认证之前过滤器：不处理
     */
    private List<String> loginUrls;

    /**
     * 商城端不需要登录就可以访问的url
     * 认证之前过滤器：请求头中有token，不处理；请求头中无token，清除 Authorization 请求头
     * 全局过滤器：请求头中有token，登录&权限认证；请求头中无token，放行
     */
    private List<String> frontWithoutLoginUrls;
}
