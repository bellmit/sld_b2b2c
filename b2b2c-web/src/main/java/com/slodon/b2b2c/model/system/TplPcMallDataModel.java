package com.slodon.b2b2c.model.system;

import com.slodon.b2b2c.core.constant.TplPcConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.dao.read.system.TplPcMallDataReadMapper;
import com.slodon.b2b2c.dao.read.system.TplPcReadMapper;
import com.slodon.b2b2c.dao.write.system.TplPcMallDataWriteMapper;
import com.slodon.b2b2c.system.example.TplPcMallDataExample;
import com.slodon.b2b2c.system.pojo.TplPc;
import com.slodon.b2b2c.system.pojo.TplPcMallData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class TplPcMallDataModel {

    @Resource
    private TplPcMallDataReadMapper tplPcMallDataReadMapper;
    @Resource
    private TplPcMallDataWriteMapper tplPcMallDataWriteMapper;
    @Resource
    private TplPcReadMapper tplPcReadMapper;

    /**
     * 新增PC商城装修模板实例化数据表
     *
     * @param tplPcMallData
     * @return
     */
    public Integer saveTplPcMallData(TplPcMallData tplPcMallData) {
        TplPc tplPc = tplPcReadMapper.getByPrimaryKey(tplPcMallData.getTplPcId());
        if (tplPc == null) {
            throw new MallException("获取装修模板信息为空");
        }
        tplPcMallData.setTplPcName(tplPc.getName());
        tplPcMallData.setTplPcType(tplPc.getType());
        tplPcMallData.setTplPcTypeName(tplPc.getTypeName());
        AssertUtil.isTrue(tplPcMallData.getName().length() > 50, "复制后名称过长，请修改");
        int count = tplPcMallDataWriteMapper.insert(tplPcMallData);
        if (count == 0) {
            throw new MallException("添加PC商城装修模板实例化数据表失败，请重试");
        }
        return count;
    }

    /**
     * 根据dataId删除PC商城装修模板实例化数据表
     *
     * @param dataId dataId
     * @return
     */
    public Integer deleteTplPcMallData(Integer dataId) {
        if (StringUtils.isEmpty(dataId)) {
            throw new MallException("请选择要删除的数据");
        }
        TplPcMallData tplPcMallData = tplPcMallDataReadMapper.getByPrimaryKey(dataId);
        if (null == tplPcMallData) {
            throw new MallException("无此模板实例化数据,无法删除");
        }
        if (TplPcConst.IS_ENABLE_YES == tplPcMallData.getIsEnable()) {
            throw new MallException("此模板实例化数据正在使用中,无法删除");
        }
        int count = tplPcMallDataWriteMapper.deleteByPrimaryKey(dataId);
        if (count == 0) {
            log.error("根据dataId：" + dataId + "删除PC商城装修模板实例化数据表失败");
            throw new MallException("删除PC商城装修模板实例化数据表失败,请重试");
        }
        return count;
    }

    /**
     * 根据dataId更新PC商城装修模板实例化数据表
     *
     * @param tplPcMallData
     * @return
     */
    public Integer updateTplPcMallData(TplPcMallData tplPcMallData) {
        if (StringUtils.isEmpty(tplPcMallData.getDataId())) {
            throw new MallException("请选择要修改的数据");
        }
        TplPcMallData tplPcMallDataDb = tplPcMallDataReadMapper.getByPrimaryKey(tplPcMallData.getDataId());
        if (null == tplPcMallDataDb) {
            throw new MallException("该模板实例化数据不存在，请新选择");
        }
        int count = tplPcMallDataWriteMapper.updateByPrimaryKeySelective(tplPcMallData);
        if (count == 0) {
            log.error("根据dataId：" + tplPcMallData.getDataId() + "更新PC商城装修模板实例化数据表失败");
            throw new MallException("更新PC商城装修模板实例化数据表失败,请重试");
        }
        return count;
    }

    /**
     * 根据dataId获取PC商城装修模板实例化数据表详情
     *
     * @param dataId dataId
     * @return
     */
    public TplPcMallData getTplPcMallDataByDataId(Integer dataId) {
        return tplPcMallDataReadMapper.getByPrimaryKey(dataId);
    }

    /**
     * 根据条件获取PC商城装修模板实例化数据表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<TplPcMallData> getTplPcMallDataList(TplPcMallDataExample example, PagerInfo pager) {
        List<TplPcMallData> tplPcMallDataList;
        if (pager != null) {
            pager.setRowsCount(tplPcMallDataReadMapper.countByExample(example));
            tplPcMallDataList = tplPcMallDataReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            tplPcMallDataList = tplPcMallDataReadMapper.listByExample(example);
        }
        return tplPcMallDataList;
    }
}