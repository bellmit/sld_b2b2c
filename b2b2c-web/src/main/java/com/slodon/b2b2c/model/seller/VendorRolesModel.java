package com.slodon.b2b2c.model.seller;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.seller.VendorRolesReadMapper;
import com.slodon.b2b2c.dao.write.seller.VendorResourcesRolesWriteMapper;
import com.slodon.b2b2c.dao.write.seller.VendorRolesWriteMapper;
import com.slodon.b2b2c.seller.example.VendorResourcesRolesExample;
import com.slodon.b2b2c.seller.example.VendorRolesExample;
import com.slodon.b2b2c.seller.pojo.VendorRoles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class VendorRolesModel {

    @Resource
    private VendorRolesReadMapper vendorRolesReadMapper;
    @Resource
    private VendorRolesWriteMapper vendorRolesWriteMapper;
    @Resource
    private VendorResourcesRolesWriteMapper vendorResourcesRolesWriteMapper;

    /**
     * 新增商家角色表
     *
     * @param vendorRoles
     * @return
     */
    public Integer saveVendorRoles(VendorRoles vendorRoles) {
        //查重角色名称
        VendorRolesExample rolesExample = new VendorRolesExample();
        rolesExample.setRolesName(vendorRoles.getRolesName());
        List<VendorRoles> vendorRolesList = vendorRolesReadMapper.listByExample(rolesExample);
        if (!CollectionUtils.isEmpty(vendorRolesList)) {
            throw new MallException("该角色名称已存在，请重新填写");
        }
        int count = vendorRolesWriteMapper.insert(vendorRoles);
        if (count == 0) {
            throw new MallException("添加商家角色表失败，请重试");
        }
        return vendorRoles.getRolesId();
    }

    /**
     * 根据rolesId删除商家角色表
     *
     * @param rolesId rolesId
     * @return
     */
    @Transactional
    public Integer deleteVendorRoles(Integer rolesId) {
        int count = vendorRolesWriteMapper.deleteByPrimaryKey(rolesId);
        if (count == 0) {
            log.error("根据rolesId：" + rolesId + "删除商家角色表失败");
            throw new MallException("删除商家角色表失败,请重试");
        }

        VendorResourcesRolesExample vendorResourcesRolesExample = new VendorResourcesRolesExample();
        vendorResourcesRolesExample.setRolesId(rolesId);
        vendorResourcesRolesWriteMapper.deleteByExample(vendorResourcesRolesExample);
        return count;
    }

    /**
     * 根据rolesId更新商家角色表
     *
     * @param vendorRoles
     * @return
     */
    public Integer updateVendorRoles(VendorRoles vendorRoles) {
        //权限组名称查重
        VendorRolesExample rolesExample = new VendorRolesExample();
        rolesExample.setRolesName(vendorRoles.getRolesName());
        rolesExample.setRolesIdNotEquals(vendorRoles.getRolesId());
        List<VendorRoles> vendorRolesList = vendorRolesReadMapper.listByExample(rolesExample);
        if (!CollectionUtils.isEmpty(vendorRolesList)) {
            throw new MallException("权限组名称重复，请重新填写");
        }
        int count = vendorRolesWriteMapper.updateByPrimaryKeySelective(vendorRoles);
        if (count == 0) {
            log.error("根据rolesId：" + vendorRoles.getRolesId() + "更新商家角色表失败");
            throw new MallException("更新商家角色表失败,请重试");
        }
        return count;
    }

    /**
     * 根据rolesId获取商家角色表详情
     *
     * @param rolesId rolesId
     * @return
     */
    public VendorRoles getVendorRolesByRolesId(Integer rolesId) {
        return vendorRolesReadMapper.getByPrimaryKey(rolesId);
    }

    /**
     * 根据条件获取商家角色表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<VendorRoles> getVendorRolesList(VendorRolesExample example, PagerInfo pager) {
        List<VendorRoles> vendorRolesList;
        if (pager != null) {
            pager.setRowsCount(vendorRolesReadMapper.countByExample(example));
            vendorRolesList = vendorRolesReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            vendorRolesList = vendorRolesReadMapper.listByExample(example);
        }
        return vendorRolesList;
    }
}