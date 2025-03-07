package org.scouts105bentaya.shared.service;

import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.features.setting.SettingService;
import org.scouts105bentaya.features.setting.enums.SettingEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender emailSender;
    private final SettingService settingService;
    @Value("${spring.mail.username}")
    private String email;

    public EmailService(JavaMailSender emailSender, SettingService settingService) {
        this.emailSender = emailSender;
        this.settingService = settingService;
    }

    public String[] getSettingEmails(SettingEnum setting) {
        return settingService.findByName(setting).getValue().split(",");
    }

    public void sendSimpleEmail(String subject, String body, String... to) {
        try {
            MimeMessageHelper helper = this.getSimpleEmail(subject, body, false, to);
            emailSender.send(helper.getMimeMessage());
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Exception whilst creating simple email: {}", e.getMessage());
        }
    }

    public void sendSimpleEmailWithHtml(String subject, String htmlBody, String... to) {
        try {
            MimeMessageHelper helper = this.getSimpleEmail(subject, htmlBody, true, to);
            emailSender.send(helper.getMimeMessage());
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Exception whilst creating email with html: {}", e.getMessage());
        }
    }

    public void sendSimpleEmailWithHtmlAndAttachment(String subject, String htmlBody, DataSource dataSource, String... to) {
        try {
            MimeMessageHelper helper = this.getSimpleEmail(subject, htmlBody, true, to);
            helper.addAttachment(dataSource.getName(), dataSource);
            emailSender.send(helper.getMimeMessage());
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Exception whilst creating email with html and attachment: {}", e.getMessage());
        }
    }

    public void sendEmailWithAttachment(String subject, String body, DataSource dataSource, String... to) {
        try {
            MimeMessageHelper helper = this.getSimpleEmail(subject, body, false, to);
            helper.addAttachment(dataSource.getName(), dataSource);
            emailSender.send(helper.getMimeMessage());
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Exception whilst creating email with attachment: {}", e.getMessage());
        }
    }

    private MimeMessageHelper getSimpleEmail(String subject, String body, boolean isHtml, String... to) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(email, "Web 105 Bentaya");
        helper.setSubject(subject);
        helper.setText(body, isHtml);

        if (to.length > 1) helper.setBcc(to);
        else helper.setTo(to);

        return helper;
    }
}