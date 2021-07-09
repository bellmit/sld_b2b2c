package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.core.constant.CouponConst;
import com.slodon.b2b2c.core.constant.MemberIntegralLogConst;
import com.slodon.b2b2c.core.constant.SignConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.uid.CouponCode;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.dao.read.promotion.CouponReadMapper;
import com.slodon.b2b2c.dao.read.promotion.SignActivityReadMapper;
import com.slodon.b2b2c.dao.read.promotion.SignLogReadMapper;
import com.slodon.b2b2c.dao.read.promotion.SignRecordReadMapper;
import com.slodon.b2b2c.dao.write.promotion.CouponMemberWriteMapper;
import com.slodon.b2b2c.dao.write.promotion.SignLogWriteMapper;
import com.slodon.b2b2c.dao.write.promotion.SignRecordWriteMapper;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.member.pojo.MemberIntegralLog;
import com.slodon.b2b2c.model.member.MemberIntegralLogModel;
import com.slodon.b2b2c.model.member.MemberModel;
import com.slodon.b2b2c.promotion.example.SignLogExample;
import com.slodon.b2b2c.promotion.example.SignRecordExample;
import com.slodon.b2b2c.promotion.pojo.*;
import com.slodon.b2b2c.vo.promotion.MemberSignDetailVO;
import com.slodon.b2b2c.vo.promotion.MemberSignListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class SignLogModel {

    @Resource
    private SignLogReadMapper signLogReadMapper;
    @Resource
    private SignActivityReadMapper signActivityReadMapper;
    @Resource
    private SignRecordReadMapper signRecordReadMapper;
    @Resource
    private CouponReadMapper couponReadMapper;

    @Resource
    private SignLogWriteMapper signLogWriteMapper;
    @Resource
    private SignRecordWriteMapper signRecordWriteMapper;
    @Resource
    private CouponMemberWriteMapper couponMemberWriteMapper;

    @Resource
    private MemberModel memberModel;
    @Resource
    private MemberIntegralLogModel memberIntegralLogModel;

    /**
     * 新增会员签到日志，每次签到关联活动ID
     *
     * @param signLog
     * @return
     */
    public Integer saveSignLog(SignLog signLog) {
        int count = signLogWriteMapper.insert(signLog);
        if (count == 0) {
            throw new MallException("添加会员签到日志，每次签到关联活动ID失败，请重试");
        }
        return count;
    }

    /**
     * 根据logId删除会员签到日志，每次签到关联活动ID
     *
     * @param logId logId
     * @return
     */
    public Integer deleteSignLog(Integer logId) {
        if (StringUtils.isEmpty(logId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = signLogWriteMapper.deleteByPrimaryKey(logId);
        if (count == 0) {
            log.error("根据logId：" + logId + "删除会员签到日志，每次签到关联活动ID失败");
            throw new MallException("删除会员签到日志，每次签到关联活动ID失败,请重试");
        }
        return count;
    }

    /**
     * 根据logId更新会员签到日志，每次签到关联活动ID
     *
     * @param signLog
     * @return
     */
    public Integer updateSignLog(SignLog signLog) {
        if (StringUtils.isEmpty(signLog.getLogId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = signLogWriteMapper.updateByPrimaryKeySelective(signLog);
        if (count == 0) {
            log.error("根据logId：" + signLog.getLogId() + "更新会员签到日志，每次签到关联活动ID失败");
            throw new MallException("更新会员签到日志，每次签到关联活动ID失败,请重试");
        }
        return count;
    }

    /**
     * 根据logId获取会员签到日志，每次签到关联活动ID详情
     *
     * @param logId logId
     * @return
     */
    public SignLog getSignLogByLogId(Integer logId) {
        return signLogReadMapper.getByPrimaryKey(logId);
    }

    /**
     * 根据条件获取会员签到日志，每次签到关联活动ID列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<SignLog> getSignLogList(SignLogExample example, PagerInfo pager) {
        List<SignLog> signLogList;
        if (pager != null) {
            pager.setRowsCount(signLogReadMapper.countByExample(example));
            signLogList = signLogReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            signLogList = signLogReadMapper.listByExample(example);
        }
        return signLogList;
    }

    /**
     * 根据条件查询用户签到统计列表
     *
     * @param example
     * @param pager
     * @return
     */
    public List<MemberSignListVO> getMemberSignList(SignLogExample example, PagerInfo pager) {
        pager.setRowsCount(signLogReadMapper.getMemberSignListCount(example));
        List<MemberSignListVO> list = signLogReadMapper.getMemberSignList(example, pager.getStart(), pager.getPageSize());
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(member -> {
                //根据会员id获取签到日志列表,列表按照签到时间倒序排列，第一个元素为最后签到记录，最后一个元素为首次签到记录
                SignLogExample example2 = new SignLogExample();
                example2.setMemberId(member.getMemberId());
                List<SignLog> signLogsList = signLogReadMapper.listByExample(example2);
                member.setFirstSignTime(signLogsList.get(signLogsList.size() - 1).getSignTime());
                member.setLastSignTime(signLogsList.get(0).getSignTime());
            });
        }
        return list;
    }


    /**
     * 会员签到
     *
     * @param signActivityId
     * @param source         会员来源来源：1、pc；2、H5；3、Android；4、IOS；5、小程序
     * @param memberId
     * @param ip
     * @return
     */
    @Transactional
    public MemberSignDetailVO doSign(Integer signActivityId, Integer source, Integer memberId, String ip) throws Exception {
        Member memberDb = memberModel.getMemberByMemberId(memberId);
        MemberSignDetailVO vo = new MemberSignDetailVO();
        //获取数据库中的签到活动
        SignActivity signActivity = signActivityReadMapper.getByPrimaryKey(signActivityId);
        AssertUtil.notNull(signActivity, "签到活动不存在");
        AssertUtil.isTrue(signActivity.getState() != SignConst.SIGN_STATE_1 || !(signActivity.getStartTime().before(new Date()) && signActivity.getEndTime().after(new Date())), "签到活动不可用");

        //签到开始
        //新增签到日志
        SignLog signLog = new SignLog();

        //活动时长
        int duration = TimeUtil.countApartDay(signActivity.getStartTime(), signActivity.getEndTime()) + 1;
        //当前签到日期是活动的第几天
        int number = TimeUtil.countApartDay(signActivity.getStartTime(), new Date()) + 1;

        //判断当前活动是否有该用户的签到记录
        SignRecordExample signRecordExample = new SignRecordExample();
        signRecordExample.setMemberId(memberId);
        signRecordExample.setActivityId(signActivityId);
        List<SignRecord> recordList = signRecordReadMapper.listByExample(signRecordExample);

        if (CollectionUtils.isEmpty(recordList)) {
            //没有记录，生成一条记录
            SignRecord newRecord = new SignRecord();
            newRecord.setActivityId(signActivityId);
            newRecord.setMemberId(memberId);
            newRecord.setMask(this.getMask(duration, number, null));
            newRecord.setContinueNum(1);
            newRecord.setLastTime(new Date());
            newRecord.setIsBonus(SignConst.IS_BONUS_0);
            int count = signRecordWriteMapper.insert(newRecord);
            AssertUtil.notNullOrZero(count, "添加签到记录失败");

            //第一次签到，只奖励积分，不判断是否连续签到
            signLog.setBonusIntegral(signActivity.getIntegralPerSign());
            //第一次签到默认每日签到，不判断是否连续
            signLog.setSignType(SignConst.SIGN_TYPE_0);
            signLog.setContinueNum(1);
        } else {
            SignRecord signRecord = recordList.get(0);
            //有记录
            //判断是否已签到   true表示今天已经签过到
            boolean today = ("" + SignConst.SIGN_RECORD_STATE_1).equals(Long.toBinaryString(signRecord.getMask()).charAt(number) + "");
            AssertUtil.isTrue(today, "今日已签到，请明日再来");

            //更新记录
            signRecord.setMask(this.getMask(duration, number, signRecord.getMask()));
            //判断第number-1天是否签到，(有记录，代表number > 1)
            // true表示昨天签到了
            boolean yesterday = ("" + SignConst.SIGN_RECORD_STATE_1).equals(Long.toBinaryString(signRecord.getMask()).charAt(number - 1) + "");
            signRecord.setContinueNum(yesterday ? signRecord.getContinueNum() + 1 : 1);
            signRecord.setLastTime(new Date());

            //判断是否满足发放连签奖励的条件
            if (signActivity.getContinueNum() > 0 && signRecord.getContinueNum().equals(signActivity.getContinueNum()) && signRecord.getIsBonus() == SignConst.IS_BONUS_0) {
                //满足奖励条件，将record表中的isBonus置为1（已领取连签奖励）
                signRecord.setIsBonus(SignConst.IS_BONUS_1);
                //设置奖励类型
                signLog.setBonusIntegral(signActivity.getIntegralPerSign() + signActivity.getBonusIntegral());
                signLog.setBonusVoucher(signActivity.getBonusVoucher());
                signLog.setSignType(SignConst.SIGN_TYPE_1);
            } else {
                //不满足连签奖励,设置奖励类型
                signLog.setBonusIntegral(signActivity.getIntegralPerSign());
                signLog.setSignType(SignConst.SIGN_TYPE_0);
            }
            signLog.setContinueNum(signRecord.getContinueNum());

            //更新签到记录
            int count = signRecordWriteMapper.updateByPrimaryKeySelective(signRecord);
            AssertUtil.notNullOrZero(count, "更新签到记录失败");
        }

        //添加签到日志
        signLog.setSignActivityId(signActivityId);
        signLog.setMemberId(memberId);
        signLog.setMemberName(memberDb.getMemberName());
        signLog.setLoginIp(ip);
        signLog.setSignTime(new Date());
        signLog.setSignSource(source);
        int count = signLogWriteMapper.insert(signLog);
        AssertUtil.notNullOrZero(count, "添加签到日志失败");

        //发放奖励（更新会员积分信息，更新优惠券信息）
        if (!StringUtil.isNullOrZero(signLog.getBonusIntegral())) {
            Member member = new Member();
            member.setMemberId(memberId);
            member.setMemberIntegral(memberDb.getMemberIntegral() + signLog.getBonusIntegral());
            member.setUpdateTime(new Date());
            count = memberModel.updateMember(member);
            AssertUtil.notNullOrZero(count, "奖励积分失败");
            vo.setBonusIntegral(signLog.getBonusIntegral());

            //记录积分日志
            MemberIntegralLog memberIntegralLog = new MemberIntegralLog();
            memberIntegralLog.setMemberId(memberDb.getMemberId());
            memberIntegralLog.setMemberName(memberDb.getMemberName());
            memberIntegralLog.setValue(signLog.getBonusIntegral());
            memberIntegralLog.setCreateTime(new Date());
            memberIntegralLog.setType(MemberIntegralLogConst.TYPE_10);
            memberIntegralLog.setDescription("签到赠送积分：" + signLog.getBonusIntegral());
            memberIntegralLog.setOptId(memberDb.getMemberId());
            memberIntegralLog.setOptName(memberDb.getMemberName());
            memberIntegralLogModel.saveMemberIntegralLog(memberIntegralLog);
        }
        if (!StringUtil.isNullOrZero(signLog.getBonusVoucher())) {
            //查询优惠券信息
            Coupon coupon = couponReadMapper.getByPrimaryKey(signLog.getBonusVoucher());
            CouponMember couponMember = new CouponMember();
            couponMember.setMemberId(memberId);
            couponMember.setMemberName(memberDb.getMemberName());
            couponMember.setCouponId(coupon.getCouponId());
            couponMember.setCouponCode(CouponCode.getKey());
            couponMember.setReceiveTime(new Date());
            couponMember.setUseState(CouponConst.USE_STATE_1);
            couponMember.setStoreId(0L);
            couponMember.setOrderSn("");
            couponMember.setEffectiveStart(coupon.getEffectiveStart());
            couponMember.setEffectiveEnd(coupon.getEffectiveEnd());
            couponMember.setUseType(coupon.getUseType());

            count = couponMemberWriteMapper.insert(couponMember);
            AssertUtil.notNullOrZero(count, "奖励优惠券失败");

            vo.setBonusVoucher(coupon.getCouponId());
            vo.setBonusVoucherName(coupon.getCouponName());
        }
        return vo;
    }

    /**
     * 用户签到，计算mask值
     *
     * @param duration 活动持续时长
     * @param number   签到时间是活动的第几天，number<=duration
     * @param mask     原纪录中的mask，如果为空，则生成一个mask
     * @return
     */
    private long getMask(Integer duration, Integer number, Long mask) {
        if (mask == null) {
            mask = (long) Math.pow(2, duration);//mask的二进制数值为：10000·····0    1后面有duration个0
        }
        //将mask对应二进制数的第number个0变成1
        mask = mask + (long) Math.pow(2, duration - number);
        return mask;
    }
}