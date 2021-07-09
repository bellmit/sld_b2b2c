package com.slodon.b2b2c.vo.system;

import com.slodon.b2b2c.core.util.ExcelExportUtil;
import com.slodon.b2b2c.system.pojo.AdminLog;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @program: slodon
 * @Description 封装操作日志VO对象
 * @Author wuxy
 */
@Data
public class AdminLogVO {

    @ApiModelProperty("操作人")
    private String adminName;

    @ApiModelProperty("操作行为")
    private String logContent;

    @ApiModelProperty("操作时间")
    private String logTime;

    @ApiModelProperty("ip")
    private String logIp;

    public AdminLogVO(AdminLog adminLog) {
        adminName = adminLog.getAdminName();
        logContent = adminLog.getLogContent();
        logTime = ExcelExportUtil.getCnDate(adminLog.getLogTime());
        logIp = adminLog.getLogIp();
    }
}
