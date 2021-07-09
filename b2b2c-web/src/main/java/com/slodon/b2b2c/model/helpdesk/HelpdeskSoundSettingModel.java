package com.slodon.b2b2c.model.helpdesk;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.helpdesk.HelpdeskSoundSettingReadMapper;
import com.slodon.b2b2c.dao.write.helpdesk.HelpdeskSoundSettingWriteMapper;
import com.slodon.b2b2c.helpdesk.example.HelpdeskSoundSettingExample;
import com.slodon.b2b2c.helpdesk.pojo.HelpdeskSoundSetting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 客服新消息声音设置表model
 */
@Component
@Slf4j
public class HelpdeskSoundSettingModel {

    @Resource
    private HelpdeskSoundSettingReadMapper helpdeskSoundSettingReadMapper;
    @Resource
    private HelpdeskSoundSettingWriteMapper helpdeskSoundSettingWriteMapper;

    /**
     * 新增客服新消息声音设置表
     *
     * @param helpdeskSoundSetting
     * @return
     */
    public Integer saveHelpdeskSoundSetting(HelpdeskSoundSetting helpdeskSoundSetting) {
        int count = helpdeskSoundSettingWriteMapper.insert(helpdeskSoundSetting);
        if (count == 0) {
            throw new MallException("添加客服新消息声音设置表失败，请重试");
        }
        return count;
    }

    /**
     * 根据settingId删除客服新消息声音设置表
     *
     * @param settingId settingId
     * @return
     */
    public Integer deleteHelpdeskSoundSetting(Integer settingId) {
        if (StringUtils.isEmpty(settingId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = helpdeskSoundSettingWriteMapper.deleteByPrimaryKey(settingId);
        if (count == 0) {
            log.error("根据settingId：" + settingId + "删除客服新消息声音设置表失败");
            throw new MallException("删除客服新消息声音设置表失败,请重试");
        }
        return count;
    }

    /**
     * 根据settingId更新客服新消息声音设置表
     *
     * @param helpdeskSoundSetting
     * @return
     */
    public Integer updateHelpdeskSoundSetting(HelpdeskSoundSetting helpdeskSoundSetting) {
        int count = 0;
        HelpdeskSoundSettingExample example = new HelpdeskSoundSettingExample();
        example.setStoreId(helpdeskSoundSetting.getStoreId());
        example.setVendorId(helpdeskSoundSetting.getVendorId());
        List<HelpdeskSoundSetting> settingList = helpdeskSoundSettingReadMapper.listByExample(example);
        if (CollectionUtils.isEmpty(settingList)) {
            count = helpdeskSoundSettingWriteMapper.insert(helpdeskSoundSetting);
        } else {
            HelpdeskSoundSetting setting = new HelpdeskSoundSetting();
            setting.setIsOpen(helpdeskSoundSetting.getIsOpen());
            count = helpdeskSoundSettingWriteMapper.updateByExampleSelective(setting, example);
        }
        if (count == 0) {
            log.error("更新客服：" + helpdeskSoundSetting.getVendorId() + "新消息声音设置失败");
            throw new MallException("更新客服新消息声音设置失败,请重试");
        }
        return count;
    }

    /**
     * 根据settingId获取客服新消息声音设置表详情
     *
     * @param settingId settingId
     * @return
     */
    public HelpdeskSoundSetting getHelpdeskSoundSettingBySettingId(Integer settingId) {
        return helpdeskSoundSettingReadMapper.getByPrimaryKey(settingId);
    }

    /**
     * 根据条件获取客服新消息声音设置表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<HelpdeskSoundSetting> getHelpdeskSoundSettingList(HelpdeskSoundSettingExample example, PagerInfo pager) {
        List<HelpdeskSoundSetting> helpdeskSoundSettingList;
        if (pager != null) {
            pager.setRowsCount(helpdeskSoundSettingReadMapper.countByExample(example));
            helpdeskSoundSettingList = helpdeskSoundSettingReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            helpdeskSoundSettingList = helpdeskSoundSettingReadMapper.listByExample(example);
        }
        return helpdeskSoundSettingList;
    }
}