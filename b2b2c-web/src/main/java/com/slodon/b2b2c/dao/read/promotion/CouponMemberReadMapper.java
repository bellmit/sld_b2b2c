package com.slodon.b2b2c.dao.read.promotion;

import com.slodon.b2b2c.core.database.BaseReadMapper;
import com.slodon.b2b2c.promotion.example.CouponMemberExample;
import com.slodon.b2b2c.promotion.pojo.CouponMember;

/**
 * 会员优惠券领取表、使用表readDao
 */
public interface CouponMemberReadMapper extends BaseReadMapper<CouponMember, CouponMemberExample> {
}