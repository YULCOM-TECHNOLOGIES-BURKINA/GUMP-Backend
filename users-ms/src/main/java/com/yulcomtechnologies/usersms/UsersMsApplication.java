package com.yulcomtechnologies.usersms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableFeignClients("com.yulcomtechnologies.usersms.feignClients")
@ComponentScan({
    "com.yulcomtechnologies.sharedlibrary",
    "com.yulcomtechnologies.usersms"
})
public class UsersMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsersMsApplication.class, args);
	}

}
