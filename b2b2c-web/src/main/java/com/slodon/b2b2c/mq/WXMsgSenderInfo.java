package com.slodon.b2b2c.mq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信模板消息对象
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class WXMsgSenderInfo {

    @JsonProperty("touser")
    private String toUser;
    @JsonProperty("template_id")
    private String templateId;
    private String url;
    private Object miniprogram;
    private Map<String, Map<String, String>> data = new HashMap<>();

    public WXMsgSenderInfo() {
    }

    public WXMsgSenderInfo(String toUser, String templateId, String url, Object miniprogram, Map<String, Map<String, String>> data) {
        this.toUser = toUser;
        this.templateId = templateId;
        this.url = url;
        this.miniprogram = miniprogram;
        this.data = data;
    }

    public Map<String, String> initData(String value, String color) {
        HashMap<String, String> data = new HashMap<>();
        data.put("value", value);
        data.put("color", color);
        return data;
    }

}
