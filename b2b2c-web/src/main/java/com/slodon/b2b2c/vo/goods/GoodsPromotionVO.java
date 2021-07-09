package com.slodon.b2b2c.vo.goods;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装商品活动列表VO对象
 * @Author wuxy
 */
@Data
public class GoodsPromotionVO implements Serializable {

    private static final long serialVersionUID = -2159880448771066183L;
    @ApiModelProperty("商品活动绑定id")
    private Integer goodsPromotionId;

    @ApiModelProperty("活动id")
    private Integer promotionId;

    @ApiModelProperty("活动名称")
    private String promotionName;

    @ApiModelProperty("活动等级 1-商品活动；2-店铺活动；3-平台活动")
    private Integer promotionGrade;

    @ApiModelProperty("活动类型")
    private Integer promotionType;

    @ApiModelProperty("活动描述列表")
    private List<String> descriptionList;
}
