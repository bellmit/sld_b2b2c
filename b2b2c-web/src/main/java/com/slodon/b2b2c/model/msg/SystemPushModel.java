package com.slodon.b2b2c.model.msg;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.msg.SystemPushReadMapper;
import com.slodon.b2b2c.dao.write.msg.SystemPushWriteMapper;
import com.slodon.b2b2c.msg.example.SystemPushExample;
import com.slodon.b2b2c.msg.pojo.SystemPush;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class SystemPushModel {
    @Resource
    private SystemPushReadMapper systemPushReadMapper;

    @Resource
    private SystemPushWriteMapper systemPushWriteMapper;

    /**
     * 新增系统推送消息表
     *
     * @param systemPush
     * @return
     */
    public Integer saveSystemPush(SystemPush systemPush) {
        int count = systemPushWriteMapper.insert(systemPush);
        if (count == 0) {
            throw new MallException("添加系统推送消息表失败，请重试");
        }
        return count;
    }

    /**
     * 根据pushId删除系统推送消息表
     *
     * @param pushId pushId
     * @return
     */
    public Integer deleteSystemPush(Integer pushId) {
        if (StringUtils.isEmpty(pushId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = systemPushWriteMapper.deleteByPrimaryKey(pushId);
        if (count == 0) {
            log.error("根据pushId：" + pushId + "删除系统推送消息表失败");
            throw new MallException("删除系统推送消息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据pushId更新系统推送消息表
     *
     * @param systemPush
     * @return
     */
    public Integer updateSystemPush(SystemPush systemPush) {
        if (StringUtils.isEmpty(systemPush.getPushId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = systemPushWriteMapper.updateByPrimaryKeySelective(systemPush);
        if (count == 0) {
            log.error("根据pushId：" + systemPush.getPushId() + "更新系统推送消息表失败");
            throw new MallException("更新系统推送消息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据pushId获取系统推送消息表详情
     *
     * @param pushId pushId
     * @return
     */
    public SystemPush getSystemPushByPushId(Integer pushId) {
        return systemPushReadMapper.getByPrimaryKey(pushId);
    }

    /**
     * 根据条件获取系统推送消息表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<SystemPush> getSystemPushList(SystemPushExample example, PagerInfo pager) {
        List<SystemPush> systemPushList;
        if (pager != null) {
            pager.setRowsCount(systemPushReadMapper.countByExample(example));
            systemPushList = systemPushReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            systemPushList = systemPushReadMapper.listByExample(example);
        }
        return systemPushList;
    }
}