package com.slodon.b2b2c.controller.system.admin;

import com.slodon.b2b2c.core.constant.TplPcConst;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.system.example.TplMobileDecoExample;
import com.slodon.b2b2c.model.system.TplMobileDecoModel;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.system.pojo.TplMobileDeco;
import com.slodon.b2b2c.vo.system.TplMobileDecoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "admin-mobile首页装修管理")
@RestController
@RequestMapping("v3/system/admin/mobileDeco")
public class AdminMobileDecoController {

    @Resource
    private TplMobileDecoModel tplMobileDecoModel;

    @ApiOperation("首页装修列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "装修页类型", paramType = "query"),
            @ApiImplicitParam(name = "os", value = "适配设备(android ios h5 weixinXcx alipayXcx)", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "装修页名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<TplMobileDecoVO>> list(HttpServletRequest request, String type, String os, String name) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        TplMobileDecoExample tplMobileDecoExample = new TplMobileDecoExample();
        tplMobileDecoExample.setStoreId(0L);
        tplMobileDecoExample.setType(type);
        tplMobileDecoExample.setNameLike(name);
        if (!StringUtil.isEmpty(os)) {
            switch (os) {
                case "android":
                    tplMobileDecoExample.setAndroid(TplPcConst.IS_ENABLE_YES);
                    break;
                case "ios":
                    tplMobileDecoExample.setIos(TplPcConst.IS_ENABLE_YES);
                    break;
                case "h5":
                    tplMobileDecoExample.setH5(TplPcConst.IS_ENABLE_YES);
                    break;
                case "weixinXcx":
                    tplMobileDecoExample.setWeixinXcx(TplPcConst.IS_ENABLE_YES);
                    break;
                default:
                    break;
            }
        }
        List<TplMobileDeco> list = tplMobileDecoModel.getTplMobileDecoList(tplMobileDecoExample, pager);
        ArrayList<TplMobileDecoVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(deco -> {
                vos.add(new TplMobileDecoVO(deco));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("获取装修页详情")
    @GetMapping("detail")
    public JsonResult<TplMobileDecoVO> detail(HttpServletRequest request, Integer decoId) {
        TplMobileDeco tplMobileDecoDb = tplMobileDecoModel.getTplMobileDecoByDecoId(decoId);
        if (null == tplMobileDecoDb) {
            return SldResponse.fail("该模板不存在");
        }
        return SldResponse.success(new TplMobileDecoVO(tplMobileDecoDb));
    }

    @ApiOperation("添加移动端装修页")
    @OperationLogger(option = "添加移动端装修页")
    @PostMapping("add")
    public JsonResult add(HttpServletRequest request, TplMobileDeco tplMobileDeco) {
        Admin adminUser = UserUtil.getUser(request, Admin.class);

        tplMobileDeco.setStoreId(0L);
        tplMobileDeco.setAndroid(TplPcConst.IS_ENABLE_NO);
        tplMobileDeco.setIos(TplPcConst.IS_ENABLE_NO);
        tplMobileDeco.setH5(TplPcConst.IS_ENABLE_NO);
        tplMobileDeco.setWeixinXcx(TplPcConst.IS_ENABLE_NO);
        tplMobileDeco.setAlipayXcx(TplPcConst.IS_ENABLE_NO);
        tplMobileDeco.setCreateUserId(adminUser.getAdminId().longValue());
        tplMobileDeco.setCreateUserName(adminUser.getAdminName());
        tplMobileDeco.setCreateTime(new Date());
        tplMobileDecoModel.saveTplMobileDeco(tplMobileDeco);
        return SldResponse.success("添加成功", "装修页名称:" + tplMobileDeco.getName());
    }

    @ApiOperation("编辑移动端装修页")
    @OperationLogger(option = "编辑移动端装修页")
    @PostMapping("update")
    public JsonResult update(HttpServletRequest request, TplMobileDeco tplMobileDeco) {
        Admin adminUser = UserUtil.getUser(request, Admin.class);

        TplMobileDeco tplMobileDecoDb = tplMobileDecoModel.getTplMobileDecoByDecoId(tplMobileDeco.getDecoId());
        AssertUtil.notNull(tplMobileDecoDb, "该模板不存在");

        tplMobileDeco.setUpdateUserId(adminUser.getAdminId().longValue());
        tplMobileDeco.setUpdateUserName(adminUser.getAdminName());
        tplMobileDeco.setUpdateTime(new Date());
        tplMobileDecoModel.updateTplMobileDeco(tplMobileDeco);
        return SldResponse.success("修改成功", "装修页ID:" + tplMobileDeco.getDecoId());
    }

    @ApiOperation("删除移动端装修页")
    @OperationLogger(option = "删除移动端装修页")
    @PostMapping("del")
    public JsonResult delTplData(HttpServletRequest request, Integer decoId) {
        tplMobileDecoModel.deleteTplMobileDeco(decoId);
        return SldResponse.success("删除成功", "装修页ID:" + decoId);
    }

    @ApiOperation("启/停用移动端装修页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "decoId", value = "装修页id", required = true),
            @ApiImplicitParam(name = "isUse", value = "启/停用[1-启用,0-停用]", required = true),
            @ApiImplicitParam(name = "os", value = "设备类型 (android,ios,h5,weixinXcx,alipayXcx)", required = true)
    })
    @OperationLogger(option = "启/停用移动端装修页")
    @PostMapping("isUse")
    public JsonResult isEnableDeco(Integer decoId, Integer isUse, String os) throws InvocationTargetException, IllegalAccessException {
        if (null == isUse) {
            return SldResponse.badArgumentValue();
        }
        TplMobileDeco tplMobileDecoDb = tplMobileDecoModel.getTplMobileDecoByDecoId(decoId);
        AssertUtil.notNull(tplMobileDecoDb, "该装修页不存在，请新选择");

        TplMobileDeco tplMobileDeco = new TplMobileDeco();
        tplMobileDeco.setDecoId(decoId);
        if (isUse == TplPcConst.IS_ENABLE_YES) {
            tplMobileDecoModel.enable(decoId, 0L, os,tplMobileDecoDb.getType());
            return SldResponse.success("启用成功", "装修页ID:" + decoId);
        } else {
            tplMobileDecoModel.disable(decoId, 0L, os,tplMobileDecoDb.getType());
            return SldResponse.success("停用成功", "装修页ID:" + decoId);
        }
    }

    @ApiOperation("复制动端装装修页")
    @OperationLogger(option = "复制动端装装修页")
    @PostMapping("copy")
    public JsonResult copyDeco(HttpServletRequest request, @RequestParam(value = "decoId") Integer decoId) {
        Admin adminUser = UserUtil.getUser(request, Admin.class);

        TplMobileDeco tplMobileDecoDb = tplMobileDecoModel.getTplMobileDecoByDecoId(decoId);
        AssertUtil.notNull(tplMobileDecoDb, "该装修页不存在，请新选择");

        TplMobileDeco deco = new TplMobileDeco();
        deco.setName("copy-" + tplMobileDecoDb.getName());
        deco.setType(tplMobileDecoDb.getType());
        deco.setStoreId(0L);
        deco.setAndroid(TplPcConst.IS_ENABLE_NO);
        deco.setIos(TplPcConst.IS_ENABLE_NO);
        deco.setH5(TplPcConst.IS_ENABLE_NO);
        deco.setWeixinXcx(TplPcConst.IS_ENABLE_NO);
        deco.setAlipayXcx(TplPcConst.IS_ENABLE_NO);
        deco.setCreateUserId(adminUser.getAdminId().longValue());
        deco.setCreateUserName(adminUser.getAdminName());
        deco.setCreateTime(new Date());
        deco.setUpdateUserId(adminUser.getAdminId().longValue());
        deco.setUpdateUserName(adminUser.getAdminName());
        deco.setUpdateTime(new Date());
        deco.setData(tplMobileDecoDb.getData());
        deco.setShowTip(tplMobileDecoDb.getShowTip());
        tplMobileDecoModel.saveTplMobileDeco(deco);
        return SldResponse.success("复制成功", "被复制的装修页ID:" + decoId);
    }
}
