package com.slodon.b2b2c.controller.promotion.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.PreSellConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.promotion.PresellLabelModel;
import com.slodon.b2b2c.promotion.example.PresellLabelExample;
import com.slodon.b2b2c.promotion.pojo.PresellLabel;
import com.slodon.b2b2c.system.pojo.Admin;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Api(tags = "admin-预售标签")
@RestController
@Slf4j
@RequestMapping("v3/promotion/admin/preSell/label")
public class AdminPreSellLabelController extends BaseController {

    @Resource
    private PresellLabelModel presellLabelModel;

    @ApiOperation("预售标签列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "presellLabelName", value = "标签名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<PresellLabel>> list(HttpServletRequest request, String presellLabelName) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        PresellLabelExample example = new PresellLabelExample();
        example.setPresellLabelNameLike(presellLabelName);
        List<PresellLabel> list = presellLabelModel.getPresellLabelList(example, pager);
        return SldResponse.success(new PageVO<>(list, pager));
    }

    @ApiOperation("新增预售标签")
    @OperationLogger(option = "新增预售标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "presellLabelName", value = "标签名称", required = true, paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "排序", required = true, paramType = "query")
    })
    @PostMapping("add")
    public JsonResult addSpellLabel(HttpServletRequest request, String presellLabelName, Integer sort) {
        AssertUtil.sortCheck(sort);

        Admin admin = UserUtil.getUser(request, Admin.class);

        //拼团标签名称不可重复
        PresellLabelExample example = new PresellLabelExample();
        example.setPresellLabelName(presellLabelName);
        List<PresellLabel> list = presellLabelModel.getPresellLabelList(example, null);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(list), "预售标签名称不可重复");

        PresellLabel label = new PresellLabel();
        label.setPresellLabelName(presellLabelName);
        label.setSort(sort);
        label.setIsShow(PreSellConst.IS_SHOW_1);
        label.setCreateUserId(admin.getAdminId());
        label.setCreateTime(new Date());
        presellLabelModel.savePresellLabel(label);
        return SldResponse.success("新增成功");
    }

    @ApiOperation("编辑预售标签")
    @OperationLogger(option = "编辑预售标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "presellLabelId", value = "标签id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "presellLabelName", value = "标签名称", required = true, paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "排序", required = true, paramType = "query")
    })
    @PostMapping("update")
    public JsonResult updateSpellLabel(HttpServletRequest request, Integer presellLabelId, String presellLabelName, Integer sort) {
        AssertUtil.sortCheck(sort);

        //拼团标签名称不可重复
        PresellLabelExample example = new PresellLabelExample();
        example.setPresellLabelName(presellLabelName);
        example.setPresellLabelIdNotEquals(presellLabelId);
        List<PresellLabel> list = presellLabelModel.getPresellLabelList(example, null);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(list), "预售标签名称不可重复");

        PresellLabel label = new PresellLabel();
        label.setPresellLabelId(presellLabelId);
        label.setPresellLabelName(presellLabelName);
        label.setSort(sort);
        presellLabelModel.updatePresellLabel(label);
        return SldResponse.success("编辑成功");
    }

    @ApiOperation("是否显示预售标签")
    @OperationLogger(option = "是否显示预售标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "presellLabelId", value = "标签id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isShow", value = "是否显示，0为不显示，1为显示", required = true, paramType = "query")
    })
    @PostMapping("isShow")
    public JsonResult isShow(HttpServletRequest request, Integer presellLabelId, Integer isShow) {
        PresellLabel label = new PresellLabel();
        label.setPresellLabelId(presellLabelId);
        label.setIsShow(isShow);
        presellLabelModel.updatePresellLabel(label);
        return SldResponse.success("修改成功");
    }

    @ApiOperation("删除预售标签")
    @OperationLogger(option = "删除预售标签")
    @PostMapping("del")
    public JsonResult delLabel(HttpServletRequest request, Integer presellLabelId) {
        presellLabelModel.deletePresellLabel(presellLabelId);
        return SldResponse.success("删除成功");
    }
}
