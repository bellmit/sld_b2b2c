package com.slodon.b2b2c.model.helpdesk;

import com.slodon.b2b2c.core.constant.HelpdeskConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.dao.read.helpdesk.HelpdeskAutomaticMsgReadMapper;
import com.slodon.b2b2c.dao.write.helpdesk.HelpdeskAutomaticMsgWriteMapper;
import com.slodon.b2b2c.helpdesk.example.HelpdeskAutomaticMsgExample;
import com.slodon.b2b2c.helpdesk.pojo.HelpdeskAutomaticMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 客服自动回复消息表model
 */
@Component
@Slf4j
public class HelpdeskAutomaticMsgModel {
    @Resource
    private HelpdeskAutomaticMsgReadMapper helpdeskAutomaticMsgReadMapper;

    @Resource
    private HelpdeskAutomaticMsgWriteMapper helpdeskAutomaticMsgWriteMapper;

    /**
     * 新增客服自动回复消息表
     *
     * @param helpdeskAutomaticMsg
     * @return
     */
    public Integer saveHelpdeskAutomaticMsg(HelpdeskAutomaticMsg helpdeskAutomaticMsg) {
        int count = helpdeskAutomaticMsgWriteMapper.insert(helpdeskAutomaticMsg);
        if (count == 0) {
            throw new MallException("添加客服自动回复消息表失败，请重试");
        }
        return count;
    }

    /**
     * 根据autoMsgId删除客服自动回复消息表
     *
     * @param autoMsgId autoMsgId
     * @return
     */
    public Integer deleteHelpdeskAutomaticMsg(Integer autoMsgId) {
        if (StringUtils.isEmpty(autoMsgId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = helpdeskAutomaticMsgWriteMapper.deleteByPrimaryKey(autoMsgId);
        if (count == 0) {
            log.error("根据autoMsgId：" + autoMsgId + "删除客服自动回复消息表失败");
            throw new MallException("删除客服自动回复消息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据autoMsgId更新客服自动回复消息表
     *
     * @param helpdeskAutomaticMsg
     * @return
     */
    public Integer updateHelpdeskAutomaticMsg(HelpdeskAutomaticMsg helpdeskAutomaticMsg) {
        if (StringUtils.isEmpty(helpdeskAutomaticMsg.getAutoMsgId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = helpdeskAutomaticMsgWriteMapper.updateByPrimaryKeySelective(helpdeskAutomaticMsg);
        if (count == 0) {
            log.error("根据autoMsgId：" + helpdeskAutomaticMsg.getAutoMsgId() + "更新客服自动回复消息表失败");
            throw new MallException("更新客服自动回复消息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据autoMsgId获取客服自动回复消息表详情
     *
     * @param autoMsgId autoMsgId
     * @return
     */
    public HelpdeskAutomaticMsg getHelpdeskAutomaticMsgByAutoMsgId(Integer autoMsgId) {
        return helpdeskAutomaticMsgReadMapper.getByPrimaryKey(autoMsgId);
    }

    /**
     * 根据条件获取客服自动回复消息表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<HelpdeskAutomaticMsg> getHelpdeskAutomaticMsgList(HelpdeskAutomaticMsgExample example, PagerInfo pager) {
        List<HelpdeskAutomaticMsg> helpdeskAutomaticMsgList;
        if (pager != null) {
            pager.setRowsCount(helpdeskAutomaticMsgReadMapper.countByExample(example));
            helpdeskAutomaticMsgList = helpdeskAutomaticMsgReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            helpdeskAutomaticMsgList = helpdeskAutomaticMsgReadMapper.listByExample(example);
        }
        return helpdeskAutomaticMsgList;
    }

    /**
     * 新增客服自动回复消息表
     *
     * @param helpdeskAutomaticMsg
     * @return
     */
    @Transactional
    public Integer saveAutomaticMsg(HelpdeskAutomaticMsg helpdeskAutomaticMsg) {
        if (HelpdeskConst.IS_SHOW_YES == helpdeskAutomaticMsg.getIsShow()) {
            HelpdeskAutomaticMsgExample example = new HelpdeskAutomaticMsgExample();
            example.setStoreId(helpdeskAutomaticMsg.getStoreId());
            example.setIsShow(HelpdeskConst.IS_SHOW_YES);
            HelpdeskAutomaticMsg updateOne = new HelpdeskAutomaticMsg();
            updateOne.setIsShow(HelpdeskConst.IS_SHOW_NO);
            helpdeskAutomaticMsgWriteMapper.updateByExampleSelective(updateOne, example);
        }
        int count = this.saveHelpdeskAutomaticMsg(helpdeskAutomaticMsg);
        AssertUtil.notNullOrZero(count, "添加客服自动回复消息表失败，请重试");
        return count;
    }

    /**
     * 修改客服自动回复消息表
     *
     * @param helpdeskAutomaticMsg
     * @return
     */
    @Transactional
    public Integer updateAutomaticMsg(HelpdeskAutomaticMsg helpdeskAutomaticMsg) {
        if (HelpdeskConst.IS_SHOW_YES == helpdeskAutomaticMsg.getIsShow()) {
            HelpdeskAutomaticMsgExample example = new HelpdeskAutomaticMsgExample();
            example.setStoreId(helpdeskAutomaticMsg.getStoreId());
            example.setIsShow(HelpdeskConst.IS_SHOW_YES);
            HelpdeskAutomaticMsg updateOne = new HelpdeskAutomaticMsg();
            updateOne.setIsShow(HelpdeskConst.IS_SHOW_NO);
            helpdeskAutomaticMsgWriteMapper.updateByExampleSelective(updateOne, example);
        }
        int count = this.updateHelpdeskAutomaticMsg(helpdeskAutomaticMsg);

        AssertUtil.notNullOrZero(count, "更新客服自动回复消息表失败,请重试");
        return count;
    }
}