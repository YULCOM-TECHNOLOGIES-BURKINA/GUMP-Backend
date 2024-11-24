package com.yulcomtechnologies.gateway.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class CustomInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Add custom logic before the request is processed
        String token = request.getHeader("Authorization");
        System.out.println("Token: " + token);
        // Process token, extract claims, etc.
        response.addHeader("DUDE", "YO");
        return true;
    }
}

