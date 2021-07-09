package com.slodon.b2b2c.vo.msg;

import com.slodon.b2b2c.msg.pojo.MemberTpl;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: slodon
 * @Description 封装会员模板VO对象
 * @Author wuxy
 */
@Data
public class MemberTplVO implements Serializable {

    private static final long serialVersionUID = -5926814951028047798L;
    @ApiModelProperty("消息模板编号")
    private String tplCode;

    @ApiModelProperty("模板名称")
    private String tplName;

    @ApiModelProperty("模板类型")
    private String tplTypeCode;

    @ApiModelProperty("是否接收 1是，0否")
    private Integer isReceive;

    public MemberTplVO(MemberTpl memberTpl) {
        tplCode = memberTpl.getTplCode();
        tplName = memberTpl.getTplName();
        tplTypeCode = memberTpl.getTplTypeCode();
        isReceive = memberTpl.getIsReceive();
    }
}
