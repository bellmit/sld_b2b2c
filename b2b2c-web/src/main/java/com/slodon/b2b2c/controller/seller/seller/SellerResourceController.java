package com.slodon.b2b2c.controller.seller.seller;

import com.slodon.b2b2c.core.constant.VendorConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.seller.VendorResourcesModel;
import com.slodon.b2b2c.seller.example.VendorResourcesExample;
import com.slodon.b2b2c.seller.pojo.VendorResources;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = "seller-资源管理")
@RestController
@RequestMapping("v3/seller/seller/resource")
public class SellerResourceController extends BaseController {

    @Resource
    private VendorResourcesModel vendorResourcesModel;

    @ApiOperation("资源列表")
    @GetMapping("list")
    public JsonResult<PageVO<VendorResources>> list(HttpServletRequest request) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        VendorResourcesExample example = new VendorResourcesExample();
        example.setState(VendorConst.RESOURCE_STATE_1);
        example.setGrade(VendorConst.RESOURCE_GRADE_2);
        example.setFrontPathNotEquals("/apply");
        example.setOrderBy("resources_id asc");
        List<VendorResources> list = vendorResourcesModel.getVendorResourcesList(example, null);
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(resource -> {
                example.setGrade(VendorConst.RESOURCE_GRADE_3);
                example.setPid(resource.getResourcesId());
                resource.setChildren(vendorResourcesModel.getVendorResourcesList(example, null));
            });
        }
        return SldResponse.success(new PageVO<>(list, pager));
    }
}
