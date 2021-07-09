package com.slodon.b2b2c.core.exception;

import com.slodon.b2b2c.core.constant.ResponseConst;

/**
 * 系统自定义异常, 常用异常参考SLDException类
 */
public class MallException extends RuntimeException {

    private static final long serialVersionUID = 2726487812574043208L;
    private int code = ResponseConst.STATE_FAIL;

    public MallException(String msg) {
        super(msg);
    }

    public MallException(String msg, int code) {
        super(msg);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}