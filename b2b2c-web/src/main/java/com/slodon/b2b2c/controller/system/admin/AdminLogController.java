package com.slodon.b2b2c.controller.system.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.system.AdminLogModel;
import com.slodon.b2b2c.system.example.AdminLogExample;
import com.slodon.b2b2c.system.pojo.AdminLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@Api(tags = "admin-操作日志管理")
@RestController
@RequestMapping("v3/system/admin/adminLog")
public class AdminLogController extends BaseController {

    @Resource
    private AdminLogModel adminLogModel;

    @ApiOperation("操作日志列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "adminName", value = "操作人", paramType = "query"),
            @ApiImplicitParam(name = "logContent", value = "操作行为", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<AdminLog>> list(HttpServletRequest request, String adminName, String logContent,
                                             Date startTime, Date endTime) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        AdminLogExample example = new AdminLogExample();
        example.setAdminNameLike(adminName);
        example.setLogContentLike(logContent);
        example.setLogTimeAfter(startTime);
        example.setLogTimeBefore(endTime);
        List<AdminLog> list = adminLogModel.getAdminLogList(example, pager);
        return SldResponse.success(new PageVO<>(list, pager));
    }

    @ApiOperation("删除操作日志")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "logIds", value = "日志ID集合，用逗号隔开"),
            @ApiImplicitParam(name = "delAgo", value = "删除多久之前的日志的时间秒数, 如三个月前的时间为：7776000；删除全部日志则为：all：logIds")
    })
    @OperationLogger(option = "删除操作日志")
    @PostMapping("del")
    public JsonResult del(HttpServletRequest request, String logIds, String delAgo) {
        //参数校验
        AssertUtil.notEmpty(logIds, "请选择要删除的数据");
        AssertUtil.notFormatFrontIds(logIds, "logIds格式错误,请重试");

        AdminLogExample example = new AdminLogExample();
        if (!StringUtils.isEmpty(logIds)) {
            example.setLogIdIn(logIds);
            adminLogModel.batchDeleteAdminLog(example);
        } else if (!StringUtils.isEmpty(delAgo)) {
            if ("all".equals(delAgo)) {
                adminLogModel.batchDeleteAdminLog(example);
            } else {
                Date date = new Date(new Date().getTime() - Long.parseLong(delAgo));
                example.setDeleteTime(date);
                adminLogModel.batchDeleteAdminLog(example);
            }
        } else {
            return SldResponse.badArgument();
        }
        return SldResponse.success("删除成功");
    }

    @ApiOperation("操作日志导出")
    @OperationLogger(option = "操作日志导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "logIds", value = "日志id集合，用逗号隔开", paramType = "query"),
            @ApiImplicitParam(name = "adminName", value = "操作人", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query")
    })
    @GetMapping("export")
    public JsonResult export(HttpServletRequest request, HttpServletResponse response, String logIds,
                             String adminName, Date startTime, Date endTime) {
        AdminLogExample example = new AdminLogExample();
        if (!StringUtil.isEmpty(logIds)) {
            example.setLogIdIn(logIds);
        }
        example.setAdminNameLike(adminName);
        example.setLogTimeAfter(startTime);
        example.setLogTimeBefore(endTime);
        adminLogModel.adminLogExport(request, response, example);
        return null;
    }
}
