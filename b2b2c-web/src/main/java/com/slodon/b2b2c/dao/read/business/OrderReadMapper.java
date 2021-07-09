package com.slodon.b2b2c.dao.read.business;

import com.slodon.b2b2c.business.dto.OrderDayDTO;
import com.slodon.b2b2c.business.dto.OrderExportDTO;
import com.slodon.b2b2c.business.dto.SaleTotalDayDTO;
import com.slodon.b2b2c.business.example.OrderExample;
import com.slodon.b2b2c.business.pojo.Order;
import com.slodon.b2b2c.core.database.BaseReadMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderReadMapper extends BaseReadMapper<Order, OrderExample> {

    /**
     * 订单列表
     *
     * @param example
     * @return
     */
    List<Order> getOrdersList4AutoFinish(@Param("example") OrderExample example);

    /**
     * 订单量统计
     *
     * @param example
     * @return
     */
    List<OrderDayDTO> getOrderDayDto(@Param("example") OrderExample example);

    /**
     * 销售总额统计
     *
     * @param example
     * @return
     */
    List<SaleTotalDayDTO> getSaleTotalDayDto(@Param("example") OrderExample example);

    /**
     * 查询符合条件的分组记录数
     *
     * @param fields
     * @param example
     * @return
     */
    Integer countGroupFieldsByExample(@Param("fields") String fields,@Param("example") OrderExample example);

    /**
     * 获取导出订单列表
     *
     * @param example
     * @return
     */
    List<OrderExportDTO> getOrderExportList(@Param("example") OrderExample example);
}