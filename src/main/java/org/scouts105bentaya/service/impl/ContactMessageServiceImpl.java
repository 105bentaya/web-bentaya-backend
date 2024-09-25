package org.scouts105bentaya.service.impl;

import org.scouts105bentaya.entity.ContactMessage;
import org.scouts105bentaya.repository.ContactMessageRepository;
import org.scouts105bentaya.service.ContactMessageService;
import org.scouts105bentaya.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactMessageServiceImpl implements ContactMessageService {

    @Value("${bentaya.email.it}")
    private String itEmail;
    private final EmailService emailService;
    private final ContactMessageRepository contactMessageRepository;

    public ContactMessageServiceImpl(EmailService emailService, ContactMessageRepository contactMessageRepository){
        this.emailService = emailService;
        this.contactMessageRepository = contactMessageRepository;
    }

    @Override
    public List<ContactMessage> findAll(){
        return contactMessageRepository.findAll();
    }

    @Override
    public ContactMessage save(ContactMessage contactMessage){
        return contactMessageRepository.save(contactMessage);
    }

    @Override
    public void sendContactMessageEmail(ContactMessage contactMessage){
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
