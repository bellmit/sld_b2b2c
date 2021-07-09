package com.slodon.b2b2c.core.constant;

/**
 * 返回参数常量
 */
public class ResponseConst {
    /**
     * state:200/255
     * 200 正常，
     * 255 失败
     * 257 店铺关闭，禁止登录
     * 258 模块未开启
     * 259 没有权限
     * 260 网站关闭，
     * 266 需要登录
     * 267 特殊情况
     * 268 分站关闭
     * 269 无推手信息
     */
    public final static int STATE_SUCCESS = 200;
    public final static int STATE_FAIL = 255;
    public final static int STATE_STORE_CLOSE = 257;
    public final static int STATE_MODULE_DISABLED = 258;
    public final static int STATE_UNAUTH = 259;
    public final static int STATE_SITE_CLOSE = 260;
    public final static int STATE_UNLOGIN = 266;
    public final static int STATE_SPECIAL = 267;
    public final static int STATE_SUBSITE_CLOSE = 268;
    public final static int STATE_NOT_SPREADER = 269;
}
