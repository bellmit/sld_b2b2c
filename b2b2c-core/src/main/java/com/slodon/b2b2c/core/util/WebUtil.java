package com.slodon.b2b2c.core.util;

import com.slodon.b2b2c.core.constant.CommonConst;
import com.slodon.b2b2c.core.response.PagerInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class WebUtil {


    /**
     * 获取POST传过来的参数
     *
     * @param request
     * @return
     */
    public static Map<String, String> handlerPostMap(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        if (params == null)
            return null;
        Map<String, String> postMap = new HashMap<>();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            if (entry.getKey().length() == 0 || entry.getValue() == null || entry.getValue().length <= 0) {
                continue;
            }
            if (entry.getValue().length == 1) {
                postMap.put(entry.getKey(), entry.getValue()[0]);
            }
        }
        return postMap;
    }




    /**
     * 分页信息相关 TODO 需要分前台和后台的page_size
     *
     * @param request
     * @return
     */
    @SuppressWarnings("unchecked")
    public static PagerInfo handlerPagerInfo(HttpServletRequest request) {
        try {
            int pageSize = "".equals(StringUtil.nullSafeString(request.getParameter("pageSize"))) ? CommonConst.DEFAULT_PAGE_SIZE
                    : Integer.parseInt(request.getParameter("pageSize"));
            int pageIndex = "".equals(StringUtil.nullSafeString(request.getParameter("current"))) ? 1
                    : Integer.parseInt(request.getParameter("current"));
            return new PagerInfo(pageSize, pageIndex);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    

    /**
     * 获取http请求ip
     *
     * @param request
     * @return
     */
    public static String getRealIp(HttpServletRequest request) {
        // 这个一般是Nginx反向代理设置的参数
        String ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多IP的情况（只取第一个IP）
        if (ip != null && ip.contains(",")) {
            String[] ipArray = ip.split(",");
            ip = ipArray[0];
        }
        return ip;
    }
}
