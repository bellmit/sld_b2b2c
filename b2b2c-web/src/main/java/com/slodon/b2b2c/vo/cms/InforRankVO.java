package com.slodon.b2b2c.vo.cms;

import com.slodon.b2b2c.cms.pojo.Information;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @program: slodon
 * @Description 封装资讯排行VO对象
 * @Author lxk
 */
@Data
public class InforRankVO {

    @ApiModelProperty("资讯id")
    private Integer informationId;

    @ApiModelProperty("资讯标题")
    private String title;

    public InforRankVO(Information information) {
        informationId = information.getInformationId();
        title = information.getTitle();
    }
}
