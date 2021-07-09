package com.slodon.b2b2c.controller.system.seller;

import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.model.system.ReasonModel;
import com.slodon.b2b2c.system.example.ReasonExample;
import com.slodon.b2b2c.system.pojo.Reason;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = "seller-原因管理")
@RestController
@RequestMapping("v3/system/seller/reason")
public class SellerReasonController extends BaseController {

    @Resource
    private ReasonModel reasonModel;

    @ApiOperation("原因列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "原因类型：101-违规下架；102-商品审核拒绝；103-入驻审核拒绝；104-会员取消订单；105-仅退款-未收货；106-仅退款-已收货；107-退款退货原因；108-商户取消订单", required = true, paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<List<Reason>> getList(HttpServletRequest request, Integer type) {
        ReasonExample example = new ReasonExample();
        example.setType(type);
        example.setOrderBy("sort asc, create_time desc");
        List<Reason> list = reasonModel.getReasonList(example, null);
        return SldResponse.success(list);
    }
}
