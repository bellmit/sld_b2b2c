package com.slodon.b2b2c.dao.read.promotion;

import com.slodon.b2b2c.core.database.BaseReadMapper;
import com.slodon.b2b2c.promotion.example.SeckillStageProductExample;
import com.slodon.b2b2c.promotion.pojo.SeckillStageProduct;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 秒杀商品表readDao
 */
public interface SeckillStageProductReadMapper extends BaseReadMapper<SeckillStageProduct, SeckillStageProductExample> {

    /**
     * 查询goodsId分组符合条件的记录数
     *
     * @param fields  分组字段
     * @param example 查询条件，如果example的字段为空，则无此查询条件
     * @return
     */
    int countByGoodsIdExample(@Param("fields") String fields, @Param("example") SeckillStageProductExample example);

    /**
     * 分页查询符合条件的记录
     * @param fields    查询字段，字段用逗号分隔
     * @param example   查询条件，如果example的字段为空，则无此查询条件
     * @param startRow  起始行数
     * @param size      需要查询的数量
     * @return
     */
    List<SeckillStageProduct> listFieldsPageByExample(@Param("fields") String fields,
                                                      @Param("example") SeckillStageProductExample example,
                                                      @Param("startRow") Integer startRow,
                                                      @Param("size") Integer size);

    /**
     * @param fields    查询字段，字段用逗号分隔
     * @param example   查询条件，如果example的字段为空，则无此查询条件
     * @return
     */
    List<SeckillStageProduct> listFieldsByExample(@Param("fields") String fields, @Param("example") SeckillStageProductExample example);

}