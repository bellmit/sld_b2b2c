package com.slodon.b2b2c.member.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员收藏商铺表
 */
@Data
public class MemberFollowStore implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("收藏id")
    private Integer followId;

    @ApiModelProperty("会员ID")
    private Integer memberId;

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("店铺LOGO")
    private String storeLogo;

    @ApiModelProperty("店铺所属分类ID")
    private Integer storeCategoryId;

    @ApiModelProperty("店铺所属分类名称")
    private String storeCategoryName;

    @ApiModelProperty("收藏时间")
    private Date createTime;

    @ApiModelProperty("是否置顶：0、不置顶；1、置顶")
    private Integer isTop;
}