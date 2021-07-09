package com.slodon.b2b2c.msg.pojo;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 商家公告查看情况
 */
@Data
public class NoticeCheckStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键id")
    private Integer checkStatusId;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("公告id")
    private Integer noticeId;

    @ApiModelProperty("查看时间")
    private Date viewTime;

    @ApiModelProperty("状态 0-未读 1-已读（可标记为未读，暂时不用）")
    private Integer state;
}