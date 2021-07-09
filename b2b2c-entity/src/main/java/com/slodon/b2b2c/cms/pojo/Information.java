package com.slodon.b2b2c.cms.pojo;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 资讯表
 */
@Data
public class Information implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("资讯id")
    private Integer informationId;

    @ApiModelProperty("资讯分类id")
    private Integer cateId;

    @ApiModelProperty("分类名称")
    private String cateName;

    @ApiModelProperty("资讯标题")
    private String title;

    @ApiModelProperty("资讯封面")
    private String coverImage;

    @ApiModelProperty("浏览量")
    private Integer pageView;

    @ApiModelProperty("是否推荐，0-否，1-是，默认0")
    private Integer isRecommend;

    @ApiModelProperty("是否显示，0-否，1-是，默认1")
    private Integer isShow;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建人id")
    private Integer createAdminId;

    @ApiModelProperty("创建人名称")
    private String createAdminName;

    @ApiModelProperty("修改时间")
    private Date updateTime;

    @ApiModelProperty("修改人id")
    private Integer updateAdminId;

    @ApiModelProperty("修改人名称")
    private String updateAdminName;

    @ApiModelProperty("资讯内容")
    private String content;
}