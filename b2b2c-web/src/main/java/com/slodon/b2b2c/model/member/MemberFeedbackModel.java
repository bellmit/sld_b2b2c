package com.slodon.b2b2c.model.member;

import com.slodon.b2b2c.dao.write.member.MemberFeedbackWriteMapper;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.member.example.MemberFeedbackExample;
import com.slodon.b2b2c.member.pojo.MemberFeedback;
import com.slodon.b2b2c.dao.read.member.MemberFeedbackReadMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class MemberFeedbackModel {

    @Resource
    private MemberFeedbackReadMapper memberFeedbackReadMapper;
    @Resource
    private MemberFeedbackWriteMapper memberFeedbackWriteMapper;

    /**
     * 新增用户反馈
     *
     * @param memberFeedback
     * @return
     */
    public Integer saveMemberFeedback(MemberFeedback memberFeedback) {
        int count = memberFeedbackWriteMapper.insert(memberFeedback);
        if (count == 0) {
            throw new MallException("添加用户反馈失败，请重试");
        }
        return count;
    }

    /**
     * 根据feedbackId删除用户反馈
     *
     * @param feedbackId feedbackId
     * @return
     */
    public Integer deleteMemberFeedback(Integer feedbackId) {
        if (StringUtils.isEmpty(feedbackId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = memberFeedbackWriteMapper.deleteByPrimaryKey(feedbackId);
        if (count == 0) {
            log.error("根据feedbackId：" + feedbackId + "删除用户反馈失败");
            throw new MallException("删除用户反馈失败,请重试");
        }
        return count;
    }

    /**
     * 根据feedbackId更新用户反馈
     *
     * @param memberFeedback
     * @return
     */
    public Integer updateMemberFeedback(MemberFeedback memberFeedback) {
        if (StringUtils.isEmpty(memberFeedback.getFeedbackId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = memberFeedbackWriteMapper.updateByPrimaryKeySelective(memberFeedback);
        if (count == 0) {
            log.error("根据feedbackId：" + memberFeedback.getFeedbackId() + "更新用户反馈失败");
            throw new MallException("更新用户反馈失败,请重试");
        }
        return count;
    }

    /**
     * 根据feedbackId获取用户反馈详情
     *
     * @param feedbackId feedbackId
     * @return
     */
    public MemberFeedback getMemberFeedbackByFeedbackId(Integer feedbackId) {
        return memberFeedbackReadMapper.getByPrimaryKey(feedbackId);
    }

    /**
     * 根据条件获取用户反馈列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<MemberFeedback> getMemberFeedbackList(MemberFeedbackExample example, PagerInfo pager) {
        List<MemberFeedback> memberFeedbackList;
        if (pager != null) {
            pager.setRowsCount(memberFeedbackReadMapper.countByExample(example));
            memberFeedbackList = memberFeedbackReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            memberFeedbackList = memberFeedbackReadMapper.listByExample(example);
        }
        return memberFeedbackList;
    }
}