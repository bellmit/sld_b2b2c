package com.slodon.b2b2c.model.business;

import com.slodon.b2b2c.business.example.OrderStatisExample;
import com.slodon.b2b2c.business.pojo.OrderStatis;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.business.OrderStatisReadMapper;
import com.slodon.b2b2c.dao.write.business.OrderStatisWriteMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class OrderStatisModel {

    @Resource
    private OrderStatisReadMapper orderStatisReadMapper;
    @Resource
    private OrderStatisWriteMapper orderStatisWriteMapper;

    /**
     * 新增订单统计表
     *
     * @param orderStatis
     * @return
     */
    public Integer saveOrderStatis(OrderStatis orderStatis) {
        int count = orderStatisWriteMapper.insert(orderStatis);
        if (count == 0) {
            throw new MallException("添加订单统计表失败，请重试");
        }
        return count;
    }

    /**
     * 根据id删除订单统计表
     *
     * @param id id
     * @return
     */
    public Integer deleteOrderStatis(Integer id) {
        if (StringUtils.isEmpty(id)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = orderStatisWriteMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            log.error("根据id：" + id + "删除订单统计表失败");
            throw new MallException("删除订单统计表失败,请重试");
        }
        return count;
    }

    /**
     * 根据id更新订单统计表
     *
     * @param orderStatis
     * @return
     */
    public Integer updateOrderStatis(OrderStatis orderStatis) {
        if (StringUtils.isEmpty(orderStatis.getId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = orderStatisWriteMapper.updateByPrimaryKeySelective(orderStatis);
        if (count == 0) {
            log.error("根据id：" + orderStatis.getId() + "更新订单统计表失败");
            throw new MallException("更新订单统计表失败,请重试");
        }
        return count;
    }

    /**
     * 根据id获取订单统计表详情
     *
     * @param id id
     * @return
     */
    public OrderStatis getOrderStatisById(Integer id) {
        return orderStatisReadMapper.getByPrimaryKey(id);
    }

    /**
     * 根据条件获取订单统计表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<OrderStatis> getOrderStatisList(OrderStatisExample example, PagerInfo pager) {
        List<OrderStatis> orderStatisList;
        if (pager != null) {
            pager.setRowsCount(orderStatisReadMapper.countByExample(example));
            orderStatisList = orderStatisReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            orderStatisList = orderStatisReadMapper.listByExample(example);
        }
        return orderStatisList;
    }
}