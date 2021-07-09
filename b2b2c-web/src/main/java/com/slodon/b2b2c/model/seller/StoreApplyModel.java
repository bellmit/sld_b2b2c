package com.slodon.b2b2c.model.seller;

import com.slodon.b2b2c.core.constant.BizTypeConst;
import com.slodon.b2b2c.core.constant.OrderPaymentConst;
import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.uid.GoodsIdGenerator;
import com.slodon.b2b2c.dao.read.goods.GoodsCategoryReadMapper;
import com.slodon.b2b2c.dao.read.seller.StoreApplyReadMapper;
import com.slodon.b2b2c.dao.read.seller.StoreBindCategoryReadMapper;
import com.slodon.b2b2c.dao.read.seller.StoreCertificateReadMapper;
import com.slodon.b2b2c.dao.read.seller.StoreGradeReadMapper;
import com.slodon.b2b2c.dao.write.seller.StoreApplyWriteMapper;
import com.slodon.b2b2c.dao.write.seller.StoreBindCategoryWriteMapper;
import com.slodon.b2b2c.dao.write.seller.StoreCertificateWriteMapper;
import com.slodon.b2b2c.goods.pojo.GoodsCategory;
import com.slodon.b2b2c.seller.dto.StoreApplyDTO;
import com.slodon.b2b2c.seller.example.StoreApplyExample;
import com.slodon.b2b2c.seller.example.StoreBindCategoryExample;
import com.slodon.b2b2c.seller.example.StoreCertificateExample;
import com.slodon.b2b2c.seller.pojo.*;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.smartid.client.utils.SmartId;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class StoreApplyModel {

    @Resource
    private StoreApplyReadMapper storeApplyReadMapper;
    @Resource
    private StoreApplyWriteMapper storeApplyWriteMapper;
    @Resource
    private StoreGradeReadMapper storeGradeReadMapper;
    @Resource
    private StoreCertificateReadMapper storeCertificateReadMapper;
    @Resource
    private StoreCertificateWriteMapper storeCertificateWriteMapper;
    @Resource
    private StoreBindCategoryReadMapper storeBindCategoryReadMapper;
    @Resource
    private StoreBindCategoryWriteMapper storeBindCategoryWriteMapper;
    @Resource
    private GoodsCategoryReadMapper goodsCategoryReadMapper;

    /**
     * 新增商家申请表
     *
     * @param storeApply
     * @return
     */
    public Integer saveStoreApply(StoreApply storeApply) {
        int count = storeApplyWriteMapper.insert(storeApply);
        if (count == 0) {
            throw new MallException("添加商家申请表失败，请重试");
        }
        return count;
    }

    /**
     * 保存商家申请信息
     *
     * @param storeApplyDTO 商户申请信息
     * @param vendor        商户账号
     * @return
     */
    @Transactional
    public Integer saveStoreApplyInfo(StoreApplyDTO storeApplyDTO, Vendor vendor) throws Exception {
        //重复判断：企业入驻时公司名称不能重复
        String companyName = null;
        if (storeApplyDTO.getEnterType() == StoreConst.APPLY_TYPE_COMPANY) {
            companyName = storeApplyDTO.getCompanyName().trim();
            if (!this.validApplyUnique(companyName, StoreConst.APPLY_VALID_TYPE_COMPANY_NAME, vendor.getVendorId())) {
                throw new MallException("公司名称已存在");
            }
        }
        //重复判断：店铺名称不能重复
        String storeName = storeApplyDTO.getStoreName().trim();
        if (!this.validApplyUnique(storeName, StoreConst.APPLY_VALID_TYPE_STORE_NAME, vendor.getVendorId())) {
            throw new MallException("店铺名称已存在");
        }

        //插入store_apply表
        StoreApply storeApply = new StoreApply();
        storeApply.setVendorId(vendor.getVendorId());
        storeApply.setVendorName(vendor.getVendorName());
        storeApply.setStoreName(storeName);
        storeApply.setState(StoreConst.STATE_1_SEND_APPLY);
        storeApply.setApplyYear(storeApplyDTO.getApplyYear());
        storeApply.setStoreGradeId(storeApplyDTO.getStoreGradeId());
        storeApply.setSubmitTime(new Date());
        storeApply.setStoreId(SmartId.nextId(BizTypeConst.STORE));
        storeApply.setStoreType(StoreConst.NO_OWN_STORE);
        //根据gradeId查询收费标准
        StoreGrade storeGrade = storeGradeReadMapper.getByPrimaryKey(storeApplyDTO.getStoreGradeId());
        storeApply.setPayAmount(new BigDecimal(storeGrade.getPrice()).multiply(new BigDecimal(storeApplyDTO.getApplyYear())));
        int count;
        count = storeApplyWriteMapper.insert(storeApply);
        if (count == 0) {
            throw new MallException("店铺申请表添加失败，请重试");
        }

        //插入store_certificate表
        StoreCertificate storeCertificate = new StoreCertificate();
        PropertyUtils.copyProperties(storeCertificate, storeApplyDTO);

        storeCertificate.setVendorId(vendor.getVendorId());
        storeCertificate.setVendorName(vendor.getVendorName());
        storeCertificate.setCompanyName(companyName);

        count = storeCertificateWriteMapper.insert(storeCertificate);
        if (count == 0) {
            throw new MallException("店铺资质表添加失败，请重试");
        }

        //插入store_bind_category表
        StoreBindCategory storeBindCategory = new StoreBindCategory();
        storeBindCategory.setStoreId(0L);
        storeBindCategory.setCreateVendorId(vendor.getVendorId());
        storeBindCategory.setCreateTime(new Date());
        storeBindCategory.setState(StoreConst.STORE_CATEGORY_STATE_SEND);

        String[] split = storeApplyDTO.getGoodsCategoryIds().split(",");
        for (String goodsCategoryId : split) {
            String[] split1 = goodsCategoryId.split("-");
            storeBindCategory.setGoodsCategoryId1(Integer.parseInt(split1[0]));
            storeBindCategory.setGoodsCategoryId2(Integer.parseInt(split1[1]));
            storeBindCategory.setGoodsCategoryId3(Integer.parseInt(split1[2]));
            //根据一级分类id查询一级分类名称
            GoodsCategory goodsCategory1 = goodsCategoryReadMapper.getByPrimaryKey(split1[0]);
            //根据二级分类id查询二级分类名称
            GoodsCategory goodsCategory2 = goodsCategoryReadMapper.getByPrimaryKey(split1[1]);

            //根据三级分类id查询三级分类名称
            GoodsCategory goodsCategory3 = goodsCategoryReadMapper.getByPrimaryKey(split1[2]);
            //拼接类目组合名称
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(goodsCategory1.getCategoryName());
            stringBuffer.append(">");
            stringBuffer.append(goodsCategory2.getCategoryName());
            stringBuffer.append(">");
            stringBuffer.append(goodsCategory3.getCategoryName());
            storeBindCategory.setGoodsCateName(stringBuffer.toString());
            count = storeBindCategoryWriteMapper.insert(storeBindCategory);
            if (count == 0) {
                throw new MallException("店铺可用商品分类表添加失败，请重试");
            }

        }
        return count;
    }

    /**
     * 根据applyId删除商家申请表
     *
     * @param applyId applyId
     * @return
     */
    public Integer deleteStoreApply(Integer applyId) {
        if (StringUtils.isEmpty(applyId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = storeApplyWriteMapper.deleteByPrimaryKey(applyId);
        if (count == 0) {
            log.error("根据applyId：" + applyId + "删除商家申请表失败");
            throw new MallException("删除商家申请表失败,请重试");
        }
        return count;
    }

    /**
     * 根据applyId更新商家申请表
     *
     * @param storeApply
     * @return
     */
    public Integer updateStoreApply(StoreApply storeApply) {
        if (StringUtils.isEmpty(storeApply.getApplyId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = storeApplyWriteMapper.updateByPrimaryKeySelective(storeApply);
        if (count == 0) {
            log.error("根据applyId：" + storeApply.getApplyId() + "更新商家申请表失败");
            throw new MallException("更新商家申请表失败,请重试");
        }
        return count;
    }

    /**
     * 根据applyId更新商家申请表
     *
     * @param storeApplyDTO 修改的申请信息
     * @param vendor        商户账号
     * @return
     */
    @Transactional
    public Integer updateStoreApplyInfo(StoreApplyDTO storeApplyDTO, Vendor vendor) throws Exception {
        //重复判断：企业入驻时公司名称不能重复
        String companyName = null;
        if (storeApplyDTO.getEnterType() == StoreConst.APPLY_TYPE_COMPANY) {
            companyName = storeApplyDTO.getCompanyName().trim();
            if (!StringUtils.isEmpty(companyName)) {
                if (!this.validApplyUnique(companyName, StoreConst.APPLY_VALID_TYPE_COMPANY_NAME, vendor.getVendorId())) {
                    throw new MallException("公司名称已存在");
                }
            }
        }
        //重复判断：店铺名称不能重复
        String storeName = storeApplyDTO.getStoreName().trim();
        if (!StringUtils.isEmpty(storeName)) {
            if (!this.validApplyUnique(storeName, StoreConst.APPLY_VALID_TYPE_STORE_NAME, vendor.getVendorId())) {
                throw new MallException("店铺名称已存在");
            }
        }

        StoreCertificate storeCertificate = storeCertificateReadMapper.getByPrimaryKey(storeApplyDTO.getCertificateId());
        PropertyUtils.copyProperties(storeCertificate, storeApplyDTO);

        //更新store_apply表
        StoreApply storeApply = storeApplyReadMapper.getByPrimaryKey(storeApplyDTO.getApplyId());
        PropertyUtils.copyProperties(storeApply, storeApplyDTO);
        storeApply.setStoreName(storeName);
        storeApply.setState(StoreConst.STATE_1_SEND_APPLY);
        storeApply.setSubmitTime(new Date());

        int count;
        count = storeApplyWriteMapper.updateByPrimaryKeySelective(storeApply);
        if (count == 0) {
            log.error("根据applyId：" + storeApply.getApplyId() + "更新商家申请表失败");
            throw new MallException("更新商家申请表失败,请重试");
        }

        //更新store_certificate表
        storeCertificate.setCompanyName(companyName);
        count = storeCertificateWriteMapper.updateByPrimaryKeySelective(storeCertificate);
        if (count == 0) {
            log.error("根据certificateId：" + storeCertificate.getCertificateId() + "更新商家资质表失败");
            throw new MallException("更新商家资质表失败,请重试");
        }

        //更新store_bind_category表
        StoreBindCategoryExample storeBindCategoryExample = new StoreBindCategoryExample();
        storeBindCategoryExample.setCreateVendorId(vendor.getVendorId());
        storeBindCategoryWriteMapper.deleteByExample(storeBindCategoryExample);

        StoreBindCategory storeBindCategory = new StoreBindCategory();
        storeBindCategory.setStoreId(0L);
        storeBindCategory.setCreateVendorId(vendor.getVendorId());
        storeBindCategory.setCreateTime(new Date());
        storeBindCategory.setState(StoreConst.STORE_CATEGORY_STATE_SEND);

        String[] split = storeApplyDTO.getGoodsCategoryIds().split(",");
        for (String goodsCategoryId : split) {
            String[] split1 = goodsCategoryId.split("-");
            storeBindCategory.setGoodsCategoryId1(Integer.parseInt(split1[0]));
            storeBindCategory.setGoodsCategoryId2(Integer.parseInt(split1[1]));
            storeBindCategory.setGoodsCategoryId3(Integer.parseInt(split1[2]));
            //根据一级分类id查询一级分类名称
            GoodsCategory goodsCategory1 = goodsCategoryReadMapper.getByPrimaryKey(split1[0]);
            //根据二级分类id查询二级分类名称
            GoodsCategory goodsCategory2 = goodsCategoryReadMapper.getByPrimaryKey(split1[1]);

            //根据三级分类id查询三级分类名称
            GoodsCategory goodsCategory3 = goodsCategoryReadMapper.getByPrimaryKey(split1[2]);
            //拼接类目组合名称
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(goodsCategory1.getCategoryName());
            stringBuffer.append(">");
            stringBuffer.append(goodsCategory2.getCategoryName());
            stringBuffer.append(">");
            stringBuffer.append(goodsCategory3.getCategoryName());
            storeBindCategory.setGoodsCateName(stringBuffer.toString());
            count = storeBindCategoryWriteMapper.insert(storeBindCategory);
            if (count == 0) {
                throw new MallException("店铺可用商品分类表添加失败，请重试");
            }
    }
        return count;
}

    /**
     * 根据applyId审核商户入驻请求
     *
     * @param applyId        申请id
     * @param isPass         是否通过[true==通过,false==驳回]
     * @param refuseReason   审核原因
     * @param remark         备注
     * @param scalingBindIds 分佣比例集合：bindId1-scaling1,bindId2-scaling2,...
     * @param admin          审核管理员
     * @return
     */
    @Transactional
    public Integer audit(Integer applyId, Boolean isPass, String refuseReason, String remark, String scalingBindIds, Admin admin) {
        //通过applyId查询数据库中的请求
        StoreApply storeApply = storeApplyReadMapper.getByPrimaryKey(applyId);
        if (storeApply == null) {
            //数据库里不存在该商家的申请
            throw new MallException("您无权进行此操作");
        }

        //审核前的状态
        Integer oldState = storeApply.getState();
        if (oldState != StoreConst.STATE_1_SEND_APPLY) {
            throw new MallException("该申请不在审核状态");
        }

        //计算审核后的状态
        int newState;
        if (isPass) {
            newState = StoreConst.STATE_2_DONE_APPLY;
            //生成支付单号，计算支付金额
            storeApply.setPaySn(GoodsIdGenerator.getPaySn());
            //根据gradeId查询收费标准
            StoreGrade storeGrade = storeGradeReadMapper.getByPrimaryKey(storeApply.getStoreGradeId());
            storeApply.setPayAmount(new BigDecimal(storeGrade.getPrice()).multiply(new BigDecimal(storeApply.getApplyYear())));
            storeApply.setPaymentCode(OrderPaymentConst.PAYMENT_CODE_ONLINE);
            storeApply.setPaymentName(OrderPaymentConst.PAYMENT_NAME_ONLINE);
        } else {
            newState = StoreConst.STATE_3_FAIL_APPLY;
        }
        storeApply.setState(newState);
        //处理审核意见
        storeApply.setRefuseReason(refuseReason == null ? "" : refuseReason);
        storeApply.setAuditInfo(remark == null ? "" : remark);
        //更新store_apply表
        int count;
        count = storeApplyWriteMapper.updateByPrimaryKeySelective(storeApply);
        if (count == 0) {
            throw new MallException("更新店铺申请失败");
        }

        switch (storeApply.getState()) {
            case StoreConst.STATE_3_FAIL_APPLY:
                //审核失败，只修改store_apply表
                return count;
            case StoreConst.STATE_2_DONE_APPLY:
                //审核成功，❶修改store_apply表;❷修改store_bind_category表，填写分佣比例;❸新增一个店铺，插入store表,;❹将storeId插入到vendor表
                //获取所有的分佣比例
                String[] scalings = scalingBindIds.split(",");
                //获取bindId对应的分佣比例
                for (String sca : scalings) {
                    String[] split = sca.split("-");
                    String bindId = split[0];
                    String scaling = split[1];
                    if (Double.valueOf(scaling).intValue() > 1 && Double.valueOf(scaling).intValue() < 0) {
                        throw new MallException("请填写0到1之间的数字");
                    }
                    //根据bingId插入scaling
                    StoreBindCategory storeBindCategory = new StoreBindCategory();
                    storeBindCategory.setBindId(Integer.parseInt(bindId));
                    storeBindCategory.setScaling(new BigDecimal(scaling));
                    storeBindCategory.setState(StoreConst.STORE_CATEGORY_STATE_PASS);
                    storeBindCategory.setAuditAdminId(admin.getAdminId());
                    storeBindCategory.setAuditTime(new Date());
                    count = storeBindCategoryWriteMapper.updateByPrimaryKeySelective(storeBindCategory);
                    if (count == 0) {
                        throw new MallException("添加分佣比例失败，请重试");
                    }
                }
        }
        return count;
    }

    /**
     * 根据applyId获取商家申请表详情
     *
     * @param applyId applyId
     * @return
     */
    public StoreApply getStoreApplyByApplyId(Integer applyId) {
        return storeApplyReadMapper.getByPrimaryKey(applyId);
    }

    /**
     * 根据条件获取商家申请表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<StoreApply> getStoreApplyList(StoreApplyExample example, PagerInfo pager) {
        List<StoreApply> storeApplyList;
        if (pager != null) {
            pager.setRowsCount(storeApplyReadMapper.countByExample(example));
            storeApplyList = storeApplyReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            storeApplyList = storeApplyReadMapper.listByExample(example);
        }
        return storeApplyList;
    }

    /**
     * 入驻信息重复判断
     *
     * @param val      验证字段值
     * @param type     验证类型 1:公司名称；2:店铺名称
     * @param vendorId 商家用户id
     * @return true:无重复，通过验证；false:有重复
     */
    public Boolean validApplyUnique(String val, Integer type, Long vendorId) {
        boolean result = true;
        switch (type) {
            case StoreConst.APPLY_VALID_TYPE_COMPANY_NAME:
                StoreCertificateExample example = new StoreCertificateExample();
                example.setCompanyName(val.trim());
                List<StoreCertificate> certificates = storeCertificateReadMapper.listByExample(example);
                if (certificates != null && certificates.size() > 0 && !certificates.get(0).getVendorId().equals(vendorId)) {
                    result = false;
                }
                break;
            case StoreConst.APPLY_VALID_TYPE_STORE_NAME:
                StoreApplyExample example2 = new StoreApplyExample();
                example2.setStoreName(val.trim());
                List<StoreApply> storeApplies = storeApplyReadMapper.listByExample(example2);
                if (storeApplies != null && storeApplies.size() > 0 && !storeApplies.get(0).getVendorId().equals(vendorId)) {
                    result = false;
                }
                break;
        }
        return result;
    }
}