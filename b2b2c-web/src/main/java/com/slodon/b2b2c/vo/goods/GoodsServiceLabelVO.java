package com.slodon.b2b2c.vo.goods;

import com.slodon.b2b2c.goods.pojo.GoodsServiceLabel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 商品服务标签
 */

/**
 * @program: slodon
 * @Description 封装商品服务标签VO对象
 * @Author   cwl
 */
@Data
public class GoodsServiceLabelVO {

    @ApiModelProperty("标签id")
    private Integer labelId;

    @ApiModelProperty("标签名称")
    private String labelName;

    @ApiModelProperty("标签描述")
    private String description;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    public GoodsServiceLabelVO(GoodsServiceLabel GoodsServiceLabel) {
        this.labelId = GoodsServiceLabel.getLabelId();
        this.labelName =GoodsServiceLabel.getLabelName();
        this.description =GoodsServiceLabel.getDescription();
        this.createTime = GoodsServiceLabel.getCreateTime();
        this.updateTime = GoodsServiceLabel.getUpdateTime();
    }
}