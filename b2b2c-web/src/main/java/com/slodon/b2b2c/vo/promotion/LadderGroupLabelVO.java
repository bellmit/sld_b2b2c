package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.promotion.pojo.LadderGroupLabel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: slodon
 * @Description 封装阶梯团标签VO对象
 * @Author wuxy
 */
@Data
public class LadderGroupLabelVO implements Serializable {

    private static final long serialVersionUID = 267377676420866762L;
    @ApiModelProperty("标签id")
    private Integer labelId;

    @ApiModelProperty("标签名称")
    private String labelName;

    public LadderGroupLabelVO(LadderGroupLabel ladderGroupLabel) {
        this.labelId = ladderGroupLabel.getLabelId();
        this.labelName = ladderGroupLabel.getLabelName();
    }
}
