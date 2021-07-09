package com.slodon.b2b2c.vo.helpdesk;

import com.slodon.b2b2c.helpdesk.pojo.HelpdeskQuickMsg;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 客服快捷回复消息表
 */
@Data
public class QuickMsgVO implements Serializable {

    private static final long serialVersionUID = 1851350868258134746L;
    @ApiModelProperty("快捷回复消息ID")
    private Integer quickMsgId;

    @ApiModelProperty("商户ID")
    private Long storeId;

    @ApiModelProperty("消息内容，JSON格式，解析参考文档")
    private String msgContent;

    @ApiModelProperty("是否显示 0-不显示 1-显示")
    private Integer isShow;

    @ApiModelProperty("排序")
    private Integer sort;

    public QuickMsgVO(HelpdeskQuickMsg helpdeskQuickMsg) {
        this.quickMsgId = helpdeskQuickMsg.getQuickMsgId();
        this.storeId=helpdeskQuickMsg.getStoreId();
        this.msgContent = helpdeskQuickMsg.getMsgContent();
        this.isShow = helpdeskQuickMsg.getIsShow();
        this.sort = helpdeskQuickMsg.getSort();
    }
}