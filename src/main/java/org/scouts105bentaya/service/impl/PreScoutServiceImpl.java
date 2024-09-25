package org.scouts105bentaya.service.impl;

import jakarta.activation.DataSource;
import org.scouts105bentaya.converter.PreScoutConverter;
import org.scouts105bentaya.dto.PreScoutAssignationDto;
import org.scouts105bentaya.dto.PreScoutDto;
import org.scouts105bentaya.entity.PreScout;
import org.scouts105bentaya.entity.PreScoutAssignation;
import org.scouts105bentaya.enums.Group;
import org.scouts105bentaya.exception.PdfCreationException;
import org.scouts105bentaya.exception.PreScoutNotFoundException;
import org.scouts105bentaya.repository.PreScoutAssignationRepository;
import org.scouts105bentaya.repository.PreScoutRepository;
import org.scouts105bentaya.service.AuthService;
import org.scouts105bentaya.service.EmailService;
import org.scouts105bentaya.service.PdfService;
import org.scouts105bentaya.service.PreScoutService;
import org.scouts105bentaya.service.SettingService;
import org.scouts105bentaya.util.FormUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class PreScoutServiceImpl implements PreScoutService {

    @Value("${bentaya.email.main}")
    private String mainEmail;
    private final EmailService emailService;
    private final PdfService pdfService;
    private final PreScoutRepository preScoutRepository;
    private final PreScoutAssignationRepository preScoutAssignationRepository;
    private final PreScoutConverter preScoutConverter;
    private final SettingService settingService;
    private final AuthService authService;

    private final Environment environment;

    public PreScoutServiceImpl(EmailService emailService, PdfService pdfService, PreScoutRepository preScoutRepository,
                               PreScoutAssignationRepository preScoutAssignationRepository,
                               PreScoutConverter preScoutConverter, SettingService settingService,
                               AuthService authService, Environment environment) {
        this.emailService = emailService;
        this.pdfService = pdfService;
        this.preScoutRepository = preScoutRepository;
        this.preScoutAssignationRepository = preScoutAssignationRepository;
        this.preScoutConverter = preScoutConverter;
        this.settingService = settingService;
        this.authService = authService;
        this.environment = environment;
    }

    @Override
    public List<PreScout> findAll() {
        return this.preScoutRepository.findAll();
    }

    @Override
    public PreScout findById(int id) {
        return this.preScoutRepository.findById(id).orElseThrow(PreScoutNotFoundException::new);
    }

    @Override
    public List<PreScout> findAllAssignedByLoggedScouter() {
        return this.preScoutRepository.findAllByPreScoutAssignation_GroupId(Optional.ofNullable(this.authService.getLoggedUser().getGroupId()).orElseThrow(PreScoutNotFoundException::new));
    }

    @Override
    public void saveAndSendEmail(PreScoutDto preScoutDto) {
        PreScout preScout = this.saveFromForm(preScoutDto);
        this.sendPreScoutEmail(preScout);
    }

    private PreScout saveFromForm(PreScoutDto preScoutDto) {
        PreScout preScout = preScoutConverter.convertFromDto(preScoutDto);

        String secondYearOfTerm = settingService.findByName("currentFormYear").getValue();
        int firstYearOfTerm = Integer.parseInt(secondYearOfTerm) - 1;

        preScout.setSection(FormUtils.getGroup(preScout.getBirthday(), true, firstYearOfTerm));
        preScout.setCreationDate(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now()));

        int preScoutBirthYear = LocalDate.parse(preScout.getBirthday(), DateTimeFormatter.ofPattern("dd/MM/yyyy")).getYear();

        preScout.setAge(String.valueOf(firstYearOfTerm - preScoutBirthYear));

        preScout.setInscriptionYear(secondYearOfTerm);

        return this.preScoutRepository.save(preScout);
    }

    private void sendPreScoutEmail(PreScout preScout) {
        DataSource dataSource = pdfService.generatePreScoutPDF(preScout);
        emailService.sendEmailWithAttachment(
                preScout.getEmail(),
                "Scouts 105 Bentaya - Copia de la preinscripción",
                "Copia de la preinscripción de la persona educanda: " + preScout.getName() + " " + preScout.getSurname() +
                        "\n\nUna vez el período de preinscripción haya finalizado y la persona educanda haya sido admitida, " +
                        "nos pondremos en contacto con usted antes del 20 de septiembre.\nEn caso contrario, llegará un " +
                        "correo una vez haya empezado el curso informando de la situación de la lista de espera." +
                        "\n\nAtentamente,\nGrupo Scout 105 Bentaya",
                dataSource);
    }

    @Override
    public void saveAssignation(PreScoutAssignationDto dto) {
        PreScoutAssignation preScoutAssignation = new PreScoutAssignation();
        preScoutAssignation.setPreScout(this.findById(dto.getPreScoutId()));
        preScoutAssignation.setComment(dto.getComment());
        preScoutAssignation.setStatus(dto.getStatus());
        preScoutAssignation.setGroupId(Group.valueOf(dto.getGroupId()));
        preScoutAssignation.setAssignationDate(ZonedDateTime.now());

        PreScoutAssignation savedAssignation = this.preScoutAssignationRepository.save(preScoutAssignation);
        this.sendAssignationEmail(savedAssignation);
    }

    @Override
    public void updateAssignation(PreScoutAssignationDto dto) {
        PreScoutAssignation preScoutAssignation = preScoutAssignationRepository.findById(dto.getPreScoutId()).orElseThrow(PreScoutNotFoundException::new);
        if (dto.getStatus() == -1) {
            PreScout preScout = preScoutAssignation.getPreScout();
            preScout.setPreScoutAssignation(null);
            this.preScoutAssignationRepository.delete(preScoutAssignation);
        } else {
            Group originalGroup = preScoutAssignation.getGroupId();
            Integer originalStatus = preScoutAssignation.getStatus();
            preScoutAssignation.setComment(dto.getComment());
            preScoutAssignation.setStatus(dto.getStatus());
            preScoutAssignation.setGroupId(Group.valueOf(dto.getGroupId()));
            PreScoutAssignation savedAssignation = this.preScoutAssignationRepository.save(preScoutAssignation);
            if (originalStatus != 3 && savedAssignation.getStatus() == 3) this.sendRejectionEmail(savedAssignation);
            if (savedAssignation.getGroupId() != originalGroup) this.sendAssignationEmail(savedAssignation);
        }
    }
    
    private void sendAssignationEmail(PreScoutAssignation assignation) {
        String groupEmail = environment.getProperty(assignation.getGroupId().getEmailProperty());
        this.emailService.sendSimpleEmail(
                groupEmail,
                "Nueva Preinscripción Asignada",
                String.format("""
                        Se ha asignado la preinscripción de %s, %s a su unidad.
                        Entre a la web para ver más detalles.
                        """, assignation.getPreScout().getSurname(), assignation.getPreScout().getName()));
    }    
    
    private void sendRejectionEmail(PreScoutAssignation assignation) {
        this.emailService.sendSimpleEmail(mainEmail, String.format("Preinscripción Rechazada - %d", assignation.getPreScoutId()),
                String.format("""
                        Se ha rechazado la preinscripción de %s, %s (ID %d) - %s por el siguiente motivo:
                        %s
                        """, assignation.getPreScout().getSurname(), assignation.getPreScout().getName(),
                        assignation.getPreScoutId(), assignation.getGroupId().toString(), assignation.getComment()));
    }

    @Override
    public ResponseEntity<byte[]> getPDF(Integer id) {
        PreScout preScout = this.findById(id);
        try {
            byte[] pdf = pdfService.generatePreScoutPDF(preScout).getInputStream().readAllBytes();
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION)
                    .body(pdf);
        } catch (Exception ignored) {
            throw new PdfCreationException();
        }
    }

    @Override
    public void delete(Integer id) {
        this.preScoutRepository.deleteById(id);
    }
}
