package com.slodon.b2b2c.controller.system.admin;

import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.system.example.TplMobileExample;
import com.slodon.b2b2c.system.example.TplMobileInnerExample;
import com.slodon.b2b2c.model.system.TplMobileInnerModel;
import com.slodon.b2b2c.model.system.TplMobileModel;
import com.slodon.b2b2c.system.pojo.TplMobile;
import com.slodon.b2b2c.system.pojo.TplMobileInner;
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
import java.util.List;

@Api(tags = "admin-mobile端装修模板管理")
@RestController
@RequestMapping("v3/system/admin/tplMobile")
public class AdminTplMobileController {

    @Resource
    private TplMobileModel tplMobileModel;
    @Resource
    private TplMobileInnerModel tplMobileInnerModel;

    @ApiOperation("装修模板列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "模板类型", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "模板名称", paramType = "query"),
            @ApiImplicitParam(name = "apply", value = "模板应用范围（商城首页==home,专题==topic,店铺首页==seller,活动==activity）", paramType = "query"),
            @ApiImplicitParam(name = "isEnable", value = "是否启用，0==不启用；1==启用", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<List<TplMobileInner>> list(HttpServletRequest request, String type, String name,
                                                 String apply, Integer isEnable) {
        TplMobileInnerExample example = new TplMobileInnerExample();
        example.setType(type);
        example.setNameLike(name);
        example.setApply(apply);
        example.setIsEnable(isEnable);
        List<TplMobileInner> list = tplMobileInnerModel.getTplMobileInnerList(example, null);
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(tplMobileInner -> {
                if (StringUtil.isEmpty(tplMobileInner.getImage())) {
                    tplMobileInner.setImage(null);
                } else {
                    tplMobileInner.setImage(DomainUrlUtil.SLD_STATIC_RESOURCES + tplMobileInner.getImage());
                }
            });
        }
        return SldResponse.success(list);
    }

    @ApiOperation("获取装修模板菜单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "模板类型", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "模板名称", paramType = "query"),
            @ApiImplicitParam(name = "apply", value = "模板应用范围（商城首页==home,专题==topic,店铺首页==seller,活动==activity）", paramType = "query"),
            @ApiImplicitParam(name = "isEnable", value = "是否展示 0--否，1--是", paramType = "query")
    })
    @GetMapping("menu")
    public JsonResult<List<TplMobile>> getMenu(HttpServletRequest request, String type, String name,
                                               String apply, Integer isEnable) {
        TplMobileExample example = new TplMobileExample();
        example.setType(type);
        example.setNameLike(name);
        example.setApply(apply);
        example.setIsUse(isEnable);
        List<TplMobile> list = tplMobileModel.getTplMobileList(example, null);
        return SldResponse.success(list);
    }


}
