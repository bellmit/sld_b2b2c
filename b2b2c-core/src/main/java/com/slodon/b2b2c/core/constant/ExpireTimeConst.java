package com.slodon.b2b2c.core.constant;

/**
 * 过期时间常量
 */
public class ExpireTimeConst {

    /**
     * int类型，单位秒
     */
    public static final int EXPIRE_SECOND_1_HOUR = 60 * 60;
    public static final int EXPIRE_SECOND_15_DAY = 15 * 24 * 60 * 60;

    /**
     * token过期时间1小时
     */
    public static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60;
    /**
     * refresh_token过期时间15天
     */
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 15;
}
