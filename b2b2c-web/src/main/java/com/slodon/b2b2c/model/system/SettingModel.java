package com.slodon.b2b2c.model.system;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.system.SettingReadMapper;
import com.slodon.b2b2c.dao.write.system.SettingWriteMapper;
import com.slodon.b2b2c.system.example.SettingExample;
import com.slodon.b2b2c.system.pojo.Setting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class SettingModel {
    
    @Resource
    private SettingReadMapper settingReadMapper;
    @Resource
    private SettingWriteMapper settingWriteMapper;

    /**
     * 新增系统设置表
     *
     * @param setting
     * @return
     */
    public Integer saveSetting(Setting setting) {
        int count = settingWriteMapper.insert(setting);
        if (count == 0) {
            throw new MallException("添加系统设置表失败，请重试");
        }
        return count;
    }

    /**
     * 根据name删除系统设置表
     *
     * @param name name
     * @return
     */
    public Integer deleteSetting(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = settingWriteMapper.deleteByPrimaryKey(name);
        if (count == 0) {
            log.error("根据name：" + name + "删除系统设置表失败");
            throw new MallException("删除系统设置表失败,请重试");
        }
        return count;
    }

    /**
     * 根据name更新系统设置表
     *
     * @param setting
     * @return
     */
    public Integer updateSetting(Setting setting) {
        if (StringUtils.isEmpty(setting.getName())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = settingWriteMapper.updateByPrimaryKeySelective(setting);
        if (count == 0) {
            log.error("根据name：" + setting.getName() + "更新系统设置表失败");
            throw new MallException("更新系统设置表失败,请重试");
        }
        return count;
    }

    /**
     * 根据name获取系统设置表详情
     *
     * @param name name
     * @return
     */
    public Setting getSettingByName(String name) {
        return settingReadMapper.getByPrimaryKey(name);
    }

    /**
     * 根据条件获取系统设置表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<Setting> getSettingList(SettingExample example, PagerInfo pager) {
        List<Setting> settingList;
        if (pager != null) {
            pager.setRowsCount(settingReadMapper.countByExample(example));
            settingList = settingReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            settingList = settingReadMapper.listByExample(example);
        }
        return settingList;
    }
}