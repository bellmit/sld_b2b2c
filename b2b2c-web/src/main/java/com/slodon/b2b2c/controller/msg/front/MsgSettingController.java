package com.slodon.b2b2c.controller.msg.front;

import com.slodon.b2b2c.core.constant.MsgConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.msg.example.MemberSettingExample;
import com.slodon.b2b2c.msg.example.SystemTplTypeExample;
import com.slodon.b2b2c.model.msg.MemberSettingModel;
import com.slodon.b2b2c.model.msg.SystemTplTypeModel;
import com.slodon.b2b2c.msg.pojo.MemberSetting;
import com.slodon.b2b2c.msg.pojo.SystemTplType;
import com.slodon.b2b2c.vo.msg.SystemTplTypeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "front-接收设置")
@RestController
@Slf4j
@RequestMapping("v3/msg/front/msg/setting")
public class MsgSettingController extends BaseController {

    @Resource
    private SystemTplTypeModel systemTplTypeModel;
    @Resource
    private MemberSettingModel memberSettingModel;

    @ApiOperation("接收设置列表")
    @GetMapping("list")
    public JsonResult<List<SystemTplTypeVO>> list(HttpServletRequest request) {
        Member member = UserUtil.getUser(request, Member.class);

        SystemTplTypeExample example = new SystemTplTypeExample();
        example.setTplSource(MsgConst.TPL_SOURCE_MEMBER);
        List<SystemTplType> list = systemTplTypeModel.getSystemTplTypeList(example, null);
        AssertUtil.notEmpty(list, "请配置消息模板分类-内置表");

        List<SystemTplTypeVO> vos = new ArrayList<>();
        list.forEach(tplType -> {
            tplType.getMemberTplList().forEach(memberTpl -> {
                MemberSettingExample settingExample = new MemberSettingExample();
                settingExample.setMemberId(member.getMemberId());
                settingExample.setTplCode(memberTpl.getTplCode());
                List<MemberSetting> settingList = memberSettingModel.getMemberSettingList(settingExample, null);
                if (!CollectionUtils.isEmpty(settingList)) {
                    memberTpl.setIsReceive(settingList.get(0).getIsReceive());
                } else {
                    //默认展示关闭状态
                    memberTpl.setIsReceive(MsgConst.IS_OPEN_SWITCH_NO);
                }
            });
            vos.add(new SystemTplTypeVO(tplType));
        });
        return SldResponse.success(vos);
    }

    @ApiOperation("是否接受设置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tplCode", value = "模板编码", required = true),
            @ApiImplicitParam(name = "isReceive", value = "是否接收 1是，0否", required = true)
    })
    @PostMapping("isReceive")
    public JsonResult isReceive(HttpServletRequest request, String tplCode, Integer isReceive) {
        Member member = UserUtil.getUser(request, Member.class);

        MemberSetting setting = new MemberSetting();
        setting.setMemberId(member.getMemberId());
        setting.setTplCode(tplCode);
        setting.setIsReceive(isReceive);
        memberSettingModel.updateMemberSetting(setting);
        return SldResponse.success("设置成功");
    }
}
