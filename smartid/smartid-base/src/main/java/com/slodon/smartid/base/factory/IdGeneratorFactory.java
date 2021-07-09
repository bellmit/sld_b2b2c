package com.slodon.smartid.base.factory;

import com.slodon.smartid.base.generator.IdGenerator;

/**
 * @author smartId
 */
public interface IdGeneratorFactory {
    /**
     * 根据bizType创建id生成器
     * @param bizType
     * @return
     */
    IdGenerator getIdGenerator(String bizType);
}
