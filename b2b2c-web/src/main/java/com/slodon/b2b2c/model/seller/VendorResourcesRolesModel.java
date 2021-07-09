package com.slodon.b2b2c.model.seller;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.seller.VendorResourcesReadMapper;
import com.slodon.b2b2c.dao.read.seller.VendorResourcesRolesReadMapper;
import com.slodon.b2b2c.dao.write.seller.VendorResourcesRolesWriteMapper;
import com.slodon.b2b2c.seller.example.VendorResourcesExample;
import com.slodon.b2b2c.seller.example.VendorResourcesRolesExample;
import com.slodon.b2b2c.seller.pojo.VendorResources;
import com.slodon.b2b2c.seller.pojo.VendorResourcesRoles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Component
@Slf4j
public class VendorResourcesRolesModel {

    @Resource
    private VendorResourcesRolesReadMapper vendorResourcesRolesReadMapper;
    @Resource
    private VendorResourcesRolesWriteMapper vendorResourcesRolesWriteMapper;
    @Resource
    private VendorResourcesReadMapper vendorResourcesReadMapper;

    /**
     * 新增商家角色资源对应表
     *
     * @param vendorResourcesRoles
     * @return
     */
    public Integer saveVendorResourcesRoles(VendorResourcesRoles vendorResourcesRoles) {
        int count = vendorResourcesRolesWriteMapper.insert(vendorResourcesRoles);
        if (count == 0) {
            throw new MallException("添加商家角色资源对应表失败，请重试");
        }
        return count;
    }

    /**
     * 添加角色资源信息
     *
     * @param rolesId
     * @param resourceIds
     * @return
     */
    @Transactional
    public Integer addVendorResourcesRoles(Integer rolesId, String resourceIds) {

        //删除与该角色绑定的资源
        VendorResourcesRolesExample vendorResourcesRolesExample = new VendorResourcesRolesExample();
        vendorResourcesRolesExample.setRolesId(rolesId);
        vendorResourcesRolesWriteMapper.deleteByExample(vendorResourcesRolesExample);

        //保存资源
        HashSet<Integer> pids = new HashSet<>();
        int count = 0;
        String[] split = resourceIds.split(",");
        for (String resourceId : split) {
            //根据resourceId获取资源信息
            VendorResources vendorResources = vendorResourcesReadMapper.getByPrimaryKey(Integer.valueOf(resourceId));
            if (!pids.contains(vendorResources.getPid())) {
                //保存选中的父资源
                VendorResourcesRoles vendorResourcesRoles = new VendorResourcesRoles();
                vendorResourcesRoles.setResourcesId(vendorResources.getPid());
                vendorResourcesRoles.setRolesId(rolesId);
                vendorResourcesRoles.setCreateTime(new Date());
                count = vendorResourcesRolesWriteMapper.insert(vendorResourcesRoles);
                if (count == 0) {
                    throw new MallException("保存选中的父资源失败，请重试");
                }
                pids.add(vendorResources.getPid());
            }
            //保存当前选中的资源
            VendorResourcesRoles vendorResourcesRolesNow = new VendorResourcesRoles();
            vendorResourcesRolesNow.setResourcesId(Integer.valueOf(resourceId));
            vendorResourcesRolesNow.setRolesId(rolesId);
            vendorResourcesRolesNow.setCreateTime(new Date());
            count = vendorResourcesRolesWriteMapper.insert(vendorResourcesRolesNow);
            if (count == 0) {
                throw new MallException("保存选中的资源失败，请重试");
            }

            //保存当前选中的子资源
            VendorResourcesExample vendorResourcesExample = new VendorResourcesExample();
            vendorResourcesExample.setPid(Integer.valueOf(resourceId));
            List<VendorResources> resourcesList = vendorResourcesReadMapper.listByExample(vendorResourcesExample);
            if (!CollectionUtils.isEmpty(resourcesList)) {
                for (VendorResources resources : resourcesList) {
                    VendorResourcesRoles vendorResourcesRolesChild = new VendorResourcesRoles();
                    vendorResourcesRolesChild.setResourcesId(resources.getResourcesId());
                    vendorResourcesRolesChild.setRolesId(rolesId);
                    vendorResourcesRolesChild.setCreateTime(new Date());
                    count = vendorResourcesRolesWriteMapper.insert(vendorResourcesRolesChild);
                    if (count == 0) {
                        throw new MallException("保存选中的子资源失败，请重试");
                    }

                }
            }
        }
        return count;
    }

    /**
     * 根据resourcesRolesId删除商家角色资源对应表
     *
     * @param resourcesRolesId resourcesRolesId
     * @return
     */
    public Integer deleteVendorResourcesRoles(Integer resourcesRolesId) {
        if (StringUtils.isEmpty(resourcesRolesId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = vendorResourcesRolesWriteMapper.deleteByPrimaryKey(resourcesRolesId);
        if (count == 0) {
            log.error("根据resourcesRolesId：" + resourcesRolesId + "删除商家角色资源对应表失败");
            throw new MallException("删除商家角色资源对应表失败,请重试");
        }
        return count;
    }

    /**
     * 根据resourcesRolesId更新商家角色资源对应表
     *
     * @param vendorResourcesRoles
     * @return
     */
    public Integer updateVendorResourcesRoles(VendorResourcesRoles vendorResourcesRoles) {
        if (StringUtils.isEmpty(vendorResourcesRoles.getResourcesRolesId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = vendorResourcesRolesWriteMapper.updateByPrimaryKeySelective(vendorResourcesRoles);
        if (count == 0) {
            log.error("根据resourcesRolesId：" + vendorResourcesRoles.getResourcesRolesId() + "更新商家角色资源对应表失败");
            throw new MallException("更新商家角色资源对应表失败,请重试");
        }
        return count;
    }

    /**
     * 根据resourcesRolesId获取商家角色资源对应表详情
     *
     * @param resourcesRolesId resourcesRolesId
     * @return
     */
    public VendorResourcesRoles getVendorResourcesRolesByResourcesRolesId(Integer resourcesRolesId) {
        return vendorResourcesRolesReadMapper.getByPrimaryKey(resourcesRolesId);
    }

    /**
     * 根据条件获取商家角色资源对应表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<VendorResourcesRoles> getVendorResourcesRolesList(VendorResourcesRolesExample example, PagerInfo pager) {
        List<VendorResourcesRoles> vendorResourcesRolesList;
        if (pager != null) {
            pager.setRowsCount(vendorResourcesRolesReadMapper.countByExample(example));
            vendorResourcesRolesList = vendorResourcesRolesReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            vendorResourcesRolesList = vendorResourcesRolesReadMapper.listByExample(example);
        }
        return vendorResourcesRolesList;
    }
}