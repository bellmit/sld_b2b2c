package com.slodon.b2b2c.cms.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lxk
 */
@Data
public class ArticleUpdateDTO implements Serializable {

    private static final long serialVersionUID = 4572979147065696154L;

    @ApiModelProperty(value = "文章id",required = true)
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

    @ApiModelProperty("内容")
    private String content;
}
