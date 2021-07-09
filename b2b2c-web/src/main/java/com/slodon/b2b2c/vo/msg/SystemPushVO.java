package com.slodon.b2b2c.vo.msg;

import com.slodon.b2b2c.msg.pojo.SystemPush;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: slodon
 * @Description 封装系统推送VO对象
 * @Author wuxy
 * @date 2020.11.20 17:42
 */
@Data
public class SystemPushVO implements Serializable {

    private static final long serialVersionUID = 1752206178019348464L;
    @ApiModelProperty("推送id")
    private Integer pushId;

    @ApiModelProperty("系统推送内容")
    private String content;

    @ApiModelProperty("推送时间")
    private Date sendTime;

    @ApiModelProperty("发送人数")
    private Integer sendNumber;

    @ApiModelProperty("已读人数")
    private Integer readNumber;

    public SystemPushVO(SystemPush systemPush) {
        pushId = systemPush.getPushId();
        content = systemPush.getContent();
        sendTime = systemPush.getSendTime();
        sendNumber = systemPush.getReceiveIds().split(",").length;
    }

}
