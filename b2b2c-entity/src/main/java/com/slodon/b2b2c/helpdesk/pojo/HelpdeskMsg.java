package com.slodon.b2b2c.helpdesk.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 客服聊天消息表
 */
@Data
public class HelpdeskMsg implements Serializable {
    private static final long serialVersionUID = -6008755824155244794L;
    @ApiModelProperty("消息ID")
    private Long msgId;

    @ApiModelProperty("会员ID")
    private Integer memberId;

    @ApiModelProperty("会员名")
    private String memberName;

    @ApiModelProperty("会员头像")
    private String memberAvatar;

    @ApiModelProperty("商户ID")
    private Long vendorId;

    @ApiModelProperty("商户名称")
    private String vendorName;

    @ApiModelProperty("商户头像")
    private String vendorAvatar;

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("消息内容，JSON格式，解析参考文档")
    private String msgContent;

    @ApiModelProperty("消息状态:1为已读,2为未读,默认为2")
    private Integer msgState;

    @ApiModelProperty("消息类型:1.text(文本) 2.img(图片) 3.goods(商品) 4.order(订单)用户")
    private Integer msgType;

    @ApiModelProperty("用户类型：1、会员发送；2、商户发送")
    private Integer userType;

    @ApiModelProperty("添加时间")
    private Date addTime;

    @ApiModelProperty("是否删除:0为未删除,1为已删除")
    private Integer isDelete;

    @ApiModelProperty("微信用户头像")
    private String wxAvatarImg;

    @JsonIgnore
    private Integer receiveMsgNumber;               // 接收消息条数
}