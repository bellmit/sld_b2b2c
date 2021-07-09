package com.slodon.b2b2c.core.util;

import com.alibaba.fastjson.JSON;
import com.slodon.b2b2c.core.constant.WebConst;
import com.slodon.b2b2c.core.user.UserAuthority;
import lombok.SneakyThrows;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录用户工具
 */
public class UserUtil {

    /**
     * 获取请求头中的登录用户
     *
     * @param request
     * @param tClass
     * @param <T>
     * @return
     */
    @SneakyThrows
    public static <T> T getUser(HttpServletRequest request, Class<T> tClass) {
        //登录用户信息
        UserAuthority<T> userInfo = (UserAuthority<T>) request.getAttribute(WebConst.USER_HEADER);
        if (userInfo != null) {
            return JSON.parseObject(JSON.toJSONString(userInfo.getT()), tClass);
        } else {
            return tClass.getDeclaredConstructor().newInstance();
        }
    }
}
