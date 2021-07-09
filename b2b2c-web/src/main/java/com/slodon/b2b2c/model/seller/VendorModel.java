package com.slodon.b2b2c.model.seller;

import com.slodon.b2b2c.core.constant.BizTypeConst;
import com.slodon.b2b2c.core.constant.VendorConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.Md5;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.dao.read.seller.StoreReadMapper;
import com.slodon.b2b2c.dao.read.seller.VendorReadMapper;
import com.slodon.b2b2c.dao.read.seller.VendorRolesReadMapper;
import com.slodon.b2b2c.dao.write.seller.VendorWriteMapper;
import com.slodon.b2b2c.seller.dto.VendorRegisterDTO;
import com.slodon.b2b2c.seller.example.VendorExample;
import com.slodon.b2b2c.seller.example.VendorRolesExample;
import com.slodon.b2b2c.seller.pojo.Store;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.seller.pojo.VendorRoles;
import com.slodon.smartid.client.utils.SmartId;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class VendorModel {

    @Resource
    private VendorReadMapper vendorReadMapper;
    @Resource
    private VendorWriteMapper vendorWriteMapper;
    @Resource
    private VendorRolesReadMapper vendorRolesReadMapper;
    @Resource
    private StoreReadMapper storeReadMapper;

    /**
     * 新增商家表
     *
     * @param vendor
     * @return
     */
    public Integer saveVendor(Vendor vendor) {

        //账号名称查重
        VendorExample vendorExample = new VendorExample();
        vendorExample.setVendorName(vendor.getVendorName());
        List<Vendor> vendorList = vendorReadMapper.listByExample(vendorExample);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(vendorList), "账号名称已存在！");

        int count = vendorWriteMapper.insert(vendor);
        if (count == 0) {
            throw new MallException("添加商家表失败，请重试");
        }
        return count;
    }

    /**
     * 注册商家账号
     *
     * @param vendorRegisterDTO
     * @return
     */
    @Transactional
    public Integer registerVendor(VendorRegisterDTO vendorRegisterDTO) throws Exception {
        //商户账号重复判断
        VendorExample example = new VendorExample();
        example.setVendorName(vendorRegisterDTO.getVendorName().trim());
        List<Vendor> vendors = vendorReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(vendors)) {
            throw new MallException("商户账号名称已存在，请重新输入");
        }
        //判断手机号是否存在，若存在，进行解绑
        VendorExample vendorExample = new VendorExample();
        vendorExample.setVendorMobile(vendorRegisterDTO.getVendorMobile());
        List<Vendor> vendorList = vendorReadMapper.listByExample(vendorExample);
        if (!CollectionUtils.isEmpty(vendorList)) {
            Vendor vendorUpdate = vendorList.get(0);
            vendorUpdate.setVendorMobile("");
            vendorWriteMapper.updateByPrimaryKeySelective(vendorUpdate);
        }
        Vendor vendor = new Vendor();
        PropertyUtils.copyProperties(vendor, vendorRegisterDTO);
        //数据合格，插入vendor表
        vendor.setVendorName(vendorRegisterDTO.getVendorName().trim());
        vendor.setVendorPassword(Md5.getMd5String(vendorRegisterDTO.getVendorPassword()));

        //注册的商家，暂时无店铺，店铺id设置为0
        vendor.setStoreId(0L);

        //查询内置商家角色
        VendorRolesExample rolesExample = new VendorRolesExample();
        rolesExample.setIsInner(1);
        rolesExample.setOrderBy("roles_id asc");
        List<VendorRoles> vendorRoles = vendorRolesReadMapper.listByExample(rolesExample);
        if (CollectionUtils.isEmpty(vendorRoles)) {
            vendor.setRolesId(0);
        } else {
            vendor.setRolesId(vendorRoles.get(0).getRolesId());
        }

        //注册时默认超级管理员
        vendor.setIsStoreAdmin(VendorConst.IS_STORE_ADMIN_1);
        vendor.setRegisterTime(new Date());
        vendor.setLatestLoginTime(new Date());
        vendor.setLatestLoginIp(vendorRegisterDTO.getIp());
        vendor.setVendorId(SmartId.nextId(BizTypeConst.VENDOR));

        int count = vendorWriteMapper.insert(vendor);
        if (count == 0) {
            throw new MallException("注册商户账号失败，请重试");
        }
        return count;
    }

    /**
     * 根据vendorId删除商家表
     *
     * @param vendorId vendorId
     * @return
     */
    public Integer deleteVendor(Long vendorId) {
        if (StringUtils.isEmpty(vendorId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = vendorWriteMapper.deleteByPrimaryKey(vendorId);
        if (count == 0) {
            log.error("根据vendorId：" + vendorId + "删除商家表失败");
            throw new MallException("删除商家表失败,请重试");
        }
        return count;
    }

    /**
     * 根据vendorId更新商家表
     *
     * @param vendor
     * @return
     */
    public Integer updateVendor(Vendor vendor) {
        //操作员名称查询
        if (!StringUtils.isEmpty(vendor.getVendorName())) {
            VendorExample vendorExample = new VendorExample();
            vendorExample.setVendorName(vendor.getVendorName());
            vendorExample.setVendorIdNotEquals(vendor.getVendorId());
            List<Vendor> vendorList = vendorReadMapper.listByExample(vendorExample);
            AssertUtil.isTrue(!CollectionUtils.isEmpty(vendorList), "账号名称已存在");
        }
        int count = vendorWriteMapper.updateByPrimaryKeySelective(vendor);
        if (count == 0) {
            log.error("根据vendorId：" + vendor.getVendorId() + "更新商家表失败");
            throw new MallException("更新商家表失败,请重试");
        }
        return count;
    }

    /**
     * 根据vendorId获取商家表详情
     *
     * @param vendorId vendorId
     * @return
     */
    public Vendor getVendorByVendorId(Long vendorId) {
        Vendor vendor = vendorReadMapper.getByPrimaryKey(vendorId);
        AssertUtil.notNull(vendor, "获取商户信息为空，请重试");
        if (!StringUtil.isNullOrZero(vendor.getStoreId())) {
            //查询店铺信息
            Store store = storeReadMapper.getByPrimaryKey(vendor.getStoreId());
            AssertUtil.notNull(store, "获取店铺信息为空，请重试");
            vendor.setStore(store);
        }
        return vendor;
    }

    /**
     * 根据条件获取商家表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<Vendor> getVendorList(VendorExample example, PagerInfo pager) {
        List<Vendor> vendorList;
        if (pager != null) {
            pager.setRowsCount(vendorReadMapper.countByExample(example));
            vendorList = vendorReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            vendorList = vendorReadMapper.listByExample(example);
        }
        return vendorList;
    }
}