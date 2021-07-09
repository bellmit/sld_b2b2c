package com.slodon.b2b2c.member.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品咨询管理
 */
@Data
public class MemberGoodsAsk implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("咨询id")
    private Integer askId;

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("商品ID")
    private Long goodsId;

    @ApiModelProperty("咨询人ID")
    private Integer memberId;

    @ApiModelProperty("咨询人账号")
    private String memberName;

    @ApiModelProperty("咨询内容")
    private String askContent;

    @ApiModelProperty("回复人ID")
    private Long replyVendorId;

    @ApiModelProperty("回复人账号")
    private String replyVendorName;

    @ApiModelProperty("回复内容")
    private String replyContent;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("回复时间")
    private Date replyTime;

    @ApiModelProperty("状态：1、咨询；2、已经回答；3、前台显示；4、删除")
    private Integer state;

    @ApiModelProperty("是否匿名提问：0、真实姓名，1、匿名")
    private Integer isAnonymous;
}