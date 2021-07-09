package com.slodon.b2b2c.vo.msg;

import com.slodon.b2b2c.core.constant.MsgConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.msg.pojo.StoreReceive;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: slodon
 * @Description
 * @Author wuxy
 * @date 2020.11.02 13:45
 */
@Data
public class StoreReceiveMsgVO implements Serializable {

    private static final long serialVersionUID = 6134852926854460490L;
    @ApiModelProperty("接收消息id")
    private Integer receiveId;

    @ApiModelProperty("通知类型")
    private String tplTypeName;

    @ApiModelProperty("通知内容")
    private String msgContent;

    @ApiModelProperty("跳转页面需要的参数")
    private String msgLinkInfo;

    @ApiModelProperty("通知时间")
    private Date msgSendTime;

    @ApiModelProperty("通知状态：0--未读，1--已读")
    private Integer msgState;

    @ApiModelProperty("通知状态值：0--未读，1--已读")
    private String msgStateValue;

    public StoreReceiveMsgVO(StoreReceive storeReceive) {
        receiveId = storeReceive.getReceiveId();
        msgContent = storeReceive.getMsgContent();
        msgLinkInfo = storeReceive.getMsgLinkInfo();
        msgSendTime = storeReceive.getMsgSendTime();
        msgState = storeReceive.getMsgState();
        msgStateValue = dealStateValue(storeReceive.getMsgState());
    }

    public static String dealStateValue(Integer msgState) {
        String value = null;
        if (StringUtils.isEmpty(msgState)) return null;
        switch (msgState) {
            case MsgConst.MSG_STATE_UNREAD:
                value = "未读";
                break;
            case MsgConst.MSG_STATE_HAVE_READ:
                value = "已读";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }
}
