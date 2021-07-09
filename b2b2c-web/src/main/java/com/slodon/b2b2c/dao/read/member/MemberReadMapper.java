package com.slodon.b2b2c.dao.read.member;

import com.slodon.b2b2c.core.database.BaseReadMapper;
import com.slodon.b2b2c.member.dto.MemberDayDTO;
import com.slodon.b2b2c.member.example.MemberExample;
import com.slodon.b2b2c.member.pojo.Member;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MemberReadMapper extends BaseReadMapper<Member, MemberExample> {

    /**
     * 会员数量统计
     *
     * @param example
     * @return
     */
    List<MemberDayDTO> getMemberDayDto(@Param("example") MemberExample example);
}