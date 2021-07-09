package com.slodon.b2b2c.vo.system;

import com.slodon.b2b2c.system.pojo.TplPcMallData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: slodon
 * @Description 封装模板实例数据vo对象
 * @Author wuxy
 */
@Data
public class TplPcMallDataVO implements Serializable {

    private static final long serialVersionUID = 6207312129514036891L;
    @ApiModelProperty("装修模板数据id")
    private Integer dataId;

    @ApiModelProperty("装修模板id")
    private Integer tplPcId;

    @ApiModelProperty("模板风格")
    private String tplPcName;

    @ApiModelProperty("模板类型")
    private String tplPcType;

    @ApiModelProperty("装修模板名称")
    private String tplPcTypeName;

    @ApiModelProperty("装修模板数据名称(用于管理端展示)")
    private String name;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("是否启用；0==不启用，1==启用")
    private Integer isEnable;

    @ApiModelProperty("实例化装修模板(html片段)")
    private String html;

    @ApiModelProperty("装修模板数据(json)")
    private String json;

    public TplPcMallDataVO(TplPcMallData tplPcMallData) {
        dataId = tplPcMallData.getDataId();
        tplPcId = tplPcMallData.getTplPcId();
        tplPcName = tplPcMallData.getTplPcName();
        tplPcType = tplPcMallData.getTplPcType();
        tplPcTypeName = tplPcMallData.getTplPcTypeName();
        name = tplPcMallData.getName();
        createTime = tplPcMallData.getCreateTime();
        updateTime = tplPcMallData.getUpdateTime();
        sort = tplPcMallData.getSort();
        isEnable = tplPcMallData.getIsEnable();
        html = tplPcMallData.getHtml();
        json = tplPcMallData.getJson();
    }
}
