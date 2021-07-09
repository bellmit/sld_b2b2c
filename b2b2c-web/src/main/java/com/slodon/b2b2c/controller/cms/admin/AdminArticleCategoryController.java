package com.slodon.b2b2c.controller.cms.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.cms.dto.ArticleCategoryAddDTO;
import com.slodon.b2b2c.cms.dto.ArticleCategoryUpdateDTO;
import com.slodon.b2b2c.cms.example.ArticleCategoryExample;
import com.slodon.b2b2c.cms.pojo.ArticleCategory;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.cms.ArticleCategoryModel;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.vo.cms.ArticleCategoryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "admin-文章分类管理")
@RestController
@Slf4j
@RequestMapping("v3/cms/admin/articleCategory")
public class AdminArticleCategoryController {

    @Resource
    private ArticleCategoryModel articleCategoryModel;

    @ApiOperation("文章分类列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryName", value = "分类名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<ArticleCategoryVO>> getList(HttpServletRequest request, String categoryName) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        ArticleCategoryExample example = new ArticleCategoryExample();
        example.setCategoryNameLike(categoryName);
        List<ArticleCategory> list = articleCategoryModel.getArticleCategoryList(example, pager);
        List<ArticleCategoryVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            for (ArticleCategory articleCategory : list) {
                ArticleCategoryVO vo = new ArticleCategoryVO(articleCategory);
                vos.add(vo);
            }
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("新增文章分类")
    @OperationLogger(option = "新增文章分类")
    @PostMapping("add")
    public JsonResult addArticleCategory(HttpServletRequest request, ArticleCategoryAddDTO articleCategoryAddDTO) throws Exception {
        Admin admin = UserUtil.getUser(request, Admin.class);

        articleCategoryModel.saveArticleCategory(articleCategoryAddDTO,admin);
        return SldResponse.success("保存成功", "文章分类名称:" + articleCategoryAddDTO.getCategoryName());
    }

    @ApiOperation(value = "编辑文章分类", notes = "用于编辑或是否显示文章分类")
    @OperationLogger(option = "编辑文章分类")
    @PostMapping("edit")
    public JsonResult editArticleCategory(HttpServletRequest request, ArticleCategoryUpdateDTO articleCategoryUpdateDTO) throws Exception {
        articleCategoryModel.updateArticleCategory(articleCategoryUpdateDTO);
        return SldResponse.success("修改成功", "文章分类ID:" + articleCategoryUpdateDTO.getCategoryId());
    }

    @ApiOperation("删除文章分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryIds", value = "文章分类ID集合，用逗号隔开", required = true)
    })
    @OperationLogger(option = "删除文章分类")
    @PostMapping("del")
    public JsonResult delArticleCategory(HttpServletRequest request, String categoryIds) {
        //参数校验
        AssertUtil.notEmpty(categoryIds,"请选择要删除的数据");
        AssertUtil.notFormatFrontIds(categoryIds,"categoryIds格式错误,请重试");

        articleCategoryModel.batchDeleteArticle(categoryIds);
        return SldResponse.success("删除成功", "文章分类ID:" + categoryIds);
    }
}
