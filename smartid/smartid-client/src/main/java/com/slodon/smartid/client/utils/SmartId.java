package com.slodon.smartid.client.utils;

import com.slodon.smartid.client.factory.impl.IdGeneratorFactoryClient;
import com.slodon.smartid.base.generator.IdGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author smartId
 */
public class SmartId {
    private static IdGeneratorFactoryClient client = IdGeneratorFactoryClient.getInstance(null);

    private SmartId() {

    }

    public static Long nextId(String bizType) {
        if(bizType == null) {
            throw new IllegalArgumentException("type is null");
        }
        IdGenerator idGenerator = client.getIdGenerator(bizType);
        return idGenerator.nextId();
    }

    public static List<Long> nextId(String bizType, Integer batchSize) {
        if(batchSize == null) {
            Long id = nextId(bizType);
            List<Long> list = new ArrayList<>();
            list.add(id);
            return list;
         }
        IdGenerator idGenerator = client.getIdGenerator(bizType);
        return idGenerator.nextId(batchSize);
    }

}
