package com.slodon.b2b2c.core.database;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BaseWriteMapper<Record, Example> {

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

    /**
     * 根据条件删除记录，可多条删除
     *
     * @param example 删除条件，根据example中的非空条件做where查询条件，空值不做查询条件
     * @return 返回删除的数据行数
     */
    int deleteByExample(@Param("example") Example example);

    /**
     * 根据主键删除记录
     *
     * @param primaryKey 主键
     * @return
     */
    int deleteByPrimaryKey(Object primaryKey);

    /**
     * 插入一条记录
     *
     * @param record 记录
     * @return
     */
    int insert(Record record);

    /**
     * 按条件更新记录中不为空的字段
     *
     * @param record  记录，如果对应字段为空则不更新对应字段
     * @param example 更新条件，是将一行中某几个属性更新,而不改变其他的值
     * @return
     */
    int updateByExampleSelective(@Param("record") Record record, @Param("example") Example example);

    /**
     * 按照主键更新记录中不为空的字段
     *
     * @param record 记录
     * @return
     */
    int updateByPrimaryKeySelective(Record record);

}
