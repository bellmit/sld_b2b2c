package com.slodon.b2b2c.cms.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 文章
 */
@Data
public class Article implements Serializable {
    private static final long serialVersionUID = -2071277410898478296L;
    @ApiModelProperty("文章id")
    private Integer articleId;

    @ApiModelProperty("分类id")
    private Integer categoryId;

    @ApiModelProperty("新闻标题")
    private String title;

    @ApiModelProperty("外部链接的URL")
    private String outUrl;

    @ApiModelProperty("显示状态：0、不显示；1、显示")
    private Integer state;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("创建人id")
    private Integer createId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改时间")
    private Date updateTime;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("分类名称")
    private String categoryName;
}