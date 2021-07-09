package com.slodon.b2b2c.seller.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 店铺等级表
 */
@Data
public class StoreGrade implements Serializable {
    private static final long serialVersionUID = 7370514913540026101L;

    @ApiModelProperty("等级ID")
    private Integer gradeId;

    @ApiModelProperty("等级名称")
    private String gradeName;

    @ApiModelProperty("允许发布的商品数量")
    private Integer goodsLimit;

    @ApiModelProperty("允许推荐的商品数量")
    private Integer recommendLimit;

    @ApiModelProperty("费用")
    private String price;

    @ApiModelProperty("审核：0为否，1为是，默认为1")
    private Integer confirm;

    @ApiModelProperty("级别，数目越大级别越高")
    private Integer sort;

    @ApiModelProperty("申请说明")
    private String description;
}