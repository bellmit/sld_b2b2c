package com.slodon.b2b2c.dao.read.promotion;

import com.slodon.b2b2c.core.database.BaseReadMapper;
import com.slodon.b2b2c.promotion.example.SeckillStageExample;
import com.slodon.b2b2c.promotion.pojo.SeckillStage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 秒杀场次readDao
 */
public interface SeckillStageReadMapper extends BaseReadMapper<SeckillStage, SeckillStageExample> {

    /**
     * 分页查询符合条件的记录
     * @param fields    查询字段，字段用逗号分隔
     * @param example   查询条件，如果example的字段为空，则无此查询条件
     * @param startRow  起始行数
     * @param size      需要查询的数量
     * @return
     */
    List<SeckillStage> listPageByFieldsExample(@Param("fields") String fields,
                                               @Param("example") SeckillStageExample example,
                                               @Param("startRow") Integer startRow,
                                               @Param("size") Integer size);

    /**
     * @param fields    查询字段，字段用逗号分隔
     * @param example   查询条件，如果example的字段为空，则无此查询条件
     * @return
     */
    List<SeckillStage> listByFieldsExample(@Param("fields") String fields, @Param("example") SeckillStageExample example);
}