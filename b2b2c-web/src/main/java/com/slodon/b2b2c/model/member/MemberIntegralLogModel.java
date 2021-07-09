package com.slodon.b2b2c.model.member;

import com.slodon.b2b2c.core.constant.MemberIntegralLogConst;
import com.slodon.b2b2c.core.constant.MemberTplConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.dao.read.member.MemberIntegralLogReadMapper;
import com.slodon.b2b2c.dao.read.member.MemberReadMapper;
import com.slodon.b2b2c.dao.write.member.MemberIntegralLogWriteMapper;
import com.slodon.b2b2c.dao.write.member.MemberWriteMapper;
import com.slodon.b2b2c.member.dto.MemberIntegralLogUpdateDTO;
import com.slodon.b2b2c.member.example.MemberIntegralLogExample;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.member.pojo.MemberIntegralLog;
import com.slodon.b2b2c.starter.mq.entity.MemberIntegralVO;
import com.slodon.b2b2c.starter.mq.entity.MessageSendProperty;
import com.slodon.b2b2c.starter.mq.entity.MessageSendVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.slodon.b2b2c.core.constant.StarterConfigConst.MQ_EXCHANGE_NAME;
import static com.slodon.b2b2c.core.constant.StarterConfigConst.MQ_QUEUE_NAME_MEMBER_MSG;

@Component
@Slf4j
public class MemberIntegralLogModel {

    @Resource
    private MemberIntegralLogReadMapper memberIntegralLogReadMapper;
    @Resource
    private MemberIntegralLogWriteMapper memberIntegralLogWriteMapper;
    @Resource
    private MemberReadMapper memberReadMapper;
    @Resource
    private MemberWriteMapper memberWriteMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 新增积分变化日志表
     *
     * @param memberIntegralLog
     * @return
     */
    public Integer saveMemberIntegralLog(MemberIntegralLog memberIntegralLog) {
        int count = memberIntegralLogWriteMapper.insert(memberIntegralLog);
        if (count == 0) {
            throw new MallException("添加积分变化日志表失败，请重试");
        }
        return count;
    }

