package com.slodon.b2b2c.vo.goods;

import com.slodon.b2b2c.goods.pojo.GoodsSpec;
import com.slodon.b2b2c.goods.pojo.GoodsSpecValue;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author cwl
 * @program: slodon
 * @Description 封装商品规格列表VO对象
 */
@Data
public class GoodsSpecVO {

    @ApiModelProperty("规格id")
    private Integer specId;

    @ApiModelProperty("规格名称")
    private String specName;

    @ApiModelProperty("状态 0-不展示 1-展示，可以删除，但是必须没有其他商品使用")
    private Integer state;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("创建人id（如果是系统创建是adminID，如果是商户是vendorID）")
    private Integer createId;

    @ApiModelProperty("规则值")
    private List<GoodsSpecValue> valueList;

    public GoodsSpecVO(GoodsSpec goodsSpec, List<GoodsSpecValue> specValues) {
        this.valueList = specValues;
        this.specId =  goodsSpec.getSpecId();
        this.specName = goodsSpec.getSpecName();
        this.state = goodsSpec.getState();
        this.sort = goodsSpec.getSort();
        this.createId = goodsSpec.getCreateId();
    }
}