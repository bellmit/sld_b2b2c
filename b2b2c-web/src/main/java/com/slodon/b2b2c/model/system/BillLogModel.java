package com.slodon.b2b2c.model.system;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.system.BillLogReadMapper;
import com.slodon.b2b2c.dao.write.system.BillLogWriteMapper;
import com.slodon.b2b2c.system.example.BillLogExample;
import com.slodon.b2b2c.system.pojo.BillLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 结算日志表model
 */
@Component
@Slf4j
public class BillLogModel {
    @Resource
    private BillLogReadMapper billLogReadMapper;

    @Resource
    private BillLogWriteMapper billLogWriteMapper;

    /**
     * 新增结算日志表
     *
     * @param billLog
     * @return
     */
    public Integer saveBillLog(BillLog billLog) {
        int count = billLogWriteMapper.insert(billLog);
        if (count == 0) {
            throw new MallException("添加结算日志表失败，请重试");
        }
        return count;
    }

    /**
     * 根据logId删除结算日志表
     *
     * @param logId logId
     * @return
     */
    public Integer deleteBillLog(Integer logId) {
        if (StringUtils.isEmpty(logId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = billLogWriteMapper.deleteByPrimaryKey(logId);
        if (count == 0) {
            log.error("根据logId：" + logId + "删除结算日志表失败");
            throw new MallException("删除结算日志表失败,请重试");
        }
        return count;
    }

    /**
     * 根据logId更新结算日志表
     *
     * @param billLog
     * @return
     */
    public Integer updateBillLog(BillLog billLog) {
        if (StringUtils.isEmpty(billLog.getLogId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = billLogWriteMapper.updateByPrimaryKeySelective(billLog);
        if (count == 0) {
            log.error("根据logId：" + billLog.getLogId() + "更新结算日志表失败");
            throw new MallException("更新结算日志表失败,请重试");
        }
        return count;
    }

    /**
     * 根据logId获取结算日志表详情
     *
     * @param logId logId
     * @return
     */
    public BillLog getBillLogByLogId(Integer logId) {
        return billLogReadMapper.getByPrimaryKey(logId);
    }

    /**
     * 根据条件获取结算日志表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<BillLog> getBillLogList(BillLogExample example, PagerInfo pager) {
        List<BillLog> billLogList;
        if (pager != null) {
            pager.setRowsCount(billLogReadMapper.countByExample(example));
            billLogList = billLogReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            billLogList = billLogReadMapper.listByExample(example);
        }
        return billLogList;
    }
}