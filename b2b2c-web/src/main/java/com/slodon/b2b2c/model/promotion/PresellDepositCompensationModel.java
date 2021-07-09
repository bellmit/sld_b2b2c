package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.promotion.PresellDepositCompensationReadMapper;
import com.slodon.b2b2c.dao.write.promotion.PresellDepositCompensationWriteMapper;
import com.slodon.b2b2c.promotion.example.PresellDepositCompensationExample;
import com.slodon.b2b2c.promotion.pojo.PresellDepositCompensation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 预售定金赔偿表model
 */
@Component
@Slf4j
public class PresellDepositCompensationModel {

    @Resource
    private PresellDepositCompensationReadMapper presellDepositCompensationReadMapper;
    @Resource
    private PresellDepositCompensationWriteMapper presellDepositCompensationWriteMapper;

    /**
     * 新增预售定金赔偿表
     *
     * @param presellDepositCompensation
     * @return
     */
    public Integer savePresellDepositCompensation(PresellDepositCompensation presellDepositCompensation) {
        int count = presellDepositCompensationWriteMapper.insert(presellDepositCompensation);
        if (count == 0) {
            throw new MallException("添加预售定金赔偿表失败，请重试");
        }
        return count;
    }

    /**
     * 根据compensationId删除预售定金赔偿表
     *
     * @param compensationId compensationId
     * @return
     */
    public Integer deletePresellDepositCompensation(Integer compensationId) {
        if (StringUtils.isEmpty(compensationId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = presellDepositCompensationWriteMapper.deleteByPrimaryKey(compensationId);
        if (count == 0) {
            log.error("根据compensationId：" + compensationId + "删除预售定金赔偿表失败");
            throw new MallException("删除预售定金赔偿表失败,请重试");
        }
        return count;
    }

    /**
     * 根据compensationId更新预售定金赔偿表
     *
     * @param presellDepositCompensation
     * @return
     */
    public Integer updatePresellDepositCompensation(PresellDepositCompensation presellDepositCompensation) {
        if (StringUtils.isEmpty(presellDepositCompensation.getCompensationId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = presellDepositCompensationWriteMapper.updateByPrimaryKeySelective(presellDepositCompensation);
        if (count == 0) {
            log.error("根据compensationId：" + presellDepositCompensation.getCompensationId() + "更新预售定金赔偿表失败");
            throw new MallException("更新预售定金赔偿表失败,请重试");
        }
        return count;
    }

    /**
     * 根据compensationId获取预售定金赔偿表详情
     *
     * @param compensationId compensationId
     * @return
     */
    public PresellDepositCompensation getPresellDepositCompensationByCompensationId(Integer compensationId) {
        return presellDepositCompensationReadMapper.getByPrimaryKey(compensationId);
    }

    /**
     * 根据条件获取预售定金赔偿表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<PresellDepositCompensation> getPresellDepositCompensationList(PresellDepositCompensationExample example, PagerInfo pager) {
        List<PresellDepositCompensation> presellDepositCompensationList;
        if (pager != null) {
            pager.setRowsCount(presellDepositCompensationReadMapper.countByExample(example));
            presellDepositCompensationList = presellDepositCompensationReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            presellDepositCompensationList = presellDepositCompensationReadMapper.listByExample(example);
        }
        return presellDepositCompensationList;
    }
}