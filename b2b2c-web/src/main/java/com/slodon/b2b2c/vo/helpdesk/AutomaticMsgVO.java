package com.slodon.b2b2c.vo.helpdesk;

import com.slodon.b2b2c.helpdesk.pojo.HelpdeskAutomaticMsg;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 客服自动回复消息表
 */
@Data
public class AutomaticMsgVO implements Serializable {

    private static final long serialVersionUID = -2271192551471594890L;
    @ApiModelProperty("自动回复消息ID")
    private Integer autoMsgId;

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("消息内容，JSON格式，解析参考文档")
    private String msgContent;

    @ApiModelProperty("是否显示 0-不显示 1-显示")
    private Integer isShow;

    @ApiModelProperty("排序")
    private Integer sort;

    public AutomaticMsgVO(HelpdeskAutomaticMsg helpdeskAutomaticMsg) {
        this.autoMsgId = helpdeskAutomaticMsg.getAutoMsgId();
        this.storeId=helpdeskAutomaticMsg.getStoreId();
        this.msgContent = helpdeskAutomaticMsg.getMsgContent();
        this.isShow = helpdeskAutomaticMsg.getIsShow();
        this.sort = helpdeskAutomaticMsg.getSort();
    }
}