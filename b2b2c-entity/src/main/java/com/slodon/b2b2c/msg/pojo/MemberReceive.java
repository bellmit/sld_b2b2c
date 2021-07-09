package com.slodon.b2b2c.msg.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员接收消息表
 */
@Data
public class MemberReceive implements Serializable {
    private static final long serialVersionUID = 5814016776637272685L;
    @ApiModelProperty("接收消息id")
    private Integer receiveId;

    @ApiModelProperty("模板编码")
    private String tplCode;

    @ApiModelProperty("会员id")
    private Integer memberId;

    @ApiModelProperty("会员名称")
    private String memberName;

    @ApiModelProperty("会员手机号")
    private String memberMobile;

    @ApiModelProperty("消息内容")
    private String msgContent;

    @ApiModelProperty("跳转页面需要的参数")
    private String msgLinkInfo;

    @ApiModelProperty("发送时间")
    private Date msgSendTime;

    @ApiModelProperty("消息操作时间")
    private Date msgOperateTime;

    @ApiModelProperty("消息状态：0--未读，1--已读，2--删除")
    private Integer msgState;

    @ApiModelProperty("推送内容id(非推送默认为0)")
    private Integer pushId;

    @ApiModelProperty("数据来源：1-系统推送；2-商户推送(非推送默认为0)")
    private Integer source;

    @ApiModelProperty("模板名称")
    private String tplName;
}