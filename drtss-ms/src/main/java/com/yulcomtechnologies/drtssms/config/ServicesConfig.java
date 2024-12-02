package com.yulcomtechnologies.drtssms.config;

import com.yulcomtechnologies.sharedlibrary.auth.AuthenticatedUserService;
import com.yulcomtechnologies.sharedlibrary.auth.GatewayAuthenticatedUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ServicesConfig {
    @Bean
    public AuthenticatedUserService authenticatedUserService() {
        return new GatewayAuthenticatedUserService();
    }
}
