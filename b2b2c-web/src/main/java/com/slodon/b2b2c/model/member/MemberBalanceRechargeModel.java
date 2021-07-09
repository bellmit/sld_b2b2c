package com.slodon.b2b2c.model.member;

import com.slodon.b2b2c.core.constant.MemberConst;
import com.slodon.b2b2c.core.constant.MemberTplConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.dao.read.member.MemberBalanceRechargeReadMapper;
import com.slodon.b2b2c.dao.read.member.MemberReadMapper;
import com.slodon.b2b2c.dao.write.member.MemberBalanceLogWriteMapper;
import com.slodon.b2b2c.dao.write.member.MemberBalanceRechargeWriteMapper;
import com.slodon.b2b2c.dao.write.member.MemberWriteMapper;
import com.slodon.b2b2c.member.dto.MemberBalanceRechargeAddDTO;
import com.slodon.b2b2c.member.dto.MemberBalanceRechargeUpdateDTO;
import com.slodon.b2b2c.member.example.MemberBalanceRechargeExample;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.member.pojo.MemberBalanceLog;
import com.slodon.b2b2c.member.pojo.MemberBalanceRecharge;
import com.slodon.b2b2c.starter.mq.entity.MessageSendProperty;
import com.slodon.b2b2c.starter.mq.entity.MessageSendVO;
import com.slodon.b2b2c.system.pojo.Admin;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.slodon.b2b2c.core.constant.StarterConfigConst.MQ_EXCHANGE_NAME;
import static com.slodon.b2b2c.core.constant.StarterConfigConst.MQ_QUEUE_NAME_MEMBER_MSG;

@Component
@Slf4j
public class MemberBalanceRechargeModel {

    @Resource
    private MemberBalanceRechargeReadMapper memberBalanceRechargeReadMapper;
    @Resource
    private MemberBalanceRechargeWriteMapper memberBalanceRechargeWriteMapper;
    @Resource
    private MemberReadMapper memberReadMapper;
    @Resource
    private MemberWriteMapper memberWriteMapper;
    @Resource
    private MemberBalanceLogWriteMapper memberBalanceLogWriteMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 新增会员充值明细表
     *
     * @param memberBalanceRecharge
     * @return
     */
    public Integer saveMemberBalanceRecharge(MemberBalanceRecharge memberBalanceRecharge) {
        int count = memberBalanceRechargeWriteMapper.insert(memberBalanceRecharge);
        if (count == 0) {
            throw new MallException("添加会员充值明细表失败，请重试");
        }
        return count;
    }

    /**
     * 新增会员充值明细表
     *
     * @param memberBalanceRechargeAddDTO
     * @param member
     * @return
     */
    @Transactional
    public Integer saveMemberBalanceRecharge(MemberBalanceRechargeAddDTO memberBalanceRechargeAddDTO, Member member) throws Exception {

        //1.新增会员充值明细表
        //组装条件
        MemberBalanceRecharge balanceRecharge = new MemberBalanceRecharge();
        PropertyUtils.copyProperties(balanceRecharge, memberBalanceRechargeAddDTO);
        balanceRecharge.setPayState(MemberConst.PAY_STATE_2);
        balanceRecharge.setMemberId(member.getMemberId());
        balanceRecharge.setMemberName(member.getMemberName());
        balanceRecharge.setMemberMobile(member.getMemberMobile());
        balanceRecharge.setCreateTime(new Date());
        //修改
        int count = memberBalanceRechargeWriteMapper.insert(balanceRecharge);
        if (count == 0) {
            log.error("添加会员充值明细表失败");
            throw new MallException("添加会员充值明细表失败,请重试");
        }

        //2.更新会员表 member  可用现金余额
        member.setBalanceAvailable(member.getBalanceAvailable().add(memberBalanceRechargeAddDTO.getPayAmount()));
        //修改
        int countMember = memberWriteMapper.updateByPrimaryKeySelective(member);
        if (countMember == 0) {
            log.error("更新会员表失败");
            throw new MallException("更新会员表失败,请重试");
        }

        //3.新增 账户余额变化明细表 member_balance_log
        MemberBalanceLog insertOne = new MemberBalanceLog();
        insertOne.setMemberId(member.getMemberId());
        insertOne.setMemberName(member.getMemberName());
        insertOne.setAfterChangeAmount(member.getBalanceAvailable());
        insertOne.setChangeValue(memberBalanceRechargeAddDTO.getPayAmount());
        insertOne.setFreezeAmount(member.getBalanceFrozen());
        insertOne.setFreezeValue(BigDecimal.ZERO);
        insertOne.setCreateTime(new Date());
        insertOne.setType(MemberConst.TYPE_1);
        insertOne.setDescription("充值");
        int countBalanceLog = memberBalanceLogWriteMapper.insert(insertOne);
        if (countBalanceLog == 0) {
            log.error("新增账户余额变化明细表失败");
            throw new MallException("新增账户余额变化明细表失败,请重试");
        }

        return count;
    }

