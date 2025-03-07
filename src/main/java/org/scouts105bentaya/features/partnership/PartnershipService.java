package org.scouts105bentaya.features.partnership;

import org.scouts105bentaya.features.setting.enums.SettingEnum;
import org.scouts105bentaya.shared.service.EmailService;
import org.scouts105bentaya.shared.util.TemplateUtils;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

@Service
public class PartnershipService {

    private static final String TEMPLATE = "partnership.html";
    private final EmailService emailService;
    private final TemplateEngine templateEngine;

    public PartnershipService(
        EmailService emailService,
        TemplateEngine templateEngine
    ) {
        this.emailService = emailService;
        this.templateEngine = templateEngine;
    }

    public void sendPartnershipEmail(PartnershipDto partnershipDto) {
        emailService.sendSimpleEmailWithHtml(
            "NUEVO MENSAJE PARA COLABORACIÓN RECIBIDO POR LA WEB",
            templateEngine.process(TEMPLATE, TemplateUtils.getContext("form", partnershipDto)),
            emailService.getSettingEmails(SettingEnum.CONTACT_MAIL)
        );
        emailService.sendSimpleEmail(
            "Copia del correo",
            """
                Gracias por ponerse en contacto con nosotros para realizar una colaboración. Su información ha sido \
                recibida y nos pondremos en contacto con usted con la mayor brevedad posible.
                
                Atentamente,
                El sistema de mensajería de Scouts 105 Bentaya
                """,
            partnershipDto.email()
        );
    }
}
