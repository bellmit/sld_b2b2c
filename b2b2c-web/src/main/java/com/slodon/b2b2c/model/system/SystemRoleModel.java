package com.slodon.b2b2c.model.system;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.dao.read.system.AdminReadMapper;
import com.slodon.b2b2c.dao.read.system.SystemRoleReadMapper;
import com.slodon.b2b2c.dao.write.system.SystemRoleWriteMapper;
import com.slodon.b2b2c.system.example.AdminExample;
import com.slodon.b2b2c.system.example.SystemRoleExample;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.system.pojo.SystemRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class SystemRoleModel {

    @Resource
    private SystemRoleReadMapper systemRoleReadMapper;
    @Resource
    private SystemRoleWriteMapper systemRoleWriteMapper;
    @Resource
    private AdminReadMapper adminReadMapper;

    /**
     * 新增角色表
     *
     * @param systemRole
     * @return
     */
    public Integer saveSystemRole(SystemRole systemRole) {
        //角色名称查重
        SystemRoleExample example = new SystemRoleExample();
        example.setRoleName(systemRole.getRoleName());
        List<SystemRole> list = systemRoleReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(list)) {
            throw new MallException("权限组名称已存在！");
        }
        int count = systemRoleWriteMapper.insert(systemRole);
        if (count == 0) {
            throw new MallException("添加角色表失败，请重试");
        }
        return systemRole.getRoleId();
    }

    /**
     * 根据roleId删除角色表
     *
     * @param roleId roleId
     * @return
     */
    public Integer deleteSystemRole(Integer roleId) {
        if (StringUtils.isEmpty(roleId)) {
            throw new MallException("请选择要删除的数据");
        }

        //查询该角色是否绑定操作员
        AdminExample example = new AdminExample();
        example.setRoleId(roleId);
        List<Admin> list = adminReadMapper.listByExample(example);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(list), "该角色已绑定管理员,无法删除");

        int count = systemRoleWriteMapper.deleteByPrimaryKey(roleId);
        if (count == 0) {
            log.error("根据roleId：" + roleId + "删除角色表失败");
            throw new MallException("删除角色表失败,请重试");
        }
        return count;
    }

    /**
     * 根据roleId更新角色表
     *
     * @param systemRole
     * @return
     */
    public Integer updateSystemRole(SystemRole systemRole) {
        if (StringUtils.isEmpty(systemRole.getRoleId())) {
            throw new MallException("请选择要修改的数据");
        }
        //角色名称查重
        if (!StringUtils.isEmpty(systemRole.getRoleName())) {
            SystemRoleExample example = new SystemRoleExample();
            example.setRoleName(systemRole.getRoleName());
            example.setRoleIdNotEquals(systemRole.getRoleId());
            List<SystemRole> list = systemRoleReadMapper.listByExample(example);
            if (!CollectionUtils.isEmpty(list)) {
                throw new MallException("权限组名称重复，请重新输入");
            }
        }
        int count = systemRoleWriteMapper.updateByPrimaryKeySelective(systemRole);
        if (count == 0) {
            log.error("根据roleId：" + systemRole.getRoleId() + "更新角色表失败");
            throw new MallException("更新角色表失败,请重试");
        }
        return count;
    }

    /**
     * 根据roleId获取角色表详情
     *
     * @param roleId roleId
     * @return
     */
    public SystemRole getSystemRoleByRoleId(Integer roleId) {
        return systemRoleReadMapper.getByPrimaryKey(roleId);
    }

    /**
     * 根据条件获取角色表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<SystemRole> getSystemRoleList(SystemRoleExample example, PagerInfo pager) {
        List<SystemRole> systemRoleList;
        if (pager != null) {
            pager.setRowsCount(systemRoleReadMapper.countByExample(example));
            systemRoleList = systemRoleReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            systemRoleList = systemRoleReadMapper.listByExample(example);
        }
        return systemRoleList;
    }
}