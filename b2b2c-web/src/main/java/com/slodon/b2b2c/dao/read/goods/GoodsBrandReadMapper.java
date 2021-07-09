package com.slodon.b2b2c.dao.read.goods;

import com.slodon.b2b2c.core.database.BaseReadMapper;
import com.slodon.b2b2c.goods.example.GoodsBrandExample;
import com.slodon.b2b2c.goods.pojo.GoodsBrand;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsBrandReadMapper extends BaseReadMapper<GoodsBrand, GoodsBrandExample> {
    /**
     * 分页查询符合条件的记录
     *
     * @param startRow 起始行数
     * @param size     需要查询的数量
     * @return
     */
    List<String> groupListFieldsPageByExample(@Param("startRow") Integer startRow, @Param("size") Integer size);

}