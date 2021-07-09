package com.slodon.b2b2c.vo.cms;


import com.slodon.b2b2c.cms.pojo.ArticleCategory;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @program: slodon
 * @Description 封装文章分类VO对象
 * @Author lxk
 */
@Data
public class ArticleCategoryVO {

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

    public ArticleCategoryVO(ArticleCategory articleCategory) {
        categoryId = articleCategory.getCategoryId();
        categoryName = articleCategory.getCategoryName();
        sort = articleCategory.getSort();
        isShow = articleCategory.getIsShow();
        createId = articleCategory.getCreateId();
        createTime = articleCategory.getCreateTime();
        updateTime = articleCategory.getUpdateTime();
    }
}
