package com.slodon.b2b2c.captcha.config;

import com.slodon.b2b2c.captcha.constant.Constants;
import com.slodon.b2b2c.captcha.util.Config;
import com.slodon.b2b2c.captcha.work.Producer;
import com.slodon.b2b2c.captcha.work.impl.DefaultCaptcha;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @program: slodon
 * @Description 验证码配置
 * @Author wuxy
 * @date 2020.09.08 20:38
 */
@Configuration
public class CaptchaConfig {

    @Bean
    public Producer producer(Config config) {
        DefaultCaptcha producer = new DefaultCaptcha();
        producer.setConfig(config);
        return producer;
    }

    @Bean
    public Config config() {
        Properties properties = new Properties();
        properties.put(Constants.CAPTCHA_BORDER, "yes");
        properties.put(Constants.CAPTCHA_BORDER_COLOR, "45,45,45");
        properties.put(Constants.CAPTCHA_TEXTPRODUCER_FONT_COLOR, "white");
        properties.put(Constants.CAPTCHA_IMAGE_WIDTH, "96");
        properties.put(Constants.CAPTCHA_IMAGE_HEIGHT, "44");
        properties.put(Constants.CAPTCHA_TEXTPRODUCER_FONT_SIZE, "36");
        properties.put(Constants.CAPTCHA_SESSION_CONFIG_KEY, "code");
        properties.put(Constants.CAPTCHA_TEXTPRODUCER_FONT_NAMES, "宋体");
        properties.put(Constants.CAPTCHA_TEXTPRODUCER_CHAR_LENGTH, "4");
        properties.put(Constants.CAPTCHA_TEXTPRODUCER_CHAR_STRING, "abcde2345678gfnmnpwx");
        properties.put(Constants.CAPTCHA_BACKGROUND_CLR_FROM, "45,45,45");
        properties.put(Constants.CAPTCHA_BACKGROUND_CLR_TO, "45,45,45");
        properties.put(Constants.CAPTCHA_NOISE_IMPL, "com.slodon.b2b2c.captcha.work.impl.NoNoise");
        properties.put(Constants.CAPTCHA_OBSCURIFICATOR_IMPL, "com.slodon.b2b2c.captcha.work.impl.ShadowGimpy");
        return new Config(properties);
    }
}
