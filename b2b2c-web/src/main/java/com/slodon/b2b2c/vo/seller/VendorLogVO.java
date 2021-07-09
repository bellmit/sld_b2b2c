package com.slodon.b2b2c.vo.seller;

import com.slodon.b2b2c.seller.pojo.VendorLog;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author lxk
 * @program: slodon
 * @Description 封装操作日志VO对象
 */
@Data
public class VendorLogVO {

    @ApiModelProperty("日志id")
    private Integer logId;

    @ApiModelProperty("账号名称")
    private String vendorName;

    @ApiModelProperty("日志内容")
    private String operationContent;

    @ApiModelProperty("操作时间")
    private Date optTime;

    @ApiModelProperty("ip")
    private String ip;

    public VendorLogVO(VendorLog vendorLog) {
        logId = vendorLog.getLogId();
        vendorName = vendorLog.getVendorName();
        operationContent = vendorLog.getOperationContent();
        optTime = vendorLog.getOptTime();
        ip = vendorLog.getIp();
    }
}
