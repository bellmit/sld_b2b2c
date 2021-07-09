package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.promotion.LadderGroupLabelReadMapper;
import com.slodon.b2b2c.dao.write.promotion.LadderGroupLabelWriteMapper;
import com.slodon.b2b2c.promotion.example.LadderGroupLabelExample;
import com.slodon.b2b2c.promotion.pojo.LadderGroupLabel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 阶梯团标签表model
 */
@Component
@Slf4j
public class LadderGroupLabelModel {

    @Resource
    private LadderGroupLabelReadMapper ladderGroupLabelReadMapper;
    @Resource
    private LadderGroupLabelWriteMapper ladderGroupLabelWriteMapper;

    /**
     * 新增阶梯团标签表
     *
     * @param ladderGroupLabel
     * @return
     */
    public Integer saveLadderGroupLabel(LadderGroupLabel ladderGroupLabel) {
        int count = ladderGroupLabelWriteMapper.insert(ladderGroupLabel);
        if (count == 0) {
            throw new MallException("添加阶梯团标签表失败，请重试");
        }
        return count;
    }

    /**
     * 根据labelId删除阶梯团标签表
     *
     * @param labelId labelId
     * @return
     */
    public Integer deleteLadderGroupLabel(Integer labelId) {
        if (StringUtils.isEmpty(labelId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = ladderGroupLabelWriteMapper.deleteByPrimaryKey(labelId);
        if (count == 0) {
            log.error("根据labelId：" + labelId + "删除阶梯团标签表失败");
            throw new MallException("删除阶梯团标签表失败,请重试");
        }
        return count;
    }

    /**
     * 根据labelId更新阶梯团标签表
     *
     * @param ladderGroupLabel
     * @return
     */
    public Integer updateLadderGroupLabel(LadderGroupLabel ladderGroupLabel) {
        if (StringUtils.isEmpty(ladderGroupLabel.getLabelId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = ladderGroupLabelWriteMapper.updateByPrimaryKeySelective(ladderGroupLabel);
        if (count == 0) {
            log.error("根据labelId：" + ladderGroupLabel.getLabelId() + "更新阶梯团标签表失败");
            throw new MallException("更新阶梯团标签表失败,请重试");
        }
        return count;
    }

    /**
     * 根据labelId获取阶梯团标签表详情
     *
     * @param labelId labelId
     * @return
     */
    public LadderGroupLabel getLadderGroupLabelByLabelId(Integer labelId) {
        return ladderGroupLabelReadMapper.getByPrimaryKey(labelId);
    }

    /**
     * 根据条件获取阶梯团标签表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<LadderGroupLabel> getLadderGroupLabelList(LadderGroupLabelExample example, PagerInfo pager) {
        List<LadderGroupLabel> ladderGroupLabelList;
        if (pager != null) {
            pager.setRowsCount(ladderGroupLabelReadMapper.countByExample(example));
            ladderGroupLabelList = ladderGroupLabelReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            ladderGroupLabelList = ladderGroupLabelReadMapper.listByExample(example);
        }
        return ladderGroupLabelList;
    }
}