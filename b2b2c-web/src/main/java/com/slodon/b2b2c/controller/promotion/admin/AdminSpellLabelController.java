package com.slodon.b2b2c.controller.promotion.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.SpellConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.promotion.SpellLabelModel;
import com.slodon.b2b2c.model.promotion.SpellModel;
import com.slodon.b2b2c.promotion.example.SpellExample;
import com.slodon.b2b2c.promotion.example.SpellLabelExample;
import com.slodon.b2b2c.promotion.pojo.Spell;
import com.slodon.b2b2c.promotion.pojo.SpellLabel;
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

@Api(tags = "admin-拼团标签")
@RestController
@Slf4j
@RequestMapping("v3/promotion/admin/spell/label")
public class AdminSpellLabelController extends BaseController {

    @Resource
    private SpellLabelModel spellLabelModel;
    @Resource
    private SpellModel spellModel;

    @ApiOperation("拼团标签列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spellLabelName", value = "标签名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<SpellLabel>> list(HttpServletRequest request, String spellLabelName) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        SpellLabelExample example = new SpellLabelExample();
        example.setSpellLabelNameLike(spellLabelName);
        List<SpellLabel> list = spellLabelModel.getSpellLabelList(example, pager);
        return SldResponse.success(new PageVO<>(list, pager));
    }

    @ApiOperation("新增拼团标签")
    @OperationLogger(option = "新增拼团标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spellLabelName", value = "标签名称", required = true, paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "排序", required = true, paramType = "query")
    })
    @PostMapping("add")
    public JsonResult addSpellLabel(HttpServletRequest request, String spellLabelName, Integer sort) {
        AssertUtil.sortCheck(sort);

        Admin admin = UserUtil.getUser(request, Admin.class);

        //拼团标签名称不可重复
        SpellLabelExample example = new SpellLabelExample();
        example.setSpellLabelName(spellLabelName);
        List<SpellLabel> list = spellLabelModel.getSpellLabelList(example, null);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(list), "拼团标签名称不可重复");

        SpellLabel spellLabel = new SpellLabel();
        spellLabel.setSpellLabelName(spellLabelName);
        spellLabel.setSort(sort);
        spellLabel.setIsShow(SpellConst.IS_SHOW_1);
        spellLabel.setCreateAdminId(admin.getAdminId());
        spellLabel.setCreateTime(new Date());
        spellLabelModel.saveSpellLabel(spellLabel);
        return SldResponse.success("新增成功");
    }

    @ApiOperation("编辑拼团标签")
    @OperationLogger(option = "编辑拼团标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spellLabelId", value = "标签id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "spellLabelName", value = "标签名称", required = true, paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "排序", required = true, paramType = "query")
    })
    @PostMapping("update")
    public JsonResult updateSpellLabel(HttpServletRequest request, Integer spellLabelId, String spellLabelName, Integer sort) {
        AssertUtil.sortCheck(sort);

        //拼团标签名称不可重复
        SpellLabelExample example = new SpellLabelExample();
        example.setSpellLabelName(spellLabelName);
        example.setSpellLabelIdNotEquals(spellLabelId);
        List<SpellLabel> list = spellLabelModel.getSpellLabelList(example, null);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(list), "拼团标签名称不可重复");

        SpellLabel spellLabel = new SpellLabel();
        spellLabel.setSpellLabelId(spellLabelId);
        spellLabel.setSpellLabelName(spellLabelName);
        spellLabel.setSort(sort);
        spellLabelModel.updateSpellLabel(spellLabel);
        return SldResponse.success("编辑成功");
    }

    @ApiOperation("是否显示拼团标签")
    @OperationLogger(option = "是否显示拼团标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spellLabelId", value = "标签id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isShow", value = "是否显示，0为不显示，1为显示", required = true, paramType = "query")
    })
    @PostMapping("isShow")
    public JsonResult isShow(HttpServletRequest request, Integer spellLabelId, Integer isShow) {
        SpellLabel spellLabel = new SpellLabel();
        spellLabel.setSpellLabelId(spellLabelId);
        spellLabel.setIsShow(isShow);
        spellLabelModel.updateSpellLabel(spellLabel);
        return SldResponse.success("修改成功");
    }

    @ApiOperation("删除拼团标签")
    @OperationLogger(option = "删除拼团标签")
    @PostMapping("del")
    public JsonResult delSpellLabel(HttpServletRequest request, Integer spellLabelId) {
        Admin admin = UserUtil.getUser(request, Admin.class);
        //使用中的标签不可以删除（标签下绑定有商品则代表该标签使用中）
        //查询正在进行或者未开始的拼团
        SpellExample spellExample = new SpellExample();
        spellExample.setSpellLabelId(spellLabelId);
        spellExample.setState(SpellConst.ACTIVITY_STATE_2);
        spellExample.setEndTimeAfter(new Date());
        List<Spell> spellList = spellModel.getSpellList(spellExample, null);
        if (!CollectionUtils.isEmpty(spellList)) {
            throw new MallException("标签名称为：" + spellList.get(0).getSpellLabelName() + "的标签已参与拼团活动,不能删除");
        }
        //删除标签
        spellLabelModel.deleteSpellLabel(spellLabelId);
        return SldResponse.success("删除成功");
    }
}
