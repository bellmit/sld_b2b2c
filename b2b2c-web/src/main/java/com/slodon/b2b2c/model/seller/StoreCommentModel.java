package com.slodon.b2b2c.model.seller;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.dao.read.seller.StoreCommentReadMapper;
import com.slodon.b2b2c.dao.write.seller.StoreCommentWriteMapper;
import com.slodon.b2b2c.seller.example.StoreCommentExample;
import com.slodon.b2b2c.seller.pojo.StoreComment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class StoreCommentModel {

    @Resource
    private StoreCommentReadMapper storeCommentReadMapper;
    @Resource
    private StoreCommentWriteMapper storeCommentWriteMapper;

    /**
     * 新增店铺评论管理
     *
     * @param storeComment
     * @return
     */
    public Integer saveStoreComment(StoreComment storeComment) {
        int count = storeCommentWriteMapper.insert(storeComment);
        if (count == 0) {
            throw new MallException("添加店铺评论管理失败，请重试");
        }
        return count;
    }

    /**
     * 根据commentId删除店铺评论管理
     *
     * @param commentId commentId
     * @return
     */
    public Integer deleteStoreComment(Integer commentId) {
        if (StringUtils.isEmpty(commentId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = storeCommentWriteMapper.deleteByPrimaryKey(commentId);
        if (count == 0) {
            log.error("根据commentId：" + commentId + "删除店铺评论管理失败");
            throw new MallException("删除店铺评论管理失败,请重试");
        }
        return count;
    }

    /**
     * 根据commentId更新店铺评论管理
     *
     * @param storeComment
     * @return
     */
    public Integer updateStoreComment(StoreComment storeComment) {
        if (StringUtils.isEmpty(storeComment.getCommentId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = storeCommentWriteMapper.updateByPrimaryKeySelective(storeComment);
        if (count == 0) {
            log.error("根据commentId：" + storeComment.getCommentId() + "更新店铺评论管理失败");
            throw new MallException("更新店铺评论管理失败,请重试");
        }
        return count;
    }

    /**
     * 根据commentId获取店铺评论管理详情
     *
     * @param commentId commentId
     * @return
     */
    public StoreComment getStoreCommentByCommentId(Integer commentId) {
        return storeCommentReadMapper.getByPrimaryKey(commentId);
    }

    /**
     * 根据条件获取店铺评论管理列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<StoreComment> getStoreCommentList(StoreCommentExample example, PagerInfo pager) {
        List<StoreComment> storeCommentList;
        if (pager != null) {
            pager.setRowsCount(storeCommentReadMapper.countByExample(example));
            storeCommentList = storeCommentReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            storeCommentList = storeCommentReadMapper.listByExample(example);
        }
        return storeCommentList;
    }

    /**
     * 根据commentIds删除店铺评论管理
     *
     * @param commentIds
     * @return
     */
    public Integer deleteStoreComment(String commentIds) {
        StoreCommentExample example = new StoreCommentExample();
        example.setCommentIdIn(commentIds);
        int count = storeCommentWriteMapper.deleteByExample(example);
        AssertUtil.notNullOrZero(count, "删除店铺评论管理失败,请重试");
        return count;
    }
}