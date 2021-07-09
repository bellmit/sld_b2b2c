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
import com.slodon.b2b2c.goods.dto.GoodsAttributeAddDTO;
import com.slodon.b2b2c.goods.dto.GoodsAttributeUpdateDTO;
import com.slodon.b2b2c.goods.example.GoodsAttributeExample;
import com.slodon.b2b2c.goods.example.GoodsAttributeValueExample;
import com.slodon.b2b2c.goods.pojo.GoodsAttribute;
import com.slodon.b2b2c.goods.pojo.GoodsAttributeValue;
import com.slodon.b2b2c.model.goods.GoodsAttributeModel;
import com.slodon.b2b2c.model.goods.GoodsAttributeValueModel;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.vo.goods.GoodsAttributeVO;
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

@Api(tags = "admin商品属性管理")
@RestController
@RequestMapping("v3/goods/admin/goodsAttribute")
public class GoodsAttributeController {

    @Resource
    private GoodsAttributeModel goodsAttributeModel;
    @Resource
    private GoodsAttributeValueModel goodsAttributeValueModel;

    @ApiOperation("属性列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "attributeName", value = "属性名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<GoodsAttributeVO>> getList(HttpServletRequest request, String attributeName) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        GoodsAttributeValueExample goodsAttributeValueExample = new GoodsAttributeValueExample();
        GoodsAttributeExample goodsAttributeExample = new GoodsAttributeExample();
        goodsAttributeExample.setAttributeNameLike(attributeName);
        goodsAttributeExample.setPager(pager);
        List<GoodsAttribute> goodsAttributeList = goodsAttributeModel.getGoodsAttributeList(goodsAttributeExample, pager);
        List<GoodsAttributeVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsAttributeList)) {
            goodsAttributeList.forEach(attribute -> {
                goodsAttributeValueExample.setAttributeId(attribute.getAttributeId());
                List<GoodsAttributeValue> goodsAttributeValueList = goodsAttributeValueModel.getGoodsAttributeValueList(goodsAttributeValueExample, null);

                GoodsAttributeVO vo = new GoodsAttributeVO(attribute, goodsAttributeValueList);
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, goodsAttributeExample.getPager()));
    }

    @ApiOperation("获取可用属性列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "attributeName", value = "属性名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "1", paramType = "query")
    })
    @GetMapping("canUseList")
    public JsonResult<PageVO<GoodsAttributeVO>> canUseList(HttpServletRequest request,
                                                           @RequestParam(value = "attributeName", required = false) String attributeName) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        GoodsAttributeValueExample goodsAttributeValueExample = new GoodsAttributeValueExample();
        GoodsAttributeExample goodsAttributeExample = new GoodsAttributeExample();
        goodsAttributeExample.setAttributeNameLike(attributeName);
        goodsAttributeExample.setIsShow(GoodsConst.IS_ATTRIBUTE_YES);
        goodsAttributeExample.setPager(pager);
        List<GoodsAttribute> goodsAttributeList = goodsAttributeModel.getGoodsAttributeList(goodsAttributeExample, pager);
        List<GoodsAttributeVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsAttributeList)) {
            goodsAttributeList.forEach(attribute -> {
                goodsAttributeValueExample.setAttributeId(attribute.getAttributeId());
                List<GoodsAttributeValue> goodsAttributeValueList = goodsAttributeValueModel.getGoodsAttributeValueList(goodsAttributeValueExample, null);

                GoodsAttributeVO vo = new GoodsAttributeVO(attribute, goodsAttributeValueList);
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, goodsAttributeExample.getPager()));
    }

    @ApiOperation("新增属性")
    @OperationLogger(option = "新增属性")
    @PostMapping("add")
    public JsonResult<Integer> addAttribute(HttpServletRequest request, GoodsAttributeAddDTO goodsAttributeAddDTO) {
        String logMsg = "属性名称" + goodsAttributeAddDTO.getAttributeName();
        // 验证参数是否为空
        AssertUtil.notEmpty(goodsAttributeAddDTO.getAttributeName(), "属性名称不能为空，请重试！");
        AssertUtil.notEmpty(goodsAttributeAddDTO.getAttributeValues(), "属性值不能为空，请重试！");
        AssertUtil.isTrue(goodsAttributeAddDTO.getSort() < 0 || goodsAttributeAddDTO.getSort() > 255, "属性排序值需在0～255内，请重试！");

        Admin admin = UserUtil.getUser(request, Admin.class);
        goodsAttributeModel.saveGoodsAttributeAndValue(admin.getAdminId(), goodsAttributeAddDTO);
        return SldResponse.success("添加成功", logMsg);
    }


    @ApiOperation("启用禁用属性")
    @OperationLogger(option = "启用禁用属性")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "attributeId", value = "属性id", paramType = "query"),
            @ApiImplicitParam(name = "isShow", value = "是否展示：0-不展示，1-展示", paramType = "query"),
    })
    @PostMapping("changeState")
    public JsonResult changeState(HttpServletRequest request, @RequestParam(value = "attributeId", required = true) Integer attributeId,
                                  @RequestParam(value = "isShow", required = true) Integer isShow) throws Exception {
        // 验证参数是否为空
        AssertUtil.notNullOrZero(attributeId, "属性Id不能为空，请重试");
        AssertUtil.notNull(isShow, "展示状态不能为空，请重试");
        AssertUtil.isTrue(isShow != 1 && isShow != 0, "启用参数错误");
        GoodsAttribute goodsAttribute = goodsAttributeModel.getGoodsAttributeByAttributeId(attributeId);
        AssertUtil.notNull(goodsAttribute, "未查到对应属性");
        if (isShow.equals(goodsAttribute.getIsShow())) {
            AssertUtil.isTrue(isShow == GoodsConst.IS_ATTRIBUTE_YES, "属性已经启用");
            AssertUtil.isTrue(isShow == GoodsConst.IS_ATTRIBUTE_NO, "属性已经禁用");
        }
        goodsAttribute.setIsShow(isShow);
        goodsAttributeModel.updateGoodsAttribute(goodsAttribute);
        String logMsg = "属性Id" + isShow;
        return SldResponse.success("编辑成功", logMsg);
    }

    @ApiOperation("编辑属性")
    @OperationLogger(option = "编辑属性")
    @PostMapping("edit")
    public JsonResult editAttribute(HttpServletRequest request, GoodsAttributeUpdateDTO goodsAttributeUpdateDTO) throws Exception {
        String logMsg = "属性id" + goodsAttributeUpdateDTO.getAttributeId();
        // 验证属性参数是否为空
        AssertUtil.notNullOrZero(goodsAttributeUpdateDTO.getAttributeId(), "请选择要修改的数据");

        Admin admin = UserUtil.getUser(request, Admin.class);
        goodsAttributeModel.updateGoodsAttributeAndValue(admin.getAdminId(), goodsAttributeUpdateDTO);
        return SldResponse.success("编辑成功", logMsg);
    }

    @ApiOperation("删除属性")
    @OperationLogger(option = "删除属性")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "attributeId", value = "属性id", required = true)
    })
    @PostMapping("del")
    public JsonResult deleteAttribute(HttpServletRequest request, @RequestParam("attributeId") Integer attributeId) {
        AssertUtil.notNullOrZero(attributeId, "请选择要删除的数据");

        goodsAttributeModel.deleteGoodsAttributeAndValue(attributeId);
        return SldResponse.success("删除属性成功");
    }
}
