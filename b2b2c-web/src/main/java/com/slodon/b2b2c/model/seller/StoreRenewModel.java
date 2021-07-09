package com.slodon.b2b2c.model.seller;

import com.slodon.b2b2c.core.constant.OrderPaymentConst;
import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.constant.VendorConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.uid.GoodsIdGenerator;
import com.slodon.b2b2c.dao.read.seller.*;
import com.slodon.b2b2c.dao.write.seller.StoreRenewWriteMapper;
import com.slodon.b2b2c.dao.write.seller.StoreWriteMapper;
import com.slodon.b2b2c.seller.example.StoreCertificateExample;
import com.slodon.b2b2c.seller.example.StoreRenewExample;
import com.slodon.b2b2c.seller.example.VendorExample;
import com.slodon.b2b2c.seller.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class StoreRenewModel {

    @Resource
    private StoreRenewReadMapper storeRenewReadMapper;
    @Resource
    private StoreRenewWriteMapper storeRenewWriteMapper;
    @Resource
    private StoreGradeReadMapper storeGradeReadMapper;
    @Resource
    private StoreReadMapper storeReadMapper;
    @Resource
    private StoreWriteMapper storeWriteMapper;
    @Resource
    private VendorReadMapper vendorReadMapper;
    @Resource
    private StoreCertificateReadMapper storeCertificateReadMapper;

    /**
     * 新增店铺续签
     *
     * @param storeRenew
     * @return
     */
    public Integer saveStoreRenew(StoreRenew storeRenew) {
        int count = storeRenewWriteMapper.insert(storeRenew);
        if (count == 0) {
            throw new MallException("添加店铺续签失败，请重试");
        }
        return count;
    }

    /**
     * 发起店铺续签
     *
     * @param storeRenew 续签信息
     * @param vendor     操作员
     * @return
     */
    public Integer doStoreRenew(StoreRenew storeRenew, Vendor vendor) {
        //根据storeId查询store表
        Store store = storeReadMapper.getByPrimaryKey(vendor.getStoreId());
        storeRenew.setStoreId(store.getStoreId());
        storeRenew.setStoreName(store.getStoreName());
        //查询vendor表
        VendorExample vendorExample = new VendorExample();
        vendorExample.setStoreId(store.getStoreId());
        vendorExample.setIsStoreAdmin(VendorConst.IS_STORE_ADMIN_1);
        List<Vendor> vendors = vendorReadMapper.listByExample(vendorExample);
        storeRenew.setVendorName(vendors.get(0).getVendorName());

        //查询store_certificate表
        StoreCertificateExample storeCertificateExample = new StoreCertificateExample();
        storeCertificateExample.setVendorId(vendors.get(0).getVendorId());
        List<StoreCertificate> storeCertificates = storeCertificateReadMapper.listByExample(storeCertificateExample);
        storeRenew.setContactName(storeCertificates.get(0).getContactName());
        storeRenew.setContactPhone(storeCertificates.get(0).getContactPhone());

        storeRenew.setApplyTime(new Date());
        storeRenew.setState(StoreConst.STORE_RENEW_STATE_WAITPAY);
        storeRenew.setPaymentCode(OrderPaymentConst.PAYMENT_CODE_ONLINE);
        storeRenew.setPaymentName(OrderPaymentConst.PAYMENT_NAME_ONLINE);
        //续签生效日期
        storeRenew.setStartTime(store.getStoreExpireTime());
        //续签失效日期(续签生效日期+续签时长)
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(storeRenew.getStartTime());
        calendar.add(Calendar.YEAR, storeRenew.getDuration());
        storeRenew.setEndTime(calendar.getTime());

        //支付金额
        StoreGrade storeGrade = storeGradeReadMapper.getByPrimaryKey(storeRenew.getGradeId());
        storeRenew.setPayAmount(new BigDecimal(storeGrade.getPrice()).multiply(new BigDecimal(storeRenew.getDuration())));

        //生产支付单号
        storeRenew.setPaySn(GoodsIdGenerator.getPaySn());

        int count = storeRenewWriteMapper.insert(storeRenew);
        if (count == 0) {
            throw new MallException("发起店铺续签失败，请重试");
        }
        return storeRenew.getRenewId();
    }

    /**
     * 根据renewId删除店铺续签
     *
     * @param renewId renewId
     * @return
     */
    public Integer deleteStoreRenew(Integer renewId) {
        int count = storeRenewWriteMapper.deleteByPrimaryKey(renewId);
        if (count == 0) {
            log.error("根据renewId：" + renewId + "删除店铺续签失败");
            throw new MallException("删除店铺续签失败,请重试");
        }
        return count;
    }

    /**
     * 根据renewId更新店铺续签
     *
     * @param storeRenew
     * @return
     */
    public Integer updateStoreRenew(StoreRenew storeRenew) {
        if (StringUtils.isEmpty(storeRenew.getRenewId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = storeRenewWriteMapper.updateByPrimaryKeySelective(storeRenew);
        if (count == 0) {
            log.error("根据renewId：" + storeRenew.getRenewId() + "更新店铺续签失败");
            throw new MallException("更新店铺续签失败,请重试");
        }
        return count;
    }

    /**
     * 成功续签，修改续签状态、店铺到期时间
     *
     * @param storeRenew
     * @return
     */
    @Transactional
    public Integer successRenew(StoreRenew storeRenew) {
        //修改store_renew表
        storeRenew.setPayTime(new Date());
        storeRenew.setState(StoreConst.STORE_RENEW_STATE_SUCCESS);

        int count;
        count = storeRenewWriteMapper.updateByPrimaryKeySelective(storeRenew);
        if (count == 0) {
            log.error("根据renewId：" + storeRenew.getRenewId() + "更新店铺续签失败");
            throw new MallException("更新店铺续签失败,请重试");
        }

        //修改store表
        Store store = storeReadMapper.getByPrimaryKey(storeRenew.getStoreId());
        StoreRenew storeRenewDb = storeRenewReadMapper.getByPrimaryKey(storeRenew.getRenewId());
        store.setStoreExpireTime(storeRenewDb.getEndTime());

        count = storeWriteMapper.updateByPrimaryKeySelective(store);
        if (count == 0) {
            log.error("根据storeId：" + store.getStoreId() + "更新店铺失败");
            throw new MallException("更新店铺失败,请重试");
        }
        return count;
    }

    /**
     * 根据renewId获取店铺续签详情
     *
     * @param renewId renewId
     * @return
     */
    public StoreRenew getStoreRenewByRenewId(Integer renewId) {
        return storeRenewReadMapper.getByPrimaryKey(renewId);
    }

    /**
     * 根据条件获取店铺续签列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<StoreRenew> getStoreRenewList(StoreRenewExample example, PagerInfo pager) {
        List<StoreRenew> storeRenewList;
        if (pager != null) {
            pager.setRowsCount(storeRenewReadMapper.countByExample(example));
            storeRenewList = storeRenewReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            storeRenewList = storeRenewReadMapper.listByExample(example);
        }
        return storeRenewList;
    }
}