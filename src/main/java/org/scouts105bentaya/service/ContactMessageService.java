package org.scouts105bentaya.service;

import org.scouts105bentaya.entity.ContactMessage;
import org.scouts105bentaya.repository.ContactMessageRepository;
import org.scouts105bentaya.util.TemplateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import java.util.List;

@Service
public class ContactMessageService {

    private static final String TEMPLATE = "contact-message.html";

    @Value("${bentaya.email.it}")
    private String itEmail;
    private final EmailService emailService;
    private final ContactMessageRepository contactMessageRepository;
    private final TemplateEngine templateEngine;

    public ContactMessageService(
        EmailService emailService,
        ContactMessageRepository contactMessageRepository,
        TemplateEngine templateEngine
    ) {
        this.emailService = emailService;
        this.contactMessageRepository = contactMessageRepository;
        this.templateEngine = templateEngine;
    }

    public List<ContactMessage> findAll() {
        return contactMessageRepository.findAll();
    }

    public ContactMessage save(ContactMessage contactMessage) {
        return contactMessageRepository.save(contactMessage);
    }

    public void sendContactMessageEmail(ContactMessage contactMessage) {
        this.save(contactMessage);
        emailService.sendSimpleEmailWithHtml(
            itEmail,
            "MENSAJE WEB: %s".formatted(contactMessage.getSubject()),
            this.templateEngine.process(TEMPLATE, TemplateUtils.getContext("form", contactMessage))
        );
    }
}
