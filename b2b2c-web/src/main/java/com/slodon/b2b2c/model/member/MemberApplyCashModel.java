package com.slodon.b2b2c.model.member;

import com.slodon.b2b2c.dao.write.member.MemberApplyCashWriteMapper;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.member.example.MemberApplyCashExample;
import com.slodon.b2b2c.member.pojo.MemberApplyCash;
import com.slodon.b2b2c.dao.read.member.MemberApplyCashReadMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class MemberApplyCashModel {

    @Resource
    private MemberApplyCashReadMapper memberApplyCashReadMapper;
    @Resource
    private MemberApplyCashWriteMapper memberApplyCashWriteMapper;

    /**
     * 新增会员提现申请
     *
     * @param memberApplyCash
     * @return
     */
    public Integer saveMemberApplyCash(MemberApplyCash memberApplyCash) {
        int count = memberApplyCashWriteMapper.insert(memberApplyCash);
        if (count == 0) {
            throw new MallException("添加会员提现申请失败，请重试");
        }
        return count;
    }

    /**
     * 根据cashId删除会员提现申请
     *
     * @param cashId cashId
     * @return
     */
    public Integer deleteMemberApplyCash(Integer cashId) {
        if (StringUtils.isEmpty(cashId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = memberApplyCashWriteMapper.deleteByPrimaryKey(cashId);
        if (count == 0) {
            log.error("根据cashId：" + cashId + "删除会员提现申请失败");
            throw new MallException("删除会员提现申请失败,请重试");
        }
        return count;
    }

    /**
     * 根据cashId更新会员提现申请
     *
     * @param memberApplyCash
     * @return
     */
    public Integer updateMemberApplyCash(MemberApplyCash memberApplyCash) {
        if (StringUtils.isEmpty(memberApplyCash.getCashId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = memberApplyCashWriteMapper.updateByPrimaryKeySelective(memberApplyCash);
        if (count == 0) {
            log.error("根据cashId：" + memberApplyCash.getCashId() + "更新会员提现申请失败");
            throw new MallException("更新会员提现申请失败,请重试");
        }
        return count;
    }

    /**
     * 根据cashId获取会员提现申请详情
     *
     * @param cashId cashId
     * @return
     */
    public MemberApplyCash getMemberApplyCashByCashId(Integer cashId) {
        return memberApplyCashReadMapper.getByPrimaryKey(cashId);
    }

    /**
     * 根据条件获取会员提现申请列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<MemberApplyCash> getMemberApplyCashList(MemberApplyCashExample example, PagerInfo pager) {
        List<MemberApplyCash> memberApplyCashList;
        if (pager != null) {
            pager.setRowsCount(memberApplyCashReadMapper.countByExample(example));
            memberApplyCashList = memberApplyCashReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            memberApplyCashList = memberApplyCashReadMapper.listByExample(example);
        }
        return memberApplyCashList;
    }
}