package org.scouts105bentaya.features.scout.service;

import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.Interval;
import org.scouts105bentaya.core.exception.WebBentayaBadRequestException;
import org.scouts105bentaya.core.exception.WebBentayaConflictException;
import org.scouts105bentaya.core.exception.WebBentayaForbiddenException;
import org.scouts105bentaya.core.exception.WebBentayaNotFoundException;
import org.scouts105bentaya.features.booking.util.IntervalUtils;
import org.scouts105bentaya.features.group.GroupRepository;
import org.scouts105bentaya.features.scout.dto.form.ScoutInfoFormDto;
import org.scouts105bentaya.features.scout.dto.form.ScoutRecordFormDto;
import org.scouts105bentaya.features.scout.dto.form.ScoutRegistrationDateFormDto;
import org.scouts105bentaya.features.scout.entity.Scout;
import org.scouts105bentaya.features.scout.entity.ScoutRecord;
import org.scouts105bentaya.features.scout.entity.ScoutRegistrationDates;
import org.scouts105bentaya.features.scout.enums.ScoutStatus;
import org.scouts105bentaya.features.scout.enums.ScoutType;
import org.scouts105bentaya.features.scout.repository.ScoutRecordRepository;
import org.scouts105bentaya.features.scout.repository.ScoutRepository;
import org.scouts105bentaya.features.setting.SettingService;
import org.scouts105bentaya.features.setting.enums.SettingEnum;
import org.scouts105bentaya.features.user.role.RoleEnum;
import org.scouts105bentaya.shared.service.AuthService;
import org.scouts105bentaya.shared.service.BlobService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ScoutGroupDataService {

    private final ScoutRepository scoutRepository;
    private final BlobService blobService;
    private final GroupRepository groupRepository;
    private final ScoutService scoutService;
    private final ScoutRecordRepository scoutRecordRepository;
    private final SettingService settingService;
    private final AuthService authService;

    public ScoutGroupDataService(
        ScoutRepository scoutRepository,
        BlobService blobService,
        GroupRepository groupRepository,
        ScoutService scoutService,
        ScoutRecordRepository scoutRecordRepository,
        SettingService settingService,
        AuthService authService) {
        this.scoutRepository = scoutRepository;
        this.blobService = blobService;
        this.groupRepository = groupRepository;
        this.scoutService = scoutService;
        this.scoutRecordRepository = scoutRecordRepository;
        this.settingService = settingService;
        this.authService = authService;
    }

    public int findLastScoutCensus() {
        return Integer.parseInt(settingService.findByName(SettingEnum.LAST_CENSUS_SCOUT).getValue());
    }

    public void updateScoutCensus(Scout scout, @Nullable Integer census) {
        if (authService.getLoggedUser().hasRole(RoleEnum.ROLE_ADMIN)) {
            scout.setCensus(census);
            if (scout.getCensus() != null && scout.getCensus() > findLastScoutCensus()) {
                this.settingService.updateValue(scout.getCensus(), SettingEnum.LAST_CENSUS_SCOUT);
            }
        } else if (census != null) {
            throw new WebBentayaForbiddenException("No tiene permiso para cambiar el censo de una scout");
        }
    }

    public Scout updateScoutInfo(Integer id, ScoutInfoFormDto form) {
        this.validateScoutGroup(form);
        this.validateCensus(form, id);
        this.validateRegistrationDates(form);

        Scout scout = scoutRepository.findById(id).orElseThrow(WebBentayaNotFoundException::new);

        this.updateScoutCensus(scout, form.census());

        scout.setScoutType(form.scoutType());
        if (scout.getScoutType() == ScoutType.INACTIVE) {
            scout.setStatus(ScoutStatus.INACTIVE);
            scout.setFederated(false);
        } else {
            scout.setStatus(ScoutStatus.ACTIVE);
            scout.setFederated(form.federated());
        }

        scout.setGroup(scout.getScoutType().isScoutOrScouter() && form.groupId() != 0 ?
            groupRepository.findById(form.groupId()).orElseThrow(WebBentayaNotFoundException::new) :
            null
        );

        this.updateScoutRegistrationDates(scout, form);

        //todo hacer actualizaciones de roles, usuarios y asistencias según cosas
        //        if (hasChangedGroup) {
        //            this.deleteFutureConfirmations(savedScout);
        //            this.createConfirmationForFutureEvents(scoutDB);
        //        }
        //    @Transactional
        //    public void disable(Integer id) {
        //        Scout scout = this.findActiveById(id);
        //        scout.getUserList().forEach(user -> userService.removeScoutFromUser(user, scout));
        //        this.deleteFutureConfirmations(scout);
        //        scout.setActive(false);
        //        scoutRepository.save(scout);
        //    }

        return scoutRepository.save(scout);
    }


    private void updateScoutRegistrationDates(Scout scout, ScoutInfoFormDto form) {
        List<ScoutRegistrationDates> registrationDates = scout.getRegistrationDates();
        List<ScoutRegistrationDates> newDates = new ArrayList<>();

        form.registrationDates().forEach(newDate -> {
            if (newDate.id() != null) {
                ScoutRegistrationDates existingDate = registrationDates.stream()
                    .filter(date -> date.getId().equals(newDate.id()))
                    .findFirst().orElseThrow(WebBentayaNotFoundException::new);
                existingDate.setRegistrationDate(newDate.registrationDate());
                existingDate.setUnregistrationDate(newDate.unregistrationDate());
            } else {
                newDates.add(new ScoutRegistrationDates()
                    .setRegistrationDate(newDate.registrationDate())
                    .setUnregistrationDate(newDate.unregistrationDate())
                    .setScout(scout)
                );
            }
        });

        registrationDates.removeIf(date -> form.registrationDates().stream()
            .noneMatch(newDate -> date.getId().equals(newDate.id()))
        );
        registrationDates.addAll(newDates);
    }

    private void validateCensus(ScoutInfoFormDto form, Integer scoutId) {
        if (form.census() != null) {
            Optional<Scout> existingCensus = scoutRepository.findFirstByCensus(form.census());
            if (existingCensus.isPresent() && !existingCensus.get().getId().equals(scoutId)) {
                throw new WebBentayaConflictException("Este censo ya está asignado");
            }
        }
    }

    private void validateScoutGroup(ScoutInfoFormDto form) {
        if (form.scoutType() == ScoutType.SCOUT && (form.groupId() == null || form.groupId() == 0)) {
            throw new WebBentayaBadRequestException("Es necesario especificar la unidad de la educanda");
        }
        if (form.scoutType() == ScoutType.SCOUTER && form.groupId() == null) {
            throw new WebBentayaBadRequestException("Es necesario especificar la unidad de la educadora");
        }
    }

    private void validateRegistrationDates(ScoutInfoFormDto form) {
        List<ScoutRegistrationDateFormDto> dates = form.registrationDates();

        if (dates.stream()
            .filter(date -> date.unregistrationDate() != null)
            .anyMatch(date -> !date.registrationDate().isBefore(date.unregistrationDate()))
        ) {
            throw new WebBentayaBadRequestException("Una fecha de baja debe ser posterior a la fecha de alta correspondiente");
        }

        if (dates.stream().filter(date -> date.unregistrationDate() == null).count() > 1) {
            throw new WebBentayaBadRequestException("Hay dos o más fechas de baja sin especificar");
        }

        List<Interval> intervals = dates.stream()
            .map(date -> IntervalUtils.intervalFromLocalDates(
                date.registrationDate(), Optional.ofNullable(date.unregistrationDate()).orElse(date.registrationDate())
            ))
            .toList();

        if (IntervalUtils.intervalsOverlapOrAbut(intervals)) {
            throw new WebBentayaBadRequestException("Hay fechas de alta y baja que se superponen");
        }
    }

    public ScoutRecord uploadScoutRecord(Integer scoutId, ScoutRecordFormDto recordForm) {
        Scout scout = scoutService.findById(scoutId);

        ScoutRecord scoutRecord = new ScoutRecord()
            .setRecordType(recordForm.recordType())
            .setStartDate(recordForm.startDate())
            .setEndDate(recordForm.endDate())
            .setObservations(recordForm.observations())
            .setScout(scout);

        scoutRecord = scoutRecordRepository.save(scoutRecord);

        scout.getRecordList().add(scoutRecord);
        scoutRepository.save(scout);

        return scoutRecord;
    }

    public ScoutRecord updateScoutRecord(Integer scoutId, Integer recordId, ScoutRecordFormDto recordForm) {
        Scout scout = scoutService.findById(scoutId);

        ScoutRecord scoutRecord = scout.getRecordList().stream()
            .filter(r -> r.getId().equals(recordId))
            .findFirst()
            .orElseThrow(WebBentayaNotFoundException::new);

        scoutRecord.setRecordType(recordForm.recordType())
            .setStartDate(recordForm.startDate())
            .setEndDate(recordForm.endDate())
            .setObservations(recordForm.observations());

        return scoutRecordRepository.save(scoutRecord);
    }

    public void deleteScoutRecord(Integer scoutId, Integer recordId) {
        Scout scout = scoutService.findById(scoutId);
        ScoutRecord scoutRecord = scout.getRecordList().stream()
            .filter(r -> r.getId().equals(recordId))
            .findFirst()
            .orElseThrow(WebBentayaNotFoundException::new);

        scoutRecord.getFiles().forEach(file -> blobService.deleteBlob(file.getUuid()));
        scoutRecordRepository.deleteById(recordId);
    }
}
