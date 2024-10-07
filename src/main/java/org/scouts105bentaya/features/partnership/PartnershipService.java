package org.scouts105bentaya.features.partnership;

import org.scouts105bentaya.shared.service.EmailService;
import org.scouts105bentaya.shared.util.TemplateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

@Service
public class PartnershipService {

    private static final String TEMPLATE = "partnership.html";
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    @Value("${bentaya.email.it}")
    private String itEmail;

    public PartnershipService(
        EmailService emailService,
        TemplateEngine templateEngine
    ) {
        this.emailService = emailService;
        this.templateEngine = templateEngine;
    }

    public void sendPartnershipEmail(PartnershipDto partnershipDto) {
        emailService.sendSimpleEmailWithHtml(
            itEmail,
            "NUEVO MENSAJE PARA COLABORACIÓN RECIBIDO POR LA WEB",
            templateEngine.process(TEMPLATE, TemplateUtils.getContext("form", partnershipDto))
        );
        emailService.sendSimpleEmail(
            partnershipDto.email(),
            "Copia del correo",
            """
                Gracias por ponerse en contacto con nosotros para realizar una colaboración. Su información ha sido \
                recibida y nos pondremos en contacto con usted con la mayor brevedad posible.
                
                Atentamente,
                El sistema de mensajería de Scouts 105 Bentaya
                """
        );
    }
}
