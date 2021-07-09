package com.slodon.b2b2c.controller.promotion.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.LadderGroupConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.promotion.LadderGroupLabelModel;
import com.slodon.b2b2c.promotion.example.LadderGroupLabelExample;
import com.slodon.b2b2c.promotion.pojo.LadderGroupLabel;
import com.slodon.b2b2c.system.pojo.Admin;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Api(tags = "admin-阶梯团标签")
@RestController
@RequestMapping("v3/promotion/admin/ladderGroup/label")
public class AdminLadderGroupLabelController extends BaseController {

    @Resource
    private LadderGroupLabelModel ladderGroupLabelModel;

    @ApiOperation("阶梯团标签列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "labelName", value = "标签名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<LadderGroupLabel>> list(HttpServletRequest request, String labelName) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        LadderGroupLabelExample example = new LadderGroupLabelExample();
        example.setLabelNameLike(labelName);
        List<LadderGroupLabel> list = ladderGroupLabelModel.getLadderGroupLabelList(example, pager);
        return SldResponse.success(new PageVO<>(list, pager));
    }

    @ApiOperation("新增阶梯团标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "labelName", value = "标签名称", required = true, paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "排序", required = true, paramType = "query")
    })
    @OperationLogger(option = "新增阶梯团标签")
    @PostMapping("add")
    public JsonResult addLabel(HttpServletRequest request, String labelName, Integer sort) {
        AssertUtil.sortCheck(sort);

        Admin admin = UserUtil.getUser(request, Admin.class);

        //拼团标签名称不可重复
        LadderGroupLabelExample example = new LadderGroupLabelExample();
        example.setLabelName(labelName);
        List<LadderGroupLabel> list = ladderGroupLabelModel.getLadderGroupLabelList(example, null);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(list), "阶梯团标签名称不可重复");

        LadderGroupLabel label = new LadderGroupLabel();
        label.setLabelName(labelName);
        label.setSort(sort);
        label.setIsShow(LadderGroupConst.IS_SHOW_0);    //默认不显示
        label.setCreateAdminId(admin.getAdminId());
        label.setCreateTime(new Date());
        ladderGroupLabelModel.saveLadderGroupLabel(label);

        return SldResponse.success("新增成功", "标签名称:" + labelName);
    }

    @ApiOperation("编辑阶梯团标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "labelId", value = "标签id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "labelName", value = "标签名称", required = true, paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "排序", required = true, paramType = "query")
    })
    @OperationLogger(option = "编辑阶梯团标签")
    @PostMapping("update")
    public JsonResult updateLabel(HttpServletRequest request, Integer labelId, String labelName, Integer sort) {
        AssertUtil.sortCheck(sort);

        //拼团标签名称不可重复
        LadderGroupLabelExample example = new LadderGroupLabelExample();
        example.setLabelName(labelName);
        example.setLabelIdNotEquals(labelId);
        List<LadderGroupLabel> list = ladderGroupLabelModel.getLadderGroupLabelList(example, null);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(list), "阶梯团标签名称不可重复");

        LadderGroupLabel label = new LadderGroupLabel();
        label.setLabelId(labelId);
        label.setLabelName(labelName);
        label.setSort(sort);
        ladderGroupLabelModel.updateLadderGroupLabel(label);

        return SldResponse.success("编辑成功", "标签id:" + labelId);
    }

    @ApiOperation("是否显示阶梯团标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "labelId", value = "标签id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isShow", value = "是否显示，0为不显示，1为显示", required = true, paramType = "query")
    })
    @OperationLogger(option = "是否显示阶梯团标签")
    @PostMapping("isShow")
    public JsonResult isShow(HttpServletRequest request, Integer labelId, Integer isShow) {
        LadderGroupLabel label = new LadderGroupLabel();
        label.setLabelId(labelId);
        label.setIsShow(isShow);
        ladderGroupLabelModel.updateLadderGroupLabel(label);

        return SldResponse.success("修改成功", "标签id:" + labelId);
    }

    @ApiOperation("删除阶梯团标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "labelId", value = "标签id", required = true, paramType = "query")
    })
    @OperationLogger(option = "删除阶梯团标签")
    @PostMapping("del")
    public JsonResult delLabel(HttpServletRequest request, Integer labelId) {
        ladderGroupLabelModel.deleteLadderGroupLabel(labelId);

        return SldResponse.success("删除成功", "标签id:" + labelId);
    }
}
