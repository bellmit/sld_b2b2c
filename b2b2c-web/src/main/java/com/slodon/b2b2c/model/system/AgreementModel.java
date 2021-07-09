package com.slodon.b2b2c.model.system;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.system.AgreementReadMapper;
import com.slodon.b2b2c.dao.write.system.AgreementWriteMapper;
import com.slodon.b2b2c.system.example.AgreementExample;
import com.slodon.b2b2c.system.pojo.Agreement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class AgreementModel {

    @Resource
    private AgreementReadMapper agreementReadMapper;
    @Resource
    private AgreementWriteMapper agreementWriteMapper;

    /**
     * 新增协议表
     *
     * @param agreement
     * @return
     */
    public Integer saveAgreement(Agreement agreement) {
        int count = agreementWriteMapper.insert(agreement);
        if (count == 0) {
            throw new MallException("添加协议表失败，请重试");
        }
        return count;
    }

    /**
     * 根据agreementCode删除协议表
     *
     * @param agreementCode agreementCode
     * @return
     */
    public Integer deleteAgreement(String agreementCode) {
        if (StringUtils.isEmpty(agreementCode)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = agreementWriteMapper.deleteByPrimaryKey(agreementCode);
        if (count == 0) {
            log.error("根据agreementCode：" + agreementCode + "删除协议表失败");
            throw new MallException("删除协议表失败,请重试");
        }
        return count;
    }

    /**
     * 根据agreementCode更新协议表
     *
     * @param agreement
     * @return
     */
    public Integer updateAgreement(Agreement agreement) {
        if (StringUtils.isEmpty(agreement.getAgreementCode())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = agreementWriteMapper.updateByPrimaryKeySelective(agreement);
        if (count == 0) {
            log.error("根据agreementCode：" + agreement.getAgreementCode() + "更新协议表失败");
            throw new MallException("更新协议表失败,请重试");
        }
        return count;
    }

    /**
     * 根据agreementCode获取协议表详情
     *
     * @param agreementCode agreementCode
     * @return
     */
    public Agreement getAgreementByAgreementCode(String agreementCode) {
        return agreementReadMapper.getByPrimaryKey(agreementCode);
    }

    /**
     * 根据条件获取协议表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<Agreement> getAgreementList(AgreementExample example, PagerInfo pager) {
        List<Agreement> agreementList;
        if (pager != null) {
            pager.setRowsCount(agreementReadMapper.countByExample(example));
            agreementList = agreementReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            agreementList = agreementReadMapper.listByExample(example);
        }
        return agreementList;
    }
}