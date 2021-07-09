package com.slodon.b2b2c.cms.pojo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 文章分类
 */
@Data
public class ArticleCategory implements Serializable {
    private static final long serialVersionUID = 6740299387604618925L;
    @ApiModelProperty("分类id")
    private Integer categoryId;

    @ApiModelProperty("分类名称")
    private String categoryName;

    @ApiModelProperty("排序：序号越小，越靠前")
    private Integer sort;

    @ApiModelProperty("是否显示：0、不显示；1、显示")
    private Integer isShow;

    @ApiModelProperty("创建人id")
    private Integer createId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改时间")
    private Date updateTime;
}