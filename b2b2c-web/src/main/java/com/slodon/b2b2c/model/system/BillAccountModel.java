package com.slodon.b2b2c.model.system;

import com.slodon.b2b2c.core.constant.BillConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.dao.read.system.BillAccountReadMapper;
import com.slodon.b2b2c.dao.write.system.BillAccountWriteMapper;
import com.slodon.b2b2c.system.example.BillAccountExample;
import com.slodon.b2b2c.system.pojo.BillAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 结算账号表model
 */
@Component
@Slf4j
public class BillAccountModel {

    @Resource
    private BillAccountReadMapper billAccountReadMapper;
    @Resource
    private BillAccountWriteMapper billAccountWriteMapper;

    /**
     * 新增结算账号表
     *
     * @param billAccount
     * @return
     */
    public Integer saveBillAccount(BillAccount billAccount) {
        billAccount.setCreateTime(new Date());
        int count = billAccountWriteMapper.insert(billAccount);
        AssertUtil.isTrue(count == 0, "添加结算账号失败，请重试");

        if (billAccount.getIsDefault() == BillConst.IS_DEFAULT_YES) {
            //默认账号只有一个，如果新增的账号是默认账号，将其他账号设置为非默认
            BillAccountExample example = new BillAccountExample();
            example.setStoreId(billAccount.getStoreId());
            example.setAccountIdNotEquals(billAccount.getAccountId());
            BillAccount billAccountNew = new BillAccount();
            billAccountNew.setIsDefault(BillConst.IS_DEFAULT_NO);
            billAccountNew.setUpdateTime(new Date());
            billAccountWriteMapper.updateByExampleSelective(billAccountNew, example);
        }
        return count;
    }

    /**
     * 根据accountId删除结算账号表
     *
     * @param accountId accountId
     * @return
     */
    public Integer deleteBillAccount(Integer accountId) {
        if (StringUtils.isEmpty(accountId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = billAccountWriteMapper.deleteByPrimaryKey(accountId);
        if (count == 0) {
            log.error("根据accountId：" + accountId + "删除结算账号表失败");
            throw new MallException("删除结算账号表失败,请重试");
        }
        return count;
    }

    /**
     * 根据accountId更新结算账号表
     *
     * @param billAccount
     * @return
     */
    public Integer updateBillAccount(BillAccount billAccount) {
        if (StringUtils.isEmpty(billAccount.getAccountId())) {
            throw new MallException("请选择要修改的数据");
        }
        billAccount.setUpdateTime(new Date());
        int count = billAccountWriteMapper.updateByPrimaryKeySelective(billAccount);
        if (count == 0) {
            log.error("根据accountId：" + billAccount.getAccountId() + "更新结算账号表失败");
            throw new MallException("更新结算账号表失败,请重试");
        }
        if (billAccount.getIsDefault() == BillConst.IS_DEFAULT_YES) {
            //默认账号只有一个，如果新增的账号是默认账号，将其他账号设置为非默认
            BillAccountExample example = new BillAccountExample();
            example.setStoreId(billAccount.getStoreId());
            example.setAccountIdNotEquals(billAccount.getAccountId());
            BillAccount billAccountNew = new BillAccount();
            billAccountNew.setIsDefault(BillConst.IS_DEFAULT_NO);
            billAccountNew.setUpdateTime(new Date());
            billAccountWriteMapper.updateByExampleSelective(billAccountNew, example);
        }
        return count;
    }

    /**
     * 根据accountId获取结算账号表详情
     *
     * @param accountId accountId
     * @return
     */
    public BillAccount getBillAccountByAccountId(Integer accountId) {
        return billAccountReadMapper.getByPrimaryKey(accountId);
    }

    /**
     * 根据条件获取结算账号表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<BillAccount> getBillAccountList(BillAccountExample example, PagerInfo pager) {
        List<BillAccount> billAccountList;
        if (pager != null) {
            pager.setRowsCount(billAccountReadMapper.countByExample(example));
            billAccountList = billAccountReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            billAccountList = billAccountReadMapper.listByExample(example);
        }
        return billAccountList;
    }
}