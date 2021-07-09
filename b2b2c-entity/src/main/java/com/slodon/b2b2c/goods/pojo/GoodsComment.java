package com.slodon.b2b2c.goods.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品评论管理
 */
@Data
public class GoodsComment implements Serializable {
    private static final long serialVersionUID = 4050167192416148115L;
    @ApiModelProperty("评论id")
    private Integer commentId;

    @ApiModelProperty("评价人ID")
    private Integer memberId;

    @ApiModelProperty("评价人账号")
    private String memberName;

    @ApiModelProperty("评分(1到5)")
    private Integer score;

    @ApiModelProperty("评价内容")
    private String content;

    @ApiModelProperty("评价时间")
    private Date createTime;

    @ApiModelProperty("商品ID")
    private Long goodsId;

    @ApiModelProperty("商品名称为3到50个字符(商品副标题)")
    private String goodsName;

    @ApiModelProperty("货品ID")
    private Long productId;

    @ApiModelProperty("商品规格集合")
    private String specValues;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("订单编号")
    private String orderSn;

    @ApiModelProperty("订单货品明细ID")
    private Long orderProductId;

    @ApiModelProperty("回复人ID")
    private Integer replyId;

    @ApiModelProperty("回复人名称")
    private String replyName;

    @ApiModelProperty("回复内容")
    private String replyContent;

    @ApiModelProperty("状态：1、评价；2、审核通过，前台显示；3、删除")
    private Integer state;

    @ApiModelProperty("审核上架人")
    private Integer adminId;

    @ApiModelProperty("评价上传图片")
    private String image;
}