    /**
     * 根据rechargeId删除会员充值明细表
     *
     * @param rechargeId rechargeId
     * @return
     */
    public Integer deleteMemberBalanceRecharge(Integer rechargeId) {
        if (StringUtils.isEmpty(rechargeId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = memberBalanceRechargeWriteMapper.deleteByPrimaryKey(rechargeId);
        if (count == 0) {
            log.error("根据rechargeId：" + rechargeId + "删除会员充值明细表失败");
            throw new MallException("删除会员充值明细表失败,请重试");
        }
        return count;
    }


    /**
     * 根据rechargeId更新会员充值明细表
     *
     * @param memberBalanceRecharge
     * @return
     */
    public Integer updateMemberBalanceRecharge(MemberBalanceRecharge memberBalanceRecharge) {
        if (StringUtils.isEmpty(memberBalanceRecharge.getRechargeId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = memberBalanceRechargeWriteMapper.updateByPrimaryKeySelective(memberBalanceRecharge);
        if (count == 0) {
            log.error("根据rechargeId：" + memberBalanceRecharge.getRechargeId() + "更新会员充值明细表失败");
            throw new MallException("更新会员充值明细表失败,请重试");
        }
        return count;
    }

    /**
     * 根据rechargeId更新会员充值明细表
     * 去付款
     *
     * @param memberBalanceRechargeUpdateDTO
     * @param admin
     * @return
     */
    @Transactional
    public Integer updateMemberBalanceRecharge(MemberBalanceRechargeUpdateDTO memberBalanceRechargeUpdateDTO, Admin admin) throws Exception {

        //1.更新会员充值明细表
        //组装条件
        MemberBalanceRecharge updateOne = new MemberBalanceRecharge();
        PropertyUtils.copyProperties(updateOne, memberBalanceRechargeUpdateDTO);
        updateOne.setAdminId(admin.getAdminId());
        updateOne.setAdminName(admin.getAdminName());
        updateOne.setPayState(MemberConst.PAY_STATE_2);
        //修改
        int count = memberBalanceRechargeWriteMapper.updateByPrimaryKeySelective(updateOne);
        if (count == 0) {
            log.error("根据rechargeId：" + memberBalanceRechargeUpdateDTO.getRechargeId() + "更新会员充值明细表失败");
            throw new MallException("更新会员充值明细表失败,请重试");
        }

        //2.更新会员表 member  可用现金余额
        //获取会员充值明细表信息,得到会员id
        MemberBalanceRecharge memberBalanceRecharge = memberBalanceRechargeReadMapper.getByPrimaryKey(memberBalanceRechargeUpdateDTO.getRechargeId());
        Member member = memberReadMapper.getByPrimaryKey(memberBalanceRecharge.getMemberId());
        member.setBalanceAvailable(member.getBalanceAvailable().add(memberBalanceRecharge.getPayAmount()));
        //修改
        int countMember = memberWriteMapper.updateByPrimaryKeySelective(member);
        if (countMember == 0) {
            log.error("根据rechargeId：" + memberBalanceRechargeUpdateDTO.getRechargeId() + "更新会员表失败");
            throw new MallException("更新会员表失败,请重试");
        }


        //3.新增 账户余额变化明细表 member_balance_log
        MemberBalanceLog insertOne = new MemberBalanceLog();
        insertOne.setMemberId(member.getMemberId());
        insertOne.setMemberName(member.getMemberName());
        insertOne.setAfterChangeAmount(member.getBalanceAvailable());
        insertOne.setChangeValue(memberBalanceRecharge.getPayAmount());
        insertOne.setFreezeAmount(member.getBalanceFrozen());
        insertOne.setFreezeValue(BigDecimal.ZERO);
        insertOne.setCreateTime(new Date());
        insertOne.setType(MemberConst.TYPE_1);
        insertOne.setDescription("充值");
        insertOne.setAdminName(admin.getAdminName());
        int countBalanceLog = memberBalanceLogWriteMapper.insert(insertOne);
        if (countBalanceLog == 0) {
            log.error("根据rechargeId：" + memberBalanceRechargeUpdateDTO.getRechargeId() + "新增账户余额变化明细表失败");
            throw new MallException("新增账户余额变化明细表失败,请重试");
        }

        return count;
    }

    /**
     * 根据rechargeId获取会员充值明细表详情
     *
     * @param rechargeId rechargeId
     * @return
     */
    public MemberBalanceRecharge getMemberBalanceRechargeByRechargeId(Integer rechargeId) {
        return memberBalanceRechargeReadMapper.getByPrimaryKey(rechargeId);
    }

    /**
     * 根据rechargeSn获取会员充值明细详情
     *
     * @param rechargeSn rechargeSn
     * @return
     */
    public MemberBalanceRecharge getMemberBalanceRechargeByRechargeSn(String rechargeSn) {
        MemberBalanceRechargeExample example = new MemberBalanceRechargeExample();
        example.setRechargeSn(rechargeSn);
        List<MemberBalanceRecharge> list = memberBalanceRechargeReadMapper.listByExample(example);
        AssertUtil.notEmpty(list, "充值信息不存在");
        return list.get(0);
    }

    /**
     * 根据条件获取会员充值明细表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<MemberBalanceRecharge> getMemberBalanceRechargeList(MemberBalanceRechargeExample example, PagerInfo pager) {
        List<MemberBalanceRecharge> memberBalanceRechargeList;
        if (pager != null) {
            pager.setRowsCount(memberBalanceRechargeReadMapper.countByExample(example));
            memberBalanceRechargeList = memberBalanceRechargeReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            memberBalanceRechargeList = memberBalanceRechargeReadMapper.listByExample(example);
        }
        return memberBalanceRechargeList;
    }

    /**
     * 根据条件查询累计充值人数
     *
     * @param example 查询条件信息
     * @return
     */
    public Integer getRechargeNumber(MemberBalanceRechargeExample example) {
        return memberBalanceRechargeReadMapper.getRechargeNumber(example);
    }

    /**
     * 充值 支付后处理
     *
     * @param paySn   支付单号
     * @param tradeSn 交易流水号
     * @return
     */
    @Transactional
    public Boolean payAfter(String paySn, String tradeSn) {
        //查询充值记录
        MemberBalanceRecharge rechargeLogDb = this.getMemberBalanceRechargeByRechargeSn(paySn);
        if (rechargeLogDb.getPayState() == MemberConst.PAY_STATE_2) {
            log.debug("该订单已支付，直接返回");
            return true;
        }

        //更新充值记录状态
        MemberBalanceRecharge rechargeLog = new MemberBalanceRecharge();
        rechargeLog.setRechargeId(rechargeLogDb.getRechargeId());
        rechargeLog.setTradeSn(tradeSn);
        rechargeLog.setPayState(MemberConst.PAY_STATE_2);
        rechargeLog.setPayTime(new Date());
        int count = memberBalanceRechargeWriteMapper.updateByPrimaryKeySelective(rechargeLog);
        AssertUtil.isTrue(count == 0, "更新充值记录状态失败，请重试！");

        //查询会员信息
        Member member = memberWriteMapper.getByPrimaryKey(rechargeLogDb.getMemberId());
        AssertUtil.notNull(member, "会员不存在，请重试！");

        //更新会员的可用余额
        Member newMember = new Member();
        newMember.setMemberId(rechargeLogDb.getMemberId());
        newMember.setBalanceAvailable(member.getBalanceAvailable().add(rechargeLogDb.getPayAmount()));
        newMember.setUpdateTime(new Date());
        count = memberWriteMapper.updateByPrimaryKeySelective(newMember);
        AssertUtil.isTrue(count == 0, "更新会员余额失败，请重试！");

        //记录余额日志
        MemberBalanceLog memberBalanceLog = new MemberBalanceLog();
        memberBalanceLog.setMemberId(rechargeLogDb.getMemberId());
        memberBalanceLog.setMemberName(member.getMemberName());
        memberBalanceLog.setAfterChangeAmount(member.getBalance().add(rechargeLogDb.getPayAmount()));
        memberBalanceLog.setChangeValue(rechargeLogDb.getPayAmount());
        memberBalanceLog.setFreezeAmount(member.getBalanceFrozen());
        memberBalanceLog.setFreezeValue(BigDecimal.ZERO);
        memberBalanceLog.setCreateTime(new Date());
        memberBalanceLog.setType(MemberConst.TYPE_1);
        memberBalanceLog.setDescription("余额充值");
        memberBalanceLog.setAdminId(0);
        memberBalanceLog.setAdminName("");
        count = memberBalanceLogWriteMapper.insert(memberBalanceLog);
        AssertUtil.isTrue(count == 0, "记录余额日志失败，请重试！");

        this.sendMsgAccountChange(memberBalanceLog);
        return true;
    }

    /**
     * 批量删除会员充值明细表
     * 列表查询页可以删除充值失败的
     *
     * @param rechargeIds
     * @return
     */
    public Integer batchDeleteMemberBalanceRecharge(String rechargeIds) {
        MemberBalanceRechargeExample example = new MemberBalanceRechargeExample();
        example.setRechargeIdIn(rechargeIds);
        int count = memberBalanceRechargeWriteMapper.deleteByExample(example);
        if (count == 0) {
            log.error("根据rechargeIds：" + rechargeIds + "删除会员充值明细表失败");
            throw new MallException("删除会员充值明细表失败,请重试");
        }
        return count;
    }

    /**
     * 发送余额变动消息通知
     *
     * @param memberBalanceLog 余额变动信息
     */
    public void sendMsgAccountChange(MemberBalanceLog memberBalanceLog) {
        //消息通知
        List<MessageSendProperty> messageSendPropertyList = new ArrayList<>();
        messageSendPropertyList.add(new MessageSendProperty("description", "余额充值"));
        messageSendPropertyList.add(new MessageSendProperty("availableBalance", memberBalanceLog.getAfterChangeAmount().toString()));
        messageSendPropertyList.add(new MessageSendProperty("frozenBalance", memberBalanceLog.getFreezeAmount().toString()));
        //微信消息通知
        List<MessageSendProperty> messageSendPropertyList4Wx = new ArrayList<>();
        messageSendPropertyList4Wx.add(new MessageSendProperty("first", "您的账户发生了资金变动。"));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword1", "余额充值"));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword2", memberBalanceLog.getChangeValue().toString()));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword3", TimeUtil.getDateTimeString(memberBalanceLog.getCreateTime())));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword4", memberBalanceLog.getAfterChangeAmount().toString()));
        String msgLinkInfo = "{\"type\":\"balance_change\"}";
        MessageSendVO messageSendVO = new MessageSendVO(messageSendPropertyList, messageSendPropertyList4Wx, "changeTime", memberBalanceLog.getMemberId(), MemberTplConst.BALANCE_CHANGE_REMINDER, msgLinkInfo);
        //发送到mq
        rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_MEMBER_MSG, messageSendVO);
    }
}