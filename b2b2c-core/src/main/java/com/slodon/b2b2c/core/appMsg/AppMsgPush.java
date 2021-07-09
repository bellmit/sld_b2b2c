package com.slodon.b2b2c.core.appMsg;

import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.style.Style0;

/**
 * app推送
 */
public class AppMsgPush {

    private static String url = "http://api.getui.com/apiex.htm";

    /**
     * app消息单推
     *
     * @param appId        appId
     * @param appKey       appKey
     * @param masterSecret masterSecret
     * @param msgTitle     消息标题
     * @param msgContent   消息内容
     * @param clientId     客户端身份ID clientId和alias二选一
     * @param alias        客户端身份别名 clientId和alias二选一
     * @return
     */
    public static void pushToSingle(String appId, String appKey, String masterSecret, String msgTitle, String msgContent, String clientId, String alias) {
        IGtPush push = new IGtPush(url, appKey, masterSecret);
        Style0 style = new Style0();

        // STEP2：设置推送标题、推送内容
        style.setTitle(msgTitle);
        style.setText(msgContent);
        style.setLogo("push.png");  // 设置推送图标
        // STEP3：设置响铃、震动等推送效果
        style.setRing(true);  // 设置响铃
        style.setVibrate(true);  // 设置震动
        style.setClearable(true);//是否可清除

        // STEP4：选择通知模板
        NotificationTemplate template = new NotificationTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        template.setStyle(style);

        // STEP5：定义""类型消息对象,设置推送消息有效期等推送参数
        SingleMessage singleMessage = new SingleMessage();
        singleMessage.setData(template);
        singleMessage.setOffline(true);
        singleMessage.setOfflineExpireTime(1000 * 600);  // 时间单位为毫秒
        //推送目标
        Target target = new Target();
        target.setAppId(appId);
        target.setClientId(clientId);
        target.setAlias(alias);
        // STEP6：执行推送
        push.pushMessageToSingle(singleMessage, target);
    }
}
