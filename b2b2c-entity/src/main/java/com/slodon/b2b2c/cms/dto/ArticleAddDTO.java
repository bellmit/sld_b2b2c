package com.slodon.b2b2c.cms.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lxk
 */
@Data
public class ArticleAddDTO implements Serializable {

    private static final long serialVersionUID = -6394995349109188263L;

    @ApiModelProperty(value = "分类id",required = true)
    private Integer categoryId;

    @ApiModelProperty(value = "新闻标题",required = true)
    private String title;

    @ApiModelProperty(value = "外部链接的URL",required = true)
    private String outUrl;

    @ApiModelProperty(value = "排序",required = true)
    private Integer sort;

    @ApiModelProperty(value = "显示状态：0、不显示；1、显示",required = true)
    private Integer state;

    @ApiModelProperty(value = "内容",required = true)
    private String content;
}
