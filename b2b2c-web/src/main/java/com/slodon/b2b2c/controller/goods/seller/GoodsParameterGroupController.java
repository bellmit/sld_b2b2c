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
import com.slodon.b2b2c.goods.dto.GoodsParameterGroupAddDTO;
import com.slodon.b2b2c.goods.dto.GoodsParameterGroupUpdateDTO;
import com.slodon.b2b2c.goods.example.GoodsParameterExample;
import com.slodon.b2b2c.goods.example.GoodsParameterGroupExample;
import com.slodon.b2b2c.goods.pojo.GoodsParameter;
import com.slodon.b2b2c.goods.pojo.GoodsParameterGroup;
import com.slodon.b2b2c.model.goods.GoodsParameterGroupModel;
import com.slodon.b2b2c.model.goods.GoodsParameterModel;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.goods.GoodsParameterGroupVO;
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
import java.util.ArrayList;
import java.util.List;

@Api(tags = "seller-属性分组")
@RestController
@Slf4j
@RequestMapping("v3/goods/seller/goodsParameterGroup")
public class GoodsParameterGroupController extends BaseController {

    @Resource
    private GoodsParameterGroupModel goodsParameterGroupModel;
    @Resource
    private GoodsParameterModel goodsParameterModel;

    @ApiOperation("新增属性分组")
    @VendorLogger(option = "新增属性分组")
    @PostMapping("add")
    public JsonResult<Integer> addGoodsRelatedTemplate(HttpServletRequest request, GoodsParameterGroupAddDTO goodsParameterGroupAddDTO) throws Exception {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        //验证参数
        AssertUtil.notNull(goodsParameterGroupAddDTO,"属性分组不能为空,请重试!");
        AssertUtil.notEmpty(goodsParameterGroupAddDTO.getGroupName(),"分组名称不能为空,请重试!");
        goodsParameterGroupModel.saveGoodsParameterGroup(goodsParameterGroupAddDTO, vendor);
        return SldResponse.success("添加成功", goodsParameterGroupAddDTO.getGroupName());
    }

    @ApiOperation("删除属性分组")
    @VendorLogger(option = "删除属性分组")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupIds", value = "关属性分组id集合,用逗号隔开", required = true)
    })
    @PostMapping("del")
    public JsonResult deleteGoodsRelatedTemplate(HttpServletRequest request, String groupIds) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        //参数校验
        AssertUtil.notEmpty(groupIds, "请选择要删除的数据");
        AssertUtil.notFormatFrontIds(groupIds, "groupIds格式错误,请重试");
        goodsParameterGroupModel.deleteGoodsParameterGroup(groupIds, vendor.getStoreId());
        return SldResponse.success("删除成功");
    }

    @ApiOperation("编辑属性分组")
    @VendorLogger(option = "编辑属性分组")
    @PostMapping("edit")
    public JsonResult editGoodsRelatedTemplate(HttpServletRequest request, GoodsParameterGroupUpdateDTO goodsParameterGroupUpdateDTO) throws Exception {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        // 验证参数
        AssertUtil.notNull(goodsParameterGroupUpdateDTO, "属性分组不能为空");
        AssertUtil.notNullOrZero(goodsParameterGroupUpdateDTO.getGroupId(), "请选择要修改的数据");

        goodsParameterGroupModel.updateGoodsParameterGroup(goodsParameterGroupUpdateDTO, vendor.getStoreId());

        return SldResponse.success("编辑成功");
    }

    @ApiOperation("属性分组列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupName", value = "分组名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<GoodsParameterGroupVO>> getList(HttpServletRequest request, String groupName) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //分页设置
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        //根据条件查询集合
        GoodsParameterGroupExample example = new GoodsParameterGroupExample();
        example.setStoreId(vendor.getStoreId());
        example.setGroupNameLike(groupName);
        example.setOrderBy("sort asc, create_time desc");
        List<GoodsParameterGroup> list = goodsParameterGroupModel.getGoodsParameterGroupList(example, pager);
        List<GoodsParameterGroupVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(goodsParameterGroup -> {
                GoodsParameterGroupVO vo = new GoodsParameterGroupVO(goodsParameterGroup);
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("可用属性分组列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("canUseList")
    public JsonResult<PageVO<GoodsParameterGroupVO>> canUseList(HttpServletRequest request) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //分页设置
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        //根据条件查询集合
        GoodsParameterGroupExample example = new GoodsParameterGroupExample();
        example.setStoreId(vendor.getStoreId());
        example.setIsShow(GoodsConst.IS_ATTRIBUTE_YES);
        example.setOrderBy("sort asc, create_time desc");
        List<GoodsParameterGroup> list = goodsParameterGroupModel.getGoodsParameterGroupList(example, pager);
        List<GoodsParameterGroupVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(goodsParameterGroup -> {
                GoodsParameterExample parameterExample = new GoodsParameterExample();
                parameterExample.setStoreId(vendor.getStoreId());
                parameterExample.setGroupId(goodsParameterGroup.getGroupId());
                parameterExample.setIsShow(GoodsConst.IS_ATTRIBUTE_YES);
                List<GoodsParameter> parameterList = goodsParameterModel.getGoodsParameterList(parameterExample, null);
                if (CollectionUtils.isEmpty(parameterList)) {
                    pager.setRowsCount(pager.getRowsCount() - 1);
                } else {
                    GoodsParameterGroupVO vo = new GoodsParameterGroupVO(goodsParameterGroup);
                    vos.add(vo);
                }
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }
}
