package com.slodon.smartid.server.factory.impl;

import com.slodon.smartid.base.factory.AbstractIdGeneratorFactory;
import com.slodon.smartid.base.generator.IdGenerator;
import com.slodon.smartid.base.generator.impl.CachedIdGenerator;
import com.slodon.smartid.base.service.SegmentIdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author smartId
 */
@Component
public class IdGeneratorFactoryServer extends AbstractIdGeneratorFactory {

    private static final Logger logger = LoggerFactory.getLogger(CachedIdGenerator.class);
    @Autowired
    private SegmentIdService segmentIdService;

    @Override
    public IdGenerator createIdGenerator(String bizType) {
        logger.info("createIdGenerator :{}", bizType);
        return new CachedIdGenerator(bizType, segmentIdService);
    }
}
