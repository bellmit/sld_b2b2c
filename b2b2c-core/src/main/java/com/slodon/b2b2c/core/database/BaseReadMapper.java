package com.slodon.b2b2c.core.database;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BaseReadMapper<Record, Example> {

    /**
     * 查询符合条件的记录数
     *
     * @param example 查询条件，如果example的字段为空，则无此查询条件
     * @return
     */
    int countByExample(@Param("example") Example example);

    /**
     * 根据主键查询记录
     *
     * @param primaryKey 主键
     * @return
     */
    Record getByPrimaryKey(Object primaryKey);

//    /**
//     * 根据主键查询记录
//     * @param example   查询条件
//     * @return
//     */
//    Record selectByExampleSelective(Example example);

    /**
     * @param example 查询条件，如果example的字段为空，则无此查询条件
     * @return
     */
    List<Record> listByExample(@Param("example") Example example);

    /**
     * 分页查询符合条件的记录
     *
     * @param example  查询条件，如果example的字段为空，则无此查询条件
     * @param startRow 起始行数
     * @param size     需要查询的数量
     * @return
     */
    List<Record> listPageByExample(@Param("example") Example example,
                                   @Param("startRow") Integer startRow,
                                   @Param("size") Integer size);

    /**
     * @param fields  查询字段，字段用逗号分隔
     * @param example 查询条件，如果example的字段为空，则无此查询条件
     * @return
     */
    List<Record> listFieldsByExample(@Param("fields") String fields, @Param("example") Example example);

    /**
     * 分页查询符合条件的记录
     *
     * @param fields   查询字段，字段用逗号分隔
     * @param example  查询条件，如果example的字段为空，则无此查询条件
     * @param startRow 起始行数
     * @param size     需要查询的数量
     * @return
     */
    List<Record> listFieldsPageByExample(@Param("fields") String fields,
                                         @Param("example") Example example,
                                         @Param("startRow") Integer startRow,
                                         @Param("size") Integer size);
}
