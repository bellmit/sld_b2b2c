package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.promotion.FullAsmRuleReadMapper;
import com.slodon.b2b2c.dao.write.promotion.FullAsmRuleWriteMapper;
import com.slodon.b2b2c.promotion.example.FullAsmRuleExample;
import com.slodon.b2b2c.promotion.pojo.FullAsmRule;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class FullAsmRuleModel {
    @Resource
    private FullAsmRuleReadMapper fullAsmRuleReadMapper;

    @Resource
    private FullAsmRuleWriteMapper fullAsmRuleWriteMapper;

    /**
     * 新增阶梯满金额减活动规则
     *
     * @param fullAsmRule
     * @return
     */
    public Integer saveFullAsmRule(FullAsmRule fullAsmRule) {
        int count = fullAsmRuleWriteMapper.insert(fullAsmRule);
        if (count == 0) {
            throw new MallException("添加阶梯满金额减活动规则失败，请重试");
        }
        return count;
    }

    /**
     * 根据ruleId删除阶梯满金额减活动规则
     *
     * @param ruleId ruleId
     * @return
     */
    public Integer deleteFullAsmRule(Integer ruleId) {
        if (StringUtils.isEmpty(ruleId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = fullAsmRuleWriteMapper.deleteByPrimaryKey(ruleId);
        if (count == 0) {
            log.error("根据ruleId：" + ruleId + "删除阶梯满金额减活动规则失败");
            throw new MallException("删除阶梯满金额减活动规则失败,请重试");
        }
        return count;
    }

    /**
     * 根据ruleId更新阶梯满金额减活动规则
     *
     * @param fullAsmRule
     * @return
     */
    public Integer updateFullAsmRule(FullAsmRule fullAsmRule) {
        if (StringUtils.isEmpty(fullAsmRule.getRuleId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = fullAsmRuleWriteMapper.updateByPrimaryKeySelective(fullAsmRule);
        if (count == 0) {
            log.error("根据ruleId：" + fullAsmRule.getRuleId() + "更新阶梯满金额减活动规则失败");
            throw new MallException("更新阶梯满金额减活动规则失败,请重试");
        }
        return count;
    }

    /**
     * 根据ruleId获取阶梯满金额减活动规则详情
     *
     * @param ruleId ruleId
     * @return
     */
    public FullAsmRule getFullAsmRuleByRuleId(Integer ruleId) {
        return fullAsmRuleReadMapper.getByPrimaryKey(ruleId);
    }

    /**
     * 根据条件获取阶梯满金额减活动规则列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<FullAsmRule> getFullAsmRuleList(FullAsmRuleExample example, PagerInfo pager) {
        List<FullAsmRule> fullAsmRuleList;
        if (pager != null) {
            pager.setRowsCount(fullAsmRuleReadMapper.countByExample(example));
            fullAsmRuleList = fullAsmRuleReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            fullAsmRuleList = fullAsmRuleReadMapper.listByExample(example);
        }
        return fullAsmRuleList;
    }
}