package com.slodon.b2b2c.model.system;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.system.TplMobileInnerReadMapper;
import com.slodon.b2b2c.dao.write.system.TplMobileInnerWriteMapper;
import com.slodon.b2b2c.system.example.TplMobileInnerExample;
import com.slodon.b2b2c.system.pojo.TplMobileInner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class TplMobileInnerModel {
    @Resource
    private TplMobileInnerReadMapper tplMobileInnerReadMapper;

    @Resource
    private TplMobileInnerWriteMapper tplMobileInnerWriteMapper;

    /**
     * 新增Mobile装修模板页（平台内置）
     *
     * @param tplMobileInner
     * @return
     */
    public Integer saveTplMobileInner(TplMobileInner tplMobileInner) {
        int count = tplMobileInnerWriteMapper.insert(tplMobileInner);
        if (count == 0) {
            throw new MallException("添加Mobile装修模板页（平台内置）失败，请重试");
        }
        return count;
    }

    /**
     * 根据tplId删除Mobile装修模板页（平台内置）
     *
     * @param tplId tplId
     * @return
     */
    public Integer deleteTplMobileInner(Integer tplId) {
        if (StringUtils.isEmpty(tplId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = tplMobileInnerWriteMapper.deleteByPrimaryKey(tplId);
        if (count == 0) {
            log.error("根据tplId：" + tplId + "删除Mobile装修模板页（平台内置）失败");
            throw new MallException("删除Mobile装修模板页（平台内置）失败,请重试");
        }
        return count;
    }

    /**
     * 根据tplId更新Mobile装修模板页（平台内置）
     *
     * @param tplMobileInner
     * @return
     */
    public Integer updateTplMobileInner(TplMobileInner tplMobileInner) {
        if (StringUtils.isEmpty(tplMobileInner.getTplId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = tplMobileInnerWriteMapper.updateByPrimaryKeySelective(tplMobileInner);
        if (count == 0) {
            log.error("根据tplId：" + tplMobileInner.getTplId() + "更新Mobile装修模板页（平台内置）失败");
            throw new MallException("更新Mobile装修模板页（平台内置）失败,请重试");
        }
        return count;
    }

    /**
     * 根据tplId获取Mobile装修模板页（平台内置）详情
     *
     * @param tplId tplId
     * @return
     */
    public TplMobileInner getTplMobileInnerByTplId(Integer tplId) {
        return tplMobileInnerReadMapper.getByPrimaryKey(tplId);
    }

    /**
     * 根据条件获取Mobile装修模板页（平台内置）列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<TplMobileInner> getTplMobileInnerList(TplMobileInnerExample example, PagerInfo pager) {
        List<TplMobileInner> tplMobileInnerList;
        if (pager != null) {
            pager.setRowsCount(tplMobileInnerReadMapper.countByExample(example));
            tplMobileInnerList = tplMobileInnerReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            tplMobileInnerList = tplMobileInnerReadMapper.listByExample(example);
        }
        return tplMobileInnerList;
    }
}