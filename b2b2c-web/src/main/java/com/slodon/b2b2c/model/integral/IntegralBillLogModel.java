package com.slodon.b2b2c.model.integral;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.integral.IntegralBillLogReadMapper;
import com.slodon.b2b2c.dao.write.integral.IntegralBillLogWriteMapper;
import com.slodon.b2b2c.integral.example.IntegralBillLogExample;
import com.slodon.b2b2c.integral.pojo.IntegralBillLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 积分商城结算日志表model
 */
@Component
@Slf4j
public class IntegralBillLogModel {
    @Resource
    private IntegralBillLogReadMapper integralBillLogReadMapper;

    @Resource
    private IntegralBillLogWriteMapper integralBillLogWriteMapper;

    /**
     * 新增积分商城结算日志表
     *
     * @param integralBillLog
     * @return
     */
    public Integer saveIntegralBillLog(IntegralBillLog integralBillLog) {
        int count = integralBillLogWriteMapper.insert(integralBillLog);
        if (count == 0) {
            throw new MallException("添加积分商城结算日志表失败，请重试");
        }
        return count;
    }

    /**
     * 根据logId删除积分商城结算日志表
     *
     * @param logId logId
     * @return
     */
    public Integer deleteIntegralBillLog(Integer logId) {
        if (StringUtils.isEmpty(logId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = integralBillLogWriteMapper.deleteByPrimaryKey(logId);
        if (count == 0) {
            log.error("根据logId：" + logId + "删除积分商城结算日志表失败");
            throw new MallException("删除积分商城结算日志表失败,请重试");
        }
        return count;
    }

    /**
     * 根据logId更新积分商城结算日志表
     *
     * @param integralBillLog
     * @return
     */
    public Integer updateIntegralBillLog(IntegralBillLog integralBillLog) {
        if (StringUtils.isEmpty(integralBillLog.getLogId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = integralBillLogWriteMapper.updateByPrimaryKeySelective(integralBillLog);
        if (count == 0) {
            log.error("根据logId：" + integralBillLog.getLogId() + "更新积分商城结算日志表失败");
            throw new MallException("更新积分商城结算日志表失败,请重试");
        }
        return count;
    }

    /**
     * 根据logId获取积分商城结算日志表详情
     *
     * @param logId logId
     * @return
     */
    public IntegralBillLog getIntegralBillLogByLogId(Integer logId) {
        return integralBillLogReadMapper.getByPrimaryKey(logId);
    }

    /**
     * 根据条件获取积分商城结算日志表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<IntegralBillLog> getIntegralBillLogList(IntegralBillLogExample example, PagerInfo pager) {
        List<IntegralBillLog> integralBillLogList;
        if (pager != null) {
            pager.setRowsCount(integralBillLogReadMapper.countByExample(example));
            integralBillLogList = integralBillLogReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            integralBillLogList = integralBillLogReadMapper.listByExample(example);
        }
        return integralBillLogList;
    }
}