package org.scouts105bentaya.service.impl;

import jakarta.activation.DataSource;
import org.scouts105bentaya.entity.Complaint;
import org.scouts105bentaya.repository.ComplaintRepository;
import org.scouts105bentaya.service.ComplaintService;
import org.scouts105bentaya.service.EmailService;
import org.scouts105bentaya.service.PdfService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    @Value("${bentaya.email.it}")
    private String itEmail;

    @Value("${bentaya.email.main}")
    private String mainEmail;

    private final EmailService emailService;
    private final PdfService pdfService;
    private final ComplaintRepository complaintRepository;

    public ComplaintServiceImpl(EmailService emailService, PdfService pdfService, ComplaintRepository complaintRepository) {
        this.emailService = emailService;
        this.pdfService = pdfService;
        this.complaintRepository = complaintRepository;
    }

    @Override
    public List<Complaint> findAll() {
        return complaintRepository.findAll();
    }

    @Override
    public Complaint save(Complaint complaint) {
        return complaintRepository.save(complaint);
    }

    @Override
    public void sendComplaintEmail(Complaint complaint) {
        this.save(complaint);
        DataSource dataSource = pdfService.generateComplaintPDF(complaint);
        emailService.sendEmailWithAttachment(
                mainEmail,
                "Denuncia de " + complaint.getCategory(),
                "Nos ha llegado una denuncia de la siguiente categoría y tipo: " +
                        complaint.getCategory() + ", " +
                        complaint.getType(),
                dataSource);
        emailService.sendEmailWithAttachment(
                itEmail,
                "Denuncia de " + complaint.getCategory(),
                "Nos ha llegado una denuncia de la siguiente categoría y tipo: " +
                        complaint.getCategory() + ", " +
                        complaint.getType(),
                dataSource);
        if (!complaint.getEmail().equals("") && complaint.getEmail() != null) {
            emailService.sendSimpleEmail(
                    complaint.getEmail(),
                    "Scouts 105 Bentaya - Recibí de su denuncia",
                    "Mediante este correo le notificamos que su denuncia ha sido recibida, nos pondremos en " +
                            "contacto con usted en caso de que sea de su interés o necesario. Muchas gracias por " +
                            "notificarnos su queja.\n Un cordial saludo,\n Scouts 105 Bentaya"
            );
        }
    }
}
