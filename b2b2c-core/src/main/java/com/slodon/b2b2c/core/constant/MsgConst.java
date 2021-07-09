package com.slodon.b2b2c.core.constant;

/**
 * 消息常量
 */
public class MsgConst {

    /**
     * 发送消息的接口
     */
    public static final String PUSH_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";

    /**
     * 微信访问token
     */
    public static final String WXPAY_ACCESS_TOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";

    /**
     * 发送方式 1--站内信，2--短信, 3--邮件
     */
    public final static int SEND_WAY_STATION_LETTER = 1;
    public final static int SEND_WAY_SMS            = 2;
    public final static int SEND_WAY_MAIL           = 3;

    /**
     * 接收人类型，1--会员，2--商家
     */
    public final static int RECEIVE_TYPE_MEMBER     = 1;
    public final static int RECEIVE_TYPE_STORE      = 2;

    /**
     * 消息状态 0--未读，1--已读，2--删除
     */
    public final static int MSG_STATE_UNREAD        = 0;
    public final static int MSG_STATE_HAVE_READ     = 1;
    public final static int MSG_STATE_DELETE        = 2;

    /**
     * 是否开启开关：0-关闭；1-开启
     */
    public final static int IS_OPEN_SWITCH_NO       = 0;
    public final static int IS_OPEN_SWITCH_YES      = 1;

    /**
     * 数据来源：0-会员；1-系统推送；2-商户推送
     */
    public final static int SOURCE_MEMBER           = 0;
    public final static int SOURCE_SYSTEM           = 1;
    public final static int SOURCE_STORE            = 2;

    /**
     * 消息模板分类来源 0-系统消息, 1-会员消息, 2-商户消息
     */
    public final static int TPL_SOURCE_SYSTEM       = 0;
    public final static int TPL_SOURCE_MEMBER       = 1;
    public final static int TPL_SOURCE_STORE        = 2;
}
