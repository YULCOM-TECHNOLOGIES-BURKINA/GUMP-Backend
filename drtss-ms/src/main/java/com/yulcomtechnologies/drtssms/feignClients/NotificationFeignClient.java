package com.yulcomtechnologies.drtssms.feignClients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "notification-ms")
public interface NotificationFeignClient {

    @PostMapping("/api/notifications")
    void sendNotification(
        @RequestParam String to,
        @RequestParam String subject,
        @RequestParam String content,
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String subtitle
    );
}
