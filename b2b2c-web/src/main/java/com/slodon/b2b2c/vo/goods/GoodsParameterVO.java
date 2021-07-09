package com.slodon.b2b2c.vo.goods;

import com.slodon.b2b2c.goods.pojo.GoodsParameter;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GoodsParameterVO {

    @ApiModelProperty("属性id")
    private Integer parameterId;

    @ApiModelProperty("分组id")
    private Integer groupId;

    @ApiModelProperty("属性名称")
    private String parameterName;

    @ApiModelProperty("排序0到255，越小越靠前展示")
    private Integer sort;

    @ApiModelProperty("是否展示：0-不展示，1-展示")
    private Integer isShow;

    @ApiModelProperty("属性值，用逗号隔开")
    private String parameterValue;

    public GoodsParameterVO(GoodsParameter goodsParameter) {
        this.parameterId =goodsParameter.getParameterId();
        this.groupId = goodsParameter.getGroupId();
        this.parameterName = goodsParameter.getParameterName();
        this.sort =goodsParameter.getSort();
        this.isShow = goodsParameter.getIsShow();
        this.parameterValue=goodsParameter.getParameterValue();
    }
}
