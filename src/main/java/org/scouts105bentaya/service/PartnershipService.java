package org.scouts105bentaya.service;

import org.scouts105bentaya.dto.PartnershipDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PartnershipService {

    @Value("${bentaya.email.it}")
    private String itEmail;
    private final EmailService emailService;

    public PartnershipService(EmailService emailService) {
        this.emailService = emailService;
    }

    //todo replace with template
    public void sendPartnershipEmail(PartnershipDto partnershipDto) {
        emailService.sendSimpleEmailWithHtml(
            itEmail,
            "NUEVO MENSAJE PARA COLABORACIÓN RECIBIDO POR LA WEB",
            String.format("<div>" +
                          "    <p>Nos ha llegado un nuevo mensaje por medio del formulario de 'COLABORACIONES - Ser Solidaria', con motivo \"<b>%s</b>\" y el mensaje recibido es:" +
                          "    </p>" +
                          "    <hr>" +
                          "    <div  style=\"white-space: pre-wrap\">%s</div>" +
                          "    <hr>" +
                          "    <p>Los datos de la persona de contacto son:</p>" +
                          "    <p><b>Nombre:</b> %s</p>" +
                          "    <p><b>Correo:</b> %s</p>" +
                          "    <p><b>Teléfono:</b> %s</p>" +
                          "    <p><b>Nombre de la entidad:</b> %s</p>" +
                          "    <p>Atentamente,</p>" +
                          "    <p>El sistema de mensajería de Scouts 105 Bentaya</p>" +
                          "</div>",
                partnershipDto.getSubject(),
                partnershipDto.getMessage(),
                partnershipDto.getName(),
                partnershipDto.getEmail(),
                partnershipDto.getPhone(),
                partnershipDto.getEntityName() != null ? partnershipDto.getEntityName() : "No especificado"
            )
        );
        emailService.sendSimpleEmail(
            partnershipDto.getEmail(),
            "Copia del correo",
            """
                Gracias por ponerse en contacto con nosotros para realizar una colaboración. Su información ha sido \
                recibida y nos pondremos en contacto con usted con la mayor brevedad posible.

                Atentamente,
                El sistema de mensajería de Scouts 105 Bentaya"""
        );
    }
}
