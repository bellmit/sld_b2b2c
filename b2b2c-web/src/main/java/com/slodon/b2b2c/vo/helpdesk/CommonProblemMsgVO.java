package com.slodon.b2b2c.vo.helpdesk;

import com.slodon.b2b2c.helpdesk.pojo.HelpdeskCommonProblemMsg;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 客服常见问题表
 */
@Data
public class CommonProblemMsgVO implements Serializable {

    private static final long serialVersionUID = -6484677498391437543L;
    @ApiModelProperty("常见问题消息ID")
    private Integer problemMsgId;

    @ApiModelProperty("商户ID")
    private Long storeId;

    @ApiModelProperty("消息内容，JSON格式，解析参考文档")
    private String msgContent;

    @ApiModelProperty("回答内容，JSON格式，解析参考文档")
    private String msgReply;

    @ApiModelProperty("是否显示 0-不显示 1-显示")
    private Integer isShow;

    @ApiModelProperty("排序")
    private Integer sort;

    public CommonProblemMsgVO(HelpdeskCommonProblemMsg helpdeskCommonProblemMsg) {
        this.problemMsgId = helpdeskCommonProblemMsg.getProblemMsgId();
        this.storeId=helpdeskCommonProblemMsg.getStoreId();
        this.msgContent = helpdeskCommonProblemMsg.getMsgContent();
        this.msgReply = helpdeskCommonProblemMsg.getMsgReply();
        this.isShow = helpdeskCommonProblemMsg.getIsShow();
        this.sort = helpdeskCommonProblemMsg.getSort();
    }
}