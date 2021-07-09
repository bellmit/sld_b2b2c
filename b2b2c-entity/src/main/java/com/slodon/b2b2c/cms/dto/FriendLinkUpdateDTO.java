package com.slodon.b2b2c.cms.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lxk
 */
@Data
public class FriendLinkUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1196295032109515327L;

    @ApiModelProperty(value = "链接id",required = true)
    private Integer linkId;

    @ApiModelProperty("链接名称")
    private String linkName;

    @ApiModelProperty("链接图片")
    private String linkImage;

    @ApiModelProperty("展示方式：1、文字；2、图片")
    private Integer showType;

    @ApiModelProperty("链接url")
    private String linkUrl;

    @ApiModelProperty("排序：数字越小，越靠前")
    private Integer sort;

    @ApiModelProperty("状态：0、不可见；1、可见")
    private Integer state;

}
