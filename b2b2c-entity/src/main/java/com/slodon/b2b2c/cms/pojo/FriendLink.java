package com.slodon.b2b2c.cms.pojo;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 合作伙伴
 */
@Data
public class FriendLink implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("链接id")
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

    @ApiModelProperty("创建管理员id")
    private Integer createAdminId;

    @ApiModelProperty("创建管理员名称")
    private String createAdminName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新管理员id")
    private Integer updateAdminId;

    @ApiModelProperty("更新管理员名称")
    private String updateAdminName;

    @ApiModelProperty("修改时间")
    private Date updateTime;
}