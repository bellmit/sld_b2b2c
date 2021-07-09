package com.slodon.smartid.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author smartId
 */
@EnableAsync
@SpringBootApplication
@EnableScheduling
public class SmartIdServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartIdServerApplication.class, args);
    }
}
