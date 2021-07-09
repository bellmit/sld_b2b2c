package com.slodon.b2b2c.msg.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息模板分类-内置表，不做修改
 */
@Data
public class SystemTplType implements Serializable {
    private static final long serialVersionUID = 965145273920672288L;
    @ApiModelProperty("消息模板分类编码")
    private String tplTypeCode;

    @ApiModelProperty("消息模板分类名称")
    private String tplName;

    @ApiModelProperty("消息模板分类来源 0==系统消息, 1==会员消息, 2==商户消息")
    private Integer tplSource;

    @ApiModelProperty("排序")
    private Integer sort;

    @JsonIgnore
    private List<MemberTpl> memberTplList = new ArrayList<>();

    @JsonIgnore
    private List<StoreTpl> storeTplList = new ArrayList<>();
}