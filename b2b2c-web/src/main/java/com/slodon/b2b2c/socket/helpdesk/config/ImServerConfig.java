package com.slodon.b2b2c.socket.helpdesk.config;

import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImServerConfig {

    @Value("${socketio.im.host}")
    private String host;

    @Value("${socketio.im.port}")
    private Integer port;

    @Value("${socketio.im.bossCount}")
    private int bossCount;

    @Value("${socketio.im.workCount}")
    private int workCount;

    @Value("${socketio.im.allowCustomRequests}")
    private boolean allowCustomRequests;

    @Value("${socketio.im.upgradeTimeout}")
    private int upgradeTimeout;

    @Value("${socketio.im.pingTimeout}")
    private int pingTimeout;

    @Value("${socketio.im.pingInterval}")
    private int pingInterval;

    @Bean
    public SocketIOServer imSocketIOServer() {
        SocketConfig socketConfig = new SocketConfig();
        socketConfig.setTcpNoDelay(true);
        socketConfig.setSoLinger(0);
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setSocketConfig(socketConfig);
        config.setHostname(host);
        config.setPort(port);
        config.setBossThreads(bossCount);
        config.setWorkerThreads(workCount);
        config.setAllowCustomRequests(allowCustomRequests);
        config.setUpgradeTimeout(upgradeTimeout);
        config.setPingTimeout(pingTimeout);
        config.setPingInterval(pingInterval);
        return new SocketIOServer(config);
    }
}
