package com.slodon.b2b2c.controller.system.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.system.SearchLogModel;
import com.slodon.b2b2c.system.example.SearchLogExample;
import com.slodon.b2b2c.system.pojo.SearchLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = "admin-搜索词管理")
@RestController
@RequestMapping("v3/system/admin/search/log")
public class AdminSearchLogController extends BaseController {

    @Resource
    private SearchLogModel searchLogModel;

    @ApiOperation("搜索词列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "搜索词", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<SearchLog>> searchHistoryList(HttpServletRequest request, String keyword) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        SearchLogExample example = new SearchLogExample();
        example.setKeywordLike(keyword);
        List<SearchLog> list = searchLogModel.getSearchLogList(example, pager);
        return SldResponse.success(new PageVO<>(list, pager));
    }

    @ApiOperation("批量删除搜索历史记录")
    @OperationLogger(option = "批量删除搜索历史记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "logIds", value = "记录id字符串，以逗号隔开", required = true)
    })
    @PostMapping("batchDel")
    public JsonResult<Integer> batchDel(HttpServletRequest request, @RequestParam("logIds") String logIds) {
        //参数校验
        AssertUtil.notEmpty(logIds,"请选择要删除的数据");
        AssertUtil.notFormatFrontIds(logIds,"logIds格式错误,请重试");

        searchLogModel.batchDeleteSearchLog(logIds);

        return SldResponse.success("删除成功");
    }
}
