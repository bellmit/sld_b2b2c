package com.slodon.b2b2c.model.system;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.system.TplMobileReadMapper;
import com.slodon.b2b2c.dao.write.system.TplMobileWriteMapper;
import com.slodon.b2b2c.system.example.TplMobileExample;
import com.slodon.b2b2c.system.pojo.TplMobile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class TplMobileModel {
    @Resource
    private TplMobileReadMapper tplMobileReadMapper;

    @Resource
    private TplMobileWriteMapper tplMobileWriteMapper;

    /**
     * 新增mobile装修基础组件
     *
     * @param tplMobile
     * @return
     */
    public Integer saveTplMobile(TplMobile tplMobile) {
        int count = tplMobileWriteMapper.insert(tplMobile);
        if (count == 0) {
            throw new MallException("添加mobile装修基础组件失败，请重试");
        }
        return count;
    }

    /**
     * 根据tplId删除mobile装修基础组件
     *
     * @param tplId tplId
     * @return
     */
    public Integer deleteTplMobile(Integer tplId) {
        if (StringUtils.isEmpty(tplId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = tplMobileWriteMapper.deleteByPrimaryKey(tplId);
        if (count == 0) {
            log.error("根据tplId：" + tplId + "删除mobile装修基础组件失败");
            throw new MallException("删除mobile装修基础组件失败,请重试");
        }
        return count;
    }

    /**
     * 根据tplId更新mobile装修基础组件
     *
     * @param tplMobile
     * @return
     */
    public Integer updateTplMobile(TplMobile tplMobile) {
        if (StringUtils.isEmpty(tplMobile.getTplId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = tplMobileWriteMapper.updateByPrimaryKeySelective(tplMobile);
        if (count == 0) {
            log.error("根据tplId：" + tplMobile.getTplId() + "更新mobile装修基础组件失败");
            throw new MallException("更新mobile装修基础组件失败,请重试");
        }
        return count;
    }

    /**
     * 根据tplId获取mobile装修基础组件详情
     *
     * @param tplId tplId
     * @return
     */
    public TplMobile getTplMobileByTplId(Integer tplId) {
        return tplMobileReadMapper.getByPrimaryKey(tplId);
    }

    /**
     * 根据条件获取mobile装修基础组件列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<TplMobile> getTplMobileList(TplMobileExample example, PagerInfo pager) {
        List<TplMobile> tplMobileList;
        if (pager != null) {
            pager.setRowsCount(tplMobileReadMapper.countByExample(example));
            tplMobileList = tplMobileReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            tplMobileList = tplMobileReadMapper.listByExample(example);
        }
        return tplMobileList;
    }
}