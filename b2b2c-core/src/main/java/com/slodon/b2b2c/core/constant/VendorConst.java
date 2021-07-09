package com.slodon.b2b2c.core.constant;

/**
 * 商户相关常量
 */
public class VendorConst {

    /**
     * 是否店铺管理员 0-否；1-是
     */
    public static final String IS_STORE_ADMIN_0    = "0";
    public static final String IS_STORE_ADMIN_1    = "1";

    /**
     * 是否允许登陆 0-禁止；1-允许
     */
    public static final int NOT_ALLOW_LOGIN   = 0;
    public static final int IS_ALLOW_LOGIN    = 1;

    /**
     * 角色状态：0-未启用，1-启用
     */
    public static final int ROLE_STATE_1 = 1;
    public static final int ROLE_STATE_0 = 0;

    /**
     * 是否系统内置 1-内置（不可删除、不可修改），0-非内置（可删除、修改）
     */
    public static final int IS_INNER_YES = 1;
    public static final int IS_INNER_NO = 0;

    /**
     * 资源状态：0-未启用，1-启用
     */
    public static final int RESOURCE_STATE_1 = 1;
    public static final int RESOURCE_STATE_0 = 0;

    /**
     * 资源等级：1-一级菜单，2-二级菜单，3-三级菜单，4-按钮
     */
    public static final int RESOURCE_GRADE_1 = 1;
    public static final int RESOURCE_GRADE_2 = 2;
    public static final int RESOURCE_GRADE_3 = 3;
    public static final int RESOURCE_GRADE_4 = 4;
}
