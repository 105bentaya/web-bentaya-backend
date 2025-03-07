package org.scouts105bentaya.features.complaint;

import jakarta.activation.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.scouts105bentaya.features.setting.enums.SettingEnum;
import org.scouts105bentaya.shared.service.EmailService;
import org.scouts105bentaya.shared.service.PdfService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComplaintService {

    private final EmailService emailService;
    private final PdfService pdfService;
    private final ComplaintRepository complaintRepository;

    public ComplaintService(
        EmailService emailService,
        PdfService pdfService,
        ComplaintRepository complaintRepository
    ) {
        this.emailService = emailService;
        this.pdfService = pdfService;
        this.complaintRepository = complaintRepository;
    }

    public List<Complaint> findAll() {
        return complaintRepository.findAll();
    }

    public Complaint save(Complaint complaint) {
        return complaintRepository.save(complaint);
    }

    public void sendComplaintEmail(Complaint complaint) {
        this.save(complaint);
        DataSource dataSource = pdfService.generateComplaintPDF(complaint);
        emailService.sendEmailWithAttachment(
            "Denuncia de " + complaint.getCategory(),
            generateBody(complaint),
            dataSource,
            emailService.getSettingEmails(SettingEnum.COMPLAINT_MAIL)
        );
        if (!StringUtils.isBlank(complaint.getEmail())) {
            emailService.sendSimpleEmail(
                "Scouts 105 Bentaya - Recibí de su denuncia",
                """
                    Mediante este correo le notificamos que su denuncia ha sido recibida, nos pondremos en \
                    contacto con usted en caso de que sea de su interés o necesario. Muchas gracias por \
                    notificarnos su queja.
                    Un cordial saludo,
                    Scouts 105 Bentaya
                    """,
                complaint.getEmail()
            );
        }
    }

    private String generateBody(Complaint complaint) {
        return """
            Nos ha llegado una denuncia de la siguiente categoría y tipo: %s, %s
            """.formatted(complaint.getCategory(), complaint.getType());
    }
}
