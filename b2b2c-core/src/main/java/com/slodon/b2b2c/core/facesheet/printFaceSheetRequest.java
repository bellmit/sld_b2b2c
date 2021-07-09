package com.slodon.b2b2c.core.facesheet;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class printFaceSheetRequest {

    private String requestData;

    @ApiModelProperty("客户id")
    private String eBusinessID;

    @ApiModelProperty("数据签名")
    private String dataSign;

    @ApiModelProperty("是否显示 1 显示 0 不显示")
    private Integer isPreview;

    @Override
    public String toString() {
        return "RequestData="+requestData+"&EBusinessID="+eBusinessID+"&IsPreview="+isPreview+"&DataSign="+dataSign;

    }
}
