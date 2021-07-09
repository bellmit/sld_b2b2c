package com.slodon.b2b2c.dao.read.promotion;

import com.slodon.b2b2c.core.database.BaseReadMapper;
import com.slodon.b2b2c.promotion.example.SeckillExample;
import com.slodon.b2b2c.promotion.pojo.Seckill;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 秒杀活动表readDao
 */
public interface SeckillReadMapper extends BaseReadMapper<Seckill, SeckillExample> {

    /**
     * 分页查询符合条件的记录
     * @param fields    查询字段，字段用逗号分隔
     * @param example   查询条件，如果example的字段为空，则无此查询条件
     * @param startRow  起始行数
     * @param size      需要查询的数量
     * @return
     */
    List<Seckill> listPageByFieldsExample(@Param("fields") String fields,
                                          @Param("example") SeckillExample example,
                                          @Param("startRow") Integer startRow,
                                          @Param("size") Integer size);

    /**
     * @param fields    查询字段，字段用逗号分隔
     * @param example   查询条件，如果example的字段为空，则无此查询条件
     * @return
     */
    List<Seckill> listByFieldsExample(@Param("fields") String fields, @Param("example") SeckillExample example);


}