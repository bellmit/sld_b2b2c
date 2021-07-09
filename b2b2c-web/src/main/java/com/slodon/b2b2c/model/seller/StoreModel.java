package com.slodon.b2b2c.model.seller;

import com.slodon.b2b2c.business.dto.OrderDayDTO;
import com.slodon.b2b2c.business.example.OrderExample;
import com.slodon.b2b2c.core.constant.*;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.Md5;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.dao.read.business.OrderReadMapper;
import com.slodon.b2b2c.dao.read.goods.GoodsExtendReadMapper;
import com.slodon.b2b2c.dao.read.goods.GoodsReadMapper;
import com.slodon.b2b2c.dao.read.member.MemberFollowStoreReadMapper;
import com.slodon.b2b2c.dao.read.seller.*;
import com.slodon.b2b2c.dao.write.goods.GoodsExtendWriteMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsWriteMapper;
import com.slodon.b2b2c.dao.write.seller.*;
import com.slodon.b2b2c.goods.example.GoodsExample;
import com.slodon.b2b2c.goods.example.GoodsExtendExample;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.GoodsExtend;
import com.slodon.b2b2c.member.example.MemberFollowStoreExample;
import com.slodon.b2b2c.member.pojo.MemberFollowStore;
import com.slodon.b2b2c.seller.dto.CommentsDTO;
import com.slodon.b2b2c.seller.dto.OwnStoreAddDTO;
import com.slodon.b2b2c.seller.dto.OwnStoreUpdateDTO;
import com.slodon.b2b2c.seller.dto.StoreUpdateDTO;
import com.slodon.b2b2c.seller.example.*;
import com.slodon.b2b2c.seller.pojo.*;
import com.slodon.smartid.client.utils.SmartId;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class StoreModel {

    @Resource
    private StoreReadMapper storeReadMapper;
    @Resource
    private StoreWriteMapper storeWriteMapper;
    @Resource
    private VendorReadMapper vendorReadMapper;
    @Resource
    private VendorWriteMapper vendorWriteMapper;
    @Resource
    private StoreApplyWriteMapper storeApplyWriteMapper;
    @Resource
    private StoreCertificateReadMapper storeCertificateReadMapper;
    @Resource
    private StoreCertificateWriteMapper storeCertificateWriteMapper;
    @Resource
    private StoreGradeReadMapper storeGradeReadMapper;
    @Resource
    private StoreBindCategoryWriteMapper storeBindCategoryWriteMapper;
    @Resource
    private StoreCommentReadMapper storeCommentReadMapper;
    @Resource
    private StoreRenewReadMapper storeRenewReadMapper;
    @Resource
    private GoodsReadMapper goodsReadMapper;
    @Resource
    private GoodsWriteMapper goodsWriteMapper;
    @Resource
    private GoodsExtendReadMapper goodsExtendReadMapper;
    @Resource
    private GoodsExtendWriteMapper goodsExtendWriteMapper;
    @Resource
    private MemberFollowStoreReadMapper memberFollowStoreReadMapper;
    @Resource
    private OrderReadMapper orderReadMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 新增店铺表
     *
     * @param store
     * @return
     */
    public Integer saveStore(Store store) {
        int count = storeWriteMapper.insert(store);
        if (count == 0) {
            throw new MallException("添加店铺表失败，请重试");
        }
        return count;
    }

    /**
     * 入驻凭证支付成功，开通店铺
     *
     * @param storeApply
     * @return
     */
    @Transactional
    public Integer openStore(StoreApply storeApply) {

        //新增store表
        Store store = new Store();
        store.setStoreId(SmartId.nextId(BizTypeConst.STORE));
        store.setStoreName(storeApply.getStoreName());
        store.setStoreGradeId(storeApply.getStoreGradeId());
        //根据店铺等级id获取等级名称
        StoreGrade storeGrade = storeGradeReadMapper.getByPrimaryKey(storeApply.getStoreGradeId());
        store.setStoreGradeName(storeGrade.getGradeName());
        store.setCreateTime(new Date());
        //店铺到期时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(store.getCreateTime());
        calendar.add(Calendar.YEAR, storeApply.getApplyYear());
        store.setStoreExpireTime(calendar.getTime());
        store.setState(StoreConst.STORE_STATE_OPEN);
        store.setIsOwnStore(StoreConst.NO_OWN_STORE);
        store.setOpenTime(storeApply.getApplyYear());

        int count;
        count = storeWriteMapper.insert(store);
        if (count == 0) {
            throw new MallException("添加店铺表失败，请重试");
        }

        //修改商家申请表申请状态
        storeApply.setState(StoreConst.STATE_4_STORE_OPEN);
        storeApply.setStoreId(store.getStoreId());
        storeApply.setCallbackTime(new Date());
        count = storeApplyWriteMapper.updateByPrimaryKeySelective(storeApply);
        if (count == 0) {
            throw new MallException("修改店铺申请表失败，请重试");
        }

        //关联store_bind_category与storeId
        StoreBindCategoryExample storeBindCategoryExample = new StoreBindCategoryExample();
        storeBindCategoryExample.setCreateVendorId(storeApply.getVendorId());
        StoreBindCategory storeBindCategory = new StoreBindCategory();
        storeBindCategory.setStoreId(store.getStoreId());
        count = storeBindCategoryWriteMapper.updateByExampleSelective(storeBindCategory, storeBindCategoryExample);
        if (count == 0) {
            throw new MallException("修改店铺可用商品分类申请表失败，请重试");
        }

        //关联storeId与vendor表
        Vendor vendor = new Vendor();
        vendor.setVendorId(storeApply.getVendorId());
        vendor.setStoreId(store.getStoreId());
        count = vendorWriteMapper.updateByPrimaryKeySelective(vendor);
        if (count == 0) {
            throw new MallException("修改商家管理员表失败，请重试");
        }
        return count;
    }

    /**
     * 新增自营店铺表
     *
     * @param ownStoreAddDTO
     * @return
     */
    @Transactional
    public Integer saveStore(OwnStoreAddDTO ownStoreAddDTO) {
        //判断店铺名称是否重复
        StoreExample storeExample = new StoreExample();
        storeExample.setStoreName(ownStoreAddDTO.getStoreName());
        List<Store> stores = storeReadMapper.listByExample(storeExample);
        if (!CollectionUtils.isEmpty(stores)) {
            throw new MallException("店铺名称已存在，请重新填写");
        }

        //插入store表
        Store storeInsert = new Store();
        storeInsert.setStoreId(SmartId.nextId(BizTypeConst.STORE));
        storeInsert.setStoreName(ownStoreAddDTO.getStoreName());
        storeInsert.setCreateTime(new Date());
        storeInsert.setIsOwnStore(StoreConst.IS_OWN_STORE);
        storeInsert.setState(StoreConst.STORE_STATE_OPEN);
        storeInsert.setOpenTime(100);
        storeInsert.setProvinceCode(ownStoreAddDTO.getProvinceCode());
        storeInsert.setCityCode(ownStoreAddDTO.getCityCode());
        storeInsert.setAreaCode(ownStoreAddDTO.getAreaCode());
        storeInsert.setAreaInfo(ownStoreAddDTO.getAreaInfo());
        if (!StringUtils.isEmpty(ownStoreAddDTO.getAddress())) {
            storeInsert.setAddress(ownStoreAddDTO.getAddress());
        }
        if (!StringUtil.isNullOrZero(ownStoreAddDTO.getBillType()) && !StringUtils.isEmpty(ownStoreAddDTO.getBillDays())) {
            storeInsert.setBillType(ownStoreAddDTO.getBillType());
            //结算日期
            String[] billDayArr = ownStoreAddDTO.getBillDays().split(",");

            boolean flag = false;
            for (String billDay : billDayArr) {
                if (ownStoreAddDTO.getBillType() == StoreConst.BILL_TYPE_WEEK) {
                    for (int i = 1; i < 8; i++) {
                        if (Integer.parseInt(billDay) == i) {
                            flag = true;
                        }
                    }
                } else if (ownStoreAddDTO.getBillType() == StoreConst.BILL_TYPE_MONTH) {
                    for (int i = 1; i < 32; i++) {
                        if (Integer.parseInt(billDay) == i) {
                            flag = true;
                        }
                    }
                }
            }
            if (!flag) {
                throw new MallException("请选择结算周期");
            }

            storeInsert.setBillDay(ownStoreAddDTO.getBillDays());
        }
        int count;
        count = storeWriteMapper.insert(storeInsert);
        if (count == 0) {
            throw new MallException("添加店铺表失败，请重试");
        }

        //插入vendor表，关联storeId
        Vendor vendor = new Vendor();

        //判断管理员名称是否重复
        VendorExample vendorExample = new VendorExample();
        vendorExample.setVendorName(ownStoreAddDTO.getVendorName());
        List<Vendor> vendors = vendorReadMapper.listByExample(vendorExample);
        if (!CollectionUtils.isEmpty(vendors)) {
            throw new MallException("店铺账号已存在，请重新填写");
        }

        //判断管理员手机号是否重复
        VendorExample vendorMobileExample = new VendorExample();
        vendorMobileExample.setVendorMobile(ownStoreAddDTO.getContactPhone());
        List<Vendor> vendorList = vendorReadMapper.listByExample(vendorMobileExample);
        if (!CollectionUtils.isEmpty(vendorList)) {
            throw new MallException("该手机号已被其他账号绑定，请重新填写");
        }
        vendor.setVendorMobile(ownStoreAddDTO.getContactPhone());
        vendor.setVendorId(SmartId.nextId(BizTypeConst.VENDOR));
        vendor.setVendorName(ownStoreAddDTO.getVendorName());
        vendor.setVendorPassword(Md5.getMd5String(ownStoreAddDTO.getVendorPassword()));
        vendor.setIsStoreAdmin(VendorConst.IS_STORE_ADMIN_1);
        vendor.setStoreId(storeInsert.getStoreId());
        vendor.setRegisterTime(new Date());
        vendor.setLatestLoginTime(new Date());
        vendor.setRolesId(2);
        count = vendorWriteMapper.insert(vendor);
        if (count == 0) {
            throw new MallException("添加管理员表失败，请重试");
        }

        //插入store_apply表
        StoreApply storeApplyInsert = new StoreApply();
        storeApplyInsert.setVendorId(vendor.getVendorId());
        storeApplyInsert.setVendorName(vendor.getVendorName());
        storeApplyInsert.setStoreId(storeInsert.getStoreId());
        storeApplyInsert.setStoreName(ownStoreAddDTO.getStoreName());
        storeApplyInsert.setState(StoreConst.STATE_4_STORE_OPEN);
        storeApplyInsert.setApplyYear(100);
        storeApplyInsert.setSubmitTime(new Date());
        storeApplyInsert.setStoreType(StoreConst.IS_OWN_STORE);
        count = storeApplyWriteMapper.insert(storeApplyInsert);
        if (count == 0) {
            throw new MallException("添加商家申请表失败，请重试");
        }

        //判断联系人手机号是否重复，若重复，进行解绑
        StoreCertificateExample storeCertificateExample = new StoreCertificateExample();
        storeCertificateExample.setContactPhone(ownStoreAddDTO.getContactPhone());
        List<StoreCertificate> storeCertificateList = storeCertificateReadMapper.listByExample(storeCertificateExample);
        if (!CollectionUtils.isEmpty(storeCertificateList)) {
            StoreCertificate storeCertificateUpdate = storeCertificateList.get(0);
            storeCertificateUpdate.setContactPhone("");
            storeCertificateWriteMapper.updateByPrimaryKeySelective(storeCertificateUpdate);
        }

        //插入store_certificate表
        StoreCertificate storeCertificate = new StoreCertificate();
        storeCertificate.setVendorId(vendor.getVendorId());
        storeCertificate.setVendorName(vendor.getVendorName());
        storeCertificate.setEnterType(StoreConst.APPLY_TYPE_COMPANY);
        storeCertificate.setCompanyProvinceCode("");
        storeCertificate.setCompanyCityCode("");
        storeCertificate.setCompanyAreaCode("");
        storeCertificate.setAreaInfo("");
        storeCertificate.setCompanyAddress("");
        storeCertificate.setPersonCardUp("");
        storeCertificate.setPersonCardDown("");
        storeCertificate.setContactName(ownStoreAddDTO.getContactName());
        storeCertificate.setContactPhone(ownStoreAddDTO.getContactPhone());
        storeCertificate.setCompanyName(ownStoreAddDTO.getStoreName());
        count = storeCertificateWriteMapper.insert(storeCertificate);
        if (count == 0) {
            throw new MallException("添加店铺资质表失败，请重试");
        }
        return count;
    }

    /**
     * 根据storeId删除店铺表
     *
     * @param storeId storeId
     * @return
     */
    @Transactional
    public Integer deleteStore(Long storeId) {
        //根据storeId查询店铺
        Store store = storeReadMapper.getByPrimaryKey(storeId);
        if (store == null) {
            throw new MallException("店铺不存在");
        }
        //只有关闭的店铺才可以删除
        if (!store.getState().equals(StoreConst.STORE_STATE_CLOSE)) {
            throw new MallException("店铺状态为关闭时才可以删除");
        }

        Integer count;
        store.setState(StoreConst.STORE_STATE_DELETE);
        count = storeWriteMapper.updateByPrimaryKeySelective(store);
        if (count == 0) {
            log.error("根据storeId：" + storeId + "删除店铺表失败");
            throw new MallException("删除店铺表失败,请重试");
        }

        //禁止vendor登陆
        VendorExample vendorExample = new VendorExample();
        vendorExample.setStoreId(storeId);
        List<Vendor> vendors = vendorReadMapper.listByExample(vendorExample);
        for (Vendor vendor : vendors) {
            vendor.setIsAllowLogin(VendorConst.NOT_ALLOW_LOGIN);
            count = vendorWriteMapper.updateByPrimaryKeySelective(vendor);
            if (count == 0) {
                throw new MallException("编辑商户管理员表失败，请重试");
            }
        }

        //删除与店铺关联的store_apply表
        StoreApplyExample storeApplyExample = new StoreApplyExample();
        storeApplyExample.setStoreId(storeId);
        count = storeApplyWriteMapper.deleteByExample(storeApplyExample);
        if (count == 0) {
            throw new MallException("删除商家申请表信息失败，请重试");
        }

        //删除与店铺关联的store_certificate
        vendorExample.setIsStoreAdmin(VendorConst.IS_STORE_ADMIN_1);
        Vendor vendor = vendorReadMapper.listByExample(vendorExample).get(0);
        StoreCertificateExample storeCertificateExample = new StoreCertificateExample();
        storeCertificateExample.setVendorId(vendor.getVendorId());
        count = storeCertificateWriteMapper.deleteByExample(storeCertificateExample);
        if (count == 0) {
            throw new MallException("删除店铺资质信息表失败，请重试");
        }
        return count;
    }

    /**
     * 根据storeId更新店铺表
     *
     * @param store
     * @return
     */
    public Integer updateStore(Store store) {
        int count = storeWriteMapper.updateByPrimaryKeySelective(store);
        if (count == 0) {
            log.error("根据storeId：" + store.getStoreId() + "更新店铺表失败");
            throw new MallException("更新店铺表失败,请重试");
        }
        return count;
    }

    /**
     * 根据storeId更新自营店铺表
     *
     * @param ownStoreUpdateDTO
     * @return
     */
    @Transactional
    public Integer updateStore(OwnStoreUpdateDTO ownStoreUpdateDTO) throws Exception {
        //根据storeId获取店铺信息
        Store storeDb = storeReadMapper.getByPrimaryKey(ownStoreUpdateDTO.getStoreId());
        AssertUtil.notNull(storeDb, "未获取到店铺信息");

        //修改store表
        Store storeUpdate = new Store();
        PropertyUtils.copyProperties(storeUpdate, ownStoreUpdateDTO);
        if (!StringUtil.isNullOrZero(ownStoreUpdateDTO.getBillType()) && !StringUtils.isEmpty(ownStoreUpdateDTO.getBillDays())) {
            //结算日期
            String[] billDayArr = ownStoreUpdateDTO.getBillDays().split(",");

            Boolean flag = false;
            for (String billDay : billDayArr) {
                if (ownStoreUpdateDTO.getBillType() == StoreConst.BILL_TYPE_WEEK) {
                    for (int i = 1; i < 8; i++) {
                        if (Integer.parseInt(billDay) == i) {
                            flag = true;
                        }
                    }
                } else if (ownStoreUpdateDTO.getBillType() == StoreConst.BILL_TYPE_MONTH) {
                    for (int i = 1; i < 32; i++) {
                        if (Integer.parseInt(billDay) == i) {
                            flag = true;
                        }
                    }
                }
            }
            if (flag == false) {
                throw new MallException("请选择结算周期");
            }

            storeUpdate.setBillDay(ownStoreUpdateDTO.getBillDays());
        }

        int count;
        count = storeWriteMapper.updateByPrimaryKeySelective(storeUpdate);
        if (count == 0) {
            log.error("根据storeId：" + storeUpdate.getStoreId() + "更新店铺表失败");
            throw new MallException("更新店铺表失败,请重试");
        }

        //根据storeId获取vendor信息
        VendorExample example = new VendorExample();
        example.setStoreId(ownStoreUpdateDTO.getStoreId());
        example.setIsStoreAdmin(VendorConst.IS_STORE_ADMIN_1);
        Vendor vendorDB = vendorReadMapper.listByExample(example).get(0);

        //若手机号不为空，修改vendor账号手机号
        if (!StringUtils.isEmpty(ownStoreUpdateDTO.getContactPhone())) {
            //判断管理员手机号是否重复，若重复，进行解绑
            VendorExample vendorMobileExample = new VendorExample();
            vendorMobileExample.setVendorMobile(ownStoreUpdateDTO.getContactPhone());
            List<Vendor> vendorList = vendorReadMapper.listByExample(vendorMobileExample);
            if (!CollectionUtils.isEmpty(vendorList)) {
                Vendor vendorUpdate = vendorList.get(0);
                vendorUpdate.setVendorMobile("");
                vendorWriteMapper.updateByPrimaryKeySelective(vendorUpdate);
            }
            //不重复，修改管理员手机号
            vendorDB.setVendorMobile(ownStoreUpdateDTO.getContactPhone());
            vendorWriteMapper.updateByPrimaryKeySelective(vendorDB);
        }

        //修改store_certificate表
        StoreCertificate storeCertificateUpdate = new StoreCertificate();
        Boolean isCertificateUpdate = false;
        StoreCertificateExample storeCertificateExample = new StoreCertificateExample();
        storeCertificateExample.setVendorId(vendorDB.getVendorId());
        if (!StringUtils.isEmpty(ownStoreUpdateDTO.getContactName())) {
            storeCertificateUpdate.setContactName(ownStoreUpdateDTO.getContactName());
            isCertificateUpdate = true;
        }
        if (!StringUtils.isEmpty(ownStoreUpdateDTO.getContactPhone())) {
            //判断联系人手机号是否重复，若重复，进行解绑
            StoreCertificateExample storeCertificateExampleByMobile = new StoreCertificateExample();
            storeCertificateExampleByMobile.setContactPhone(ownStoreUpdateDTO.getContactPhone());
            List<StoreCertificate> storeCertificateList = storeCertificateReadMapper.listByExample(storeCertificateExampleByMobile);
            if (!CollectionUtils.isEmpty(storeCertificateList)) {
                StoreCertificate storeCertificateEdit = storeCertificateList.get(0);
                storeCertificateEdit.setContactPhone("");
                storeCertificateWriteMapper.updateByPrimaryKeySelective(storeCertificateEdit);
            }
            //不重复，修改联系人手机号
            storeCertificateUpdate.setContactPhone(ownStoreUpdateDTO.getContactPhone());
            isCertificateUpdate = true;
        }
        if (isCertificateUpdate == true) {
            count = storeCertificateWriteMapper.updateByExampleSelective(storeCertificateUpdate, storeCertificateExample);
            if (count == 0) {
                throw new MallException("更新商家资质表失败，请重试");
            }
        }
        return count;
    }

    /**
     * 根据storeId修改入驻店铺信息
     *
     * @param storeUpdateDTO
     * @return
     */
    @Transactional
    public Integer editStoreInfo(StoreUpdateDTO storeUpdateDTO) {
        //根据storeId查询store表
        Store storeDB = storeReadMapper.getByPrimaryKey(storeUpdateDTO.getStoreId());
        if (storeDB == null) {
            throw new MallException("店铺不存在");
        }

        //修改store表
        Store storeUpdate = new Store();
        storeUpdate.setStoreId(storeDB.getStoreId());
        Boolean flag = false;
        if (!StringUtils.isEmpty(storeUpdateDTO.getStoreGradeId())) {
            //店铺等级id
            storeUpdate.setStoreGradeId(storeUpdateDTO.getStoreGradeId());
            //根据storeGradeId获取等级名称
            StoreGrade storeGrade = storeGradeReadMapper.getByPrimaryKey(storeUpdateDTO.getStoreGradeId());
            storeUpdate.setStoreGradeName(storeGrade.getGradeName());
            flag = true;
        }

        if (!StringUtil.isNullOrZero(storeUpdateDTO.getOpenTime())) {
            //开店时长
            storeUpdate.setOpenTime(storeUpdateDTO.getOpenTime());
            //店铺到期时间
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(storeDB.getCreateTime());
            calendar.add(Calendar.YEAR, storeUpdateDTO.getOpenTime());
            storeUpdate.setStoreExpireTime(calendar.getTime());
            flag = true;
        }

        //修改结算周期
        if (!StringUtil.isNullOrZero(storeUpdateDTO.getBillType()) && !StringUtils.isEmpty(storeUpdateDTO.getBillDays())) {
            storeUpdate.setBillType(storeUpdateDTO.getBillType());
            //结算日期
            String[] billDayArr = storeUpdateDTO.getBillDays().split(",");

            Boolean billFlag = false;
            for (String billDay : billDayArr) {
                if (storeUpdateDTO.getBillType() == StoreConst.BILL_TYPE_WEEK) {
                    for (int i = 1; i < 8; i++) {
                        if (Integer.parseInt(billDay) == i) {
                            billFlag = true;
                        }
                    }
                } else if (storeUpdateDTO.getBillType() == StoreConst.BILL_TYPE_MONTH) {
                    for (int i = 1; i < 32; i++) {
                        if (Integer.parseInt(billDay) == i) {
                            billFlag = true;
                        }
                    }
                }
            }
            if (billFlag == false) {
                throw new MallException("请选择结算周期");
            }
            storeUpdate.setBillDay(storeUpdateDTO.getBillDays());
        }

        int count;
        if (flag == true) {
            count = storeWriteMapper.updateByPrimaryKeySelective(storeUpdate);
            if (count == 0) {
                log.error("根据storeId：" + storeUpdate.getStoreId() + "更新店铺表失败");
                throw new MallException("更新店铺表失败,请重试");
            }
        }

        //修改store_apply表
        StoreApply storeApplyUpdate = new StoreApply();
        Boolean isUpdate = false;
        StoreApplyExample storeApplyExample = new StoreApplyExample();
        storeApplyExample.setStoreId(storeUpdateDTO.getStoreId());
        if (!StringUtils.isEmpty(storeUpdateDTO.getStoreGradeId())) {
            storeApplyUpdate.setStoreGradeId(storeUpdateDTO.getStoreGradeId());
            isUpdate = true;
        }
        if (!StringUtils.isEmpty(storeUpdateDTO.getOpenTime())) {
            storeApplyUpdate.setApplyYear(storeUpdateDTO.getOpenTime());
            isUpdate = true;
        }
        if (isUpdate == true) {
            count = storeApplyWriteMapper.updateByExampleSelective(storeApplyUpdate, storeApplyExample);
            if (count == 0) {
                throw new MallException("更新商家申请表失败，请重试");
            }
        }

        //根据storeId查询vendor表
        VendorExample vendorExample = new VendorExample();
        vendorExample.setStoreId(storeDB.getStoreId());
        vendorExample.setIsStoreAdmin(VendorConst.IS_STORE_ADMIN_1);
        Vendor vendor = vendorReadMapper.listByExample(vendorExample).get(0);

        //修改store_certificate表
        StoreCertificate storeCertificateUpdate = new StoreCertificate();
        if (!StringUtils.isEmpty(storeUpdateDTO.getContactPhone())) {
            //判断联系人手机号是否重复，若重复，进行解绑
            StoreCertificateExample storeCertificateExampleByMobile = new StoreCertificateExample();
            storeCertificateExampleByMobile.setContactPhone(storeUpdateDTO.getContactPhone());
            List<StoreCertificate> storeCertificateList = storeCertificateReadMapper.listByExample(storeCertificateExampleByMobile);
            if (!CollectionUtils.isEmpty(storeCertificateList)) {
                StoreCertificate storeCertificateEdit = storeCertificateList.get(0);
                storeCertificateEdit.setContactPhone("");
                storeCertificateWriteMapper.updateByPrimaryKeySelective(storeCertificateEdit);
            }
            //不重复，修改联系人手机号
            storeCertificateUpdate.setContactPhone(storeUpdateDTO.getContactPhone());
        }
        StoreCertificateExample storeCertificateExample = new StoreCertificateExample();
        storeCertificateExample.setVendorId(vendor.getVendorId());
        storeCertificateUpdate.setContactName(storeUpdateDTO.getContactName());
        storeCertificateUpdate.setContactPhone(storeUpdateDTO.getContactPhone());
        storeCertificateUpdate.setCompanyProvinceCode(storeUpdateDTO.getCompanyProvinceCode());
        storeCertificateUpdate.setCompanyCityCode(storeUpdateDTO.getCompanyCityCode());
        storeCertificateUpdate.setCompanyAreaCode(storeUpdateDTO.getCompanyAreaCode());
        storeCertificateUpdate.setAreaInfo(storeUpdateDTO.getAreaInfo());
        storeCertificateUpdate.setCompanyAddress(storeUpdateDTO.getCompanyAddress());
        storeCertificateUpdate.setPersonCardUp(storeUpdateDTO.getPersonCardUp());
        storeCertificateUpdate.setPersonCardDown(storeUpdateDTO.getPersonCardDown());
        storeCertificateUpdate.setCompanyName(storeUpdateDTO.getCompanyName());
        storeCertificateUpdate.setBusinessLicenseImage(storeUpdateDTO.getBusinessLicenseImage());
        storeCertificateUpdate.setMoreQualification1(storeUpdateDTO.getMoreQualification1());
        storeCertificateUpdate.setMoreQualification2(storeUpdateDTO.getMoreQualification2());
        storeCertificateUpdate.setMoreQualification3(storeUpdateDTO.getMoreQualification3());
        count = storeCertificateWriteMapper.updateByExampleSelective(storeCertificateUpdate, storeCertificateExample);
        if (count == 0) {
            throw new MallException("更新商家资质表失败，请重试");
        }
        return count;
    }

    /**
     * 根据storeId关闭/开启店铺
     *
     * @param storeId
     * @param state
     * @return
     */
    public Integer lockUpStore(Long storeId, Integer state) {
        //根据storeId查询店铺
        Store store = storeReadMapper.getByPrimaryKey(storeId);
        if (store == null) {
            throw new MallException("店铺不存在");
        }

        Integer count;
        if (state == 2) {
            //关闭店铺
            store.setState(StoreConst.STORE_STATE_CLOSE);
            count = storeWriteMapper.updateByPrimaryKeySelective(store);
            if (count == 0) {
                throw new MallException("关闭店铺失败,请重试");
            }
            //下架店铺的商品
            GoodsExample example = new GoodsExample();
            example.setStoreId(storeId);
            Goods goods = new Goods();
            goods.setState(GoodsConst.GOODS_STATE_LOWER_BY_SYSTEM);
            goods.setUpdateTime(new Date());
            goodsWriteMapper.updateByExampleSelective(goods, example);
            //修改商品更新时间
            GoodsExtendExample extendExample = new GoodsExtendExample();
            extendExample.setStoreId(storeId);
            GoodsExtend goodsExtend = new GoodsExtend();
            goodsExtend.setOfflineReason("店铺关闭");
            goodsExtend.setOfflineComment("平台后台操作");
            goodsExtendWriteMapper.updateByExampleSelective(goodsExtend, extendExample);
        } else {
            //开启店铺
            store.setState(StoreConst.STORE_STATE_OPEN);
            count = storeWriteMapper.updateByPrimaryKeySelective(store);
            if (count == 0) {
                throw new MallException("开启店铺失败,请重试");
            }
        }
        return count;
    }

    /**
     * 根据storeId获取店铺表详情
     *
     * @param storeId storeId
     * @return
     */
    public Store getStoreByStoreId(Long storeId) {
        return storeReadMapper.getByPrimaryKey(storeId);
    }

    /**
     * 根据条件获取店铺表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<Store> getStoreList(StoreExample example, PagerInfo pager) {
        List<Store> storeList;
        if (pager != null) {
            pager.setRowsCount(storeReadMapper.countByExample(example));
            storeList = storeReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            storeList = storeReadMapper.listByExample(example);
        }
        return storeList;
    }

    /**
     * 定时任务设定商家的评分
     *
     * @return
     */
    public boolean jobSetStoreScore() {
        StoreExample example = new StoreExample();
        example.setState(StoreConst.STORE_STATE_OPEN);
        List<Store> storeList = storeReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(storeList)) {
            for (Store store : storeList) {
                CommentsDTO commentsDto = storeCommentReadMapper.getStoreScoreSum(store.getStoreId());
                if (commentsDto != null && commentsDto.getNumber() != null && commentsDto.getNumber() > 0) {
                    Store storeNew = new Store();
                    storeNew.setStoreId(store.getStoreId());
                    BigDecimal descriptionScore = (new BigDecimal(commentsDto.getDescription()))
                            .divide((new BigDecimal(commentsDto.getNumber())), 1, BigDecimal.ROUND_HALF_UP);
                    storeNew.setDescriptionScore(descriptionScore.toString());

                    BigDecimal serviceScore = (new BigDecimal(commentsDto.getServiceAttitude()))
                            .divide((new BigDecimal(commentsDto.getNumber())), 1, BigDecimal.ROUND_HALF_UP);
                    storeNew.setServiceScore(serviceScore.toString());

                    BigDecimal deliverGoodsScore = (new BigDecimal(commentsDto.getDeliverSpeed())).divide(
                            (new BigDecimal(commentsDto.getNumber())), 1, BigDecimal.ROUND_HALF_UP);
                    storeNew.setDeliverScore(deliverGoodsScore.toString());

                    int update = storeWriteMapper.updateByPrimaryKeySelective(storeNew);
                    AssertUtil.isTrue(update == 0, "修改商家评分时失败：storeId=" + store.getStoreId());
                }
            }
        }
        return true;
    }

    /**
     * 定时任务设定商家各项统计数据
     *
     * @return
     */
    public boolean jobStoreStatistics() {
        StoreExample example = new StoreExample();
        example.setState(StoreConst.STORE_STATE_OPEN);
        List<Store> storeList = storeReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(storeList)) {
            for (Store store : storeList) {
                Store storeNew = new Store();
                storeNew.setStoreId(store.getStoreId());
                // 商品数量
                GoodsExample goodsExample = new GoodsExample();
                goodsExample.setStoreId(store.getStoreId());
                goodsExample.setState(GoodsConst.GOODS_STATE_UPPER);
                storeNew.setGoodsNumber(goodsReadMapper.countByExample(goodsExample));

                // 店铺收藏
                MemberFollowStoreExample example2 = new MemberFollowStoreExample();
                example2.setStoreId(store.getStoreId());
                List<MemberFollowStore> memberFollowStoreList = memberFollowStoreReadMapper.listByExample(example2);
                storeNew.setFollowNumber(memberFollowStoreList.size());

                // 店铺总销售金额、店铺完成订单量
                OrderExample orderExample = new OrderExample();
                orderExample.setStoreId(store.getStoreId());
                orderExample.setOrderState(OrderConst.ORDER_STATE_40);
                List<OrderDayDTO> dtoList = orderReadMapper.getOrderDayDto(orderExample);

                if (!CollectionUtils.isEmpty(dtoList)) {
                    BigDecimal orderAmount = BigDecimal.ZERO;
                    BigDecimal refundAmount = BigDecimal.ZERO;
                    Integer count = 0;
                    for (OrderDayDTO orderDayDto : dtoList) {
                        orderAmount = orderAmount.add(orderDayDto.getOrderAmount());
                        refundAmount = refundAmount.add(orderDayDto.getRefundAmount());
                        count += orderDayDto.getCount();
                    }
                    storeNew.setStoreTotalSale(orderAmount.subtract(refundAmount));
                    storeNew.setOrderFinishedCount(count);
                }

                // 店铺总订单量
                orderExample.setOrderState(null);
                storeNew.setOrderTotalCount(orderReadMapper.countByExample(orderExample));
                int update = storeWriteMapper.updateByPrimaryKeySelective(storeNew);
                AssertUtil.isTrue(update == 0, "统计商家数据时失败：storeId=" + store.getStoreId());
            }
        }
        return true;
    }

    /**
     * 系统自动完成对店铺的冻结或续签管理
     *
     * @return
     */
    public List<Store> jobSystemStoreManage() {
        List<Store> stores = new ArrayList<>();
        //查询审核通过状态的store，判断店铺是否到期
        StoreExample example = new StoreExample();
        example.setState(StoreConst.STORE_STATE_OPEN);
        example.setIsOwnStore(StoreConst.NO_OWN_STORE);
        List<Store> storeList = storeReadMapper.listByExample(example);

        //所有店铺共用一个固定的到期时限提醒
        int reminderDay = Integer.parseInt(stringRedisTemplate.opsForValue().get("store_expired_reminder_day"));
        //计算到期日期
        Date expireTime = TimeUtil.getDateApartDay(-reminderDay);

        if (!CollectionUtils.isEmpty(storeList)) {
            for (Store store : storeList) {
                Date storeExpireTime = store.getStoreExpireTime();
                //只通知一次
                if (storeExpireTime.before(expireTime) && storeExpireTime.after(TimeUtil.getDateApartDay(expireTime, -1))) {
                    stores.add(store);
                    continue;
                }

                Date startTime = TimeUtil.changeFormatYMD(storeExpireTime);
                Date endTime = TimeUtil.changeFormatYMDHMS(storeExpireTime);
                //店铺到期，查询续签表
                StoreRenewExample renewExample = new StoreRenewExample();
                renewExample.setStoreId(store.getStoreId());
                renewExample.setState(StoreConst.STORE_RENEW_STATE_SUCCESS);
                renewExample.setStartTimeAfter(startTime);
                List<StoreRenew> storeRenewList = storeRenewReadMapper.listByExample(renewExample);
                if (CollectionUtils.isEmpty(storeRenewList)) {
                    //续签表中没有符合条件的续签申请，冻结店铺，冻结店铺的商品
                    //判断是否今天到期
                    if (startTime.before(new Date()) && endTime.after(startTime)) {
                        //冻结店铺
                        Store storeNew = new Store();
                        storeNew.setStoreId(store.getStoreId());
                        storeNew.setState(StoreConst.STORE_STATE_CLOSE);
                        int row = storeWriteMapper.updateByPrimaryKeySelective(storeNew);
                        AssertUtil.isTrue(row == 0, "冻结店铺时失败！");

                        // 冻结店铺的商品
                        GoodsExample goodsExample = new GoodsExample();
                        goodsExample.setStoreId(store.getStoreId());
                        Goods goods = new Goods();
                        goods.setState(GoodsConst.GOODS_STATE_LOWER_BY_STORE);
                        row = goodsWriteMapper.updateByExampleSelective(goods, goodsExample);
                        AssertUtil.isTrue(row == 0, "冻结店铺商品时失败！");
                    }
                } else {
                    //续签表中有符合条件的续签申请，续签店铺，修改store表的店铺等级、开店时长、开店结束时间
                    StoreRenew storeRenew = storeRenewList.get(0);
                    Store updateStore = new Store();
                    updateStore.setStoreId(store.getStoreId());
                    updateStore.setStoreGradeId(storeRenew.getGradeId());
                    updateStore.setOpenTime(storeRenew.getDuration());
                    updateStore.setStoreExpireTime(storeRenew.getEndTime());
                    int row = storeWriteMapper.updateByPrimaryKeySelective(updateStore);
                    AssertUtil.isTrue(row == 0, "更新店铺信息时失败！");
                }
            }
        }
        return stores;
    }

    /**
     * 定时更新店铺商品总销量、总浏览量
     *
     * @return
     */
    public boolean jobSetStoreSalesAndLookVolume() {
        //获取所有开启状态的店铺
        StoreExample storeExample = new StoreExample();
        storeExample.setState(StoreConst.STORE_STATE_OPEN);
        List<Store> storeList = storeReadMapper.listByExample(storeExample);
        if (!CollectionUtils.isEmpty(storeList)) {
            for (Store store : storeList) {
                //更新每一个店铺商品总销量、总浏览量
                Store storeUpdate = new Store();
                storeUpdate.setStoreId(store.getStoreId());
                //店铺商品总销量
                int allSaleNum = 0;
                //店铺商品总浏览量
                int allLookNum = 0;
                GoodsExample goodsExample = new GoodsExample();
                goodsExample.setStoreId(store.getStoreId());
                List<Goods> goodsList = goodsReadMapper.listByExample(goodsExample);
                if (!CollectionUtils.isEmpty(goodsList)) {
                    for (Goods goods : goodsList) {
                        int saleNum = goods.getActualSales() + goods.getVirtualSales();
                        allSaleNum += saleNum;

                        GoodsExtendExample goodsExtendExample = new GoodsExtendExample();
                        goodsExtendExample.setGoodsId(goods.getGoodsId());
                        GoodsExtend goodsExtend = goodsExtendReadMapper.listByExample(goodsExtendExample).get(0);
                        allLookNum += goodsExtend.getClickNumber();
                    }
                }
                //获取店铺商品总销量
                storeUpdate.setStoreSalesVolume(allSaleNum);
                //获取店铺商品总浏览量
                storeUpdate.setStoreLookVolume(allLookNum);
                int update = storeWriteMapper.updateByPrimaryKeySelective(storeUpdate);
                AssertUtil.isTrue(update == 0, "更新店铺商品总销量、总浏览量时失败：storeId=" + storeUpdate.getStoreId());
            }
        }
        return true;
    }

    /**
     * 根据条件获取店铺数量
     *
     * @param example 查询条件信息
     * @return
     */
    public Integer getStoreCount(StoreExample example) {
        return storeReadMapper.countByExample(example);
    }
}