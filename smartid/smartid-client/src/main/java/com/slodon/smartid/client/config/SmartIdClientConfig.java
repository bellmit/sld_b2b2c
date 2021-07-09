package com.slodon.smartid.client.config;

import java.util.List;

/**
 * @author smartId
 */
public class SmartIdClientConfig {

    private String smartIdToken;
    private String smartIdServer;
    private List<String> serverList;
    private Integer readTimeout;
    private Integer connectTimeout;

    private volatile static SmartIdClientConfig smartIdClientConfig;

    private SmartIdClientConfig() {
    }

    public static SmartIdClientConfig getInstance() {
        if (smartIdClientConfig != null) {
            return smartIdClientConfig;
        }
        synchronized (SmartIdClientConfig.class) {
            if (smartIdClientConfig != null) {
                return smartIdClientConfig;
            }
            smartIdClientConfig = new SmartIdClientConfig();
        }
        return smartIdClientConfig;
    }

    public String getSmartIdToken() {
        return smartIdToken;
    }

    public void setSmartIdToken(String smartIdToken) {
        this.smartIdToken = smartIdToken;
    }

    public String getSmartIdServer() {
        return smartIdServer;
    }

    public void setSmartIdServer(String smartIdServer) {
        this.smartIdServer = smartIdServer;
    }

    public List<String> getServerList() {
        return serverList;
    }

    public void setServerList(List<String> serverList) {
        this.serverList = serverList;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
}
