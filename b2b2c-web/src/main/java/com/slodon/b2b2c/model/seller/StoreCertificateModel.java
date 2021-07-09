package com.slodon.b2b2c.model.seller;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.seller.StoreCertificateReadMapper;
import com.slodon.b2b2c.dao.write.seller.StoreCertificateWriteMapper;
import com.slodon.b2b2c.seller.example.StoreCertificateExample;
import com.slodon.b2b2c.seller.pojo.StoreCertificate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class StoreCertificateModel {

    @Resource
    private StoreCertificateReadMapper storeCertificateReadMapper;
    @Resource
    private StoreCertificateWriteMapper storeCertificateWriteMapper;

    /**
     * 新增店铺资质信息表
     *
     * @param storeCertificate
     * @return
     */
    public Integer saveStoreCertificate(StoreCertificate storeCertificate) {
        int count = storeCertificateWriteMapper.insert(storeCertificate);
        if (count == 0) {
            throw new MallException("添加店铺资质信息表失败，请重试");
        }
        return count;
    }

    /**
     * 根据certificateId删除店铺资质信息表
     *
     * @param certificateId certificateId
     * @return
     */
    public Integer deleteStoreCertificate(Integer certificateId) {
        if (StringUtils.isEmpty(certificateId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = storeCertificateWriteMapper.deleteByPrimaryKey(certificateId);
        if (count == 0) {
            log.error("根据certificateId：" + certificateId + "删除店铺资质信息表失败");
            throw new MallException("删除店铺资质信息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据certificateId更新店铺资质信息表
     *
     * @param storeCertificate
     * @return
     */
    public Integer updateStoreCertificate(StoreCertificate storeCertificate) {
        if (StringUtils.isEmpty(storeCertificate.getCertificateId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = storeCertificateWriteMapper.updateByPrimaryKeySelective(storeCertificate);
        if (count == 0) {
            log.error("根据certificateId：" + storeCertificate.getCertificateId() + "更新店铺资质信息表失败");
            throw new MallException("更新店铺资质信息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据certificateId获取店铺资质信息表详情
     *
     * @param certificateId certificateId
     * @return
     */
    public StoreCertificate getStoreCertificateByCertificateId(Integer certificateId) {
        return storeCertificateReadMapper.getByPrimaryKey(certificateId);
    }

    /**
     * 根据条件获取店铺资质信息表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<StoreCertificate> getStoreCertificateList(StoreCertificateExample example, PagerInfo pager) {
        List<StoreCertificate> storeCertificateList;
        if (pager != null) {
            pager.setRowsCount(storeCertificateReadMapper.countByExample(example));
            storeCertificateList = storeCertificateReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            storeCertificateList = storeCertificateReadMapper.listByExample(example);
        }
        return storeCertificateList;
    }
}