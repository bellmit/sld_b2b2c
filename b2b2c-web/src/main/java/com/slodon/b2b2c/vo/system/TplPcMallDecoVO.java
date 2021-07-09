package com.slodon.b2b2c.vo.system;

import com.slodon.b2b2c.system.pojo.TplPcMallDeco;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: slodon
 * @Description 封装pc装修页VO对象
 * @Author wuxy
 */
@Data
public class TplPcMallDecoVO implements Serializable {

    private static final long serialVersionUID = 1141186446255349622L;
    @ApiModelProperty("装修页id")
    private Integer decoId;

    @ApiModelProperty("装修页类型")
    private String decoType;

    @ApiModelProperty("装修页名称")
    private String decoName;

    @ApiModelProperty("是否启用该装修页；0==不启用，1==启用")
    private Integer isEnable;

    @ApiModelProperty("创建管理员名称")
    private String createAdminName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新管理员名称")
    private String updateAdminName;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    public TplPcMallDecoVO(TplPcMallDeco tplPcMallDeco) {
        decoId = tplPcMallDeco.getDecoId();
        decoType = tplPcMallDeco.getDecoType();
        decoName = tplPcMallDeco.getDecoName();
        isEnable = tplPcMallDeco.getIsEnable();
        createAdminName = tplPcMallDeco.getCreateUserName();
        createTime = tplPcMallDeco.getCreateTime();
        updateAdminName = tplPcMallDeco.getUpdateUserName();
        updateTime = tplPcMallDeco.getUpdateTime();
    }

}
