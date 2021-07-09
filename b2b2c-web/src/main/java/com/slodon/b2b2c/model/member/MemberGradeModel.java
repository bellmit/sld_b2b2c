package com.slodon.b2b2c.model.member;

import com.slodon.b2b2c.dao.write.member.MemberGradeWriteMapper;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.member.MemberGradeReadMapper;
import com.slodon.b2b2c.member.example.MemberGradeExample;
import com.slodon.b2b2c.member.pojo.MemberGrade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class MemberGradeModel {

    @Resource
    private MemberGradeReadMapper memberGradeReadMapper;
    @Resource
    private MemberGradeWriteMapper memberGradeWriteMapper;

    /**
     * 新增会员等级配置表
     *
     * @param memberGrade
     * @return
     */
    public Integer saveMemberGrade(MemberGrade memberGrade) {
        int count = memberGradeWriteMapper.insert(memberGrade);
        if (count == 0) {
            throw new MallException("添加会员等级配置表失败，请重试");
        }
        return count;
    }

    /**
     * 根据gradeId删除会员等级配置表
     *
     * @param gradeId gradeId
     * @return
     */
    public Integer deleteMemberGrade(Integer gradeId) {
        if (StringUtils.isEmpty(gradeId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = memberGradeWriteMapper.deleteByPrimaryKey(gradeId);
        if (count == 0) {
            log.error("根据gradeId：" + gradeId + "删除会员等级配置表失败");
            throw new MallException("删除会员等级配置表失败,请重试");
        }
        return count;
    }

    /**
     * 根据gradeId更新会员等级配置表
     *
     * @param memberGrade
     * @return
     */
    public Integer updateMemberGrade(MemberGrade memberGrade) {
        if (StringUtils.isEmpty(memberGrade.getGradeId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = memberGradeWriteMapper.updateByPrimaryKeySelective(memberGrade);
        if (count == 0) {
            log.error("根据gradeId：" + memberGrade.getGradeId() + "更新会员等级配置表失败");
            throw new MallException("更新会员等级配置表失败,请重试");
        }
        return count;
    }

    /**
     * 根据gradeId获取会员等级配置表详情
     *
     * @param gradeId gradeId
     * @return
     */
    public MemberGrade getMemberGradeByGradeId(Integer gradeId) {
        return memberGradeReadMapper.getByPrimaryKey(gradeId);
    }

    /**
     * 根据条件获取会员等级配置表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<MemberGrade> getMemberGradeList(MemberGradeExample example, PagerInfo pager) {
        List<MemberGrade> memberGradeList;
        if (pager != null) {
            pager.setRowsCount(memberGradeReadMapper.countByExample(example));
            memberGradeList = memberGradeReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            memberGradeList = memberGradeReadMapper.listByExample(example);
        }
        return memberGradeList;
    }
}