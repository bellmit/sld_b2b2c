package com.slodon.b2b2c.model.system;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.system.SystemResourceReadMapper;
import com.slodon.b2b2c.dao.write.system.SystemResourceRoleWriteMapper;
import com.slodon.b2b2c.dao.write.system.SystemResourceWriteMapper;
import com.slodon.b2b2c.system.example.SystemResourceExample;
import com.slodon.b2b2c.system.example.SystemResourceRoleExample;
import com.slodon.b2b2c.system.pojo.SystemResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class SystemResourceModel {
    @Resource
    private SystemResourceReadMapper systemResourceReadMapper;

    @Resource
    private SystemResourceWriteMapper systemResourceWriteMapper;

    @Resource
    private SystemResourceRoleWriteMapper systemResourceRoleWriteMapper;

    /**
     * 新增平台资源表
     *
     * @param systemResource
     * @return
     */
    public Integer saveSystemResource(SystemResource systemResource) {
        int count = systemResourceWriteMapper.insert(systemResource);
        if (count == 0) {
            throw new MallException("添加平台资源表失败，请重试");
        }
        return count;
    }

    /**
     * 根据resourceId删除平台资源表
     *
     * @param resourceId resourceId
     * @return
     */
    public Integer deleteSystemResource(Integer resourceId) {
        if (StringUtils.isEmpty(resourceId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = systemResourceWriteMapper.deleteByPrimaryKey(resourceId);
        if (count == 0) {
            log.error("根据resourceId：" + resourceId + "删除平台资源表失败");
            throw new MallException("删除平台资源表失败,请重试");
        }
        return count;
    }

    /**
     * 批量删除资源
     *
     * @param resourceIds
     * @return
     */
    public Integer deleteSystemResourceByResourceIds(String resourceIds) {
        //删除绑定关系
        SystemResourceRoleExample resourceRoleExample = new SystemResourceRoleExample();
        resourceRoleExample.setResourcesIdIn(resourceIds);
        systemResourceRoleWriteMapper.deleteByExample(resourceRoleExample);

        //删除资源
        SystemResourceExample systemResourceExample = new SystemResourceExample();
        systemResourceExample.setResourceIdIn(resourceIds);
        int count = systemResourceWriteMapper.deleteByExample(systemResourceExample);
        if (count == 0) {
            throw new MallException("批量删除平台资源表失败,请重试");
        }
        return count;
    }

    /**
     * 根据resourceId更新平台资源表
     *
     * @param systemResource
     * @return
     */
    public Integer updateSystemResource(SystemResource systemResource) {
        if (StringUtils.isEmpty(systemResource.getResourceId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = systemResourceWriteMapper.updateByPrimaryKeySelective(systemResource);
        if (count == 0) {
            log.error("根据resourceId：" + systemResource.getResourceId() + "更新平台资源表失败");
            throw new MallException("更新平台资源表失败,请重试");
        }
        return count;
    }

    /**
     * 根据resourceId获取平台资源表详情
     *
     * @param resourceId resourceId
     * @return
     */
    public SystemResource getSystemResourceByResourceId(Integer resourceId) {
        return systemResourceReadMapper.getByPrimaryKey(resourceId);
    }

    /**
     * 根据条件获取平台资源表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<SystemResource> getSystemResourceList(SystemResourceExample example, PagerInfo pager) {
        List<SystemResource> systemResourceList;
        if (pager != null) {
            pager.setRowsCount(systemResourceReadMapper.countByExample(example));
            systemResourceList = systemResourceReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            systemResourceList = systemResourceReadMapper.listByExample(example);
        }
        return systemResourceList;
    }
}