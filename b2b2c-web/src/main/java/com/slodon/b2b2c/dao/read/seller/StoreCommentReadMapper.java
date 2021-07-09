package com.slodon.b2b2c.dao.read.seller;

import com.slodon.b2b2c.core.database.BaseReadMapper;
import com.slodon.b2b2c.seller.dto.CommentsDTO;
import com.slodon.b2b2c.seller.example.StoreCommentExample;
import com.slodon.b2b2c.seller.pojo.StoreComment;
import org.apache.ibatis.annotations.Param;

public interface StoreCommentReadMapper extends BaseReadMapper<StoreComment, StoreCommentExample> {

    /**
     * 店铺评分
     *
     * @param storeId
     * @return
     */
    CommentsDTO getStoreScoreSum(@Param("storeId") Long storeId);
}