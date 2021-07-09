package com.slodon.b2b2c.model.member;

import com.slodon.b2b2c.dao.write.member.MemberGradeLogWriteMapper;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.member.MemberGradeLogReadMapper;
import com.slodon.b2b2c.member.example.MemberGradeLogExample;
import com.slodon.b2b2c.member.pojo.MemberGradeLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class MemberGradeLogModel {

    @Resource
    private MemberGradeLogReadMapper memberGradeLogReadMapper;
    @Resource
    private MemberGradeLogWriteMapper memberGradeLogWriteMapper;

    /**
     * 新增等级变化日志表
     *
     * @param memberGradeLog
     * @return
     */
    public Integer saveMemberGradeLog(MemberGradeLog memberGradeLog) {
        int count = memberGradeLogWriteMapper.insert(memberGradeLog);
        if (count == 0) {
            throw new MallException("添加等级变化日志表失败，请重试");
        }
        return count;
    }

    /**
     * 根据logId删除等级变化日志表
     *
     * @param logId logId
     * @return
     */
    public Integer deleteMemberGradeLog(Integer logId) {
        if (StringUtils.isEmpty(logId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = memberGradeLogWriteMapper.deleteByPrimaryKey(logId);
        if (count == 0) {
            log.error("根据logId：" + logId + "删除等级变化日志表失败");
            throw new MallException("删除等级变化日志表失败,请重试");
        }
        return count;
    }

    /**
     * 根据logId更新等级变化日志表
     *
     * @param memberGradeLog
     * @return
     */
    public Integer updateMemberGradeLog(MemberGradeLog memberGradeLog) {
        if (StringUtils.isEmpty(memberGradeLog.getLogId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = memberGradeLogWriteMapper.updateByPrimaryKeySelective(memberGradeLog);
        if (count == 0) {
            log.error("根据logId：" + memberGradeLog.getLogId() + "更新等级变化日志表失败");
            throw new MallException("更新等级变化日志表失败,请重试");
        }
        return count;
    }

    /**
     * 根据logId获取等级变化日志表详情
     *
     * @param logId logId
     * @return
     */
    public MemberGradeLog getMemberGradeLogByLogId(Integer logId) {
        return memberGradeLogReadMapper.getByPrimaryKey(logId);
    }

    /**
     * 根据条件获取等级变化日志表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<MemberGradeLog> getMemberGradeLogList(MemberGradeLogExample example, PagerInfo pager) {
        List<MemberGradeLog> memberGradeLogList;
        if (pager != null) {
            pager.setRowsCount(memberGradeLogReadMapper.countByExample(example));
            memberGradeLogList = memberGradeLogReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            memberGradeLogList = memberGradeLogReadMapper.listByExample(example);
        }
        return memberGradeLogList;
    }
}