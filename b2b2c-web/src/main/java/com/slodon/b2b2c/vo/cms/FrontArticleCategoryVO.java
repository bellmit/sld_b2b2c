package com.slodon.b2b2c.vo.cms;

import com.slodon.b2b2c.cms.pojo.Article;
import com.slodon.b2b2c.cms.pojo.ArticleCategory;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装帮助中心文章分类VO对象
 * @Author wuxy
 */
@Data
public class FrontArticleCategoryVO implements Serializable {

    private static final long serialVersionUID = -6608063144880309320L;
    @ApiModelProperty("分类id")
    private Integer categoryId;

    @ApiModelProperty("分类名称")
    private String categoryName;

    @ApiModelProperty("分类名称")
    private List<FrontArticleVO> articleList;

    public FrontArticleCategoryVO(ArticleCategory articleCategory) {
        this.categoryId = articleCategory.getCategoryId();
        this.categoryName = articleCategory.getCategoryName();
    }

    @Data
    public static class FrontArticleVO implements Serializable {

        private static final long serialVersionUID = -4204927751383585867L;
        @ApiModelProperty("文章id")
        private Integer articleId;

        @ApiModelProperty("新闻标题")
        private String title;

        public FrontArticleVO(Article article) {
            this.articleId = article.getArticleId();
            this.title = article.getTitle();
        }
    }
}
