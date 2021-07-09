package com.slodon.b2b2c.dao.read.goods;

import com.slodon.b2b2c.core.database.BaseReadMapper;
import com.slodon.b2b2c.goods.example.GoodsCommentExample;
import com.slodon.b2b2c.goods.pojo.GoodsComment;
import org.apache.ibatis.annotations.Param;

/**
 * 商品评论管理readDao
 */
public interface GoodsCommentReadMapper extends BaseReadMapper<GoodsComment, GoodsCommentExample> {
    /**
     * 平均评分，0-5，保留1位小数
     *
     * @param example
     * @return
     */
    Integer avgScoreByExample(@Param("example") GoodsCommentExample example);
}