package com.slodon.b2b2c.core.constant;

/**
 * 结算相关常量
 */
public class BillConst {

    /**
     * 结算状态：1、生成结算单；2、店铺确认；3、平台审核；4、结算完成
     */
    public static final int STATE_1 = 1;
    public static final int STATE_2 = 2;
    public static final int STATE_3 = 3;
    public static final int STATE_4 = 4;

    /**
     * 账户类型：1-银行账号；2-支付宝账号
     */
    public static final int ACCOUNT_TYPE_1 = 1;
    public static final int ACCOUNT_TYPE_2 = 2;

    /**
     * 是否默认账号：1-默认账号，0-非默认账号
     */
    public static final int IS_DEFAULT_YES = 1;
    public static final int IS_DEFAULT_NO = 0;

    /**
     * 操作人角色，1-平台，2-商户
     */
    public static final int OPERATOR_ROLE_1 = 1;
    public static final int OPERATOR_ROLE_2 = 2;

}
