package com.slodon.b2b2c.controller.system.seller;

import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.model.system.SettingModel;
import com.slodon.b2b2c.system.pojo.Setting;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

@Api(tags = "seller-系统配置")
@RestController
@RequestMapping("v3/system/seller/setting")
public class SellerSettingController extends BaseController {

    @Resource
    private SettingModel settingModel;

    @ApiOperation("获取店铺配置信息(不需要登录)")
    @GetMapping("getStoreSetting")
    public JsonResult<List<Setting>> getStoreSetting() {

        String str = "main_seller_center_logo";

        List<Setting> list = getSetting(str);
        AssertUtil.notNull(list, "请输入正确的参数");
        return SldResponse.success(list);
    }

    @ApiOperation("获取配置信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "str", value = "要查询的键组合，以逗号分割", required = true, paramType = "query")
    })
    @GetMapping("getSettingList")
    public JsonResult<List<Setting>> getSettingList(String str) {
        AssertUtil.isTrue(StringUtils.isEmpty(str), "请输入正确的参数");

        List<Setting> list = getSetting(str);
        AssertUtil.notNull(list, "请输入正确的参数");
        return SldResponse.success(list);
    }

    /**
     * 获取Setting参数
     *
     * @param str 以逗号分割要查询的键值name
     * @return
     */
    private List<Setting> getSetting(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        List<Setting> list = new LinkedList<>();
        String[] split = str.split(",");
        for (String s : split) {
            if (StringUtils.isEmpty(s)) {
                continue;
            }
            Setting setting = settingModel.getSettingByName(s);
            if (setting == null) {
                continue;
            }
            //类型，1-字符串，2-图片，3-固定不能修改；4-开关配置, 如果是图片需要返回图片的完整连接地址
            if (2 == setting.getType()) {
                setting.setImageUrl(StringUtil.isEmpty(setting.getValue()) ? null : FileUrlUtil.getFileUrl(setting.getValue(), null));
            }
            list.add(setting);
        }
        return list;
    }

}
