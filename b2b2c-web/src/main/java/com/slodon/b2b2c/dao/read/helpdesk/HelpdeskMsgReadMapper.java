package com.slodon.b2b2c.dao.read.helpdesk;

import com.slodon.b2b2c.core.database.BaseReadMapper;
import com.slodon.b2b2c.helpdesk.example.HelpdeskMsgExample;
import com.slodon.b2b2c.helpdesk.pojo.HelpdeskMsg;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HelpdeskMsgReadMapper extends BaseReadMapper<HelpdeskMsg, HelpdeskMsgExample> {

    Integer getChatCount(@Param("example") HelpdeskMsgExample example);

    List<HelpdeskMsg> getChatList(@Param("example") HelpdeskMsgExample example,
                                  @Param("start") Integer start,
                                  @Param("size") Integer size);

}