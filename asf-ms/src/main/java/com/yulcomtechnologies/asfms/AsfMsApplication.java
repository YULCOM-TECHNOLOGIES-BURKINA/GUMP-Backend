package com.yulcomtechnologies.asfms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
 @EnableAsync

public class AsfMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AsfMsApplication.class, args);
    }

}
