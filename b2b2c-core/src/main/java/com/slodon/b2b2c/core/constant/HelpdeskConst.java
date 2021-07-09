package com.slodon.b2b2c.core.constant;

/**
 * helpdesk常量
 **/
public class HelpdeskConst {

    /**
     * 聊天关系状态：0-不活跃（已被转接）,1-活跃
     */
    public static final int ACTIVE_STATE_NO = 0;
    public static final int ACTIVE_STATE_YES = 1;

    /**
     * 是否删除: 0为未删除, 1为已删除
     */
    public static final int IS_DELETE_NO = 0;
    public static final int IS_DELETE_YES = 1;

    /**
     * 消息状态: 1为已读; 2为未读,默认为2
     */
    public static final int MSG_STATE_YES = 1;
    public static final int MSG_STATE_NO = 2;

    /**
     * 角色类型：1、会员, 2、商户
     */
    public static final String ROLE_TYPE_MEMBER = "1";
    public static final String ROLE_TYPE_VENDOR = "2";

    /**
     * 消息类型:1.text(文本) 2.img(图片) 3.goods(商品) 4.order(订单)用户，5.常见问题
     */
    public static final int MSG_TYPE_TEXT = 1;
    public static final int MSG_TYPE_IMG = 2;
    public static final int MSG_TYPE_GOODS = 3;
    public static final int MSG_TYPE_ORDER = 4;
    public static final int MSG_TYPE_FAQ = 5;

    /**
     * 是否显示 0-不显示 1-显示
     */
    public static final int IS_SHOW_YES = 1;
    public static final int IS_SHOW_NO = 0;
}
