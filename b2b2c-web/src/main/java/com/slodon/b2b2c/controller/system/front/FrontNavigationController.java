package com.slodon.b2b2c.controller.system.front;

import com.slodon.b2b2c.core.constant.TplPcConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.model.system.NavigationModel;
import com.slodon.b2b2c.system.example.NavigationExample;
import com.slodon.b2b2c.system.pojo.Navigation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = "front-首页导航")
@RestController
@RequestMapping("v3/system/front/navigation")
public class FrontNavigationController extends BaseController {

    @Resource
    private NavigationModel navigationModel;

    @ApiOperation("首页导航列表")
    @GetMapping("list")
    public JsonResult<List<Navigation>> list(HttpServletRequest request) {
        NavigationExample example = new NavigationExample();
        example.setIsShow(TplPcConst.IS_SHOW_YES);
        example.setOrderBy("sort asc");
        List<Navigation> list = navigationModel.getNavigationList(example, null);
        return SldResponse.success(list);
    }

}
