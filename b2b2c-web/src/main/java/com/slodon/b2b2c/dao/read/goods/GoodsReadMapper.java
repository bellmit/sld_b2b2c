package com.slodon.b2b2c.dao.read.goods;

import com.slodon.b2b2c.core.database.BaseReadMapper;
import com.slodon.b2b2c.goods.example.GoodsExample;
import com.slodon.b2b2c.goods.pojo.Goods;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品公共信息表（SPU）readDao
 */
public interface GoodsReadMapper extends BaseReadMapper<Goods, GoodsExample> {

    List<String> listFieldsOnTimeByExample(@Param("fields") String fields,
                                           @Param("example") GoodsExample example,
                                           @Param("startRow") Integer startRow,
                                           @Param("size") Integer size);

    /**
     * 查询符合条件的记录数
     * @param example 查询条件，如果example的字段为空，则无此查询条件
     * @return
     */
    int countByGoodsExample(@Param("fields") String fields,
                            @Param("example") GoodsExample example);
}