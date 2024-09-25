package org.scouts105bentaya.service.impl;

import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.scouts105bentaya.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String email;
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final String name = "Web 105 Bentaya";
    private final JavaMailSender emailSender;

    public EmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void sendSimpleEmail(String to, String subject, String body) {
        try {
            MimeMessageHelper helper = this.getSimpleEmail(to, subject, body, false);
            emailSender.send(helper.getMimeMessage());
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Exception whilst creating simple email: {}", e.getMessage());
        }
    }

    @Override
    public void sendSimpleEmailWithHtml(String to, String subject, String htmlBody) {
        try {
            MimeMessageHelper helper = this.getSimpleEmail(to, subject, htmlBody, true);
            emailSender.send(helper.getMimeMessage());
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Exception whilst creating email with attachment: {}", e.getMessage());
        }
    }

    @Override
    public void sendSimpleEmailWithHtmlAndAttachment(String to, String subject, String htmlBody, DataSource dataSource) {
        try {
            MimeMessageHelper helper = this.getSimpleEmail(to, subject, htmlBody, true);
            helper.addAttachment(dataSource.getName(), dataSource);
            emailSender.send(helper.getMimeMessage());
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Exception whilst creating email with attachment: {}", e.getMessage());
        }
    }

    @Override
    public void sendEmailWithAttachment(String to, String subject, String body, DataSource dataSource) {
        try {
            MimeMessageHelper helper = this.getSimpleEmail(to, subject, body, false);
            helper.addAttachment(dataSource.getName(), dataSource);
            emailSender.send(helper.getMimeMessage());
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Exception whilst creating email with attachment: {}", e.getMessage());
        }
    }

    private MimeMessageHelper getSimpleEmail(String to, String subject, String body, boolean isHtml) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(email, name);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, isHtml);
        return helper;
    }
}