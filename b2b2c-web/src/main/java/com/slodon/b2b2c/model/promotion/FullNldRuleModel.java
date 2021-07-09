package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.promotion.FullNldRuleReadMapper;
import com.slodon.b2b2c.dao.write.promotion.FullNldRuleWriteMapper;
import com.slodon.b2b2c.promotion.example.FullNldRuleExample;
import com.slodon.b2b2c.promotion.pojo.FullNldRule;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class FullNldRuleModel {
    @Resource
    private FullNldRuleReadMapper fullNldRuleReadMapper;

    @Resource
    private FullNldRuleWriteMapper fullNldRuleWriteMapper;

    /**
     * 新增阶梯满件折扣活动规则
     *
     * @param fullNldRule
     * @return
     */
    public Integer saveFullNldRule(FullNldRule fullNldRule) {
        int count = fullNldRuleWriteMapper.insert(fullNldRule);
        if (count == 0) {
            throw new MallException("添加阶梯满件折扣活动规则失败，请重试");
        }
        return count;
    }

    /**
     * 根据ruleId删除阶梯满件折扣活动规则
     *
     * @param ruleId ruleId
     * @return
     */
    public Integer deleteFullNldRule(Integer ruleId) {
        if (StringUtils.isEmpty(ruleId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = fullNldRuleWriteMapper.deleteByPrimaryKey(ruleId);
        if (count == 0) {
            log.error("根据ruleId：" + ruleId + "删除阶梯满件折扣活动规则失败");
            throw new MallException("删除阶梯满件折扣活动规则失败,请重试");
        }
        return count;
    }

    /**
     * 根据ruleId更新阶梯满件折扣活动规则
     *
     * @param fullNldRule
     * @return
     */
    public Integer updateFullNldRule(FullNldRule fullNldRule) {
        if (StringUtils.isEmpty(fullNldRule.getRuleId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = fullNldRuleWriteMapper.updateByPrimaryKeySelective(fullNldRule);
        if (count == 0) {
            log.error("根据ruleId：" + fullNldRule.getRuleId() + "更新阶梯满件折扣活动规则失败");
            throw new MallException("更新阶梯满件折扣活动规则失败,请重试");
        }
        return count;
    }

    /**
     * 根据ruleId获取阶梯满件折扣活动规则详情
     *
     * @param ruleId ruleId
     * @return
     */
    public FullNldRule getFullNldRuleByRuleId(Integer ruleId) {
        return fullNldRuleReadMapper.getByPrimaryKey(ruleId);
    }

    /**
     * 根据条件获取阶梯满件折扣活动规则列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<FullNldRule> getFullNldRuleList(FullNldRuleExample example, PagerInfo pager) {
        List<FullNldRule> fullNldRuleList;
        if (pager != null) {
            pager.setRowsCount(fullNldRuleReadMapper.countByExample(example));
            fullNldRuleList = fullNldRuleReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            fullNldRuleList = fullNldRuleReadMapper.listByExample(example);
        }
        return fullNldRuleList;
    }
}