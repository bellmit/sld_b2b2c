package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.promotion.LadderGroupRuleReadMapper;
import com.slodon.b2b2c.dao.write.promotion.LadderGroupRuleWriteMapper;
import com.slodon.b2b2c.promotion.example.LadderGroupRuleExample;
import com.slodon.b2b2c.promotion.pojo.LadderGroupRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 阶梯团规则表model
 */
@Component
@Slf4j
public class LadderGroupRuleModel {

    @Resource
    private LadderGroupRuleReadMapper ladderGroupRuleReadMapper;
    @Resource
    private LadderGroupRuleWriteMapper ladderGroupRuleWriteMapper;

    /**
     * 新增阶梯团规则表
     *
     * @param ladderGroupRule
     * @return
     */
    public Integer saveLadderGroupRule(LadderGroupRule ladderGroupRule) {
        int count = ladderGroupRuleWriteMapper.insert(ladderGroupRule);
        if (count == 0) {
            throw new MallException("添加阶梯团规则表失败，请重试");
        }
        return count;
    }

    /**
     * 根据ruleId删除阶梯团规则表
     *
     * @param ruleId ruleId
     * @return
     */
    public Integer deleteLadderGroupRule(Integer ruleId) {
        if (StringUtils.isEmpty(ruleId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = ladderGroupRuleWriteMapper.deleteByPrimaryKey(ruleId);
        if (count == 0) {
            log.error("根据ruleId：" + ruleId + "删除阶梯团规则表失败");
            throw new MallException("删除阶梯团规则表失败,请重试");
        }
        return count;
    }

    /**
     * 根据ruleId更新阶梯团规则表
     *
     * @param ladderGroupRule
     * @return
     */
    public Integer updateLadderGroupRule(LadderGroupRule ladderGroupRule) {
        if (StringUtils.isEmpty(ladderGroupRule.getRuleId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = ladderGroupRuleWriteMapper.updateByPrimaryKeySelective(ladderGroupRule);
        if (count == 0) {
            log.error("根据ruleId：" + ladderGroupRule.getRuleId() + "更新阶梯团规则表失败");
            throw new MallException("更新阶梯团规则表失败,请重试");
        }
        return count;
    }

    /**
     * 根据ruleId获取阶梯团规则表详情
     *
     * @param ruleId ruleId
     * @return
     */
    public LadderGroupRule getLadderGroupRuleByRuleId(Integer ruleId) {
        return ladderGroupRuleReadMapper.getByPrimaryKey(ruleId);
    }

    /**
     * 根据条件获取阶梯团规则表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<LadderGroupRule> getLadderGroupRuleList(LadderGroupRuleExample example, PagerInfo pager) {
        List<LadderGroupRule> ladderGroupRuleList;
        if (pager != null) {
            pager.setRowsCount(ladderGroupRuleReadMapper.countByExample(example));
            ladderGroupRuleList = ladderGroupRuleReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            ladderGroupRuleList = ladderGroupRuleReadMapper.listByExample(example);
        }
        return ladderGroupRuleList;
    }
}