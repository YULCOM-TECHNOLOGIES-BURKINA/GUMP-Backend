package com.yulcomtechnologies.drtssms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync
@ComponentScan({
    "com.yulcomtechnologies.drtssms",
    "com.yulcomtechnologies.sharedlibrary",
})
public class DrtssMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DrtssMsApplication.class, args);
	}

}
