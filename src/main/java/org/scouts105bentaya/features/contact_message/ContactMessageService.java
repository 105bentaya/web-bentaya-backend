package org.scouts105bentaya.features.contact_message;

import org.scouts105bentaya.features.setting.enums.SettingEnum;
import org.scouts105bentaya.shared.service.EmailService;
import org.scouts105bentaya.shared.util.TemplateUtils;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import java.util.List;

@Service
public class ContactMessageService {

    private static final String TEMPLATE = "contact-message.html";
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
            "MENSAJE WEB: %s".formatted(contactMessage.getSubject()),
            this.templateEngine.process(TEMPLATE, TemplateUtils.getContext("form", contactMessage)),
            emailService.getSettingEmails(SettingEnum.CONTACT_MAIL)
        );
    }
}
