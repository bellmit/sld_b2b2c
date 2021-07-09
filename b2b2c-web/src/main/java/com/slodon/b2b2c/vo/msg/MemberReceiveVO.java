package com.slodon.b2b2c.vo.msg;

import com.slodon.b2b2c.core.constant.MsgConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.CommonUtil;
import com.slodon.b2b2c.msg.pojo.MemberReceive;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;

//import com.slodon.b2b2c.msg.pojo.MemberReceive;

/**
 * @program: slodon
 * @Description 封装会员接收VO对象
 * @Author wuxy
 * @date 2020.11.23 11:14
 */
@Data
public class MemberReceiveVO implements Serializable {

    private static final long serialVersionUID = 5604387691324833157L;
    @ApiModelProperty("会员id")
    private Integer memberId;

    @ApiModelProperty("会员名称")
    private String memberName;

    @ApiModelProperty("会员手机号")
    private String memberMobile;

    @ApiModelProperty("消息状态：0--未读，1--已读")
    private Integer msgState;

    @ApiModelProperty("消息状态值：0--未读，1--已读")
    private String msgStateValue;

    public MemberReceiveVO(MemberReceive memberReceive) {
        memberId = memberReceive.getMemberId();
        memberName = memberReceive.getMemberName();
        memberMobile = CommonUtil.dealMobile(memberReceive.getMemberMobile());
        msgState = memberReceive.getMsgState();
        msgStateValue = dealMsgStateValue(memberReceive.getMsgState());
    }

    public static String dealMsgStateValue(Integer msgState) {
        String value = null;
        if (StringUtils.isEmpty(msgState)) return Language.translate("未知");
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
