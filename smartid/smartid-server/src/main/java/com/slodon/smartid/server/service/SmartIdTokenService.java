package com.slodon.smartid.server.service;

/**
 * @author smartId
 */
public interface SmartIdTokenService {
    /**
     * 是否有权限
     * @param bizType
     * @param token
     * @return
     */
    boolean canVisit(String bizType, String token);
}
