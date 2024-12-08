package com.yulcomtechnologies.justicems;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients("com.yulcomtechnologies.feignClients")
@ComponentScan({
    "com.yulcomtechnologies.sharedlibrary",
    "com.yulcomtechnologies.justicems"
})
public class JusticeMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(JusticeMsApplication.class, args);
	}

}
