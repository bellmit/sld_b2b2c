package com.slodon.b2b2c.core.exception;

public final class SLDException {

    public final static MallException NO_AUTH                   = new MallException("没有权限",10001);
    public final static MallException PARAMETER_NOT_ALLOW_EMPTY = new MallException("参数不能为空",10002);
    public final static MallException DATA_DISABLED             = new MallException("数据已锁定",10003);
    public final static MallException DATA_DUPLICATION          = new MallException("已存在相同数据",10004);
    public final static MallException SERVICE_ERROR             = new MallException("服务异常，请联系系统管理员",10005);
    public final static MallException PARAMETER_ERROR           = new MallException("参数值不对",250);

//    public final static MallException UNLOGIN                   = new MallException("未登录",501);
    public final static MallException UNAUTHORITY               = new MallException("未授权",251);
    public final static MallException INTERNAL_ERROR            = new MallException("系统内部错误",252);
    public final static MallException UN_SUPPORT                = new MallException("不支持业务",253);
    public final static MallException UN_LOGIN                  = new MallException("未登录",501);
    public final static MallException CHOOSE_PAY_TYPE           = new MallException("请选择支付方式",501);
    public final static MallException BALANCE_NOT_ENOUGH        = new MallException("余额不够",501);
    public final static MallException HAVENT_PAY_CHANNEL        = new MallException("没有对应的支付方式",501);
    public final static MallException GET_ORDER_FAIL            = new MallException("获取订单失败",501);
    public final static MallException REFUND_FAIL               = new MallException("退款失败，请重试",  250);
    public final static MallException MEMBER_ID_NOT_BE_NULL     = new MallException("会员ID不能为空，请重试！", 250);
    public final static MallException MEMBER_NOT_EXSITI         = new MallException("会员不存在，请重试！", 250);


}
