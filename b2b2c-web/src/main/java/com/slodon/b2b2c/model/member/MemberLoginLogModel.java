package com.slodon.b2b2c.model.member;

import com.slodon.b2b2c.core.constant.MemberConst;
import com.slodon.b2b2c.dao.write.member.MemberLoginLogWriteMapper;
import com.slodon.b2b2c.dao.write.member.MemberWriteMapper;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.dao.read.member.MemberLoginLogReadMapper;
import com.slodon.b2b2c.dao.read.member.MemberReadMapper;
import com.slodon.b2b2c.member.example.MemberExample;
import com.slodon.b2b2c.member.example.MemberLoginLogExample;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.member.pojo.MemberLoginLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class MemberLoginLogModel {

    @Resource
    private MemberLoginLogReadMapper memberLoginLogReadMapper;
    @Resource
    private MemberLoginLogWriteMapper memberLoginLogWriteMapper;
    @Resource
    private MemberReadMapper memberReadMapper;
    @Resource
    private MemberWriteMapper memberWriteMapper;

    /**
     * 新增用户会员登录日志表
     *
     * @param memberLoginLog
     * @return
     */
    public Integer saveMemberLoginLog(MemberLoginLog memberLoginLog) {
        int count = memberLoginLogWriteMapper.insert(memberLoginLog);
        if (count == 0) {
            throw new MallException("添加用户会员登录日志表失败，请重试");
        }
        return count;
    }

    /**
     * 根据logId删除用户会员登录日志表
     *
     * @param logId logId
     * @return
     */
    public Integer deleteMemberLoginLog(Integer logId) {
        if (StringUtils.isEmpty(logId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = memberLoginLogWriteMapper.deleteByPrimaryKey(logId);
        if (count == 0) {
            log.error("根据logId：" + logId + "删除用户会员登录日志表失败");
            throw new MallException("删除用户会员登录日志表失败,请重试");
        }
        return count;
    }

    /**
     * 根据logId更新用户会员登录日志表
     *
     * @param memberLoginLog
     * @return
     */
    public Integer updateMemberLoginLog(MemberLoginLog memberLoginLog) {
        if (StringUtils.isEmpty(memberLoginLog.getLogId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = memberLoginLogWriteMapper.updateByPrimaryKeySelective(memberLoginLog);
        if (count == 0) {
            log.error("根据logId：" + memberLoginLog.getLogId() + "更新用户会员登录日志表失败");
            throw new MallException("更新用户会员登录日志表失败,请重试");
        }
        return count;
    }

    /**
     * 根据logId获取用户会员登录日志表详情
     *
     * @param logId logId
     * @return
     */
    public MemberLoginLog getMemberLoginLogByLogId(Integer logId) {
        return memberLoginLogReadMapper.getByPrimaryKey(logId);
    }

    /**
     * 根据条件获取用户会员登录日志表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<MemberLoginLog> getMemberLoginLogList(MemberLoginLogExample example, PagerInfo pager) {
        List<MemberLoginLog> memberLoginLogList;
        if (pager != null) {
            pager.setRowsCount(memberLoginLogReadMapper.countByExample(example));
            memberLoginLogList = memberLoginLogReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            memberLoginLogList = memberLoginLogReadMapper.listByExample(example);
        }
        return memberLoginLogList;
    }

    /**
     * 手机号登录会员
     *
     * @param mobile
     * @param ip
     * @param source
     * @return
     */
    public Member memberLoginByMobile(String mobile, String ip, Integer source) {
        //查询用户
        MemberExample example = new MemberExample();
        example.setMemberMobile(mobile);
        List<Member> memberList = memberReadMapper.listByExample(example);
        AssertUtil.notEmpty(memberList, "该手机号未注册！");
        AssertUtil.isTrue(memberList.size() > 1, "手机号重复，请联系系统管理员！");
        Member member = memberList.get(0);

        AssertUtil.isTrue(!member.getState().equals(MemberConst.STATE_1), "会员账户状态异常，请联系系统管理员！");

        //1、更新会员表登录信息
        Member newMember = new Member();
        newMember.setMemberId(member.getMemberId());
        newMember.setLastLoginTime(new Date());
        newMember.setLastLoginIp(ip);
        //登录次数累加
        newMember.setLoginNumber(member.getLoginNumber() + 1);
        newMember.setUpdateTime(new Date());
        memberWriteMapper.updateByPrimaryKeySelective(newMember);

        //2、记录登录日志
        MemberLoginLog memberLoginLog = new MemberLoginLog();
        memberLoginLog.setMemberId(member.getMemberId());
        memberLoginLog.setMemberName(member.getMemberName());
        memberLoginLog.setLoginIp(ip);
        memberLoginLog.setSource(source);
        memberLoginLog.setCreateTime(new Date());
        memberLoginLogWriteMapper.insert(memberLoginLog);

        return member;
    }
}