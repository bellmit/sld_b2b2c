package com.slodon.b2b2c.mq;

import lombok.Data;

@Data
public class WxMsgResult {

    private Integer errcode;
    private String errmsg;
    private Long msgid;

}