package com.slodon.b2b2c.model.member;

import com.slodon.b2b2c.dao.write.member.MemberBankAccountWriteMapper;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.member.example.MemberBankAccountExample;
import com.slodon.b2b2c.member.pojo.MemberBankAccount;
import com.slodon.b2b2c.dao.read.member.MemberBankAccountReadMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class MemberBankAccountModel {

    @Resource
    private MemberBankAccountReadMapper memberBankAccountReadMapper;
    @Resource
    private MemberBankAccountWriteMapper memberBankAccountWriteMapper;

    /**
     * 新增会员提现银行账号
     *
     * @param memberBankAccount
     * @return
     */
    public Integer saveMemberBankAccount(MemberBankAccount memberBankAccount) {
        int count = memberBankAccountWriteMapper.insert(memberBankAccount);
        if (count == 0) {
            throw new MallException("添加会员提现银行账号失败，请重试");
        }
        return count;
    }

    /**
     * 根据accountId删除会员提现银行账号
     *
     * @param accountId accountId
     * @return
     */
    public Integer deleteMemberBankAccount(Integer accountId) {
        if (StringUtils.isEmpty(accountId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = memberBankAccountWriteMapper.deleteByPrimaryKey(accountId);
        if (count == 0) {
            log.error("根据accountId：" + accountId + "删除会员提现银行账号失败");
            throw new MallException("删除会员提现银行账号失败,请重试");
        }
        return count;
    }

    /**
     * 根据accountId更新会员提现银行账号
     *
     * @param memberBankAccount
     * @return
     */
    public Integer updateMemberBankAccount(MemberBankAccount memberBankAccount) {
        if (StringUtils.isEmpty(memberBankAccount.getAccountId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = memberBankAccountWriteMapper.updateByPrimaryKeySelective(memberBankAccount);
        if (count == 0) {
            log.error("根据accountId：" + memberBankAccount.getAccountId() + "更新会员提现银行账号失败");
            throw new MallException("更新会员提现银行账号失败,请重试");
        }
        return count;
    }

    /**
     * 根据accountId获取会员提现银行账号详情
     *
     * @param accountId accountId
     * @return
     */
    public MemberBankAccount getMemberBankAccountByAccountId(Integer accountId) {
        return memberBankAccountReadMapper.getByPrimaryKey(accountId);
    }

    /**
     * 根据条件获取会员提现银行账号列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<MemberBankAccount> getMemberBankAccountList(MemberBankAccountExample example, PagerInfo pager) {
        List<MemberBankAccount> memberBankAccountList;
        if (pager != null) {
            pager.setRowsCount(memberBankAccountReadMapper.countByExample(example));
            memberBankAccountList = memberBankAccountReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            memberBankAccountList = memberBankAccountReadMapper.listByExample(example);
        }
        return memberBankAccountList;
    }
}