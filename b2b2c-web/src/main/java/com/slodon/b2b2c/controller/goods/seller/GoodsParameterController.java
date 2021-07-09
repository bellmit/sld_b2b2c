package com.slodon.b2b2c.controller.goods.seller;

import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.dto.GoodsParameterAddDTO;
import com.slodon.b2b2c.goods.dto.GoodsParameterUpdateDTO;
import com.slodon.b2b2c.goods.example.GoodsParameterExample;
import com.slodon.b2b2c.goods.pojo.GoodsParameter;
import com.slodon.b2b2c.model.goods.GoodsParameterModel;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.goods.GoodsParameterVO;
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

@Api(tags = "seller-自定义属性")
@RestController
@Slf4j
@RequestMapping("v3/goods/seller/goodsParameter")
public class GoodsParameterController extends BaseController {

    @Resource
    private GoodsParameterModel goodsParameterModel;

    @ApiOperation("新增自定义属性")
    @VendorLogger(option = "新增自定义属性")
    @PostMapping("add")
    public JsonResult<Integer> addGoodsParameter(HttpServletRequest request, GoodsParameterAddDTO goodsParameterAddDTO) throws Exception {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        //验证参数
        AssertUtil.notNull(goodsParameterAddDTO,"自定义属性不能为空,请重试!");
        AssertUtil.notEmpty(goodsParameterAddDTO.getParameterName(),"自定义属性名称不能为空,请重试!");
        AssertUtil.notEmpty(goodsParameterAddDTO.getParameterValue(),"自定义属性值不能为空,请重试!");
        AssertUtil.notNullOrZero(goodsParameterAddDTO.getSort(),"排序不能为空,请重试!");

        goodsParameterModel.saveGoodsParameter(goodsParameterAddDTO, vendor);
        return SldResponse.success("添加成功", goodsParameterAddDTO.getParameterName());
    }

    @ApiOperation("删除自定义属性")
    @VendorLogger(option = "删除自定义属性")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parametersIds", value = "自定义属性id集合,用逗号隔开", required = true)
    })
    @PostMapping("del")
    public JsonResult deleteGoodsParameter(HttpServletRequest request, String parametersIds) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        //参数校验
        AssertUtil.notEmpty(parametersIds,"请选择要删除的数据");
        AssertUtil.notFormatFrontIds(parametersIds,"parametersIds格式错误,请重试");

        goodsParameterModel.deleteGoodsParameter(parametersIds,vendor.getStoreId());
        return SldResponse.success("删除成功");
    }

    @ApiOperation("编辑自定义属性")
    @VendorLogger(option = "编辑自定义属性")
    @PostMapping("edit")
    public JsonResult editGoodsParameter(HttpServletRequest request, GoodsParameterUpdateDTO goodsParameterUpdateDTO) throws Exception {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNull(goodsParameterUpdateDTO,"自定义属性不能为空");
        AssertUtil.notNullOrZero(goodsParameterUpdateDTO.getParameterId(),"请选择要修改的数据");

        goodsParameterModel.updateGoodsParameter(goodsParameterUpdateDTO,vendor.getStoreId());
        return SldResponse.success("编辑成功", goodsParameterUpdateDTO.getParameterName());
    }

    @ApiOperation("自定义属性列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupId", value = "分组id", required = true),
            @ApiImplicitParam(name = "parameterName", value = "属性名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<GoodsParameterVO>> getList(HttpServletRequest request,
                                                        @RequestParam(value = "parameterName", required = false) String parameterName,
                                                        @RequestParam(value = "groupId") Integer groupId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        //分页设置
        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        //根据条件查询集合
        GoodsParameterExample example = new GoodsParameterExample();
        example.setStoreId(vendor.getStoreId());
        example.setParameterNameLike(parameterName);
        example.setGroupId(groupId);
        List<GoodsParameter> list = goodsParameterModel.getGoodsParameterList(example, pager);

        //响应
        List<GoodsParameterVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(goodsParameter -> {
                GoodsParameterVO vo = new GoodsParameterVO(goodsParameter);
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("可用的自定义属性列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupId", value = "分组id", required = true),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("canUseList")
    public JsonResult<PageVO<GoodsParameterVO>> canUseList(HttpServletRequest request,
                                                           @RequestParam(value = "groupId") Integer groupId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        //分页设置
        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        //根据条件查询集合
        GoodsParameterExample example = new GoodsParameterExample();
        example.setGroupId(groupId);
        example.setStoreId(vendor.getStoreId());
        example.setIsShow(GoodsConst.IS_ATTRIBUTE_YES);
        example.setOrderBy("sort asc,create_time desc");
        List<GoodsParameter> list = goodsParameterModel.getGoodsParameterList(example, pager);

        //响应
        List<GoodsParameterVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(goodsParameter -> {
                GoodsParameterVO vo = new GoodsParameterVO(goodsParameter);
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }
}
