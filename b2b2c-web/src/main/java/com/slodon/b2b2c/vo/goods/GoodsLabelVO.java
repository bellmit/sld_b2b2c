package com.slodon.b2b2c.vo.goods;

import com.slodon.b2b2c.goods.pojo.GoodsLabel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 商品标签
 */

/**
 * @program: slodon
 * @Description 封装商品标签VO对象
 * @Author suopengpeng
 */
@Data
public class GoodsLabelVO {

    @ApiModelProperty("标签id")
    private Integer labelId;

    @ApiModelProperty("标签名称")
    private String labelName;

    @ApiModelProperty("标签描述")
    private String description;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    public GoodsLabelVO(GoodsLabel goodsLabel) {
        this.labelId = goodsLabel.getLabelId();
        this.labelName =goodsLabel.getLabelName();
        this.description =goodsLabel.getDescription();
        this.sort =goodsLabel.getSort();
        this.createTime = goodsLabel.getCreateTime();
        this.updateTime = goodsLabel.getUpdateTime();
    }
}