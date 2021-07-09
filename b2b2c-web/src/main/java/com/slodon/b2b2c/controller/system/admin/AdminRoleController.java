package com.slodon.b2b2c.controller.system.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.AdminConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.system.SystemResourceModel;
import com.slodon.b2b2c.model.system.SystemResourceRoleModel;
import com.slodon.b2b2c.model.system.SystemRoleModel;
import com.slodon.b2b2c.system.example.SystemResourceExample;
import com.slodon.b2b2c.system.example.SystemRoleExample;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.system.pojo.SystemResource;
import com.slodon.b2b2c.system.pojo.SystemRole;
import com.slodon.b2b2c.vo.system.SystemRoleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

@Api(tags = "admin-权限组管理")
@RestController
@RequestMapping("v3/system/admin/system/role")
public class AdminRoleController extends BaseController {

    @Resource
    private SystemRoleModel systemRoleModel;
    @Resource
    private SystemResourceModel systemResourceModel;
    @Resource
    private SystemResourceRoleModel systemResourceRoleModel;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @ApiOperation("权限组列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleName", value = "权限组名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<SystemRoleVO>> list(HttpServletRequest request, String roleName) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        SystemRoleExample example = new SystemRoleExample();
        example.setRoleNameLike(roleName);
        List<SystemRole> list = systemRoleModel.getSystemRoleList(example, pager);
        ArrayList<SystemRoleVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(systemRole -> {
                SystemRoleVO vo = new SystemRoleVO(systemRole);
                //查询角色资源对应表,获取该角色拥有的资源列表
                SystemResourceExample resourceExample = new SystemResourceExample();
                resourceExample.setRoleId(systemRole.getRoleId());
                resourceExample.setGrade(AdminConst.RESOURCE_GRADE_3);
                resourceExample.setOrderBy("resource_id asc");
                List<SystemResource> resourceRoleList = systemResourceModel.getSystemResourceList(resourceExample, null);
                List<Integer> resourcesList = new ArrayList<>();
                if (!CollectionUtils.isEmpty(resourceRoleList)) {
                    resourceRoleList.forEach(resourceRole -> {
                        resourcesList.add(resourceRole.getResourceId());
                    });
                }
                vo.setResourcesList(resourcesList);
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("添加权限组")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleName", value = "权限组名称", required = true),
            @ApiImplicitParam(name = "description", value = "权限组描述", required = true)
    })
    @OperationLogger(option = "添加权限组")
    @PostMapping("add")
    public JsonResult save(HttpServletRequest request, String roleName, String description) {
        Admin adminUser = UserUtil.getUser(request, Admin.class);

        SystemRole systemRole = new SystemRole();
        systemRole.setRoleName(roleName);
        systemRole.setDescription(description);
        systemRole.setCreateAdminId(adminUser.getAdminId());
        systemRole.setCreateAdminName(adminUser.getAdminName());
        systemRole.setCreateTime(new Date());
        systemRole.setState(AdminConst.ROLE_STATE_1);
        systemRole.setIsInner(AdminConst.IS_INNER_NO);
        Integer roleId = systemRoleModel.saveSystemRole(systemRole);
        return SldResponse.success("添加成功", "权限组名称:" + systemRole.getRoleName());
    }

    @ApiOperation("编辑权限组")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "权限组id", required = true),
            @ApiImplicitParam(name = "roleName", value = "权限组名称", required = true),
            @ApiImplicitParam(name = "description", value = "权限组描述", required = true)
    })
    @OperationLogger(option = "编辑权限组")
    @PostMapping("update")
    public JsonResult update(HttpServletRequest request, Integer roleId, String roleName, String description) {
        Admin adminUser = UserUtil.getUser(request, Admin.class);

        //查询数据库中的角色信息
        SystemRole systemRoleDb = systemRoleModel.getSystemRoleByRoleId(roleId);
        AssertUtil.notNull(systemRoleDb, "权限组不存在");
        AssertUtil.isTrue(systemRoleDb.getIsInner() == AdminConst.IS_INNER_YES, "系统内置角色不可修改");

        SystemRole systemRole = new SystemRole();
        systemRole.setRoleId(roleId);
        systemRole.setRoleName(roleName);
        systemRole.setDescription(description);
        systemRole.setUpdateAdminId(adminUser.getAdminId());
        systemRole.setUpdateAdminName(adminUser.getAdminName());
        systemRole.setUpdateTime(new Date());
        systemRoleModel.updateSystemRole(systemRole);
        return SldResponse.success("修改成功", "角色ID:" + systemRole.getRoleId());
    }

    @ApiOperation("删除权限组")
    @OperationLogger(option = "删除权限组")
    @PostMapping("del")
    public JsonResult del(HttpServletRequest request, Integer roleId) {
        //查询数据库中的角色信息
        SystemRole systemRoleDb = systemRoleModel.getSystemRoleByRoleId(roleId);
        AssertUtil.notNull(systemRoleDb, "权限组不存在");
        AssertUtil.isTrue(systemRoleDb.getIsInner() == AdminConst.IS_INNER_YES, "系统内置角色不可删除");

        systemRoleModel.deleteSystemRole(roleId);
        return SldResponse.success("删除成功", "角色ID:" + roleId);
    }

    @ApiOperation("权限组添加资源")
    @OperationLogger(option = "权限组添加资源")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色id", required = true),
            @ApiImplicitParam(name = "resourceIds", value = "3级资源id串，如 12,3,14", required = true)
    })
    @PostMapping("saveRoleResource")
    public JsonResult saveRoleResource(HttpServletRequest request, Integer roleId, String resourceIds) {
        AssertUtil.notEmpty(resourceIds, "请选择资源");

        Admin adminUser = UserUtil.getUser(request, Admin.class);
        //授权时更新时间
        SystemRole systemRole = new SystemRole();
        systemRole.setRoleId(roleId);
        systemRole.setUpdateAdminId(adminUser.getAdminId());
        systemRole.setUpdateAdminName(adminUser.getAdminName());
        systemRole.setUpdateTime(new Date());
        systemRoleModel.updateSystemRole(systemRole);

        //查询数据库中的角色信息
        SystemRole systemRoleDb = systemRoleModel.getSystemRoleByRoleId(roleId);
        AssertUtil.notNull(systemRoleDb, "权限组不存在");
        AssertUtil.isTrue(systemRoleDb.getIsInner() == AdminConst.IS_INNER_YES, "系统内置角色不可修改");

        systemResourceRoleModel.saveSystemResourceRole(roleId, resourceIds);
        return SldResponse.success("添加成功");
    }

}
