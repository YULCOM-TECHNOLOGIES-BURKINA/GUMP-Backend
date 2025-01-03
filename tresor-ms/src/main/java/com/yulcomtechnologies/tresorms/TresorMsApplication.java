package com.yulcomtechnologies.tresorms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync
@EnableFeignClients("com.yulcomtechnologies.tresorms.feignClients")
@ComponentScan({
    "com.yulcomtechnologies.tresorms",
    "com.yulcomtechnologies.sharedlibrary",
})
public class TresorMsApplication {
	public static void main(String[] args) {
		SpringApplication.run(TresorMsApplication.class, args);
	}
}
