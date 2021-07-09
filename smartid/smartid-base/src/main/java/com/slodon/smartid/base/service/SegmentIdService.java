package com.slodon.smartid.base.service;

import com.slodon.smartid.base.entity.SegmentId;

/**
 * @author smartId
 */
public interface SegmentIdService {

    /**
     * 根据bizType获取下一个SegmentId对象
     *
     * @param bizType
     * @return
     */
    SegmentId getNextSegmentId(String bizType);

}
