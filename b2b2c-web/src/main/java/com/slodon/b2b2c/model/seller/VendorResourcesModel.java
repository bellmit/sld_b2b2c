package com.slodon.b2b2c.model.seller;

import com.slodon.b2b2c.core.constant.VendorConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.seller.VendorResourcesReadMapper;
import com.slodon.b2b2c.dao.write.seller.VendorResourcesRolesWriteMapper;
import com.slodon.b2b2c.dao.write.seller.VendorResourcesWriteMapper;
import com.slodon.b2b2c.seller.example.VendorResourcesExample;
import com.slodon.b2b2c.seller.example.VendorResourcesRolesExample;
import com.slodon.b2b2c.seller.pojo.VendorResources;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class VendorResourcesModel {

    @Resource
    private VendorResourcesReadMapper vendorResourcesReadMapper;
    @Resource
    private VendorResourcesWriteMapper vendorResourcesWriteMapper;
    @Resource
    private VendorResourcesRolesWriteMapper vendorResourcesRolesWriteMapper;

    /**
     * 新增商家资源表
     *
     * @param vendorResources
     * @return
     */
    public Integer saveVendorResources(VendorResources vendorResources) {
        int count = vendorResourcesWriteMapper.insert(vendorResources);
        if (count == 0) {
            throw new MallException("添加商家资源表失败，请重试");
        }
        return count;
    }

    /**
     * 根据resourcesId删除商家资源表
     *
     * @param resourcesId resourcesId
     * @return
     */
    public Integer deleteVendorResources(Integer resourcesId) {
        if (StringUtils.isEmpty(resourcesId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = vendorResourcesWriteMapper.deleteByPrimaryKey(resourcesId);
        if (count == 0) {
            log.error("根据resourcesId：" + resourcesId + "删除商家资源表失败");
            throw new MallException("删除商家资源表失败,请重试");
        }
        return count;
    }

    /**
     * 批量删除商家资源表
     *
     * @param resourcesIds
     * @return
     */
    public Integer batchDeleteVendorResources(String resourcesIds) {
        //删除绑定关系
        VendorResourcesRolesExample vendorResourcesRolesExample = new VendorResourcesRolesExample();
        vendorResourcesRolesExample.setResourcesIdIn(resourcesIds);
        vendorResourcesRolesWriteMapper.deleteByExample(vendorResourcesRolesExample);

        //删除资源
        VendorResourcesExample vendorResourcesExample = new VendorResourcesExample();
        vendorResourcesExample.setResourcesIdIn(resourcesIds);
        int count = vendorResourcesWriteMapper.deleteByExample(vendorResourcesExample);
        if (count == 0) {
            throw new MallException("批量删除商家资源表失败,请重试");
        }
        return count;
    }

    /**
     * 根据resourcesId更新商家资源表
     *
     * @param vendorResources
     * @return
     */
    public Integer updateVendorResources(VendorResources vendorResources) {
        if (StringUtils.isEmpty(vendorResources.getResourcesId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = vendorResourcesWriteMapper.updateByPrimaryKeySelective(vendorResources);
        if (count == 0) {
            log.error("根据resourcesId：" + vendorResources.getResourcesId() + "更新商家资源表失败");
            throw new MallException("更新商家资源表失败,请重试");
        }
        return count;
    }

    /**
     * 根据resourcesId获取商家资源表详情
     *
     * @param resourcesId resourcesId
     * @return
     */
    public VendorResources getVendorResourcesByResourcesId(Integer resourcesId) {
        return vendorResourcesReadMapper.getByPrimaryKey(resourcesId);
    }

    /**
     * 根据条件获取商家资源表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<VendorResources> getVendorResourcesList(VendorResourcesExample example, PagerInfo pager) {
        List<VendorResources> vendorResourcesList;
        if (pager != null) {
            pager.setRowsCount(vendorResourcesReadMapper.countByExample(example));
            vendorResourcesList = vendorResourcesReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            vendorResourcesList = vendorResourcesReadMapper.listByExample(example);
        }
        return vendorResourcesList;
    }

    /**
     * 获取二，三级系统菜单列表,用于前端展示
     *
     * @return
     */
    public List<VendorResources> getVendorResourceByFrontPath(VendorResourcesExample example) {
        example.setState(VendorConst.RESOURCE_STATE_1);//未删除
        example.setOrderBy("resources_id asc");
        List<VendorResources> secondList = vendorResourcesReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(secondList)) {
            VendorResourcesExample vendorResourcesExample = new VendorResourcesExample();
            secondList.forEach(vendorResources -> {
                vendorResourcesExample.setGrade(VendorConst.RESOURCE_GRADE_3);
                vendorResourcesExample.setState(VendorConst.RESOURCE_STATE_1);//未删除
                vendorResourcesExample.setOrderBy("resources_id asc");
                vendorResourcesExample.setPid(vendorResources.getResourcesId());
                vendorResources.setChildren(vendorResourcesReadMapper.listByExample(vendorResourcesExample));
            });
        }
        return secondList;
    }
}