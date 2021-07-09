package com.slodon.b2b2c.controller.promotion.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.SeckillConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.promotion.SeckillLabelModel;
import com.slodon.b2b2c.model.promotion.SeckillStageProductModel;
import com.slodon.b2b2c.promotion.dto.SeckillLabelAddDTO;
import com.slodon.b2b2c.promotion.dto.SeckillLabelEditDTO;
import com.slodon.b2b2c.promotion.example.SeckillLabelExample;
import com.slodon.b2b2c.promotion.example.SeckillStageProductExample;
import com.slodon.b2b2c.promotion.pojo.SeckillLabel;
import com.slodon.b2b2c.promotion.pojo.SeckillStageProduct;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.vo.promotion.SeckillLabelVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "admin-秒杀标签")
@RestController
@Slf4j
@RequestMapping("v3/promotion/admin/SeckillLabel")
public class AdminSeckillLabelController extends BaseController {

    @Resource
    private SeckillLabelModel seckillLabelModel;
    @Resource
    private SeckillStageProductModel seckillStageProductModel;

    @ApiOperation("秒杀标签列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "labelName", value = "标签名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<SeckillLabelVO>> getList(HttpServletRequest request, String labelName) {
        //分页设置
        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        //根据标签名称查询秒杀活动标签集合
        SeckillLabelExample example = new SeckillLabelExample();
        example.setLabelNameLike(labelName);
        List<SeckillLabel> list = seckillLabelModel.getSeckillLabelList(example, pager);
        ArrayList<SeckillLabelVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(seckillLabel -> {
                SeckillLabelVO vo = new SeckillLabelVO(seckillLabel);
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("获取秒杀活动标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "labelId", value = "秒杀标签id", required = true)
    })
    @GetMapping("getLabel")
    public JsonResult<SeckillLabel> getLabel(HttpServletRequest request, @RequestParam("labelId") Integer labelId) {
        return SldResponse.success(seckillLabelModel.getSeckillLabelByLabelId(labelId));
    }

    @ApiOperation("删除秒杀活动标签")
    @OperationLogger(option = "删除秒杀活动标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "labelIds", value = "秒杀标签id集合,用逗号隔开", required = true)
    })
    @PostMapping("deleteLabel")
    public JsonResult deleteLabel(HttpServletRequest request, String labelIds) {
        //参数校验
        AssertUtil.notEmpty(labelIds, "请选择要删除的数据");
        AssertUtil.notFormatFrontIds(labelIds, "labelIds格式错误,请重试");

        String[] labelIdArr = labelIds.split(",");
        for (String labelId : labelIdArr) {
            SeckillStageProductExample example = new SeckillStageProductExample();
            example.setLabelId(Integer.parseInt(labelId));
            List<SeckillStageProduct> seckillStageProductList = seckillStageProductModel.getSeckillStageProductList(example, null);
            SeckillLabel seckillLabelDb = seckillLabelModel.getSeckillLabelByLabelId(Integer.parseInt(labelId));
            AssertUtil.isTrue(!CollectionUtils.isEmpty(seckillStageProductList), "标签名称为：" + seckillLabelDb.getLabelName() + "的标签已参与秒杀不能删除");
        }
        seckillLabelModel.batchDeleteSeckillLabel(labelIds);
        return SldResponse.success("删除成功", "商品标签ids:" + labelIds);
    }

    @ApiOperation("新增秒杀标签")
    @OperationLogger(option = "新增秒杀标签")
    @PostMapping("addLabel")
    public JsonResult addLabel(HttpServletRequest request, SeckillLabelAddDTO seckillLabelAddDTO) throws Exception {
        AssertUtil.sortCheck(seckillLabelAddDTO.getSort());

        Admin admin = UserUtil.getUser(request, Admin.class);
        //验证参数是否为空
        AssertUtil.notEmpty(seckillLabelAddDTO.getLabelName(), "秒杀标签名称不能为空,请重试!");

        seckillLabelModel.saveSeckillLabel(seckillLabelAddDTO, admin.getAdminId());
        return SldResponse.success("保存成功", "商品标签名称:" + seckillLabelAddDTO.getLabelName());
    }

    @ApiOperation("编辑秒杀标签")
    @OperationLogger(option = "编辑秒杀标签")
    @PostMapping("edit")
    public JsonResult editLabel(HttpServletRequest request, SeckillLabelEditDTO seckillLabelEditDTO) throws Exception {
        AssertUtil.sortCheck(seckillLabelEditDTO.getSort());

        Admin admin = UserUtil.getUser(request, Admin.class);
        AssertUtil.notNullOrZero(seckillLabelEditDTO.getLabelId(), "请选择要修改的数据,请重试!");
        AssertUtil.notEmpty(seckillLabelEditDTO.getLabelName(), "秒杀标签名称不能为空,请重试!");

        seckillLabelModel.updateSeckillLabel(seckillLabelEditDTO, admin.getAdminId());
        return SldResponse.success("修改成功", "秒杀标签名称:" + seckillLabelEditDTO.getLabelName());
    }

    @ApiOperation("设置秒杀标签显示与否")
    @OperationLogger(option = "设置秒杀标签显示与否")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "labelId", value = "秒杀标签id", required = true),
            @ApiImplicitParam(name = "isShow", value = "是否显示 0-不显示 1-显示", required = true),
    })
    @PostMapping("setShowLabel")
    public JsonResult setShowLabel(HttpServletRequest request, @RequestParam("labelId") Integer labelId, @RequestParam("isShow") Integer isShow) throws Exception {

        AssertUtil.notNullOrZero(labelId, "请选择要修改的数据,请重试!");
        AssertUtil.notNull(isShow, "秒杀标签显示状态不能为空,请重试!");
        AssertUtil.isTrue(isShow != SeckillConst.IS_SHOW_YES && isShow != SeckillConst.IS_SHOW_NO, "显示状态值错误,请重试!");
        Admin admin = UserUtil.getUser(request, Admin.class);

        seckillLabelModel.setSeckillLabel(labelId, isShow, admin.getAdminId());
        return SldResponse.success("修改成功", "秒杀标签Id:" + labelId);
    }
}
