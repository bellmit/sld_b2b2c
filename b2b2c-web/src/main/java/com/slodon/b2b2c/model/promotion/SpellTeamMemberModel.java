package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.promotion.SpellTeamMemberReadMapper;
import com.slodon.b2b2c.dao.write.promotion.SpellTeamMemberWriteMapper;
import com.slodon.b2b2c.promotion.example.SpellTeamMemberExample;
import com.slodon.b2b2c.promotion.pojo.SpellTeamMember;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class SpellTeamMemberModel {

    @Resource
    private SpellTeamMemberReadMapper spellTeamMemberReadMapper;
    @Resource
    private SpellTeamMemberWriteMapper spellTeamMemberWriteMapper;

    /**
     * 新增拼团活动团队成员表
     *
     * @param spellTeamMember
     * @return
     */
    public Integer saveSpellTeamMember(SpellTeamMember spellTeamMember) {
        int count = spellTeamMemberWriteMapper.insert(spellTeamMember);
        if (count == 0) {
            throw new MallException("添加拼团活动团队成员表失败，请重试");
        }
        return count;
    }

    /**
     * 根据spellTeamMemberId删除拼团活动团队成员表
     *
     * @param spellTeamMemberId spellTeamMemberId
     * @return
     */
    public Integer deleteSpellTeamMember(Integer spellTeamMemberId) {
        if (StringUtils.isEmpty(spellTeamMemberId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = spellTeamMemberWriteMapper.deleteByPrimaryKey(spellTeamMemberId);
        if (count == 0) {
            log.error("根据spellTeamMemberId：" + spellTeamMemberId + "删除拼团活动团队成员表失败");
            throw new MallException("删除拼团活动团队成员表失败,请重试");
        }
        return count;
    }

    /**
     * 根据spellTeamMemberId更新拼团活动团队成员表
     *
     * @param spellTeamMember
     * @return
     */
    public Integer updateSpellTeamMember(SpellTeamMember spellTeamMember) {
        if (StringUtils.isEmpty(spellTeamMember.getSpellTeamMemberId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = spellTeamMemberWriteMapper.updateByPrimaryKeySelective(spellTeamMember);
        if (count == 0) {
            log.error("根据spellTeamMemberId：" + spellTeamMember.getSpellTeamMemberId() + "更新拼团活动团队成员表失败");
            throw new MallException("更新拼团活动团队成员表失败,请重试");
        }
        return count;
    }

    /**
     * 根据spellTeamMemberId获取拼团活动团队成员表详情
     *
     * @param spellTeamMemberId spellTeamMemberId
     * @return
     */
    public SpellTeamMember getSpellTeamMemberBySpellTeamMemberId(Integer spellTeamMemberId) {
        return spellTeamMemberReadMapper.getByPrimaryKey(spellTeamMemberId);
    }

    /**
     * 根据条件获取拼团活动团队成员表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<SpellTeamMember> getSpellTeamMemberList(SpellTeamMemberExample example, PagerInfo pager) {
        List<SpellTeamMember> spellTeamMemberList;
        if (pager != null) {
            pager.setRowsCount(spellTeamMemberReadMapper.countByExample(example));
            spellTeamMemberList = spellTeamMemberReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            spellTeamMemberList = spellTeamMemberReadMapper.listByExample(example);
        }
        return spellTeamMemberList;
    }
}