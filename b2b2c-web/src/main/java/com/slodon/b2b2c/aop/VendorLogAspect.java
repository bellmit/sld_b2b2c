package com.slodon.b2b2c.aop;


import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.seller.VendorLogModel;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.seller.pojo.VendorLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

@Aspect
@Component
public class VendorLogAspect {

    @Resource
    private VendorLogModel vendorLogModel;

    @Pointcut("@annotation(com.slodon.b2b2c.aop.VendorLogger)")
    public void controllerAspect() {
    }

    @AfterReturning(returning = "value", pointcut = "controllerAspect()")
    public void doAfter(JoinPoint joinPoint, Object value) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        if (vendor != null) {
            VendorLog log = new VendorLog();
            log.setVendorId(vendor.getVendorId());
            log.setVendorName(vendor.getVendorName());
            log.setIp(WebUtil.getRealIp(request));
            log.setOperationUrl(request.getRequestURI());

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            VendorLogger annotation = method.getAnnotation(VendorLogger.class);
            StringBuilder opt = new StringBuilder(annotation.option());
            JsonResult result = (JsonResult) value;
            opt.append("[");
            opt.append(result.getMsg());
            opt.append("]");
            if (!StringUtils.isEmpty(result.getLogMsg())) {
                opt.append("[");
                opt.append(result.getLogMsg());
                opt.append("]");
            }
            log.setOperationContent(opt.toString());
            log.setOptTime(new Date());
            vendorLogModel.saveVendorLog(log);
        }
    }
}
