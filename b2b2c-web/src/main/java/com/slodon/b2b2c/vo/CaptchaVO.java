package com.slodon.b2b2c.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @program: slodon
 * @Description 验证码返回数据
 * @Author wuxy
 */
@Data
public class CaptchaVO {

    @ApiModelProperty("唯一标识")
    private String key;

    @ApiModelProperty("base64验证码")
    private String captcha;

    public CaptchaVO(String key, String captcha) {
        this.key = key;
        this.captcha = captcha;
    }
}
