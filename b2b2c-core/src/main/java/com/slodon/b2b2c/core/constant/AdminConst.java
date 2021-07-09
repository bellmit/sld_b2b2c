package com.slodon.b2b2c.core.constant;

public class AdminConst {

    /**
     * 管理员状态：1-正常；2-冻结；3-删除（伪删除）
     */
    public static final int ADMIN_STATE_NORM = 1;
    public static final int ADMIN_STATE_FREEZE = 2;
    public static final int ADMIN_STATE_DEL = 3;

    /**
     * 是否超级管理员
     */
    public static final int IS_SUPER_ADMIN_NO = 0;
    public static final int IS_SUPER_ADMIN_YES = 1;

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
