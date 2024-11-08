package com.yulcomtechnologies.drtssms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync
@EnableFeignClients("com.yulcomtechnologies.drtssms.feignClients")
@ComponentScan({
    "com.yulcomtechnologies.sharedlibrary",
    "com.yulcomtechnologies.drtssms"
})
public class DrtssMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DrtssMsApplication.class, args);
	}

}
