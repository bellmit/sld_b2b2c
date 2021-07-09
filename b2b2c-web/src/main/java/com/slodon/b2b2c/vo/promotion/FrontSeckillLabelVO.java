package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.promotion.pojo.SeckillLabel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: slodon
 * @Description 封装秒杀标签VO对象
 */
@Data
public class FrontSeckillLabelVO implements Serializable {
    private static final long serialVersionUID = -2147192751515439936L;
    @ApiModelProperty("标签id")
    private Integer labelId;

    @ApiModelProperty("标签名称")
    private String labelName;

    public FrontSeckillLabelVO(SeckillLabel seckillLabel) {
        this.labelId = seckillLabel.getLabelId();
        this.labelName = seckillLabel.getLabelName();
    }
}