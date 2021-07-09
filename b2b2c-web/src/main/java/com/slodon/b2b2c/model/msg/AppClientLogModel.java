package com.slodon.b2b2c.model.msg;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.msg.AppClientLogReadMapper;
import com.slodon.b2b2c.dao.write.msg.AppClientLogWriteMapper;
import com.slodon.b2b2c.msg.example.AppClientLogExample;
import com.slodon.b2b2c.msg.pojo.AppClientLog;

import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * app客户端记录表，每个客户端有一个唯一的记录，用于app消息推送model
 */
@Component
@Slf4j
public class AppClientLogModel {
    @Resource
    private AppClientLogReadMapper appClientLogReadMapper;

    @Resource
    private AppClientLogWriteMapper appClientLogWriteMapper;

    /**
     * 新增app客户端记录表，每个客户端有一个唯一的记录，用于app消息推送
     *
     * @param appClientLog
     * @return
     */
    public Integer saveAppClientLog(AppClientLog appClientLog) {
        int count = appClientLogWriteMapper.insert(appClientLog);
        if (count == 0) {
            throw new MallException("添加app客户端记录表，每个客户端有一个唯一的记录，用于app消息推送失败，请重试");
        }
        return count;
    }

    /**
     * 根据logId删除app客户端记录表，每个客户端有一个唯一的记录，用于app消息推送
     *
     * @param logId logId
     * @return
     */
    public Integer deleteAppClientLog(Integer logId) {
        if (StringUtils.isEmpty(logId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = appClientLogWriteMapper.deleteByPrimaryKey(logId);
        if (count == 0) {
            log.error("根据logId：" + logId + "删除app客户端记录表，每个客户端有一个唯一的记录，用于app消息推送失败");
            throw new MallException("删除app客户端记录表，每个客户端有一个唯一的记录，用于app消息推送失败,请重试");
        }
        return count;
    }

    /**
     * 根据example删除
     * @param example
     * @return
     */
    public Integer deleteAppClientLogByExample(AppClientLogExample example) {
        return appClientLogWriteMapper.deleteByExample(example);
    }

    /**
     * 根据logId更新app客户端记录表，每个客户端有一个唯一的记录，用于app消息推送
     *
     * @param appClientLog
     * @return
     */
    public Integer updateAppClientLog(AppClientLog appClientLog) {
        if (StringUtils.isEmpty(appClientLog.getLogId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = appClientLogWriteMapper.updateByPrimaryKeySelective(appClientLog);
        if (count == 0) {
            log.error("根据logId：" + appClientLog.getLogId() + "更新app客户端记录表，每个客户端有一个唯一的记录，用于app消息推送失败");
            throw new MallException("更新app客户端记录表，每个客户端有一个唯一的记录，用于app消息推送失败,请重试");
        }
        return count;
    }

    /**
     * 根据example更新
     * @param clientId
     * @param alias
     * @return
     */
    public Integer updateAppClientLogByParams(String clientId, String alias) {
        AppClientLog appClientLog = new AppClientLog();
        appClientLog.setUpdateTime(new Date());
        AppClientLogExample appClientLogExample = new AppClientLogExample();
        appClientLogExample.setClientId(clientId);
        appClientLogExample.setAlias(alias);
        return appClientLogWriteMapper.updateByExampleSelective(appClientLog, appClientLogExample);
    }

    /**
     * 根据logId获取app客户端记录表，每个客户端有一个唯一的记录，用于app消息推送详情
     *
     * @param logId logId
     * @return
     */
    public AppClientLog getAppClientLogByLogId(Integer logId) {
        return appClientLogReadMapper.getByPrimaryKey(logId);
    }

    /**
     * 根据条件获取app客户端记录表，每个客户端有一个唯一的记录，用于app消息推送列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<AppClientLog> getAppClientLogList(AppClientLogExample example, PagerInfo pager) {
        List<AppClientLog> appClientLogList;
        if (pager != null) {
            pager.setRowsCount(appClientLogReadMapper.countByExample(example));
            appClientLogList = appClientLogReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            appClientLogList = appClientLogReadMapper.listByExample(example);
        }
        return appClientLogList;
    }
}