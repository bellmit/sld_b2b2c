package com.slodon.b2b2c.controller.msg.seller;

import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.constant.MsgConst;
import com.slodon.b2b2c.core.constant.VendorConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.model.msg.StoreSettingModel;
import com.slodon.b2b2c.model.msg.StoreTplModel;
import com.slodon.b2b2c.model.msg.StoreTplRoleBindModel;
import com.slodon.b2b2c.model.msg.SystemTplTypeModel;
import com.slodon.b2b2c.msg.example.StoreSettingExample;
import com.slodon.b2b2c.msg.example.StoreTplExample;
import com.slodon.b2b2c.msg.example.StoreTplRoleBindExample;
import com.slodon.b2b2c.msg.example.SystemTplTypeExample;
import com.slodon.b2b2c.msg.pojo.StoreSetting;
import com.slodon.b2b2c.msg.pojo.StoreTpl;
import com.slodon.b2b2c.msg.pojo.StoreTplRoleBind;
import com.slodon.b2b2c.msg.pojo.SystemTplType;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.msg.StoreTplVO;
import com.slodon.b2b2c.vo.msg.SystemTplTypeVO;
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
import java.util.List;

@Api(tags = "seller-接收设置")
@RestController
@RequestMapping("v3/msg/seller/msg/setting")
public class SellerMsgSettingController extends BaseController {

    @Resource
    private StoreTplModel storeTplModel;
    @Resource
    private StoreSettingModel storeSettingModel;
    @Resource
    private SystemTplTypeModel systemTplTypeModel;
    @Resource
    private StoreTplRoleBindModel storeTplRoleBindModel;

    @ApiOperation("接收设置列表")
    @GetMapping("list")
    public JsonResult<List<StoreTplVO>> list(HttpServletRequest request) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        StoreTplExample example = new StoreTplExample();
        //不是超级管理员查询绑定权限
        if (vendor.getIsStoreAdmin().equals(VendorConst.IS_STORE_ADMIN_0)) {
            StoreTplRoleBindExample roleBindExample = new StoreTplRoleBindExample();
            roleBindExample.setStoreId(vendor.getStoreId());
            roleBindExample.setRoleId(vendor.getRolesId());
            List<StoreTplRoleBind> roleBindList = storeTplRoleBindModel.getStoreTplRoleBindList(roleBindExample, null);
            if (CollectionUtils.isEmpty(roleBindList)) {
                return SldResponse.success(new ArrayList<>());
            } else {
                StoreTplRoleBind storeTplRoleBind = roleBindList.get(0);
                example.setTplCodeIn("'" + storeTplRoleBind.getTplCodes().replace(",", "','") + "'");
            }
        }
        List<StoreTpl> list = storeTplModel.getStoreTplList(example, null);
        List<StoreTplVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(storeTpl -> {
                StoreTplVO vo = new StoreTplVO(storeTpl);
                StoreSettingExample settingExample = new StoreSettingExample();
                settingExample.setTplCode(storeTpl.getTplCode());
                settingExample.setStoreId(vendor.getStoreId());
                settingExample.setVendorId(vendor.getVendorId());
                List<StoreSetting> settingList = storeSettingModel.getStoreSettingList(settingExample, null);
                if (!CollectionUtils.isEmpty(settingList)) {
                    vo.setIsReceive(settingList.get(0).getIsReceive());
                } else {
                    //默认展示关闭状态
                    vo.setIsReceive(MsgConst.IS_OPEN_SWITCH_NO);
                }
                vos.add(vo);
            });
        }
        return SldResponse.success(vos);
    }

    @ApiOperation("是否接受设置")
    @VendorLogger(option = "是否接受设置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tplCode", value = "模板编码", required = true),
            @ApiImplicitParam(name = "isReceive", value = "是否接收 1是，0否", required = true)
    })
    @PostMapping("isReceive")
    public JsonResult isReceive(HttpServletRequest request, String tplCode, Integer isReceive) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        StoreSetting setting = new StoreSetting();
        setting.setTplCode(tplCode);
        setting.setStoreId(vendor.getStoreId());
        setting.setVendorId(vendor.getVendorId());
        setting.setIsReceive(isReceive);
        storeSettingModel.updateStoreSetting(setting);
        return SldResponse.success("设置成功");
    }

    @ApiOperation("权限组接收消息模板列表")
    @GetMapping("receiveList")
    public JsonResult<List<SystemTplTypeVO>> receiveList(HttpServletRequest request) {
        SystemTplTypeExample example = new SystemTplTypeExample();
        example.setTplSource(MsgConst.TPL_SOURCE_STORE);
        example.setOrderBy("sort asc");
        List<SystemTplType> list = systemTplTypeModel.getSystemTplTypeList(example, null);
        AssertUtil.notEmpty(list, "请配置消息模板分类-内置表");
        List<SystemTplTypeVO> vos = new ArrayList<>();
        list.forEach(tplType -> {
            vos.add(new SystemTplTypeVO(tplType));
        });
        return SldResponse.success(vos);
    }

    @VendorLogger(option = "权限组接收消息设置")
    @ApiOperation("权限组接收消息设置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "权限组id", required = true),
            @ApiImplicitParam(name = "tplCodes", value = "权限组消息模板code集合,用逗号隔开", required = true)
    })
    @PostMapping("roleReceiveSetting")
    public JsonResult roleReceiveSetting(HttpServletRequest request, Integer roleId, String tplCodes) {
        AssertUtil.notNullOrZero(roleId, "权限组id不能为空");
        AssertUtil.notEmpty(tplCodes, "消息模板code不能为空");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        StoreTplRoleBind roleBind = new StoreTplRoleBind();
        roleBind.setStoreId(vendor.getStoreId());
        roleBind.setRoleId(roleId);
        roleBind.setTplCodes(tplCodes);
        storeTplRoleBindModel.saveStoreTplRoleBind(roleBind);

        return SldResponse.success("设置成功", "权限组id：" + roleId);
    }
}
