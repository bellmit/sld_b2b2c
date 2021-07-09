package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.promotion.pojo.PresellLabel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: slodon
 * @Description 封装预售活动标签VO对象
 * @Author wuxy
 * @date 2020.11.04 10:27
 */
@Data
public class PreSellLabelVO implements Serializable {

    private static final long serialVersionUID = -8306197642204638320L;
    @ApiModelProperty("预售活动标签id")
    private Integer presellLabelId;

    @ApiModelProperty("预售活动标签名称")
    private String presellLabelName;

    public PreSellLabelVO(PresellLabel label) {
        presellLabelId = label.getPresellLabelId();
        presellLabelName = label.getPresellLabelName();
    }
}
