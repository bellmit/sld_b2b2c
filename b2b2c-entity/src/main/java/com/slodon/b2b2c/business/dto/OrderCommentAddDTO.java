package com.slodon.b2b2c.business.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商品评论管理
 */
@Data
public class OrderCommentAddDTO implements Serializable {

    private static final long serialVersionUID = 4692998282543567142L;
    @ApiModelProperty(value = "订单编号", required = true)
    private String orderSn;

    @ApiModelProperty(value = "描述相符(1到5星)")
    private Integer description;

    @ApiModelProperty(value = "服务态度(1到5星)")
    private Integer serviceAttitude;

    @ApiModelProperty(value = "发货速度(1到5星)")
    private Integer deliverSpeed;

    @ApiModelProperty(value = "商品评价信息", required = true)
    private List<GoodsCommentInfo> goodsCommentInfoList;

    @Data
    public static class GoodsCommentInfo implements Serializable {

        private static final long serialVersionUID = -7479591491494817067L;
        @ApiModelProperty(value = "评分(1到5)", required = true)
        private Integer score;

        @ApiModelProperty(value = "评价内容")
        private String content;

        @ApiModelProperty(value = "订单货品明细ID", required = true)
        private Long orderProductId;

        @ApiModelProperty(value = "评价上传图片")
        private String image;
    }

}