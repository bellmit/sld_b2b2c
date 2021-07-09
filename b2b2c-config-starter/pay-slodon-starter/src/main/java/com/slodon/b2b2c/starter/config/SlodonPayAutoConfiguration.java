package com.slodon.b2b2c.starter.config;

import com.slodon.b2b2c.starter.pay.SlodonPay;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lxk
 */
@Configuration
public class SlodonPayAutoConfiguration {

    @Bean
    public SlodonPay slodonPay(){
        return new SlodonPay();
    }
}
