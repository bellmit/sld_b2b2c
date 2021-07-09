package com.slodon.b2b2c.model.helpdesk;

import com.slodon.b2b2c.core.constant.HelpdeskConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.helpdesk.HelpdeskContactReadMapper;
import com.slodon.b2b2c.dao.read.helpdesk.HelpdeskMsgReadMapper;
import com.slodon.b2b2c.dao.write.helpdesk.HelpdeskContactWriteMapper;
import com.slodon.b2b2c.dao.write.helpdesk.HelpdeskMsgWriteMapper;
import com.slodon.b2b2c.helpdesk.example.HelpdeskContactExample;
import com.slodon.b2b2c.helpdesk.example.HelpdeskMsgExample;
import com.slodon.b2b2c.helpdesk.pojo.HelpdeskContact;
import com.slodon.b2b2c.helpdesk.pojo.HelpdeskMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class HelpdeskMsgModel {

    @Resource
    private HelpdeskMsgReadMapper helpdeskMsgReadMapper;
    @Resource
    private HelpdeskMsgWriteMapper helpdeskMsgWriteMapper;
    @Resource
    private HelpdeskContactWriteMapper helpdeskContactWriteMapper;
    @Resource
    private HelpdeskContactReadMapper helpdeskContactReadMapper;

    /**
     * 新增客服聊天消息表
     *
     * @param helpdeskMsg
     * @return
     */
    @Transactional
    public Integer saveHelpdeskMsg(HelpdeskMsg helpdeskMsg) {
        // 保存聊天信息
        Integer count = helpdeskMsgWriteMapper.insert(helpdeskMsg);
        if (count == 0) {
            throw new MallException("聊天信息保存失败，请重试！");
        }

        HelpdeskContactExample example = new HelpdeskContactExample();
        example.setMemberId(helpdeskMsg.getMemberId());
        example.setVendorId(helpdeskMsg.getVendorId());
        List<HelpdeskContact> helpdeskContacts = helpdeskContactReadMapper.listByExample(example);
        if (CollectionUtils.isEmpty(helpdeskContacts)) {
            HelpdeskContact helpdeskContact = new HelpdeskContact();
            helpdeskContact.setMemberId(helpdeskMsg.getMemberId());
            helpdeskContact.setVendorId(helpdeskMsg.getVendorId());
            helpdeskContact.setStoreId(helpdeskMsg.getStoreId());
            helpdeskContact.setState(HelpdeskConst.ACTIVE_STATE_YES);
            helpdeskContact.setCreateTime(new Date());
            count = helpdeskContactWriteMapper.insert(helpdeskContact);
            if (count == 0) {
                throw new MallException("客服-会员关系信息保存失败，请重试！");
            }
        } else {
            HelpdeskContact helpdeskContact = helpdeskContacts.get(0);
            helpdeskContact.setContactId(helpdeskContact.getContactId());
            helpdeskContact.setState(HelpdeskConst.ACTIVE_STATE_YES);
            helpdeskContact.setUpdateTime(new Date());
            count = helpdeskContactWriteMapper.updateByPrimaryKeySelective(helpdeskContact);
            if (count == 0) {
                throw new MallException("客服-会员关系信息更新失败，请重试！");
            }
        }
        return count;
    }

    /**
     * 根据msgId删除客服聊天消息表
     *
     * @param msgId msgId
     * @return
     */
    public Integer deleteHelpdeskMsg(Integer msgId) {
        if (StringUtils.isEmpty(msgId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = helpdeskMsgWriteMapper.deleteByPrimaryKey(msgId);
        if (count == 0) {
            log.error("根据msgId：" + msgId + "删除客服聊天消息表失败");
            throw new MallException("删除客服聊天消息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据msgId更新客服聊天消息表
     *
     * @param helpdeskMsg
     * @return
     */
    public Integer updateHelpdeskMsg(HelpdeskMsg helpdeskMsg) {
        if (StringUtils.isEmpty(helpdeskMsg.getMsgId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = helpdeskMsgWriteMapper.updateByPrimaryKeySelective(helpdeskMsg);
        if (count == 0) {
            log.error("根据msgId：" + helpdeskMsg.getMsgId() + "更新客服聊天消息表失败");
            throw new MallException("更新客服聊天消息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据msgId获取客服聊天消息表详情
     *
     * @param msgId msgId
     * @return
     */
    public HelpdeskMsg getHelpdeskMsgByMsgId(Integer msgId) {
        return helpdeskMsgReadMapper.getByPrimaryKey(msgId);
    }

    /**
     * 根据条件获取客服聊天消息表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<HelpdeskMsg> getHelpdeskMsgList(HelpdeskMsgExample example, PagerInfo pager) {
        List<HelpdeskMsg> helpdeskMsgList;
        if (pager != null) {
            pager.setRowsCount(helpdeskMsgReadMapper.countByExample(example));
            helpdeskMsgList = helpdeskMsgReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            helpdeskMsgList = helpdeskMsgReadMapper.listByExample(example);
        }
        for (HelpdeskMsg helpdeskMsg : helpdeskMsgList) {
            // 店铺id
            example.setStoreId(helpdeskMsg.getStoreId());
            // 未读
            example.setMsgState(HelpdeskConst.MSG_STATE_NO);
            Integer msgCount = helpdeskMsgReadMapper.countByExample(example);
            helpdeskMsg.setReceiveMsgNumber(msgCount);
        }
        return helpdeskMsgList;
    }

    /**
     * 获取聊天列表
     *
     * @param example
     * @param pager
     * @return
     */
    public List<HelpdeskMsg> getChatList(HelpdeskMsgExample example, PagerInfo pager) {
        pager.setRowsCount(helpdeskMsgReadMapper.getChatCount(example));
        List<HelpdeskMsg> list = helpdeskMsgReadMapper.getChatList(example, pager.getStart(), pager.getPageSize());
        for (HelpdeskMsg helpdeskMsg : list) {
            // 店铺id
            example.setStoreId(helpdeskMsg.getStoreId());
            // 未读
            example.setMsgState(HelpdeskConst.MSG_STATE_NO);
            Integer msgCount = helpdeskMsgReadMapper.countByExample(example);
            helpdeskMsg.setReceiveMsgNumber(msgCount);
        }
        return list;
    }

    /**
     * 根据条件更新
     *
     * @param helpdeskMsg
     * @param example
     * @return
     */
    public Integer updateHelpdeskMsgByExample(HelpdeskMsg helpdeskMsg, HelpdeskMsgExample example) {
        return helpdeskMsgWriteMapper.updateByExampleSelective(helpdeskMsg, example);
    }
}