package com.slodon.b2b2c.member.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品浏览记录表
 */
@Data
public class MemberProductLookLog implements Serializable {
    private static final long serialVersionUID = 8597503968089777624L;
    @ApiModelProperty("记录id")
    private Integer logId;

    @ApiModelProperty("会员id，没有登录ID为0")
    private Integer memberId;

    @ApiModelProperty("商品id")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品副标题，长度建议140个字符内")
    private String goodsBrief;

    @ApiModelProperty("商品图片")
    private String goodsImage;

    @ApiModelProperty("产品id")
    private Long productId;

    @ApiModelProperty("货品价格")
    private BigDecimal productPrice;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("访问时间")
    private Date createTime;
}