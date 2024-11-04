package org.scouts105bentaya.features.pre_scouter;

import jakarta.activation.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaErrorException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.shared.service.EmailService;
import org.scouts105bentaya.shared.service.PdfService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class PreScouterService {

    private final EmailService emailService;
    private final PdfService pdfService;
    private final PreScouterRepository preScouterRepository;
    private final PreScouterConverter preScouterConverter;
    @Value("${bentaya.email.main}")
    private String email;

    public PreScouterService(
        EmailService emailService,
        PdfService pdfService,
        PreScouterRepository preScouterRepository,
        PreScouterConverter preScouterConverter
    ) {
        this.emailService = emailService;
        this.pdfService = pdfService;
        this.preScouterRepository = preScouterRepository;
        this.preScouterConverter = preScouterConverter;
    }

    public List<PreScouter> findAll() {
        return this.preScouterRepository.findAll();
    }

    public void saveAndSendEmail(PreScouterDto preScouterDto) {
        PreScouter preScouter = this.save(preScouterDto);
        this.sendPreScouterEmail(preScouter);
    }

    private PreScouter save(PreScouterDto preScouterDto) {
        PreScouter preScouter = preScouterConverter.convertFromDto(preScouterDto);
        preScouter.setCreationDate(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now()));
        return this.preScouterRepository.save(preScouter);
    }

    private void sendPreScouterEmail(PreScouter preScouter) {
        DataSource dataSource = pdfService.generatePreScouterPDF(preScouter);
        emailService.sendEmailWithAttachment(
            email,
            "Preinscripción de voluntariado: %s, %s".formatted(preScouter.getSurname(), preScouter.getName()),
            "Preinscripción de la persona interesada - %s, %s".formatted(preScouter.getSurname(), preScouter.getName()),
            dataSource
        );
        emailService.sendEmailWithAttachment(
            preScouter.getEmail(),
            "Scouts 105 Bentaya - Copia de la preinscripción",
            """
                Se ha recibido con éxito la preinscripción de: %s, %s
                Nos pondremos en contacto con usted con la mayor brevedad posible.
                
                Atentamente,
                Grupo Scout 105 Bentaya
                """.formatted(preScouter.getSurname(), preScouter.getName()), dataSource);
    }

    public ResponseEntity<byte[]> getPDF(Integer id) {
        try {
            PreScouter preScouter = this.preScouterRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION)
                .body(pdfService.generatePreScouterPDF(preScouter).getInputStream().readAllBytes());
        } catch (IOException e) {
            log.error("getPDF - {}", e.getMessage());
            throw new WebBentayaErrorException("Error al generar el PDF");
        }
    }

    public void delete(Integer id) {
        this.preScouterRepository.deleteById(id);
    }
}
