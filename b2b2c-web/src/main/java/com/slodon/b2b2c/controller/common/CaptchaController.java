package com.slodon.b2b2c.controller.common;

import com.slodon.b2b2c.captcha.work.Producer;
import com.slodon.b2b2c.vo.CaptchaVO;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Api(tags = "图形验证码")
@RestController
@RequestMapping("/v3/captcha/common")
public class CaptchaController {

    @Resource
    private Producer producer;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("getCaptcha")
    @ApiOperation("获取验证码")
    public JsonResult<CaptchaVO> getCaptchaImage() {

        String capText = producer.createText();

        BufferedImage bi = producer.createImage(capText);
        //将随机数存在redis中
        String key = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(key, capText, 300, TimeUnit.SECONDS); //有效期五分钟
        //将图片转化成base64进行输出
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String captcha = "";
        try {
            ImageIO.write(bi, "png", baos);//写入流中
            captcha = new String(Base64.encodeBase64(baos.toByteArray()));
        } catch (IOException e) {
            log.error("验证码生成失败", e);
        }
        return SldResponse.success(new CaptchaVO(key, captcha));
    }

}
