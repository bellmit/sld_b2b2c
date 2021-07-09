package com.slodon.b2b2c.controller.system.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.AdminConst;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.user.UserAuthority;
import com.slodon.b2b2c.core.util.*;
import com.slodon.b2b2c.model.system.AdminModel;
import com.slodon.b2b2c.model.system.SystemRoleModel;
import com.slodon.b2b2c.system.dto.AdminAddDTO;
import com.slodon.b2b2c.system.dto.AdminUpdateDTO;
import com.slodon.b2b2c.system.example.AdminExample;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.system.pojo.SystemRole;
import com.slodon.b2b2c.vo.system.AdminVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
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
import java.util.UUID;

@Api(tags = "admin-操作员管理")
@RestController
@RequestMapping("v3/system/admin/adminUser")
public class AdminUserController {

    @Resource
    private AdminModel adminModel;
    @Resource
    private SystemRoleModel systemRoleModel;
    @Resource
    private RedisTemplate<String, Object> objectRedisTemplate;

    @ApiOperation("操作员管理列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "adminName", value = "账号", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<AdminVO>> list(HttpServletRequest request, String adminName) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        AdminExample example = new AdminExample();
        example.setAdminNameLike(adminName);
        example.setStateNotEquals(AdminConst.ADMIN_STATE_DEL);
        List<Admin> list = adminModel.getAdminList(example, pager);
        List<AdminVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(admin -> {
                vos.add(new AdminVO(admin));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("添加操作员")
    @OperationLogger(option = "添加操作员")
    @PostMapping("add")
    public JsonResult addAdmin(HttpServletRequest request, AdminAddDTO adminAddDTO) {
        AssertUtil.isTrue(!adminAddDTO.getPassword().equals(adminAddDTO.getConfirmPwd()), "确认密码跟密码不一致！");

        Admin adminUser = UserUtil.getUser(request, Admin.class);

        Admin admin = new Admin();
        BeanUtils.copyProperties(adminAddDTO, admin);
        admin.setPassword(Md5.getMd5String(adminAddDTO.getPassword()));
        admin.setState(AdminConst.ADMIN_STATE_NORM);
        admin.setIsSuper(AdminConst.IS_SUPER_ADMIN_NO);
        admin.setCreateAdminId(adminUser.getAdminId());
        admin.setCreateTime(new Date());
        if (!StringUtil.isNullOrZero(adminAddDTO.getRoleId())) {
            SystemRole systemRole = systemRoleModel.getSystemRoleByRoleId(adminAddDTO.getRoleId());
            AssertUtil.notNull(systemRole, "权限组不存在");

            admin.setRoleName(systemRole.getRoleName());
        }
        adminModel.saveAdmin(admin);
        return SldResponse.success("保存成功", "账号:" + admin.getAdminName());
    }

    @ApiOperation("编辑操作员")
    @OperationLogger(option = "编辑操作员")
    @PostMapping("update")
    public JsonResult updateAdmin(HttpServletRequest request, AdminUpdateDTO adminUpdateDTO) {

        Admin adminByAdminId = adminModel.getAdminByAdminId(adminUpdateDTO.getAdminId());
        AssertUtil.isTrue(null != adminByAdminId && "admin".equals(adminByAdminId.getAdminName()), "admin账号不可更改");

        Admin admin = new Admin();
        BeanUtils.copyProperties(adminUpdateDTO, admin);
        if (!StringUtil.isNullOrZero(adminUpdateDTO.getRoleId())) {
            SystemRole systemRole = systemRoleModel.getSystemRoleByRoleId(adminUpdateDTO.getRoleId());
            AssertUtil.notNull(systemRole, "权限组不存在");

            admin.setRoleName(systemRole.getRoleName());
        }
        //删除原来的资源权限
        objectRedisTemplate.delete("admin-" + adminByAdminId.getAdminId());
        //定义一个uuid，用做存储redis的key
        String uuid = UUID.randomUUID().toString();
        //获取redis中账号绑定的权限资源，进行清空
        UserAuthority<Admin> userAuthority = new UserAuthority<>();
        userAuthority.setT(adminByAdminId);
        userAuthority.addAuthority(null);
        objectRedisTemplate.opsForHash().put("admin-" + adminByAdminId.getAdminId(), uuid, userAuthority);
        adminModel.updateAdmin(admin);
        return SldResponse.success("修改成功", "操作员ID:" + admin.getAdminId());
    }

    @ApiOperation("冻结/解冻操作员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "adminId", value = "操作员id", required = true),
            @ApiImplicitParam(name = "isFreeze", value = "冻结/解冻[true==冻结,false==解冻]", required = true)
    })
    @OperationLogger(option = "冻结/解冻管理员")
    @PostMapping("isFreeze")
    public JsonResult freeze(HttpServletRequest request, Integer adminId, Boolean isFreeze) {
        Admin admin = adminModel.getAdminByAdminId(adminId);
        AssertUtil.isTrue(null != admin && "admin".equals(admin.getAdminName()), "admin账号不可更改");

        Admin adminNew = new Admin();
        adminNew.setAdminId(adminId);
        if (isFreeze) {
            AssertUtil.isTrue(admin.getState() == AdminConst.ADMIN_STATE_FREEZE, "该账号已被冻结");

            adminNew.setState(AdminConst.ADMIN_STATE_FREEZE);
            //删除原来的资源权限
            objectRedisTemplate.delete("admin-" + admin.getAdminId());
            //定义一个uuid，用做存储redis的key
            String uuid = UUID.randomUUID().toString();
            //获取redis中账号绑定的权限资源，进行清空
            UserAuthority<Admin> userAuthority = new UserAuthority<>();
            userAuthority.setT(admin);
            userAuthority.addAuthority(null);
            objectRedisTemplate.opsForHash().put("admin-" + admin.getAdminId(), uuid, userAuthority);
            adminModel.updateAdmin(adminNew);
            return SldResponse.success("冻结成功", "账号[" + admin.getAdminName() + "]已被冻结");

        } else {
            AssertUtil.isTrue(admin.getState() == AdminConst.ADMIN_STATE_NORM, "该账号非冻结状态");

            adminNew.setState(AdminConst.ADMIN_STATE_NORM);
            adminModel.updateAdmin(adminNew);
            return SldResponse.success("解冻成功", "账号[" + admin.getAdminName() + "]已被解冻");
        }
    }

    @ApiOperation("删除操作员")
    @OperationLogger(option = "删除操作员")
    @PostMapping("del")
    public JsonResult del(HttpServletRequest request, Integer adminId) {
        Admin admin = adminModel.getAdminByAdminId(adminId);
        AssertUtil.isTrue(null != admin && "admin".equals(admin.getAdminName()), "admin账号不可删除");

        //删除原来的资源权限
        objectRedisTemplate.delete("admin-" + admin.getAdminId());
        //定义一个uuid，用做存储redis的key
        String uuid = UUID.randomUUID().toString();
        //获取redis中账号绑定的权限资源，进行清空
        UserAuthority<Admin> userAuthority = new UserAuthority<>();
        userAuthority.setT(admin);
        userAuthority.addAuthority(null);
        objectRedisTemplate.opsForHash().put("admin-" + admin.getAdminId(), uuid, userAuthority);
        adminModel.deleteAdmin(adminId);
        return SldResponse.success("删除成功", "操作员ID:" + adminId);
    }

    @ApiOperation("修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldPassword", value = "旧密码", required = true),
            @ApiImplicitParam(name = "newPassword", value = "新密码", required = true),
            @ApiImplicitParam(name = "newPasswordCfm", value = "新密码确认", required = true)
    })
    @OperationLogger(option = "修改密码")
    @PostMapping("updatePwd")
    public JsonResult updatePwd(HttpServletRequest request, String oldPassword, String newPassword, String newPasswordCfm) {
        Admin adminUser = UserUtil.getUser(request, Admin.class);
        AssertUtil.isTrue(StringUtil.isEmpty(oldPassword) || StringUtil.isEmpty(newPassword) || StringUtil.isEmpty(newPasswordCfm), "密码不能为空，请重新输入");
        AssertUtil.isTrue(!newPassword.equals(newPasswordCfm), "新密码与确认密码不一致，请重新输入");
        AssertUtil.isTrue(oldPassword.equals(newPassword), "新密码与旧密码不能相同，请重新输入");

        Admin adminDb = adminModel.getAdminByAdminId(adminUser.getAdminId());
        String oldPasswordMd5 = Md5.getMd5String(oldPassword);
        AssertUtil.isTrue(!oldPasswordMd5.equals(adminDb.getPassword()), "旧密码输入错误，请重新输入");

        Admin adminNew = new Admin();
        adminNew.setAdminId(adminUser.getAdminId());
        adminNew.setPassword(Md5.getMd5String(newPassword));
        adminModel.updateAdmin(adminNew);
        return SldResponse.success("修改成功，请重新登录", "管理员名称:" + adminDb.getAdminName());
    }

    @ApiOperation("重置密码")
    @OperationLogger(option = "重置密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "adminId", value = "管理员id", required = true),
            @ApiImplicitParam(name = "newPassword", value = "新密码", required = true),
            @ApiImplicitParam(name = "newPasswordCfm", value = "确认新密码", required = true)
    })
    @PostMapping("resetPassword")
    public JsonResult resetPassword(HttpServletRequest request, Integer adminId, String newPassword, String newPasswordCfm) {
        AssertUtil.isTrue(!newPassword.equals(newPasswordCfm), "密码输入不一致,请重试!");

//        Admin admin = adminModel.getAdminByAdminId(adminId);

        Admin adminNew = new Admin();
        adminNew.setAdminId(adminId);
        adminNew.setPassword(Md5.getMd5String(newPassword));
        adminModel.updateAdmin(adminNew);

        return SldResponse.success("重置成功");
    }
}
