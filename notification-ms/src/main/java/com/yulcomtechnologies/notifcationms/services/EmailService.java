package com.yulcomtechnologies.notifcationms.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@Slf4j
@AllArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public void sendEmail(String to, String subject, String content, String title, String subtitle) {
        Context context = new Context();
        context.setVariable("title", title);
        context.setVariable("subtitle", subtitle);
        context.setVariable("content", content);

        sendTemplateEmail(to, subject, "mail-template", context);
    }


    private void sendTemplateEmail(String to, String subject, String template, Context context) {
        try {
            String htmlContent = templateEngine.process(template, context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Template email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send template email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
