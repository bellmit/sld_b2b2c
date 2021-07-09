package com.slodon.smartid.client.service.impl;

import com.slodon.smartid.base.entity.SegmentId;
import com.slodon.smartid.base.service.SegmentIdService;
import com.slodon.smartid.client.config.SmartIdClientConfig;
import com.slodon.smartid.client.utils.SmartIdHttpUtils;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * @author smartId
 */
public class HttpSegmentIdServiceImpl implements SegmentIdService {

    private static final Logger logger = Logger.getLogger(HttpSegmentIdServiceImpl.class.getName());

    @Override
    public SegmentId getNextSegmentId(String bizType) {
        String url = chooseService(bizType);
        String response = SmartIdHttpUtils.post(url, SmartIdClientConfig.getInstance().getReadTimeout(),
                SmartIdClientConfig.getInstance().getConnectTimeout());
        logger.info("smartId client getNextSegmentId end, response:" + response);
        if (response == null || "".equals(response.trim())) {
            return null;
        }
        SegmentId segmentId = new SegmentId();
        String[] arr = response.split(",");
        segmentId.setCurrentId(new AtomicLong(Long.parseLong(arr[0])));
        segmentId.setLoadingId(Long.parseLong(arr[1]));
        segmentId.setMaxId(Long.parseLong(arr[2]));
        segmentId.setDelta(Integer.parseInt(arr[3]));
        segmentId.setRemainder(Integer.parseInt(arr[4]));
        return segmentId;
    }

    private String chooseService(String bizType) {
        List<String> serverList = SmartIdClientConfig.getInstance().getServerList();
        String url = "";
        if (serverList != null && serverList.size() == 1) {
            url = serverList.get(0);
        } else if (serverList != null && serverList.size() > 1) {
            Random r = new Random();
            url = serverList.get(r.nextInt(serverList.size()));
        }
        url += bizType;
        return url;
    }


}
