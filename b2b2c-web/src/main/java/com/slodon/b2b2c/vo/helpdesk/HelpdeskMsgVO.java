package com.slodon.b2b2c.vo.helpdesk;

import com.slodon.b2b2c.core.constant.ImageSizeEnum;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.helpdesk.pojo.HelpdeskMsg;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @program: slodon
 * @Description
 * @Author wuxy
 * @date 2020.09.29 19:32
 */
@Data
public class HelpdeskMsgVO implements Serializable {

    private static final long serialVersionUID = 6858938818307528727L;
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

    @ApiModelProperty("消息类型:1.text(文本) 2.img(图片) 3.goods(商品) 4.order(订单)用户 5.常见问题")
    private Integer msgType;

    @ApiModelProperty("用户类型：1、会员发送；2、商户发送")
    private Integer userType;

    @ApiModelProperty("添加时间")
    private String addTime;

    @ApiModelProperty("接收消息条数")
    private Integer receiveMsgNumber;

    public HelpdeskMsgVO(HelpdeskMsg helpdeskMsg) {
        msgId = helpdeskMsg.getMsgId();
        memberId = helpdeskMsg.getMemberId();
        memberName = helpdeskMsg.getMemberName();
        memberAvatar = StringUtil.isEmpty(helpdeskMsg.getMemberAvatar()) ? helpdeskMsg.getWxAvatarImg() : FileUrlUtil.getFileUrl(helpdeskMsg.getMemberAvatar(), ImageSizeEnum.SMALL);
        vendorId = helpdeskMsg.getVendorId();
        vendorName = helpdeskMsg.getVendorName();
        vendorAvatar = FileUrlUtil.getFileUrl(helpdeskMsg.getVendorAvatar(), ImageSizeEnum.SMALL);
        storeId = helpdeskMsg.getStoreId();
        storeName = helpdeskMsg.getStoreName();
        msgContent = helpdeskMsg.getMsgContent();
        msgState = helpdeskMsg.getMsgState();
        msgType = helpdeskMsg.getMsgType();
        userType = helpdeskMsg.getUserType();
        addTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(helpdeskMsg.getAddTime());
        receiveMsgNumber = Integer.max(helpdeskMsg.getReceiveMsgNumber() == null ? 0 : helpdeskMsg.getReceiveMsgNumber(),0);
    }
}
