package com.slodon.b2b2c.vo.goods;

import com.slodon.b2b2c.goods.pojo.GoodsParameterGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GoodsParameterGroupVO {

    @ApiModelProperty("分组id")
    private Integer groupId;

    @ApiModelProperty("分组名称")
    private String groupName;

    @ApiModelProperty("排序0到255，越小越靠前展示")
    private Integer sort;

    @ApiModelProperty("是否展示：0-不展示，1-展示")
    private Integer isShow;

    public GoodsParameterGroupVO(GoodsParameterGroup goodsParameterGroup) {
        this.groupId=goodsParameterGroup.getGroupId();
        this.groupName = goodsParameterGroup.getGroupName();
        this.sort =goodsParameterGroup.getSort();
        this.isShow =goodsParameterGroup.getIsShow();
    }
}
