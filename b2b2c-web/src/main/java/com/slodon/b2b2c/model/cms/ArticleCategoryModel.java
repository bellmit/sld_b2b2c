package com.slodon.b2b2c.model.cms;

import com.slodon.b2b2c.dao.read.cms.ArticleCategoryReadMapper;
import com.slodon.b2b2c.dao.read.cms.ArticleReadMapper;
import com.slodon.b2b2c.dao.write.cms.ArticleCategoryWriteMapper;
import com.slodon.b2b2c.cms.dto.ArticleCategoryAddDTO;
import com.slodon.b2b2c.cms.dto.ArticleCategoryUpdateDTO;
import com.slodon.b2b2c.cms.example.ArticleCategoryExample;
import com.slodon.b2b2c.cms.example.ArticleExample;
import com.slodon.b2b2c.cms.pojo.Article;
import com.slodon.b2b2c.cms.pojo.ArticleCategory;
import com.slodon.b2b2c.core.constant.ArticleConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.system.pojo.Admin;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class ArticleCategoryModel {

    @Resource
    private ArticleCategoryReadMapper articleCategoryReadMapper;
    @Resource
    private ArticleCategoryWriteMapper articleCategoryWriteMapper;
    @Resource
    private ArticleReadMapper articleReadMapper;

    /**
     * 新增文章分类
     *
     * @param articleCategory
     * @return
     */
    public Integer saveArticleCategory(ArticleCategory articleCategory) {
        int count = articleCategoryWriteMapper.insert(articleCategory);
        if (count == 0) {
            throw new MallException("添加文章分类失败，请重试");
        }
        return count;
    }

    /**
     * 新增文章分类
     *
     * @param articleCategoryAddDTO
     * @param admin
     * @return
     */
    public Integer saveArticleCategory(ArticleCategoryAddDTO articleCategoryAddDTO, Admin admin) throws Exception {
        //判断分类名称是否重复
        ArticleCategoryExample articleCategoryExample = new ArticleCategoryExample();
        //去掉头尾空格字符
        articleCategoryExample.setCategoryName(StringUtil.trim(articleCategoryAddDTO.getCategoryName()));
        List<ArticleCategory> list = articleCategoryReadMapper.listByExample(articleCategoryExample);
        if (!CollectionUtils.isEmpty(list)) {
            throw new MallException("分类名称重复，请重新输入");
        }

        ArticleCategory articleCategoryInsert = new ArticleCategory();
        articleCategoryInsert.setCreateId(admin.getAdminId());
        articleCategoryInsert.setCategoryName(articleCategoryAddDTO.getCategoryName());
        articleCategoryInsert.setSort(articleCategoryAddDTO.getSort());
        articleCategoryInsert.setIsShow(ArticleConst.STATE_YES);
        articleCategoryInsert.setCreateTime(new Date());
        int count = articleCategoryWriteMapper.insert(articleCategoryInsert);
        if (count == 0) {
            throw new MallException("添加文章分类失败，请重试");
        }
        return articleCategoryInsert.getCategoryId();
    }

    /**
     * 根据categoryId删除文章分类
     *
     * @param categoryId categoryId
     * @return
     */
    public Integer deleteArticleCategory(Integer categoryId) {
        if (StringUtils.isEmpty(categoryId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = articleCategoryWriteMapper.deleteByPrimaryKey(categoryId);
        if (count == 0) {
            log.error("根据categoryId：" + categoryId + "删除文章分类失败");
            throw new MallException("删除文章分类失败,请重试");
        }
        return count;
    }

    /**
     * 批量删除文章分类
     *
     * @param categoryIds categoryIds
     * @return
     */
    public Integer batchDeleteArticle(String categoryIds) {
        String[] idStr = categoryIds.split(",");
        for (String categoryId : idStr) {
            //判断分类下是否有文章
            ArticleExample articleExample = new ArticleExample();
            articleExample.setCategoryId(Integer.parseInt(categoryId));
            List<Article> list = articleReadMapper.listByExample(articleExample);
            if (!CollectionUtils.isEmpty(list)) {
                throw new MallException("请先删除该分类" + categoryIds + "下的所有文章");
            }
        }
        ArticleCategoryExample example = new ArticleCategoryExample();
        example.setCategoryIdIn(categoryIds);
        int count = articleCategoryWriteMapper.deleteByExample(example);
        if (count == 0) {
            log.error("根据categoryIds：" + categoryIds + "批量删除文章分类失败");
            throw new MallException("删除文章分类失败,请重试");
        }
        return count;
    }

    /**
     * 根据categoryId更新文章分类
     *
     * @param articleCategory
     * @return
     */
    public Integer updateArticleCategory(ArticleCategory articleCategory) {
        if (StringUtils.isEmpty(articleCategory.getCategoryId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = articleCategoryWriteMapper.updateByPrimaryKeySelective(articleCategory);
        if (count == 0) {
            log.error("根据categoryId：" + articleCategory.getCategoryId() + "更新文章分类失败");
            throw new MallException("更新文章分类失败,请重试");
        }
        return count;
    }

    /**
     * 根据categoryId更新文章分类
     *
     * @param articleCategoryUpdateDTO
     * @return
     */
    public Integer updateArticleCategory(ArticleCategoryUpdateDTO articleCategoryUpdateDTO) throws Exception {
        if (StringUtils.isEmpty(articleCategoryUpdateDTO.getIsShow())) {
            if (StringUtils.isEmpty(articleCategoryUpdateDTO.getCategoryName())) {
                throw new MallException("请填写分类名称");
            }

            //判断分类名称是否重复
            ArticleCategoryExample articleCategoryExample = new ArticleCategoryExample();
            articleCategoryExample.setCategoryIdNotEquals(articleCategoryUpdateDTO.getCategoryId());
            articleCategoryExample.setCategoryName(StringUtil.trim(articleCategoryUpdateDTO.getCategoryName()));
            List<ArticleCategory> list = articleCategoryReadMapper.listByExample(articleCategoryExample);
            if (!CollectionUtils.isEmpty(list)) {
                throw new MallException("分类名称重复，请重新输入");
            }
        }
        ArticleCategory articleCategoryUpdate = new ArticleCategory();
        PropertyUtils.copyProperties(articleCategoryUpdate,articleCategoryUpdateDTO);
        articleCategoryUpdate.setUpdateTime(new Date());
        int count = articleCategoryWriteMapper.updateByPrimaryKeySelective(articleCategoryUpdate);
        if (count == 0) {
            log.error("根据categoryId：" + articleCategoryUpdate.getCategoryId() + "更新文章分类失败");
            throw new MallException("更新文章分类失败,请重试");
        }
        return count;
    }

    /**
     * 根据categoryId获取文章分类详情
     *
     * @param categoryId categoryId
     * @return
     */
    public ArticleCategory getArticleCategoryByCategoryId(Integer categoryId) {
        return articleCategoryReadMapper.getByPrimaryKey(categoryId);
    }

    /**
     * 根据条件获取文章分类列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<ArticleCategory> getArticleCategoryList(ArticleCategoryExample example, PagerInfo pager) {
        List<ArticleCategory> articleCategoryList;
        if (pager != null) {
            pager.setRowsCount(articleCategoryReadMapper.countByExample(example));
            articleCategoryList = articleCategoryReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            articleCategoryList = articleCategoryReadMapper.listByExample(example);
        }
        return articleCategoryList;
    }
}