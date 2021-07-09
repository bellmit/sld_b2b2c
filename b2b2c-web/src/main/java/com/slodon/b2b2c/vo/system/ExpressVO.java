package com.slodon.b2b2c.vo.system;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: slodon
 * @Description 封装物流公司VO对象
 * @Author wuxy
 */
@Data
public class ExpressVO implements Serializable {

    private static final long serialVersionUID = 1602217049353247103L;
    @ApiModelProperty("物流ID")
    private Integer expressId;

    @ApiModelProperty("物流名称")
    private String expressName;

    public ExpressVO(Integer expressId, String expressName) {
        this.expressId = expressId;
        this.expressName = expressName;
    }
}
