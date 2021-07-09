package com.slodon.smartid.server.service.impl;

import com.slodon.smartid.base.entity.SegmentId;
import com.slodon.smartid.base.exception.SmartIdException;
import com.slodon.smartid.base.service.SegmentIdService;
import com.slodon.smartid.server.common.Constants;
import com.slodon.smartid.server.dao.SmartIdInfoDAO;
import com.slodon.smartid.server.dao.entity.SmartIdInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicLong;


/**
 * @author smartId
 */
@Component
public class DbSegmentIdServiceImpl implements SegmentIdService {

    private static final Logger logger = LoggerFactory.getLogger(DbSegmentIdServiceImpl.class);

    @Autowired
    private SmartIdInfoDAO smartIdInfoDAO;

    /**
     * Transactional标记保证query和update使用的是同一连接
     *
     * @param bizType
     * @return
     */
    @Override
    @Transactional
    public SegmentId getNextSegmentId(String bizType) {
        // 获取nextSmartId的时候，有可能存在version冲突，需要重试
        for (int i = 0; i < Constants.RETRY; i++) {
            SmartIdInfo smartIdInfo = smartIdInfoDAO.queryByBizType(bizType);
            if (smartIdInfo == null) {
                throw new SmartIdException("can not find bizType:" + bizType);
            }
            Long newMaxId = smartIdInfo.getMaxId() + smartIdInfo.getStep();
            Long oldMaxId = smartIdInfo.getMaxId();
            int row = smartIdInfoDAO.updateMaxId(smartIdInfo.getId(), newMaxId, oldMaxId, smartIdInfo.getVersion(),
                    smartIdInfo.getBizType());
            if (row == 1) {
                smartIdInfo.setMaxId(newMaxId);
                SegmentId segmentId = convert(smartIdInfo);
                logger.info("getNextSegmentId success smartIdInfo:{} current:{}", smartIdInfo, segmentId);
                return segmentId;
            } else {
                logger.info("getNextSegmentId conflict smartIdInfo:{}", smartIdInfo);
            }
        }
        throw new SmartIdException("get next segmentId conflict");
    }

    public SegmentId convert(SmartIdInfo idInfo) {
        SegmentId segmentId = new SegmentId();
        segmentId.setCurrentId(new AtomicLong(idInfo.getMaxId() - idInfo.getStep()));
        segmentId.setMaxId(idInfo.getMaxId());
        segmentId.setRemainder(idInfo.getRemainder() == null ? 0 : idInfo.getRemainder());
        segmentId.setDelta(idInfo.getDelta() == null ? 1 : idInfo.getDelta());
        // 默认20%加载
        segmentId.setLoadingId(segmentId.getCurrentId().get() + idInfo.getStep() * Constants.LOADING_PERCENT / 100);
        return segmentId;
    }
}
