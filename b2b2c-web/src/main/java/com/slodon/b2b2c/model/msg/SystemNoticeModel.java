package com.slodon.b2b2c.model.msg;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.msg.SystemNoticeReadMapper;
import com.slodon.b2b2c.dao.write.msg.SystemNoticeWriteMapper;
import com.slodon.b2b2c.msg.example.SystemNoticeExample;
import com.slodon.b2b2c.msg.pojo.SystemNotice;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class SystemNoticeModel {
    @Resource
    private SystemNoticeReadMapper systemNoticeReadMapper;

    @Resource
    private SystemNoticeWriteMapper systemNoticeWriteMapper;

    /**
     * 新增平台公告
     *
     * @param systemNotice
     * @return
     */
    public Integer saveSystemNotice(SystemNotice systemNotice) {
        int count = systemNoticeWriteMapper.insert(systemNotice);
        if (count == 0) {
            throw new MallException("添加平台公告失败，请重试");
        }
        return count;
    }

    /**
     * 根据noticeId删除平台公告
     *
     * @param noticeId noticeId
     * @return
     */
    public Integer deleteSystemNotice(Integer noticeId) {
        if (StringUtils.isEmpty(noticeId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = systemNoticeWriteMapper.deleteByPrimaryKey(noticeId);
        if (count == 0) {
            log.error("根据noticeId：" + noticeId + "删除平台公告失败");
            throw new MallException("删除平台公告失败,请重试");
        }
        return count;
    }

    /**
     * 根据noticeId更新平台公告
     *
     * @param systemNotice
     * @return
     */
    public Integer updateSystemNotice(SystemNotice systemNotice) {
        if (StringUtils.isEmpty(systemNotice.getNoticeId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = systemNoticeWriteMapper.updateByPrimaryKeySelective(systemNotice);
        if (count == 0) {
            log.error("根据noticeId：" + systemNotice.getNoticeId() + "更新平台公告失败");
            throw new MallException("更新平台公告失败,请重试");
        }
        return count;
    }

    /**
     * 根据noticeId获取平台公告详情
     *
     * @param noticeId noticeId
     * @return
     */
    public SystemNotice getSystemNoticeByNoticeId(Integer noticeId) {
        return systemNoticeReadMapper.getByPrimaryKey(noticeId);
    }

    /**
     * 根据条件获取平台公告列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<SystemNotice> getSystemNoticeList(SystemNoticeExample example, PagerInfo pager) {
        List<SystemNotice> systemNoticeList;
        if (pager != null) {
            pager.setRowsCount(systemNoticeReadMapper.countByExample(example));
            systemNoticeList = systemNoticeReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            systemNoticeList = systemNoticeReadMapper.listByExample(example);
        }
        return systemNoticeList;
    }
}