package com.slodon.b2b2c.model.msg;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.msg.NoticeCheckStatusReadMapper;
import com.slodon.b2b2c.dao.write.msg.NoticeCheckStatusWriteMapper;
import com.slodon.b2b2c.msg.example.NoticeCheckStatusExample;
import com.slodon.b2b2c.msg.pojo.NoticeCheckStatus;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class NoticeCheckStatusModel {
    @Resource
    private NoticeCheckStatusReadMapper noticeCheckStatusReadMapper;

    @Resource
    private NoticeCheckStatusWriteMapper noticeCheckStatusWriteMapper;

    /**
     * 新增商家公告查看情况
     *
     * @param noticeCheckStatus
     * @return
     */
    public Integer saveNoticeCheckStatus(NoticeCheckStatus noticeCheckStatus) {
        int count = noticeCheckStatusWriteMapper.insert(noticeCheckStatus);
        if (count == 0) {
            throw new MallException("添加商家公告查看情况失败，请重试");
        }
        return count;
    }

    /**
     * 根据checkStatusId删除商家公告查看情况
     *
     * @param checkStatusId checkStatusId
     * @return
     */
    public Integer deleteNoticeCheckStatus(Integer checkStatusId) {
        if (StringUtils.isEmpty(checkStatusId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = noticeCheckStatusWriteMapper.deleteByPrimaryKey(checkStatusId);
        if (count == 0) {
            log.error("根据checkStatusId：" + checkStatusId + "删除商家公告查看情况失败");
            throw new MallException("删除商家公告查看情况失败,请重试");
        }
        return count;
    }

    /**
     * 根据checkStatusId更新商家公告查看情况
     *
     * @param noticeCheckStatus
     * @return
     */
    public Integer updateNoticeCheckStatus(NoticeCheckStatus noticeCheckStatus) {
        if (StringUtils.isEmpty(noticeCheckStatus.getCheckStatusId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = noticeCheckStatusWriteMapper.updateByPrimaryKeySelective(noticeCheckStatus);
        if (count == 0) {
            log.error("根据checkStatusId：" + noticeCheckStatus.getCheckStatusId() + "更新商家公告查看情况失败");
            throw new MallException("更新商家公告查看情况失败,请重试");
        }
        return count;
    }

    /**
     * 根据checkStatusId获取商家公告查看情况详情
     *
     * @param checkStatusId checkStatusId
     * @return
     */
    public NoticeCheckStatus getNoticeCheckStatusByCheckStatusId(Integer checkStatusId) {
        return noticeCheckStatusReadMapper.getByPrimaryKey(checkStatusId);
    }

    /**
     * 根据条件获取商家公告查看情况列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<NoticeCheckStatus> getNoticeCheckStatusList(NoticeCheckStatusExample example, PagerInfo pager) {
        List<NoticeCheckStatus> noticeCheckStatusList;
        if (pager != null) {
            pager.setRowsCount(noticeCheckStatusReadMapper.countByExample(example));
            noticeCheckStatusList = noticeCheckStatusReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            noticeCheckStatusList = noticeCheckStatusReadMapper.listByExample(example);
        }
        return noticeCheckStatusList;
    }
}