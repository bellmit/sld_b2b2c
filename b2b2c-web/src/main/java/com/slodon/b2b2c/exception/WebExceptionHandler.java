package com.slodon.b2b2c.exception;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类
 */
@Slf4j
@RestControllerAdvice("com.slodon.b2b2c.controller")
public class WebExceptionHandler {

    @ExceptionHandler(MallException.class)
    @ResponseBody
    public JsonResult HandleMallException(MallException e) {
        log.error(e.getMessage(), e);
        return new JsonResult(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public JsonResult HandleException(Exception e) {
        log.error(e.getMessage(), e);
        return new JsonResult("服务器开小差了，请稍后重试");
    }
}