    /**
     * 根据logId删除积分变化日志表
     *
     * @param logId logId
     * @return
     */
    public Integer deleteMemberIntegralLog(Integer logId) {
        if (StringUtils.isEmpty(logId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = memberIntegralLogWriteMapper.deleteByPrimaryKey(logId);
        if (count == 0) {
            log.error("根据logId：" + logId + "删除积分变化日志表失败");
            throw new MallException("删除积分变化日志表失败,请重试");
        }
        return count;
    }

    /**
     * 根据logId更新积分变化日志表
     *
     * @param memberIntegralLog
     * @return
     */
    public Integer updateMemberIntegralLog(MemberIntegralLog memberIntegralLog) {
        if (StringUtils.isEmpty(memberIntegralLog.getLogId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = memberIntegralLogWriteMapper.updateByPrimaryKeySelective(memberIntegralLog);
        if (count == 0) {
            log.error("根据logId：" + memberIntegralLog.getLogId() + "更新积分变化日志表失败");
            throw new MallException("更新积分变化日志表失败,请重试");
        }
        return count;
    }

    /**
     * 根据logId更新积分变化日志表
     *
     * @param memberIntegralLogUpdateDTO
     * @param adminId
     * @param adminName
     * @return
     */
    @Transactional
    public Integer editMemberIntegral(MemberIntegralLogUpdateDTO memberIntegralLogUpdateDTO, Integer adminId, String adminName) throws Exception {

        Member memberDb = memberReadMapper.getByPrimaryKey(memberIntegralLogUpdateDTO.getMemberId());
        //新增积分变化日志表
        MemberIntegralLog insertOne = new MemberIntegralLog();
        PropertyUtils.copyProperties(insertOne, memberIntegralLogUpdateDTO);
        insertOne.setMemberName(memberDb.getMemberName());
        insertOne.setCreateTime(new Date());
        insertOne.setType(memberIntegralLogUpdateDTO.getType() == MemberIntegralLogConst.ADMIN_TYPE_1 ? MemberIntegralLogConst.TYPE_5 : MemberIntegralLogConst.TYPE_6);
        insertOne.setDescription(StringUtil.isEmpty(memberIntegralLogUpdateDTO.getDescription()) ? (memberIntegralLogUpdateDTO.getType() == MemberIntegralLogConst.ADMIN_TYPE_1 ? "系统添加" : "系统减少") : memberIntegralLogUpdateDTO.getDescription());
        insertOne.setOptId(adminId);
        insertOne.setOptName(adminName);
        saveMemberIntegralLog(insertOne);

        //积分描述
        String description = "";
        //根据memberId更新会员表
        Member updateOne = new Member();
        updateOne.setMemberId(memberIntegralLogUpdateDTO.getMemberId());
        updateOne.setUpdateTime(new Date());
        if (memberIntegralLogUpdateDTO.getType() == MemberIntegralLogConst.ADMIN_TYPE_1) {
            updateOne.setMemberIntegral(memberDb.getMemberIntegral() + memberIntegralLogUpdateDTO.getValue());
            AssertUtil.isTrue(((long) memberDb.getMemberIntegral() + (long) memberIntegralLogUpdateDTO.getValue()) > Integer.MAX_VALUE || ((long) memberDb.getMemberIntegral() + (long) memberIntegralLogUpdateDTO.getValue()) < Integer.MIN_VALUE, "您的会员可用积分必须在-2147483648~2147483647之间,请重试");
            description = "系统增加（备注：" + memberIntegralLogUpdateDTO.getDescription() + "）";
        } else {
            updateOne.setMemberIntegral(memberDb.getMemberIntegral() - memberIntegralLogUpdateDTO.getValue());
            AssertUtil.isTrue(((long) memberDb.getMemberIntegral() - (long) memberIntegralLogUpdateDTO.getValue()) < Integer.MIN_VALUE || ((long) memberDb.getMemberIntegral() - (long) memberIntegralLogUpdateDTO.getValue()) > Integer.MAX_VALUE, "您的会员可用积分必须在-2147483648~2147483647之间,请重试");
            description = "系统减少（备注：" + memberIntegralLogUpdateDTO.getDescription() + "）";
        }
        int count = memberWriteMapper.updateByPrimaryKeySelective(updateOne);
        AssertUtil.notNullOrZero(count, "更新会员表积分失败");

        //发送积分变动消息通知
        this.sendMsgIntegralChange(updateOne, description);

        return count;
    }

    /**
     * 发送积分变动消息通知
     *
     * @param member 会员信息
     */
    public void sendMsgIntegralChange(Member member, String description) {
        //消息通知
        List<MessageSendProperty> messageSendPropertyList = new ArrayList<>();
        messageSendPropertyList.add(new MessageSendProperty("description", description));
        messageSendPropertyList.add(new MessageSendProperty("availableIntegral", member.getMemberIntegral().toString()));
        //微信消息通知
        List<MessageSendProperty> messageSendPropertyList4Wx = new ArrayList<>();
        messageSendPropertyList4Wx.add(new MessageSendProperty("first", "您的账户发生了积分变动。"));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword1", TimeUtil.getDateTimeString(new Date())));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword2", description));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword3", member.getMemberIntegral().toString()));
        String msgLinkInfo = "{\"type\":\"integral_change\"}";
        MessageSendVO messageSendVO = new MessageSendVO(messageSendPropertyList, messageSendPropertyList4Wx, "changeTime", member.getMemberId(), MemberTplConst.INTEGRAL_CHANGE_REMINDER, msgLinkInfo);
        //发送到mq
        rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_MEMBER_MSG, messageSendVO);
    }

    /**
     * 根据logId获取积分变化日志表详情
     *
     * @param logId logId
     * @return
     */
    public MemberIntegralLog getMemberIntegralLogByLogId(Integer logId) {
        return memberIntegralLogReadMapper.getByPrimaryKey(logId);
    }

    /**
     * 根据条件获取积分变化日志表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<MemberIntegralLog> getMemberIntegralLogList(MemberIntegralLogExample example, PagerInfo pager) {
        List<MemberIntegralLog> memberIntegralLogList;
        if (pager != null) {
            pager.setRowsCount(memberIntegralLogReadMapper.countByExample(example));
            memberIntegralLogList = memberIntegralLogReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            memberIntegralLogList = memberIntegralLogReadMapper.listByExample(example);
        }
        return memberIntegralLogList;
    }

    /**
     * 会员赠送积分
     *
     * @param memberIntegralVO
     * @return
     */
    @Transactional
    public Integer memberSendIntegral(MemberIntegralVO memberIntegralVO) {
        //记录积分日志
        MemberIntegralLog integralLog = new MemberIntegralLog();
        integralLog.setMemberId(memberIntegralVO.getMemberId());
        integralLog.setMemberName(memberIntegralVO.getMemberName());
        integralLog.setValue(memberIntegralVO.getValue());
        integralLog.setCreateTime(new Date());
        integralLog.setType(memberIntegralVO.getType());
        integralLog.setDescription(memberIntegralVO.getDescription());
        integralLog.setRefCode(memberIntegralVO.getRefCode());
        integralLog.setOptId(memberIntegralVO.getOptId());
        integralLog.setOptName(memberIntegralVO.getOptName());
        int result = memberIntegralLogWriteMapper.insert(integralLog);
        AssertUtil.isTrue(result == 0, "记录积分信息失败");

        //查询用户信息
        Member memberDb = memberReadMapper.getByPrimaryKey(memberIntegralVO.getMemberId());
        AssertUtil.notNull(memberDb, "用户不存在");

        //修改用户信息
        Member updateMember = new Member();
        updateMember.setMemberId(memberDb.getMemberId());
        updateMember.setMemberIntegral(memberDb.getMemberIntegral() + memberIntegralVO.getValue());
        updateMember.setUpdateTime(new Date());
        result = memberWriteMapper.updateByPrimaryKeySelective(updateMember);
        AssertUtil.isTrue(result == 0, "更新用户积分信息失败");
        return result;
    }
}