package com.slodon.b2b2c.controller.seller.seller;

import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.constant.VendorConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.msg.StoreTplRoleBindModel;
import com.slodon.b2b2c.model.seller.VendorModel;
import com.slodon.b2b2c.model.seller.VendorResourcesModel;
import com.slodon.b2b2c.model.seller.VendorResourcesRolesModel;
import com.slodon.b2b2c.model.seller.VendorRolesModel;
import com.slodon.b2b2c.msg.example.StoreTplRoleBindExample;
import com.slodon.b2b2c.msg.pojo.StoreTplRoleBind;
import com.slodon.b2b2c.seller.example.VendorExample;
import com.slodon.b2b2c.seller.example.VendorResourcesExample;
import com.slodon.b2b2c.seller.example.VendorRolesExample;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.seller.pojo.VendorResources;
import com.slodon.b2b2c.seller.pojo.VendorRoles;
import com.slodon.b2b2c.vo.seller.VendorRolesVO;
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

@Api(tags = "seller-权限组管理")
@RestController
@RequestMapping("v3/seller/seller/roles")
public class SellerRolesController extends BaseController {

    @Resource
    private VendorRolesModel vendorRolesModel;
    @Resource
    private VendorResourcesModel vendorResourcesModel;
    @Resource
    private VendorResourcesRolesModel vendorResourcesRolesModel;
    @Resource
    private VendorModel vendorModel;
    @Resource
    private StoreTplRoleBindModel storeTplRoleBindModel;

