package org.scouts105bentaya.features.pre_scout;

import jakarta.activation.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.scouts105bentaya.core.exception.WebBentayaErrorException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.pre_scout.dto.PreScoutAssignationDto;
import org.scouts105bentaya.features.pre_scout.dto.PreScoutDto;
import org.scouts105bentaya.features.pre_scout.entity.PreScout;
import org.scouts105bentaya.features.pre_scout.entity.PreScoutAssignation;
import org.scouts105bentaya.features.pre_scout.repository.PreScoutAssignationRepository;
import org.scouts105bentaya.features.pre_scout.repository.PreScoutRepository;
import org.scouts105bentaya.features.setting.SettingService;
import org.scouts105bentaya.shared.Group;
import org.scouts105bentaya.shared.service.AuthService;
import org.scouts105bentaya.shared.service.EmailService;
import org.scouts105bentaya.shared.service.PdfService;
import org.scouts105bentaya.shared.util.FormUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PreScoutService {

    private final EmailService emailService;
    private final PdfService pdfService;
    private final PreScoutRepository preScoutRepository;
    private final PreScoutAssignationRepository preScoutAssignationRepository;
    private final PreScoutConverter preScoutConverter;
    private final SettingService settingService;
    private final AuthService authService;
    private final Environment environment;
    @Value("${bentaya.email.main}")
    private String mainEmail;

    public PreScoutService(
        EmailService emailService,
        PdfService pdfService,
        PreScoutRepository preScoutRepository,
        PreScoutAssignationRepository preScoutAssignationRepository,
        PreScoutConverter preScoutConverter,
        SettingService settingService,
        AuthService authService,
        Environment environment
    ) {
        this.emailService = emailService;
        this.pdfService = pdfService;
        this.preScoutRepository = preScoutRepository;
        this.preScoutAssignationRepository = preScoutAssignationRepository;
        this.preScoutConverter = preScoutConverter;
        this.settingService = settingService;
        this.authService = authService;
        this.environment = environment;
    }

    public List<PreScout> findAll() {
        return this.preScoutRepository.findAll();
    }

    public PreScout findById(int id) {
        return this.preScoutRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);
    }

    public List<PreScout> findAllAssignedByLoggedScouter() {
        Group group = Optional.ofNullable(this.authService.getLoggedUser().getGroupId()).orElseThrow(WebBentayaNotFoundException::new);
        return this.preScoutRepository.findAllByPreScoutAssignation_GroupId(group);
    }

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

    public void saveAssignation(PreScoutAssignationDto dto) {
        PreScoutAssignation preScoutAssignation = new PreScoutAssignation();
        preScoutAssignation.setPreScout(this.findById(dto.preScoutId()));
        preScoutAssignation.setComment(dto.comment());
        preScoutAssignation.setStatus(dto.status());
        preScoutAssignation.setGroupId(Group.valueOf(dto.groupId()));
        preScoutAssignation.setAssignationDate(ZonedDateTime.now());

        PreScoutAssignation savedAssignation = this.preScoutAssignationRepository.save(preScoutAssignation);
        this.sendAssignationEmail(savedAssignation);
    }

    public void updateAssignation(PreScoutAssignationDto dto) {
        PreScoutAssignation preScoutAssignation = preScoutAssignationRepository.findById(dto.preScoutId()).orElseThrow(WebBentayaNotFoundException::new);
        if (dto.status() == -1) {
            PreScout preScout = preScoutAssignation.getPreScout();
            preScout.setPreScoutAssignation(null);
            this.preScoutAssignationRepository.delete(preScoutAssignation);
        } else {
            Group originalGroup = preScoutAssignation.getGroupId();
            Integer originalStatus = preScoutAssignation.getStatus();
            preScoutAssignation.setComment(dto.comment());
            preScoutAssignation.setStatus(dto.status());
            preScoutAssignation.setGroupId(Group.valueOf(dto.groupId()));
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

    public ResponseEntity<byte[]> getPDF(Integer id) {
        try {
            PreScout preScout = this.findById(id);
            return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION)
                .body(pdfService.generatePreScoutPDF(preScout).getInputStream().readAllBytes());
        } catch (IOException e) {
            log.error("getPDF - {}", e.getMessage());
            throw new WebBentayaErrorException("Error al generar el PDF");
        }
    }

    public void delete(Integer id) {
        this.preScoutRepository.deleteById(id);
    }
}
