package com.slodon.b2b2c.model.member;

import com.slodon.b2b2c.business.pojo.Order;
import com.slodon.b2b2c.dao.write.member.MemberExperienceLogWriteMapper;
import com.slodon.b2b2c.dao.write.member.MemberGradeLogWriteMapper;
import com.slodon.b2b2c.dao.write.member.MemberWriteMapper;
import com.slodon.b2b2c.core.constant.MemberGradeConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.dao.read.member.MemberExperienceLogReadMapper;
import com.slodon.b2b2c.dao.read.member.MemberGradeReadMapper;
import com.slodon.b2b2c.member.example.MemberExperienceLogExample;
import com.slodon.b2b2c.member.example.MemberGradeExample;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.member.pojo.MemberExperienceLog;
import com.slodon.b2b2c.member.pojo.MemberGrade;
import com.slodon.b2b2c.member.pojo.MemberGradeLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class MemberExperienceLogModel {

    @Resource
    private MemberExperienceLogReadMapper memberExperienceLogReadMapper;
    @Resource
    private MemberExperienceLogWriteMapper memberExperienceLogWriteMapper;
    @Resource
    private MemberGradeReadMapper memberGradeReadMapper;
    @Resource
    private MemberGradeLogWriteMapper memberGradeLogWriteMapper;
    @Resource
    private MemberWriteMapper memberWriteMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 新增经验值变化日志表
     *
     * @param memberExperienceLog
     * @return
     */
    public Integer saveMemberExperienceLog(MemberExperienceLog memberExperienceLog) {
        int count = memberExperienceLogWriteMapper.insert(memberExperienceLog);
        if (count == 0) {
            throw new MallException("添加经验值变化日志表失败，请重试");
        }
        return count;
    }

    /**
     * 根据logId删除经验值变化日志表
     *
     * @param logId logId
     * @return
     */
    public Integer deleteMemberExperienceLog(Integer logId) {
        if (StringUtils.isEmpty(logId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = memberExperienceLogWriteMapper.deleteByPrimaryKey(logId);
        if (count == 0) {
            log.error("根据logId：" + logId + "删除经验值变化日志表失败");
            throw new MallException("删除经验值变化日志表失败,请重试");
        }
        return count;
    }

    /**
     * 根据logId更新经验值变化日志表
     *
     * @param memberExperienceLog
     * @return
     */
    public Integer updateMemberExperienceLog(MemberExperienceLog memberExperienceLog) {
        if (StringUtils.isEmpty(memberExperienceLog.getLogId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = memberExperienceLogWriteMapper.updateByPrimaryKeySelective(memberExperienceLog);
        if (count == 0) {
            log.error("根据logId：" + memberExperienceLog.getLogId() + "更新经验值变化日志表失败");
            throw new MallException("更新经验值变化日志表失败,请重试");
        }
        return count;
    }

    /**
     * 根据logId获取经验值变化日志表详情
     *
     * @param logId logId
     * @return
     */
    public MemberExperienceLog getMemberExperienceLogByLogId(Integer logId) {
        return memberExperienceLogReadMapper.getByPrimaryKey(logId);
    }

    /**
     * 根据条件获取经验值变化日志表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<MemberExperienceLog> getMemberExperienceLogList(MemberExperienceLogExample example, PagerInfo pager) {
        List<MemberExperienceLog> memberExperienceLogList;
        if (pager != null) {
            pager.setRowsCount(memberExperienceLogReadMapper.countByExample(example));
            memberExperienceLogList = memberExperienceLogReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            memberExperienceLogList = memberExperienceLogReadMapper.listByExample(example);
        }
        return memberExperienceLogList;
    }

    /**
     * 会员下单经验值变化（订单确认收货后调用）
     *
     * @param member
     * @param order
     * @return
     */
    public boolean updateOrderExperienceValue(Member member, Order order) {
        //下单赠送经验值数量
        int orderBuy = Integer.parseInt(stringRedisTemplate.opsForValue().get("experience_present_order_buy"));
        //下单最大赠送经验值数量
        int orderMax = Integer.parseInt(stringRedisTemplate.opsForValue().get("experience_present_order_max"));
        //订单评价赠送经验值数量
        int orderEvaluate = Integer.parseInt(stringRedisTemplate.opsForValue().get("experience_present_order_evaluate"));
        //计算赠送经验值
        int value = order.getBalanceAmount().add(order.getPayAmount()).intValue() / orderBuy;
        if (value <= 0) {
            return false;
        }
        //记录日志
        MemberExperienceLog experienceLogs = new MemberExperienceLog();
        experienceLogs.setMemberId(member.getMemberId());
        experienceLogs.setMemberName(member.getMemberName());
        value = Math.min(value, orderMax);
        experienceLogs.setExperienceValue(orderEvaluate);
        experienceLogs.setCreateTime(new Date());
        experienceLogs.setType(MemberGradeConst.MEMBER_GRD_INT_LOG_OPT_T_3);
        experienceLogs.setDescription("下单赠送经验，订单号：" + order.getOrderSn());
        experienceLogs.setRefCode(order.getOrderSn());
        int result = memberExperienceLogWriteMapper.insert(experienceLogs);
        AssertUtil.isTrue(result == 0, "记录经验值信息失败");

        //修改用户信息，记录等级日志
        this.updateMember(value, member);
        return true;
    }

    /**
     * 经验值变更，修改用户信息，记录等级日志
     *
     * @param changeEXPValue 变更的值
     * @param memberDb
     */
    private void updateMember(Integer changeEXPValue, Member memberDb) {
        Member updateMember = new Member();
        updateMember.setMemberId(memberDb.getMemberId());
        updateMember.setExperienceValue(memberDb.getExperienceValue() + changeEXPValue);
        updateMember.setUpdateTime(new Date());
        //查询会员等级列表，按照所需经验值倒序排列
        MemberGradeExample gradeExample = new MemberGradeExample();
        gradeExample.setOrderBy("experience_value desc");
        List<MemberGrade> memberGradeList = memberGradeReadMapper.listByExample(gradeExample);

        //经验变更后，能达到的新等级
        int newGrade = memberDb.getGrade();
        for (MemberGrade g : memberGradeList) {
            //遍历等级表，取用户升级后能达到的最高等级
            if (updateMember.getExperienceValue() >= g.getExperienceValue()) {
                newGrade = g.getGradeId();
                break;
            }
        }
        if (newGrade != memberDb.getGrade()) {
            //经验值变更后，等级发生了变化
            //更新用户等级
            updateMember.setGrade(newGrade);

            //记录升级日志
            MemberGradeLog gradeLogs = new MemberGradeLog();
            gradeLogs.setMemberId(memberDb.getMemberId());
            gradeLogs.setMemberName(memberDb.getMemberName());
            if (changeEXPValue > 0) {
                //升级
                gradeLogs.setChangeType(MemberGradeConst.CHANGE_TYPE_1);
            } else {
                //降级
                gradeLogs.setChangeType(MemberGradeConst.CHANGE_TYPE_2);
            }
            gradeLogs.setBeforeExper(memberDb.getExperienceValue());
            gradeLogs.setAfterExper(updateMember.getExperienceValue());
            gradeLogs.setBeforeGrade(memberDb.getGrade());
            gradeLogs.setAfterGrade(updateMember.getGrade());
            gradeLogs.setCreateTime(new Date());
            int result = memberGradeLogWriteMapper.insert(gradeLogs);
            AssertUtil.isTrue(result == 0, "记录等级信息失败");
        }

        //更新用户信息
        int result = memberWriteMapper.updateByPrimaryKeySelective(updateMember);
        AssertUtil.isTrue(result == 0, "更新用户信息失败");
    }
}