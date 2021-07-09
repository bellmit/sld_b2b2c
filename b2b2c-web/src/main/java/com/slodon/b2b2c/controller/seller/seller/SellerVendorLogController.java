package com.slodon.b2b2c.controller.seller.seller;

import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.seller.VendorLogModel;
import com.slodon.b2b2c.seller.example.VendorLogExample;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.seller.pojo.VendorLog;
import com.slodon.b2b2c.vo.seller.VendorLogVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "操作员日志管理")
@RestController
@RequestMapping("v3/seller/seller/vendorLog")
public class SellerVendorLogController extends BaseController {

    @Resource
    private VendorLogModel vendorLogModel;

    @ApiOperation("操作日志列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vendorName", value = "账号名称", paramType = "query"),
            @ApiImplicitParam(name = "operationContent", value = "日志内容", paramType = "query"),
            @ApiImplicitParam(name = "optTimeAfter", value = "操作开始时间", paramType = "query"),
            @ApiImplicitParam(name = "optTimeBefore", value = "操作结束时间", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<VendorLogVO>> getList(HttpServletRequest request,
                                                   @RequestParam(value = "vendorName", required = false) String vendorName,
                                                   @RequestParam(value = "operationContent", required = false) String operationContent,
                                                   @RequestParam(value = "optTimeAfter", required = false) Date optTimeAfter,
                                                   @RequestParam(value = "optTimeBefore", required = false) Date optTimeBefore) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        VendorLogExample vendorLogExample = new VendorLogExample();
        vendorLogExample.setVendorNameLike(vendorName);
        vendorLogExample.setOperationContentLike(operationContent);
        vendorLogExample.setOptTimeAfter(optTimeAfter);
        vendorLogExample.setOptTimeBefore(optTimeBefore);
        vendorLogExample.setStoreId(vendor.getStoreId());
        vendorLogExample.setPager(pager);
        List<VendorLogVO> vos = new ArrayList<>();
        List<VendorLog> vendorLogList = vendorLogModel.getVendorLogList(vendorLogExample, pager);
        if (!CollectionUtils.isEmpty(vendorLogList)) {
            for (VendorLog vendorLog : vendorLogList) {
                VendorLogVO vo = new VendorLogVO(vendorLog);
                vos.add(vo);
            }
        }
        return SldResponse.success(new PageVO<>(vos, vendorLogExample.getPager()));
    }

    @ApiOperation("操作日志批量导出")
    @VendorLogger(option = "操作日志批量导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "logIds", value = "日志id字符串", paramType = "query"),
            @ApiImplicitParam(name = "vendorName", value = "账号名称", paramType = "query"),
            @ApiImplicitParam(name = "operationContent", value = "日志内容", paramType = "query"),
            @ApiImplicitParam(name = "optTimeAfter", value = "操作开始时间", paramType = "query"),
            @ApiImplicitParam(name = "optTimeBefore", value = "操作结束时间", paramType = "query")
    })
    @GetMapping("export")
    public JsonResult export(HttpServletRequest request, HttpServletResponse response, String logIds,
                             String vendorName, String operationContent, Date optTimeAfter, Date optTimeBefore) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        //参数校验
        AssertUtil.notEmpty(logIds,"日志批量导出logIds不能为空!");
        AssertUtil.notFormatFrontIds(logIds,"logIds格式错误,请重试");
        VendorLogExample example = new VendorLogExample();
        example.setStoreId(vendor.getStoreId());
        example.setLogIdIn(logIds);
        example.setVendorNameLike(vendorName);
        example.setOperationContent(operationContent);
        example.setOptTimeAfter(optTimeAfter);
        example.setOptTimeBefore(optTimeBefore);
        vendorLogModel.vendorLogExport(request, response, example);
        return null;
    }
}
