package org.scouts105bentaya.features.pre_scout.service;

import jakarta.activation.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.scouts105bentaya.core.exception.WebBentayaErrorException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.pre_scout.PreScoutUtils;
import org.scouts105bentaya.features.pre_scout.dto.PreScoutAssignationDto;
import org.scouts105bentaya.features.pre_scout.dto.PreScoutFormDto;
import org.scouts105bentaya.features.pre_scout.entity.PreScout;
import org.scouts105bentaya.features.pre_scout.entity.PreScoutAssignation;
import org.scouts105bentaya.features.pre_scout.repository.PreScoutAssignationRepository;
import org.scouts105bentaya.features.pre_scout.repository.PreScoutRepository;
import org.scouts105bentaya.features.setting.SettingService;
import org.scouts105bentaya.shared.GenericConstants;
import org.scouts105bentaya.shared.Group;
import org.scouts105bentaya.shared.service.AuthService;
import org.scouts105bentaya.shared.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PreScoutService {
    private final EmailService emailService;
    private final PreScoutRepository preScoutRepository;
    private final PreScoutAssignationRepository preScoutAssignationRepository;
    private final SettingService settingService;
    private final AuthService authService;
    private final Environment environment;
    private final PreScoutPdfService preScoutPdfService;
    @Value("${bentaya.email.main}")
    private String mainEmail;

    public PreScoutService(
        EmailService emailService,
        PreScoutRepository preScoutRepository,
        PreScoutAssignationRepository preScoutAssignationRepository,
        SettingService settingService,
        AuthService authService,
        Environment environment,
        PreScoutPdfService preScoutPdfService
    ) {
        this.emailService = emailService;
        this.preScoutRepository = preScoutRepository;
        this.preScoutAssignationRepository = preScoutAssignationRepository;
        this.settingService = settingService;
        this.authService = authService;
        this.environment = environment;
        this.preScoutPdfService = preScoutPdfService;
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

    public void saveAndSendEmail(PreScoutFormDto preScoutDto) {
        PreScout preScout = this.saveFromForm(preScoutDto);
        this.sendPreScoutEmail(preScout);
    }

    private PreScout saveFromForm(PreScoutFormDto preScoutDto) {
        PreScout preScout = new PreScout()
            .setName(preScoutDto.name().toUpperCase().trim())
            .setSurname(joinSurnames(preScoutDto.firstSurname(), preScoutDto.secondSurname()))
            .setBirthday(preScoutDto.birthday())
            .setGender(preScoutDto.gender().toUpperCase())
            .setDni(preScoutDto.dni().toUpperCase())
            .setSize(preScoutDto.size().toUpperCase())
            .setMedicalData(preScoutDto.medicalData().trim())
            .setHasBeenInGroup(preScoutDto.hasBeenInGroup())
            .setYearAndSection(preScoutDto.yearAndSection())
            .setParentsName(preScoutDto.parentsName().toUpperCase())
            .setParentsSurname(joinSurnames(preScoutDto.parentsFirstSurname(), preScoutDto.parentsSecondSurname()))
            .setRelationship(preScoutDto.relationship().toUpperCase())
            .setPhone(preScoutDto.phone())
            .setEmail(preScoutDto.email().toLowerCase())
            .setPriority(preScoutDto.priority())
            .setPriorityInfo(preScoutDto.priorityInfo())
            .setComment(preScoutDto.comment());

        int secondYearOfTerm = Integer.parseInt(settingService.findByName("currentFormYear").getValue());
        int firstYearOfTerm = secondYearOfTerm - 1;
        int preScoutBirthYear = LocalDate.parse(preScout.getBirthday(), DateTimeFormatter.ofPattern("dd/MM/yyyy")).getYear();

        preScout.setAge(String.valueOf(firstYearOfTerm - preScoutBirthYear));
        preScout.setInscriptionYear(secondYearOfTerm);
        preScout.setSection(PreScoutUtils.getGroup(preScout.getBirthday(), firstYearOfTerm));
        preScout.setCreationDate(ZonedDateTime.now(GenericConstants.CANARY_ZONE_ID));
        preScout.setPriorityAsText(PreScoutUtils.getPriority(preScout.getPriority(), secondYearOfTerm));

        return this.preScoutRepository.save(preScout);
    }

    private String joinSurnames(String firstSurname, @Nullable String secondSurname) {
        firstSurname = formatSurname(firstSurname);
        if (!StringUtils.isBlank(secondSurname)) {
            return "%s %s".formatted(firstSurname, formatSurname(secondSurname));
        }
        return firstSurname;
    }

    private String formatSurname(String surname) {
        return surname.trim().replaceAll("\\s+", "-").toUpperCase();
    }

    private void sendPreScoutEmail(PreScout preScout) {
        DataSource dataSource = preScoutPdfService.generatePreScoutPDF(preScout);
        emailService.sendEmailWithAttachment(
            preScout.getEmail(),
            "Scouts 105 Bentaya - Copia de la preinscripción",
            """
                Copia de la preinscripción de la persona educanda: %s %s
                
                Una vez el período de preinscripción haya finalizado y la persona educanda haya sido admitida, nos \
                pondremos en contacto con usted antes del 20 de septiembre.
                En caso contrario, llegará un correo una vez haya empezado el curso informando de la situación de la \
                lista de espera.
                
                Atentamente,
                Grupo Scout 105 Bentaya""".formatted(preScout.getName(), preScout.getSurname()),
            dataSource
        );
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
        PreScout preScout = assignation.getPreScout();
        this.emailService.sendSimpleEmail(
            groupEmail,
            "Nueva Preinscripción Asignada - %s %s".formatted(preScout.getName(), preScout.getSurname()),
            generateAssignationBody(preScout)
        );
    }

    private String generateAssignationBody(PreScout preScout) {
        return """
            Se ha asignado la preinscripción de %s, %s a su unidad.
            Entre a la web para ver más detalles.
            """.formatted(preScout.getSurname(), preScout.getName());
    }

    private void sendRejectionEmail(PreScoutAssignation assignation) {
        this.emailService.sendSimpleEmail(
            mainEmail,
            "Preinscripción Rechazada - %d".formatted(assignation.getPreScoutId()),
            generateRejectionMessage(assignation)
        );
    }

    private String generateRejectionMessage(PreScoutAssignation assignation) {
        return """
            Se ha rechazado la preinscripción de %s, %s (ID %d) - %s por el siguiente motivo:
            %s
            """.formatted(assignation.getPreScout().getSurname(), assignation.getPreScout().getName(),
            assignation.getPreScoutId(), assignation.getGroupId().toString(), assignation.getComment());
    }

    public ResponseEntity<byte[]> getPDF(Integer id) {
        try {
            PreScout preScout = this.findById(id);
            return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION)
                .body(preScoutPdfService.generatePreScoutPDF(preScout).getInputStream().readAllBytes());
        } catch (IOException e) {
            log.error("getPDF - {}", e.getMessage());
            throw new WebBentayaErrorException("Error al generar el PDF");
        }
    }

    public void saveAsAssigned(Integer id) {
        PreScout preScout = this.findById(id);
        preScout.setAssigned(true);
        preScoutRepository.save(preScout);
    }

    public void delete(Integer id) {
        this.preScoutRepository.deleteById(id);
    }
}
