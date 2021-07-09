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
import com.slodon.b2b2c.model.system.TplPcMallDecoModel;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.system.example.TplPcMallDecoExample;
import com.slodon.b2b2c.system.pojo.TplPcMallDeco;
import com.slodon.b2b2c.vo.system.TplPcMallDecoDetailVO;
import com.slodon.b2b2c.vo.system.TplPcMallDecoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "seller-pc首页装修管理")
@RestController
@RequestMapping("v3/system/seller/pcDeco")
public class SellerPcDecoController extends BaseController {

    @Resource
    private TplPcMallDecoModel tplPcMallDecoModel;

    @ApiOperation("首页装修列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "decoType", value = "装修页类型", paramType = "query"),
            @ApiImplicitParam(name = "decoName", value = "装修页名称", paramType = "query"),
            @ApiImplicitParam(name = "isEnable", value = "是否启用，0==不启用；1==启用", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<TplPcMallDecoVO>> list(HttpServletRequest request,
                                                    @RequestParam(value = "decoType", required = false) String decoType,
                                                    @RequestParam(value = "decoName", required = false) String decoName,
                                                    @RequestParam(value = "isEnable", required = false) Integer isEnable) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        TplPcMallDecoExample example = new TplPcMallDecoExample();
        example.setDecoType(decoType);
        example.setDecoNameLike(decoName);
        example.setIsEnable(isEnable);
        example.setStoreId(vendor.getStoreId());
        List<TplPcMallDeco> list = tplPcMallDecoModel.getTplPcMallDecoList(example, pager);
        List<TplPcMallDecoVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(deco -> {
                vos.add(new TplPcMallDecoVO(deco));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("装修页详情")
    @GetMapping("display")
    public JsonResult<TplPcMallDecoDetailVO> display(@RequestParam(value = "decoId") Integer decoId) {
        TplPcMallDeco tplPcMallDeco = tplPcMallDecoModel.getTplPcMallDecoDetail(decoId);
        return SldResponse.success(new TplPcMallDecoDetailVO(tplPcMallDeco));
    }

    @ApiOperation("添加装修页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "decoType", value = "装修页类型", required = true),
            @ApiImplicitParam(name = "decoName", value = "装修页名称", required = true)
    })
    @VendorLogger(option = "添加装修页")
    @PostMapping("add")
    public JsonResult add(HttpServletRequest request, String decoType, String decoName) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        TplPcMallDeco tplPcMallDeco = new TplPcMallDeco();
        tplPcMallDeco.setDecoType(decoType);
        tplPcMallDeco.setDecoName(decoName);
        tplPcMallDeco.setMasterNavigationBarId(0);
        tplPcMallDeco.setMasterBannerId(0);
        tplPcMallDeco.setIsEnable(TplPcConst.IS_ENABLE_NO);
        tplPcMallDeco.setCreateUserId(vendor.getVendorId());
        tplPcMallDeco.setCreateUserName(vendor.getVendorName());
        tplPcMallDeco.setCreateTime(new Date());
        tplPcMallDeco.setUpdateUserId(vendor.getVendorId());
        tplPcMallDeco.setUpdateUserName(vendor.getVendorName());
        tplPcMallDeco.setUpdateTime(new Date());
        tplPcMallDeco.setStoreId(vendor.getStoreId());
        tplPcMallDecoModel.saveTplPcMallDeco(tplPcMallDeco);
        return SldResponse.success("添加成功", "装修页名称:" + tplPcMallDeco.getDecoName());
    }

    @ApiOperation("编辑装修页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "decoId", value = "装修页id", required = true),
            @ApiImplicitParam(name = "decoType", value = "装修页类型", required = true),
            @ApiImplicitParam(name = "decoName", value = "装修页名称", required = true),
            @ApiImplicitParam(name = "masterNavigationBarId", value = "装修页主导航条数据id"),
            @ApiImplicitParam(name = "masterBannerId", value = "装修页主轮播图数据id"),
            @ApiImplicitParam(name = "rankedTplDataIds", value = "有序模板数据id集合，按存储顺序确定位置")
    })
    @VendorLogger(option = "编辑装修页")
    @PostMapping("update")
    public JsonResult update(HttpServletRequest request, Integer decoId, String decoType, String decoName,
                             Integer masterNavigationBarId, Integer masterBannerId, String rankedTplDataIds) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        TplPcMallDeco tplPcMallDecoDb = tplPcMallDecoModel.getTplPcMallDecoByDecoId(decoId);
        AssertUtil.notNull(tplPcMallDecoDb, "该装修页不存在");

        TplPcMallDeco tplPcMallDeco = new TplPcMallDeco();
        tplPcMallDeco.setDecoId(decoId);
        tplPcMallDeco.setDecoType(decoType);
        tplPcMallDeco.setDecoName(decoName);
        tplPcMallDeco.setMasterNavigationBarId(masterNavigationBarId);
        tplPcMallDeco.setMasterBannerId(masterBannerId);
        tplPcMallDeco.setRankedTplDataIds(rankedTplDataIds);

        tplPcMallDeco.setUpdateUserId(vendor.getVendorId());
        tplPcMallDeco.setUpdateUserName(vendor.getVendorName());
        tplPcMallDeco.setUpdateTime(new Date());
        tplPcMallDecoModel.updateTplPcMallDeco(tplPcMallDeco);
        return SldResponse.success("修改成功", "装修页ID:" + tplPcMallDeco.getDecoId());
    }

