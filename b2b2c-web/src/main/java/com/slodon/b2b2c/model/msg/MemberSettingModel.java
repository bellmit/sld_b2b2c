package com.slodon.b2b2c.model.msg;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.msg.MemberSettingReadMapper;
import com.slodon.b2b2c.dao.write.msg.MemberSettingWriteMapper;
import com.slodon.b2b2c.msg.example.MemberSettingExample;
import com.slodon.b2b2c.msg.pojo.MemberSetting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class MemberSettingModel {

    @Resource
    private MemberSettingReadMapper memberSettingReadMapper;
    @Resource
    private MemberSettingWriteMapper memberSettingWriteMapper;

    /**
     * 新增用户消息接收设置表
     *
     * @param memberSetting
     * @return
     */
    public Integer saveMemberSetting(MemberSetting memberSetting) {
        int count = memberSettingWriteMapper.insert(memberSetting);
        if (count == 0) {
            throw new MallException("添加用户消息接收设置表失败，请重试");
        }
        return count;
    }

    /**
     * 根据tplCode删除用户消息接收设置表
     *
     * @param tplCode tplCode
     * @return
     */
    public Integer deleteMemberSetting(String tplCode) {
        if (StringUtils.isEmpty(tplCode)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = memberSettingWriteMapper.deleteByPrimaryKey(tplCode);
        if (count == 0) {
            log.error("根据tplCode：" + tplCode + "删除用户消息接收设置表失败");
            throw new MallException("删除用户消息接收设置表失败,请重试");
        }
        return count;
    }

    /**
     * 根据tplCode更新用户消息接收设置表
     *
     * @param memberSetting
     * @return
     */
    public Integer updateMemberSetting(MemberSetting memberSetting) {
        if (StringUtils.isEmpty(memberSetting.getTplCode())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = 0;
        MemberSettingExample example = new MemberSettingExample();
        example.setTplCode(memberSetting.getTplCode());
        example.setMemberId(memberSetting.getMemberId());
        List<MemberSetting> settingList = memberSettingReadMapper.listByExample(example);
        if (CollectionUtils.isEmpty(settingList)) {
            count = memberSettingWriteMapper.insert(memberSetting);
        } else {
            MemberSetting setting = new MemberSetting();
            setting.setIsReceive(memberSetting.getIsReceive());
            count = memberSettingWriteMapper.updateByExampleSelective(setting, example);
        }
        if (count == 0) {
            log.error("根据tplCode：" + memberSetting.getTplCode() + "更新用户消息接收设置失败");
            throw new MallException("更新用户消息接收设置失败,请重试");
        }
        return count;
    }

    /**
     * 根据tplCode获取用户消息接收设置表详情
     *
     * @param tplCode tplCode
     * @return
     */
    public MemberSetting getMemberSettingByTplCode(Integer tplCode) {
        return memberSettingReadMapper.getByPrimaryKey(tplCode);
    }

    /**
     * 根据条件获取用户消息接收设置表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<MemberSetting> getMemberSettingList(MemberSettingExample example, PagerInfo pager) {
        List<MemberSetting> memberSettingList;
        if (pager != null) {
            pager.setRowsCount(memberSettingReadMapper.countByExample(example));
            memberSettingList = memberSettingReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            memberSettingList = memberSettingReadMapper.listByExample(example);
        }
        return memberSettingList;
    }
}