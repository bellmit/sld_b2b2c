package com.slodon.b2b2c.vo.business;


import com.slodon.b2b2c.system.pojo.Agreement;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 封装协议详情vo
 */
@Data
public class AgreementDetailVO implements Serializable {
    private static final long serialVersionUID = 110738701334134140L;
    @ApiModelProperty("协议编码")
    private String agreementCode;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("修改时间")
    private Date updateTime;

    @ApiModelProperty("内容")
    private String content;

    public AgreementDetailVO(Agreement agreement) {
        this.agreementCode = agreement.getAgreementCode();
        this.title = agreement.getTitle();
        this.updateTime = agreement.getUpdateTime();
        this.content = agreement.getContent();
    }
}