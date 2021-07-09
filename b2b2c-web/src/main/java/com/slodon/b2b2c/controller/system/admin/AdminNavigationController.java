package com.slodon.b2b2c.controller.system.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.system.NavigationModel;
import com.slodon.b2b2c.system.example.NavigationExample;
import com.slodon.b2b2c.system.pojo.Navigation;
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
import java.util.List;

@Api(tags = "admin-导航设置")
@RestController
@RequestMapping("v3/system/admin/navigation")
public class AdminNavigationController extends BaseController {

    @Resource
    private NavigationModel navigationModel;

    @ApiOperation("导航列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "navName", value = "导航名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<Navigation>> list(HttpServletRequest request, String navName) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        NavigationExample example = new NavigationExample();
        example.setNavNameLike(navName);
        List<Navigation> list = navigationModel.getNavigationList(example, pager);
        return SldResponse.success(new PageVO<>(list, pager));
    }

    @ApiOperation("添加导航")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "navName", value = "导航名称", required = true),
            @ApiImplicitParam(name = "sort", value = "排序", required = true),
            @ApiImplicitParam(name = "isShow", value = "是否显示，0：否；1：是"),
            @ApiImplicitParam(name = "data", value = "链接数据，json类型，包含链接类型、链接地址等")
    })
    @OperationLogger(option = "添加导航")
    @PostMapping("add")
    public JsonResult addNavigation(HttpServletRequest request, String navName, Integer sort, Integer isShow, String data) {
        Navigation navigation = new Navigation();
        navigation.setNavName(navName);
        navigation.setSort(sort);
        navigation.setIsShow(isShow);
        navigation.setData(data);
        navigationModel.saveNavigation(navigation);
        return SldResponse.success("添加成功", "导航名称:" + navigation.getNavName());
    }

    @ApiOperation("修改导航")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "navId", value = "导航id", required = true),
            @ApiImplicitParam(name = "navName", value = "导航名称", required = true),
            @ApiImplicitParam(name = "sort", value = "排序", required = true),
            @ApiImplicitParam(name = "isShow", value = "是否显示，0：否；1：是"),
            @ApiImplicitParam(name = "data", value = "链接数据，json类型，包含链接类型、链接地址等")
    })
    @OperationLogger(option = "修改导航")
    @PostMapping("update")
    public JsonResult updateNavigation(HttpServletRequest request, Integer navId, String navName, Integer sort, Integer isShow, String data) {
        Navigation navigation = new Navigation();
        navigation.setNavId(navId);
        navigation.setNavName(navName);
        navigation.setSort(sort);
        navigation.setIsShow(isShow);
        navigation.setData(data);
        navigationModel.updateNavigation(navigation);
        return SldResponse.success("编辑成功", "导航id:" + navId);
    }

    @ApiOperation("删除导航")
    @OperationLogger(option = "删除导航")
    @PostMapping("del")
    public JsonResult del(HttpServletRequest request, Integer navId) {
        navigationModel.deleteNavigation(navId);
        return SldResponse.success("删除成功", "导航id:" + navId);
    }

    @ApiOperation("是否显示导航")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "navId", value = "导航id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isShow", value = "是否显示，0为不显示，1为显示", required = true, paramType = "query")
    })
    @OperationLogger(option = "是否显示导航")
    @PostMapping("isShow")
    public JsonResult isShow(HttpServletRequest request, Integer navId, Integer isShow) {
        Navigation navigation = new Navigation();
        navigation.setNavId(navId);
        navigation.setIsShow(isShow);
        navigationModel.updateNavigation(navigation);
        return SldResponse.success("修改成功", "导航id:" + navId);
    }
}
