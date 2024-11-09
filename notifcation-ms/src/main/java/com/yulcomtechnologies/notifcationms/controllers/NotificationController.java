package com.yulcomtechnologies.notifcationms.controllers;

import com.yulcomtechnologies.notifcationms.services.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class NotificationController {
    private final EmailService emailService;

    @PostMapping("/notifications")
    public void sendNotification(
        @RequestParam String to,
        @RequestParam String subject,
        @RequestParam String content,
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String subtitle
    ) {
        emailService.sendEmail(to, subject, content, title, subtitle);
    }
}
