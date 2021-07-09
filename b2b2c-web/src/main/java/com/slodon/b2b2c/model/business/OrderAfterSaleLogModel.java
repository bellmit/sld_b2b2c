package com.slodon.b2b2c.model.business;

import com.slodon.b2b2c.dao.read.business.OrderAfterSaleLogReadMapper;
import com.slodon.b2b2c.dao.write.business.OrderAfterSaleLogWriteMapper;
import com.slodon.b2b2c.business.example.OrderAfterSaleLogExample;
import com.slodon.b2b2c.business.pojo.OrderAfterSaleLog;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class OrderAfterSaleLogModel {

    @Resource
    private OrderAfterSaleLogReadMapper orderAfterSaleLogReadMapper;
    @Resource
    private OrderAfterSaleLogWriteMapper orderAfterSaleLogWriteMapper;

    /**
     * 新增售后服务操作日志表
     *
     * @param orderAfterSaleLog
     * @return
     */
    public Integer saveOrderAfterSaleLog(OrderAfterSaleLog orderAfterSaleLog) {
        int count = orderAfterSaleLogWriteMapper.insert(orderAfterSaleLog);
        if (count == 0) {
            throw new MallException("添加售后服务操作日志表失败，请重试");
        }
        return count;
    }

    /**
     * 根据logId删除售后服务操作日志表
     *
     * @param logId logId
     * @return
     */
    public Integer deleteOrderAfterSaleLog(Integer logId) {
        if (StringUtils.isEmpty(logId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = orderAfterSaleLogWriteMapper.deleteByPrimaryKey(logId);
        if (count == 0) {
            log.error("根据logId：" + logId + "删除售后服务操作日志表失败");
            throw new MallException("删除售后服务操作日志表失败,请重试");
        }
        return count;
    }

    /**
     * 根据logId更新售后服务操作日志表
     *
     * @param orderAfterSaleLog
     * @return
     */
    public Integer updateOrderAfterSaleLog(OrderAfterSaleLog orderAfterSaleLog) {
        if (StringUtils.isEmpty(orderAfterSaleLog.getLogId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = orderAfterSaleLogWriteMapper.updateByPrimaryKeySelective(orderAfterSaleLog);
        if (count == 0) {
            log.error("根据logId：" + orderAfterSaleLog.getLogId() + "更新售后服务操作日志表失败");
            throw new MallException("更新售后服务操作日志表失败,请重试");
        }
        return count;
    }

    /**
     * 根据logId获取售后服务操作日志表详情
     *
     * @param logId logId
     * @return
     */
    public OrderAfterSaleLog getOrderAfterSaleLogByLogId(Integer logId) {
        return orderAfterSaleLogReadMapper.getByPrimaryKey(logId);
    }

    /**
     * 根据条件获取售后服务操作日志表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<OrderAfterSaleLog> getOrderAfterSaleLogList(OrderAfterSaleLogExample example, PagerInfo pager) {
        List<OrderAfterSaleLog> orderAfterSaleLogList;
        if (pager != null) {
            pager.setRowsCount(orderAfterSaleLogReadMapper.countByExample(example));
            orderAfterSaleLogList = orderAfterSaleLogReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            orderAfterSaleLogList = orderAfterSaleLogReadMapper.listByExample(example);
        }
        return orderAfterSaleLogList;
    }
}