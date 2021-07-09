package com.slodon.b2b2c.vo.system;

import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.constant.ImageSizeEnum;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.system.pojo.TplPc;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: slodon
 * @Description 封装装修模板vo对象
 * @Author wuxy
 */
@Data
public class TplPcVO implements Serializable {

    private static final long serialVersionUID = 4912977191928379014L;
    @ApiModelProperty("模板id")
    private Integer tplPcId;

    @ApiModelProperty("模板分类")
    private String type;

    @ApiModelProperty("模板分类名称")
    private String typeName;

    @ApiModelProperty("模板风格")
    private String name;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("是否启用，0==不启用；1==启用")
    private Integer isEnable;

    @ApiModelProperty("是否可实例化 0==否，1==是")
    private Integer isInstance;

    @ApiModelProperty("模板缩略图")
    private String image;

    @ApiModelProperty("模板描述")
    private String desc;

    @ApiModelProperty("模板数据")
    private String data;

    @ApiModelProperty("默认模板实例数据")
    private String defaultData;

    public TplPcVO(TplPc tplPc) {
        tplPcId = tplPc.getTplPcId();
        type = tplPc.getType();
        typeName = tplPc.getTypeName();
        name = tplPc.getName();
        sort = tplPc.getSort();
        isEnable = tplPc.getIsEnable();
        isInstance = tplPc.getIsInstance();
        image = DomainUrlUtil.SLD_STATIC_RESOURCES + tplPc.getImage();
        desc = tplPc.getDesc();
        data = tplPc.getData();
        defaultData = tplPc.getDefaultData();
    }

}
