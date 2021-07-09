package com.slodon.b2b2c.dao.write.msg;

import com.slodon.b2b2c.core.database.BaseWriteMapper;
import com.slodon.b2b2c.msg.example.AppClientLogExample;
import com.slodon.b2b2c.msg.pojo.AppClientLog;

/**
 * app客户端记录表，每个客户端有一个唯一的记录，用于app消息推送writeDao
 */
public interface AppClientLogWriteMapper extends BaseWriteMapper<AppClientLog, AppClientLogExample> {
}