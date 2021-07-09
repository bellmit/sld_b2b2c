package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.promotion.SpellLabelReadMapper;
import com.slodon.b2b2c.dao.write.promotion.SpellLabelWriteMapper;
import com.slodon.b2b2c.promotion.example.SpellLabelExample;
import com.slodon.b2b2c.promotion.pojo.SpellLabel;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class SpellLabelModel {
    @Resource
    private SpellLabelReadMapper spellLabelReadMapper;

    @Resource
    private SpellLabelWriteMapper spellLabelWriteMapper;

    /**
     * 新增拼团活动标签表
     *
     * @param spellLabel
     * @return
     */
    public Integer saveSpellLabel(SpellLabel spellLabel) {
        int count = spellLabelWriteMapper.insert(spellLabel);
        if (count == 0) {
            throw new MallException("添加拼团活动标签表失败，请重试");
        }
        return count;
    }

    /**
     * 根据spellLabelId删除拼团活动标签表
     *
     * @param spellLabelId spellLabelId
     * @return
     */
    public Integer deleteSpellLabel(Integer spellLabelId) {
        if (StringUtils.isEmpty(spellLabelId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = spellLabelWriteMapper.deleteByPrimaryKey(spellLabelId);
        if (count == 0) {
            log.error("根据spellLabelId：" + spellLabelId + "删除拼团活动标签表失败");
            throw new MallException("删除拼团活动标签表失败,请重试");
        }
        return count;
    }

    /**
     * 根据spellLabelId更新拼团活动标签表
     *
     * @param spellLabel
     * @return
     */
    public Integer updateSpellLabel(SpellLabel spellLabel) {
        if (StringUtils.isEmpty(spellLabel.getSpellLabelId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = spellLabelWriteMapper.updateByPrimaryKeySelective(spellLabel);
        if (count == 0) {
            log.error("根据spellLabelId：" + spellLabel.getSpellLabelId() + "更新拼团活动标签表失败");
            throw new MallException("更新拼团活动标签表失败,请重试");
        }
        return count;
    }

    /**
     * 根据spellLabelId获取拼团活动标签表详情
     *
     * @param spellLabelId spellLabelId
     * @return
     */
    public SpellLabel getSpellLabelBySpellLabelId(Integer spellLabelId) {
        return spellLabelReadMapper.getByPrimaryKey(spellLabelId);
    }

    /**
     * 根据条件获取拼团活动标签表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<SpellLabel> getSpellLabelList(SpellLabelExample example, PagerInfo pager) {
        List<SpellLabel> spellLabelList;
        if (pager != null) {
            pager.setRowsCount(spellLabelReadMapper.countByExample(example));
            spellLabelList = spellLabelReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            spellLabelList = spellLabelReadMapper.listByExample(example);
        }
        return spellLabelList;
    }
}