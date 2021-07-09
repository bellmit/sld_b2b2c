package com.slodon.b2b2c.model.business;

import com.slodon.b2b2c.business.example.ComplainExample;
import com.slodon.b2b2c.business.example.ComplainTalkExample;
import com.slodon.b2b2c.business.pojo.Complain;
import com.slodon.b2b2c.business.pojo.ComplainTalk;
import com.slodon.b2b2c.core.constant.ComplainConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.business.ComplainTalkReadMapper;
import com.slodon.b2b2c.dao.write.business.ComplainTalkWriteMapper;
import com.slodon.b2b2c.dao.write.business.ComplainWriteMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class ComplainTalkModel {

    @Resource
    private ComplainTalkReadMapper complainTalkReadMapper;

    @Resource
    private ComplainTalkWriteMapper complainTalkWriteMapper;
    @Resource
    private ComplainWriteMapper complainWriteMapper;

    /**
     * 新增投诉对话表
     *
     * @param complainTalk
     * @return
     */
    public Integer saveComplainTalk(ComplainTalk complainTalk) {
        int count = complainTalkWriteMapper.insert(complainTalk);
        if (count == 0) {
            throw new MallException("添加投诉对话表失败，请重试");
        }
        return count;
    }

    /**
     * 根据complainTalkId删除投诉对话表
     *
     * @param complainTalkId complainTalkId
     * @return
     */
    public Integer deleteComplainTalk(Integer complainTalkId) {
        if (StringUtils.isEmpty(complainTalkId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = complainTalkWriteMapper.deleteByPrimaryKey(complainTalkId);
        if (count == 0) {
            log.error("根据complainTalkId：" + complainTalkId + "删除投诉对话表失败");
            throw new MallException("删除投诉对话表失败,请重试");
        }
        return count;
    }

    /**
     * 根据complainTalkId更新投诉对话表
     *
     * @param complainTalk
     * @return
     */
    public Integer updateComplainTalk(ComplainTalk complainTalk) {
        if (StringUtils.isEmpty(complainTalk.getComplainTalkId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = complainTalkWriteMapper.updateByPrimaryKeySelective(complainTalk);
        if (count == 0) {
            log.error("根据complainTalkId：" + complainTalk.getComplainTalkId() + "更新投诉对话表失败");
            throw new MallException("更新投诉对话表失败,请重试");
        }
        return count;
    }

    /**
     * 根据complainTalkId获取投诉对话表详情
     *
     * @param complainTalkId complainTalkId
     * @return
     */
    public ComplainTalk getComplainTalkByComplainTalkId(Integer complainTalkId) {
        return complainTalkReadMapper.getByPrimaryKey(complainTalkId);
    }

    /**
     * 根据条件获取投诉对话表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<ComplainTalk> getComplainTalkList(ComplainTalkExample example, PagerInfo pager) {
        List<ComplainTalk> complainTalkList;
        if (pager != null) {
            pager.setRowsCount(complainTalkReadMapper.countByExample(example));
            complainTalkList = complainTalkReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            complainTalkList = complainTalkReadMapper.listByExample(example);
        }
        return complainTalkList;
    }

    /**
     * 新增投诉对话表
     *
     * @param
     * @return
     */
    @Transactional
    public Integer saveComplainTalk(Integer complainId, String talkContent, Long talkUserId, String talkUserName, Integer talkUserType) {
        //新增对话
        ComplainTalk insertOne = new ComplainTalk();
        insertOne.setComplainId(complainId);
        insertOne.setTalkUserId(talkUserId);
        insertOne.setTalkUserName(talkUserName);
        insertOne.setTalkUserType(talkUserType);
        insertOne.setTalkContent(talkContent);
        insertOne.setTalkTime(new Date());
        int count = complainTalkWriteMapper.insert(insertOne);
        if (count == 0) {
            throw new MallException("添加投诉对话表失败，请重试");
        }
        //修改投诉状态
        Complain updateOne = new Complain();
        updateOne.setComplainState(ComplainConst.COMPLAIN_STATE_3);

        ComplainExample example = new ComplainExample();
        example.setComplainId(complainId);
        complainWriteMapper.updateByExampleSelective(updateOne, example);
        return count;
    }
}