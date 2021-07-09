package com.slodon.smartid.server.dao;

import com.slodon.smartid.server.dao.entity.SmartIdInfo;

/**
 * @author smartId
 */
public interface SmartIdInfoDAO {
    /**
     * 根据bizType获取db中的smartId对象
     * @param bizType
     * @return
     */
    SmartIdInfo queryByBizType(String bizType);

    /**
     * 根据id、oldMaxId、version、bizType更新最新的maxId
     * @param id
     * @param newMaxId
     * @param oldMaxId
     * @param version
     * @param bizType
     * @return
     */
    int updateMaxId(Long id, Long newMaxId, Long oldMaxId, Long version, String bizType);
}
