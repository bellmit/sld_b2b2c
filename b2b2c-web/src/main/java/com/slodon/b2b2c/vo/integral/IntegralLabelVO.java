package com.slodon.b2b2c.vo.integral;

import com.slodon.b2b2c.integral.pojo.IntegralGoodsLabel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: slodon
 * @Description 封装标签VO对象
 * @Author wuxy
 */
@Data
public class IntegralLabelVO implements Serializable {

    private static final long serialVersionUID = -5182715582148607366L;
    @ApiModelProperty("标签id")
    private Integer labelId;

    @ApiModelProperty("标签名称")
    private String labelName;

    public IntegralLabelVO(IntegralGoodsLabel integralGoodsLabel) {
        this.labelId = integralGoodsLabel.getLabelId();
        this.labelName = integralGoodsLabel.getLabelName();
    }
}
