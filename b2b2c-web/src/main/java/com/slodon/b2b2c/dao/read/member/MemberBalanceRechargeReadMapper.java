package com.slodon.b2b2c.dao.read.member;

import com.slodon.b2b2c.core.database.BaseReadMapper;
import com.slodon.b2b2c.member.example.MemberBalanceRechargeExample;
import com.slodon.b2b2c.member.pojo.MemberBalanceRecharge;
import org.apache.ibatis.annotations.Param;

public interface MemberBalanceRechargeReadMapper extends BaseReadMapper<MemberBalanceRecharge, MemberBalanceRechargeExample> {

    /**
     * 根据条件查询累计充值人数
     *
     * @param example
     * @return
     */
    int getRechargeNumber(@Param("example") MemberBalanceRechargeExample example);
}