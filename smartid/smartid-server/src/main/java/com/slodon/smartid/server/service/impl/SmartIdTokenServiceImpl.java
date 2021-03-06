package com.slodon.smartid.server.service.impl;

import com.slodon.smartid.server.dao.SmartIdTokenDAO;
import com.slodon.smartid.server.dao.entity.SmartIdToken;
import com.slodon.smartid.server.service.SmartIdTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author smartId
 */
@Component
public class SmartIdTokenServiceImpl implements SmartIdTokenService {

    @Autowired
    private SmartIdTokenDAO smartIdTokenDAO;

    private static Map<String, Set<String>> token2bizTypes = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(SmartIdTokenServiceImpl.class);

    public List<SmartIdToken> queryAll() {
        return smartIdTokenDAO.selectAll();
    }

    /**
     * 1分钟刷新一次token
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void refresh() {
        logger.info("refresh token begin");
        init();
    }

    @PostConstruct
    private synchronized void init() {
        logger.info("smartId token init begin");
        List<SmartIdToken> list = queryAll();
        Map<String, Set<String>> map = convertToMap(list);
        token2bizTypes = map;
        logger.info("smartId token init success, token size:{}", list == null ? 0 : list.size());
    }

    @Override
    public boolean canVisit(String bizType, String token) {
        if (StringUtils.isEmpty(bizType) || StringUtils.isEmpty(token)) {
            return false;
        }
        Set<String> bizTypes = token2bizTypes.get(token);
        return (bizTypes != null && bizTypes.contains(bizType));
    }

    private Map<String, Set<String>> convertToMap(List<SmartIdToken> list) {
        Map<String, Set<String>> map = new HashMap<>(64);
        if (list != null) {
            for (SmartIdToken smartIdToken : list) {
                if (!map.containsKey(smartIdToken.getToken())) {
                    map.put(smartIdToken.getToken(), new HashSet<String>());
                }
                map.get(smartIdToken.getToken()).add(smartIdToken.getBizType());
            }
        }
        return map;
    }

}
