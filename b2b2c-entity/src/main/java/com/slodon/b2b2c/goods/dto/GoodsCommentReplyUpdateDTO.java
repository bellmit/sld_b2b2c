package com.slodon.b2b2c.goods.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商品评论管理
 */
@Data
public class GoodsCommentReplyUpdateDTO {

    @ApiModelProperty(value = "评论id",required = true)
    private Integer commentId;

    @ApiModelProperty("回复内容")
    private String replyContent;


}