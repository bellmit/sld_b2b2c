package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.promotion.pojo.SpellLabel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: slodon
 * @Description 封装拼团活动标签VO对象
 * @Author wuxy
 * @date 2020.11.04 19:27
 */
@Data
public class SpellLabelVO implements Serializable {

    private static final long serialVersionUID = 6091535712308954669L;
    @ApiModelProperty("拼团活动标签id")
    private Integer spellLabelId;

    @ApiModelProperty("拼团活动标签名称")
    private String spellLabelName;

    public SpellLabelVO(SpellLabel label) {
        spellLabelId = label.getSpellLabelId();
        spellLabelName = label.getSpellLabelName();
    }
}
