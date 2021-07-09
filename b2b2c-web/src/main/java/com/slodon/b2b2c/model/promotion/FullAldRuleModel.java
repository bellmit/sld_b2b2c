package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.promotion.FullAldRuleReadMapper;
import com.slodon.b2b2c.dao.write.promotion.FullAldRuleWriteMapper;
import com.slodon.b2b2c.promotion.example.FullAldRuleExample;
import com.slodon.b2b2c.promotion.pojo.FullAldRule;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class FullAldRuleModel {
    @Resource
    private FullAldRuleReadMapper fullAldRuleReadMapper;

    @Resource
    private FullAldRuleWriteMapper fullAldRuleWriteMapper;

    /**
     * 新增阶梯满折扣活动规则
     *
     * @param fullAldRule
     * @return
     */
    public Integer saveFullAldRule(FullAldRule fullAldRule) {
        int count = fullAldRuleWriteMapper.insert(fullAldRule);
        if (count == 0) {
            throw new MallException("添加阶梯满折扣活动规则失败，请重试");
        }
        return count;
    }

    /**
     * 根据ruleId删除阶梯满折扣活动规则
     *
     * @param ruleId ruleId
     * @return
     */
    public Integer deleteFullAldRule(Integer ruleId) {
        if (StringUtils.isEmpty(ruleId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = fullAldRuleWriteMapper.deleteByPrimaryKey(ruleId);
        if (count == 0) {
            log.error("根据ruleId：" + ruleId + "删除阶梯满折扣活动规则失败");
            throw new MallException("删除阶梯满折扣活动规则失败,请重试");
        }
        return count;
    }

    /**
     * 根据ruleId更新阶梯满折扣活动规则
     *
     * @param fullAldRule
     * @return
     */
    public Integer updateFullAldRule(FullAldRule fullAldRule) {
        if (StringUtils.isEmpty(fullAldRule.getRuleId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = fullAldRuleWriteMapper.updateByPrimaryKeySelective(fullAldRule);
        if (count == 0) {
            log.error("根据ruleId：" + fullAldRule.getRuleId() + "更新阶梯满折扣活动规则失败");
            throw new MallException("更新阶梯满折扣活动规则失败,请重试");
        }
        return count;
    }

    /**
     * 根据ruleId获取阶梯满折扣活动规则详情
     *
     * @param ruleId ruleId
     * @return
     */
    public FullAldRule getFullAldRuleByRuleId(Integer ruleId) {
        return fullAldRuleReadMapper.getByPrimaryKey(ruleId);
    }

    /**
     * 根据条件获取阶梯满折扣活动规则列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<FullAldRule> getFullAldRuleList(FullAldRuleExample example, PagerInfo pager) {
        List<FullAldRule> fullAldRuleList;
        if (pager != null) {
            pager.setRowsCount(fullAldRuleReadMapper.countByExample(example));
            fullAldRuleList = fullAldRuleReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            fullAldRuleList = fullAldRuleReadMapper.listByExample(example);
        }
        return fullAldRuleList;
    }
}