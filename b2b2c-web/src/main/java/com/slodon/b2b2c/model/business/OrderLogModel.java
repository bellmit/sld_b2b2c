package com.slodon.b2b2c.model.business;

import com.slodon.b2b2c.business.example.OrderLogExample;
import com.slodon.b2b2c.business.pojo.OrderLog;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.business.OrderLogReadMapper;
import com.slodon.b2b2c.dao.write.business.OrderLogWriteMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class OrderLogModel {

    @Resource
    private OrderLogReadMapper orderLogReadMapper;
    @Resource
    private OrderLogWriteMapper orderLogWriteMapper;

    /**
     * 新增订单操作日志表
     *
     * @param orderLog
     * @return
     */
    public Integer saveOrderLog(OrderLog orderLog) {
        int count = orderLogWriteMapper.insert(orderLog);
        if (count == 0) {
            throw new MallException("添加订单操作日志表失败，请重试");
        }
        return count;
    }

    /**
     * 根据logId删除订单操作日志表
     *
     * @param logId logId
     * @return
     */
    public Integer deleteOrderLog(Integer logId) {
        if (StringUtils.isEmpty(logId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = orderLogWriteMapper.deleteByPrimaryKey(logId);
        if (count == 0) {
            log.error("根据logId：" + logId + "删除订单操作日志表失败");
            throw new MallException("删除订单操作日志表失败,请重试");
        }
        return count;
    }

    /**
     * 根据logId更新订单操作日志表
     *
     * @param orderLog
     * @return
     */
    public Integer updateOrderLog(OrderLog orderLog) {
        if (StringUtils.isEmpty(orderLog.getLogId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = orderLogWriteMapper.updateByPrimaryKeySelective(orderLog);
        if (count == 0) {
            log.error("根据logId：" + orderLog.getLogId() + "更新订单操作日志表失败");
            throw new MallException("更新订单操作日志表失败,请重试");
        }
        return count;
    }

    /**
     * 根据logId获取订单操作日志表详情
     *
     * @param logId logId
     * @return
     */
    public OrderLog getOrderLogByLogId(Integer logId) {
        return orderLogReadMapper.getByPrimaryKey(logId);
    }

    /**
     * 根据条件获取订单操作日志表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<OrderLog> getOrderLogList(OrderLogExample example, PagerInfo pager) {
        List<OrderLog> orderLogList;
        if (pager != null) {
            pager.setRowsCount(orderLogReadMapper.countByExample(example));
            orderLogList = orderLogReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            orderLogList = orderLogReadMapper.listByExample(example);
        }
        return orderLogList;
    }

    /**
     * 保存订单日志
     *
     * @param logRole     操作人角色(1-系统管理员，2-商户，3-会员）
     * @param logUserId   操作人ID，结合log_role使用
     * @param logUserName 操作人名称
     * @param orderSn     订单号
     * @param orderState  订单状态：0-已取消；10-未付款订单；20-已付款；30-已发货；40-已完成;50-已关闭
     * @param content     文字描述
     */
    public void insertOrderLog(Integer logRole, Long logUserId, String logUserName, String orderSn, Integer orderState, String content) {
        OrderLog orderLog = new OrderLog();
        orderLog.setLogRole(logRole);
        orderLog.setLogUserId(logUserId);
        orderLog.setLogUserName(logUserName);
        orderLog.setOrderSn(orderSn);
        orderLog.setOrderStateLog(orderState);
        orderLog.setLogTime(new Date());
        orderLog.setLogContent(content);
        this.saveOrderLog(orderLog);
    }
}