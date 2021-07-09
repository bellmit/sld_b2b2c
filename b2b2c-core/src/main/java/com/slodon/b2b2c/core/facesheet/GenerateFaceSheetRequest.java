package com.slodon.b2b2c.core.facesheet;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 快递鸟电子面单生成接口 数据封装
 */
@Data
public class GenerateFaceSheetRequest implements Serializable {


    private static final long serialVersionUID = 1820297183886168247L;
    @ApiModelProperty("请求数据")
    private String requestData;

    @ApiModelProperty("客户id")
    private String eBusinessID;

    @ApiModelProperty("请求类型")
    private String requestType;

    @ApiModelProperty("数据签名")
    private String dataSign;

    @ApiModelProperty("数据类型 选填")
    private String dataType;

    @Override
    public String toString() {
        return "RequestData=" + requestData + "&EBusinessID=" + eBusinessID + "&DataType=" + dataType + "&DataSign=" + dataSign + "&RequestType=" + requestType;
    }
}
