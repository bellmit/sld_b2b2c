package com.slodon.b2b2c.model.system;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.system.TplPcReadMapper;
import com.slodon.b2b2c.dao.write.system.TplPcWriteMapper;
import com.slodon.b2b2c.system.example.TplPcExample;
import com.slodon.b2b2c.system.pojo.TplPc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class TplPcModel {

    @Resource
    private TplPcReadMapper tplPcReadMapper;
    @Resource
    private TplPcWriteMapper tplPcWriteMapper;

    /**
     * 新增PC装修模板表
     *
     * @param tplPc
     * @return
     */
    public Integer saveTplPc(TplPc tplPc) {
        int count = tplPcWriteMapper.insert(tplPc);
        if (count == 0) {
            throw new MallException("添加PC装修模板表失败，请重试");
        }
        return count;
    }

    /**
     * 根据tplPcId删除PC装修模板表
     *
     * @param tplPcId tplPcId
     * @return
     */
    public Integer deleteTplPc(Integer tplPcId) {
        if (StringUtils.isEmpty(tplPcId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = tplPcWriteMapper.deleteByPrimaryKey(tplPcId);
        if (count == 0) {
            log.error("根据tplPcId：" + tplPcId + "删除PC装修模板表失败");
            throw new MallException("删除PC装修模板表失败,请重试");
        }
        return count;
    }

    /**
     * 根据tplPcId更新PC装修模板表
     *
     * @param tplPc
     * @return
     */
    public Integer updateTplPc(TplPc tplPc) {
        if (StringUtils.isEmpty(tplPc.getTplPcId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = tplPcWriteMapper.updateByPrimaryKeySelective(tplPc);
        if (count == 0) {
            log.error("根据tplPcId：" + tplPc.getTplPcId() + "更新PC装修模板表失败");
            throw new MallException("更新PC装修模板表失败,请重试");
        }
        return count;
    }

    /**
     * 根据tplPcId获取PC装修模板表详情
     *
     * @param tplPcId tplPcId
     * @return
     */
    public TplPc getTplPcByTplPcId(Integer tplPcId) {
        return tplPcReadMapper.getByPrimaryKey(tplPcId);
    }

    /**
     * 根据条件获取PC装修模板表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<TplPc> getTplPcList(TplPcExample example, PagerInfo pager) {
        List<TplPc> tplPcList;
        if (pager != null) {
            pager.setRowsCount(tplPcReadMapper.countByExample(example));
            tplPcList = tplPcReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            tplPcList = tplPcReadMapper.listByExample(example);
        }
        return tplPcList;
    }

    /**
     * 根据条件获取PC装修模板表列表
     *
     * @param fields  查询字段，字段用逗号分隔
     * @param example 查询条件信息
     * @return
     */
    public List<TplPc> getTplPcListFieldsByExample(String fields, TplPcExample example) {
        return tplPcReadMapper.listFieldsByExample(fields, example);
    }
}