package com.slodon.b2b2c.controller.cms.front;

import com.slodon.b2b2c.cms.example.ArticleCategoryExample;
import com.slodon.b2b2c.cms.example.ArticleExample;
import com.slodon.b2b2c.cms.pojo.Article;
import com.slodon.b2b2c.cms.pojo.ArticleCategory;
import com.slodon.b2b2c.core.constant.ArticleConst;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.model.cms.ArticleCategoryModel;
import com.slodon.b2b2c.model.cms.ArticleModel;
import com.slodon.b2b2c.vo.cms.ArticleVO;
import com.slodon.b2b2c.vo.cms.FrontArticleCategoryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "front-帮助中心")
@RestController
@RequestMapping("v3/cms/front/article")
public class FrontArticleController {

    @Resource
    private ArticleModel articleModel;
    @Resource
    private ArticleCategoryModel articleCategoryModel;

    @ApiOperation("帮助中心文章分类列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cateSize", value = "展示文章分类数量", required = true, paramType = "query"),
            @ApiImplicitParam(name = "articleSize", value = "展示文章分类数量,不传代表只查询文章分类", paramType = "query")
    })
    @GetMapping("helpList")
    public JsonResult<List<FrontArticleCategoryVO>> helpList(HttpServletRequest request, Integer cateSize, Integer articleSize) {
        PagerInfo pager = new PagerInfo(cateSize, 1);
        PagerInfo articlePager = null;
        if (!StringUtil.isNullOrZero(articleSize)) {
            articlePager = new PagerInfo(articleSize, 1);
        }
        ArticleCategoryExample example = new ArticleCategoryExample();
        example.setIsShow(ArticleConst.STATE_YES);
        example.setOrderBy("sort asc, create_time desc");
        List<ArticleCategory> list = articleCategoryModel.getArticleCategoryList(example, pager);
        List<FrontArticleCategoryVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            for (ArticleCategory articleCategory : list) {
                FrontArticleCategoryVO categoryVO = new FrontArticleCategoryVO(articleCategory);
                //查询文章列表
                ArticleExample articleExample = new ArticleExample();
                articleExample.setCategoryId(articleCategory.getCategoryId());
                articleExample.setState(ArticleConst.STATE_YES);
                articleExample.setOrderBy("sort asc, create_time desc");
                List<Article> articleList = articleModel.getArticleList(articleExample, articlePager);
                List<FrontArticleCategoryVO.FrontArticleVO> articleVOS = new ArrayList<>();
                if (!CollectionUtils.isEmpty(articleList)) {
                    articleList.forEach(article -> {
                        articleVOS.add(new FrontArticleCategoryVO.FrontArticleVO(article));
                    });
                }
                categoryVO.setArticleList(articleVOS);
                vos.add(categoryVO);
            }
        }
        return SldResponse.success(vos);
    }

    @ApiOperation("文章列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryId", value = "文章分类id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "size", value = "展示文章数量", required = true, paramType = "query")
    })
    @GetMapping("articleList")
    public JsonResult<List<ArticleVO>> articleList(HttpServletRequest request, Integer categoryId, Integer size) {
        PagerInfo pager = new PagerInfo(size, 1);
        ArticleExample example = new ArticleExample();
        example.setCategoryId(categoryId);
        example.setState(ArticleConst.STATE_YES);
        example.setOrderBy("sort asc, create_time desc");
        List<Article> list = articleModel.getArticleList(example, pager);
        List<ArticleVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(article -> {
                vos.add(new ArticleVO(article));
            });
        }
        return SldResponse.success(vos);
    }

    @ApiOperation("文章详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "articleId", value = "文章id", required = true, paramType = "query")
    })
    @GetMapping("articleDetail")
    public JsonResult<Article> articleDetail(HttpServletRequest request, Integer articleId) {
        return SldResponse.success(articleModel.getArticleByArticleId(articleId));
    }

}
