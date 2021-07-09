package com.slodon.b2b2c.controller.helpdesk.seller;


import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.constant.MsgConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.helpdesk.example.HelpdeskSoundSettingExample;
import com.slodon.b2b2c.helpdesk.pojo.HelpdeskSoundSetting;
import com.slodon.b2b2c.model.helpdesk.HelpdeskSoundSettingModel;
import com.slodon.b2b2c.seller.pojo.Vendor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "seller-消息设置")
@RestController
@RequestMapping("v3/helpdesk/seller/sound/setting")
public class SellerSoundSettingController extends BaseController {

    @Resource
    private HelpdeskSoundSettingModel helpdeskSoundSettingModel;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @ApiOperation("接收设置")
    @PostMapping("isOpen")
    public JsonResult isOpen(HttpServletRequest request) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        HelpdeskSoundSettingExample example = new HelpdeskSoundSettingExample();
        example.setStoreId(vendor.getStoreId());
        example.setVendorId(vendor.getVendorId());
        List<HelpdeskSoundSetting> list = helpdeskSoundSettingModel.getHelpdeskSoundSettingList(example, null);
        Map<String, Object> dataMap = new HashMap<>();
        if (CollectionUtils.isEmpty(list)) {
            dataMap.put("isOpen", 1);
        } else {
            HelpdeskSoundSetting soundSetting = list.get(0);
            if (soundSetting.getIsOpen() == MsgConst.IS_OPEN_SWITCH_YES) {
                dataMap.put("isOpen", 1);
            } else {
                dataMap.put("isOpen", 0);
            }
        }
        return SldResponse.success(dataMap);
    }

    @ApiOperation("修改新消息声音提醒设置")
    @VendorLogger(option = "修改新消息声音提醒设置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isOpen", value = "是否开启 1是，0否", required = true)
    })
    @PostMapping("update")
    public JsonResult update(HttpServletRequest request, Integer isOpen) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        HelpdeskSoundSetting setting = new HelpdeskSoundSetting();
        setting.setStoreId(vendor.getStoreId());
        setting.setVendorId(vendor.getVendorId());
        setting.setIsOpen(isOpen);
        helpdeskSoundSettingModel.updateHelpdeskSoundSetting(setting);
        return SldResponse.success("设置成功");
    }

}
