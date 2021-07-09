package com.slodon.b2b2c.starter.mq.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: slodon
 * @Description 消息属性
 * @Author wuxy
 */
@Data
public class MessageSendProperty implements Serializable {

    //对应属性（订单号,结算号,商品名,品牌名等)的名称
    private String propertyName;
    //属性值
    private String propertyValue;

    public MessageSendProperty(String propertyName, String propertyValue) {
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }

}
