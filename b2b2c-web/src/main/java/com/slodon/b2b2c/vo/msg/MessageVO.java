package com.slodon.b2b2c.vo.msg;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: slodon
 * @Description 封装消息列表VO对象
 * @Author wuxy
 * @date 2020.11.28 16:47
 */
@Data
public class MessageVO implements Serializable {

    private static final long serialVersionUID = -850003229514575558L;
    @ApiModelProperty("消息模板分类编码")
    private String tplTypeCode;

    @ApiModelProperty("消息名称")
    private String msgName;

    @ApiModelProperty("消息条数")
    private Integer msgNum;
}
