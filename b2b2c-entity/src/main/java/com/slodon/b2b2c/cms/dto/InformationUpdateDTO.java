package com.slodon.b2b2c.cms.dto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lxk
 */
@Data
public class InformationUpdateDTO implements Serializable {

    private static final long serialVersionUID = 3052908747475937963L;

    @ApiModelProperty(value = "资讯id",required = true)
    private Integer informationId;

    @ApiModelProperty("资讯分类id")
    private Integer cateId;

    @ApiModelProperty("资讯标题")
    private String title;

    @ApiModelProperty("资讯封面")
    private String coverImage;

    @ApiModelProperty("浏览量,用于修改阅读量功能")
    private Integer pageView;

    @ApiModelProperty("是否推荐，0-否，1-是，默认0，用于开启关闭推荐开关")
    private Integer isRecommend;

    @ApiModelProperty("是否显示，0-否，1-是，默认1，用于开启关闭显示开关")
    private Integer isShow;

    @ApiModelProperty("资讯内容")
    private String content;
}
