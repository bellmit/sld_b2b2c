package com.slodon.b2b2c.vo.msg;

import com.slodon.b2b2c.msg.pojo.StoreTpl;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: slodon
 * @Description 封装店铺模板VO对象
 * @Author wuxy
 */
@Data
public class StoreTplVO implements Serializable {

    private static final long serialVersionUID = -5926814951028047798L;
    @ApiModelProperty("消息模板编号")
    private String tplCode;

    @ApiModelProperty("模板名称")
    private String tplName;

    @ApiModelProperty("是否接收 1是，0否")
    private Integer isReceive;

    public StoreTplVO(StoreTpl storeTpl) {
        tplCode = storeTpl.getTplCode();
        tplName = storeTpl.getTplName();
    }
}
