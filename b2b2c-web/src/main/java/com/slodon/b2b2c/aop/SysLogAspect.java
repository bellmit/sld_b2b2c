package com.slodon.b2b2c.aop;

import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.system.AdminLogModel;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.system.pojo.AdminLog;
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
public class SysLogAspect {

    @Resource
    private AdminLogModel adminLogModel;

    @Pointcut("@annotation(com.slodon.b2b2c.aop.OperationLogger)")
    public void controllerAspect() {
    }

    @AfterReturning(returning = "value", pointcut = "controllerAspect()")
    public void doAfter(JoinPoint joinPoint, Object value) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        Admin admin = UserUtil.getUser(request, Admin.class);
        if (admin != null && admin.getAdminId() != null) {
            AdminLog adminLog = new AdminLog();
            adminLog.setAdminId(admin.getAdminId());
            adminLog.setAdminName(admin.getAdminName());
            adminLog.setLogIp(WebUtil.getRealIp(request));
            adminLog.setLogUrl(request.getRequestURI());

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            OperationLogger annotation = method.getAnnotation(OperationLogger.class);
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
            adminLog.setLogContent(opt.toString());
            adminLog.setLogTime(new Date());
            adminLogModel.saveAdminLog(adminLog);
        }
    }
}
