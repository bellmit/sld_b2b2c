package com.slodon.b2b2c.model.system;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.system.TplPcMallDecoInnerReadMapper;
import com.slodon.b2b2c.dao.write.system.TplPcMallDecoInnerWriteMapper;
import com.slodon.b2b2c.system.example.TplPcMallDecoInnerExample;
import com.slodon.b2b2c.system.pojo.TplPcMallDecoInner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class TplPcMallDecoInnerModel {
    @Resource
    private TplPcMallDecoInnerReadMapper tplPcMallDecoInnerReadMapper;

    @Resource
    private TplPcMallDecoInnerWriteMapper tplPcMallDecoInnerWriteMapper;

    /**
     * 新增平台首页内置模版
     *
     * @param tplPcMallDecoInner
     * @return
     */
    public Integer saveTplPcMallDecoInner(TplPcMallDecoInner tplPcMallDecoInner) {
        int count = tplPcMallDecoInnerWriteMapper.insert(tplPcMallDecoInner);
        if (count == 0) {
            throw new MallException("添加平台首页内置模版失败，请重试");
        }
        return count;
    }

    /**
     * 根据decoId删除平台首页内置模版
     *
     * @param decoId decoId
     * @return
     */
    public Integer deleteTplPcMallDecoInner(Integer decoId) {
        if (StringUtils.isEmpty(decoId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = tplPcMallDecoInnerWriteMapper.deleteByPrimaryKey(decoId);
        if (count == 0) {
            log.error("根据decoId：" + decoId + "删除平台首页内置模版失败");
            throw new MallException("删除平台首页内置模版失败,请重试");
        }
        return count;
    }

    /**
     * 根据decoId更新平台首页内置模版
     *
     * @param tplPcMallDecoInner
     * @return
     */
    public Integer updateTplPcMallDecoInner(TplPcMallDecoInner tplPcMallDecoInner) {
        if (StringUtils.isEmpty(tplPcMallDecoInner.getDecoId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = tplPcMallDecoInnerWriteMapper.updateByPrimaryKeySelective(tplPcMallDecoInner);
        if (count == 0) {
            log.error("根据decoId：" + tplPcMallDecoInner.getDecoId() + "更新平台首页内置模版失败");
            throw new MallException("更新平台首页内置模版失败,请重试");
        }
        return count;
    }

    /**
     * 根据decoId获取平台首页内置模版详情
     *
     * @param decoId decoId
     * @return
     */
    public TplPcMallDecoInner getTplPcMallDecoInnerByDecoId(Integer decoId) {
        return tplPcMallDecoInnerReadMapper.getByPrimaryKey(decoId);
    }

    /**
     * 根据条件获取平台首页内置模版列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<TplPcMallDecoInner> getTplPcMallDecoInnerList(TplPcMallDecoInnerExample example, PagerInfo pager) {
        List<TplPcMallDecoInner> tplPcMallDecoInnerList;
        if (pager != null) {
            pager.setRowsCount(tplPcMallDecoInnerReadMapper.countByExample(example));
            tplPcMallDecoInnerList = tplPcMallDecoInnerReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            tplPcMallDecoInnerList = tplPcMallDecoInnerReadMapper.listByExample(example);
        }
        return tplPcMallDecoInnerList;
    }
}