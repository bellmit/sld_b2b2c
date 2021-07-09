package com.slodon.b2b2c.core.express;

import com.alibaba.fastjson.JSON;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @program: slodon
 * @Description 获取物流轨迹工具类
 * @Author wuxy
 */
@Slf4j
public class TrackUtil {

    /**
     * 获取快递鸟物流轨迹
     *
     * @param orderSn       订单号
     * @param expressCode   快递编码
     * @param expressName   快递公司
     * @param expressNumber 快递单号
     * @param EBusinessID   快递鸟EBusinessID
     * @param AppKey        快递鸟AppKey
     * @return
     */
    public static TracesResult getKdniaoTrack(String orderSn, String expressCode, String expressName, String expressNumber,
                                              String EBusinessID, String AppKey) {
        String expressTraceResult = null;
        //查询快递鸟
        KdniaoTrackQueryAPI api = new KdniaoTrackQueryAPI();
        try {
            expressTraceResult = api.getOrderTracesByJson(EBusinessID, AppKey, orderSn, expressCode, expressNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("快递鸟查询物流信息：" + expressTraceResult);
        AssertUtil.isTrue(StringUtil.isEmpty(expressTraceResult), "获取快递轨迹信息失败，请稍后重试");

        DeliverInfo deliverInfo = JSON.parseObject(expressTraceResult, DeliverInfo.class);

        TracesResult tracesResult = new TracesResult();
        tracesResult.setExpressName(expressName);
        tracesResult.setExpressNumber(expressNumber);
        tracesResult.setType("0");
        List<DeliverInfo.Traces> tracesList = deliverInfo.getTraces();
        if (CollectionUtils.isEmpty(tracesList)) {
            tracesList = new ArrayList<>();
        } else {
            Collections.reverse(tracesList);
        }
        tracesResult.setRouteList(tracesList);
        return tracesResult;
    }
}
