package org.scouts105bentaya.service;

import jakarta.activation.DataSource;
import org.scouts105bentaya.entity.Complaint;
import org.scouts105bentaya.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComplaintService {

    @Value("${bentaya.email.it}")
    private String itEmail;

    @Value("${bentaya.email.main}")
    private String mainEmail;

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
            mainEmail,
            "Denuncia de " + complaint.getCategory(),
            generateBody(complaint),
            dataSource
        );
        emailService.sendEmailWithAttachment(
            itEmail,
            "Denuncia de " + complaint.getCategory(),
            generateBody(complaint),
            dataSource
        );
        if (complaint.getEmail() != null && !complaint.getEmail().isBlank()) {
            emailService.sendSimpleEmail(
                complaint.getEmail(),
                "Scouts 105 Bentaya - Recibí de su denuncia",
                """
                    Mediante este correo le notificamos que su denuncia ha sido recibida, nos pondremos en \
                    contacto con usted en caso de que sea de su interés o necesario. Muchas gracias por \
                    notificarnos su queja.
                    Un cordial saludo,
                    Scouts 105 Bentaya
                    """
            );
        }
    }

    private String generateBody(Complaint complaint) {
        return """
            Nos ha llegado una denuncia de la siguiente categoría y tipo: %s, %s
            """.formatted(complaint.getCategory(), complaint.getType());
    }
}
