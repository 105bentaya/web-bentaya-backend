package org.scouts105bentaya.shared.service;

import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender emailSender;
    @Value("${spring.mail.username}")
    private String email;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendSimpleEmail(String to, String subject, String body) {
        try {
            MimeMessageHelper helper = this.getSimpleEmail(to, subject, body, false);
            emailSender.send(helper.getMimeMessage());
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Exception whilst creating simple email: {}", e.getMessage());
        }
    }

    public void sendSimpleEmailWithHtml(String to, String subject, String htmlBody) {
        try {
            MimeMessageHelper helper = this.getSimpleEmail(to, subject, htmlBody, true);
            emailSender.send(helper.getMimeMessage());
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Exception whilst creating email with html: {}", e.getMessage());
        }
    }

    public void sendSimpleEmailWithHtmlAndAttachment(String to, String subject, String htmlBody, DataSource dataSource) {
        try {
            MimeMessageHelper helper = this.getSimpleEmail(to, subject, htmlBody, true);
            helper.addAttachment(dataSource.getName(), dataSource);
            emailSender.send(helper.getMimeMessage());
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Exception whilst creating email with html and attachment: {}", e.getMessage());
        }
    }

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
        helper.setFrom(email, "Web 105 Bentaya");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, isHtml);
        return helper;
    }
}