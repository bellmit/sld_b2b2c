package com.slodon.b2b2c.model.helpdesk;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.helpdesk.HelpdeskCommonProblemMsgReadMapper;
import com.slodon.b2b2c.dao.write.helpdesk.HelpdeskCommonProblemMsgWriteMapper;
import com.slodon.b2b2c.helpdesk.example.HelpdeskCommonProblemMsgExample;
import com.slodon.b2b2c.helpdesk.pojo.HelpdeskCommonProblemMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 客服常见问题表model
 */
@Component
@Slf4j
public class HelpdeskCommonProblemMsgModel {
    @Resource
    private HelpdeskCommonProblemMsgReadMapper helpdeskCommonProblemMsgReadMapper;

    @Resource
    private HelpdeskCommonProblemMsgWriteMapper helpdeskCommonProblemMsgWriteMapper;

    /**
     * 新增客服常见问题表
     *
     * @param helpdeskCommonProblemMsg
     * @return
     */
    public Integer saveHelpdeskCommonProblemMsg(HelpdeskCommonProblemMsg helpdeskCommonProblemMsg) {
        int count = helpdeskCommonProblemMsgWriteMapper.insert(helpdeskCommonProblemMsg);
        if (count == 0) {
            throw new MallException("添加客服常见问题表失败，请重试");
        }
        return count;
    }

    /**
     * 根据problemMsgId删除客服常见问题表
     *
     * @param problemMsgId problemMsgId
     * @return
     */
    public Integer deleteHelpdeskCommonProblemMsg(Integer problemMsgId) {
        if (StringUtils.isEmpty(problemMsgId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = helpdeskCommonProblemMsgWriteMapper.deleteByPrimaryKey(problemMsgId);
        if (count == 0) {
            log.error("根据problemMsgId：" + problemMsgId + "删除客服常见问题表失败");
            throw new MallException("删除客服常见问题表失败,请重试");
        }
        return count;
    }

    /**
     * 根据problemMsgId更新客服常见问题表
     *
     * @param helpdeskCommonProblemMsg
     * @return
     */
    public Integer updateHelpdeskCommonProblemMsg(HelpdeskCommonProblemMsg helpdeskCommonProblemMsg) {
        if (StringUtils.isEmpty(helpdeskCommonProblemMsg.getProblemMsgId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = helpdeskCommonProblemMsgWriteMapper.updateByPrimaryKeySelective(helpdeskCommonProblemMsg);
        if (count == 0) {
            log.error("根据problemMsgId：" + helpdeskCommonProblemMsg.getProblemMsgId() + "更新客服常见问题表失败");
            throw new MallException("更新客服常见问题表失败,请重试");
        }
        return count;
    }

    /**
     * 根据problemMsgId获取客服常见问题表详情
     *
     * @param problemMsgId problemMsgId
     * @return
     */
    public HelpdeskCommonProblemMsg getHelpdeskCommonProblemMsgByProblemMsgId(Integer problemMsgId) {
        return helpdeskCommonProblemMsgReadMapper.getByPrimaryKey(problemMsgId);
    }

    /**
     * 根据条件获取客服常见问题表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<HelpdeskCommonProblemMsg> getHelpdeskCommonProblemMsgList(HelpdeskCommonProblemMsgExample example, PagerInfo pager) {
        List<HelpdeskCommonProblemMsg> helpdeskCommonProblemMsgList;
        if (pager != null) {
            pager.setRowsCount(helpdeskCommonProblemMsgReadMapper.countByExample(example));
            helpdeskCommonProblemMsgList = helpdeskCommonProblemMsgReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            helpdeskCommonProblemMsgList = helpdeskCommonProblemMsgReadMapper.listByExample(example);
        }
        return helpdeskCommonProblemMsgList;
    }
}