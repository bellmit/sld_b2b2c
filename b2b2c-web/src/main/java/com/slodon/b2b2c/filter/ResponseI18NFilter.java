package com.slodon.b2b2c.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.slodon.b2b2c.core.i18n.Language;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 多语言全局控制过滤器
 * 截获响应，对其中的msg字段进行多语言翻译
 */
@Slf4j
@Component
public class ResponseI18NFilter implements Filter, Ordered {


    public void destroy() {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        ResponseWrapper responseWrapper = new ResponseWrapper(response);
        filterChain.doFilter(request,responseWrapper);

        if (!responseWrapper.getContentType().equalsIgnoreCase("application/json;charset=utf-8")){
            //响应类型不是json，不处理
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }

        //响应二进制流
        byte[] content = responseWrapper.getContent();
        String s = new String(content, StandardCharsets.UTF_8);
        JSONObject jsonObject;
        try {
            jsonObject = JSON.parseObject(s);
        }catch (Exception e){
            //转换异常，直接跳过
            response.getOutputStream().write(content);
            return;
        }

        if (!StringUtils.isEmpty(jsonObject.getString("msg"))){
            //从请求头中获取语言类型
            String languageType = request.getHeader("Language");
            if (StringUtils.isEmpty(languageType)){
                languageType = Language.DEFAULT_LANGUAGE_TYPE;
            }
            //翻译
            //对响应中的msg字段进行翻译
            jsonObject.put("msg", Language.translate(jsonObject.getString("msg"),languageType));
        }
        //重新写入响应
        response.getOutputStream().write(JSON.toJSONString(jsonObject).getBytes());
    }

    public void init(FilterConfig arg0) {
    }


    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    /**
     * 响应包装类
     */
    public class ResponseWrapper extends HttpServletResponseWrapper {

        private ByteArrayOutputStream buffer;

        private ServletOutputStream out;

        public ResponseWrapper(HttpServletResponse httpServletResponse) {
            super(httpServletResponse);
            buffer = new ByteArrayOutputStream();
            out = new WrapperOutputStream(buffer);
        }

        @Override
        public ServletOutputStream getOutputStream()
                throws IOException {
            return out;
        }

        @Override
        public void flushBuffer()
                throws IOException {
            if (out != null) {
                out.flush();
            }
        }

        /**
         * 将响应流转为二进制流
         * @return
         * @throws IOException
         */
        public byte[] getContent()
                throws IOException {
            flushBuffer();
            return buffer.toByteArray();
        }

        /**
         * 响应输出流包装类
         */
        class WrapperOutputStream extends ServletOutputStream {
            private ByteArrayOutputStream bos;

            public WrapperOutputStream(ByteArrayOutputStream bos) {
                this.bos = bos;
            }

            /**
             * 写入二进制输出流中
             * @param b
             * @throws IOException
             */
            @Override
            public void write(int b)
                    throws IOException {
                bos.write(b);
            }

            @Override
            public boolean isReady() {
                return false;

            }

            @Override
            public void setWriteListener(WriteListener arg0) {

            }
        }

    }
}
