package com.slodon.b2b2c.controller.system.admin;

import com.slodon.b2b2c.core.constant.AdminConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.system.SystemResourceModel;
import com.slodon.b2b2c.system.example.SystemResourceExample;
import com.slodon.b2b2c.system.pojo.SystemResource;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = "admin-资源管理")
@RestController
@RequestMapping("v3/system/admin/resource")
public class AdminResourceController extends BaseController {

    @Resource
    private SystemResourceModel systemResourceModel;

    @ApiOperation("资源列表")
    @GetMapping("list")
    public JsonResult<PageVO<SystemResource>> list(HttpServletRequest request) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        SystemResourceExample example = new SystemResourceExample();
        example.setState(AdminConst.RESOURCE_STATE_1);
        example.setGrade(AdminConst.RESOURCE_GRADE_1);
        example.setOrderBy("resource_id asc");
        List<SystemResource> list = systemResourceModel.getSystemResourceList(example, null);
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(resource -> {
                example.setGrade(AdminConst.RESOURCE_GRADE_2);
                example.setPid(resource.getResourceId());
                List<SystemResource> secondList = systemResourceModel.getSystemResourceList(example, null);
                resource.setChildren(secondList);
                if (!CollectionUtils.isEmpty(secondList)) {
                    secondList.forEach(systemResource -> {
                        example.setGrade(AdminConst.RESOURCE_GRADE_3);
                        example.setPid(systemResource.getResourceId());
                        systemResource.setChildren(systemResourceModel.getSystemResourceList(example, null));
                    });
                }
            });
        }
        return SldResponse.success(new PageVO<>(list, pager));
    }
}
