package com.slodon.b2b2c.model.system;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.system.PcFirstAdvReadMapper;
import com.slodon.b2b2c.dao.write.system.PcFirstAdvWriteMapper;
import com.slodon.b2b2c.system.example.PcFirstAdvExample;
import com.slodon.b2b2c.system.pojo.PcFirstAdv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class PcFirstAdvModel {

    @Resource
    private PcFirstAdvReadMapper pcFirstAdvReadMapper;
    @Resource
    private PcFirstAdvWriteMapper pcFirstAdvWriteMapper;

    /**
     * 新增pc首页弹层广告表
     *
     * @param pcFirstAdv
     * @return
     */
    public Integer savePcFirstAdv(PcFirstAdv pcFirstAdv) {
        int count = pcFirstAdvWriteMapper.insert(pcFirstAdv);
        if (count == 0) {
            throw new MallException("添加pc首页弹层广告表失败，请重试");
        }
        return count;
    }

    /**
     * 根据advId删除pc首页弹层广告表
     *
     * @param advId advId
     * @return
     */
    public Integer deletePcFirstAdv(Integer advId) {
        if (StringUtils.isEmpty(advId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = pcFirstAdvWriteMapper.deleteByPrimaryKey(advId);
        if (count == 0) {
            log.error("根据advId：" + advId + "删除pc首页弹层广告表失败");
            throw new MallException("删除pc首页弹层广告表失败,请重试");
        }
        return count;
    }

    /**
     * 根据advId更新pc首页弹层广告表
     *
     * @param pcFirstAdv
     * @return
     */
    public Integer updatePcFirstAdv(PcFirstAdv pcFirstAdv) {
        if (StringUtils.isEmpty(pcFirstAdv.getAdvId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = pcFirstAdvWriteMapper.updateByPrimaryKeySelective(pcFirstAdv);
        if (count == 0) {
            log.error("根据advId：" + pcFirstAdv.getAdvId() + "更新pc首页弹层广告表失败");
            throw new MallException("更新pc首页弹层广告表失败,请重试");
        }
        return count;
    }

    /**
     * 根据advId获取pc首页弹层广告表详情
     *
     * @param advId advId
     * @return
     */
    public PcFirstAdv getPcFirstAdvByAdvId(Integer advId) {
        return pcFirstAdvReadMapper.getByPrimaryKey(advId);
    }

    /**
     * 根据条件获取pc首页弹层广告表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<PcFirstAdv> getPcFirstAdvList(PcFirstAdvExample example, PagerInfo pager) {
        List<PcFirstAdv> pcFirstAdvList;
        if (pager != null) {
            pager.setRowsCount(pcFirstAdvReadMapper.countByExample(example));
            pcFirstAdvList = pcFirstAdvReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            pcFirstAdvList = pcFirstAdvReadMapper.listByExample(example);
        }
        return pcFirstAdvList;
    }
}