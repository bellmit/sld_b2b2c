package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.promotion.SpellTeamReadMapper;
import com.slodon.b2b2c.dao.write.promotion.SpellTeamWriteMapper;
import com.slodon.b2b2c.promotion.example.SpellTeamExample;
import com.slodon.b2b2c.promotion.pojo.SpellTeam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class SpellTeamModel {

    @Resource
    private SpellTeamReadMapper spellTeamReadMapper;
    @Resource
    private SpellTeamWriteMapper spellTeamWriteMapper;

    /**
     * 新增拼团活动团队表
     *
     * @param spellTeam
     * @return
     */
    public Integer saveSpellTeam(SpellTeam spellTeam) {
        int count = spellTeamWriteMapper.insert(spellTeam);
        if (count == 0) {
            throw new MallException("添加拼团活动团队表失败，请重试");
        }
        return count;
    }

    /**
     * 根据spellTeamId删除拼团活动团队表
     *
     * @param spellTeamId spellTeamId
     * @return
     */
    public Integer deleteSpellTeam(Integer spellTeamId) {
        if (StringUtils.isEmpty(spellTeamId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = spellTeamWriteMapper.deleteByPrimaryKey(spellTeamId);
        if (count == 0) {
            log.error("根据spellTeamId：" + spellTeamId + "删除拼团活动团队表失败");
            throw new MallException("删除拼团活动团队表失败,请重试");
        }
        return count;
    }

    /**
     * 根据spellTeamId更新拼团活动团队表
     *
     * @param spellTeam
     * @return
     */
    public Integer updateSpellTeam(SpellTeam spellTeam) {
        if (StringUtils.isEmpty(spellTeam.getSpellTeamId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = spellTeamWriteMapper.updateByPrimaryKeySelective(spellTeam);
        if (count == 0) {
            log.error("根据spellTeamId：" + spellTeam.getSpellTeamId() + "更新拼团活动团队表失败");
            throw new MallException("更新拼团活动团队表失败,请重试");
        }
        return count;
    }

    /**
     * 根据spellTeamId获取拼团活动团队表详情
     *
     * @param spellTeamId spellTeamId
     * @return
     */
    public SpellTeam getSpellTeamBySpellTeamId(Integer spellTeamId) {
        return spellTeamReadMapper.getByPrimaryKey(spellTeamId);
    }

    /**
     * 根据条件获取拼团活动团队表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<SpellTeam> getSpellTeamList(SpellTeamExample example, PagerInfo pager) {
        List<SpellTeam> spellTeamList;
        if (pager != null) {
            pager.setRowsCount(spellTeamReadMapper.countByExample(example));
            spellTeamList = spellTeamReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            spellTeamList = spellTeamReadMapper.listByExample(example);
        }
        return spellTeamList;
    }

}