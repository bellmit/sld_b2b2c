package com.slodon.b2b2c.model.business;

import com.slodon.b2b2c.business.example.OrderReplacementExample;
import com.slodon.b2b2c.business.pojo.OrderAfterSaleLog;
import com.slodon.b2b2c.business.pojo.OrderReplacement;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.constant.OrdersAfsConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.dao.read.business.OrderReplacementReadMapper;
import com.slodon.b2b2c.dao.write.business.OrderAfterSaleLogWriteMapper;
import com.slodon.b2b2c.dao.write.business.OrderReplacementWriteMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class OrderReplacementModel {

    @Resource
    private OrderReplacementReadMapper orderReplacementReadMapper;
    @Resource
    private OrderReplacementWriteMapper orderReplacementWriteMapper;
    @Resource
    private OrderAfterSaleLogWriteMapper orderAfterSaleLogWriteMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 新增订单换货表
     *
     * @param orderReplacement
     * @return
     */
    public Integer saveOrderReplacement(OrderReplacement orderReplacement) {
        int count = orderReplacementWriteMapper.insert(orderReplacement);
        if (count == 0) {
            throw new MallException("添加订单换货表失败，请重试");
        }
        return count;
    }

    /**
     * 根据replacementId删除订单换货表
     *
     * @param replacementId replacementId
     * @return
     */
    public Integer deleteOrderReplacement(Integer replacementId) {
        if (StringUtils.isEmpty(replacementId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = orderReplacementWriteMapper.deleteByPrimaryKey(replacementId);
        if (count == 0) {
            log.error("根据replacementId：" + replacementId + "删除订单换货表失败");
            throw new MallException("删除订单换货表失败,请重试");
        }
        return count;
    }

    /**
     * 根据replacementId更新订单换货表
     *
     * @param orderReplacement
     * @return
     */
    public Integer updateOrderReplacement(OrderReplacement orderReplacement) {
        if (StringUtils.isEmpty(orderReplacement.getReplacementId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = orderReplacementWriteMapper.updateByPrimaryKeySelective(orderReplacement);
        if (count == 0) {
            log.error("根据replacementId：" + orderReplacement.getReplacementId() + "更新订单换货表失败");
            throw new MallException("更新订单换货表失败,请重试");
        }
        return count;
    }

    /**
     * 根据replacementId获取订单换货表详情
     *
     * @param replacementId replacementId
     * @return
     */
    public OrderReplacement getOrderReplacementByReplacementId(Integer replacementId) {
        return orderReplacementReadMapper.getByPrimaryKey(replacementId);
    }

    /**
     * 根据条件获取订单换货表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<OrderReplacement> getOrderReplacementList(OrderReplacementExample example, PagerInfo pager) {
        List<OrderReplacement> orderReplacementList;
        if (pager != null) {
            pager.setRowsCount(orderReplacementReadMapper.countByExample(example));
            orderReplacementList = orderReplacementReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            orderReplacementList = orderReplacementReadMapper.listByExample(example);
        }
        return orderReplacementList;
    }

    /**
     * 系统自动完成换货单
     *
     * @return
     */
    @Transactional
    public boolean jobSystemFinishReplacementOrder() {
        // 获取当前时间n天之前的时间，n为系统设置的自动收货时间
        int n = Integer.parseInt(stringRedisTemplate.opsForValue().get("time_limit_of_auto_receive"));

        Date deliverTime = TimeUtil.getDateApartDay(-n);

        OrderReplacementExample replacementExample = new OrderReplacementExample();
        replacementExample.setState(OrdersAfsConst.REPLACEMENT_STATE_STORE_DELIVERY);
        replacementExample.setStoreDeliveryTimeBefore(deliverTime);

        //获取发货时间超过15天的换货单
        List<OrderReplacement> replacementList = orderReplacementReadMapper.listByExample(replacementExample);

        if (!CollectionUtils.isEmpty(replacementList)) {
            // 单条数据处理异常不影响其他数据执行
            for (OrderReplacement replacement : replacementList) {
                //更新换货信息，完成换货
                OrderReplacement updateReplacement = new OrderReplacement();
                updateReplacement.setReplacementId(replacement.getReplacementId());
                updateReplacement.setState(OrdersAfsConst.REPLACEMENT_STATE_DONE);
                updateReplacement.setCompleteTime(new Date());

                int update = orderReplacementWriteMapper.updateByPrimaryKeySelective(updateReplacement);
                AssertUtil.isTrue(update == 0, "更新换货信息失败");

                //记录售后日志
                OrderAfterSaleLog afterSaleLog = new OrderAfterSaleLog();
                afterSaleLog.setLogRole(OrderConst.LOG_ROLE_ADMIN);
                afterSaleLog.setLogUserId(0L);
                afterSaleLog.setLogUserName("系统");
                afterSaleLog.setAfsSn(replacement.getAfsSn());
                afterSaleLog.setAfsType(OrdersAfsConst.AFS_TYPE_REPLACEMENT);
                afterSaleLog.setState(OrdersAfsConst.REPLACEMENT_STATE_DONE + "");
                afterSaleLog.setCreateTime(new Date());
                afterSaleLog.setContent("系统自动收货，换货完成");
                update = orderAfterSaleLogWriteMapper.insert(afterSaleLog);
                AssertUtil.isTrue(update == 0, "记录换货信息失败");
            }
        }
        return true;
    }
}