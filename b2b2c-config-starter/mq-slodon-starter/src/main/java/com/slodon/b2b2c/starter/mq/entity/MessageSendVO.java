package com.slodon.b2b2c.starter.mq.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: slodon
 * @Description 消息发送信息
 * @Author wuxy
 */
@Data
public class MessageSendVO implements Serializable {

    //时间字段对应的名称
    private String dateName;
    //商户对应id或者会员对应id
    private long receiveId;
    //模版类型
    private String tplType;
    //跳转链接信息
    private String msgLinkInfo;

    private List<MessageSendProperty> propertyList;
    private List<MessageSendProperty> wxPropertyList;

    public MessageSendVO(List<MessageSendProperty> messageSendProperties, String dateName, long receiveId, String tplType, String msgLinkInfo) {
        this.dateName = dateName;
        this.receiveId = receiveId;
        this.tplType = tplType;
        this.msgLinkInfo = msgLinkInfo;
        this.propertyList = messageSendProperties;
    }

    //待微信参数的构造
    public MessageSendVO(List<MessageSendProperty> messageSendProperties, List<MessageSendProperty> wxPropertyList,
                         String dateName, Integer receiveId, String tplType, String msgLinkInfo) {
        this.dateName = dateName;
        this.receiveId = receiveId;
        this.tplType = tplType;
        this.msgLinkInfo = msgLinkInfo;
        this.propertyList = messageSendProperties;
        this.wxPropertyList = wxPropertyList;
    }

}
