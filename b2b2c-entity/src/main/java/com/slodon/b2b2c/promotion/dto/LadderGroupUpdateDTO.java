package com.slodon.b2b2c.promotion.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: slodon
 * @Description 阶梯团编辑DTO
 * @Author wuxy
 */
@Data
public class LadderGroupUpdateDTO extends LadderGroupAddDTO implements Serializable {

    private static final long serialVersionUID = 6911466425534942349L;
    @ApiModelProperty("阶梯团活动id")
    private Integer groupId;
}
