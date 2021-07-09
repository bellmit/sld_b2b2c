package com.slodon.smartid.client.factory.impl;

import com.slodon.smartid.base.factory.AbstractIdGeneratorFactory;
import com.slodon.smartid.base.generator.IdGenerator;
import com.slodon.smartid.base.generator.impl.CachedIdGenerator;
import com.slodon.smartid.client.config.SmartIdClientConfig;
import com.slodon.smartid.client.service.impl.HttpSegmentIdServiceImpl;
import com.slodon.smartid.client.utils.PropertiesLoader;
import com.slodon.smartid.client.utils.SmartIdNumberUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author smartId
 */
public class IdGeneratorFactoryClient extends AbstractIdGeneratorFactory {

    private static final Logger logger = Logger.getLogger(IdGeneratorFactoryClient.class.getName());

    private static IdGeneratorFactoryClient idGeneratorFactoryClient;

    private static final String DEFAULT_PROP = "smartId_client.properties";

    private static final int DEFAULT_TIME_OUT = 5000;

    private static String serverUrl = "http://{0}/smartid/id/nextSegmentIdSimple?token={1}&bizType=";

    private IdGeneratorFactoryClient() {

    }

    public static IdGeneratorFactoryClient getInstance(String location) {
        if (idGeneratorFactoryClient == null) {
            synchronized (IdGeneratorFactoryClient.class) {
                if (idGeneratorFactoryClient == null) {
                    if (location == null || "".equals(location)) {
                        init(DEFAULT_PROP);
                    } else {
                        init(location);
                    }
                }
            }
        }
        return idGeneratorFactoryClient;
    }

    private static void init(String location) {
        idGeneratorFactoryClient = new IdGeneratorFactoryClient();
        Properties properties = PropertiesLoader.loadProperties(location);
        String smartIdToken = properties.getProperty("smartId.token");
        String smartIdServer = properties.getProperty("smartId.server");
        String readTimeout = properties.getProperty("smartId.readTimeout");
        String connectTimeout = properties.getProperty("smartId.connectTimeout");

        if (smartIdToken == null || "".equals(smartIdToken.trim())
                || smartIdServer == null || "".equals(smartIdServer.trim())) {
            throw new IllegalArgumentException("cannot find smartId.token and smartId.server config in:" + location);
        }

        SmartIdClientConfig smartIdClientConfig = SmartIdClientConfig.getInstance();
        smartIdClientConfig.setSmartIdServer(smartIdServer);
        smartIdClientConfig.setSmartIdToken(smartIdToken);
        smartIdClientConfig.setReadTimeout(SmartIdNumberUtils.toInt(readTimeout, DEFAULT_TIME_OUT));
        smartIdClientConfig.setConnectTimeout(SmartIdNumberUtils.toInt(connectTimeout, DEFAULT_TIME_OUT));

        String[] smartIdServers = smartIdServer.split(",");
        List<String> serverList = new ArrayList<>(smartIdServers.length);
        for (String server : smartIdServers) {
            String url = MessageFormat.format(serverUrl, server, smartIdToken);
            serverList.add(url);
        }
        logger.info("init smartId client success url info:" + serverList);
        smartIdClientConfig.setServerList(serverList);
    }

    @Override
    protected IdGenerator createIdGenerator(String bizType) {
        return new CachedIdGenerator(bizType, new HttpSegmentIdServiceImpl());
    }

}
