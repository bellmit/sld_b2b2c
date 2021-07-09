package com.slodon.b2b2c.model.system;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.system.SystemResourceReadMapper;
import com.slodon.b2b2c.dao.read.system.SystemResourceRoleReadMapper;
import com.slodon.b2b2c.dao.write.system.SystemResourceRoleWriteMapper;
import com.slodon.b2b2c.system.example.SystemResourceExample;
import com.slodon.b2b2c.system.example.SystemResourceRoleExample;
import com.slodon.b2b2c.system.pojo.SystemResource;
import com.slodon.b2b2c.system.pojo.SystemResourceRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Component
@Slf4j
public class SystemResourceRoleModel {

    @Resource
    private SystemResourceRoleReadMapper systemResourceRoleReadMapper;
    @Resource
    private SystemResourceRoleWriteMapper systemResourceRoleWriteMapper;
    @Resource
    private SystemResourceReadMapper systemResourceReadMapper;

    /**
     * 新增角色资源对应表
     *
     * @param systemResourceRole
     * @return
     */
    public Integer saveSystemResourceRole(SystemResourceRole systemResourceRole) {
        int count = systemResourceRoleWriteMapper.insert(systemResourceRole);
        if (count == 0) {
            throw new MallException("添加角色资源对应表失败，请重试");
        }
        return count;
    }

    /**
     * 保存选中的角色和对应资源
     *
     * @param roleId
     * @param resourceIds
     * @return
     */
    @Transactional
    public boolean saveSystemResourceRole(Integer roleId, String resourceIds) {
        //删除此角色之前的资源关联
        SystemResourceRoleExample example = new SystemResourceRoleExample();
        example.setRoleId(roleId);
        systemResourceRoleWriteMapper.deleteByExample(example);
        //保存选中的资源
        HashSet<Integer> PIds = new HashSet<>();
        String[] resourceArr = resourceIds.split(",");
        for (String resourceId : resourceArr) {
            //保存选中的父资源
            SystemResource systemResource = systemResourceReadMapper.getByPrimaryKey(Integer.valueOf(resourceId));
            if (!PIds.contains(systemResource.getPid())) {
                SystemResourceRole srr1 = new SystemResourceRole();
                srr1.setResourceId(systemResource.getPid());
                srr1.setRoleId(roleId);
                srr1.setCreateTime(new Date());
                systemResourceRoleWriteMapper.insert(srr1);
                PIds.add(systemResource.getPid());
            }
            //保存选中的资源
            SystemResourceRole srr2 = new SystemResourceRole();
            srr2.setResourceId(Integer.valueOf(resourceId));
            srr2.setRoleId(roleId);
            srr2.setCreateTime(new Date());
            systemResourceRoleWriteMapper.insert(srr2);

            //保存选中的子资源
            SystemResourceExample resourceExample = new SystemResourceExample();
            resourceExample.setPid(Integer.valueOf(resourceId));
            List<SystemResource> byPId = systemResourceReadMapper.listByExample(resourceExample);
            for (SystemResource s : byPId) {
                SystemResourceRole srr3 = new SystemResourceRole();
                srr3.setResourceId(s.getResourceId());
                srr3.setRoleId(roleId);
                srr3.setCreateTime(new Date());
                systemResourceRoleWriteMapper.insert(srr3);
            }
        }
        return true;
    }

    /**
     * 根据resourceRoleId删除角色资源对应表
     *
     * @param resourceRoleId resourceRoleId
     * @return
     */
    public Integer deleteSystemResourceRole(Integer resourceRoleId) {
        if (StringUtils.isEmpty(resourceRoleId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = systemResourceRoleWriteMapper.deleteByPrimaryKey(resourceRoleId);
        if (count == 0) {
            log.error("根据resourceRoleId：" + resourceRoleId + "删除角色资源对应表失败");
            throw new MallException("删除角色资源对应表失败,请重试");
        }
        return count;
    }

    /**
     * 根据resourceRoleId更新角色资源对应表
     *
     * @param systemResourceRole
     * @return
     */
    public Integer updateSystemResourceRole(SystemResourceRole systemResourceRole) {
        if (StringUtils.isEmpty(systemResourceRole.getResourceRoleId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = systemResourceRoleWriteMapper.updateByPrimaryKeySelective(systemResourceRole);
        if (count == 0) {
            log.error("根据resourceRoleId：" + systemResourceRole.getResourceRoleId() + "更新角色资源对应表失败");
            throw new MallException("更新角色资源对应表失败,请重试");
        }
        return count;
    }

    /**
     * 根据resourceRoleId获取角色资源对应表详情
     *
     * @param resourceRoleId resourceRoleId
     * @return
     */
    public SystemResourceRole getSystemResourceRoleByResourceRoleId(Integer resourceRoleId) {
        return systemResourceRoleReadMapper.getByPrimaryKey(resourceRoleId);
    }

    /**
     * 根据条件获取角色资源对应表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<SystemResourceRole> getSystemResourceRoleList(SystemResourceRoleExample example, PagerInfo pager) {
        List<SystemResourceRole> systemResourceRoleList;
        if (pager != null) {
            pager.setRowsCount(systemResourceRoleReadMapper.countByExample(example));
            systemResourceRoleList = systemResourceRoleReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            systemResourceRoleList = systemResourceRoleReadMapper.listByExample(example);
        }
        return systemResourceRoleList;
    }
}