    @ApiOperation("权限组列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "rolesName", value = "权限组名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<VendorRolesVO>> getList(HttpServletRequest request,
                                                     @RequestParam(value = "rolesName", required = false) String rolesName) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        VendorRolesExample rolesExample = new VendorRolesExample();
        rolesExample.setRolesNameLike(rolesName);
        rolesExample.setStoreId(vendor.getStoreId());
        rolesExample.setPager(pager);
        List<VendorRoles> vendorRolesList = vendorRolesModel.getVendorRolesList(rolesExample, pager);
        List<VendorRolesVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(vendorRolesList)) {
            for (VendorRoles vendorRoles : vendorRolesList) {
                VendorRolesVO vo = new VendorRolesVO(vendorRoles);
                //查三级资源id
                VendorResourcesExample example = new VendorResourcesExample();
                example.setRoleId(vendorRoles.getRolesId());
                example.setGrade(VendorConst.RESOURCE_GRADE_3);
                example.setOrderBy("resources_id asc");
                List<VendorResources> resourcesList = vendorResourcesModel.getVendorResourcesList(example, null);
                List<Integer> resourcesIds = new ArrayList<>();
                if (!CollectionUtils.isEmpty(resourcesList)) {
                    resourcesList.forEach(vendorResources -> {
                        resourcesIds.add(vendorResources.getResourcesId());
                    });
                }
                vo.setResourcesList(resourcesIds);

                //查询权限绑定消息
                StoreTplRoleBindExample roleBindExample = new StoreTplRoleBindExample();
                roleBindExample.setStoreId(vendor.getStoreId());
                roleBindExample.setRoleId(vendorRoles.getRolesId());
                List<StoreTplRoleBind> tplRoleBindList = storeTplRoleBindModel.getStoreTplRoleBindList(roleBindExample, null);
                if (!CollectionUtils.isEmpty(tplRoleBindList)) {
                    StoreTplRoleBind storeTplRoleBind = tplRoleBindList.get(0);
                    vo.setMsgList(StringUtil.stringList(storeTplRoleBind.getTplCodes()));
                }
                vos.add(vo);
            }
        }
        return SldResponse.success(new PageVO<>(vos, rolesExample.getPager()));
    }

    @ApiOperation("添加权限组")
    @VendorLogger(option = "添加权限组")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "rolesName", value = "权限组名称", required = true),
            @ApiImplicitParam(name = "description", value = "权限组描述", required = true)
    })
    @PostMapping("add")
    public JsonResult<Integer> addVendorRoles(HttpServletRequest request,
                                              @RequestParam("rolesName") String rolesName,
                                              @RequestParam(value = "description") String description) {
        String logMsg = "权限组名称：" + rolesName;
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //添加角色信息
        VendorRoles vendorRolesInsert = new VendorRoles();
        vendorRolesInsert.setRolesName(rolesName);
        vendorRolesInsert.setStoreId(vendor.getStoreId());
        vendorRolesInsert.setContent(description);
        vendorRolesInsert.setCreateVendorId(vendor.getVendorId());
        vendorRolesInsert.setCreateVendorName(vendor.getVendorName());
        vendorRolesInsert.setCreateTime(new Date());
        vendorRolesInsert.setState(VendorConst.ROLE_STATE_1);
        vendorRolesInsert.setIsInner(VendorConst.IS_INNER_NO);
        vendorRolesModel.saveVendorRoles(vendorRolesInsert);
        return SldResponse.success("添加成功", logMsg);
    }

    @ApiOperation("编辑权限组")
    @VendorLogger(option = "编辑权限组")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "rolesId", value = "角色id", required = true),
            @ApiImplicitParam(name = "rolesName", value = "权限组名称", required = true),
            @ApiImplicitParam(name = "description", value = "权限组描述", required = true)
    })
    @PostMapping("update")
    public JsonResult updateVendorRoles(HttpServletRequest request,
                                        @RequestParam("rolesId") Integer rolesId,
                                        @RequestParam("rolesName") String rolesName,
                                        @RequestParam(value = "description") String description) {
        String logMsg = "角色id" + rolesId;
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        AssertUtil.notNullOrZero(rolesId, "角色id不能为空");
        //根据rolesId获取角色信息
        VendorRoles vendorRoles = vendorRolesModel.getVendorRolesByRolesId(rolesId);
        AssertUtil.notNull(vendorRoles, "获取角色信息失败，请重试");
        AssertUtil.isTrue(vendorRoles.getIsInner().equals(VendorConst.IS_INNER_YES), "系统内置数据不能修改");
        AssertUtil.isTrue(!vendor.getStoreId().equals(vendorRoles.getStoreId()), "非法操作");

        //修改权限组
        VendorRoles vendorRolesUpdate = new VendorRoles();
        vendorRolesUpdate.setRolesId(rolesId);
        vendorRolesUpdate.setRolesName(rolesName);
        vendorRolesUpdate.setContent(description);
        vendorRolesUpdate.setUpdateVendorId(vendor.getVendorId());
        vendorRolesUpdate.setUpdateVendorName(vendor.getVendorName());
        vendorRolesUpdate.setUpdateTime(new Date());
        vendorRolesModel.updateVendorRoles(vendorRolesUpdate);
        return SldResponse.success("修改成功", logMsg);
    }

    @ApiOperation("删除权限组")
    @VendorLogger(option = "删除权限组")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "rolesId", value = "角色id", required = true)
    })
    @PostMapping("delete")
    public JsonResult deleteVendorRoles(HttpServletRequest request, @RequestParam("rolesId") Integer rolesId) {
        String logMsg = "角色id:" + rolesId;
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //根据角色id查询角色信息
        VendorRoles vendorRoles = vendorRolesModel.getVendorRolesByRolesId(rolesId);
        AssertUtil.notNull(vendorRoles, "获取权限组信息失败，请重试");
        AssertUtil.isTrue(vendorRoles.getIsInner().equals(VendorConst.IS_INNER_YES), "系统内置数据不能删除");
        AssertUtil.isTrue(!vendor.getStoreId().equals(vendorRoles.getStoreId()), "非法操作");

        //角色是否绑定账号，如果绑定则不能删除
        VendorExample vendorExample = new VendorExample();
        vendorExample.setRolesId(rolesId);
        List<Vendor> vendorList = vendorModel.getVendorList(vendorExample, null);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(vendorList), "该角色绑定了账号，不能删除");

        vendorRolesModel.deleteVendorRoles(rolesId);
        return SldResponse.success("删除成功", logMsg);
    }

    @ApiOperation("权限组添加资源")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色id", required = true),
            @ApiImplicitParam(name = "resourceIds", value = "3级资源id串，如 12,3,14", required = true)
    })
    @PostMapping("saveRoleResource")
    public JsonResult saveRoleResource(HttpServletRequest request, Integer roleId, String resourceIds) {
        AssertUtil.notEmpty(resourceIds, "请选择权限资源");
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //查询数据库中的角色信息
        VendorRoles vendorRolesDb = vendorRolesModel.getVendorRolesByRolesId(roleId);
        AssertUtil.notNull(vendorRolesDb, "权限组不存在");
        AssertUtil.isTrue(vendorRolesDb.getIsInner() == VendorConst.IS_INNER_YES, "系统内置角色不可修改");

        vendorResourcesRolesModel.addVendorResourcesRoles(roleId, resourceIds);
        return SldResponse.success("添加成功");
    }

}
