package com.slodon.b2b2c.controller.msg.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.model.msg.MemberTplModel;
import com.slodon.b2b2c.model.msg.StoreTplModel;
import com.slodon.b2b2c.msg.dto.MemberTplDTO;
import com.slodon.b2b2c.msg.dto.StoreTplDTO;
import com.slodon.b2b2c.msg.example.MemberTplExample;
import com.slodon.b2b2c.msg.example.StoreTplExample;
import com.slodon.b2b2c.msg.pojo.MemberTpl;
import com.slodon.b2b2c.msg.pojo.StoreTpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = "admin-消息模板")
@RestController
@Slf4j
@RequestMapping("v3/msg/admin/msg/tpl")
public class AdminMsgTplController {

    @Resource
    private MemberTplModel memberTplModel;
    @Resource
    private StoreTplModel storeTplModel;

    @ApiOperation("会员消息模板列表")
    @GetMapping("memberTplList")
    public JsonResult<List<MemberTpl>> memberTplList(HttpServletRequest request) {
        //从请求头中获取语言类型
        String languageType = request.getHeader("Language");

        List<MemberTpl> list = memberTplModel.getMemberTplList(new MemberTplExample(), null);
        list.forEach(memberTpl -> {
            //翻译
            String value = Language.translate(memberTpl.getTplName(), languageType);
            memberTpl.setTplName(value);
        });
        return SldResponse.success(list);
    }

    @ApiOperation("编辑会员消息模板")
    @OperationLogger(option = "编辑会员消息模板")
    @PostMapping("updateMemberTpl")
    public JsonResult updateMemberTpl(HttpServletRequest request, MemberTplDTO memberTplDTO) {
        memberTplModel.updateMemberTpl(memberTplDTO);
        return SldResponse.success("修改成功");
    }

    @ApiOperation("商户消息模板列表")
    @GetMapping("storeTplList")
    public JsonResult<List<StoreTpl>> storeTplList(HttpServletRequest request) {
        //从请求头中获取语言类型
        String languageType = request.getHeader("Language");

        List<StoreTpl> list = storeTplModel.getStoreTplList(new StoreTplExample(), null);
        list.forEach(StoreTpl -> {
            //翻译
            String value = Language.translate(StoreTpl.getTplName(), languageType);
            StoreTpl.setTplName(value);
        });
        return SldResponse.success(list);
    }

    @ApiOperation("编辑商户消息模板")
    @OperationLogger(option = "编辑商户消息模板")
    @PostMapping("updateStoreTpl")
    public JsonResult updateStoreTpl(HttpServletRequest request, StoreTplDTO storeTplDTO) {
        storeTplModel.updateStoreTpl(storeTplDTO);
        return SldResponse.success("修改成功");
    }

}
