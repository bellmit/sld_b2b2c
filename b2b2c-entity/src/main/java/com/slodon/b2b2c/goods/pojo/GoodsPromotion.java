package com.slodon.b2b2c.goods.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品活动绑定关系
 */
@Data
public class GoodsPromotion implements Serializable {
    private static final long serialVersionUID = 473567174122821856L;
    @ApiModelProperty("商品活动ID")
    private Integer goodsPromotionId;

    @ApiModelProperty("活动ID")
    private Integer promotionId;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("创建活动的店铺管理员ID")
    private Integer createVendorId;

    @ApiModelProperty("绑定时间")
    private Date bindTime;

    @ApiModelProperty("商品id（spu）")
    private Long goodsId;

    @ApiModelProperty("货品id（sku）")
    private Long productId;

    @ApiModelProperty("商品三级分类id")
    private Integer goodsCategoryId3;

    @ApiModelProperty("活动等级 1-商品活动；2-店铺活动；3-平台活动")
    private Integer promotionGrade;

    @ApiModelProperty("系统内置，每创建一个活动都有一个活动类型")
    private Integer promotionType;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @ApiModelProperty("描述信息")
    private String description;

    @ApiModelProperty("活动是否有效,0-无效，1-有效，置为无效状态后，将此活动信息存入slodon_goods_activity_history")
    private Integer isEffective;

    @ApiModelProperty("绑定类型：1-商品绑定；2-店铺绑定；3-三级分类绑定")
    private Integer bindType;
}