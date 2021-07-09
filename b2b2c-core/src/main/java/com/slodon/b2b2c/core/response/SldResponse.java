package com.slodon.b2b2c.core.response;

import com.slodon.b2b2c.core.constant.ResponseConst;

/**
 * API返回Json数据处理
 *
 * @author john
 * @version 1.0
 */
public class SldResponse {

    private SldResponse() {
    }

    public static <T> JsonResult<T> success() {
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.setState(ResponseConst.STATE_SUCCESS);
        jsonResult.setMsg("成功");
        return jsonResult;
    }

    public static <T> JsonResult<T> success(String msg) {
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.setState(ResponseConst.STATE_SUCCESS);
        jsonResult.setMsg(msg);
        return jsonResult;
    }

    public static <T> JsonResult<T> success(String msg, String logMsg) {
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.setState(ResponseConst.STATE_SUCCESS);
        jsonResult.setMsg(msg);
        jsonResult.setLogMsg(logMsg);
        return jsonResult;
    }

    public static <T> JsonResult<T> successMsg(String msg) {
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.setState(ResponseConst.STATE_SUCCESS);
        jsonResult.setMsg(msg);
        return jsonResult;
    }

    public static <T> JsonResult<T> success(T data) {
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.setState(ResponseConst.STATE_SUCCESS);
        jsonResult.setMsg("成功");
        jsonResult.setData(data);
        return jsonResult;
    }

    public static <T> JsonResult<T> success(String msg, T data) {
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.setState(ResponseConst.STATE_SUCCESS);
        jsonResult.setMsg(msg);
        jsonResult.setData(data);
        return jsonResult;
    }

    public static <T> JsonResult<T> success(T data, String logMsg) {
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.setState(ResponseConst.STATE_SUCCESS);
        jsonResult.setLogMsg(logMsg);
        jsonResult.setData(data);
        return jsonResult;
    }

    public static <T> JsonResult<T> fail() {
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.setState(ResponseConst.STATE_FAIL);
        jsonResult.setMsg("错误");
        return jsonResult;
    }

    public static <T> JsonResult<T> fail(String msg) {
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.setState(ResponseConst.STATE_FAIL);
        jsonResult.setMsg(msg);
        return jsonResult;
    }

    public static <T> JsonResult<T> moduleDisabled(String msg) {
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.setState(ResponseConst.STATE_MODULE_DISABLED);
        jsonResult.setMsg(msg);
        return jsonResult;
    }

    public static <T> JsonResult<T> failSpecial(String msg) {
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.setState(ResponseConst.STATE_SPECIAL);
        jsonResult.setMsg(msg);
        return jsonResult;
    }

    public static <T> JsonResult<T> failSpecial(T data) {
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.setState(ResponseConst.STATE_SPECIAL);
        jsonResult.setData(data);
        return jsonResult;
    }

    public static <T> JsonResult<T> fail(String msg, String logMsg) {
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.setState(ResponseConst.STATE_FAIL);
        jsonResult.setMsg(msg);
        jsonResult.setLogMsg(logMsg);
        return jsonResult;
    }

    public static <T> JsonResult<T> fail(int state, String msg) {
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.setState(state);
        jsonResult.setMsg(msg);
        return jsonResult;
    }

    public static <T> JsonResult<T> failSpecial2(String msg) {
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.setState(ResponseConst.STATE_SUBSITE_CLOSE);
        jsonResult.setMsg(msg);
        return jsonResult;
    }

    public static <T> JsonResult<T> badArgument() {
        return fail(ResponseConst.STATE_FAIL, "参数不对");
    }

    public static <T> JsonResult<T> badArgumentValue() {
        return fail(ResponseConst.STATE_FAIL, "参数值不对");
    }

    public static <T> JsonResult<T> unLogin() {
        return fail(ResponseConst.STATE_UNLOGIN, "请登录");
    }

    public static <T> JsonResult<T> loginExpired() {
        return fail(ResponseConst.STATE_UNLOGIN, "请登录");
    }

    public static <T> JsonResult<T> unAuth() {
        return fail(ResponseConst.STATE_UNAUTH, "未授权");
    }

    public static <T> JsonResult<T> serious() {
        return fail(ResponseConst.STATE_FAIL, "系统内部错误");
    }

    public static <T> JsonResult<T> unSupport() {
        return fail(ResponseConst.STATE_FAIL, "业务不支持");
    }

    public static <T> JsonResult<T> updatedDateExpired() {
        return fail(ResponseConst.STATE_FAIL, "更新数据已经失效");
    }

    public static <T> JsonResult<T> updatedDataFailed() {
        return fail(ResponseConst.STATE_FAIL, "更新数据失败");
    }

    public static <T> JsonResult<T> sellerFrozen(String tel) {
        return fail(ResponseConst.STATE_STORE_CLOSE, "您的店铺已经被关闭，请联系平台客服：" + tel);
    }
}
