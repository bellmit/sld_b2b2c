package com.slodon.b2b2c.seller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lxk
 */
@Data
public class StoreGradeUpdateDTO implements Serializable {

    private static final long serialVersionUID = -5154070630919232483L;

    @ApiModelProperty(value = "等级ID",required = true)
    private Integer gradeId;

    @ApiModelProperty("等级名称")
    private String gradeName;

    @ApiModelProperty("允许发布的商品数量")
    private Integer goodsLimit;

    @ApiModelProperty("可推荐的商品数量")
    private Integer recommendLimit;

    @ApiModelProperty("费用")
    private String price;

    @ApiModelProperty("级别，数目越大级别越高")
    private Integer sort;

    @ApiModelProperty("审核：0为否，1为是，默认为1")
    private Integer confirm;

    @ApiModelProperty("申请说明")
    private String description;
}
