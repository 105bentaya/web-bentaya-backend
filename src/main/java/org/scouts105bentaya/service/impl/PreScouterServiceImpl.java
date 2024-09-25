package org.scouts105bentaya.service.impl;

import jakarta.activation.DataSource;
import org.scouts105bentaya.converter.PreScouterConverter;
import org.scouts105bentaya.dto.PreScouterDto;
import org.scouts105bentaya.entity.PreScouter;
import org.scouts105bentaya.repository.PreScouterRepository;
import org.scouts105bentaya.service.EmailService;
import org.scouts105bentaya.service.PdfService;
import org.scouts105bentaya.service.PreScouterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class PreScouterServiceImpl implements PreScouterService {

    @Value("${bentaya.email.main}")
    private String email;
    private final EmailService emailService;
    private final PdfService pdfService;
    private final PreScouterRepository preScouterRepository;
    private final PreScouterConverter preScouterConverter;

    public PreScouterServiceImpl(EmailService emailService, PdfService pdfService,
                                 PreScouterRepository preScouterRepository, PreScouterConverter preScouterConverter) {
        this.emailService = emailService;
        this.pdfService = pdfService;
        this.preScouterRepository = preScouterRepository;
        this.preScouterConverter = preScouterConverter;
    }

    @Override
    public List<PreScouter> findAll() {
        return this.preScouterRepository.findAll();
    }

    @Override
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
                "Preinscripción de voluntariado: " + preScouter.getSurname() + ", " + preScouter.getName(),
                "Preinscripción de la persona interesada - " + preScouter.getSurname() + ", " + preScouter.getName(),
                dataSource);
        emailService.sendEmailWithAttachment(
                preScouter.getEmail(),
                "Scouts 105 Bentaya - Copia de la preinscripción",
                "Se ha recibido con éxito la preinscripción de: " +
                        preScouter.getSurname() + ", " + preScouter.getName() +
                        "\nNos pondremos en contacto con usted con la mayor brevedad posible." +
                        "\nAtentamente,\nGrupo Scout 105 Bentaya",
                dataSource);
    }

    @Override
    public ResponseEntity<byte[]> getPDF(Integer id) {
        Optional<PreScouter> optionalPreScouter = this.preScouterRepository.findById(id);
        if (optionalPreScouter.isPresent()) {
            try {
                byte[] pdf = pdfService.generatePreScouterPDF(optionalPreScouter.get()).getInputStream().readAllBytes();
                return ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION)
                        .body(pdf);
            } catch (Exception ignored) {
            }
        }
        return ResponseEntity
                .badRequest().build();
    }

    @Override
    public void delete(Integer id) {
        this.preScouterRepository.deleteById(id);
    }
}
