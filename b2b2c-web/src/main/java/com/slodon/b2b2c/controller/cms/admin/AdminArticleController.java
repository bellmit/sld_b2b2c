package com.slodon.b2b2c.controller.cms.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.cms.dto.ArticleAddDTO;
import com.slodon.b2b2c.cms.dto.ArticleUpdateDTO;
import com.slodon.b2b2c.cms.example.ArticleExample;
import com.slodon.b2b2c.cms.pojo.Article;
import com.slodon.b2b2c.cms.pojo.ArticleCategory;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.cms.ArticleCategoryModel;
import com.slodon.b2b2c.model.cms.ArticleModel;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.vo.cms.ArticleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "admin-文章管理")
@RestController
@Slf4j
@RequestMapping("v3/cms/admin/article")
public class AdminArticleController {

    @Resource
    private ArticleModel articleModel;
    @Resource
    private ArticleCategoryModel articleCategoryModel;

    @ApiOperation("文章列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "title", value = "新闻标题", paramType = "query"),
            @ApiImplicitParam(name = "content", value = "内容", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<ArticleVO>> getList(HttpServletRequest request, String title, String content) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        ArticleExample example = new ArticleExample();
        example.setTitleLike(title);
        example.setContentLike(content);
        List<Article> list = articleModel.getArticleList(example, pager);
        List<ArticleVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(article -> {
                ArticleVO vo = new ArticleVO(article);
                ArticleCategory articleCategory = articleCategoryModel.getArticleCategoryByCategoryId(article.getCategoryId());
                vo.setCategoryName(articleCategory.getCategoryName());
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("获取文章详情")
    @GetMapping("detail")
    public JsonResult<Article> getDetail(HttpServletRequest request, @RequestParam("articleId") Integer articleId) {
        return SldResponse.success(articleModel.getArticleByArticleId(articleId));
    }

    @ApiOperation("新增文章")
    @OperationLogger(option = "新增文章")
    @PostMapping("add")
    public JsonResult addArticle(HttpServletRequest request, ArticleAddDTO articleAddDTO) throws Exception {
        String logMsg = "文章标题:" + articleAddDTO.getTitle();

        Admin admin = UserUtil.getUser(request, Admin.class);

        AssertUtil.notNull(articleAddDTO.getContent(),"文章内容不能为空");
        String description = articleAddDTO.getContent();
        description = description.replaceAll(System.getProperty("line.separator"), "");
        articleAddDTO.setContent(description);
        articleModel.saveArticle(articleAddDTO,admin);
        return SldResponse.success("保存成功", logMsg);
    }

    @ApiOperation("编辑文章")
    @OperationLogger(option = "编辑文章")
    @PostMapping("edit")
    public JsonResult editArticle(HttpServletRequest request, ArticleUpdateDTO articleUpdateDTO) throws Exception {
        articleModel.updateArticle(articleUpdateDTO);
        return SldResponse.success("修改成功", "文章ID:" + articleUpdateDTO.getArticleId());
    }

    @ApiOperation("删除文章")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "文章ID集合，用逗号隔开", required = true)
    })
    @OperationLogger(option = "删除文章")
    @PostMapping("del")
    public JsonResult delArticle(HttpServletRequest request, String ids) {
        //参数校验
        AssertUtil.notEmpty(ids,"请选择要删除的数据");
        AssertUtil.notFormatFrontIds(ids,"ids格式错误,请重试");

        articleModel.batchDeleteArticle(ids);
        return SldResponse.success("删除成功", "文章ID:" + ids);
    }
}