    @ApiOperation("删除装修页")
    @VendorLogger(option = "删除装修页")
    @PostMapping("del")
    public JsonResult delTplData(HttpServletRequest request, Integer decoId) {
        tplPcMallDecoModel.deleteTplPcMallDeco(decoId);
        return SldResponse.success("删除成功", "装修页ID:" + decoId);
    }

    @ApiOperation("启/停用装修页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "decoId", value = "装修页id", required = true),
            @ApiImplicitParam(name = "isEnable", value = "启/停用[true==启用,false==停用]", required = true)
    })
    @VendorLogger(option = "启/停用装修页")
    @PostMapping("isEnable")
    public JsonResult isEnableDeco(HttpServletRequest request, Integer decoId, Boolean isEnable) {
        TplPcMallDeco tplPcMallDecoDb = tplPcMallDecoModel.getTplPcMallDecoByDecoId(decoId);
        AssertUtil.notNull(tplPcMallDecoDb, "该装修页不存在，请新选择");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        TplPcMallDeco tplPcMallDeco = new TplPcMallDeco();
        tplPcMallDeco.setDecoId(decoId);
        if (isEnable) {
            if (tplPcMallDecoDb.getDecoType().contains("index")) {
                //关闭其他开启的装修页
                TplPcMallDecoExample example = new TplPcMallDecoExample();
                example.setDecoType(tplPcMallDecoDb.getDecoType());
                example.setIsEnable(TplPcConst.IS_ENABLE_YES);
                example.setStoreId(vendor.getStoreId());
                TplPcMallDeco disableDeco = new TplPcMallDeco();
                disableDeco.setIsEnable(TplPcConst.IS_ENABLE_NO);
                tplPcMallDecoModel.updateTplPcMallDecoByExample(disableDeco,example);
            }
            tplPcMallDeco.setIsEnable(TplPcConst.IS_ENABLE_YES);
            tplPcMallDecoModel.updateTplPcMallDeco(tplPcMallDeco);
            return SldResponse.success("启用成功", "装修页ID:" + decoId);
        } else {
            if (TplPcConst.IS_ENABLE_YES == tplPcMallDecoDb.getIsEnable()) {
                AssertUtil.isTrue(tplPcMallDecoDb.getDecoType().contains("index"), "该装修页无法停用,请重试");
            }
            tplPcMallDeco.setIsEnable(TplPcConst.IS_ENABLE_NO);
            tplPcMallDecoModel.updateTplPcMallDeco(tplPcMallDeco);
            return SldResponse.success("停用成功", "装修页ID:" + decoId);
        }
    }

    @ApiOperation("复制装修页")
    @VendorLogger(option = "复制装修页")
    @PostMapping("copy")
    public JsonResult copyDeco(HttpServletRequest request, @RequestParam(value = "decoId") Integer decoId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        TplPcMallDeco tplPcMallDecoDb = tplPcMallDecoModel.getTplPcMallDecoByDecoId(decoId);
        AssertUtil.notNull(tplPcMallDecoDb, "该装修页不存在，请新选择");

        TplPcMallDeco deco = new TplPcMallDeco();
        deco.setDecoType(tplPcMallDecoDb.getDecoType());
        deco.setDecoName("copy-" + tplPcMallDecoDb.getDecoName());
        deco.setMasterNavigationBarId(tplPcMallDecoDb.getMasterNavigationBarId());
        deco.setMasterBannerId(tplPcMallDecoDb.getMasterBannerId());
        deco.setRankedTplDataIds(tplPcMallDecoDb.getRankedTplDataIds());
        deco.setIsEnable(TplPcConst.IS_ENABLE_NO);
        deco.setCreateUserId(vendor.getVendorId());
        deco.setCreateUserName(vendor.getVendorName());
        deco.setCreateTime(new Date());
        deco.setUpdateUserId(vendor.getVendorId());
        deco.setUpdateUserName(vendor.getVendorName());
        deco.setUpdateTime(new Date());
        deco.setStoreId(vendor.getStoreId());
        tplPcMallDecoModel.saveTplPcMallDeco(deco);
        return SldResponse.success("复制成功");
    }

}
