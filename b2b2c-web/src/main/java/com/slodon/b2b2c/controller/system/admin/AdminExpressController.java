package com.slodon.b2b2c.controller.system.admin;

import com.slodon.b2b2c.core.constant.RedisConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.system.example.ExpressExample;
import com.slodon.b2b2c.model.system.ExpressModel;
import com.slodon.b2b2c.system.pojo.Express;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Api(tags = "admin-物流管理")
@RestController
@RequestMapping("v3/system/admin/express")
public class AdminExpressController extends BaseController {

    @Resource
    private ExpressModel expressModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("物流管理列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "expressName", value = "物流名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<Express>> list(HttpServletRequest request, String expressName) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        ExpressExample example = new ExpressExample();
        example.setExpressNameLike(expressName);
        List<Express> list = expressModel.getExpressList(example, pager);
        return SldResponse.success(new PageVO<>(list, pager));
    }

    @ApiOperation("添加物流公司")
    @OperationLogger(option = "添加物流公司")
    @PostMapping("add")
    public JsonResult addExpress(HttpServletRequest request, Express express) {
        expressModel.saveExpress(express);

        //更新成功，修改redis中的 express_company_update_time
        stringRedisTemplate.opsForValue().set(RedisConst.EXPRESS_COMPANY_UPDATE_TIME, String.valueOf(new Date().getTime()));

        return SldResponse.success("添加成功", "物流公司名称:" + express.getExpressName());
    }

    @ApiOperation("编辑物流公司")
    @OperationLogger(option = "编辑物流公司")
    @PostMapping("update")
    public JsonResult updateExpress(HttpServletRequest request, Express express) {
        expressModel.updateExpress(express);

        //更新成功，修改redis中的 express_company_update_time
        stringRedisTemplate.opsForValue().set(RedisConst.EXPRESS_COMPANY_UPDATE_TIME, String.valueOf(new Date().getTime()));

        return SldResponse.success("更新成功", "物流公司ID:" + express.getExpressId());
    }

    @ApiOperation("删除物流公司")
    @OperationLogger(option = "删除物流公司")
    @PostMapping("del")
    public JsonResult delExpress(HttpServletRequest request, Integer expressId) {
        expressModel.deleteExpress(expressId);

        //更新成功，修改redis中的 express_company_update_time
        stringRedisTemplate.opsForValue().set(RedisConst.EXPRESS_COMPANY_UPDATE_TIME, String.valueOf(new Date().getTime()));

        return SldResponse.success("删除成功", "快递公司ID:" + expressId);
    }
}
