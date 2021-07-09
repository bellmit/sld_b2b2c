package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.promotion.pojo.SeckillLabel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: slodon
 * @Description 封装秒杀标签VO对象
 */
@Data
public class SeckillLabelVO implements Serializable {

    private static final long serialVersionUID = 781693934765765915L;
    @ApiModelProperty("标签id")
    private Integer labelId;

    @ApiModelProperty("标签名称")
    private String labelName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;


    @ApiModelProperty("是否显示 0-不显示 1-显示")
    private Integer isShow;

    @ApiModelProperty("标签排序")
    private Integer sort;

    public SeckillLabelVO(SeckillLabel seckillLabel) {
        this.labelId = seckillLabel.getLabelId();
        this.labelName =seckillLabel.getLabelName();
        this.sort =seckillLabel.getSort();
        this.createTime = seckillLabel.getCreateTime();
        this.updateTime = seckillLabel.getUpdateTime();
        this.isShow=seckillLabel.getIsShow();
    }
}