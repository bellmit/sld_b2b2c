package com.slodon.b2b2c.core.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author john 2018.12.23
 * @version 1.0
 * JsonResult.java 前后端交互数据标准
 */
@Data
public class JsonResult<T> implements Serializable {

    private static final long serialVersionUID = 5742081810915305509L;

    public JsonResult() {
    }

    public JsonResult(String errMsg) {
        this.state = 250;
        this.msg = errMsg;
    }

    public JsonResult(Integer state, String errMsg) {
        this.state = state;
        this.msg = errMsg;
    }

    /**
     * 返回代码，200==成功，255==失败，267==需要特殊处理，400==请求无效，403==禁止访问，404==请求地址不存在，503==服务器异常
     */
    @ApiModelProperty("返回代码，200==成功，255==失败，267==需要特殊处理，400==请求无效，403==禁止访问，404==请求地址不存在，503==服务器异常")
    private Integer state = 200;

    /**
     * 消息
     */
    @ApiModelProperty("消息")
    private String msg = "成功";

    /**
     * 时间戳
     */
    @ApiModelProperty("时间戳")
    private long timestamp = System.currentTimeMillis();

    /**
     * 结果对象
     */
    @ApiModelProperty(value = "结果对象")
    private T data;

    @ApiModelProperty(hidden = true)
    @JsonIgnore
    private String logMsg;

}
