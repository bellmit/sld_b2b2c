package com.slodon.b2b2c.controller.system.seller;

import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.model.system.AgreementModel;
import com.slodon.b2b2c.system.pojo.Agreement;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Api(tags = "seller-协议管理")
@RestController
@RequestMapping("v3/system/seller/agreement")
public class SellerAgreementController extends BaseController {

    @Resource
    private AgreementModel agreementModel;

    @ApiOperation("获取协议详情")
    @GetMapping("detail")
    public JsonResult<Agreement> detail(HttpServletRequest request) {
        Agreement agreement = agreementModel.getAgreementByAgreementCode("business_residence_agreement");
        return SldResponse.success(agreement);
    }

}
