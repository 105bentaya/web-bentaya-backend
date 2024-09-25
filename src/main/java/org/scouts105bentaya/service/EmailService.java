package org.scouts105bentaya.service;

import jakarta.activation.DataSource;

public interface EmailService {
    void sendSimpleEmail(String to, String subject, String body);
    void sendSimpleEmailWithHtml(String to, String subject, String htmlBody);
    void sendSimpleEmailWithHtmlAndAttachment(String to, String subject, String htmlBody, DataSource dataSource);

    void sendEmailWithAttachment(String to, String subject, String body, DataSource dataSource);
}