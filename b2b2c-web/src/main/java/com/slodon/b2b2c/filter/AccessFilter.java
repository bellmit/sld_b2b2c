package com.slodon.b2b2c.filter;

import com.alibaba.fastjson.JSON;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.slodon.b2b2c.config.IgnoreUrlsConfig;
import com.slodon.b2b2c.core.constant.WebConst;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.user.UserAuthority;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 访问过滤器
 */
@Slf4j
@Component
public class AccessFilter implements Filter, Ordered {

    /**
     * 合法的uri前缀
     */
    private List<String> legalPrefixes = Arrays.asList(WebConst.WEB_IDENTIFY_ADMIN, WebConst.WEB_IDENTIFY_SELLER, WebConst.WEB_IDENTIFY_FRONT);

    @Resource
    private IgnoreUrlsConfig ignoreUrlsConfig;
    @Resource
    private RedisTemplate<String, Object> objectRedisTemplate;

    public void destroy() {
    }

    @SneakyThrows
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
//        response.setHeader("Access-Control-Allow-Origin", "*"); //  这里最好明确的写允许的域名
//        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
//        response.setHeader("Access-Control-Max-Age", "3600");
//        response.setHeader("Access-Control-Allow-Headers", "Content-Type,Access-Token,Authorization,ybg");
        response.setHeader("Content-Type", "application/json;charset=utf-8");

        //请求的URI
        String uri = request.getRequestURI();
        // Referer从哪个页面链接过来的
        String referer = request.getHeader("Referer");
        log.debug("AccessFilter-getRequestURI:" + request.getRequestURI());
        log.debug("AccessFilter-referer:" + referer);

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        PathMatcher pathMatcher = new AntPathMatcher();
        //通用白名单放行
        for (String s : ignoreUrlsConfig.getCommonUrls()) {
            if (pathMatcher.match(s, uri)) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
        }
        //登录白名单放行
        for (String s : ignoreUrlsConfig.getLoginUrls()) {
            if (pathMatcher.match(s, uri)) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
        }
        //商城端未传token的白名单放行
        for (String s : ignoreUrlsConfig.getFrontWithoutLoginUrls()) {
            if (pathMatcher.match(s, uri) && !containsToken(header)) {
                //不包含token，或者token不合法
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
        }

        //验证请求uri是否合法
        String webId;
        String regex = "/(v\\d+?)(/\\S*?)/(\\S*?)/\\S*";
        if (uri.matches(regex)) {
            webId = uri.replaceFirst(regex, "$3");
            if (!legalPrefixes.contains(webId)) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
        } else {
            // 如果没有登录，去登录页
            response.getWriter().print(JSON.toJSON(SldResponse.unAuth()));
            return;
        }

        try {
            String token = header.replaceFirst("Bearer ", "");
            //解析token
            JWTClaimsSet jwtClaimsSet = JWTParser.parse(token).getJWTClaimsSet();
            if (!webId.equals(jwtClaimsSet.getStringClaim(WebConst.WEB_IDENTIFY))) {
                response.getWriter().print(JSON.toJSON(SldResponse.fail("非法请求")));
                return;
            }
            //根据 token 信息获取 redis 中的用户信息及权限信息
            String uuid = jwtClaimsSet.getStringClaim("uuid");
            //获取其中的user_id
            String userId = jwtClaimsSet.getStringClaim("user_id");
            Object user = objectRedisTemplate.opsForHash().get(jwtClaimsSet.getStringClaim("webIdentify") + "-" + userId, uuid);
            if (user == null) {
                // 如果没有登录，去登录页
                response.getWriter().print(JSON.toJSON(SldResponse.unLogin()));
            } else {
                //用户权限信息
                UserAuthority<Object> userAuthority = UserAuthority.mapToUser((Map) user);
                servletRequest.setAttribute(WebConst.USER_HEADER, userAuthority);
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
        } catch (Exception e) {
            response.getWriter().print(JSON.toJSON(SldResponse.unLogin()));
        }
    }

    public void init(FilterConfig arg0) {
    }

    /**
     * 判断 AUTHENTICATION 请求头中是否包含合法的token
     *
     * @param header
     * @return true==token合法
     */
    private Boolean containsToken(String header) {
        if (StringUtils.isEmpty(header)){
            return false;
        }
        String token = header.replaceFirst("Bearer ", "");
        //解析token
        try {
            JWTClaimsSet jwtClaimsSet = JWTParser.parse(token).getJWTClaimsSet();
            //token未过期，返回true
            return jwtClaimsSet.getExpirationTime().after(new Date());
        } catch (ParseException e) {
            //解析出错，token不合法
            return false;
        }
    }


    @Override
    public int getOrder() {
        return -1;
    }
}
