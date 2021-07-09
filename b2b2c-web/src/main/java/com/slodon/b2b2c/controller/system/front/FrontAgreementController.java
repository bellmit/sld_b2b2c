package com.slodon.b2b2c.controller.system.front;


import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.model.system.AgreementModel;
import com.slodon.b2b2c.system.pojo.Agreement;
import com.slodon.b2b2c.vo.business.AgreementDetailVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Api(tags = "front-协议管理")
@RestController
@RequestMapping("v3/system/front/agreement")
public class FrontAgreementController extends BaseController {

    @Resource
    private AgreementModel agreementModel;

    @ApiOperation("获取协议详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "agreementCode", value = "协议编码: register_agreement 用户注册的协议, business_residence_agreement 商户入驻的协议", required = true, paramType = "query")
    })
    @GetMapping("detail")
    public JsonResult<AgreementDetailVO> detail(HttpServletRequest request, String agreementCode) {
        AssertUtil.notEmpty(agreementCode, "协议编码不能为空");
        Agreement agreement = agreementModel.getAgreementByAgreementCode(agreementCode);
        AssertUtil.notNull(agreement, "查询的协议信息为空");
        AgreementDetailVO vo = new AgreementDetailVO(agreement);
        return SldResponse.success(vo);
    }
}
