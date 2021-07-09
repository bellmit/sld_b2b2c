package com.slodon.b2b2c.model.cms;

import com.slodon.b2b2c.dao.read.cms.ArticleCategoryReadMapper;
import com.slodon.b2b2c.dao.read.cms.ArticleReadMapper;
import com.slodon.b2b2c.dao.write.cms.ArticleWriteMapper;
import com.slodon.b2b2c.cms.dto.ArticleAddDTO;
import com.slodon.b2b2c.cms.dto.ArticleUpdateDTO;
import com.slodon.b2b2c.cms.example.ArticleExample;
import com.slodon.b2b2c.cms.pojo.Article;
import com.slodon.b2b2c.cms.pojo.ArticleCategory;
import com.slodon.b2b2c.core.constant.ArticleConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.system.pojo.Admin;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class ArticleModel {

    @Resource
    private ArticleReadMapper articleReadMapper;
    @Resource
    private ArticleWriteMapper articleWriteMapper;
    @Resource
    private ArticleCategoryReadMapper articleCategoryReadMapper;


    /**
     * 新增文章
     *
     * @param article
     * @return
     */
    public Integer saveArticle(Article article) {
        int count = articleWriteMapper.insert(article);
        if (count == 0) {
            throw new MallException("添加文章失败，请重试");
        }
        return article.getArticleId();
    }

    /**
     * 新增文章
     *
     * @param articleAddDTO
     * @param admin
     * @return
     */
    public Integer saveArticle(ArticleAddDTO articleAddDTO, Admin admin) throws Exception {
        //根据文章标题查重
        ArticleExample articleExample = new ArticleExample();
        articleExample.setTitle(articleAddDTO.getTitle());
        List<Article> list = articleReadMapper.listByExample(articleExample);
        if (!CollectionUtils.isEmpty(list)) {
            throw new MallException("文章标题重复，请重新填写");
        }
        Article articleInsert = new Article();
        PropertyUtils.copyProperties(articleInsert,articleAddDTO);
        articleInsert.setCreateTime(new Date());
        articleInsert.setCreateId(admin.getAdminId());
        articleInsert.setState(ArticleConst.STATE_YES);
        int count = articleWriteMapper.insert(articleInsert);
        if (count == 0) {
            throw new MallException("添加文章失败，请重试");
        }
        return articleInsert.getArticleId();
    }

    /**
     * 根据articleId删除文章
     *
     * @param articleId articleId
     * @return
     */
    public Integer deleteArticle(Integer articleId) {
        if (StringUtils.isEmpty(articleId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = articleWriteMapper.deleteByPrimaryKey(articleId);
        if (count == 0) {
            log.error("根据articleId：" + articleId + "删除文章失败");
            throw new MallException("删除文章失败,请重试");
        }
        return count;
    }

    /**
     * 批量删除文章
     *
     * @param ids ids
     * @return
     */
    public Integer batchDeleteArticle(String ids) {
        ArticleExample example = new ArticleExample();
        example.setArticleIdIn(ids);
        int count = articleWriteMapper.deleteByExample(example);
        if (count == 0) {
            log.error("根据ids：" + ids + "批量删除文章失败");
            throw new MallException("删除文章失败,请重试");
        }
        return count;
    }

    /**
     * 根据articleId更新文章
     *
     * @param article
     * @return
     */
    public Integer updateArticle(Article article) {
        if (StringUtils.isEmpty(article.getArticleId())) {
            throw new MallException("请选择要修改的数据");
        }

        int count = articleWriteMapper.updateByPrimaryKeySelective(article);
        if (count == 0) {
            log.error("根据articleId：" + article.getArticleId() + "更新文章失败");
            throw new MallException("更新文章失败,请重试");
        }
        return count;
    }

    /**
     * 根据articleId更新文章
     *
     * @param articleUpdateDTO
     * @return
     */
    public Integer updateArticle(ArticleUpdateDTO articleUpdateDTO) throws Exception{
        if (StringUtils.isEmpty(articleUpdateDTO.getArticleId())) {
            throw new MallException("请选择要修改的数据");
        }
        //根据文章标题查重
        ArticleExample articleExample = new ArticleExample();
        articleExample.setArticleIdNotEquals(articleUpdateDTO.getArticleId());
        articleExample.setTitle(articleUpdateDTO.getTitle());
        List<Article> list = articleReadMapper.listByExample(articleExample);
        if (!CollectionUtils.isEmpty(list)) {
            throw new MallException("文章标题重复，请重新填写");
        }
        Article articleUpdate = new Article();
        PropertyUtils.copyProperties(articleUpdate,articleUpdateDTO);
        articleUpdate.setUpdateTime(new Date());
        int count = articleWriteMapper.updateByPrimaryKeySelective(articleUpdate);
        if (count == 0) {
            log.error("根据articleId：" + articleUpdateDTO.getArticleId() + "更新文章失败");
            throw new MallException("更新文章失败,请重试");
        }
        return count;
    }

    /**
     * 根据articleId获取文章详情
     *
     * @param articleId articleId
     * @return
     */
    public Article getArticleByArticleId(Integer articleId) {
        Article article = articleReadMapper.getByPrimaryKey(articleId);
        if (article == null) {
            throw new MallException("获取文章详情为空，请重试");
        }
        ArticleCategory category = articleCategoryReadMapper.getByPrimaryKey(article.getCategoryId());
        article.setCategoryName(category.getCategoryName());
        return article;
    }

    /**
     * 根据条件获取文章列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<Article> getArticleList(ArticleExample example, PagerInfo pager) {
        List<Article> articleList;
        if (pager != null) {
            pager.setRowsCount(articleReadMapper.countByExample(example));
            articleList = articleReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            articleList = articleReadMapper.listByExample(example);
        }
        return articleList;
    }
}