package org.scouts105bentaya.service;

import org.scouts105bentaya.entity.ContactMessage;
import org.scouts105bentaya.repository.ContactMessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactMessageService {

    @Value("${bentaya.email.it}")
    private String itEmail;
    private final EmailService emailService;
    private final ContactMessageRepository contactMessageRepository;

    public ContactMessageService(
        EmailService emailService,
        ContactMessageRepository contactMessageRepository
    ) {
        this.emailService = emailService;
        this.contactMessageRepository = contactMessageRepository;
    }

    public List<ContactMessage> findAll() {
        return contactMessageRepository.findAll();
    }

    public ContactMessage save(ContactMessage contactMessage) {
        return contactMessageRepository.save(contactMessage);
    }

    //todo replace with template
    public void sendContactMessageEmail(ContactMessage contactMessage) {
        this.save(contactMessage);
        emailService.sendSimpleEmailWithHtml(
            itEmail,
            "MENSAJE WEB: " + contactMessage.getSubject(),
            String.format("<div>" +
                          "    <p>Nos ha llegado un nuevo mensaje por medio del formulario de contacto de la web, con motivo \"<b>%s</b>\" y el mensaje recibido es:" +
                          "    </p>" +
                          "    <hr>" +
                          "    <div  style=\"white-space: pre-wrap\">%s</div>" +
                          "    <hr>" +
                          "    <p>Los datos de la persona de contacto son:</p>" +
                          "    <p><b>Nombre:</b> %s</p>" +
                          "    <p><b>Correo:</b> %s</p>" +
                          "    <p mt-4>Atentamente,</p>" +
                          "    <p>El sistema de mensajería de Scouts 105 Bentaya</p>" +
                          "</div>", contactMessage.getSubject(), contactMessage.getMessage(), contactMessage.getName(), contactMessage.getEmail())
        );
    }
}
