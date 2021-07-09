package com.slodon.b2b2c.controller.system.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.system.AgreementModel;
import com.slodon.b2b2c.system.example.AgreementExample;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.system.pojo.Agreement;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Api(tags = "admin-协议管理")
@RestController
@RequestMapping("v3/system/admin/agreement")
public class AdminAgreementController extends BaseController {

    @Resource
    private AgreementModel agreementModel;

    @ApiOperation("协议列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<Agreement>> list(HttpServletRequest request) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        AgreementExample example = new AgreementExample();
        List<Agreement> list = agreementModel.getAgreementList(example, pager);
        return SldResponse.success(new PageVO<>(list, pager));
    }

    @ApiOperation("获取协议详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "agreementCode", value = "协议编码", required = true, paramType = "query")
    })
    @GetMapping("detail")
    public JsonResult<Agreement> detail(HttpServletRequest request, String agreementCode) {
        Agreement agreement = agreementModel.getAgreementByAgreementCode(agreementCode);
        return SldResponse.success(agreement);
    }

    @ApiOperation("修改协议")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "agreementCode", value = "协议编码", required = true),
            @ApiImplicitParam(name = "title", value = "标题", required = true),
            @ApiImplicitParam(name = "content", value = "内容", required = true)
    })
    @OperationLogger(option = "修改协议")
    @PostMapping("update")
    public JsonResult update(HttpServletRequest request, String agreementCode, String title, String content) {
        Admin adminUser = UserUtil.getUser(request, Admin.class);

        Agreement agreement = new Agreement();
        agreement.setAgreementCode(agreementCode);
        agreement.setTitle(title);
        agreement.setContent(content);
        agreement.setUpdateAdminId(adminUser.getAdminId());
        agreement.setUpdateAdminName(adminUser.getAdminName());
        agreement.setUpdateTime(new Date());
        agreementModel.updateAgreement(agreement);
        return SldResponse.success("修改成功", "协议编码:" + agreement.getAgreementCode());
    }
}
