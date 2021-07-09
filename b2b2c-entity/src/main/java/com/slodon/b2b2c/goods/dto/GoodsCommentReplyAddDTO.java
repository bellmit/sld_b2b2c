package com.slodon.b2b2c.goods.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商品评论管理
 */
@Data
public class GoodsCommentReplyAddDTO  {

    @ApiModelProperty(value = "评论id",required = true)
    private Integer commentId;

    @ApiModelProperty(value = "回复内容",required = true)
    private String replyContent;


}