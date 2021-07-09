package com.slodon.b2b2c.controller.system.admin;

import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.model.system.PcFirstAdvModel;
import com.slodon.b2b2c.system.pojo.PcFirstAdv;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Api(tags = "admin-首页开屏图设置")
@RestController
@RequestMapping("v3/system/admin/pcFirstAdv")
public class AdminPcFirstAdvController {

    @Resource
    private PcFirstAdvModel pcFirstAdvModel;

    @ApiOperation("pc首页弹层广告")
    @GetMapping("get")
    public JsonResult<PcFirstAdv> get() {
        PcFirstAdv pcFirstAdv = pcFirstAdvModel.getPcFirstAdvByAdvId(1);
        return SldResponse.success(pcFirstAdv);
    }

    @ApiOperation("更新pc首页弹层广告")
    @OperationLogger(option = "更新pc首页弹层广告")
    @PostMapping("update")
    public JsonResult update(HttpServletRequest request, PcFirstAdv pcFirstAdv) {
        pcFirstAdvModel.updatePcFirstAdv(pcFirstAdv);
        return SldResponse.success("更新成功");
    }
}
