package com.slodon.b2b2c.model.member;

import com.slodon.b2b2c.dao.write.member.MemberFeedbackTypeWriteMapper;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.member.example.MemberFeedbackTypeExample;
import com.slodon.b2b2c.member.pojo.MemberFeedbackType;
import com.slodon.b2b2c.dao.read.member.MemberFeedbackTypeReadMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class MemberFeedbackTypeModel {

    @Resource
    private MemberFeedbackTypeReadMapper memberFeedbackTypeReadMapper;
    @Resource
    private MemberFeedbackTypeWriteMapper memberFeedbackTypeWriteMapper;

    /**
     * 新增反馈类型
     *
     * @param memberFeedbackType
     * @return
     */
    public Integer saveMemberFeedbackType(MemberFeedbackType memberFeedbackType) {
        int count = memberFeedbackTypeWriteMapper.insert(memberFeedbackType);
        if (count == 0) {
            throw new MallException("添加反馈类型失败，请重试");
        }
        return count;
    }

    /**
     * 根据typeId删除反馈类型
     *
     * @param typeId typeId
     * @return
     */
    public Integer deleteMemberFeedbackType(Integer typeId) {
        if (StringUtils.isEmpty(typeId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = memberFeedbackTypeWriteMapper.deleteByPrimaryKey(typeId);
        if (count == 0) {
            log.error("根据typeId：" + typeId + "删除反馈类型失败");
            throw new MallException("删除反馈类型失败,请重试");
        }
        return count;
    }

    /**
     * 根据typeId更新反馈类型
     *
     * @param memberFeedbackType
     * @return
     */
    public Integer updateMemberFeedbackType(MemberFeedbackType memberFeedbackType) {
        if (StringUtils.isEmpty(memberFeedbackType.getTypeId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = memberFeedbackTypeWriteMapper.updateByPrimaryKeySelective(memberFeedbackType);
        if (count == 0) {
            log.error("根据typeId：" + memberFeedbackType.getTypeId() + "更新反馈类型失败");
            throw new MallException("更新反馈类型失败,请重试");
        }
        return count;
    }

    /**
     * 根据typeId获取反馈类型详情
     *
     * @param typeId typeId
     * @return
     */
    public MemberFeedbackType getMemberFeedbackTypeByTypeId(Integer typeId) {
        return memberFeedbackTypeReadMapper.getByPrimaryKey(typeId);
    }

    /**
     * 根据条件获取反馈类型列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<MemberFeedbackType> getMemberFeedbackTypeList(MemberFeedbackTypeExample example, PagerInfo pager) {
        List<MemberFeedbackType> memberFeedbackTypeList;
        if (pager != null) {
            pager.setRowsCount(memberFeedbackTypeReadMapper.countByExample(example));
            memberFeedbackTypeList = memberFeedbackTypeReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            memberFeedbackTypeList = memberFeedbackTypeReadMapper.listByExample(example);
        }
        return memberFeedbackTypeList;
    }
}