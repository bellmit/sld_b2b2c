package com.slodon.b2b2c.controller.system.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.system.SearchFilterModel;
import com.slodon.b2b2c.system.example.SearchFilterExample;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.system.pojo.SearchFilter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Api(tags = "admin-敏感词管理")
@RestController
@RequestMapping("v3/system/admin/search/filter")
public class AdminSearchFilterController extends BaseController {

    @Resource
    private SearchFilterModel searchFilterModel;

    @ApiOperation("敏感词列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "敏感词", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<SearchFilter>> list(HttpServletRequest request, String keyword) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        SearchFilterExample example = new SearchFilterExample();
        example.setKeywordLike(keyword);
        List<SearchFilter> list = searchFilterModel.getSearchFilterList(example, pager);
        return SldResponse.success(new PageVO<>(list, pager));
    }

    @ApiOperation("添加敏感词")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "敏感词", required = true)
    })
    @OperationLogger(option = "添加敏感词")
    @PostMapping("add")
    public JsonResult addSearchFilter(HttpServletRequest request, String keyword) {
        AssertUtil.isTrue(StringUtils.isEmpty(keyword.trim()), "请输入敏感词");

        Admin adminUser = UserUtil.getUser(request, Admin.class);

        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setKeyword(keyword.trim());
        searchFilter.setCreateId(adminUser.getAdminId());
        searchFilter.setCreateName(adminUser.getAdminName());
        searchFilter.setCreateTime(new Date());
        searchFilterModel.saveSearchFilter(searchFilter);
        return SldResponse.success("添加成功", searchFilter.getKeyword());
    }

    @ApiOperation("修改敏感词")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filterId", value = "过滤id", required = true),
            @ApiImplicitParam(name = "keyword", value = "敏感词", required = true)
    })
    @OperationLogger(option = "修改敏感词")
    @PostMapping("update")
    public JsonResult updateSearchFilter(HttpServletRequest request, Integer filterId, String keyword) {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setFilterId(filterId);
        searchFilter.setKeyword(keyword.trim());
        searchFilter.setUpdateTime(new Date());
        searchFilterModel.updateSearchFilter(searchFilter);
        return SldResponse.success("修改成功", searchFilter.getKeyword());
    }

    @ApiOperation("删除敏感词")
    @OperationLogger(option = "删除敏感词")
    @PostMapping("del")
    public JsonResult delSearchFilter(HttpServletRequest request, Integer filterId) {
        searchFilterModel.deleteSearchFilter(filterId);
        return SldResponse.success("删除成功", "敏感词ID:" + filterId);
    }

}
