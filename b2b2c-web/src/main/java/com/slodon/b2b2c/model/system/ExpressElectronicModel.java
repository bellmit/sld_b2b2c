package com.slodon.b2b2c.model.system;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.system.ExpressElectronicReadMapper;
import com.slodon.b2b2c.dao.write.system.ExpressElectronicWriteMapper;
import com.slodon.b2b2c.system.example.ExpressElectronicExample;
import com.slodon.b2b2c.system.pojo.ExpressElectronic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class ExpressElectronicModel {
    @Resource
    private ExpressElectronicReadMapper expressElectronicReadMapper;

    @Resource
    private ExpressElectronicWriteMapper expressElectronicWriteMapper;

    /**
     * 新增快递鸟平台支持快递面单的快递公司
     *
     * @param expressElectronic
     * @return
     */
    public Integer saveExpressElectronic(ExpressElectronic expressElectronic) {
        int count = expressElectronicWriteMapper.insert(expressElectronic);
        if (count == 0) {
            throw new MallException("添加快递鸟平台支持快递面单的快递公司失败，请重试");
        }
        return count;
    }

    /**
     * 根据electronicId删除快递鸟平台支持快递面单的快递公司
     *
     * @param electronicId electronicId
     * @return
     */
    public Integer deleteExpressElectronic(Integer electronicId) {
        if (StringUtils.isEmpty(electronicId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = expressElectronicWriteMapper.deleteByPrimaryKey(electronicId);
        if (count == 0) {
            log.error("根据electronicId：" + electronicId + "删除快递鸟平台支持快递面单的快递公司失败");
            throw new MallException("删除快递鸟平台支持快递面单的快递公司失败,请重试");
        }
        return count;
    }

    /**
     * 根据electronicId更新快递鸟平台支持快递面单的快递公司
     *
     * @param expressElectronic
     * @return
     */
    public Integer updateExpressElectronic(ExpressElectronic expressElectronic) {
        if (StringUtils.isEmpty(expressElectronic.getElectronicId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = expressElectronicWriteMapper.updateByPrimaryKeySelective(expressElectronic);
        if (count == 0) {
            log.error("根据electronicId：" + expressElectronic.getElectronicId() + "更新快递鸟平台支持快递面单的快递公司失败");
            throw new MallException("更新快递鸟平台支持快递面单的快递公司失败,请重试");
        }
        return count;
    }

    /**
     * 根据electronicId获取快递鸟平台支持快递面单的快递公司详情
     *
     * @param electronicId electronicId
     * @return
     */
    public ExpressElectronic getExpressElectronicByElectronicId(Integer electronicId) {
        return expressElectronicReadMapper.getByPrimaryKey(electronicId);
    }

    /**
     * 根据条件获取快递鸟平台支持快递面单的快递公司列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<ExpressElectronic> getExpressElectronicList(ExpressElectronicExample example, PagerInfo pager) {
        List<ExpressElectronic> expressElectronicList;
        if (pager != null) {
            pager.setRowsCount(expressElectronicReadMapper.countByExample(example));
            expressElectronicList = expressElectronicReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            expressElectronicList = expressElectronicReadMapper.listByExample(example);
        }
        return expressElectronicList;
    }
}