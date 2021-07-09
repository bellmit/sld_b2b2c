package com.slodon.b2b2c.controller.system.seller;

import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.constant.TplPcConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.system.TplPcMallDataModel;
import com.slodon.b2b2c.model.system.TplPcModel;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.system.example.TplPcExample;
import com.slodon.b2b2c.system.example.TplPcMallDataExample;
import com.slodon.b2b2c.system.pojo.TplPc;
import com.slodon.b2b2c.system.pojo.TplPcMallData;
import com.slodon.b2b2c.vo.system.TplPcMallDataVO;
import com.slodon.b2b2c.vo.system.TplPcVO;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "seller-pc端装修模板管理")
@RestController
@RequestMapping("v3/system/seller/tplPc")
public class SellerTplPcController extends BaseController {

    @Resource
    private TplPcModel tplPcModel;
    @Resource
    private TplPcMallDataModel tplPcMallDataModel;

    @ApiOperation("装修模板列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "模板类型", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "模板名称", paramType = "query"),
            @ApiImplicitParam(name = "isEnable", value = "是否启用，0==不启用；1==启用", paramType = "query"),
            @ApiImplicitParam(name = "isInstance", value = "是否可实例化 0==否，1==是", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<TplPcVO>> list(HttpServletRequest request, String type, String name,
                                            Integer isEnable, Integer isInstance) {
        TplPcExample example = new TplPcExample();
        example.setType(type);
        example.setNameLike(name);
        example.setIsEnable(isEnable);
        example.setIsInstance(isInstance);
        example.setClientIn(TplPcConst.CLIENT_SELLER + "," + TplPcConst.CLIENT_ALL);
        List<TplPc> list = tplPcModel.getTplPcList(example, null);
        List<TplPcVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(tplPc -> {
                vos.add(new TplPcVO(tplPc));
            });
        }
        return SldResponse.success(new PageVO<>(vos, null));
    }

    @ApiOperation("实例化模板列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tplId", value = "模板id", paramType = "query"),
            @ApiImplicitParam(name = "tplType", value = "模板类型", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "实例化模板名称", paramType = "query"),
            @ApiImplicitParam(name = "isEnable", value = "是否启用，0==不启用；1==启用", paramType = "query"),
            @ApiImplicitParam(name = "tplName", value = "模板风格", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("data/list")
    public JsonResult<PageVO<TplPcMallDataVO>> getTplData(HttpServletRequest request, Integer tplId, String tplType,
                                                          String name, Integer isEnable, String tplName) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        TplPcMallDataExample example = new TplPcMallDataExample();
        example.setStoreId(vendor.getStoreId());
        example.setTplPcId(tplId);
        example.setTplPcType(tplType);
        example.setNameLike(name);
        example.setTplPcNameLike(tplName);
        example.setIsEnable(isEnable);
        example.setOrderBy("sort asc, create_time desc");
        List<TplPcMallData> list = tplPcMallDataModel.getTplPcMallDataList(example, pager);
        List<TplPcMallDataVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(tplPcMallData -> {
                vos.add(new TplPcMallDataVO(tplPcMallData));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("添加实例化模板数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tplPcId", value = "装修模板id", required = true),
            @ApiImplicitParam(name = "name", value = "实例化模板名称"),
            @ApiImplicitParam(name = "html", value = "实例化装修模板(html片段)"),
            @ApiImplicitParam(name = "json", value = "装修模板数据(json)"),
            @ApiImplicitParam(name = "sort", value = "排序")
    })
    @VendorLogger(option = "添加实例化模板数据")
    @PostMapping("data/add")
    public JsonResult addTplData(HttpServletRequest request, Integer tplPcId, String name, String html, String json, Integer sort) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        TplPcMallData tplPcMallData = new TplPcMallData();
        tplPcMallData.setTplPcId(tplPcId);
        tplPcMallData.setName(name);
        tplPcMallData.setHtml(html);
        tplPcMallData.setJson(json);
        tplPcMallData.setSort(sort);
        tplPcMallData.setCreateTime(new Date());
        tplPcMallData.setCreateUserId(vendor.getVendorId());
        tplPcMallData.setIsEnable(TplPcConst.IS_ENABLE_NO);
        tplPcMallData.setStoreId(vendor.getStoreId());
        tplPcMallDataModel.saveTplPcMallData(tplPcMallData);
        return SldResponse.success("添加成功", "模板数据名称:" + tplPcMallData.getName());
    }

    @ApiOperation("编辑实例化模板数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataId", value = "实例化模板id", required = true),
            @ApiImplicitParam(name = "name", value = "实例化模板名称"),
            @ApiImplicitParam(name = "html", value = "实例化装修模板(html片段)"),
            @ApiImplicitParam(name = "json", value = "装修模板数据(json)"),
            @ApiImplicitParam(name = "sort", value = "排序")
    })
    @VendorLogger(option = "编辑实例化模板数据")
    @PostMapping("data/update")
    public JsonResult updateTplData(HttpServletRequest request, Integer dataId, String name, String html, String json, Integer sort) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        TplPcMallData tplPcMallData = new TplPcMallData();
        tplPcMallData.setDataId(dataId);
        tplPcMallData.setName(name);
        tplPcMallData.setHtml(html);
        tplPcMallData.setJson(json);
        tplPcMallData.setSort(sort);
        tplPcMallData.setUpdateTime(new Date());
        tplPcMallData.setUpdateUserId(vendor.getVendorId());
        tplPcMallDataModel.updateTplPcMallData(tplPcMallData);
        return SldResponse.success("修改成功", "模板数据ID:" + tplPcMallData.getDataId());
    }

    @ApiOperation("删除实例化模板数据")
    @VendorLogger(option = "删除实例化模板数据")
    @PostMapping("data/del")
    public JsonResult delTplData(HttpServletRequest request, Integer dataId) {
        tplPcMallDataModel.deleteTplPcMallData(dataId);
        return SldResponse.success("删除成功", "模板数据ID:" + dataId);
    }

    @ApiOperation("启/停用实例化模板数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataId", value = "模板数据id", required = true),
            @ApiImplicitParam(name = "isEnable", value = "启/停用[true==启用,false==停用]", required = true)
    })
    @VendorLogger(option = "启/停用实例化模板数据")
    @PostMapping("data/isEnable")
    public JsonResult isEnableTplData(HttpServletRequest request, Integer dataId, Boolean isEnable) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        TplPcMallData tplPcMallData = new TplPcMallData();
        tplPcMallData.setDataId(dataId);
        tplPcMallData.setUpdateTime(new Date());
        tplPcMallData.setUpdateUserId(vendor.getVendorId());
        if (isEnable) {
            tplPcMallData.setIsEnable(TplPcConst.IS_ENABLE_YES);
            tplPcMallDataModel.updateTplPcMallData(tplPcMallData);
            return SldResponse.success("启用成功", "模板数据id:" + dataId);
        } else {
            tplPcMallData.setIsEnable(TplPcConst.IS_ENABLE_NO);
            tplPcMallDataModel.updateTplPcMallData(tplPcMallData);
            return SldResponse.success("停用成功", "模板数据id:" + dataId);
        }
    }

    @ApiOperation("复制实例化模板数据")
    @VendorLogger(option = "复制实例化模板数据")
    @PostMapping("data/copy")
    public JsonResult copyTplData(HttpServletRequest request, Integer dataId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        TplPcMallData tplPcMallDataDb = tplPcMallDataModel.getTplPcMallDataByDataId(dataId);
        AssertUtil.notNull(tplPcMallDataDb, "实例化模板不存在，请新选择");

        TplPcMallData tplPcMallData = new TplPcMallData();
        tplPcMallData.setTplPcId(tplPcMallDataDb.getTplPcId());
        tplPcMallData.setName("copy-" + tplPcMallDataDb.getName());
        tplPcMallData.setCreateTime(new Date());
        tplPcMallData.setCreateUserId(vendor.getVendorId());
        tplPcMallData.setSort(tplPcMallDataDb.getSort());
        tplPcMallData.setIsEnable(TplPcConst.IS_ENABLE_NO);
        tplPcMallData.setHtml(tplPcMallDataDb.getHtml());
        tplPcMallData.setJson(tplPcMallDataDb.getJson());
        tplPcMallData.setStoreId(vendor.getStoreId());
        tplPcMallDataModel.saveTplPcMallData(tplPcMallData);
        return SldResponse.success("复制成功", "被复制的模板数据名称:" + tplPcMallDataDb.getName());
    }

    @ApiOperation("模板类型列表")
    @GetMapping("type/list")
    public JsonResult<List<TplPc>> getTplTypes(HttpServletRequest request) {
        String fields = "type, type_name";
        TplPcExample example = new TplPcExample();
        example.setClientIn(TplPcConst.CLIENT_MALL + "," + TplPcConst.CLIENT_ALL);
        example.setGroupBy("type, type_name");
        example.setOrderBy("type");
        List<TplPc> list = tplPcModel.getTplPcListFieldsByExample(fields, example);
        return SldResponse.success(list);
    }
}
