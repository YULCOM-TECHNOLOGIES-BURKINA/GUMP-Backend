package com.yulcomtechnologies.notifcationms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class NotifcationMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotifcationMsApplication.class, args);
	}

}
