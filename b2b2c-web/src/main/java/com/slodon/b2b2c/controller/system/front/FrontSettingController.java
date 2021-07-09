package com.slodon.b2b2c.controller.system.front;

import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.model.system.SettingModel;
import com.slodon.b2b2c.system.pojo.Setting;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "front-系统配置")
@RestController
@RequestMapping("v3/system/front/setting")
public class FrontSettingController {

    @Resource
    private SettingModel settingModel;

    @ApiOperation("获取配置信息接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "names", required = true, paramType = "query",
                    value = "配置名称,多个配置名用逗号分隔（basic_site_name==网站名称，main_site_logo==网站logo）")
    })
    @GetMapping("getSettings")
    public JsonResult getSettings(String names) {
        List<String> list = new ArrayList<>();
        for (String name : names.split(",")) {
            Setting setting = settingModel.getSettingByName(name);
            if (setting == null) {
                list.add("");
            } else {
                list.add(setting.getType() == 2 ? FileUrlUtil.getFileUrl(setting.getValue(), null) : setting.getValue());
            }
        }
        return SldResponse.success(list);
    }
}
