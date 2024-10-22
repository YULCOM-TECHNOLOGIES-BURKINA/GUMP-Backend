package com.yulcomtechnologies.tresorms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TresorMsApplication {
	public static void main(String[] args) {
		SpringApplication.run(TresorMsApplication.class, args);
	}
}
