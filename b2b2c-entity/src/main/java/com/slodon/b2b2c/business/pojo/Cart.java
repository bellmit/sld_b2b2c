package com.slodon.b2b2c.business.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商城购物车
 */
@Data
public class Cart implements Serializable {
    private static final long serialVersionUID = -4294410411219177405L;
    @ApiModelProperty("购物车ID")
    private Integer cartId;

    @ApiModelProperty("会员ID")
    private Integer memberId;

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("商品ID(SPU ID)")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("货品ID(SKU ID)")
    private Long productId;

    @ApiModelProperty("数量")
    private Integer buyNum;

    @ApiModelProperty("货品价格")
    private BigDecimal productPrice;

    @ApiModelProperty("商品图片")
    private String productImage;

    @ApiModelProperty("规格值ID，多个规格用逗号分隔")
    private String specValueIds;

    @ApiModelProperty("规格值集合")
    private String specValues;

    @ApiModelProperty("是否选中状态：0-未选中、1-选中")
    private Integer isChecked;

    @ApiModelProperty("创建时间或更新时间")
    private Date updateTime;

    @ApiModelProperty("促销活动id")
    private Integer promotionId;

    @ApiModelProperty("促销活动类型")
    private Integer promotionType;

    @ApiModelProperty("促销活动描述")
    private String promotionDescription;

    @ApiModelProperty("活动优惠减少的价格")
    private BigDecimal offPrice;

    @ApiModelProperty("1-正常状态，2-商品失效（已删除），3-商品无货")
    private Integer productState;
}