package com.slodon.b2b2c.goods.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lxk
 */
@Data
public class GoodsFreightTemplateDTO implements Serializable {

    private static final long serialVersionUID = 3420626839193463262L;

    @ApiModelProperty(value = "运费模板ID")
    private Integer freightTemplateId;

    @ApiModelProperty(value = "运费模板名称",required = true)
    private String templateName;

    @ApiModelProperty(value = "计费方式：1-按件，2-按重量，3-按体积",required = true)
    private String chargeType;

    @ApiModelProperty(value = "字符串的json运费模板详情对象 [{\n" +
            "     \"cityCode\": \"CN\",\n" +
            "     \"cityName\": \"全国\",\n" +
            "     \"baseNumber\": 1,\n" +
            "     \"basePrice\": 10,\n" +
            "     \"addNumber\": 1,\n" +
            "     \"addPrice\": 10\n" +
            "      }, {\n" +
            "     \"cityCode\": \"110100,120100,130100,130200,130300,130400\",\n" +
            "     \"cityName\": \"北京市,天津市,石家庄市,唐山市,秦皇岛市,邯郸市\",\n" +
            "     \"baseNumber\": 1,\n" +
            "     \"basePrice\": 10,\n" +
            "     \"addNumber\": 1,\n" +
            "     \"addPrice\": 10,\n" +
            "     \"provinceInfo\": \"[{\\\"provinceCode\\\":\\\"CN003000000\\\",\\\"cityCode\\\":\\\"CN003001000\\\"},{\\\"provinceCode\\\":\\\"CN028000000\\\",\\\"cityCode\\\":\\\"CN028001000\\\"},{\\\"provinceCode\\\":\\\"CN010000000\\\",\\\"cityCode\\\":\\\"CN010009000,CN010010000,CN010007000,CN010004000\\\"}]\"\n" +
            "      }] ",required = true)
    private String freightExtendList;
}
