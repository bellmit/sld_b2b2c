package com.slodon.b2b2c.core.util;

import com.slodon.b2b2c.core.config.DomainUrlUtil;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 通用处理方法工具类
 */
public class CommonUtil {

    /**
     * 处理手机号，隐藏中间四位
     *
     * @param mobile
     * @return
     */
    public static String dealMobile(String mobile) {
        if (StringUtils.isEmpty(mobile)) {
            return mobile;
        }
        List<String> hiddenHosts = Arrays.asList("https://jbbcadmin.slodon.cn", "http://jbbcs-admin.slodon.cn");
        if (hiddenHosts.contains(DomainUrlUtil.SLD_ADMIN_URL)) {
            return mobile.replaceFirst("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        }
        return mobile;
    }
}
