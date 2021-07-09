package com.slodon.b2b2c.controller.system.front;

import com.slodon.b2b2c.core.constant.ExpressConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.system.example.ExpressExample;
import com.slodon.b2b2c.model.system.ExpressModel;
import com.slodon.b2b2c.system.pojo.Express;
import com.slodon.b2b2c.vo.system.ExpressVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "front-物流公司")
@RestController
@RequestMapping("v3/system/front/express")
public class FrontExpressController extends BaseController {

    @Resource
    private ExpressModel expressModel;

    @ApiOperation("物流公司")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "expressName", value = "物流名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<ExpressVO>> list(HttpServletRequest request, String expressName) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        ExpressExample example = new ExpressExample();
        example.setExpressNameLike(expressName);
        example.setOrderBy("sort asc");
        example.setExpressState(ExpressConst.EXPRESS_STATE_OPEN);
        List<Express> list = expressModel.getExpressList(example, pager);
        List<ExpressVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(express -> {
                vos.add(new ExpressVO(express.getExpressId(), express.getExpressName()));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }
}
