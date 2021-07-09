package com.slodon.b2b2c.cms.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lxk
 */
@Data
public class FriendLinkAddDTO implements Serializable {

    private static final long serialVersionUID = -2123262648171965455L;

    @ApiModelProperty(value = "链接名称",required = true)
    private String linkName;

    @ApiModelProperty("链接图片")
    private String linkImage;

    @ApiModelProperty(value = "展示方式：1、文字；2、图片",required = true)
    private Integer showType;

    @ApiModelProperty(value = "链接url",required = true)
    private String linkUrl;

    @ApiModelProperty(value = "排序：数字越小，越靠前",required = true)
    private Integer sort;
}
