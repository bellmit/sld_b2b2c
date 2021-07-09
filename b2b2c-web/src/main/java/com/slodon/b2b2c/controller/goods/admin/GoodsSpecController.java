package com.slodon.b2b2c.controller.goods.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.dto.GoodsSpecAddDTO;
import com.slodon.b2b2c.goods.dto.GoodsSpecUpdateDTO;
import com.slodon.b2b2c.goods.example.GoodsSpecExample;
import com.slodon.b2b2c.goods.example.GoodsSpecValueExample;
import com.slodon.b2b2c.goods.pojo.GoodsSpec;
import com.slodon.b2b2c.goods.pojo.GoodsSpecValue;
import com.slodon.b2b2c.model.goods.GoodsSpecModel;
import com.slodon.b2b2c.model.goods.GoodsSpecValueModel;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.vo.goods.GoodsSpecVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "admin规格管理")
@RestController
@RequestMapping("v3/goods/admin/goodsSpec")
public class GoodsSpecController {

    @Resource
    private GoodsSpecModel goodsSpecModel;
    @Resource
    private GoodsSpecValueModel goodsSpecValueModel;

    @ApiOperation("规格列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "specName", value = "规则名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<GoodsSpecVO>> getList(HttpServletRequest request,
                                                   @RequestParam(value = "specName", required = false) String specName) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        GoodsSpecExample goodsSpecExample = new GoodsSpecExample();
        goodsSpecExample.setSpecNameLike(specName);
        goodsSpecExample.setPager(pager);
        goodsSpecExample.setStoreId(0L);
        List<GoodsSpec> goodsSpecList = goodsSpecModel.getGoodsSpecList(goodsSpecExample, pager);
        List<GoodsSpecVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsSpecList)) {
            GoodsSpecValueExample goodsSpecValueExample = new GoodsSpecValueExample();
            goodsSpecList.forEach(spec -> {
                goodsSpecValueExample.setSpecId(spec.getSpecId());
                List<GoodsSpecValue> goodsSpecValueList = goodsSpecValueModel.getGoodsSpecValueList(goodsSpecValueExample, null);
                GoodsSpecVO vo = new GoodsSpecVO(spec, goodsSpecValueList);
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, goodsSpecExample.getPager()));
    }

    @ApiOperation("新增规格")
    @OperationLogger(option = "新增规格")
    @PostMapping("add")
    public JsonResult<Integer> addSpec(HttpServletRequest request, GoodsSpecAddDTO goodsSpecAddDTO) {
        // 验证参数是否为空
        AssertUtil.notNull(goodsSpecAddDTO, "规则信息不能为空，请重试！");
        AssertUtil.notEmpty(goodsSpecAddDTO.getSpecName(), "规则名称不能为空，请重试！");
        AssertUtil.notNullOrZero(goodsSpecAddDTO.getSort(), "规则排序不能为空，请重试！");
        AssertUtil.notNullOrZero(goodsSpecAddDTO.getState(), "规则启用状态不能为空，请重试！");
        // 规格排序值范围检查
        AssertUtil.isTrue(goodsSpecAddDTO.getSort() < 0 || goodsSpecAddDTO.getSort() > 255, "规格排序值需在0～255内，请重试！");

        String logMsg = "规格名称" + goodsSpecAddDTO.getSpecName();
        Admin admin = UserUtil.getUser(request, Admin.class);
        Integer specId = goodsSpecModel.saveGoodsSpecAndValue(0L, admin.getAdminId(), admin.getAdminName(), goodsSpecAddDTO);

        JsonResult<Integer> result = SldResponse.success("添加成功", logMsg);
        result.setData(specId);
        return result;
    }

    @ApiOperation("启用禁用规格")
    @OperationLogger(option = "启用禁用规格")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "specId", value = "规格id", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "是否展示：0-不展示，1-展示", paramType = "query"),
    })
    @PostMapping("changeState")
    public JsonResult changeState(HttpServletRequest request, @RequestParam(value = "specId", required = true) Integer specId,
                                  @RequestParam(value = "state", required = true) Integer state) throws Exception {
        // 验证参数是否为空
        AssertUtil.notNullOrZero(specId, "规则ID不能为空，请重试");
        AssertUtil.notNull(state, "状态不能为空，请重试");
        AssertUtil.isTrue(state != 1 && state != 0, "启用参数错误");
        GoodsSpec goodsSpec = goodsSpecModel.getGoodsSpecBySpecId(specId);
        AssertUtil.notNull(goodsSpec, "未查到对应规格");
        if (state.equals(goodsSpec.getState())) {
            AssertUtil.isTrue(state == GoodsConst.IS_SPEC_YES, "规格已经启用");
            AssertUtil.isTrue(state == GoodsConst.IS_SPEC_NO, "规格已经禁用");
        }
        goodsSpec.setState(state);
        goodsSpecModel.updateGoodsSpec(goodsSpec);
        String logMsg = "规格id" + specId;
        return SldResponse.success("编辑成功", logMsg);
    }

    @ApiOperation("编辑规格")
    @OperationLogger(option = "编辑规格")
    @PostMapping("edit")
    public JsonResult editSpec(HttpServletRequest request, GoodsSpecUpdateDTO goodsSpecUpdateDTO) throws Exception {
        // 验证参数是否为空
        AssertUtil.notNull(goodsSpecUpdateDTO, "规则信息不能为空，请重试！");
        AssertUtil.notNullOrZero(goodsSpecUpdateDTO.getSpecId(), "请选择要修改的数据，请重试！");
        AssertUtil.notEmpty(goodsSpecUpdateDTO.getSpecName(), "规则名称不能为空，请重试！");
        AssertUtil.notEmpty(goodsSpecUpdateDTO.getSpecValues(), "规则值不能为空，请重试！");
        AssertUtil.notNullOrZero(goodsSpecUpdateDTO.getSort(), "规则排序不能为空，请重试！");
        AssertUtil.notNullOrZero(goodsSpecUpdateDTO.getState(), "规则启用状态不能为空，请重试！");
        // 规格排序值范围检查
        AssertUtil.isTrue(goodsSpecUpdateDTO.getSort() < 0 || goodsSpecUpdateDTO.getSort() > 255, "规格排序值需在0～255内，请重试！");

        String logMsg = "规格id" + goodsSpecUpdateDTO.getSpecId();
        Admin admin = UserUtil.getUser(request, Admin.class);
        goodsSpecModel.updateGoodsSpecAndValue(0L, admin.getAdminId(), admin.getAdminName(), goodsSpecUpdateDTO);
        return SldResponse.success("编辑成功", logMsg);
    }

    @ApiOperation("删除规格")
    @OperationLogger(option = "删除规格")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "specId", value = "规格Id", required = true)
    })
    @PostMapping("delete")
    public JsonResult deleteSpec(HttpServletRequest request, @RequestParam("specId") Integer specId) {
        AssertUtil.notNullOrZero(specId, "请选择要修改的数据，请重试！");

        String logMsg = "规格id" + specId;
        goodsSpecModel.deleteSpecAndValue(specId);
        return SldResponse.success("删除规格成功");
    }
}
