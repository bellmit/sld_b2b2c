package com.slodon.b2b2c.msg.pojo;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 平台公告
 */
@Data
public class SystemNotice implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("公告id")
    private Integer noticeId;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建人（管理员id）")
    private Integer createAdminId;

    @ApiModelProperty("创建人（管理员）名称")
    private String createAdminName;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("更新人（管理员id）")
    private Integer updateAdminId;

    @ApiModelProperty("更新人（管理员）名称")
    private String updateAdminName;

    @ApiModelProperty("是否置顶 0-否 1-是")
    private Integer isTop;

    @ApiModelProperty("排序号 越小越靠前")
    private Integer sort;

    @ApiModelProperty("内容")
    private String content;
}