package com.slodon.smartid.server.dao;

import com.slodon.smartid.server.dao.entity.SmartIdToken;

import java.util.List;

/**
 * @author smartId
 */
public interface SmartIdTokenDAO {
    /**
     * 查询db中所有的token信息
     * @return
     */
    List<SmartIdToken> selectAll();
}
