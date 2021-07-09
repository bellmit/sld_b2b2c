package com.slodon.b2b2c.vo.cms;


import com.slodon.b2b2c.cms.pojo.Article;
import com.slodon.b2b2c.core.constant.ArticleConst;
import io.swagger.annotations.ApiModelProperty;
import com.slodon.b2b2c.core.i18n.Language;
import lombok.Data;
import com.slodon.b2b2c.core.i18n.Language;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @program: slodon
 * @Description 封装文章VO对象
 * @Author wuxy
 */
@Data
public class ArticleVO {

    @ApiModelProperty("文章id")
    private Integer articleId;

    @ApiModelProperty("分类名称")
    private String categoryName;

    @ApiModelProperty("新闻标题")
    private String title;

    @ApiModelProperty("显示状态：0、不显示；1、显示")
    private Integer state;

    @ApiModelProperty("显示状态值：0、不显示；1、显示")
    private String stateValue;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("创建时间")
    private Date createTime;

    public ArticleVO(Article article) {
        articleId = article.getArticleId();
        title = article.getTitle();
        state = article.getState();
        stateValue = getRealStateValue(state);
        sort = article.getSort();
        createTime = article.getCreateTime();
    }

    public String getRealStateValue(Integer state) {
        String value = null;
        if (StringUtils.isEmpty(state)) return null;
        switch (state) {
            case ArticleConst.STATE_NO:
                value = "不显示";
                break;
            case ArticleConst.STATE_YES:
                value = "显示";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }
}
