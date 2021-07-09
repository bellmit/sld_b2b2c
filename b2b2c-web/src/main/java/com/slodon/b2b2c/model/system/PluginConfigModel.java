package com.slodon.b2b2c.model.system;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.system.PluginConfigReadMapper;
import com.slodon.b2b2c.dao.write.system.PluginConfigWriteMapper;
import com.slodon.b2b2c.system.example.PluginConfigExample;
import com.slodon.b2b2c.system.pojo.PluginConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class PluginConfigModel {
    @Resource
    private PluginConfigReadMapper pluginConfigReadMapper;

    @Resource
    private PluginConfigWriteMapper pluginConfigWriteMapper;

    /**
     * 新增插件配置信息表
     *
     * @param pluginConfig
     * @return
     */
    public Integer savePluginConfig(PluginConfig pluginConfig) {
        int count = pluginConfigWriteMapper.insert(pluginConfig);
        if (count == 0) {
            throw new MallException("添加插件配置信息表失败，请重试");
        }
        return count;
    }

    /**
     * 根据pId删除插件配置信息表
     *
     * @param pId pId
     * @return
     */
    public Integer deletePluginConfig(Integer pId) {
        if (StringUtils.isEmpty(pId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = pluginConfigWriteMapper.deleteByPrimaryKey(pId);
        if (count == 0) {
            log.error("根据pId：" + pId + "删除插件配置信息表失败");
            throw new MallException("删除插件配置信息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据pId更新插件配置信息表
     *
     * @param pluginConfig
     * @return
     */
    public Integer updatePluginConfig(PluginConfig pluginConfig) {
        if (StringUtils.isEmpty(pluginConfig.getPId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = pluginConfigWriteMapper.updateByPrimaryKeySelective(pluginConfig);
        if (count == 0) {
            log.error("根据pId：" + pluginConfig.getPId() + "更新插件配置信息表失败");
            throw new MallException("更新插件配置信息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据pId获取插件配置信息表详情
     *
     * @param pId pId
     * @return
     */
    public PluginConfig getPluginConfigByPId(Integer pId) {
        return pluginConfigReadMapper.getByPrimaryKey(pId);
    }

    /**
     * 根据条件获取插件配置信息表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<PluginConfig> getPluginConfigList(PluginConfigExample example, PagerInfo pager) {
        List<PluginConfig> pluginConfigList;
        if (pager != null) {
            pager.setRowsCount(pluginConfigReadMapper.countByExample(example));
            pluginConfigList = pluginConfigReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            pluginConfigList = pluginConfigReadMapper.listByExample(example);
        }
        return pluginConfigList;
    }
}