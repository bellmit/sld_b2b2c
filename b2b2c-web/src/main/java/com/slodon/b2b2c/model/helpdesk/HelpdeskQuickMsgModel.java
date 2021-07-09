package com.slodon.b2b2c.model.helpdesk;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.helpdesk.HelpdeskQuickMsgReadMapper;
import com.slodon.b2b2c.dao.write.helpdesk.HelpdeskQuickMsgWriteMapper;
import com.slodon.b2b2c.helpdesk.example.HelpdeskQuickMsgExample;
import com.slodon.b2b2c.helpdesk.pojo.HelpdeskQuickMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 客服快捷回复消息表model
 */
@Component
@Slf4j
public class HelpdeskQuickMsgModel {
    @Resource
    private HelpdeskQuickMsgReadMapper helpdeskQuickMsgReadMapper;

    @Resource
    private HelpdeskQuickMsgWriteMapper helpdeskQuickMsgWriteMapper;

    /**
     * 新增客服快捷回复消息表
     *
     * @param helpdeskQuickMsg
     * @return
     */
    public Integer saveHelpdeskQuickMsg(HelpdeskQuickMsg helpdeskQuickMsg) {
        int count = helpdeskQuickMsgWriteMapper.insert(helpdeskQuickMsg);
        if (count == 0) {
            throw new MallException("添加客服快捷回复消息表失败，请重试");
        }
        return count;
    }

    /**
     * 根据quickMsgId删除客服快捷回复消息表
     *
     * @param quickMsgId quickMsgId
     * @return
     */
    public Integer deleteHelpdeskQuickMsg(Integer quickMsgId) {
        if (StringUtils.isEmpty(quickMsgId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = helpdeskQuickMsgWriteMapper.deleteByPrimaryKey(quickMsgId);
        if (count == 0) {
            log.error("根据quickMsgId：" + quickMsgId + "删除客服快捷回复消息表失败");
            throw new MallException("删除客服快捷回复消息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据quickMsgId更新客服快捷回复消息表
     *
     * @param helpdeskQuickMsg
     * @return
     */
    public Integer updateHelpdeskQuickMsg(HelpdeskQuickMsg helpdeskQuickMsg) {
        if (StringUtils.isEmpty(helpdeskQuickMsg.getQuickMsgId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = helpdeskQuickMsgWriteMapper.updateByPrimaryKeySelective(helpdeskQuickMsg);
        if (count == 0) {
            log.error("根据quickMsgId：" + helpdeskQuickMsg.getQuickMsgId() + "更新客服快捷回复消息表失败");
            throw new MallException("更新客服快捷回复消息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据quickMsgId获取客服快捷回复消息表详情
     *
     * @param quickMsgId quickMsgId
     * @return
     */
    public HelpdeskQuickMsg getHelpdeskQuickMsgByQuickMsgId(Integer quickMsgId) {
        return helpdeskQuickMsgReadMapper.getByPrimaryKey(quickMsgId);
    }

    /**
     * 根据条件获取客服快捷回复消息表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<HelpdeskQuickMsg> getHelpdeskQuickMsgList(HelpdeskQuickMsgExample example, PagerInfo pager) {
        List<HelpdeskQuickMsg> helpdeskQuickMsgList;
        if (pager != null) {
            pager.setRowsCount(helpdeskQuickMsgReadMapper.countByExample(example));
            helpdeskQuickMsgList = helpdeskQuickMsgReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            helpdeskQuickMsgList = helpdeskQuickMsgReadMapper.listByExample(example);
        }
        return helpdeskQuickMsgList;
    }
}