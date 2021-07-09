package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.promotion.PresellLabelReadMapper;
import com.slodon.b2b2c.dao.write.promotion.PresellLabelWriteMapper;
import com.slodon.b2b2c.promotion.example.PresellLabelExample;
import com.slodon.b2b2c.promotion.pojo.PresellLabel;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class PresellLabelModel {
    @Resource
    private PresellLabelReadMapper presellLabelReadMapper;

    @Resource
    private PresellLabelWriteMapper presellLabelWriteMapper;

    /**
     * 新增预售活动标签表
     *
     * @param presellLabel
     * @return
     */
    public Integer savePresellLabel(PresellLabel presellLabel) {
        int count = presellLabelWriteMapper.insert(presellLabel);
        if (count == 0) {
            throw new MallException("添加预售活动标签表失败，请重试");
        }
        return count;
    }

    /**
     * 根据presellLabelId删除预售活动标签表
     *
     * @param presellLabelId presellLabelId
     * @return
     */
    public Integer deletePresellLabel(Integer presellLabelId) {
        if (StringUtils.isEmpty(presellLabelId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = presellLabelWriteMapper.deleteByPrimaryKey(presellLabelId);
        if (count == 0) {
            log.error("根据presellLabelId：" + presellLabelId + "删除预售活动标签表失败");
            throw new MallException("删除预售活动标签表失败,请重试");
        }
        return count;
    }

    /**
     * 根据presellLabelId更新预售活动标签表
     *
     * @param presellLabel
     * @return
     */
    public Integer updatePresellLabel(PresellLabel presellLabel) {
        if (StringUtils.isEmpty(presellLabel.getPresellLabelId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = presellLabelWriteMapper.updateByPrimaryKeySelective(presellLabel);
        if (count == 0) {
            log.error("根据presellLabelId：" + presellLabel.getPresellLabelId() + "更新预售活动标签表失败");
            throw new MallException("更新预售活动标签表失败,请重试");
        }
        return count;
    }

    /**
     * 根据presellLabelId获取预售活动标签表详情
     *
     * @param presellLabelId presellLabelId
     * @return
     */
    public PresellLabel getPresellLabelByPresellLabelId(Integer presellLabelId) {
        return presellLabelReadMapper.getByPrimaryKey(presellLabelId);
    }

    /**
     * 根据条件获取预售活动标签表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<PresellLabel> getPresellLabelList(PresellLabelExample example, PagerInfo pager) {
        List<PresellLabel> presellLabelList;
        if (pager != null) {
            pager.setRowsCount(presellLabelReadMapper.countByExample(example));
            presellLabelList = presellLabelReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            presellLabelList = presellLabelReadMapper.listByExample(example);
        }
        return presellLabelList;
    }
}