package com.slodon.b2b2c.model.promotion;

import cn.hutool.core.collection.CollectionUtil;
import com.slodon.b2b2c.core.constant.CouponConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.uid.CouponCode;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.RedBagUtils;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.dao.read.promotion.CouponMemberReadMapper;
import com.slodon.b2b2c.dao.read.promotion.CouponReadMapper;
import com.slodon.b2b2c.dao.write.promotion.CouponMemberWriteMapper;
import com.slodon.b2b2c.dao.write.promotion.CouponUseLogWriteMapper;
import com.slodon.b2b2c.dao.write.promotion.CouponWriteMapper;
import com.slodon.b2b2c.model.member.MemberModel;
import com.slodon.b2b2c.promotion.example.CouponMemberExample;
import com.slodon.b2b2c.promotion.pojo.Coupon;
import com.slodon.b2b2c.promotion.pojo.CouponMember;
import com.slodon.b2b2c.promotion.pojo.CouponUseLog;
import com.slodon.b2b2c.vo.promotion.CouponReceiveVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class CouponMemberModel {

    @Resource
    private CouponMemberReadMapper couponMemberReadMapper;
    @Resource
    private CouponMemberWriteMapper couponMemberWriteMapper;
    @Resource
    private CouponReadMapper couponReadMapper;
    @Resource
    private CouponWriteMapper couponWriteMapper;
    @Resource
    private CouponUseLogWriteMapper couponUseLogWriteMapper;
    @Resource
    private MemberModel memberModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 新增会员优惠券领取表、使用表
     *
     * @param couponMember
     * @return
     */
    @Transactional
    public CouponReceiveVO saveCouponMember(CouponMember couponMember) {
        // 查询优惠券信息
        Coupon coupon = couponReadMapper.getByPrimaryKey(couponMember.getCouponId());
        AssertUtil.notNull(coupon, "优惠券不存在，请重试！");
        AssertUtil.isTrue(coupon.getPublishType() != CouponConst.PUBLISH_TYPE_1, "该优惠券不能直接领取");
        AssertUtil.isTrue(coupon.getPublishStartTime().after(new Date()), "优惠券还没有到领取时间");
        AssertUtil.isTrue(coupon.getPublishEndTime().before(new Date()), "优惠券已经过了领取时间");
        AssertUtil.isTrue((coupon.getReceivedNum() + 1) > coupon.getPublishNum(), "优惠券已经被领完了");

        //封装返回信息
        CouponReceiveVO vo = new CouponReceiveVO(coupon);
        vo.setDescription("满" + coupon.getLimitQuota() + "元可用");

        // 读库取数据，避免数据库同步的时间差
        CouponMemberExample example = new CouponMemberExample();
        example.setMemberId(couponMember.getMemberId());
        example.setCouponId(couponMember.getCouponId());
        int receivedNum = couponMemberWriteMapper.countByExample(example);
        AssertUtil.isTrue(coupon.getLimitReceive() > 0 && receivedNum >= coupon.getLimitReceive(), "您已经领取过该优惠券了");

        // 查询用户信息
        Member member = memberModel.getMemberByMemberId(couponMember.getMemberId());
        AssertUtil.notNull(member, "用户信息获取失败，请稍后再试！");

        if (coupon.getCouponType() == CouponConst.COUPON_TYPE_3) {
            BigDecimal randomAmount = RedBagUtils.createRandomKey(coupon.getRandomMin(), coupon.getRandomMax());
            vo.setPublishValue(randomAmount);
            couponMember.setRandomAmount(randomAmount);
        } else {
            vo.setPublishValue(coupon.getPublishValue());
        }

        couponMember.setCouponCode(CouponCode.getKey());
        couponMember.setStoreId(coupon.getStoreId());
        couponMember.setReceiveTime(new Date());
        couponMember.setUseState(CouponConst.USE_STATE_1);
        if (coupon.getEffectiveTimeType() == CouponConst.EFFECTIVE_TIME_TYPE_2) {
            couponMember.setEffectiveStart(new Date());
            couponMember.setEffectiveEnd(TimeUtil.getDateApartDay(coupon.getCycle()));
        } else {
            couponMember.setEffectiveStart(coupon.getEffectiveStart());
            couponMember.setEffectiveEnd(coupon.getEffectiveEnd());
        }
        couponMember.setUseType(coupon.getUseType());
        //保存信息
        int count = couponMemberWriteMapper.insert(couponMember);
        AssertUtil.isTrue(count == 0, "保存会员优惠券领取信息失败，请重试！");

        //记录领取数量
        Coupon couponNew = new Coupon();
        couponNew.setCouponId(coupon.getCouponId());
        couponNew.setReceivedNum(coupon.getReceivedNum() + 1);
        count = couponWriteMapper.updateByPrimaryKeySelective(couponNew);
        AssertUtil.isTrue(count == 0, "更新优惠券领取数量信息失败，请重试！");

        CouponUseLog couponUseLog = new CouponUseLog();
        couponUseLog.setCouponCode(couponMember.getCouponCode());
        couponUseLog.setMemberId(member.getMemberId());
        couponUseLog.setMemberName(member.getMemberName());
        couponUseLog.setStoreId(coupon.getStoreId());
        couponUseLog.setLogType(CouponConst.LOG_TYPE_1);
        couponUseLog.setLogTime(new Date());
        couponUseLog.setLogContent("领取优惠券");
        count = couponUseLogWriteMapper.insert(couponUseLog);
        AssertUtil.isTrue(count == 0, "保存优惠券详细日志信息失败，请重试！");
        return vo;
    }

    /**
     * 根据couponMemberId删除会员优惠券领取表、使用表
     *
     * @param couponMemberId couponMemberId
     * @return
     */
    public Integer deleteCouponMember(Integer couponMemberId) {
        if (StringUtils.isEmpty(couponMemberId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = couponMemberWriteMapper.deleteByPrimaryKey(couponMemberId);
        if (count == 0) {
            log.error("根据couponMemberId：" + couponMemberId + "删除会员优惠券领取表、使用表失败");
            throw new MallException("删除会员优惠券领取表、使用表失败,请重试");
        }
        return count;
    }

    /**
     * 根据couponMemberId更新会员优惠券领取表、使用表
     *
     * @param couponMember
     * @return
     */
    public Integer updateCouponMember(CouponMember couponMember) {
        if (StringUtils.isEmpty(couponMember.getCouponMemberId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = couponMemberWriteMapper.updateByPrimaryKeySelective(couponMember);
        if (count == 0) {
            log.error("根据couponMemberId：" + couponMember.getCouponMemberId() + "更新会员优惠券领取表、使用表失败");
            throw new MallException("更新会员优惠券领取表、使用表失败,请重试");
        }
        return count;
    }

    /**
     * 根据couponMemberId获取会员优惠券领取表、使用表详情
     *
     * @param couponMemberId couponMemberId
     * @return
     */
    public CouponMember getCouponMemberByCouponMemberId(Integer couponMemberId) {
        return couponMemberReadMapper.getByPrimaryKey(couponMemberId);
    }

    /**
     * 根据条件获取会员优惠券领取表、使用表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<CouponMember> getCouponMemberList(CouponMemberExample example, PagerInfo pager) {
        List<CouponMember> couponMemberList;
        if (pager != null) {
            pager.setRowsCount(couponMemberReadMapper.countByExample(example));
            couponMemberList = couponMemberReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            couponMemberList = couponMemberReadMapper.listByExample(example);
        }
        return couponMemberList;
    }

    /**
     * 系统定时检查即将过期优惠券
     *
     * @return
     */
   @Transactional
    public List<CouponMember> jobSystemCheckExpiredCoupon() {
        //查询优惠券过期前多少天提醒
        int couponExpiredDay = Integer.parseInt(stringRedisTemplate.opsForValue().get("coupon_expired_reminder"));
        List<CouponMember> couponMemberList = new ArrayList<>();
        //首先获取所有会员领的优惠券--未使用和未提醒状态
        CouponMemberExample couponMemberExample = new CouponMemberExample();
        couponMemberExample.setExpiredNoticeState(CouponConst.EXPIRED_NOTICE_STATE_1);
        couponMemberExample.setUseState(CouponConst.USE_STATE_1);
        List<CouponMember> memberList = couponMemberReadMapper.listByExample(couponMemberExample);
        if (!CollectionUtil.isEmpty(memberList)) {
            Calendar calendar = Calendar.getInstance();
            for (CouponMember couponMember : memberList) {
                //获取优惠券的过期时间
                Date currentDate = new Date();
                Date expiredDate = couponMember.getEffectiveEnd();
                Coupon coupon = couponReadMapper.getByPrimaryKey(couponMember.getCouponId());
                if (coupon == null) {
                    continue;
                }
                calendar.setTime(currentDate);
                calendar.add(Calendar.DATE, couponExpiredDay);
                if (calendar.getTime().compareTo(expiredDate) >= 0) {
                    //更新消息通知状态
                    CouponMember couponMemberUpdate = new CouponMember();
                    couponMemberUpdate.setCouponMemberId(couponMember.getCouponMemberId());
                    couponMemberUpdate.setExpiredNoticeState(CouponConst.EXPIRED_NOTICE_STATE_2);
                    couponMemberWriteMapper.updateByPrimaryKeySelective(couponMemberUpdate);
                    couponMemberList.add(couponMember);
                }
            }
        }
        return couponMemberList;
    }
}