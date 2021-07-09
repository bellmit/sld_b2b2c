package com.slodon.smartid.base.generator;

import java.util.List;

/**
 * @author smartId
 */
public interface IdGenerator {
    /**
     * get next id，获取一个ID
     * @return
     */
    Long nextId();

    /**
     * get next id batch，获取一批ID
     * @param batchSize
     * @return
     */
    List<Long> nextId(Integer batchSize);
}
