package com.slodon.b2b2c.model.system;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.system.SearchLogReadMapper;
import com.slodon.b2b2c.dao.write.system.SearchLogWriteMapper;
import com.slodon.b2b2c.system.example.SearchLogExample;
import com.slodon.b2b2c.system.pojo.SearchLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class SearchLogModel {

    @Resource
    private SearchLogReadMapper searchLogReadMapper;
    @Resource
    private SearchLogWriteMapper searchLogWriteMapper;

    /**
     * 新增搜索历史记录表
     *
     * @param searchLog
     * @return
     */
    public Integer saveSearchLog(SearchLog searchLog) {
        int count = searchLogWriteMapper.insert(searchLog);
        if (count == 0) {
            throw new MallException("添加搜索历史记录表失败，请重试");
        }
        return count;
    }

    /**
     * 根据logId删除搜索历史记录表
     *
     * @param logId logId
     * @return
     */
    public Integer deleteSearchLog(Integer logId) {
        if (StringUtils.isEmpty(logId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = searchLogWriteMapper.deleteByPrimaryKey(logId);
        if (count == 0) {
            log.error("根据logId：" + logId + "删除搜索历史记录表失败");
            throw new MallException("删除搜索历史记录表失败,请重试");
        }
        return count;
    }

    /**
     * 根据logId更新搜索历史记录表
     *
     * @param searchLog
     * @return
     */
    public Integer updateSearchLog(SearchLog searchLog) {
        if (StringUtils.isEmpty(searchLog.getLogId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = searchLogWriteMapper.updateByPrimaryKeySelective(searchLog);
        if (count == 0) {
            log.error("根据logId：" + searchLog.getLogId() + "更新搜索历史记录表失败");
            throw new MallException("更新搜索历史记录表失败,请重试");
        }
        return count;
    }

    /**
     * 根据logId获取搜索历史记录表详情
     *
     * @param logId logId
     * @return
     */
    public SearchLog getSearchLogByLogId(Integer logId) {
        return searchLogReadMapper.getByPrimaryKey(logId);
    }

    /**
     * 根据条件获取搜索历史记录表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<SearchLog> getSearchLogList(SearchLogExample example, PagerInfo pager) {
        List<SearchLog> searchLogList;
        if (pager != null) {
            pager.setRowsCount(searchLogReadMapper.countByExample(example));
            searchLogList = searchLogReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            searchLogList = searchLogReadMapper.listByExample(example);
        }
        return searchLogList;
    }

    /**
     * 批量删除搜索历史记录表
     *
     * @param logIds
     * @return
     */
    @Transactional
    public Integer batchDeleteSearchLog(String logIds) {

        SearchLogExample example = new SearchLogExample();
        example.setLogIdIn(logIds);
        int count = searchLogWriteMapper.deleteByExample(example);
        if (count == 0) {
            log.error("根据logIds：" + logIds + "删除搜索历史记录表失败");
            throw new MallException("批量删除搜索历史记录表失败,请重试");
        }
        return count;
    }
}