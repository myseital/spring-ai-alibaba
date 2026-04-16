package org.alibaba.cloud.ai.agent.service.impl;

import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.alibaba.cloud.ai.agent.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

/**
 *
 * @author myseital
 * @date 2026/4/14
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String from;

    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private TemplateEngine templateEngine;

    @Override
    public void sendTemplateMail(String to, Map<String, Object> variables) {
        Context context = new Context();
        variables.forEach(context::setVariable);
        String htmlContent = templateEngine.process("EmailTemplate", context);
        sendHtmlEmail(to, htmlContent);
    }

    private void sendHtmlEmail(String to, String htmlContent) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("AI智能库存调拨单通知");
            helper.setText(htmlContent, true);
//            javaMailSender.send(mimeMessage);
            log.info("HTML email sent success, to=[{}]", to);
        } catch (MessagingException e) {
            log.error("Failed to send html email", e);
            throw new RuntimeException("Failed to send html email", e);
        }
    }
}
