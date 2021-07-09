package com.slodon.b2b2c.dao.write.promotion;

import com.slodon.b2b2c.core.database.BaseWriteMapper;
import com.slodon.b2b2c.promotion.example.CouponMemberExample;
import com.slodon.b2b2c.promotion.pojo.CouponMember;

/**
 * 会员优惠券领取表、使用表writeDao
 */
public interface CouponMemberWriteMapper extends BaseWriteMapper<CouponMember, CouponMemberExample> {
}