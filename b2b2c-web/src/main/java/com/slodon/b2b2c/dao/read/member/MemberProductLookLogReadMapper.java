package com.slodon.b2b2c.dao.read.member;

import com.slodon.b2b2c.core.database.BaseReadMapper;
import com.slodon.b2b2c.member.example.MemberProductLookLogExample;
import com.slodon.b2b2c.member.pojo.MemberProductLookLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MemberProductLookLogReadMapper extends BaseReadMapper<MemberProductLookLog, MemberProductLookLogExample> {
    /**
     * 分页查询符合条件的记录
     *
     * @param example  查询条件，如果example的字段为空，则无此查询条件
     * @param startRow 起始行数
     * @param size     需要查询的数量
     * @return
     */
    List<String> listFieldsPageByExampleAndDay(
            @Param("example") MemberProductLookLogExample example,
            @Param("startRow") Integer startRow,
            @Param("size") Integer size);
}