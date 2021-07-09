package com.slodon.b2b2c.controller.goods.seller;

import com.slodon.b2b2c.aop.VendorLogger;
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
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.goods.SellerGoodsSpecVO;
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

@Api(tags = "seller规格管理")
@RestController
@RequestMapping("v3/goods/seller/goodsSpec")
public class GoodsSpecSellerController {

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
    public JsonResult<PageVO<SellerGoodsSpecVO>> getList(HttpServletRequest request,
                                                         @RequestParam(value = "specName", required = false) String specName) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        GoodsSpecValueExample goodsSpecValueExample = new GoodsSpecValueExample();
        GoodsSpecExample goodsSpecExample = new GoodsSpecExample();
        goodsSpecExample.setSpecNameLike(specName);
        goodsSpecExample.setStoreIdOrZero(vendor.getStoreId());
        goodsSpecExample.setPager(pager);
        List<GoodsSpec> goodsSpecList = goodsSpecModel.getGoodsSpecList(goodsSpecExample, pager);
        List<SellerGoodsSpecVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsSpecList)) {
            goodsSpecList.forEach(spec -> {
                goodsSpecValueExample.setSpecId(spec.getSpecId());
                List<GoodsSpecValue> goodsSpecValueList = goodsSpecValueModel.getGoodsSpecValueList(goodsSpecValueExample, null);
                SellerGoodsSpecVO vo = new SellerGoodsSpecVO(spec, goodsSpecValueList);
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, goodsSpecExample.getPager()));
    }

    @ApiOperation("新增规格")
    @VendorLogger(option = "新增规格")
    @PostMapping("add")
    public JsonResult<Integer> addSpec(HttpServletRequest request, GoodsSpecAddDTO goodsSpecAddDTO) {
        // 验证参数是否为空
        AssertUtil.notNull(goodsSpecAddDTO, "规则信息不能为空，请重试！");
        AssertUtil.notEmpty(goodsSpecAddDTO.getSpecName(), "规则名称不能为空，请重试！");
        AssertUtil.notNull(goodsSpecAddDTO.getSort(), "规则排序不能为空，请重试！");
        AssertUtil.notNull(goodsSpecAddDTO.getState(), "规则启用状态不能为空，请重试！");

        //规格值范围检查
        AssertUtil.isTrue(goodsSpecAddDTO.getSort() < 0 || goodsSpecAddDTO.getSort() > 255, "规格排序值需在0～255内，请重试！");

        String logMsg = "规格名称" + goodsSpecAddDTO.getSpecName();
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        Integer specId = goodsSpecModel.saveGoodsSpecAndValue(vendor.getStoreId(), vendor.getVendorId().intValue(), vendor.getVendorName(), goodsSpecAddDTO);
        JsonResult<Integer> result = SldResponse.success("添加成功", logMsg);
        result.setData(specId);
        return result;
    }

    @ApiOperation("新增规格值")
    @VendorLogger(option = "新增规格值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "specId", value = "规则Id", paramType = "query"),
            @ApiImplicitParam(name = "specValues", value = "规格值,多个规格值用,分割", paramType = "query"),
    })
    @PostMapping("addSpecValue")
    public JsonResult<Integer> addSpecValue(HttpServletRequest request, @RequestParam(value = "specId", required = false) Integer specId,
                                            @RequestParam(value = "specValues", required = false) String specValues) {
        // 验证参数是否为空
        AssertUtil.notNullOrZero(specId, "规则Id不能为空，请重试！");
        AssertUtil.notEmpty(specValues, "规则值不能为空，请重试！");

        GoodsSpec goodsSpec = goodsSpecModel.getGoodsSpecBySpecId(specId);
        AssertUtil.notNull(goodsSpec, "未查询到对应规格，请重试！");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.isTrue(!vendor.getStoreId().equals(goodsSpec.getStoreId()), "没有权限操作");

        Integer specValueId = goodsSpecValueModel.saveGoodsSpecValue(vendor.getStoreId(), vendor.getVendorId().intValue(), specId, specValues);
        return SldResponse.success("添加成功", specValueId);
    }

    @ApiOperation("启用禁用规格")
    @VendorLogger(option = "启用禁用规格")
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
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.isTrue(!vendor.getStoreId().equals(goodsSpec.getStoreId()), "没有权限操作");
        if (state.equals(goodsSpec.getState())) {
            AssertUtil.isTrue(state == GoodsConst.IS_SPEC_YES, "规格已经启用");
            AssertUtil.isTrue(state == GoodsConst.IS_SPEC_NO, "规格已经禁用");
        }
        goodsSpec.setState(state);
        goodsSpecModel.updateGoodsSpec(goodsSpec);
        return SldResponse.success("编辑成功");
    }

    @ApiOperation("编辑规格")
    @VendorLogger(option = "编辑规格")
    @PostMapping("edit")
    public JsonResult editSpec(HttpServletRequest request, GoodsSpecUpdateDTO goodsSpecUpdateDTO) throws Exception {
        // 验证参数是否为空

        AssertUtil.notNull(goodsSpecUpdateDTO, "规格信息不能为空,请重试");
        AssertUtil.notNullOrZero(goodsSpecUpdateDTO.getSpecId(), "请选择要修改的数据");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        goodsSpecModel.updateGoodsSpecAndValue(vendor.getStoreId(), vendor.getVendorId().intValue(), vendor.getVendorName(), goodsSpecUpdateDTO);
        return SldResponse.success("编辑成功");
    }

    @ApiOperation("删除规格")
    @VendorLogger(option = "删除规格")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "specId", value = "规格Id", required = true)
    })
    @PostMapping("del")
    public JsonResult deleteSpec(HttpServletRequest request, @RequestParam("specId") Integer specId) {
        Vendor vendor =UserUtil.getUser(request,Vendor.class);
        AssertUtil.notNullOrZero(specId,"请选择要删除的数据");

        GoodsSpec goodsSpec = goodsSpecModel.getGoodsSpecBySpecId(specId);
        AssertUtil.notNull(goodsSpec, "规格id:"+specId+"详情为空!");
        AssertUtil.isTrue(!goodsSpec.getStoreId().equals(vendor.getStoreId()), "无操作权限，请重试！");
        goodsSpecModel.deleteSpecAndValue(specId);
        return SldResponse.success("删除规格成功");
    }
}
