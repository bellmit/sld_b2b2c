package com.slodon.b2b2c.dao.read.promotion;

import com.slodon.b2b2c.core.database.BaseReadMapper;
import com.slodon.b2b2c.promotion.example.SignLogExample;
import com.slodon.b2b2c.promotion.pojo.SignLog;
import com.slodon.b2b2c.vo.promotion.MemberSignListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SignLogReadMapper extends BaseReadMapper<SignLog, SignLogExample> {

    Integer getMemberSignListCount(@Param("example") SignLogExample example);

    List<MemberSignListVO> getMemberSignList(@Param("example") SignLogExample example,
                                             @Param("start") Integer start,
                                             @Param("size") Integer size);
}